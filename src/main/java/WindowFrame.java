import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

public class WindowFrame extends JFrame {
    private JPanel menu = new JPanel();
    private JPanel contactspane = new JPanel();
    private JPanel messages = new JPanel();

    private JButton refresh = new JButton("Refresh");
    private JButton add = new JButton("Add contact");

    JList<Contact> list;

    private ArrayList<JPanel> contactpanels = new ArrayList<>();

    public WindowFrame(ArrayList<Contact> contacts){
        this.setTitle("Peer to peer messenger");
        this.setSize(1200, 900);
        this.setResizable(true);
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new CloseListener());
        GridBagConstraints constraints = new GridBagConstraints();

        contactspane.setLayout(new GridLayout());

        DefaultListModel<Contact> l1 = new DefaultListModel<>();
        for (Contact c : contacts){
            l1.addElement(c);
        }
        list = new JList<>(l1);
        list.setBounds(100,100, 75,75);
        list.setFixedCellHeight(60);
        list.addListSelectionListener(new ContactSelectionListener());
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(list);
        contactspane.add(scroll);

        list.setCellRenderer(new CustomListRenderer());
        contactspane.setBackground(new Color(121, 211, 107));

        /*
        contactspane.setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.PAGE_START;
        for (Contact c : contacts){
            JPanel contact = new JPanel(new GridBagLayout());
            constraints.gridx = 0;
            constraints.gridy = 0;
            contact.add(new JLabel(c.getName()), constraints);
            constraints.gridx = 0;
            constraints.gridy = 1;
            contact.add(new JLabel(c.getAddress()), constraints);
            contactpanels.add(contact);
        }


        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;

        int i = 0;
        for (JPanel jp : contactpanels){
            constraints.gridy = i++;
            contactspane.add(jp, constraints);
        }
         */

        menu.setLayout(new BorderLayout());
        menu.setBackground(new Color(79, 79, 219));
        menu.add(refresh, BorderLayout.WEST);
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
        this.add(menu, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.2;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(contactspane, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.8;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        this.add(messages, constraints);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void notifyMessage(Contact c){
        if (list.getSelectedValue() == c){
            ((MessagePanel)messages.getComponent(0)).newMessage();
        }
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
}
