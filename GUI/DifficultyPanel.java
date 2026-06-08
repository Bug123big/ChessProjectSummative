package GUI;

import javax.swing.*;
import java.awt.*;

public class DifficultyPanel extends JPanel {

    private JSlider difficultySlider;
    private JLabel levelLabel;

    public DifficultyPanel(StartAIAction startAIAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Select AI Difficulty", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        levelLabel = new JLabel("Level 1 - Easy", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 28));
        levelLabel.setForeground(Color.YELLOW);

        difficultySlider = new JSlider(1, 10, 1);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setBackground(new Color(30, 30, 30));
        difficultySlider.setForeground(Color.WHITE);

        difficultySlider.addChangeListener(e -> updateLevelLabel());

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 28));

        startButton.addActionListener(e -> {
            int level = difficultySlider.getValue();
            int moveTime = convertLevelToMoveTime(level);
            startAIAction.start(level, moveTime);
        });

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 120, 80, 120));

        centerPanel.add(levelLabel);
        centerPanel.add(difficultySlider);
        centerPanel.add(startButton);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void updateLevelLabel() {
        int level = difficultySlider.getValue();

        if (level <= 3) {
            levelLabel.setText("Level " + level + " - Easy");
        } else if (level <= 7) {
            levelLabel.setText("Level " + level + " - Medium");
        } else {
            levelLabel.setText("Level " + level + " - Hard");
        }
    }

    private int convertLevelToMoveTime(int level) {
        switch (level) {
            case 1: return 50;
            case 2: return 100;
            case 3: return 200;
            case 4: return 300;
            case 5: return 500;
            case 6: return 800;
            case 7: return 1200;
            case 8: return 2000;
            case 9: return 3000;
            case 10: return 5000;
            default: return 500;
        }
    }

    public interface StartAIAction {
        void start(int level, int moveTime);
    }
}
