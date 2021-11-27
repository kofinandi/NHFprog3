import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class WindowFrame extends JFrame {
    private JPanel menu = new JPanel();
    private JPanel contactspane = new JPanel();
    private JPanel messages = new JPanel();

    private JButton refresh = new JButton("Refresh");
    private JButton add = new JButton("Add contact");

    LinkedList<Contact> contacts;
    DefaultListModel<Contact> listModel;
    JList<Contact> list;

    private LinkedList<JPanel> contactpanels = new LinkedList<>();

    public WindowFrame(LinkedList<Contact> cin){
        contacts = cin;
        this.setTitle("Peer to peer messenger");
        this.setSize(1200, 900);
        this.setResizable(true);
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new CloseListener());
        Image icon = Toolkit.getDefaultToolkit().getImage("/Users/kofinandi/OneDrive - Budapesti Műszaki és Gazdaságtudományi Egyetem/3. félév/Prog3/NHF/src/design/pear.png");
        this.setIconImage(icon);
        GridBagConstraints constraints = new GridBagConstraints();

        contactspane.setLayout(new GridLayout());

        listModel = new DefaultListModel<>();
        for (Contact c : contacts){
            listModel.addElement(c);
        }
        list = new JList<>(listModel);
        list.setBounds(100,100, 75,75);
        list.setFixedCellHeight(60);
        list.addListSelectionListener(new ContactSelectionListener());
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        scroll.setViewportView(list);
        contactspane.add(scroll);

        list.setCellRenderer(new CustomListRenderer());

        menu.setLayout(new BorderLayout());
        menu.setBackground(new Color(219, 227, 255, 255));
        refresh.addActionListener(new RefreshListener());
        menu.add(refresh, BorderLayout.WEST);
        add.addActionListener(new AddContactListener());
        menu.add(add, BorderLayout.EAST);

        messages.setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        if (contacts.size() > 0){
            messages.add(new MessagePanel(contacts.get(0)), constraints);
        }
        else {
            messages.setBackground(new Color(227, 227, 227));
        }

        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        menu.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(menu, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.2;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        contactspane.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(contactspane, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.8;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        messages.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(messages, constraints);

        list.setSelectedIndex(0);
        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void notifyMessage(Contact c){
        if (list.getSelectedValue() == c){
            ((MessagePanel)messages.getComponent(0)).newMessage();
        }
    }

    public void notifyContact(){
        listModel.addElement(contacts.getFirst());
    }

    public void notifyOnline(Contact c){
        if (list.getSelectedValue() == c){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0.8;
            gbc.weighty = 1;
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;

            messages.removeAll();
            messages.add(new MessagePanel(c), gbc);
            messages.updateUI();
        }
    }

    public String requestContact(String address){
        ConfirmContactPopup confirmcontact = new ConfirmContactPopup(address);
        ImageIcon icon = new ImageIcon(new File("").getAbsolutePath() + "/src/design/addcontact.png");
        if (JOptionPane.showConfirmDialog(null, confirmcontact, "Confirm contact", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon) == JOptionPane.YES_OPTION){
            return ((JTextField)confirmcontact.getComponent(2)).getText();
        }
        return null;
    }

    class CloseListener implements WindowListener{
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                ContactHandler.quit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }

    public class ContactSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            messages.removeAll();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.anchor = GridBagConstraints.PAGE_START;
            constraints.fill = GridBagConstraints.BOTH;
            messages.add(new MessagePanel(list.getSelectedValue()), constraints);
            messages.updateUI();
        }
    }

    public class AddContactListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            AddContactPopup addcontact = new AddContactPopup();
            ImageIcon icon = new ImageIcon(new File("").getAbsolutePath() + "/src/design/addcontact.png");
            if (JOptionPane.showConfirmDialog(null, addcontact, "Add contact", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon) == JOptionPane.OK_OPTION){
                System.out.println(((JTextField)addcontact.getComponent(1)).getText() + " " + ((JTextField)addcontact.getComponent(3)).getText());
                if (((JTextField)addcontact.getComponent(1)).getText() != null || ((JTextField)addcontact.getComponent(3)).getText() != null){
                    Contact newcontact = Contact.createContact(((JTextField)addcontact.getComponent(1)).getText(), ((JTextField)addcontact.getComponent(3)).getText());
                    if (newcontact != null){
                        ContactHandler.addContact(newcontact);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,"Cannot add contact!","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public class RefreshListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            ContactHandler.reload();
        }
    }
}
