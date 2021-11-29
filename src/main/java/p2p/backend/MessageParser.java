package p2p.backend;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

/**
 * Ez az osztály felelős az üzenetek XML fájljainak beolvasásáért.
 */
public class MessageParser extends DefaultHandler {
    private LinkedList<Message> messages;
    private StringBuilder text;
    private Message read;
    private boolean message;

    /**
     * @param m Az üzenetek listája a memóriában. Ezt tölti fel a beolvasottakkal.
     */
    public MessageParser(LinkedList<Message> m){
        messages = m;
    }

    /**
     * @param uri Parser default paramétere.
     * @param localName Parser default paramétere.
     * @param qName Parser default paramétere, ezzel azonosítja az egyes üzeneteket.
     * @param attributes Parser default paramétere, itt találhatóak az üzenet tulajdonságai (dátum, idő, küldő, file).
     * @throws SAXException
     */
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

    /**
     * A bejövő szöveghez fűzi a beolvasott karaktereket.
     * @param ch Parser default paramétere, beolvasott karakterek.
     * @param start Parser default paramétere, kezdő karakter.
     * @param length Parser default paramétere, beolvasott karakterek száma.
     * @throws SAXException Parser default exception-je.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (message){
            text.append(new String(ch, start, length));
        }
    }

    /**
     * @param uri Parser default paramétere.
     * @param localName Parser default paramétere.
     * @param qName Parser default paramétere, ezzel ellenőrzi, hogy az üzenet beolvasásának a végére ért.
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("message")){
            read.text = text.toString();
            messages.add(read);
            message = false;
        }
    }
}
