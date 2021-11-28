import java.io.*;
import java.util.LinkedList;

/**
 * A program Main osztálya. Innen indul a kontaktok betöltése, a grafika és a szerver (ami figyeli az új csatlakozásokat).
 */
public class Main {
    private static WindowFrame window;

    public static void main(String[] args) {
        //Kontaktokat kezelő globálisan kezelt lista
        LinkedList<Contact> contacts = new LinkedList<>();

        //Kontaktok betöltése
        try {
            ContactHandler.setup(contacts);
        } catch (IOException e) {
            System.out.println("Cannot load contacts!");
        }

        //Szerver indítása
        Thread s = new Server(contacts);
        s.start();

        //GUI indítása
        window = new WindowFrame(contacts);
        window.setVisible(true);
    }

    /**
     * Ezzel a függvénnyel lehet értesíteni a Main-t, hogy új üzenet érkezett. Értesíti róla a megjelenítést.
     * @param c A kontakt, akitől az üzenet érkezett.
     */
    public static void notifyMessage(Contact c){
        if (window != null){
            window.notifyMessage(c);
        }
    }

    /**
     * Ezzel a függvénnyel lehet értesíteni a Main-t, hogy új kontaktot adtak hozzá. Értesíti róla a megjelenítést.
     */
    public static void notifyContact(){
        if (window != null){
            window.notifyContact();
        }
    }

    /**
     * Ezzel a függvénnyel lehet értesíteni a Main-t, hogy új kontakt szeretne csatlakozni. Értesíti róla a megjelenítést.
     * @param address Új kontakt IP címe.
     * @return Visszaadja az új kontaktnak adott nevet, ha elutasították, akkor null.
     */
    public static String requestContact(String address){
        return window.requestContact(address);
    }

    /**
     * Ezzel a függvénnyel lehet értesíteni a Main-t, hogy egy kontakt státusza megváltozott (online vagy offline). Értesíti róla a megjelenítést.
     * @param c Kontakt, amelyiknek megváltozott a státusza.
     */
    public static void notifyOnline(Contact c){
        if (window != null){
            window.notifyOnline(c);
        }
    }

}
