package vn.edu.tdc.ltdd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoCode implements Parcelable {
    // Khai báo biến
    private String key;
    private String name;
    private String startDate;
    private String endDate;
    private String image;

    protected PromoCode(Parcel in) {
        key = in.readString();
        name = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PromoCode> CREATOR = new Creator<PromoCode>() {
        @Override
        public PromoCode createFromParcel(Parcel in) {
            return new PromoCode(in);
        }

        @Override
        public PromoCode[] newArray(int size) {
            return new PromoCode[size];
        }
    };

    // Get - set
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Contructor
    public PromoCode(String key, String name, String startDate, String endDate, String image) {
        this.key = key;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
    }

    public PromoCode() {
    }
}
