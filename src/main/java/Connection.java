import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;

public class Connection {
    private Socket s = new Socket();
    private DataOutputStream out;
    private MessageListener in;
    private Contact contact;

    public Connection(Socket socket) throws IOException {
        s = socket;
    }

    public Connection(InetAddress address) throws IOException, ConnectionDenied {
        SocketAddress socketAddress = new InetSocketAddress(address, 50000);
        s.connect(socketAddress, 1500);
        DataInputStream accept = new DataInputStream(s.getInputStream());
        if (!accept.readUTF().equals("1")){
            s.close();
            throw new ConnectionDenied();
        }
    }

    public void init(Contact c) throws IOException {
        contact = c;
        out = new DataOutputStream(s.getOutputStream());
        in = new MessageListener(s, this, c);
        in.start();
    }

    void close() throws IOException {
        s.close();
        if (contact != null){
            contact.disconnect();
        }
        Main.removeConnection(this);
    }

    public void send(Message m){
        try {
            out.writeUTF(m.date + " " + m.time + " " + (m.file ? 1 : 0) + " " + m.text);
            System.out.println(m.date + " " + m.time + " " + (m.file ? 1 : 0) + " " + m.text);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendfile(FileInputStream input) throws IOException {
        int res = IOUtils.copy(input, out);
    }

    public void sendresponse(String s){
        try {
            out.writeUTF(s);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getAddress(){
        return s.getInetAddress();
    }
}
