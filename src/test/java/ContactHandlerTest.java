import org.junit.AfterClass;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ContactHandlerTest {
    LinkedList<Contact> test = new LinkedList<>();

    @BeforeEach
    void prepare() throws IOException {
        ContactHandler.setup(test);
        ContactHandler.addContact(new Contact("Teszt", InetAddress.getByName("192.0.2.0")));
    }

    @AfterEach
    void reset() throws IOException {
        test.removeAll(test);
        ContactHandler.quit();
    }

    @Test
    void setup() {
        assertEquals(test.getFirst().getName(), "Teszt");
        assertFalse(test.getFirst().online());
        assertEquals(test.getFirst().getAddress(), "192.0.2.0");
    }

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