package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class Employee implements Parcelable {
    private String id;
    private String accountID;
    private String address;
    private int allowance;
    private String birthday;
    private String created_at;
    private String gender;
    private String image;
    private String name;
    private String position;
    private int salary;

    protected Employee(Parcel in) {
        id = in.readString();
        accountID = in.readString();
        address = in.readString();
        allowance = in.readInt();
        birthday = in.readString();
        created_at = in.readString();
        gender = in.readString();
        image = in.readString();
        name = in.readString();
        position = in.readString();
        salary = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accountID);
        dest.writeString(address);
        dest.writeInt(allowance);
        dest.writeString(birthday);
        dest.writeString(created_at);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(position);
        dest.writeInt(salary);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getAllowance() {
        return allowance;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Employee(String accountID, String address, int allowance, String birthday, String created_at, String gender, String image, String name, String position, int salary) {

        this.accountID = accountID;
        this.address = address;
        this.allowance = allowance;
        this.birthday = birthday;
        this.created_at = created_at;
        this.gender = gender;
        this.image = image;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return name;
    }
}
