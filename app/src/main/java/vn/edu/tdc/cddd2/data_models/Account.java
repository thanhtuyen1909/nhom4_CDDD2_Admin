package vn.edu.tdc.cddd2.data_models;

public class Account {
    // Khai báo biến:
    private String username;
    private String role;

    // Get - set:
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Contructor
    public Account(String username, String role) {
        this.username = username;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
