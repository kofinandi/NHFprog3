import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {
    Contact contact;
    private JLabel name = new JLabel();
    private JPanel messages = new JPanel();
    private JPanel sender = new JPanel();

    private JButton file = new JButton("Send file");
    private JTextField text = new JTextField(20);
    private JButton send = new JButton("Send!");

    public MessagePanel(Contact c){
        contact = c;
        name.setText(contact.getName());

        sender.add(file);
        sender.add(text);
        sender.add(send);

        this.setLayout(new BorderLayout());
        this.add(name, BorderLayout.NORTH);
        this.add(messages, BorderLayout.CENTER);
        this.add(sender, BorderLayout.SOUTH);
    }
}
