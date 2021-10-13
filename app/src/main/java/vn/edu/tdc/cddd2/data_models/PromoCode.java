package vn.edu.tdc.cddd2.data_models;

public class PromoCode {
    // Khai báo biến
    private String name;
    private String dateStart;
    private String dateEnd;

    // Get - set
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    // Contructor
    public PromoCode(String name, String dateStart, String dateEnd) {
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    // toString
    @Override
    public String toString() {
        return "PromoCode{" +
                "name='" + name + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                '}';
    }
}
