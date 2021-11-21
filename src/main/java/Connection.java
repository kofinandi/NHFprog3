import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    Socket s;
    PrintWriter out;
    MessageListener in;
    public Connection(Socket socket) throws IOException {
        s = socket;
        init();
    }

    public Connection(String address) throws IOException {
        s = new Socket(InetAddress.getByName(address), 50000);
        init();
    }

    private void init() throws IOException {
        out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
        in = new MessageListener(s, this);
        in.start();
    }

    void closed() throws IOException {
        s.close();
        Main.removeConnection(this);
    }

    public void send(String s){
        out.println(s);
        out.flush();
    }
}
