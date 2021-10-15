package vn.edu.tdc.cddd2.data_models;

public class Comment {
    // Khai báo biến:
    private String name;
    private String comment;
    private String date;

    // Get - set:
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    // Constructor
    public Comment(String name, String comment, String date) {
        this.name = name;
        this.comment = comment;
        this.date = date;
    }

    // toString

    @Override
    public String toString() {
        return "Comment{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
