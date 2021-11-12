package vn.edu.tdc.cddd2.data_models;

public class CustomerType {

    // Properties:
    private int consume;
    private int discount;
    private String name;
    private String key;

    // Get - set:
    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Contructors
    public CustomerType() {
    }

    public CustomerType(int consume, int discount, String name, String key) {
        this.consume = consume;
        this.discount = discount;
        this.name = name;
        this.key = key;
    }
}
