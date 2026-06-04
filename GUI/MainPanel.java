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

    private static class BoardCanvas extends JPanel {

        private ChessBoard board;
        private ChessBoardStyle boardStyle;
        private ChessPieceStyle pieceStyle;

        public BoardCanvas(ChessBoard board) {
            this.board = board;
            this.boardStyle = new ChessBoardStyle();
            this.pieceStyle = new ChessPieceStyle();
            setPreferredSize(new Dimension(800, 800));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int tileSize = Math.min(getWidth(), getHeight()) / 8;

            boardStyle.drawBoard(g, tileSize);
            pieceStyle.drawPieces(g, board, tileSize);
        }
    }
}
