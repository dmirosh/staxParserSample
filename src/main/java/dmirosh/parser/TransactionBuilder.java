package dmirosh.parser;

import java.util.Date;

public class TransactionBuilder {
    private Transaction transaction;

    public TransactionBuilder startBuild() {
        transaction = new Transaction();
        return this;
    }
    public Transaction build() {
        return transaction;
    }

    public TransactionBuilder setVersion(int version) {
        transaction.setVersion(version);
        return this;
    }

    public TransactionBuilder setId(long id) {
        transaction.setId(id);
        return this;
    }

    public TransactionBuilder setDate(Date date) {
        transaction.setDate(date);
        return this;
    }

    public TransactionBuilder setSender(long id, String type) {
        transaction.setSenderId(id);
        transaction.setSenderType(type);
        return this;
    }

    public TransactionBuilder setReceiver(long id, String detais) {
        transaction.setReceiverId(id);
        transaction.setReceiverDetails(detais);
        return this;
    }
}
