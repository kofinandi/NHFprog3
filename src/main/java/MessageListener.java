import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListener extends Thread{
    private Socket socket;
    private BufferedReader in;
    private Connection connection;
    private Contact contact;

    public MessageListener(Socket s, Connection c1, Contact c2) throws IOException {
        socket = s;
        connection = c1;
        contact = c2;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String ins;
        while (true){
            try {
                ins = in.readLine();
            } catch (IOException e) {
                try {
                    connection.close();
                } catch (IOException ee) {
                    e.printStackTrace();
                }
                return;
            }

            if (ins == null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            contact.receive(ins);
        }
    }
}
