import javax.swing.*;
import java.awt.*;

public class CustomListRenderer implements ListCellRenderer<Object> {
    public Component getListCellRendererComponent(
            JList<?> list,           // the list
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // does the cell have focus
    {
        JPanel panel = new JPanel();
        JLabel name = new JLabel(((Contact)value).getName());
        JLabel address = new JLabel(((Contact)value).getAddress());
        if (isSelected) {
            panel.setBackground(new Color(255, 36, 36));
            panel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
        }
        panel.setEnabled(list.isEnabled());
        panel.setFont(list.getFont());
        panel.setOpaque(true);
        panel.add(name);
        panel.add(address);

        return panel;
    }
}
