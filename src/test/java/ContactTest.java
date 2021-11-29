import org.junit.Assert;
import org.junit.jupiter.api.Test;
import p2p.backend.Connection;
import p2p.backend.Contact;
import p2p.backend.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {
    /**
     * Megpróbál nem létező IP címmel létrehozni egy kontaktot, de null-t kell kapnia, mivel így nem hozható létre.
     */
    @Test
    void createContact() {
        Contact c = Contact.createContact("Teszt", "192.0.2.0");
        Assert.assertNull(c);
    }

    /**
     * Betölt egy korábban elmentett kontaktot.
     */
    @Test
    void loadContact() {
        Contact c = Contact.loadContact("Teszt kontakt", "192.0.2.1");
        assertNotNull(c.getName());
        assertEquals(c.getName(), "Teszt kontakt");
        assertEquals(c.getAddress(), "192.0.2.1");
        assertEquals(c.unread(), 0);
    }

    /**
     * Létrehoz egy már létező kontakthoz egy objektumot és megnézi, hogy sikerült-e betölteni az üzeneteket.
     * @throws UnknownHostException
     */
    @Test
    void constructor() throws UnknownHostException {
        Contact c = new Contact("Teszt", InetAddress.getByName("192.0.2.2"));
        assertEquals(c.getName(), "Teszt");
        assertEquals(c.getAddress(), "192.0.2.2");
        LinkedList<Message> test = c.getMessages();
        assertEquals(test.get(0).text, "Hello");
        assertEquals(test.get(0).date, LocalDate.parse("2021-11-25"));
        assertTrue(test.get(0).received);
        assertEquals(test.get(1).text, "Hali");
        assertEquals(test.get(1).date, LocalDate.parse("2021-11-26"));
        assertFalse(test.get(1).received);
        assertEquals(test.get(2).text, "Mizu?");
        assertEquals(test.get(2).date, LocalDate.parse("2021-11-27"));
        assertTrue(test.get(2).received);
    }

    /**
     * Ellenőrzi az olvastlan üzenet számát.
     * @throws UnknownHostException
     */
    @Test
    void readMessages() throws UnknownHostException {
        Contact c = new Contact("Név", InetAddress.getByName("192.0.2.3"));
        assertEquals(c.unread(), 0);
        c.read();
        assertEquals(c.unread(), 0);
    }

    /**
     * Egy kontakthoz hozzáad egy teszt csatlakozást, majd lecsatlakoztatja.
     * @throws IOException
     */
    @Test
    void connect() throws IOException {
        Contact c = new Contact("Példa", InetAddress.getByName("192.0.2.4"));
        Connection test = new Connection(new Socket("google.com", 80));
        c.connect(test);
        assertEquals(c.getConnection(), test);
        assertTrue(c.online());
        c.disconnect();
        assertNull(c.getConnection());
        assertFalse(c.online());
    }

    /**
     * Teszteli az üzenetek fogadását. Új üzenetet fogad a kontakttól, majd leellenőrzi, hogy sikerült-e elmenteni.
     * @throws UnknownHostException
     */
    @Test
    void receive() throws UnknownHostException {
        LocalDate d = LocalDate.now();
        LocalTime t = LocalTime.now();

        Contact c = new Contact("Teszt", InetAddress.getByName("192.0.2.2"));
        c.receive(new Message(d, t, true, false, "Valami"));

        LinkedList<Message> messages = c.getMessages();

        Message test = messages.getLast();
        assertEquals(test.text, "Valami");
        assertEquals(test.date, d);
        assertTrue(test.received);

        c.receive(new Message(d, t, true, false, "Teszt"));

        test = messages.getLast();
        assertEquals(test.text, "Teszt");
        assertEquals(test.date, d);
        assertTrue(test.received);

        assertEquals(c.unread(), 2);
        c.read();
        assertEquals(c.unread(), 0);
    }
}