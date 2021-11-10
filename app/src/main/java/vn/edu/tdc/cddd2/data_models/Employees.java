package vn.edu.tdc.cddd2.data_models;

public class Employees {
    private String keyEmployees,accountID,address,birthday,created_at,gender,image,name,position;
    private int allowance,salary;

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
}
