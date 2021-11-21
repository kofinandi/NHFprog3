import java.io.*;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        File contactfile = new File("contacts.json");
        ArrayList<Contact> contacts = new ArrayList<>();

        if (!contactfile.exists()){
            contactfile.createNewFile();
        }
        else {
            BufferedReader fr = new BufferedReader(new FileReader(contactfile));
            StringBuilder jsonstring = new StringBuilder();
            while (true){
                String s = fr.readLine();
                if (s == null){
                    break;
                }
                jsonstring.append(s);
            }
            JSONObject js = new JSONObject(jsonstring.toString());
            JSONArray ja = js.getJSONArray("contacts");

            for (int i = 0; i < ja.length(); i++){
                contacts.add(Contact.loadContact(ja.getJSONObject(i).getString("name"), ja.getJSONObject(i).getString("address")));
                System.out.println(ja.getJSONObject(i).getString("name") + " " + ja.getJSONObject(i).getString("address"));
            }
        }

        Thread s = new Server(contacts);
        s.start();

//        contacts.add(Contact.createContact("Teszt", "192.168.1.243"));
//        if (contacts.get(0) != null){
//            contacts.get(0).send("Haho");
//        }

        JSONObject ojs = new JSONObject();
        JSONArray ocontacts = new JSONArray();

        for (Contact c : contacts){
            System.out.println(c.getName() + " " + c.getAddress());
            JSONObject contact = new JSONObject();
            contact.put("name", c.getName());
            contact.put("address", c.getAddress());
            ocontacts.put(contact);
        }
        ojs.put("contacts", ocontacts);
        PrintWriter pw = new PrintWriter(new FileWriter(contactfile));
        pw.println(ojs.toString());
        pw.flush();
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
