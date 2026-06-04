package GUI;

import javax.swing.*;
import java.awt.*;

public class CoverPanel extends JPanel {

    public CoverPanel(Runnable startAction) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel title = new JLabel("Chess Game", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 60));
        title.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Click to Start", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.PLAIN, 28));
        hint.setForeground(Color.LIGHT_GRAY);

        add(title, BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                startAction.run();
            }
        });
    }
}
