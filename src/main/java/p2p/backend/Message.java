package p2p.backend;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Üzenetek tárolására szolgáló osztály. Nem tud semmit csinálni, csak a megfelelő adatok együtt tárolásáért felel.
 */
public class Message {
    public LocalDate date;
    public LocalTime time;
    public boolean received;
    public boolean file;
    public String text;

    public Message(LocalDate d, LocalTime ti, boolean r, boolean f, String t){
        date = d;
        time = ti;
        received = r;
        file = f;
        text = t;
    }
}
