package Graphics;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIStyle {
    public static final Color BG_DARK = new Color(18, 15, 12);
    public static final Color PANEL_DARK = new Color(35, 29, 23);
    public static final Color PANEL_SOFT = new Color(48, 39, 30);
    public static final Color GOLD = new Color(190, 150, 80);
    public static final Color GOLD_LIGHT = new Color(230, 190, 110);
    public static final Color TEXT = new Color(235, 225, 205);

    public static Font titleFont(int size) {
        return new Font("Serif", Font.BOLD, size);
    }

    public static Font normalFont(int size) {
        return new Font("Serif", Font.PLAIN, size);
    }

    public static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PANEL_SOFT);
        button.setForeground(GOLD_LIGHT);
        button.setFont(titleFont(16));
        button.setBorder(new CompoundBorder(
                new LineBorder(GOLD, 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_DARK);
        panel.setBorder(new CompoundBorder(
                new LineBorder(GOLD, 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT);
        label.setFont(normalFont(16));
    }
}
