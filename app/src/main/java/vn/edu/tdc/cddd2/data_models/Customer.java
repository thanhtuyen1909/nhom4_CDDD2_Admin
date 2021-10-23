package vn.edu.tdc.cddd2.data_models;

public class Customer {
    private int id;
    private String name;
    private String image;
    private String phone;
    private String dob;
    private String type_id;
    private String created_at;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public Customer(int id, String name, String image, String phone, String dob, String type_id, String created_at, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.dob = dob;
        this.type_id = type_id;
        this.created_at = created_at;
        this.status = status;
    }

    public Customer() {
    }

    @Override
    public String toString() {
        return name;
    }
}
