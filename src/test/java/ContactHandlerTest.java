import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import p2p.backend.Contact;
import p2p.backend.ContactHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ContactHandlerTest {
    LinkedList<Contact> test = new LinkedList<>();

    /**
     * Bolvassa a kontaktokat és hozzáad egy újat.
     * @throws IOException
     */
    @BeforeEach
    void prepare() throws IOException {
        ContactHandler.setup(test);
        ContactHandler.addContact(new Contact("Teszt", InetAddress.getByName("192.0.2.0")));
    }

    /**
     * Kitörli a kontaktokat (hogy ne zavarjanak máshol).
     * @throws IOException
     */
    @AfterEach
    void reset() throws IOException {
        test.removeAll(test);
        ContactHandler.quit();
    }

    /**
     * Teszteli egy új kontakt hozzáadását, és megnézi, hogy online-e (nem lehet, mert dokumentációs IP cím van megadva).
     */
    @Test
    void setup() {
        assertEquals(test.getFirst().getName(), "Teszt");
        assertFalse(test.getFirst().online());
        assertEquals(test.getFirst().getAddress(), "192.0.2.0");
    }

    /**
     * Megnézi, hogy létező kontakt el van-e mentve, nem létező pedig tényleg nincs.
     * @throws UnknownHostException
     */
    @Test
    void haveContact() throws UnknownHostException {
        Contact c = ContactHandler.haveContact("192.0.2.0");
        assertNotNull(c);
        assertEquals(c.getAddress(), "192.0.2.0");
        c = ContactHandler.haveContact("255.255.255.255");
        assertNull(c);
        ContactHandler.addContact(new Contact("Valami", InetAddress.getByName("192.0.2.10")));
        c = ContactHandler.haveContact("192.0.2.10");
        assertNotNull(c);
        assertEquals(c.getAddress(), "192.0.2.10");
        assertEquals(c.getName(), "Valami");
    }

    /**
     * Ellenőrzi, hogy kilépés utáni visszatöltésnél visszatöltődik-e a korábban elmentett kontakt.
     * @throws IOException
     */
    @Test
    void quit() throws IOException {
        ContactHandler.addContact(new Contact("Teszt2", InetAddress.getByName("192.0.2.1")));
        ContactHandler.quit();
        ContactHandler.setup(test);
        assertEquals(test.getFirst().getName(), "Teszt2");
        assertFalse(test.getFirst().online());
        assertEquals(test.getFirst().getAddress(), "192.0.2.1");

        assertEquals(test.get(1).getName(), "Teszt");
        assertFalse(test.get(1).online());
        assertEquals(test.get(1).getAddress(), "192.0.2.0");
    }

    /**
     * Ellenőrzi, hogy reload után megmarad-e a kontakt.
     */
    @Test
    void reload() {
        assertEquals(test.getFirst().getName(), "Teszt");
        assertFalse(test.getFirst().online());
        assertEquals(test.getFirst().getAddress(), "192.0.2.0");

        ContactHandler.reload();

        assertEquals(test.getFirst().getName(), "Teszt");
        assertFalse(test.getFirst().online());
        assertEquals(test.getFirst().getAddress(), "192.0.2.0");
    }
}