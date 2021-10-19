package vn.edu.tdc.cddd2.data_models;

public class DetailPromoCode {
    // Khai báo biến
    private String offerID;
    private String key;
    private String productID;
    private int percentSale;

    // Get - set:
    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getPercentSale() {
        return percentSale;
    }

    public void setPercentSale(int percentSale) {
        this.percentSale = percentSale;
    }

    // Contructors:
    public DetailPromoCode() {
    }

    public DetailPromoCode(String offerID, String key, String productID, int percentSale) {
        this.offerID = offerID;
        this.key = key;
        this.productID = productID;
        this.percentSale = percentSale;
    }
}
