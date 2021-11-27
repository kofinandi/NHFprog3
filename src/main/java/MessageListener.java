import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageListener extends Thread{
    private Socket socket;
    private DataInputStream in;
    private Connection connection;
    private Contact contact;

    private String filename;
    private FileOutputStream stream;

    public MessageListener(Socket s, Connection c1, Contact c2) throws IOException {
        socket = s;
        connection = c1;
        contact = c2;
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        String ins;
        while (true){
            try {
                ins = in.readUTF();
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
            else{
                contact.receive(new Message(LocalDate.parse(arr[0]), LocalTime.parse(arr[1]), true, false, arr[3]));
            }
        }
    }
}
