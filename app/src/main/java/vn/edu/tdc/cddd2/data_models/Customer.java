package vn.edu.tdc.cddd2.data_models;

public class Customer {
    // Khai báo biến:
    private String name;

    // Get - set:
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Contructor
    public Customer(String name) {
        this.name = name;
    }

    // toString
    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                '}';
    }
}
