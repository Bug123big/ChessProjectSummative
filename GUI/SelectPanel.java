package GUI;

import javax.swing.*;
import java.awt.*;

public class SelectPanel extends JPanel {

    public SelectPanel(Runnable twoPlayerAction, Runnable aiAction) {
        setLayout(new GridLayout(3, 1, 20, 20));
        setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Select Game Mode", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);

        JButton twoPlayerButton = new JButton("Two Players");
        JButton aiButton = new JButton("Player vs AI");

        twoPlayerButton.addActionListener(e -> twoPlayerAction.run());
        aiButton.addActionListener(e -> aiAction.run());

        add(title);
        add(twoPlayerButton);
        add(aiButton);
    }
}
