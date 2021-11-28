import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * A kontaktok kezeléséért felelős csak statikus metódusokkal rendelkező osztály.
 * Azért statikus, mert szinte mindenhonnan el kell tudni érni ezeket a funkcionalitásokat, nem tartoznak adott objektumhoz.
 */
public class ContactHandler {
    private static LinkedList<Contact> contacts;
    private static File contactfile;

    /**
     * Betölti az elmentett kontaktokat egy JSON fájlból.
     * @param c A lista ahova a kontaktokat betölti.
     * @throws IOException
     */
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

            //Minden kontakthoz megpróbál csatlakozni
            for (int i = 0; i < ja.length(); i++){
                contacts.add(Contact.loadContact(ja.getJSONObject(i).getString("name"), ja.getJSONObject(i).getString("address")));
            }
        }
    }

    /**
     * Új kontaktot ad a kontaktok listájához.
     * @param c Új kontakt.
     */
    public static void addContact(Contact c){
        contacts.addFirst(c);
        Main.notifyContact();
    }

    /**
     * Visszaadja, hogy talált-e ilyen címmel kontaktot.
     * @param a Cím, amivel a kontaktot keressük.
     * @return A megtalált kontakt, egyéb esetben null.
     */
    public static Contact haveContact(String a){
        for (Contact i : contacts){
            if (i.getAddress().equals(a)){
                return i;
            }
        }
        return null;
    }

    /**
     * Kilépéskor minden kontaktról lecsatlakozik, valamint elmenti a kontaktokat egy JSON fájlba.
     * @throws IOException
     */
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

    /**
     * Minden kontaktról lecsatlakozik, majd megpróbál újra csatlakozni rá.
     */
    public static void reload(){
        for (Contact c : contacts){
            if (c.online()){
                try {
                    c.getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InetAddress address = null;
            try {
                address = InetAddress.getByName(c.getAddress());
            } catch (UnknownHostException e) {
                System.out.println("Cannot reconnect, wrong address!");
            }

            Connection connection;
            try {
                connection = new Connection(address);
            }
            catch (Exception t){
                continue;
            }

            try {
                connection.init(c);
            } catch (IOException e) {
                System.out.println("Cannot reconnect, cannot initialize connection!");
            }
            c.connect(connection);
        }
    }
}
