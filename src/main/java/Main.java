import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        Thread s = new Server();
        s.start();
        Connection c = new Connection("192.168.1.243");
        c.send("Hello");
        c.send("Hali");
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
