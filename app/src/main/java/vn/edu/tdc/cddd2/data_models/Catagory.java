package vn.edu.tdc.cddd2.data_models;

public class Catagory {
    //Khai báo biến
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Contructor
    public Catagory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Catagory{" +
                "name='" + name + '\'' +
                '}';
    }
}
