import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MessageRenderer implements ListCellRenderer<Object> {
    String name;

    public MessageRenderer(String n){
        name = n;
    }

    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel sender;
        if (((Message)value).received){
            sender = new JLabel("me");
        }
        else{
            sender = new JLabel(name);
        }
        sender.setFont(new Font(Font.DIALOG_INPUT,  Font.BOLD, 13));
        sender.setForeground(new Color(58, 83, 173));
        JLabel date = new JLabel(((Message)value).date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.")));
        date.setForeground(new Color(143, 143, 143));
        JLabel time = new JLabel(((Message)value).time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        time.setForeground(new Color(143, 143, 143));
        JLabel text = new JLabel(((Message)value).text);

        panel.setBackground(list.getBackground());
        panel.setForeground(list.getForeground());

        panel.setEnabled(false);
        panel.setFont(list.getFont());
        panel.setOpaque(true);

        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(date, gbc);
        gbc.gridx = 1;
        panel.add(sender, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(time, gbc);
        gbc.gridx = 1;
        panel.add(text, gbc);

        return panel;
    }
}
