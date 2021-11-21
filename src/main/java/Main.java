import java.util.ArrayList;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args){
        Thread s = new Server();
        s.start();
    }

    public static void addConnection(Connection c){
        connections.add(c);
    }
}
