import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessagePanel extends JPanel {
    Contact contact;
    private JPanel namepanel = new JPanel();
    private JPanel messages = new JPanel();
    private JPanel sender = new JPanel();

    private JLabel name = new JLabel();
    private JLabel online = new JLabel("offline");

    private DefaultListModel<Message> listModel;
    private JList list;

    private JTextField text = new JTextField();
    private JButton send = new JButton("Send");
    private JButton file = new JButton("Send file");

    public MessagePanel(Contact c){
        contact = c;

        GridBagConstraints gbc = new GridBagConstraints();

        namepanel.setLayout(new GridBagLayout());
        name.setText(contact.getName());
        name.setFont(new Font(Font.DIALOG_INPUT,  Font.BOLD, 20));
        online.setFont(new Font(Font.DIALOG_INPUT,  Font.PLAIN, 13));
        if (contact.online()){
            online.setForeground(new Color(51, 204, 51));
            online.setText("online");
        }
        else {
            online.setForeground(new Color(255, 63, 63));
            online.setText("offline");
        }
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        namepanel.add(name, gbc);
        gbc.gridx = 1;
        namepanel.add(online, gbc);

        send.addActionListener(new sendButtonListener());
        text.addActionListener(new sendButtonListener());
        file.addActionListener(new sendFileListener());
        file.setSize(30, 30);

        sender.setLayout(new GridBagLayout());
        gbc.ipadx = 20;
        gbc.ipady = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sender.add(text, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        sender.add(send, gbc);
        gbc.gridx = 2;
        sender.add(file, gbc);

        messages.setLayout(new GridBagLayout());

       listModel = new DefaultListModel<>();
        for (Message m : contact.getMessages()){
            listModel.addElement(m);
        }
        list = new JList<>(listModel);
        list.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scroll.setViewportView(list);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        messages.add(scroll, gbc);

        list.setCellRenderer(new MessageRenderer(contact.getName()));
        list.setFocusable(false);
        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        text.requestFocus();

        this.setLayout(new BorderLayout());
        this.add(namepanel, BorderLayout.NORTH);
        this.add(messages, BorderLayout.CENTER);
        this.add(sender, BorderLayout.SOUTH);
    }

    public void newMessage(){
        listModel.addElement(contact.getMessages().getLast());
    }

    public class sendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!text.getText().equals("")){
                text.setText("");
                contact.send(new Message(LocalDate.now(), LocalTime.now(), false, false, text.getText()));
            }
        }
    }

    public class sendFileListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION){
                contact.sendFile(j.getSelectedFile());
            }
        }
    }
}
