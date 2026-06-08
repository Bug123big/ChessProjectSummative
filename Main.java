import GUI.*;

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
                () -> startGame(false, 0, 0),
                () -> showDifficultyPanel()));
        frame.revalidate();
    }

    private void showDifficultyPanel() {
        frame.setContentPane(new DifficultyPanel(
                (level, moveTime) -> startGame(true, level, moveTime)));
        frame.revalidate();
    }

    private void startGame(boolean aiMode, int aiLevel, int aiMoveTime) {
        frame.setContentPane(new MainPanel(aiMode, aiLevel, aiMoveTime));
        frame.revalidate();
    }

    public static void main(String[] args) {
        new Main();
    }
}
