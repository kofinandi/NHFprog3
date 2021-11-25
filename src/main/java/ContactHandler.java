import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;

public class ContactHandler {
    private static LinkedList<Contact> contacts;
    private static File contactfile;

    public static void setup(LinkedList<Contact> c) throws IOException {
        contacts = c;
        contactfile = new File("contacts.json");

        if (!contactfile.exists()){
            contactfile.createNewFile();
        }
        else {
            BufferedReader fr = new BufferedReader(new FileReader(contactfile));
            StringBuilder jsonstring = new StringBuilder();
            while (true){
                String st = fr.readLine();
                if (st == null){
                    break;
                }
                jsonstring.append(st);
            }
            JSONObject js = new JSONObject(jsonstring.toString());
            JSONArray ja = js.getJSONArray("contacts");

            for (int i = 0; i < ja.length(); i++){
                contacts.add(Contact.loadContact(ja.getJSONObject(i).getString("name"), ja.getJSONObject(i).getString("address")));
            }
        }
    }

    public static void addContact(Contact c){
        contacts.addFirst(c);
        Main.notifyContact();
    }

    public static Contact haveContact(String a){
        for (Contact i : contacts){
            if (i.getAddress().equals(a)){
                return i;
            }
        }
        return null;
    }

    public static void quit() throws IOException {
        JSONObject ojs = new JSONObject();
        JSONArray ocontacts = new JSONArray();

        for (Contact c : contacts){
            JSONObject contact = new JSONObject();
            contact.put("name", c.getName());
            contact.put("address", c.getAddress());
            ocontacts.put(contact);
            if (c.online()){
                c.closeConnection();
            }
        }
        ojs.put("contacts", ocontacts);
        PrintWriter pw = new PrintWriter(new FileWriter(contactfile));
        pw.println(ojs.toString());
        pw.flush();

        contacts.removeAll(contacts);
    }
}
