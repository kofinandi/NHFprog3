import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageListener extends Thread{
    private Socket socket;
    private BufferedReader in;
    private Connection connection;
    private Contact contact;

    private boolean file = false;
    private String filename;
    private PrintWriter writer = null;
    private FileOutputStream output;

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

            String[] arr = ins.split(" ", 4);

            if (!file){
                if (arr[2].equals("1")){
                    file = true;
                    filename = arr[3];
                    File incoming = new File(filename);
                    try {
                        incoming.createNewFile();
                        output = new FileOutputStream(incoming, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    contact.receive(new Message(LocalDate.parse(arr[0]), LocalTime.parse(arr[1]), true, false, arr[3]));
                }
            }
            else{
                if (arr[2].equals("0")){
                    file = false;
                    output = null;
                    contact.receiveFile(arr[0], arr[1], arr[3]);
                }
                else {
                    try {
                        arr[3] = arr[3].replace("\0", "");
                        output.write(arr[3].getBytes());
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
