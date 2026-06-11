package Graphics;

import javax.swing.*;
import java.awt.*;

public class StyledMessage {

    public static void showCheck(Component parent, String message) {
        showMessage(parent, "Check", message, new Color(220, 180, 80));
    }

    public static void showCheckmate(Component parent, String winner) {
        showMessage(parent, "Checkmate", winner + " wins!", new Color(220, 100, 50));
    }

    private static void showMessage(Component parent, String title, String message, Color accent) {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(28, 22, 18));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(accent);

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        messageLabel.setForeground(new Color(235, 225, 195));

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Serif", Font.BOLD, 16));
        okButton.setBackground(accent);
        okButton.setForeground(new Color(28, 22, 18));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(28, 22, 18));
        buttonPanel.add(okButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
