package GUI;

import GameRole.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter {

    private ChessBoard board;
    private MainPanel.BoardCanvas boardPanel;
    private SidePanel sidePanel;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public MouseController(ChessBoard board, MainPanel.BoardCanvas boardPanel, SidePanel sidePanel) {
        this.board = board;
        this.boardPanel = boardPanel;
        this.sidePanel = sidePanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int tileSize = boardPanel.getTileSize();

        int col = e.getX() / tileSize;
        int row = e.getY() / tileSize;

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

        // 如果第二次又点了自己的棋子，改为选择新棋子
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
            Player nextPlayer = board.getCurrentPlayer();

            if (board.isCheckmate(nextPlayer)) {
                sidePanel.updateInfo(nextPlayer, GameState.CHECKMATE);
                boardPanel.repaint();

                JOptionPane.showMessageDialog(
                        boardPanel,
                        nextPlayer + " is checkmated! " + nextPlayer.opposite() + " wins!",
                        "Checkmate",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (board.isStalemate(nextPlayer)) {
                sidePanel.updateInfo(nextPlayer, GameState.STALEMATE);
                boardPanel.repaint();

                JOptionPane.showMessageDialog(
                        boardPanel,
                        "Stalemate! The game is a draw.",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (board.isKingInCheck(nextPlayer)) {
                sidePanel.updateInfo(nextPlayer, GameState.CHECK);

                JOptionPane.showMessageDialog(
                        boardPanel,
                        nextPlayer + " king is in check!",
                        "Check",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                sidePanel.updateInfo(nextPlayer, GameState.PLAYING);
            }
        }

        boardPanel.repaint();
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        boardPanel.clearSelectedSquare();
    }
}