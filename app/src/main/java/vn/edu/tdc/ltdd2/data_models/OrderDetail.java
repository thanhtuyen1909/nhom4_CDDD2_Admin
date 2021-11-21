package vn.edu.tdc.ltdd2.data_models;

public class OrderDetail {
    // Khai báo biến
    private String orderID;
    private int amount;
    private String productID;
    private int price;

    // Get - set
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Contructors
    public OrderDetail() {
    }

    public OrderDetail(String orderID, int amount, String productID, int price) {
        this.orderID = orderID;
        this.amount = amount;
        this.productID = productID;
        this.price = price;
    }
}