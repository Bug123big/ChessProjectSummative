package GUI;

import GameRole.GameState;
import GameRole.Player;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    private JLabel turnLabel;
    private JLabel stateLabel;
    private JLabel thinkingLabel;
    private JPanel historyPanel;
    private JScrollPane historyScrollPane;
    private JButton returnButton;
    private MainPanel mainPanel;
    private JButton previousButton;
    private JButton nextButton;
    private JLabel aiLevelLabel;

    public SidePanel(MainPanel mainPanel, int aiLevel) {
        this.mainPanel = mainPanel;

        setPreferredSize(new Dimension(260, 800));
        setBackground(new Color(45, 45, 45));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        topPanel.setBackground(new Color(45, 45, 45));

        turnLabel = new JLabel("Turn: WHITE", SwingConstants.CENTER);
        stateLabel = new JLabel("State: PLAYING", SwingConstants.CENTER);
        thinkingLabel = new JLabel("", SwingConstants.CENTER);

        aiLevelLabel = new JLabel(
                aiLevel > 0 ? "AI Level: " + aiLevel : "Two Player Mode",
                SwingConstants.CENTER
        );

        returnButton = new JButton("Return to Current");
        returnButton.setVisible(false);
        returnButton.addActionListener(e -> mainPanel.returnToCurrentPosition());

        turnLabel.setForeground(Color.WHITE);
        stateLabel.setForeground(Color.WHITE);
        thinkingLabel.setForeground(Color.YELLOW);
        aiLevelLabel.setForeground(Color.WHITE);

        topPanel.add(turnLabel);
        topPanel.add(stateLabel);
        topPanel.add(aiLevelLabel);
        topPanel.add(thinkingLabel);
        topPanel.add(returnButton);

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

        historyScrollPane = new JScrollPane(historyPanel);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Move History"));

        add(topPanel, BorderLayout.NORTH);
        add(historyScrollPane, BorderLayout.CENTER);

        previousButton = new JButton("◀");
        nextButton = new JButton("▶");

        previousButton.addActionListener(e -> mainPanel.previousMove());
        nextButton.addActionListener(e -> mainPanel.nextMove());

        JPanel navigationPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);

        add(navigationPanel, BorderLayout.SOUTH);
    }

    public void updateInfo(Player currentPlayer, GameState state) {
        turnLabel.setText("Turn: " + currentPlayer);
        stateLabel.setText("State: " + state);
    }

    public void setThinking(boolean thinking) {
        thinkingLabel.setText(thinking ? "AI Thinking..." : "");
    }

    public void addMoveButton(int moveNumber, String notation) {
        JButton button = new JButton(moveNumber + ". " + notation);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 35));

        button.addActionListener(e -> mainPanel.reviewMove(moveNumber));

        historyPanel.add(button);
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    public void setReviewMode(boolean reviewMode) {
        returnButton.setVisible(reviewMode);
    }
}
