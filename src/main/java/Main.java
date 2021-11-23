import java.io.*;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        ArrayList<Contact> contacts = new ArrayList<>();

        ContactHandler.setup(contacts);

        Thread s = new Server(contacts);
        s.start();

        JFrame window = new WindowFrame(contacts);
        window.setVisible(true);

//        contacts.add(Contact.createContact("Teszt", "192.168.1.243"));
//        if (contacts.get(0) != null){
//            contacts.get(0).send("Haho");
//        }
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
