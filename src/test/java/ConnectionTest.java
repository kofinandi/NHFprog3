import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {
    @Test
    void init() throws IOException {
        Contact test = new Contact("Teszt", InetAddress.getByName("192.0.2.2"));
        Connection c = new Connection(new Socket("google.com", 80));
        c.init(test);
    }

    @Test
    void constructor() throws IOException, ConnectionDenied {
        Exception exception = assertThrows(IOException.class, () -> {new Connection(InetAddress.getByName("192.0.2.0"));});
    }

    @Test
    void close() throws IOException {
        Connection test = new Connection(new Socket("google.com", 80));
        test.close();
    }
}