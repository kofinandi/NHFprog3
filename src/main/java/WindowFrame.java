import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * Ez az osztály felelős a fő ablak megjelenítéséért és elrendezéséért,
 * valamint ezen keresztül tartja a kapcsolatot a backend és a GUI.
 */
public class WindowFrame extends JFrame {
    private JPanel menu = new JPanel();
    private JPanel contactspane = new JPanel();
    private JPanel messages = new JPanel();

    private JButton refresh = new JButton("Refresh");
    private JButton add = new JButton("Add contact");
    private JLabel ipaddress = new JLabel();

    LinkedList<Contact> contacts;
    DefaultListModel<Contact> listModel;
    JList<Contact> list;

    /**
     * @param cin Lista, amiben a kontaktok tárolódnak. Ezzel tudja megjeleníteni a kontakot listáját, és a hozzájuk tartozó üzeneteket.
     */
    public WindowFrame(LinkedList<Contact> cin){
        contacts = cin;
        //Ablak beállítása
        this.setTitle("Peer to peer messenger");
        this.setSize(1000, 650);
        this.setResizable(true);
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new CloseListener());
        Image icon = Toolkit.getDefaultToolkit().getImage("/Users/kofinandi/OneDrive - Budapesti Műszaki és Gazdaságtudományi Egyetem/3. félév/Prog3/NHF/src/design/pear.png");
        this.setIconImage(icon);
        GridBagConstraints constraints = new GridBagConstraints();

        //Kontaktlista beállítása
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

        //Menü beállítása
        menu.setLayout(new BorderLayout());
        menu.setBackground(new Color(219, 227, 255, 255));
        refresh.addActionListener(new RefreshListener());
        menu.add(refresh, BorderLayout.WEST);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(menu.getBackground());
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
        }
        ipaddress.setText("Local IP: " + addr.getHostAddress());
        ipaddress.setForeground(new Color(87, 87, 87));
        container.add(ipaddress, BorderLayout.WEST);
        add.addActionListener(new AddContactListener());
        container.add(add, BorderLayout.EAST);
        menu.add(container, BorderLayout.EAST);

        //Üzenetek rész beállítása
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

        //Menü hozzáadása az ablakhoz az elrendezéssel
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        menu.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(menu, constraints);

        //Kontaktok panel hozzáadása az ablakhoz az elrendezéssel
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.3;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        contactspane.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(contactspane, constraints);

        //Üzenet panel hozzáadása az ablakhoz az elrendezéssel
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.7;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        messages.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        this.add(messages, constraints);

        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * @param c Értesíti a megjelenítést, hogy az adott kontakt üzenetet kapott. Ha éppen láthatóak az üzenetei, akkor meg is jelennek.
     */
    public void notifyMessage(Contact c){
        if (list.getSelectedValue() == c){
            ((MessagePanel)messages.getComponent(0)).newMessage();
            c.read();
        }
        list.updateUI();
    }

    /**
     * Értesíti a megjelenítést, hogy új kontaktot adtak a listához, ez alapján frissül.
     */
    public void notifyContact(){
        listModel.addElement(contacts.getFirst());
    }

    /**
     * @param c Értesíti a megjelenítést, hogy az adott kontakt online vagy offline lett. Ha éppen ennek a kontaktnak az üzenetei láthatóak akkor megjelenik a státusza.
     */
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

    /**
     * @param address Értesíti a megjelenítést, hogy egy új kontakt szeretne csatlakozni. Feldob egy ablakot, amiben megkérdezi, hogy szeretnénk-e hozzáadni, ha igen akkor milyen néven.
     *                Ha nem adunk meg nevet akkor úgy veszi mintha elutasítottuk volna.
     * @return Visszaadja az elfogadott kontakt nevét, vagy null-t, ha elutasítják.
     */
    public String requestContact(String address){
        ConfirmContactPopup confirmcontact = new ConfirmContactPopup(address);
        ImageIcon icon = new ImageIcon(new File("").getAbsolutePath() + "/src/design/addcontact.png");
        if (JOptionPane.showConfirmDialog(null, confirmcontact, "Confirm contact", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon) == JOptionPane.YES_OPTION){
            if (((JTextField)confirmcontact.getComponent(2)).getText() == null){
                return null;
            }
            return ((JTextField)confirmcontact.getComponent(2)).getText();
        }
        return null;
    }

    /**
     * Ez az osztály felelős az ablak eseményeinek a kezeléséért.
     */
    class CloseListener implements WindowListener{
        /**
         * Megnyitás után kiválasztja az első elemet a kontaktok közül, ha létezik.
         * @param e Esemény objektuma.
         */
        @Override
        public void windowOpened(WindowEvent e) {
            try {
                if (list.getModel().getSize() > 0){
                    list.setSelectedIndex(0);
                }
            } catch (NullPointerException ex){
                //Indításkor néha ez a metódus exceptiont dob, de helyesen működik
            }
        }

        /**
         * Az ablak bezárásakor meghívja a kontakt kezelő kilépő eseményét, majd kilép.
         * @param e Esemény objektuma.
         */
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

    /**
     * Ez az osztály felelős, azért, hogy kezelje, ha egy kontaktot kiválasztanak a listából.
     */
    public class ContactSelectionListener implements ListSelectionListener {
        /**
         * Kicseréli a megjelenített üzeneteket a kiválasztott kontakt üzeneteire.
         * @param e Esemény objektuma.
         */
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
            list.getSelectedValue().read();
            messages.updateUI();
            list.updateUI();
        }
    }

    /**
     * Ez az osztály felelős, azért, hogy kezelje, ha rákattintanak az új kontakt gombra.
     */
    public class AddContactListener implements ActionListener{
        /**
         * Feldob egy ablakot ahol megadhatjuk az új kontakt nevét és IP címét. Ha OK-t választunk megpróbál csatlakozni a kontakthoz.
         * Ha nem sikerül (offline, hiba vagy elutasította a fogadó), kiírja, és nem kerül hozzáadásra a kontakt.
         * @param e Esemény objektuma.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            AddContactPopup addcontact = new AddContactPopup();
            ImageIcon icon = new ImageIcon(new File("").getAbsolutePath() + "/src/design/addcontact.png");
            if (JOptionPane.showConfirmDialog(null, addcontact, "Add contact", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon) == JOptionPane.OK_OPTION){
                if (!((JTextField)addcontact.getComponent(1)).getText().equals("") || !((JTextField)addcontact.getComponent(3)).getText().equals("")){
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

    /**
     * Ez az osztály felelős, azért, hogy kezelje, ha rákattintanak a frissítés gombra.
     */
    public class RefreshListener implements ActionListener{
        /**
         * Meghívja a kontakt kezelő újratöltés metódusát, valamint frissíti a kiírt IP címet.
         * @param e Esemény objektuma.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            InetAddress addr = null;
            try {
                addr = InetAddress.getLocalHost();
            } catch (UnknownHostException ex) {
            }
            ipaddress.setText("Local IP: " + addr.getHostAddress());
            ContactHandler.reload();
        }
    }
}
