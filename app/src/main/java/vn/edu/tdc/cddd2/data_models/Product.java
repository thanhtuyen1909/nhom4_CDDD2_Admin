package vn.edu.tdc.cddd2.data_models;

public class Product {
    //Khai báo biến
    private String key;
    private int id;
    private String name;
    private String category_id;
    private String image;
    private String manu_id;
    private int quantity;
    private String description;
    private int import_price;
    private int price;

    //Get - set
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getManu_id() {
        return manu_id;
    }

    public void setManu_id(String manu_id) {
        this.manu_id = manu_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImport_price() {
        return import_price;
    }

    public void setImport_price(int import_price) {
        this.import_price = import_price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    //Contructor
    public Product(String key, int id, String name, String category_id, String image, String manu_id, int quantity, String description, int import_price, int price) {
        this.key = key;
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.image = image;
        this.manu_id = manu_id;
        this.quantity = quantity;
        this.description = description;
        this.import_price = import_price;
        this.price = price;
    }

    public Product() {
    }

    // To string

    @Override
    public String toString() {
        return getName();
    }
}
