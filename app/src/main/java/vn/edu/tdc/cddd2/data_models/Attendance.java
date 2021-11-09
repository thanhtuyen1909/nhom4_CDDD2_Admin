package vn.edu.tdc.cddd2.data_models;

public class Attendance {
    private String employeeID;
    private String date;
    private String note;
    private int status;

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Attendance(String employeeID, String date, String note, int status) {
        this.employeeID = employeeID;
        this.date = date;
        this.note = note;
        this.status = status;
    }
}
