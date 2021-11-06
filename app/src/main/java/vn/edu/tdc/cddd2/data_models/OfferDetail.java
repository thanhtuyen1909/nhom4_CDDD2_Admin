package vn.edu.tdc.cddd2.data_models;

public class OfferDetail {
    private String key;
    private String offerID;
    private int percentSale;
    private String productID;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public int getPercentSale() {
        return percentSale;
    }

    public void setPercentSale(int percentSale) {
        this.percentSale = percentSale;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public OfferDetail() {
    }

    public OfferDetail(String offerID, int percentSale, String productID) {
        this.offerID = offerID;
        this.percentSale = percentSale;
        this.productID = productID;
    }
}