import org.junit.jupiter.api.Test;
import p2p.backend.Connection;
import p2p.backend.ConnectionDenied;
import p2p.backend.Contact;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {
    /**
     * Létrehoz egy teszt kapcsolatot agy teszt kontakttal.
     * @throws IOException
     */
    @Test
    void init() throws IOException {
        Contact test = new Contact("Teszt", InetAddress.getByName("192.0.2.2"));
        Connection c = new Connection(new Socket("google.com", 80));
        c.init(test);
    }

    /**
     * Létrehoz egy olyan kapcsolatot, aminek hibát kell dobni, mert nem tud csatlakozni.
     * @throws IOException
     * @throws ConnectionDenied
     */
    @Test
    void constructor() throws IOException, ConnectionDenied {
        Exception exception = assertThrows(IOException.class, () -> {new Connection(InetAddress.getByName("192.0.2.0"));});
    }

    /**
     * Létrehoz egy kapcsolatot, majd bezárja.
     * @throws IOException
     */
    @Test
    void close() throws IOException {
        Connection test = new Connection(new Socket("google.com", 80));
        test.close();
    }
}