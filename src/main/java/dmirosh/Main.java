package dmirosh;

import dmirosh.parser.TransactionParser;
import dmirosh.parser.Transaction;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static final String FILE_NAME = "/test.xml";

    public static void main(String[] args) throws IOException, XMLStreamException {
        Queue<Transaction> queue = new ConcurrentLinkedQueue<>();
        TransactionParser parser = new TransactionParser(Main.class.getResourceAsStream(FILE_NAME), queue);
        try {
            parser.parse();
        } catch (Exception e) {
            System.out.println("CAN'T PARSE: " + e.getMessage());
        }
        System.out.println("successfully parsed");
        System.out.println("queue length: " + queue.size());
    }

}
