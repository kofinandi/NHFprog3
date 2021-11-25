import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class Server extends Thread{
    private ServerSocket server;
    private LinkedList<Contact> contacts;

    public Server(LinkedList<Contact> c){
        contacts = c;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(50000);
        } catch (IOException e) {
            System.out.println("Cannot start server.");
            System.exit(1);
        }

        while(true){
            try {
                Connection tmp = new Connection(server.accept());
                String address = tmp.getAddress().getHostAddress();

                Contact c = ContactHandler.haveContact(address);
                if (c != null){
                    c.connect(tmp);
                    tmp.init(c);
                    tmp.sendresponse("1");
                    Main.addConnection(tmp);
                }
                else {
                    if (Main.requestContact(address)){ //Szeretnenk-e hozzaadni?
                        c = new Contact("Megadott nev", tmp.getAddress(), tmp);
                        contacts.add(c);
                        tmp.init(c);
                        tmp.sendresponse("1");
                        Main.notifyContact();
                        Main.addConnection(tmp);
                    }
                    else {
                        c = new Contact(null, null, null);
                        tmp.init(c);
                        tmp.sendresponse("0");
                        tmp.close();
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection request error.");
            }
        }
    }
}
