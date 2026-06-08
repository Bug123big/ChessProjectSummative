package GUI;

import GameRole.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter {

    private ChessBoard board;
    private MainPanel.BoardCanvas boardPanel;
    private SidePanel sidePanel;
    private MainPanel mainPanel;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public MouseController(
            ChessBoard board,
            MainPanel.BoardCanvas boardPanel,
            SidePanel sidePanel,
            MainPanel mainPanel) {
        this.board = board;
        this.boardPanel = boardPanel;
        this.sidePanel = sidePanel;
        this.mainPanel = mainPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mainPanel.isReviewMode()) {
            return;
        }
        if (board.isGameOver()) {
            return;
        }
        int tileSize = boardPanel.getTileSize();
        int margin = boardPanel.getMargin();

        int col = (e.getX() - margin) / tileSize;
        int row = (e.getY() - margin) / tileSize;

        if (e.getX() < margin || e.getY() < margin) {
            clearSelection();
            return;
        }

        if (!board.isInsideBoard(row, col)) {
            clearSelection();
            return;
        }

        ChessPiece clickedPiece = board.getPiece(row, col);

        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.getOwner() == board.getCurrentPlayer()) {
                selectedRow = row;
                selectedCol = col;
                boardPanel.setSelectedSquare(row, col);
            }
            return;
        }

        if (clickedPiece != null && clickedPiece.getOwner() == board.getCurrentPlayer()) {
            selectedRow = row;
            selectedCol = col;
            boardPanel.setSelectedSquare(row, col);
            return;
        }

        Move move = new Move(selectedRow, selectedCol, row, col);
        boolean success = board.makeMove(move);

        clearSelection();

        if (success) {
            mainPanel.recordMove(move);
            mainPanel.updateGameStatus();
            boardPanel.repaint();
            mainPanel.makeAIMoveIfNeeded();
        }

        boardPanel.repaint();
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        boardPanel.clearSelectedSquare();
    }
}