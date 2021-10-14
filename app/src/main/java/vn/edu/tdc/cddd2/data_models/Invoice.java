package vn.edu.tdc.cddd2.data_models;

public class Invoice {
    // Khai báo biến
    private String maHD;
    private int tongTien;
    private String ngay;

    // Get - set:
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    // Contructor
    public Invoice(String maHD, int tongTien, String ngay) {
        this.maHD = maHD;
        this.tongTien = tongTien;
        this.ngay = ngay;
    }

    // ToString
    @Override
    public String toString() {
        return "Invoice{" +
                "maHD='" + maHD + '\'' +
                ", tongTien=" + tongTien +
                ", ngay='" + ngay + '\'' +
                '}';
    }
}
