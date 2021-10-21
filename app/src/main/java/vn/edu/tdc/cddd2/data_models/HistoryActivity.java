package vn.edu.tdc.cddd2.data_models;

public class HistoryActivity {
    private int account_id;
    private String date;
    private String action;
    private String detail;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
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

    public HistoryActivity(int account_id, String date, String action, String detail) {
        this.account_id = account_id;
        this.date = date;
        this.action = action;
        this.detail = detail;
    }

    public HistoryActivity() {
    }

    @Override
    public String toString() {
        return action;
    }
}
