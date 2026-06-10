package GUI;

import javax.swing.*;
import java.awt.*;

public class CoverPanel extends JPanel {

    private Image backgroundImage;

    public CoverPanel(Runnable startAction) {
        backgroundImage = new ImageIcon("image/7f37d845-8526-4667-89a6-bdfc1a0211ba.png").getImage();

        setLayout(null);

        JLabel title = new JLabel("Welcome to ZChess", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 72));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 120, 1000, 90);

        JLabel hint = new JLabel("Click to Start", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.BOLD, 30));
        hint.setForeground(new Color(230, 230, 230));
        hint.setBounds(0, 650, 1000, 60);

        add(title);
        add(hint);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                startAction.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        g.setColor(new Color(0, 0, 0, 90));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
