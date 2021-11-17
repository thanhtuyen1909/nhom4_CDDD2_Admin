package vn.edu.tdc.cddd2.data_models;

public class ItemChat {
    // Properties:
    String userID, messageNew, created_at, name, image;
    boolean isSeen;

    // Get - set
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessageNew() {
        return messageNew;
    }

    public void setMessageNew(String messageNew) {
        this.messageNew = messageNew;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public ItemChat() {
    }
}
