package vn.edu.tdc.cddd2.data_models;

public class History {
    // Khai báo biến:
    private String date;
    private String name;
    private int soluong;

    // Get - set:
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    // Contructor:
    public History(String date, String name, int soluong) {
        this.date = date;
        this.name = name;
        this.soluong = soluong;
    }

    // ToString:

    @Override
    public String toString() {
        return "History{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", soluong='" + soluong + '\'' +
                '}';
    }
}
