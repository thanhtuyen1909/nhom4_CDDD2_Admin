package vn.edu.tdc.cddd2.data_models;

public class CartDetail {
    private String cartID;
    private String productID;
    private int amount;
    private int price;
    private String key;

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int totalPrice) {
        this.price = totalPrice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CartDetail() {
    }

    public CartDetail(String cartID, String productID, int amount, int price) {
        this.cartID = cartID;
        this.productID = productID;
        this.amount = amount;
        this.price = price;
    }
}