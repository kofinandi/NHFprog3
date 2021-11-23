import javax.swing.*;
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

    private JLabel name = new JLabel("Név");

    private ArrayList<JPanel> contactpanels = new ArrayList<>();

    public WindowFrame(ArrayList<Contact> contacts){
        this.setTitle("Peer to peer messenger");
        this.setSize(1200, 900);
        this.setResizable(true);
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.addWindowListener(new CloseListener());

        contactspane.setLayout(new GridBagLayout());

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        for (Contact c : contacts){
            JPanel contact = new JPanel(new GridBagLayout());
            constraints.gridx = 0;
            constraints.gridy = 0;
            contact.add(new JLabel(c.getName()), constraints);
            constraints.gridx = 0;
            constraints.gridy = 1;
            contact.add(new JLabel(c.getAddress()), constraints);
            contact.setSize(0, 270);
            contactpanels.add(contact);
        }

        menu.setLayout(new BorderLayout());
        menu.setSize(0, 90);
        menu.setBackground(new Color(79, 79, 219));
        menu.add(refresh, BorderLayout.WEST);
        menu.add(add, BorderLayout.EAST);

        contactspane.setBackground(new Color(121, 211, 107));
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.PAGE_START;
        int i = 0;
        for (JPanel jp : contactpanels){
            constraints.gridy = i++;
            contactspane.add(jp, constraints);
        }

        messages.setBackground(new Color(232, 61, 61));
        messages.add(name);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        this.add(menu, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.2;
        constraints.weighty = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(contactspane, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.8;
        constraints.weighty = 0.5;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        this.add(messages, constraints);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    class CloseListener implements WindowListener{
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            System.out.println("Quiting");
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
}
