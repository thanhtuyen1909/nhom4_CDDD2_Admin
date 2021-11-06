package vn.edu.tdc.cddd2.data_models;

public class Cart {
    private String cartID;
    private String accountID;
    private int total;

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Cart() {
    }

    public Cart(String accountID, int total) {
        this.accountID = accountID;
        this.total = total;
    }

}
