package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    // Khai báo biến
    private String orderID;
    private String accountID;
    private String address;
    private String created_at;
    private String note;
    private String shipperID;
    private int status;
    private int total;
    private String phone;
    private String name;
    private int remain;

    protected Order(Parcel in) {
        orderID = in.readString();
        accountID = in.readString();
        address = in.readString();
        created_at = in.readString();
        note = in.readString();
        shipperID = in.readString();
        status = in.readInt();
        total = in.readInt();
        remain = in.readInt();
        phone = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderID);
        dest.writeString(accountID);
        dest.writeString(address);
        dest.writeString(created_at);
        dest.writeString(note);
        dest.writeString(shipperID);
        dest.writeInt(status);
        dest.writeInt(total);
        dest.writeInt(remain);
        dest.writeString(phone);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    // Get - set
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    // Contructors
    public Order() {
    }

    public Order(String orderID, String accountID, String address, String created_at, String note, String shipperID, int status, int total, String phone, String name, int remain) {
        this.orderID = orderID;
        this.accountID = accountID;
        this.address = address;
        this.created_at = created_at;
        this.note = note;
        this.shipperID = shipperID;
        this.status = status;
        this.total = total;
        this.phone = phone;
        this.name = name;
        this.remain = remain;
    }
}
