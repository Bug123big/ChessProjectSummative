package GUI;

import GameRole.*;

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
        if (!mainPanel.isPlayerTurn()) {
            return;
        }
        if (board.isGameOver())
            return;
        if (mainPanel.isReviewMode())
            return;

        int[] square = getSquareFromMouse(e);

        if (square == null) {
            clearSelection();
            return;
        }

        int row = square[0];
        int col = square[1];

        ChessPiece clickedPiece = board.getPiece(row, col);

        if (clickedPiece != null && clickedPiece.getOwner() == board.getCurrentPlayer()) {
            selectedRow = row;
            selectedCol = col;

            boardPanel.setSelectedSquare(row, col);
            boardPanel.startDragging(clickedPiece, row, col, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!mainPanel.isPlayerTurn()) {
            return;
        }
        if (board.isGameOver())
            return;
        if (mainPanel.isReviewMode())
            return;

        if (selectedRow != -1) {
            boardPanel.updateDragging(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!mainPanel.isPlayerTurn()) {
            return;
        }
        if (board.isGameOver())
            return;
        if (mainPanel.isReviewMode())
            return;

        if (selectedRow == -1) {
            return;
        }

        int[] square = getSquareFromMouse(e);

        boardPanel.stopDragging();

        if (square == null) {
            clearSelection();
            return;
        }

        int row = square[0];
        int col = square[1];

        Move move = new Move(selectedRow, selectedCol, row, col);
        String notation = ChessNotation.getSAN(board, move);
        boolean success = board.makeMove(move);

        clearSelection();

        if (success) {
            mainPanel.recordMove(move, notation);
            mainPanel.updateGameStatus();
            boardPanel.repaint();
            mainPanel.makeAIMoveIfNeeded();
        } else {
            boardPanel.repaint();
        }
    }

    private int[] getSquareFromMouse(MouseEvent e) {
        int tileSize = boardPanel.getTileSize();
        int margin = boardPanel.getMargin();

        int x = e.getX();
        int y = e.getY();

        if (x < margin || y < margin) {
            return null;
        }

        int displayCol = (x - margin) / tileSize;
        int displayRow = (y - margin) / tileSize;

        if (displayRow < 0 || displayRow >= 8 || displayCol < 0 || displayCol >= 8) {
            return null;
        }

        int row;
        int col;

        if (boardPanel.isFlipped()) {
            row = 7 - displayRow;
            col = 7 - displayCol;
        } else {
            row = displayRow;
            col = displayCol;
        }

        if (!board.isInsideBoard(row, col)) {
            return null;
        }

        return new int[] { row, col };
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        boardPanel.clearSelectedSquare();
    }
}