package vn.edu.tdc.cddd2.data_models;

public class Account {
    // Khai báo biến:
    private String accountID,passWord,status,username;
    private int role_id;
    public Account() {
    }

    public Account(String accountID, String passWord, int role_id, String status, String username) {
        this.accountID = accountID;
        this.passWord = passWord;
        this.role_id = role_id;
        this.status = status;
        this.username = username;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
