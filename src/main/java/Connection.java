import java.io.*;
import java.net.*;

public class Connection {
    private Socket s = new Socket();
    private PrintWriter out;
    private MessageListener in;
    private Contact contact;

    public Connection(Socket socket) throws IOException {
        s = socket;
    }

    public Connection(InetAddress address) throws IOException, ConnectionDenied {
        SocketAddress socketAddress = new InetSocketAddress(address, 50000);
        s.connect(socketAddress, 1500);
        BufferedReader accept = new BufferedReader(new InputStreamReader(s.getInputStream()));
        if (!accept.readLine().equals("1")){
            s.close();
            throw new ConnectionDenied();
        }
    }

    public void init(Contact c) throws IOException {
        contact = c;
        out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
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
        out.println(m.date + " " + m.time + " " + (m.file ? 1 : 0) + " " + m.text);
        System.out.println(m.date + " " + m.time + " " + (m.file ? 1 : 0) + " " + m.text);
        out.flush();
    }

    public void sendfile(Message m, byte[] file){
        out.print(m.date + " " + m.time + " 1 ");
        System.out.print(m.date + " " + m.time + " 1 ");
        out.flush();
        try {
            BufferedOutputStream stream = new BufferedOutputStream(s.getOutputStream());
            stream.write(file);
            System.out.println(new String(file));
            stream.flush();
        } catch (IOException e) {
            System.out.println("Cannot send file!");
        }
    }

    public void sendresponse(String s){
        out.println(s);
        out.flush();
    }

    public InetAddress getAddress(){
        return s.getInetAddress();
    }
}
