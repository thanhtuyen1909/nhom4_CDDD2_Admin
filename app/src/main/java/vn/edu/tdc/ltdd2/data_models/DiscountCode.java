package vn.edu.tdc.ltdd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class DiscountCode implements Parcelable {
    // Khai báo biến
   private String code;
   private int value;
   private String type;
   private String event;


    protected DiscountCode(Parcel in) {
        code = in.readString();
        value = in.readInt();
        type = in.readString();
        event = in.readString();
    }

    public static final Creator<DiscountCode> CREATOR = new Creator<DiscountCode>() {
        @Override
        public DiscountCode createFromParcel(Parcel in) {
            return new DiscountCode(in);
        }

        @Override
        public DiscountCode[] newArray(int size) {
            return new DiscountCode[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public DiscountCode( String code, int value, String type, String event) {
        this.code = code;
        this.value = value;
        this.type = type;
        this.event = event;
    }

    public DiscountCode() {
    }

    @Override
    public String toString() {
        return code;
    }
    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("code",code);
        map.put("type",type);
        map.put("event",event);
        map.put("value",value);
        return map;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeInt(value);
        dest.writeString(type);
        dest.writeString(event);
    }
}
