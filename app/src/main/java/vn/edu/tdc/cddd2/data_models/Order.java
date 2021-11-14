package vn.edu.tdc.cddd2.data_models;

public class Order {


    // Khai báo biến
    private String accountID,address,create_at,name,note,orderID,phone,shipperID;
    private int status,total;


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShipperID() {
        return shipperID;
    }

    public void setShipperID(String shipperID) {
        this.shipperID = shipperID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    public Order() {
    }
    public Order(String accountID, String address, String create_at, String name, String note, String orderID, String phone, String shipperID, int status, int total) {
        this.accountID = accountID;
        this.address = address;
        this.create_at = create_at;
        this.name = name;
        this.note = note;
        this.orderID = orderID;
        this.phone = phone;
        this.shipperID = shipperID;
        this.status = status;
        this.total = total;
    }
}
