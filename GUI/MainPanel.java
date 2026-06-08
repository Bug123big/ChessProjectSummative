package GUI;

import AI.ChessAI;
import GameRole.*;
import Graphics.*;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private ChessBoard board;
    private SidePanel sidePanel;
    private GameState gameState;
    private boolean aiMode;
    private ChessAI chessAI;
    private BoardCanvas boardCanvas;
    private java.util.ArrayList<ChessPiece[][]> boardHistory = new java.util.ArrayList<>();
    private java.util.ArrayList<String> moveHistory = new java.util.ArrayList<>();
    private boolean reviewMode = false;
    private ChessPiece[][] currentBoardSnapshot;
    private int reviewIndex = -1;
    private int aiLevel;
    private int aiMoveTime;

    public MainPanel(boolean aiMode, int aiLevel, int aiMoveTime) {
        this.aiMode = aiMode;
        this.aiLevel = aiLevel;
        this.aiMoveTime = aiMoveTime;

        board = new ChessBoard();
        sidePanel = new SidePanel(this, aiLevel);
        gameState = GameState.PLAYING;
        boardHistory.add(board.copyBoardArray());

        setLayout(new BorderLayout());

        boardCanvas = new BoardCanvas(board);
        MouseController mouseController = new MouseController(board, boardCanvas, sidePanel, this);

        boardCanvas.addMouseListener(mouseController);

        add(boardCanvas, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        if (aiMode) {
            try {
                chessAI = new ChessAI(
                        "Engine/stockfish-macos-m1-apple-silicon");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Stockfish failed to start.");
            }
        }
    }

    public static class BoardCanvas extends JPanel {

        private ChessBoard board;
        private ChessBoardStyle boardStyle;
        private ChessPieceStyle pieceStyle;

        private int selectedRow = -1;
        private int selectedCol = -1;

        private final int margin = 40;

        public BoardCanvas(ChessBoard board) {
            this.board = board;
            this.boardStyle = new ChessBoardStyle();
            this.pieceStyle = new ChessPieceStyle();
            setPreferredSize(new Dimension(900, 900));
        }

        public int getMargin() {
            return margin;
        }

        public int getTileSize() {
            return (Math.min(getWidth(), getHeight()) - margin * 2) / 8;
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

            boardStyle.drawBoard(g, tileSize, margin);

            Move lastMove = board.getLastExecutedMove();
            if (lastMove != null) {
                g.setColor(new Color(80, 200, 120, 120));

                g.fillRect(
                        margin + lastMove.getFromCol() * tileSize,
                        margin + lastMove.getFromRow() * tileSize,
                        tileSize,
                        tileSize);

                g.fillRect(
                        margin + lastMove.getToCol() * tileSize,
                        margin + lastMove.getToRow() * tileSize,
                        tileSize,
                        tileSize);
            }

            if (selectedRow != -1 && selectedCol != -1) {
                g.setColor(new Color(255, 255, 0, 120));
                g.fillRect(
                        margin + selectedCol * tileSize,
                        margin + selectedRow * tileSize,
                        tileSize,
                        tileSize);
            }

            Player current = board.getCurrentPlayer();

            if (board.isKingInCheck(current)) {
                int[] kingPos = board.findKing(current);

                if (kingPos != null) {
                    g.setColor(new Color(255, 0, 0, 130));
                    g.fillRect(
                            margin + kingPos[1] * tileSize,
                            margin + kingPos[0] * tileSize,
                            tileSize,
                            tileSize);
                }
            }

            pieceStyle.drawPieces(g, board, tileSize, margin);

            drawCoordinates(g, tileSize);
        }

        private void drawCoordinates(Graphics g, int tileSize) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            for (int col = 0; col < 8; col++) {
                char file = (char) ('a' + col);

                g.drawString(
                        String.valueOf(file),
                        margin + col * tileSize + tileSize / 2,
                        margin + 8 * tileSize + 25);
            }

            for (int row = 0; row < 8; row++) {
                int rank = 8 - row;

                g.drawString(
                        String.valueOf(rank),
                        margin - 25,
                        margin + row * tileSize + tileSize / 2);
            }
        }
    }

    public void makeAIMoveIfNeeded() {
        if (!aiMode)
            return;
        if (board.isGameOver())
            return;
        if (board.getCurrentPlayer() != Player.BLACK)
            return;
        if (chessAI == null)
            return;

        sidePanel.setThinking(true);

        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override
            protected Move doInBackground() throws Exception {
                String fen = board.toFEN();
                return chessAI.getBestMove(fen, aiMoveTime);
            }

            @Override
            protected void done() {
                try {
                    Move aiMove = get();

                    if (aiMove != null && !board.isGameOver()) {
                        board.makeMove(aiMove);
                        recordMove(aiMove);
                        updateGameStatus();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sidePanel.setThinking(false);
                    boardCanvas.repaint();
                }
            }
        };

        worker.execute();
    }

    public void updateGameStatus() {
        Player nextPlayer = board.getCurrentPlayer();

        if (board.isCheckmate(nextPlayer)) {
            board.setGameOver(true);
            sidePanel.updateInfo(nextPlayer, GameState.CHECKMATE);

            JOptionPane.showMessageDialog(
                    this,
                    nextPlayer + " is checkmated! " + nextPlayer.opposite() + " wins!",
                    "Checkmate",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (board.isStalemate(nextPlayer)) {
            board.setGameOver(true);
            sidePanel.updateInfo(nextPlayer, GameState.STALEMATE);

            JOptionPane.showMessageDialog(
                    this,
                    "Stalemate! The game is a draw.",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (board.isKingInCheck(nextPlayer)) {
            sidePanel.updateInfo(nextPlayer, GameState.CHECK);

            JOptionPane.showMessageDialog(
                    this,
                    nextPlayer + " king is in check!",
                    "Check",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            sidePanel.updateInfo(nextPlayer, GameState.PLAYING);
        }
    }

    public boolean isReviewMode() {
        return reviewMode;
    }

    public void recordMove(Move move) {
        moveHistory.add(move.toNotation());
        boardHistory.add(board.copyBoardArray());

        sidePanel.addMoveButton(moveHistory.size(), move.toNotation());
    }

    public void reviewMove(int index) {
        if (!reviewMode) {
            currentBoardSnapshot = board.copyBoardArray();
        }

        reviewMode = true;
        reviewIndex = index;

        board.loadBoardArray(boardHistory.get(index));

        sidePanel.setReviewMode(true);

        boardCanvas.repaint();
    }

    public void previousMove() {

        if (!reviewMode) {
            reviewMove(boardHistory.size() - 1);
            return;
        }

        if (reviewIndex > 0) {
            reviewIndex--;

            board.loadBoardArray(
                    boardHistory.get(reviewIndex));

            boardCanvas.repaint();
        }
    }

    public void nextMove() {

        if (!reviewMode)
            return;

        if (reviewIndex < boardHistory.size() - 1) {

            reviewIndex++;

            board.loadBoardArray(boardHistory.get(reviewIndex));

            boardCanvas.repaint();
        } else {

            returnToCurrentPosition();
        }
    }

    public void returnToCurrentPosition() {
        reviewMode = false;

        if (currentBoardSnapshot != null) {
            board.loadBoardArray(currentBoardSnapshot);
        }

        sidePanel.setReviewMode(false);
        boardCanvas.repaint();
    }
}
