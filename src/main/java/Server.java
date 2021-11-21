import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private ServerSocket server;

    @Override
    public void run() {
        try {
            server = new ServerSocket(50000);
        } catch (IOException e) {
            System.out.println("Cannot start server.");
            System.exit(1);
        }

        while(true){
            try {
                Connection tmp = new Connection(server.accept());
                tmp.init(new Contact("Valami", tmp.getAddress(), tmp)); //Itt kell ellenorzni majd, hogy van-e mar ilyen kontaktunk, es azt beallitani
                tmp.send("1");
                Main.addConnection(tmp);
            } catch (IOException e) {
                System.out.println("Connection request error.");
            }
        }
    }
}
