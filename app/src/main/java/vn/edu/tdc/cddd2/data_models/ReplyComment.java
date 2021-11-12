package vn.edu.tdc.cddd2.data_models;

public class ReplyComment {
    // Properties:
    String accountID, created_at, replyComment;

    // Get - set:
    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public ReplyComment(String accountID, String created_at, String replyComment) {
        this.accountID = accountID;
        this.created_at = created_at;
        this.replyComment = replyComment;
    }
}
