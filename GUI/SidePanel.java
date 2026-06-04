package GUI;

import GameRole.GameState;
import GameRole.Player;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    private JLabel turnLabel;
    private JLabel stateLabel;

    public SidePanel() {
        setPreferredSize(new Dimension(220, 800));
        setBackground(new Color(45, 45, 45));
        setLayout(new GridLayout(6, 1, 10, 10));

        turnLabel = new JLabel("Turn: WHITE", SwingConstants.CENTER);
        stateLabel = new JLabel("State: PLAYING", SwingConstants.CENTER);

        turnLabel.setForeground(Color.WHITE);
        stateLabel.setForeground(Color.WHITE);

        add(turnLabel);
        add(stateLabel);
    }

    public void updateInfo(Player currentPlayer, GameState state) {
        turnLabel.setText("Turn: " + currentPlayer);
        stateLabel.setText("State: " + state);
    }
}
