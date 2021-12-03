package vn.edu.tdc.cddd2.data_models;

public class Attendance {
    private String key, date, employeeID, note;
    private int status;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Attendance() {
    }

    public Attendance(String date, String employeeID, String note, int status) {
        this.date = date;
        this.employeeID = employeeID;
        this.note = note;
        this.status = status;
    }
}
