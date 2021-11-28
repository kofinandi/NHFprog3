import javax.swing.*;
import java.awt.*;

/**
 * A kontaktokhoz tartozó panel lista celláinak egyedi megjelenítője.
 */
public class CustomListRenderer implements ListCellRenderer<Object> {
    /**
     * A saját renderelő interface-hez illeszkedő konstruktora.
     * @param list JList, amihez tartozik a renderelő.
     * @param value Objektum, amit meg kell jeleníteni a JListben.
     * @param index Objektum indexe.
     * @param isSelected Objektum ki van-e választva.
     * @param cellHasFocus Objektum fokuszban van-e.
     * @return JPanel Visszaad egy JPanel-t, amiben a megfelelő adatok vannak megjelenítve.
     */
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        //Szövegek elrendezése
        JLabel name = new JLabel(((Contact)value).getName());
        JLabel address = new JLabel(((Contact)value).getAddress());
        address.setFont(new Font(Font.DIALOG, Font.ITALIC, 13));
        address.setForeground(new Color(141, 141, 141));
        if (isSelected) {
            panel.setBackground(new Color(236, 236, 236));
            panel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
        }
        panel.setBorder(BorderFactory.createLineBorder(new Color(194, 194, 194), 1));
        panel.setEnabled(list.isEnabled());
        panel.setFont(list.getFont());
        panel.setOpaque(true);

        gbc.ipady = 5;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(name, gbc);
        gbc.gridy = 2;
        panel.add(address, gbc);

        //Olvasatlan üzenetek számának megjelenítése
        if (((Contact)value).unread() > 0){
            JLabel unread = new JLabel(String.valueOf(((Contact)value).unread()));
            unread.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
            unread.setForeground(new Color(194, 0, 0));
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(unread, gbc);
        }

        //Mivel a JListnek nem működik semmilyen elrendezése, és a víszintes méret beállításokat sem tartja be, ezért egy láthatatlan szöveggel
        //minden üzenet mező ugyanolyan széles lesz, így megfelelően lesznek elrendezve.
        JLabel placeholder = new JLabel("---------------------------------------------------------------------------------------------");
        placeholder.setFont(new Font(Font.DIALOG, Font.PLAIN, 4));
        placeholder.setForeground(new Color(0, 0, 0, 0));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(placeholder, gbc);

        return panel;
    }
}
