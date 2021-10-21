package vn.edu.tdc.cddd2.data_models;

public class Order {
    // Khai báo biến
    private String maDH;
    private int total;
    private String address;
    private String accountID;
    private String created_at;
    private String shipperID;
    private int status;
    private int transport_fee;

    // Get - set
    public String getMaDH() {
        return maDH;
    }

    public void setMaDH(String maDH) {
        this.maDH = maDH;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getTransport_fee() {
        return transport_fee;
    }

    public void setTransport_fee(int transport_fee) {
        this.transport_fee = transport_fee;
    }

    // Contructor
    public Order(int total, String address, String accountID, String created_at, String shipperID, int status, int transport_fee) {
        this.total = total;
        this.address = address;
        this.accountID = accountID;
        this.created_at = created_at;
        this.shipperID = shipperID;
        this.status = status;
        this.transport_fee = transport_fee;
    }

    public Order() {}
}
