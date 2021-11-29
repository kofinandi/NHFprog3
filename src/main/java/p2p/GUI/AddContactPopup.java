package p2p.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Felugró ablak, amiben megadhatjuk a hozzáadandó kontakt nevét és IP címét.
 */
public class AddContactPopup extends JPanel {
    private JTextField name = new JTextField(20);
    private JTextField address = new JTextField(20);
    private JLabel namelabel = new JLabel("Name: ");
    private JLabel addresslabel = new JLabel("Address: ");

    /**
     * Létrehozza a felugró ablakot a megfelelő elrendezéssel.
     */
    public AddContactPopup(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(namelabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(name, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(addresslabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(address, gbc);
    }
}
