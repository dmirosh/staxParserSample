package dmirosh.parser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Queue;

public class TransactionParser {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    private State transactionHandlingState;
    private State receiverHandlingState;
    private State senderHandlingState;

    private InputStream source; //from read

    private Queue<Transaction> queue; //where to store parsed transactions

    private State currentState; //current parsing state

    private TransactionBuilder transactionBuilder; //stores current parsing transaction

    public TransactionParser(InputStream source, Queue<Transaction> queue) {
        this.source = source;
        this.queue = queue;

        this.transactionBuilder = new TransactionBuilder();

        transactionHandlingState = new TransactionHandlingState(this, transactionBuilder);
        receiverHandlingState = new ReceiverHandlingState(this, transactionBuilder);
        senderHandlingState = new SenderHandlingState(this, transactionBuilder);

        currentState = transactionHandlingState;
    }

    public void parse() throws Exception {
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(source);
        try {
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.peek();
                boolean isConsumed = false;
                // delegate event handling to current state
                isConsumed = currentState.handleEvent(event);
                if(isConsumed) {
                    xmlEventReader.nextEvent();
                }
            }
        } catch (ParseException e) {
            throw new Exception("wrong format");
        }
    }

    private void setCurrentState(State state) {
        currentState = state;
    }

    private void onTransactionParsed() {
        queue.add(transactionBuilder.build());
    }
/** classes that implements State pattern to handle parsing Transaction entity **/

//base class for all states that provides tome util functionality
    private static abstract class State {
        protected TransactionParser parser;
        protected TransactionBuilder builder;
        protected StringBuilder currentContent; //text content of current parsing tag

        public State(TransactionParser parser, TransactionBuilder builder) {
            this.parser = parser;
            this.builder = builder;
        }

        // returns 'true' if event was consumed and 'false' otherwise
        public boolean handleEvent(XMLEvent event) throws ParseException {
            if(event.isStartElement()) {
                currentContent = new StringBuilder();
                return handleElementStart(event.asStartElement());
            } else if(event.isEndElement()) {
                boolean res = handleElementEnd(event.asEndElement());
                currentContent = null; //tag is ended so remove tet content connected with it
                return res;
            } else if(event.isCharacters()) {
                return handleElementCharacters(event.asCharacters());
            }
            return true;
        }

        private boolean handleElementCharacters(Characters characters) {
            if(currentContent != null) {
                currentContent.append(characters.getData());
            }
            return true;
        }

        protected boolean handleElementStart(StartElement startElement) {
            return true;
        }
        protected boolean handleElementEnd(EndElement endElement) throws ParseException {
            return true;
        }
    }
    //handles top level children of <transaction> tag
    private static class TransactionHandlingState extends State {
        private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        protected TransactionHandlingState(TransactionParser parser, TransactionBuilder builder) {
            super(parser, builder);
        }

        @Override
        protected boolean handleElementStart(StartElement startElement) {
            String elemName = startElement.getName().getLocalPart();
            boolean isConsumed = true;
            switch (elemName) {
                case "transaction": {
                    builder.startBuild();
                    break;
                }
                case "sender": {
                    parser.setCurrentState(parser.senderHandlingState);
                    isConsumed = false;
                    break;
                }
                case "receiver": {
                    parser.setCurrentState(parser.receiverHandlingState);
                    isConsumed = false;
                    break;
                }
            }
            return isConsumed;
        }

        @Override
        protected boolean handleElementEnd(EndElement endElement) throws ParseException {
            String elemName = endElement.getName().getLocalPart();
            boolean isConsumed = true;
            switch (elemName) {
                case "version": {
                    if(currentContent != null) {
                        builder.setVersion(Integer.parseInt(currentContent.toString()));
                    }
                    break;
                }
                case "date": {
                    if(currentContent != null) {
                        builder.setDate(dateFormat.parse(currentContent.toString()));
                    }
                    break;
                }
                case "transaction": {
                    parser.onTransactionParsed();
                    break;
                }
            }
            return isConsumed;
        }
    }

    private static class ReceiverHandlingState extends State {
        String details = "";
        long id = -1;
        private ReceiverHandlingState(TransactionParser parser, TransactionBuilder builder) {
            super(parser, builder);
        }

        @Override
        protected boolean handleElementEnd(EndElement endElement) {
            String name = endElement.getName().getLocalPart();
            boolean isConsumed = true;
            switch (name) {
                case "id": {
                    if(currentContent != null) {
                        id = Long.parseLong(currentContent.toString());
                    }
                    break;
                }
                case "details": {
                    if(currentContent != null) {
                        details = currentContent.toString();
                    }
                    break;
                }
                case "receiver": {
                    builder.setReceiver(id, details);
                    parser.setCurrentState(parser.transactionHandlingState);
                    break;
                }
            }
            return isConsumed;
        }
    }

    private static class SenderHandlingState extends State {
        String type = "";
        long id = -1;
        protected SenderHandlingState(TransactionParser parser, TransactionBuilder builder) {
            super(parser, builder);
        }

        @Override
        protected boolean handleElementEnd(EndElement endElement) throws ParseException {
            String name = endElement.getName().getLocalPart();
            boolean isConsumed = true;
            switch (name) {
                case "id": {
                    if(currentContent != null) {
                        id = Long.parseLong(currentContent.toString());
                    }
                    break;
                }
                case "type": {
                    if(currentContent != null) {
                        type = currentContent.toString();
                    }
                    break;
                }
                case "sender": {
                    builder.setSender(id, type);
                    parser.setCurrentState(parser.transactionHandlingState);
                    break;
                }
            }
            return isConsumed;
        }
    }
}
