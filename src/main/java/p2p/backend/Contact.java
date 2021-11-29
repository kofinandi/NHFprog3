package p2p.backend;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.xml.sax.InputSource;
import p2p.Main;

import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

/**
 * Ez az osztály felelős a kontaktok kezeléséért. Eltárolja a nevet, IP címet, a kapcsolatot (ha van), az üzeneteket és az olvasatlan üzenetek számát.
 */
public class Contact {
    private String name;
    private InetAddress address;
    private Connection connection = null;
    private LinkedList<Message> messages = new LinkedList<>();
    private int unread = 0;

    /**
     * Létrehoz egy kontaktot a megadott adatokkal és visszaadja.
     * Azért nem konstruktor, mert itt a kapcsolatot is megpróbálja felépíteni, amit a konstruktor már csak eltárol.
     * @param inname Név az új kontakthoz.
     * @param inip IP cím az új kontakthoz.
     * @return Visszaadja az új kontaktot.
     */
    public static Contact createContact(String inname, String inip){
        InetAddress inaddress;
        try {
            inaddress = InetAddress.getByName(inip);
        } catch (UnknownHostException e) {
            return null;
        }

        Connection connection;

        try {
            connection = new Connection(inaddress);
        }
        catch (Exception e){
            return null;
        }

        Contact contact = new Contact(inname, inaddress, connection);
        try {
            connection.init(contact);
        } catch (IOException e) {
            System.out.println("IO exception!");
        }
        return contact;
    }

    /**
     * Betölti a kontaktot a megadott adatokkal és visszaadja.
     * Azért nem konstruktor, mert itt a kapcsolatot is megpróbálja felépíteni, amit a konstruktor már csak eltárol.
     * @param inname Név a betöltendő kontakthoz.
     * @param inip IP cím a betöltendő kontakthoz.
     * @return Visszaadja a betöltött kontaktot.
     */
    public static Contact loadContact(String inname, String inip){
        InetAddress inaddress;
        try {
            inaddress = InetAddress.getByName(inip);
        } catch (UnknownHostException e) {
            System.out.println("Invalid address in JSON!");
            return null;
        }

        Connection connection;

        try {
            connection = new Connection(inaddress);
        }
        catch (Exception t){
            return new Contact(inname, inaddress);
        }

        Contact contact = new Contact(inname, inaddress, connection);
        try {
            connection.init(contact);
        } catch (IOException e) {
            System.out.println("IO exception!");
        }

        return contact;
    }

    /**
     * Létrehozza a kontaktot a megadott adatokkal.
     * @param n Név.
     * @param a IP cím.
     * @param c Kapcsolat ami a kontakthoz tartozik.
     */
    public Contact(String n, InetAddress a, Connection c){
        name = n;
        address = a;
        connection = c;
        if (n != null){
            loadHistory();
        }
    }

    /**
     * Létrehozza a kontaktot a megadott adatokkal.
     * @param n Név.
     * @param a IP cím.
     */
    public Contact(String n, InetAddress a){
        name = n;
        address = a;
        if (n != null){
            loadHistory();
        }
    }

    /**
     * Betölti a kontakt korábbi üzeneteit XML fájlból.
     */
    private void loadHistory(){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,getAddress() + ".messages");

