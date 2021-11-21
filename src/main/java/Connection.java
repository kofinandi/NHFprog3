import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connection {
    Socket s;
    public Connection(Socket socket){
        s = socket;
        InputStream is = null;
        try {
            is = s.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            System.out.println(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
