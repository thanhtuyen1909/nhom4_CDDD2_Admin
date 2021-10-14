package vn.edu.tdc.cddd2.data_models;

public class Order {
    // Khai báo biến
    private String maDH;
    private int tongTien;
    private String diaChi;
    private String nguoiGiao;

    // Get - set:
    public String getMaDH() {
        return maDH;
    }

    public void setMaDH(String maDH) {
        this.maDH = maDH;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getNguoiGiao() {
        return nguoiGiao;
    }

    public void setNguoiGiao(String nguoiGiao) {
        this.nguoiGiao = nguoiGiao;
    }

    // Contructor
    public Order(String maDH, int tongTien, String diaChi, String nguoiGiao) {
        this.maDH = maDH;
        this.tongTien = tongTien;
        this.diaChi = diaChi;
        this.nguoiGiao = nguoiGiao;
    }

    // toString
    @Override
    public String toString() {
        return "Oder{" +
                "maDH='" + maDH + '\'' +
                ", tongTien=" + tongTien +
                ", diaChi='" + diaChi + '\'' +
                ", nguoiGiao='" + nguoiGiao + '\'' +
                '}';
    }
}
