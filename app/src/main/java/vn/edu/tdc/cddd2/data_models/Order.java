package vn.edu.tdc.cddd2.data_models;

public class Order {
    private String keyOrder, accountID,address,created_at,name,note,phone,shipperID;
    private int status, total;

    public Order() {
    }

    public Order(String keyOrder, String accountID, String address, String created_at, String name, String note, String phone, String shipperID, int status, int total) {
        this.keyOrder = keyOrder;
        this.accountID = accountID;
        this.address = address;
        this.created_at = created_at;
        this.name = name;
        this.note = note;
        this.phone = phone;
        this.shipperID = shipperID;
        this.status = status;
        this.total = total;
    }

    public String getKeyOrder() {
        return keyOrder;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getAddress() {
        return address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getPhone() {
        return phone;
    }

    public String getShipperID() {
        return shipperID;
    }

    public int getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }
}
