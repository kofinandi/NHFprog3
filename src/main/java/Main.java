import java.io.*;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private static WindowFrame window;

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        ArrayList<Contact> contacts = new ArrayList<>();

        ContactHandler.setup(contacts);

        Thread s = new Server(contacts);
        s.start();

        window = new WindowFrame(contacts);
        window.setVisible(true);
    }

    public static void notifyMessage(Contact c){
        window.notifyMessage(c);
    }

    public static void addConnection(Connection c){
        connections.add(c);
        System.out.println("New connection");
    }

    public static void removeConnection(Connection c){
        if (connections.remove(c)){
            System.out.println("Connection closed");
        }
    }
}
