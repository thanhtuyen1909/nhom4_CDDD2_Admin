package vn.edu.tdc.ltdd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    //Khai báo biến
    private String key;
    private String name;
    private String image;

    protected Category(Parcel in) {
        key = in.readString();
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    // Get - set:
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Contructors:
    public Category(String key, String name, String image) {
        this.key = key;
        this.name = name;
        this.image = image;
    }

    public Category() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(image);
    }

    @Override
    public String toString() {
        return name;
    }
}
