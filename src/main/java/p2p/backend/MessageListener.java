package p2p.backend;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Az osztály feladata, hogy figyelje mikor érkezik üzenet a megadott Socket kapcsolaton,
 * és ha üzenet érkezik akkor azt továbbítsa a megfelelő kontaktnak.
 * Valójában ez egy Thread, mivel folyamatosan futnia kell a háttérben minden megnyitott kapcsolathoz.
 */
public class MessageListener extends Thread{
    private Socket socket;
    private DataInputStream in;
    private Connection connection;
    private Contact contact;

    private String filename;
    private FileOutputStream stream;

    /**
     * @param s Socket, ahol kapcsolatot figyelnie kell és beolvasni róla ha tud
     * @param c1 Kapcsolat, ami ezért a beolvasóért is felel
     * @param c2 Kontakt akihez tartozik az üzenet fogadás
     * @throws IOException
     */
    public MessageListener(Socket s, Connection c1, Contact c2) throws IOException {
        socket = s;
        connection = c1;
        contact = c2;
        in = new DataInputStream(socket.getInputStream());
    }

    /**
     * Egy végtelen ciklusban folyamatosan próbál beolvasni a megadott Socket DataInputStreamjéről. Ha null-t kap (Windows) vagy exception
     * jön beolvasáskor (Mac), akkor megszakadt a túloldalon a kapcsolat, ezért itt is leállítja. Ha tud beolvasni, akkor
     * létrehozza az üzenetet, és továbbadja a kontaktnak, ha pedig fájl jön azt letölti és értesíti a kontaktot róla.
     */
    @Override
    public void run() {
        String ins;
        while (true){
            try {
                ins = in.readUTF();
            } catch (IOException e) { //Kapcsolat bezárult (Mac)
                try {
                    connection.close();
                } catch (IOException ee) {
                    e.printStackTrace();
                }
                return;
            }

            //Kapcsolat bezárult (Windows)
            if (ins == null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            String[] arr = ins.split(" ", 4);

            //Fájl fogadása
            if (arr[2].equals("1")){
                filename = arr[3];
                File incoming = new File(filename);
                contact.receiveFile(arr[0], arr[1], filename);
                try {
                    stream = new FileOutputStream(incoming);
                    IOUtils.copyLarge(in, stream);
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Egyszerű üzenet fogadása
            else{
                contact.receive(new Message(LocalDate.parse(arr[0]), LocalTime.parse(arr[1]), true, false, arr[3]));
            }
        }
    }
}
