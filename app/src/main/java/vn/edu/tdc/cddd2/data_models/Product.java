package vn.edu.tdc.cddd2.data_models;

public class Product {
    //Khai báo biến
    private String name;
    private int price;
    private String manu;
    private int quantity;

    //Get - set

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getManu() {
        return manu;
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //Contructor

    public Product(String name, int price, String manu, int quantity) {
        this.name = name;
        this.price = price;
        this.manu = manu;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", manu='" + manu + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
