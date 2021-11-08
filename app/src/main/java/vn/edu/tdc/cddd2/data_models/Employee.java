package vn.edu.tdc.cddd2.data_models;

public class Employee {
    public Employee() {
    }

    public Employee(String maNV, String accountID, String address, String birthday, String create_at, String gender, String image, String name, String position, int salary, int allowance) {
        this.maNV = maNV;
        this.accountID = accountID;
        this.address = address;
        this.birthday = birthday;
        this.create_at = create_at;
        this.gender = gender;
        this.image = image;
        this.name = name;
        this.position = position;
        this.salary = salary;
        this.allowance = allowance;
    }

    // Khai báo biến:
    private String maNV,accountID,address,birthday,create_at,gender,image,name,position;
    private int salary,allowance;

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
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

    public int getAllowance() {
        return allowance;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }
}
