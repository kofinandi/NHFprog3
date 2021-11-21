import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListener extends Thread{
    private Socket s;
    private BufferedReader in;
    private Connection c;

    public MessageListener(Socket socket, Connection connection) throws IOException {
        c = connection;
        s = socket;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public void run() {
        String ins;
        while (true){
            try {
                ins = in.readLine();
            } catch (IOException e) {
                try {
                    c.closed();
                } catch (IOException ee) {
                    e.printStackTrace();
                }
                return;
            }

            if (ins == null){
                try {
                    c.closed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            System.out.println(ins);
        }
    }
}
