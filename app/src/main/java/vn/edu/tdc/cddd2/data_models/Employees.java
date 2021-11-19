package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class Employees implements Parcelable {
    private String keyEmployees,accountID,address,birthday,created_at,gender,image,name,position;
    private int allowance,salary;

    public void setKeyEmployees(String keyEmployees) {
        this.keyEmployees = keyEmployees;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public static Creator<Employees> getCREATOR() {
        return CREATOR;
    }

    public Employees() {
    }

    public Employees(String keyEmployees, String accountID, String address, String birthday, String created_at, String gender, String image, String name, String position, int allowance, int salary) {
        this.keyEmployees = keyEmployees;
        this.accountID = accountID;
        this.address = address;
        this.birthday = birthday;
        this.created_at = created_at;
        this.gender = gender;
        this.image = image;
        this.name = name;
        this.position = position;
        this.allowance = allowance;
        this.salary = salary;
    }

    protected Employees(Parcel in) {
        keyEmployees = in.readString();
        accountID = in.readString();
        address = in.readString();
        birthday = in.readString();
        created_at = in.readString();
        gender = in.readString();
        image = in.readString();
        name = in.readString();
        position = in.readString();
        allowance = in.readInt();
        salary = in.readInt();
    }

    public static final Creator<Employees> CREATOR = new Creator<Employees>() {
        @Override
        public Employees createFromParcel(Parcel in) {
            return new Employees(in);
        }

        @Override
        public Employees[] newArray(int size) {
            return new Employees[size];
        }
    };

    public String getKeyEmployees() {
        return keyEmployees;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getAllowance() {
        return allowance;
    }

    public int getSalary() {
        return salary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyEmployees);
        dest.writeString(accountID);
        dest.writeString(address);
        dest.writeString(birthday);
        dest.writeString(created_at);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(position);
        dest.writeInt(allowance);
        dest.writeInt(salary);
    }
}
