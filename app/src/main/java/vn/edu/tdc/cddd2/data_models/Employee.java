package vn.edu.tdc.cddd2.data_models;

public class Employee {
    // Khai báo biến:
    private String maNV;
    private String hoTenNV;
    private String chucVu;

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public Employee(String maNV, String hoTenNV, String chucVu) {
        this.maNV = maNV;
        this.hoTenNV = hoTenNV;
        this.chucVu = chucVu;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "maNV='" + maNV + '\'' +
                ", hoTenNV='" + hoTenNV + '\'' +
                ", chucVu='" + chucVu + '\'' +
                '}';
    }
}
