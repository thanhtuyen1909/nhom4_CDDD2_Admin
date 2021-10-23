package vn.edu.tdc.cddd2.data_models;

public class DiscountCode_Customer {
    private String code;
    private String customer_id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public DiscountCode_Customer(String code, String customer_id) {
        this.code = code;
        this.customer_id = customer_id;
    }

    public DiscountCode_Customer() {
    }


}
