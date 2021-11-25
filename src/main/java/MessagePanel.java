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

    private DefaultListModel<Message> listModel;
    private JList list;

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

        messages.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

       listModel = new DefaultListModel<>();
        for (Message m : contact.getMessages()){
            listModel.addElement(m);
        }
        list = new JList<>(listModel);
        list.setBounds(100,100, 75,75);
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(list);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        messages.add(scroll, gbc);

        list.setCellRenderer(new MessageRenderer());


        this.setLayout(new BorderLayout());
        this.add(name, BorderLayout.NORTH);
        this.add(messages, BorderLayout.CENTER);
        this.add(sender, BorderLayout.SOUTH);
    }

    public void newMessage(){
        listModel.addElement(contact.getMessages().getLast());
    }

    public class sendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            contact.send(new Message(LocalDate.now(), LocalTime.now(), false, false, text.getText()));
        }
    }
}
