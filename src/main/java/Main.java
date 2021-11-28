import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Main {
    private static WindowFrame window;

    public static void main(String[] args) throws IOException { //Exceptiont kezelni kell
        LinkedList<Contact> contacts = new LinkedList<>();

        ContactHandler.setup(contacts);

        Thread s = new Server(contacts);
        s.start();

        window = new WindowFrame(contacts);
        window.setVisible(true);
    }

    public static void notifyMessage(Contact c){
        if (window != null){
            window.notifyMessage(c);
        }
    }

    public static void notifyContact(){
        if (window != null){
            window.notifyContact();
        }
    }

    public static String requestContact(String address){
        return window.requestContact(address);
    }

    public static void notifyOnline(Contact c){
        if (window != null){
            window.notifyOnline(c);
        }
    }

}
