import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    private ServerSocket server;
    private ArrayList<Contact> contacts;

    public Server(ArrayList<Contact> c){
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
                boolean found = false;

                contact : for (Contact c : contacts){
                    if (c.getAddress().equals(address)){
                        System.out.println("Egyezik");
                        c.connect(tmp);
                        tmp.init(c);
                        tmp.send("1");
                        Main.addConnection(tmp);
                        found = true;
                        break contact;
                    }
                }

                if (!found){
                    //Megy egy prompt hogy akarjuk-e a csatlakozast
                    if (true){ //Szeretnenk-e hozzaadni?
                        Contact c = new Contact("Megadott nev", tmp.getAddress(), tmp);
                        contacts.add(c);
                        tmp.init(c);
                        tmp.send("1");
                        Main.addConnection(tmp);
                    }
                    else {
                        Contact c = new Contact(null, null, null);
                        tmp.init(c);
                        tmp.send("0");
                        tmp.close();
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection request error.");
            }
        }
    }
}
