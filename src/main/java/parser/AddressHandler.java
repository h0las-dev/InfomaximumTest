package parser;

import static parser.AddressAttributes.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

// Изначально хотел написать парсер на регулярках, на C#, например, такой вариант работает шустрее, чем аналогичный
// XML парсер, но в итоге посчитал, что привычный читабельний код SAX парсера без костылей будет оптимальнее.
public class AddressHandler extends DefaultHandler {
    // Для записей-дубликатов и уникальных записей можно было бы использовать одну структуру
    // для экономии памяти, но тогда пришлось бы проходить через весь хэш-мап для сбора статистики.
    // К тому же повторений в файле не так уж много в общем масштабе.
    private HashMap<String, AddressQuantityStatistics> duplicateEntries = null;
    private HashMap<String, AddressQuantityStatistics> uniqueEntries = null;
    private HashMap<String, CityStatistics> cities = null;
    private Address address = null;
    private StringBuilder data = null;

    public HashMap<String, AddressQuantityStatistics> getUniqueEntries() {
        return uniqueEntries;
    }
    public HashMap<String, AddressQuantityStatistics> getDuplicateEntries() {
        return duplicateEntries;
    }
    public HashMap<String, CityStatistics> getCities() { return cities; }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase(Item)) {
            address = new Address();

            // Привожу к нижнему регистру, чтобы банальные ошибки в написании города не влияли на работу приложения.
            var city = attributes.getValue(City).toLowerCase();
            address.setCity(city);

            // Привожу к нижнему регистру, чтобы учесть различное написание одинаковых улиц, например:
            // <item city="Ангарск" street="Садовый Квартал, улица" house="20" floor="5" />
            // <item city="Ангарск" street="Садовый квартал, улица" house="20" floor="5" />
            var street = attributes.getValue(Street).toLowerCase();
            address.setStreet(street);

            var house = Integer.parseInt(attributes.getValue(House));
            address.setHouse(house);

            var floor = Integer.parseInt(attributes.getValue(Floor));
            address.setFloor(floor);
        } else if (qName.equalsIgnoreCase(Root)) {
            duplicateEntries = new HashMap<>();
            uniqueEntries = new HashMap<>();
            cities = new HashMap<>();
        }

        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(Item)) {
            this.handleAddressQuantityStatistics();
            this.handleHousesQuantityStatistics();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }

    private void handleAddressQuantityStatistics() {
        var addressQuantityStatistics = new AddressQuantityStatistics(address);
        var addressKey = address.toString();

        if (uniqueEntries.containsKey(addressKey)) {
            if (duplicateEntries.containsKey(addressKey)) {
                var duplicateEntry = duplicateEntries.get(addressKey);
                duplicateEntry.incCount();
            }
            else {
                addressQuantityStatistics.incCount();
                duplicateEntries.put(addressKey, addressQuantityStatistics);
            }
        } else {
            uniqueEntries.put(addressKey, addressQuantityStatistics);
        }
    }

    private void handleHousesQuantityStatistics() {
        var cityKey = address.getCity();

        if (cities.containsKey(cityKey)) {
            var cityStatistics = cities.get(cityKey);
            cityStatistics.incHouses(address.getFloor());
        } else {
            var cityStatistics = new CityStatistics(address.getCity());
            cityStatistics.incHouses(address.getFloor());
            cities.put(cityKey, cityStatistics);
        }
    }
}
