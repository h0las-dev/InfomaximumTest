package parser;

public class Address {
    private String city;
    private String street;
    private int house;
    private int floor;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder("item::");
        stringBuilder.append(" city=");
        stringBuilder.append(this.city);

        stringBuilder.append(" street=");
        stringBuilder.append(this.street);

        stringBuilder.append(" house=");
        stringBuilder.append(this.house);

        stringBuilder.append(" floor=");
        stringBuilder.append(this.floor);

        return stringBuilder.toString();
    }
}
