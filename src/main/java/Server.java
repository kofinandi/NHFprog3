import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

/**
 * Ez az osztály felelős azért, hogy az alkalmazás a háttérben folyamatosan figyelje az új csatlakozásokat, és azokat azonosítsa.
 * Igazából ez egy futtatható szál, hiszen minden működéstől függetlenül a háttérben kell dolgoznia.
 */
public class Server extends Thread{
    private ServerSocket server;
    private LinkedList<Contact> contacts;

    public Server(LinkedList<Contact> c){
        contacts = c;
    }

    /**
     * Szerver szál futtatása. Egy végtelen cilusban folyamatosan figyeli, hogy mikor jön új csatlakozás.
     * Ha új csatlakozás érkezik ellenőrzi, hogy új vagy korábbi kontakt csatlakozik-e. Ha korábbi, akkor engedi a csatlakozást.
     * Ha új, akkor szól a Main osztálynak, hogy felhasználói beavatkozás kell az új kontakt elfogadásához.
     * A csatlakozott kontaktot inicializálja.
     */
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

                //Ismert kontakt korábbról
                Contact c = ContactHandler.haveContact(address);
                if (c != null){
                    c.connect(tmp);
                    tmp.init(c);
                    tmp.sendresponse("1");
                }
                else {
                    //Új kontakt, kérdés, hogy elfogadják-e a csatlakozást
                    String newname = Main.requestContact(address);
                    //Ha igen, akkor inicializálja, felveszi a kontaktok közé és válaszban elküldi, hogy engedélyezték a csatlakozást
                    if (newname != null){
                        c = new Contact(newname, tmp.getAddress(), tmp);
                        contacts.addFirst(c);
                        tmp.init(c);
                        tmp.sendresponse("1");
                        Main.notifyContact();
                    }
                    //Ha nem, akkor elutasító üzenetet küld
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
