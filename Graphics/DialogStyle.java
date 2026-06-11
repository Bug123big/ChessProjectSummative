package Graphics;

import javax.swing.*;
import java.awt.*;

public class DialogStyle {

    public static boolean confirm(Component parent, String title, String message) {
        final boolean[] result = {false};

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(460, 230);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIStyle.BG_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.GOLD, 3),
                BorderFactory.createEmptyBorder(22, 26, 22, 26)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UIStyle.titleFont(26));
        titleLabel.setForeground(UIStyle.GOLD_LIGHT);

        JLabel messageLabel = new JLabel(
                "<html><center>" + message + "</center></html>",
                SwingConstants.CENTER
        );
        messageLabel.setFont(UIStyle.normalFont(18));
        messageLabel.setForeground(UIStyle.TEXT);

        JButton yesButton = new JButton("Confirm");
        JButton noButton = new JButton("Cancel");

        UIStyle.styleButton(yesButton);
        UIStyle.styleButton(noButton);

        yesButton.setPreferredSize(new Dimension(120, 38));
        noButton.setPreferredSize(new Dimension(120, 38));

        yesButton.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        noButton.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(UIStyle.BG_DARK);
        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);

        return result[0];
    }

    public static void message(Component parent, String title, String message) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(460, 220);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIStyle.BG_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.GOLD, 3),
                BorderFactory.createEmptyBorder(22, 26, 22, 26)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UIStyle.titleFont(26));
        titleLabel.setForeground(UIStyle.GOLD_LIGHT);

        JLabel messageLabel = new JLabel(
                "<html><center>" + message + "</center></html>",
                SwingConstants.CENTER
        );
        messageLabel.setFont(UIStyle.normalFont(18));
        messageLabel.setForeground(UIStyle.TEXT);

        JButton okButton = new JButton("OK");
        UIStyle.styleButton(okButton);
        okButton.setPreferredSize(new Dimension(120, 38));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(UIStyle.BG_DARK);
        buttonPanel.add(okButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    
}
