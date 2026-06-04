import GUI.CoverPanel;
import GUI.SelectPanel;
import GUI.MainPanel;

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
                () -> startGame(false),
                () -> startGame(true)
        ));
        frame.revalidate();
    }

    private void startGame(boolean aiMode) {
        frame.setContentPane(new MainPanel(aiMode));
        frame.revalidate();
    }

    public static void main(String[] args) {
        new Main();
    }
}
