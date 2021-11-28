package vn.edu.tdc.cddd2.data_models;

public class Customer {

    private String key,accountID,create_at,dob,email,image,name,status,typeID;
    private int totalPayment;

    public Customer() {
    }

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

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Customer(String key, String accountID, String create_at, String dob, String email, String image, String name, String status, String typeID, int totalPayment) {
        this.key = key;
        this.accountID = accountID;
        this.create_at = create_at;
        this.dob = dob;
        this.email = email;
        this.image = image;
        this.name = name;
        this.status = status;
        this.typeID = typeID;
        this.totalPayment = totalPayment;
    }


}
