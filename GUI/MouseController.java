package GUI;

import GameRole.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter {

    private ChessBoard board;
    private JPanel boardPanel;
    private SidePanel sidePanel;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public MouseController(ChessBoard board, JPanel boardPanel, SidePanel sidePanel) {
        this.board = board;
        this.boardPanel = boardPanel;
        this.sidePanel = sidePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int tileSize = Math.min(boardPanel.getWidth(), boardPanel.getHeight()) / 8;

        int col = e.getX() / tileSize;
        int row = e.getY() / tileSize;

        if (!board.isInsideBoard(row, col)) return;

        if (selectedRow == -1) {
            ChessPiece piece = board.getPiece(row, col);

            if (piece != null && piece.getOwner() == board.getCurrentPlayer()) {
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            Move move = new Move(selectedRow, selectedCol, row, col);
            board.makeMove(move);

            selectedRow = -1;
            selectedCol = -1;

            sidePanel.updateInfo(board.getCurrentPlayer(), GameState.PLAYING);
            boardPanel.repaint();
        }
    }
}
