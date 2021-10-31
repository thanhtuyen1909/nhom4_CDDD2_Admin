package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Account implements Parcelable {
    //Properties
    private String key;
    private String username;
    private String password;
    private int role_id;
    private String status;

    protected Account(Parcel in) {
        key = in.readString();
        username = in.readString();
        password = in.readString();
        role_id = in.readInt();
        status = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Account( String username, String password, int role_id, String status) {

        this.username = username;
        this.password = password;
        this.role_id = role_id;
        this.status = status;
    }

    public Account() {
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(role_id);
        dest.writeString(status);
    }
    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        map.put("role_id",role_id);
        map.put("status",status);
        return map;
    }
}