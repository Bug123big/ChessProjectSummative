package GUI;

import Graphics.UIStyle;

import javax.swing.*;
import java.awt.*;

public class SelectPanel extends JPanel {

    public SelectPanel(Runnable twoPlayerAction, Runnable aiAction) {
        setLayout(new BorderLayout());
        setBackground(UIStyle.BG_DARK);

        JLabel title = new JLabel("Choose Your Battle", SwingConstants.CENTER);
        title.setFont(UIStyle.titleFont(54));
        title.setForeground(UIStyle.GOLD_LIGHT);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        JLabel subtitle = new JLabel("Select a game mode to begin", SwingConstants.CENTER);
        subtitle.setFont(UIStyle.normalFont(24));
        subtitle.setForeground(UIStyle.TEXT);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(title);
        headerPanel.add(subtitle);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(120, 140, 180, 140));

        JButton twoPlayerButton = createModeButton(
                "Two Players",
                "Face another player on the same board"
        );

        JButton aiButton = createModeButton(
                "Player vs AI",
                "Challenge the Stockfish chess engine"
        );

        twoPlayerButton.addActionListener(e -> twoPlayerAction.run());
        aiButton.addActionListener(e -> aiAction.run());

        buttonPanel.add(twoPlayerButton);
        buttonPanel.add(aiButton);

        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createModeButton(String title, String description) {
        JButton button = new JButton(
                "<html><center>"
                        + "<div style='font-size:24px;'>" + title + "</div>"
                        + "<br>"
                        + "<div style='font-size:12px;'>" + description + "</div>"
                        + "</center></html>"
        );

        button.setFocusPainted(false);
        button.setBackground(UIStyle.PANEL_SOFT);
        button.setForeground(UIStyle.GOLD_LIGHT);
        button.setFont(UIStyle.titleFont(22));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.GOLD, 3, true),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(65, 52, 38));
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(UIStyle.PANEL_SOFT);
                button.setForeground(UIStyle.GOLD_LIGHT);
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // dark overlay
        g2.setColor(new Color(18, 15, 12));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // subtle golden lines
        g2.setColor(new Color(190, 150, 80, 70));
        g2.setStroke(new BasicStroke(3));

        g2.drawLine(80, 120, getWidth() - 80, 120);
        g2.drawLine(80, getHeight() - 80, getWidth() - 80, getHeight() - 80);

        // background circles / decoration
        g2.setColor(new Color(190, 150, 80, 25));
        g2.fillOval(-120, 200, 300, 300);
        g2.fillOval(getWidth() - 180, 160, 260, 260);
    }
}