import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.*;
import java.util.LinkedList;

public class Contact {
    private String name;
    private InetAddress address;
    private Connection connection = null;
    private LinkedList<Message> messages = new LinkedList<>();

    static Contact createContact(String inname, String inip){
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
        catch (SocketTimeoutException t){
            System.out.println("Cannot connect to contact!");
            return null;
        } catch (IOException e) {
            System.out.println("Connection error!");
            return null;
        } catch (ConnectionDenied e) {
            System.out.println("Connection refused!");
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

    static Contact loadContact(String inname, String inip){
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

    public Contact(String n, InetAddress a, Connection c){
        name = n;
        address = a;
        connection = c;
        if (n != null){
            loadHistory();
        }
    }

    public Contact(String n, InetAddress a){
        name = n;
        address = a;
        if (n != null){
            loadHistory();
        }
    }

    private void loadHistory(){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,address.getHostAddress() + ".messages");

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
            sb.append("</root>");
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

    public void connect(Connection c){
        connection = c;
    }

    public void disconnect(){
        connection = null;
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Message> getMessages(){
        return messages;
    }

    public Connection getConnection(){
        return connection;
    }

    public void send(Message m){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,address + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"" + 0 + "\" file = \"" + m.file + "\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages.addLast(m);
        connection.send(m);
        Main.notifyMessage(this);
    }

    public void receive(Message m){
        File messagesfolder = new File("messages");
        File messagefile = new File(messagesfolder,name.toLowerCase() + ".messages");

        StringBuilder xmlStringBuilder = new StringBuilder();

        if (!messagefile.exists()){
            try {
                messagefile.createNewFile();
                xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xmlStringBuilder.append("<root>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        xmlStringBuilder.append("<message date = \"" + m.date + "\" time = \"" + m.time + "\" received = \"" + 1 + "\" file = \"" + m.file + "\">" + m.text + "</message>");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(messagefile, true));
            pw.println(xmlStringBuilder.toString());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages.addLast(m);
        Main.notifyMessage(this);
    }

    public boolean online(){
        return connection != null;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address.getHostAddress();
    }
}
