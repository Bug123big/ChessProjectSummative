package GUI;

import Graphics.UIStyle;

import javax.swing.*;
import java.awt.*;

public class SelectPanel extends JPanel {

    private JRadioButton classicalButton;
    private JRadioButton chess960Button;

    public SelectPanel(
            Runnable classicalPVP,
            Runnable classicalAI,
            Runnable chess960PVP,
            Runnable chess960AI
    ) {
        setLayout(new BorderLayout());
        setBackground(UIStyle.BG_DARK);

        JLabel title = new JLabel("ZChess", SwingConstants.CENTER);
        title.setFont(UIStyle.titleFont(54));
        title.setForeground(UIStyle.GOLD_LIGHT);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        JLabel subtitle = new JLabel("Select rules, then choose a game mode", SwingConstants.CENTER);
        subtitle.setFont(UIStyle.normalFont(24));
        subtitle.setForeground(UIStyle.TEXT);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(title);
        headerPanel.add(subtitle);

        add(headerPanel, BorderLayout.NORTH);

        classicalButton = new JRadioButton("Classical Chess");
        chess960Button = new JRadioButton("Chess960");

        ButtonGroup ruleGroup = new ButtonGroup();
        ruleGroup.add(classicalButton);
        ruleGroup.add(chess960Button);

        classicalButton.setSelected(true);

        styleRadioButton(classicalButton);
        styleRadioButton(chess960Button);

        JPanel rulePanel = new JPanel(new GridLayout(1, 2, 40, 0));
        rulePanel.setOpaque(false);
        rulePanel.setBorder(BorderFactory.createEmptyBorder(40, 180, 30, 180));
        rulePanel.add(classicalButton);
        rulePanel.add(chess960Button);

        JButton pvpButton = createModeButton(
                "Two Players",
                "Play locally with another player"
        );

        JButton aiButton = createModeButton(
                "Player vs AI",
                "Challenge the Stockfish engine"
        );

        pvpButton.addActionListener(e -> {
            if (classicalButton.isSelected()) {
                classicalPVP.run();
            } else {
                chess960PVP.run();
            }
        });

        aiButton.addActionListener(e -> {
            if (classicalButton.isSelected()) {
                classicalAI.run();
            } else {
                chess960AI.run();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(60, 140, 160, 140));
        buttonPanel.add(pvpButton);
        buttonPanel.add(aiButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(rulePanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void styleRadioButton(JRadioButton button) {
        button.setFont(UIStyle.titleFont(24));
        button.setForeground(UIStyle.GOLD_LIGHT);
        button.setBackground(UIStyle.BG_DARK);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        g2.setColor(new Color(18, 15, 12));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(190, 150, 80, 70));
        g2.setStroke(new BasicStroke(3));

        g2.drawLine(80, 120, getWidth() - 80, 120);
        g2.drawLine(80, getHeight() - 80, getWidth() - 80, getHeight() - 80);

        g2.setColor(new Color(190, 150, 80, 25));
        g2.fillOval(-120, 200, 300, 300);
        g2.fillOval(getWidth() - 180, 160, 260, 260);
    }
}