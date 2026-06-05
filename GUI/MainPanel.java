package GUI;

import GameRole.*;
import Graphics.*;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private ChessBoard board;
    private SidePanel sidePanel;
    private GameState gameState;

    public MainPanel(boolean aiMode) {
        board = new ChessBoard();
        sidePanel = new SidePanel();
        gameState = GameState.PLAYING;

        setLayout(new BorderLayout());

        BoardCanvas boardCanvas = new BoardCanvas(board);
        MouseController mouseController = new MouseController(board, boardCanvas, sidePanel);

        boardCanvas.addMouseListener(mouseController);

        add(boardCanvas, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }

    public static class BoardCanvas extends JPanel {

        private ChessBoard board;
        private ChessBoardStyle boardStyle;
        private ChessPieceStyle pieceStyle;

        private int selectedRow = -1;
        private int selectedCol = -1;

        public BoardCanvas(ChessBoard board) {
            this.board = board;
            this.boardStyle = new ChessBoardStyle();
            this.pieceStyle = new ChessPieceStyle();
            setPreferredSize(new Dimension(800, 800));
        }

        public int getTileSize() {
            return Math.min(getWidth(), getHeight()) / 8;
        }

        public void setSelectedSquare(int row, int col) {
            selectedRow = row;
            selectedCol = col;
            repaint();
        }

        public void clearSelectedSquare() {
            selectedRow = -1;
            selectedCol = -1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int tileSize = getTileSize();

            boardStyle.drawBoard(g, tileSize);

            if (selectedRow != -1 && selectedCol != -1) {
                g.setColor(new Color(255, 255, 0, 120));
                g.fillRect(
                        selectedCol * tileSize,
                        selectedRow * tileSize,
                        tileSize,
                        tileSize);
            }

            pieceStyle.drawPieces(g, board, tileSize);
        }
    }
}
