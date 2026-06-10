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
    private boolean playerIsWhite;
    private Runnable backToMenuAction;

    public MainPanel(boolean aiMode, int aiLevel, int aiMoveTime, boolean playerIsWhite, Runnable backToMenuAction) {
        this.aiMode = aiMode;
        this.aiLevel = aiLevel;
        this.aiMoveTime = aiMoveTime;
        this.playerIsWhite = playerIsWhite;
        this.backToMenuAction = backToMenuAction;

        board = new ChessBoard();
        sidePanel = new SidePanel(this, aiLevel);
        gameState = GameState.PLAYING;
        boardHistory.add(board.copyBoardArray());

        setLayout(new BorderLayout());

        boardCanvas = new BoardCanvas(board);
        boardCanvas.setFlipped(aiMode && !playerIsWhite);

        boardCanvas.setBoardTheme(ChessBoardStyle.BoardTheme.CLASSIC);
        boardCanvas.setPieceTheme(ChessPieceStyle.PieceTheme.HOLLOW);

        MouseController mouseController = new MouseController(board, boardCanvas, sidePanel, this);

        boardCanvas.addMouseListener(mouseController);
        boardCanvas.addMouseMotionListener(mouseController);

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

            if (!playerIsWhite) {
                makeAIMoveIfNeeded();
            }
        }
    }

    public static class BoardCanvas extends JPanel {

        private ChessBoard board;
        private ChessBoardStyle boardStyle;
        private ChessPieceStyle pieceStyle;
        private boolean flipped = false;

        private int selectedRow = -1;
        private int selectedCol = -1;

        private java.util.ArrayList<Move> legalMoves = new java.util.ArrayList<>();

        private boolean dragging = false;
        private ChessPiece draggingPiece;
        private int dragRow = -1;
        private int dragCol = -1;
        private int mouseX;
        private int mouseY;

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
            legalMoves = board.getLegalMovesForPiece(row, col);
            repaint();
        }

        public void clearSelectedSquare() {
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();
            repaint();
        }

        public void startDragging(ChessPiece piece, int row, int col, int x, int y) {
            dragging = true;
            draggingPiece = piece;
            dragRow = row;
            dragCol = col;
            mouseX = x;
            mouseY = y;
            repaint();
        }

        public void updateDragging(int x, int y) {
            mouseX = x;
            mouseY = y;
            repaint();
        }

        public void stopDragging() {
            dragging = false;
            draggingPiece = null;
            dragRow = -1;
            dragCol = -1;
            repaint();
        }

        public boolean isDraggingPieceAt(int row, int col) {
            return dragging && dragRow == row && dragCol == col;
        }

        public void setBoardTheme(ChessBoardStyle.BoardTheme theme) {
            boardStyle.setTheme(theme);
            repaint();
        }

        public void setPieceTheme(ChessPieceStyle.PieceTheme theme) {
            pieceStyle.setTheme(theme);
            repaint();
        }

        public void setFlipped(boolean flipped) {
            this.flipped = flipped;
            repaint();
        }

        public boolean isFlipped() {
            return flipped;
        }

        public int displayRow(int boardRow) {
            return flipped ? 7 - boardRow : boardRow;
        }

        public int displayCol(int boardCol) {
            return flipped ? 7 - boardCol : boardCol;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int tileSize = getTileSize();

            boardStyle.drawBoard(g, tileSize, margin);

            Move lastMove = board.getLastExecutedMove();

            if (lastMove != null) {
                g.setColor(new Color(80, 200, 120, 120));

                int fromDisplayRow = displayRow(lastMove.getFromRow());
                int fromDisplayCol = displayCol(lastMove.getFromCol());

                int toDisplayRow = displayRow(lastMove.getToRow());
                int toDisplayCol = displayCol(lastMove.getToCol());

                g.fillRect(
                        margin + fromDisplayCol * tileSize,
                        margin + fromDisplayRow * tileSize,
                        tileSize,
                        tileSize);

                g.fillRect(
                        margin + toDisplayCol * tileSize,
                        margin + toDisplayRow * tileSize,
                        tileSize,
                        tileSize);
            }

            if (selectedRow != -1 && selectedCol != -1) {
                int displayRow = displayRow(selectedRow);
                int displayCol = displayCol(selectedCol);

                g.setColor(new Color(255, 255, 0, 120));
                g.fillRect(
                        margin + displayCol * tileSize,
                        margin + displayRow * tileSize,
                        tileSize,
                        tileSize);
            }

            drawLegalMoveDots(g, tileSize);

            Player current = board.getCurrentPlayer();

            if (board.isKingInCheck(current)) {
                int[] kingPos = board.findKing(current);

                if (kingPos != null) {
                    int displayRow = displayRow(kingPos[0]);
                    int displayCol = displayCol(kingPos[1]);

                    g.setColor(new Color(255, 0, 0, 130));
                    g.fillRect(
                            margin + displayCol * tileSize,
                            margin + displayRow * tileSize,
                            tileSize,
                            tileSize);
                }
            }
            pieceStyle.drawPieces(g, board, tileSize, margin, this);

            if (dragging && draggingPiece != null) {
                pieceStyle.drawSinglePieceAtMouse(
                        g,
                        draggingPiece,
                        mouseX,
                        mouseY,
                        tileSize);
            }

            drawCoordinates(g, tileSize);
        }

        private void drawLegalMoveDots(Graphics g, int tileSize) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(new Color(80, 80, 80, 120));

            for (Move move : legalMoves) {
                int displayRow = displayRow(move.getToRow());
                int displayCol = displayCol(move.getToCol());

                int centerX = margin + displayCol * tileSize + tileSize / 2;
                int centerY = margin + displayRow * tileSize + tileSize / 2;

                int dotSize = tileSize / 4;

                g2.fillOval(
                        centerX - dotSize / 2,
                        centerY - dotSize / 2,
                        dotSize,
                        dotSize);
            }
        }

        private void drawCoordinates(Graphics g, int tileSize) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setFont(new Font("Serif", Font.BOLD, 14));

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int boardCol = flipped ? 7 - displayCol : displayCol;
                char file = (char) ('a' + boardCol);

                int rowForFile = 7;
                int boardRow = flipped ? 7 - rowForFile : rowForFile;

                boolean lightSquare = (boardRow + boardCol) % 2 == 0;

                g2.setColor(lightSquare
                        ? new Color(55, 38, 25, 180)
                        : new Color(220, 190, 130, 180));

                g2.drawString(
                        String.valueOf(file),
                        margin + displayCol * tileSize + tileSize - 16,
                        margin + 8 * tileSize - 6);
            }

            for (int displayRow = 0; displayRow < 8; displayRow++) {
                int boardRow = flipped ? 7 - displayRow : displayRow;
                int rank = 8 - boardRow;

                int colForRank = 0;
                int boardCol = flipped ? 7 - colForRank : colForRank;

                boolean lightSquare = (boardRow + boardCol) % 2 == 0;

                g2.setColor(lightSquare
                        ? new Color(40, 25, 15, 220)
                        : new Color(245, 220, 150, 220));

                g2.drawString(
                        String.valueOf(rank),
                        margin + 5,
                        margin + displayRow * tileSize + 16);
            }
        }
    }

    public void makeAIMoveIfNeeded() {
        if (!aiMode)
            return;
        if (board.isGameOver())
            return;
        Player aiPlayer = playerIsWhite ? Player.BLACK : Player.WHITE;
        if (board.getCurrentPlayer() != aiPlayer)
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
                        String notation = ChessNotation.getSAN(board, aiMove);

                        board.makeMove(aiMove);
                        recordMove(aiMove, notation);
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

    public void recordMove(Move move, String notation) {
        moveHistory.add(notation);
        boardHistory.add(board.copyBoardArray());

        sidePanel.addMoveButton(moveHistory.size(), notation);
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

    public void setBoardTheme(ChessBoardStyle.BoardTheme theme) {
        boardCanvas.setBoardTheme(theme);
    }

    public void setPieceTheme(ChessPieceStyle.PieceTheme theme) {
        boardCanvas.setPieceTheme(theme);
    }

    public void analyzeCurrentPosition() {
        if (chessAI == null) {
            sidePanel.setAnalysisText("Analysis is only available in AI mode.");
            return;
        }

        sidePanel.setAnalysisText("Analyzing position...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String fen = board.toFEN();
                return chessAI.analyzePosition(fen, 5000);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    sidePanel.setAnalysisText(convertAnalysisText(result));
                } catch (Exception e) {
                    sidePanel.setAnalysisText("Analysis failed.");
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    public boolean isPlayerTurn() {
        if (!aiMode) {
            return true;
        }

        Player humanPlayer = playerIsWhite ? Player.WHITE : Player.BLACK;

        return board.getCurrentPlayer() == humanPlayer;
    }

    public void backToMenu() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Return to menu? Current game will be lost.",
                "Back to Menu",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (backToMenuAction != null) {
                backToMenuAction.run();
            }
        }
    }

    public void resignGame() {
        if (board.isGameOver()) {
            return;
        }

        Player loser = board.getCurrentPlayer();
        Player winner = loser.opposite();

        int result = JOptionPane.showConfirmDialog(
                this,
                loser + " resigns?",
                "Resign",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            board.setGameOver(true);
            sidePanel.updateInfo(winner, GameState.GAME_OVER);

            JOptionPane.showMessageDialog(
                    this,
                    loser + " resigned. " + winner + " wins!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String squareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    private String convertAnalysisText(String text) {

        String[] lines = text.split("\n");

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {

            if (line.startsWith("Best Move: ")) {

                String uci = line.replace("Best Move: ", "").trim();

                sb.append("Best Move: ")
                        .append(convertUciToNotation(uci))
                        .append("\n");

            } else {

                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }

    private String convertUciToNotation(String uci) {
        if (uci == null || uci.length() < 4) {
            return uci;
        }

        return ChessNotation.getNotationFromUci(board, uci);
    }
}
