package vn.edu.tdc.cddd2.data_models;

public class Account {
    //Properties
    private String username;
    private String password;
    private int role_id;
    private String status;
    private String id;

    //Get - set
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    // Contructors
    public Account(String username, String password, int role_id, String status) {
        this.username = username;
        this.password = password;
        this.role_id = role_id;
        this.status = status;
    }

    public Account() {
    }

    //Tostring
    @Override
    public String toString() {
        return username;
    }
}
