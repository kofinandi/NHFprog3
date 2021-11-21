import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageListener extends Thread{
    private BufferedReader in;

    public MessageListener(InputStream is){
        in = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        while (true){
            try {
                System.out.println(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
