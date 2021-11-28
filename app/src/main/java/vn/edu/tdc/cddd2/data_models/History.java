package vn.edu.tdc.cddd2.data_models;

public class History {

    // Khai báo biến:
    private String key,accountID,date,action,detail;
    public String getKey() {
        return key;
    }

    public History() {
    }

    public History(String key, String accountID, String date, String action, String detail) {
        this.key = key;
        this.accountID = accountID;
        this.date = date;
        this.action = action;
        this.detail = detail;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }



}
