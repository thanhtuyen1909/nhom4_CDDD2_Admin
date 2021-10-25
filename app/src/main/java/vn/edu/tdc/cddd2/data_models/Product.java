package vn.edu.tdc.cddd2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Locale;

public class Product implements Parcelable {
    private String key;
    private String category_id;
    private String created_at;
    private String description;
    private String id;
    private String image;
    private int import_price;
    private String manu_id;
    private String name;
    private int price;
    private int quantity;
    private int sold;
    private int status;

    protected Product(Parcel in) {
        key = in.readString();
        category_id = in.readString();
        created_at = in.readString();
        description = in.readString();
        id = in.readString();
        image = in.readString();
        import_price = in.readInt();
        manu_id = in.readString();
        name = in.readString();
        price = in.readInt();
        quantity = in.readInt();
        sold = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImport_price() {
        return import_price;
    }

    public void setImport_price(int import_price) {
        this.import_price = import_price;
    }

    public String getManu_id() {
        return manu_id;
    }

    public void setManu_id(String manu_id) {
        this.manu_id = manu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public Product() {
    }

    public Product(String key, String category_id, String created_at, String description, String id, String image, int import_price, String manu_id, String name, int price, int quantity, int sold, int status) {
        this.key = key;
        this.category_id = category_id;
        this.created_at = created_at;
        this.description = description;
        this.id = id;
        this.image = image;
        this.import_price = import_price;
        this.manu_id = manu_id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.sold = sold;
        this.status = status;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(category_id);
        dest.writeString(created_at);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeString(image);
        dest.writeInt(import_price);
        dest.writeString(manu_id);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(quantity);
        dest.writeInt(sold);
    }
}