        //Ha már vannak üzenetek akkor tölti be
        if (messagefile.exists()){
            StringBuilder sb = new StringBuilder();

            FileReader fr = null;
            try {
                fr = new FileReader(messagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int data;
            while (true) {
                try {
                    if (!((data = fr.read()) != -1)) break;
                    sb.append((char) data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sb.append("</root>"); //Ha lezáró elem nélkül tároljuk az XML fájlt, akkor nem kell minden
            // alkalommal újraírni, hanem elég a végére fűzni az új üzenetet. A lezáró elem a feldolgozásnál kell csak.
            String xml = sb.toString();

            MessageParser p = new MessageParser(messages);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser sp = factory.newSAXParser();
                InputSource is = new InputSource(new StringReader(xml));
                sp.parse(is, p);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A megadott kapcsolatot a kontakthoz rendeli.
     * @param c A kapcsolat.
     */
    public void connect(Connection c){
        connection = c;
        Main.notifyOnline(this);
    }

    /**
     * Törli a kontakthoz tartozó kapcsolatot.
     */
    public void disconnect(){
        connection = null;
        Main.notifyOnline(this);
    }

    /**
     * Bezárja a kontakthoz tartozó kapcsolatot.
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Üzenetek olvasása, vagyis az olvasatlanok számának nullára állítása.
     */
    public void read(){
        unread = 0;
    }

    /**
     * @return Visszaadja az olvasatlan üzenetek számát.
     */
    public int unread(){
        return unread;
    }

    /**
     * @return Visszaadja a kontakthoz tartozó üzeneteket egy listában.
     */
    public LinkedList<Message> getMessages(){
        return messages;
    }

    /**
     * @return Visszaadja a kontakthoz tartozó kapcsolatot.
     */
    public Connection getConnection(){
        return connection;
    }

    /**
     * Elmenti és elküldi a paraméterül kapott üzenetet ennek a kontaktnak.
     * @param m Az üzenet, amit küldünk
     */
    public void send(Message m){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,getAddress() + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        //Ha még nincs fájl az üzeneteknek, akkor létrehoz egyet
        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //XML fájl végére szúrja az új üzenetet
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"0\" file = \"0\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Elküldi az üzenetet, hozzáadja az üzenetekhez és szól a p2p.Main-nek, hogy új üzenetet kell megjeleníteni
        messages.addLast(m);
        connection.send(m);
        Main.notifyMessage(this);
    }

    /**
     * Az adott kontakttól üzenetet kapunk.
     * @param m Az érkező üzenet.
     */
    public void receive(Message m){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,getAddress() + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        //Ha még nincs fájl az üzeneteknek, akkor létrehoz egyet
        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //XML fájl végére szúrja az új üzenetet
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"1\" file = \"" + m.file + "\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Hozzáadja az üzenetekhez, növeli az olvasatlan üzenetek számát és szól a p2p.Main-nek, hogy új üzenetet kell megjeleníteni
        messages.addLast(m);
        unread++;
        Main.notifyMessage(this);
    }

    /**
     * @return Visszaadja, hogy a kontakt online-e (van-e hozzá tartozó kapcsolat).
     */
    public boolean online(){
        return connection != null;
    }

    /**
     * @return Visszaadja a kontakt nevét.
     */
    public String getName(){
        return name;
    }

    /**
     * @return Visszaadja a kontakt IP címét.
     */
    public String getAddress(){
        return address.getHostAddress();
    }

    /**
     * Elküldi a paraméterül adott fájlt a kontaktnak.
     * @param f Küldendő fájl.
     */
    public void sendFile(File f){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,getAddress() + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        //Ha még nincs fájl az üzeneteknek, akkor létrehoz egyet
        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //XML fájl végére szúrja az új üzenetet
        Message m = new Message(LocalDate.now(), LocalTime.now(), false, true, f.getAbsolutePath());
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"0\" file = \"1\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Beolvassa a fájlt és elküldi
            FileInputStream input = new FileInputStream(f);
            connection.send(new Message(m.date, m.time, false, true, f.getName()));
            connection.sendfile(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Hozzáadja az üzenetekhez és szól a p2p.Main-nek, hogy új üzenetet kell megjeleníteni
        messages.addLast(m);
        Main.notifyMessage(this);
    }

    public void receiveFile(String date, String time, String filename){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,getAddress() + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        //Ha még nincs fájl az üzeneteknek, akkor létrehoz egyet
        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //XML fájl végére szúrja az új üzenetet
        Message m = new Message(LocalDate.parse(date), LocalTime.parse(time), true, true, filename);
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"1\" file = \"1\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Hozzáadja az üzenetekhez, növeli az olvasatlan üzenetek számát és szól a p2p.Main-nek, hogy új üzenetet kell megjeleníteni
        messages.addLast(m);
        unread++;
        Main.notifyMessage(this);
    }
}
