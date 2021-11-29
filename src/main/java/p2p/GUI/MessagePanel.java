package p2p.GUI;

import p2p.backend.Contact;
import p2p.backend.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Ez az osztály felelős a fő ablak üzenet küldő részének a megjelenítéséért.
 */
public class MessagePanel extends JPanel {
    private Contact contact;
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

    /**
     * @param c Létrehozza az üzenetek panelt a megfelelő kontakthoz.
     */
    public MessagePanel(Contact c){
        contact = c;

        GridBagConstraints gbc = new GridBagConstraints();

        //Elrendezés
        namepanel.setLayout(new GridBagLayout());
        name.setText(contact.getName());
        name.setFont(new Font(Font.DIALOG,  Font.BOLD, 20));
        online.setFont(new Font(Font.DIALOG,  Font.PLAIN, 13));
        //Offline esetén kikapcsolja a küldés funkciót
        if (contact.online()){
            online.setForeground(new Color(51, 204, 51));
            online.setText("online");
        }
        else {
            online.setForeground(new Color(255, 63, 63));
            online.setText("offline");
            text.setEnabled(false);
            send.setEnabled(false);
            file.setEnabled(false);
        }
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        namepanel.add(name, gbc);
        gbc.gridx = 1;
        namepanel.add(online, gbc);

        //Gombok beállítása
        send.addActionListener(new SendButtonListener());
        text.addActionListener(new SendButtonListener());
        file.addActionListener(new SendFileListener());
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

        //Üzenetek listája (JList)
        listModel = new DefaultListModel<>();
        for (Message m : contact.getMessages()){
            listModel.addElement(m);
        }
        list = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scroll.setViewportView(list);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        messages.add(scroll, gbc);

        //Saját üzenet renderelő beállítása
        list.setCellRenderer(new MessageRenderer(contact.getName()));
        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        list.addListSelectionListener(new MessageSelectionListener());
        list.setFixedCellHeight(90);

        text.requestFocus();

        this.setLayout(new BorderLayout());
        this.add(namepanel, BorderLayout.NORTH);
        this.add(messages, BorderLayout.CENTER);
        this.add(sender, BorderLayout.SOUTH);
    }

    /**
     * Értesíti egy új üzenet érkezéséről a panelt. Az új üzenetet megjeleníti.
     */
    public void newMessage(){
        listModel.addElement(contact.getMessages().getLast());
        list.ensureIndexIsVisible(listModel.indexOf(contact.getMessages().getLast()));
    }

    /**
     * Ez az osztály felelős a küldés gomb eseménykezeléséért.
     */
    public class SendButtonListener implements ActionListener {
        /**
         * Elküldi az üzenetet a kontakton keresztül és törli a szövegmező tartalmát.
         * @param e A gomb eseménye
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!text.getText().equals("")){
                contact.send(new Message(LocalDate.now(), LocalTime.now(), false, false, text.getText()));
                text.setText("");
            }
        }
    }

    /**
     * Ez az osztály felelős a fájl küldés gomb eseménykezeléséért.
     */
    public class SendFileListener implements ActionListener{
        /**
         * Megjelenít egy fájl választó ablakot, ahonnan kiválasztott fájlal elindítja a kontakton keresztül a fájl küldését.
         * @param e A gomb eseménye
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION){
                contact.sendFile(j.getSelectedFile());
            }
        }
    }

    /**
     * Ez az osztály felelős az üzenetekre kattintásért.
     */
    public class MessageSelectionListener implements ListSelectionListener {
        /**
         * Ha egy üzenetre kattintunk (kiválasztjuk), akkor ha az egy fájl, akkor megnyitja a rendszer alapértelmezetten hozzá rendelt programjával.
         * @param e Bekövetkezett esemény
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (((Message)list.getSelectedValue()).file){
                File f = new File(((Message)list.getSelectedValue()).text);
                Desktop dt = Desktop.getDesktop();
                if (f.exists()){
                    try {
                        dt.open(f);
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
}
