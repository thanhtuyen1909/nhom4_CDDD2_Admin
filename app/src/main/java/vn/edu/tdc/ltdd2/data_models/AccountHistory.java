package vn.edu.tdc.ltdd2.data_models;

public class AccountHistory {
    // Properties:
    private String key;
    private String accountID;
    private String action;
    private String detail;
    private String date;

    // Get - set:
    public String getKey() {
        return key;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Contructors:
    public AccountHistory() {
    }

    public AccountHistory(String key, String accountID, String action, String detail, String date) {
        this.key = key;
        this.accountID = accountID;
        this.action = action;
        this.detail = detail;
        this.date = date;
    }
}
