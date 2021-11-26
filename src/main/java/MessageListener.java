import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageListener extends Thread{
    private Socket socket;
    private BufferedReader in;
    private Connection connection;
    private Contact contact;

    private boolean file = false;
    private String filename;
    private FileOutputStream stream;
    private InputStream instream;

    public MessageListener(Socket s, Connection c1, Contact c2) throws IOException {
        socket = s;
        connection = c1;
        contact = c2;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String ins = null;
        while (true){
            try {
                if (!file){
                    ins = in.readLine();
                }
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
                        stream = new FileOutputStream(incoming, true);
                        instream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    contact.receive(new Message(LocalDate.parse(arr[0]), LocalTime.parse(arr[1]), true, false, arr[3]));
                }
            }
            else{
                StringBuilder str = new StringBuilder();
                int c;
                try {
                    while ((c = in.read()) != ' ') {
                        str.append((char) c);
                    }
                    arr[0] = str.toString();

                    str = new StringBuilder();
                    while ((c = in.read()) != ' ') {
                        str.append((char) c);
                    }
                    arr[1] = str.toString();

                    arr[2] = String.valueOf((char) in.read());
                    in.skip(1);
                } catch (IOException e){
                    System.out.println("Cannot download file!");
                }

                System.out.println(arr[0] + " " + arr[1] + " " + arr[2] + " ");

                if (arr[2].equals("0")){
                    file = false;
                    stream = null;
                    instream = null;
                    contact.receiveFile(arr[0], arr[1], arr[3]);
                }
                else {
                    try {
                        byte[] b = new byte[1000];
                        System.out.println("hello");
                        System.out.println("olvasott " + instream.read(b, 0, 1000));
                        System.out.println(new String(b));
                        stream.write(b);
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
