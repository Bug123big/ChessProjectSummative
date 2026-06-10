package GUI;

import GameRole.GameState;
import GameRole.Player;
import Graphics.ChessBoardStyle;
import Graphics.ChessPieceStyle;
import Graphics.UIStyle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private JComboBox<String> boardThemeBox;
    private JComboBox<String> pieceThemeBox;
    private JTextArea analysisArea;
    private JButton analyzeButton;
    private JButton backButton;
    private JButton resignButton;

    public SidePanel(MainPanel mainPanel, int aiLevel) {
        this.mainPanel = mainPanel;

        setPreferredSize(new Dimension(260, 800));
        setBackground(UIStyle.BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topPanel = new JPanel(new GridLayout(9, 1, 8, 8));
        UIStyle.stylePanel(topPanel);

        turnLabel = new JLabel("Turn: WHITE", SwingConstants.CENTER);
        stateLabel = new JLabel("State: PLAYING", SwingConstants.CENTER);
        thinkingLabel = new JLabel("", SwingConstants.CENTER);

        aiLevelLabel = new JLabel(
                aiLevel > 0 ? "AI Level: " + aiLevel : "Two Player Mode",
                SwingConstants.CENTER);

        UIStyle.styleLabel(turnLabel);
        UIStyle.styleLabel(stateLabel);
        UIStyle.styleLabel(aiLevelLabel);

        thinkingLabel.setForeground(UIStyle.GOLD_LIGHT);
        thinkingLabel.setFont(UIStyle.titleFont(16));

        returnButton = new JButton("Return to Current");
        returnButton.setVisible(false);
        returnButton.addActionListener(e -> mainPanel.returnToCurrentPosition());
        UIStyle.styleButton(returnButton);

        backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> mainPanel.backToMenu());

        resignButton = new JButton("Resign");
        resignButton.addActionListener(e -> mainPanel.resignGame());

        UIStyle.styleButton(backButton);
        UIStyle.styleButton(resignButton);

        boardThemeBox = new JComboBox<>(new String[] {
                "Classic Board", "Dark Board", "Blue Board", "Green Board"
        });

        pieceThemeBox = new JComboBox<>(new String[] {
                "Hollow Pieces", "Solid Pieces"
        });

        styleComboBox(boardThemeBox);
        styleComboBox(pieceThemeBox);

        boardThemeBox.addActionListener(e -> {
            int index = boardThemeBox.getSelectedIndex();

            switch (index) {
                case 1 -> mainPanel.setBoardTheme(ChessBoardStyle.BoardTheme.DARK);
                case 2 -> mainPanel.setBoardTheme(ChessBoardStyle.BoardTheme.BLUE);
                case 3 -> mainPanel.setBoardTheme(ChessBoardStyle.BoardTheme.GREEN);
                default -> mainPanel.setBoardTheme(ChessBoardStyle.BoardTheme.CLASSIC);
            }
        });

        pieceThemeBox.addActionListener(e -> {
            int index = pieceThemeBox.getSelectedIndex();

            switch (index) {
                case 1 -> mainPanel.setPieceTheme(ChessPieceStyle.PieceTheme.SOLID);
                default -> mainPanel.setPieceTheme(ChessPieceStyle.PieceTheme.HOLLOW);
            }
        });

        topPanel.add(turnLabel);
        topPanel.add(stateLabel);
        topPanel.add(aiLevelLabel);
        topPanel.add(thinkingLabel);
        topPanel.add(boardThemeBox);
        topPanel.add(pieceThemeBox);
        topPanel.add(returnButton);
        topPanel.add(backButton);
        topPanel.add(resignButton);

        add(topPanel, BorderLayout.NORTH);

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(UIStyle.PANEL_DARK);

        historyScrollPane = new JScrollPane(historyPanel);
        historyScrollPane.setPreferredSize(new Dimension(240, 160));
        styleScrollPane(historyScrollPane, "Move History");

        analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setLineWrap(true);
        analysisArea.setWrapStyleWord(true);
        analysisArea.setText("Analysis will appear here ...");
        analysisArea.setRows(16);
        analysisArea.setBackground(new Color(20, 17, 14));
        analysisArea.setForeground(UIStyle.TEXT);
        analysisArea.setCaretColor(UIStyle.TEXT);
        analysisArea.setFont(new Font("Serif", Font.PLAIN, 15));
        analysisArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane analysisScrollPane = new JScrollPane(analysisArea);
        styleScrollPane(analysisScrollPane, "Position Analysis");

        analyzeButton = new JButton("Analyze");
        analyzeButton.setFont(new Font("Serif", Font.BOLD, 13));
        analyzeButton.setPreferredSize(new Dimension(100, 30));
        analyzeButton.addActionListener(e -> mainPanel.analyzeCurrentPosition());
        UIStyle.styleButton(analyzeButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        buttonPanel.setBackground(UIStyle.PANEL_DARK);
        buttonPanel.add(analyzeButton);

        JPanel analysisPanel = new JPanel(new BorderLayout(4, 4));
        analysisPanel.setBackground(UIStyle.PANEL_DARK);
        analysisPanel.add(buttonPanel, BorderLayout.NORTH);
        analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(6, 6));
        centerPanel.setBackground(UIStyle.PANEL_DARK);
        centerPanel.add(historyScrollPane, BorderLayout.NORTH);
        centerPanel.add(analysisPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        previousButton = new JButton("◀");
        nextButton = new JButton("▶");

        previousButton.addActionListener(e -> mainPanel.previousMove());
        nextButton.addActionListener(e -> mainPanel.nextMove());

        UIStyle.styleButton(previousButton);
        UIStyle.styleButton(nextButton);

        JPanel navigationPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        navigationPanel.setBackground(UIStyle.BG_DARK);
        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);

        add(navigationPanel, BorderLayout.SOUTH);
    }

    private void styleComboBox(JComboBox<String> box) {

        Color bg = new Color(28, 22, 18);
        Color gold = new Color(220, 180, 100);

        box.setBackground(bg);
        box.setForeground(gold);
        box.setFont(new Font("Serif", Font.BOLD, 14));
        box.setFocusable(false);

        box.setBorder(BorderFactory.createLineBorder(UIStyle.GOLD));

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus);

                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(new Color(70, 55, 35));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(bg);
                    label.setForeground(gold);
                }

                label.setFont(new Font("Serif", Font.BOLD, 14));

                return label;
            }
        });

        box.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {

                JButton button = new JButton("▼");

                button.setBorder(BorderFactory.createEmptyBorder());
                button.setBackground(bg);
                button.setForeground(gold);
                button.setFocusPainted(false);

                return button;
            }
        });
    }

    private void styleScrollPane(JScrollPane scrollPane, String title) {
        scrollPane.getViewport().setBackground(UIStyle.PANEL_DARK);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyle.GOLD, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UIStyle.titleFont(14),
                UIStyle.BG_DARK));
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
        button.setMaximumSize(new Dimension(220, 32));
        button.setPreferredSize(new Dimension(220, 32));
        UIStyle.styleButton(button);

        button.addActionListener(e -> mainPanel.reviewMove(moveNumber));

        historyPanel.add(Box.createVerticalStrut(5));
        historyPanel.add(button);
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    public void setReviewMode(boolean reviewMode) {
        returnButton.setVisible(reviewMode);
    }

    public void setAnalysisText(String text) {
        analysisArea.setText(text);
    }
}