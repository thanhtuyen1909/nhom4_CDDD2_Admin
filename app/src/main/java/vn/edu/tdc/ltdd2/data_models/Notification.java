package vn.edu.tdc.ltdd2.data_models;

public class Notification {
    private String key;
    private String accountID;
    private String image;
    private String title;
    private String content;
    private String created_at;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Notification() {
    }

    public Notification(String accountID, String image, String title, String content, String created_at) {
        this.accountID = accountID;
        this.image = image;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return title;
    }
}
