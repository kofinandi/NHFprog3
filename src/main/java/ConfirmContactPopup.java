import javax.swing.*;
import java.awt.*;

public class ConfirmContactPopup extends JPanel {
    private JTextField name = new JTextField(20);
    private JLabel namelabel = new JLabel("Name: ");
    private JLabel addresslabel = new JLabel();

    public ConfirmContactPopup(String address){
        this.setLayout(new GridBagLayout());
        addresslabel.setText("Would you like to add contact " + address + "?");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        this.add(addresslabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(namelabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(name, gbc);
    }
}
