package GUI;

import Graphics.UIStyle;

import javax.swing.*;
import java.awt.*;

public class DifficultyPanel extends JPanel {

    private JSlider difficultySlider;
    private JLabel levelLabel;

    private JRadioButton whiteButton;
    private JRadioButton blackButton;
    private JRadioButton randomButton;

    public DifficultyPanel(StartAIAction startAIAction) {
        setLayout(new BorderLayout());
        setBackground(UIStyle.BG_DARK);

        JLabel title = new JLabel("Select AI Difficulty", SwingConstants.CENTER);
        title.setFont(UIStyle.titleFont(46));
        title.setForeground(UIStyle.GOLD_LIGHT);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        levelLabel = new JLabel("Level 1 - Easy", SwingConstants.CENTER);
        levelLabel.setFont(UIStyle.titleFont(28));
        levelLabel.setForeground(UIStyle.GOLD_LIGHT);

        difficultySlider = new JSlider(1, 10, 1);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setBackground(UIStyle.BG_DARK);
        difficultySlider.setForeground(UIStyle.GOLD_LIGHT);
        difficultySlider.addChangeListener(e -> updateLevelLabel());

        JPanel colorPanel = createColorPanel();

        JButton startButton = new JButton("Start Game");
        UIStyle.styleButton(startButton);

        startButton.addActionListener(e -> {
            int level = difficultySlider.getValue();
            int moveTime = convertLevelToMoveTime(level);

            boolean playerIsWhite;

            if (whiteButton.isSelected()) {
                playerIsWhite = true;
            } else if (blackButton.isSelected()) {
                playerIsWhite = false;
            } else {
                playerIsWhite = Math.random() < 0.5;
            }

            startAIAction.start(level, moveTime, playerIsWhite);
        });

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 30, 30));
        centerPanel.setBackground(UIStyle.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(70, 140, 110, 140));

        centerPanel.add(levelLabel);
        centerPanel.add(difficultySlider);
        centerPanel.add(colorPanel);
        centerPanel.add(startButton);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createColorPanel() {
        whiteButton = new JRadioButton("♔ Play as White");
        blackButton = new JRadioButton("♚ Play as Black");
        randomButton = new JRadioButton("🎲 Random Color");

        ButtonGroup group = new ButtonGroup();
        group.add(whiteButton);
        group.add(blackButton);
        group.add(randomButton);

        whiteButton.setSelected(true);

        JRadioButton[] buttons = {
                whiteButton,
                blackButton,
                randomButton
        };

        for (JRadioButton button : buttons) {
            button.setFont(UIStyle.titleFont(20));
            button.setBackground(UIStyle.BG_DARK);
            button.setForeground(UIStyle.GOLD_LIGHT);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        JPanel colorPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        colorPanel.setBackground(UIStyle.BG_DARK);
        colorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.GOLD, 2, true),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        colorPanel.add(whiteButton);
        colorPanel.add(blackButton);
        colorPanel.add(randomButton);

        return colorPanel;
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
        void start(int level, int moveTime, boolean playerIsWhite);
    }
}