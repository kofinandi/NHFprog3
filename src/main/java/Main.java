import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        Thread s = new Server();
        s.start();

        Contact c = Contact.createContact("Teszt", "192.168.1.243");
        if (c != null){
            c.send("Haho");
        }
    }

    public static void addConnection(Connection c){
        connections.add(c);
        System.out.println("New connection");
    }

    public static void removeConnection(Connection c){
        connections.remove(c);
        System.out.println("Connection closed");
    }
}
