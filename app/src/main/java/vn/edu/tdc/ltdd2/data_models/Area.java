package vn.edu.tdc.ltdd2.data_models;

public class Area {
    private String key;
    private String name;

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

    public Area() {
    }

    public Area(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
