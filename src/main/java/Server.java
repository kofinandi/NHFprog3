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
                Main.addConnection(new Connection(server.accept()));
            } catch (IOException e) {
                System.out.println("Server connection request error.");
            }
        }
    }
}
