import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    Socket s;
    PrintWriter out;
    MessageListener in;
    public Connection(Socket socket) throws IOException { // Exceptiont kezelni kell
        s = socket;
        out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
        in = new MessageListener(s.getInputStream());
        in.start();
    }

    public Connection(String address) throws UnknownHostException {
        try {
            s = new Socket(InetAddress.getByName(address), 50000, InetAddress.getLocalHost(), 50000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String s){
        out.println(s);
        out.flush();
    }
}
