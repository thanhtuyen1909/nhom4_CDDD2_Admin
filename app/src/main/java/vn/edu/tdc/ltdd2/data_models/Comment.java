package vn.edu.tdc.ltdd2.data_models;

public class Comment {
    // Khai báo biến:
    private String key;
    private String orderID;
    private String comment;
    private String created_at;
    private String productID;
    private int rating;
    private ReplyComment reply;

    // Get - set:
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getProductID() {
        return productID;
    }

    public int getRating() {
        return rating;
    }

    public ReplyComment getReply() {
        return reply;
    }

    public void setReply(ReplyComment reply) {
        this.reply = reply;
    }

    // Constructors
    public Comment() {
    }

    public Comment(String key, String orderID, String comment, String created_at, String productID, int rating, ReplyComment reply) {
        this.key = key;
        this.orderID = orderID;
        this.comment = comment;
        this.created_at = created_at;
        this.productID = productID;
        this.rating = rating;
        this.reply = reply;
    }

    // Define class child:
    public static class ReplyComment {
        // Properties:
        String accountID, replyComment;

        // Get - set:
        public String getAccountID() {
            return accountID;
        }

        public void setAccountID(String accountID) {
            this.accountID = accountID;
        }

        public String getReplyComment() {
            return replyComment;
        }

        public void setReplyComment(String replyComment) {
            this.replyComment = replyComment;
        }

        // Constructors:
        public ReplyComment() {
        }

        public ReplyComment(String accountID, String replyComment) {
            this.accountID = accountID;
            this.replyComment = replyComment;
        }
    }
}
