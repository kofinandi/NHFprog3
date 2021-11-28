import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;

/**
 * A kapcsolatokat kezelő osztály.
 */
public class Connection {
    private Socket s = new Socket();
    private DataOutputStream out;
    private MessageListener in;
    private Contact contact;

    /**
     * Létrehoz egy kapcsolatot a megadott Sockettel.
     * (Azért kell olyan konstruktor is, ami nem csatlakozik, mert ilyet csinál a szerver, amikor egy új kapcsolat jön.)
     * @param socket A Socket.
     * @throws IOException
     */
    public Connection(Socket socket) throws IOException {
        s = socket;
    }

    /**
     * Létrehozza a kapcsolatot és megpróbál csatlakozni, ha nem sikerül, hibát dob.
     * @param address
     * @throws IOException
     * @throws ConnectionDenied
     */
    public Connection(InetAddress address) throws IOException, ConnectionDenied {
        SocketAddress socketAddress = new InetSocketAddress(address, 50000);
        s.connect(socketAddress, 1500); //Legfeljebb 1.5 máasodpercig várjuk a választ.
        DataInputStream accept = new DataInputStream(s.getInputStream());
        if (!accept.readUTF().equals("1")){//Elfogadták-e a túloldalon a csatlakozásunkat
            s.close();
            throw new ConnectionDenied();
        }
    }

    /**
     * Inicalizálja a kapcsolatot. Létrehozza a kimenő és bemenő streameket és hozzárendeli egy kontakthoz
     * (akit értesíteni kell, ha üzenet jön vagy megszakad a kapcsolat).
     * @param c Kontakt, akihez a kapcsolat tartozik.
     * @throws IOException
     */
    public void init(Contact c) throws IOException {
        contact = c;
        out = new DataOutputStream(s.getOutputStream());
        in = new MessageListener(s, this, c);
        in.start();
    }

    /**
     * Bezárja a Socketet és lecsatlakoztatja a kontaktról (ekkor az már offline-nak látszik).
     * @throws IOException
     */
    void close() throws IOException {
        s.close();
        if (contact != null){
            contact.disconnect();
        }
    }

    /**
     * Üzenet küldése.
     * @param m Küldendő üzenet.
     */
    public void send(Message m){
        try {
            out.writeUTF(m.date + " " + m.time + " " + (m.file ? 1 : 0) + " " + m.text);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fájl küldése.
     * @param input FileInputStream, ahonnan a fájlt be lehet olvasni.
     * @throws IOException
     */
    public void sendfile(FileInputStream input) throws IOException {
        IOUtils.copyLarge(input, out);
        out.flush();
        input.close();
        ContactHandler.reload();
    }

    /**
     * Rövid válasz küldése, hogy elfogadjuk-e a csatlakozást.
     * @param s
     */
    public void sendresponse(String s){
        try {
            out.writeUTF(s);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Visszaadja a kapcsolat IP címét.
     */
    public InetAddress getAddress(){
        return s.getInetAddress();
    }
}
