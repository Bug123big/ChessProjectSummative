import GUI.*;
import Graphics.DialogStyle;

import javax.swing.*;

public class Main {

    private JFrame frame;

    public Main() {
        frame = new JFrame("Chess Game");
        frame.setSize(1000, 850);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        showCoverPanel();

        frame.setVisible(true);
    }

    private void showCoverPanel() {
        frame.setContentPane(new CoverPanel(() -> showSelectPanel()));
        frame.revalidate();
    }

    private void showSelectPanel() {
        frame.setContentPane(new SelectPanel(
                () -> startGame(false, 0, 0, true, false),
                () -> showDifficultyPanel(false),
                () -> startGame(false, 0, 0, true, true),
                () -> showDifficultyPanel(true)));

        frame.revalidate();
    }

    private void showDifficultyPanel(boolean chess960Mode) {
        frame.setContentPane(new DifficultyPanel(
        (level, moveTime, playerIsWhite) ->
        startGame(true, level, moveTime, playerIsWhite, chess960Mode),() -> showSelectPanel()));

        frame.revalidate();
    }

    private void startGame(boolean aiMode, int aiLevel, int aiMoveTime, boolean playerIsWhite, boolean chess960Mode) {
        frame.setContentPane(new MainPanel
            (aiMode, aiLevel, aiMoveTime, playerIsWhite, chess960Mode, () -> showSelectPanel()));

        frame.revalidate();
    }

    public static void main(String[] args) {

        new Main();
    }
}
