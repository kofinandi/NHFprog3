import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class MessageParser extends DefaultHandler {
    private LinkedList<Message> messages;
    private boolean inelement = false;
    private StringBuilder text;
    private Message read;
    private boolean message;

    public MessageParser(LinkedList<Message> m){
        messages = m;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("message")){
            text = new StringBuilder();
            read = new Message(LocalDate.parse(attributes.getValue("date")),
                    LocalTime.parse(attributes.getValue("time")),
                    (attributes.getValue("received").equals("1")),
                    (attributes.getValue("file").equals("1")),
                    null);
            message = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (message){
            text.append(new String(ch, start, length));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("message")){
            read.text = text.toString();
            messages.add(read);
            message = false;
        }
    }
}
