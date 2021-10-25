package vn.edu.tdc.cddd2.data_models;

public class OrderDetail {
    // Khai báo biến
    private String orderID;
    private int amount;
    private String productID;
    private int totalPrice;

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

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Contructors
    public OrderDetail() {
    }

    public OrderDetail(String orderID, int amount, String productID, int totalPrice) {
        this.orderID = orderID;
        this.amount = amount;
        this.productID = productID;
        this.totalPrice = totalPrice;
    }
}
