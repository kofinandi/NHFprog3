import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Contact {
    private String name;
    private InetAddress address;
    private Connection connection = null;

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

    public Contact(String n, InetAddress a, Connection c){
        name = n;
        address = a;
        connection = c;
    }

    public Contact(String n, InetAddress a){
        name = n;
        address = a;
    }

    public void connect(Connection c){
        connection = c;
    }

    public void disconnect(){
        connection = null;
    }

    public void send(String s){
        connection.send(s);
    }

    public void receive(String s){
        System.out.println(s);
    }
}
