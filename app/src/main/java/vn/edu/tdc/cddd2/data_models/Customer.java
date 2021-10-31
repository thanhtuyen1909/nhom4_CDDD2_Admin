package vn.edu.tdc.cddd2.data_models;

public class Customer {
    private String key;
    private String accountID;
    private String created_at;
    private String dob;
    private String image;
    private String name;
    private String status;
    private String type_id;
    private String email;

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Customer() {
    }

    public Customer(String accountID, String created_at, String dob, String image, String name, String status, String type_id) {
        this.accountID = accountID;
        this.created_at = created_at;
        this.dob = dob;
        this.image = image;
        this.name = name;
        this.status = status;
        this.type_id = type_id;
    }

    @Override
    public String toString() {
        return name;
    }
}
