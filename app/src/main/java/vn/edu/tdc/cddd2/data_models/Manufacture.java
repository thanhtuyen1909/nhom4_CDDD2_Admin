package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class Manufacture implements Parcelable {
    //Khai báo biến
    private String key;
    private String name;
    private String image;

    protected Manufacture(Parcel in) {
        key = in.readString();
        name = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Manufacture> CREATOR = new Creator<Manufacture>() {
        @Override
        public Manufacture createFromParcel(Parcel in) {
            return new Manufacture(in);
        }

        @Override
        public Manufacture[] newArray(int size) {
            return new Manufacture[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Manufacture(String key, String name, String image) {
        this.key = key;
        this.name = name;
        this.image = image;
    }

    public Manufacture() {
    }

    @Override
    public String toString() {
        return name;
    }
}
