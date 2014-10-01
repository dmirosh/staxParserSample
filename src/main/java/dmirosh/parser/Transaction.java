package dmirosh.parser;

import java.util.Date;

public class Transaction {
    private int version;
    private long id;
    private Date date;

    private long senderId;
    private String senderType;

    private long receiverId;
    private String receiverDetails;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverDetails() {
        return receiverDetails;
    }

    public void setReceiverDetails(String receiverDetails) {
        this.receiverDetails = receiverDetails;
    }
}
