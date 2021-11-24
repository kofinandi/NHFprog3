import java.time.LocalDate;
import java.time.LocalTime;

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
