import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

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

        send.addActionListener(new sendButtonListener());

        sender.add(file);
        sender.add(text);
        sender.add(send);

        this.setLayout(new BorderLayout());
        this.add(name, BorderLayout.NORTH);
        this.add(messages, BorderLayout.CENTER);
        this.add(sender, BorderLayout.SOUTH);
    }

    public class sendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            contact.send(new Message(LocalDate.now(), LocalTime.now(), false, false, text.getText()));
        }
    }
}
