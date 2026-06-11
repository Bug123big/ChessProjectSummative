import AI.ChessAI;
import GameRole.*;

public class TestProgram {

    private static final String STOCKFISH_PATH = "Engine/stockfish-macos-m1-apple-silicon";

    public static void main(String[] args) throws Exception{
        System.out.println("===== Chess Program Test Start =====");

        // testInitialFEN();
        // testSAN();
        // testClassicalCastling();
        // testChess960Generation();
        // testAIOnce();
        // testAIStressGames(50);

        testInitialFEN();

        testNotation();

        testCastlingNotation();

        testChess960Rules();

        testPromotion();

        testFENGeneration();

        testAISpeed();

        testAIStressGames(50);

        testChess960AI();

        System.out.println("===== Chess Program Test Finished =====");
    }

    private static void testInitialFEN() {
        System.out.println("\n===== TEST: Initial FEN =====");

        ChessBoard board = new ChessBoard();

        System.out.println(board.toFEN());
    }

    private static void testSAN() {
        System.out.println("\n===== TEST: SAN =====");

        ChessBoard board = new ChessBoard();

        Move nf3 = new Move(7, 6, 5, 5);

        System.out.println("Expected: Nf3");
        System.out.println("Actual:   " + ChessNotation.getSAN(board, nf3));
    }

    private static void testClassicalCastling() {
        System.out.println("\n===== TEST: Classical Castling =====");

        ChessBoard board = new ChessBoard();

        board.makeMove(new Move(6, 4, 4, 4)); // e4
        board.makeMove(new Move(1, 4, 3, 4)); // e5
        board.makeMove(new Move(7, 6, 5, 5)); // Nf3
        board.makeMove(new Move(0, 1, 2, 2)); // Nc6
        board.makeMove(new Move(7, 5, 4, 2)); // Bc4
        board.makeMove(new Move(0, 6, 2, 5)); // Nf6

        Move castle = new Move(7, 4, 7, 6);

        System.out.println("Expected SAN: O-O");
        System.out.println("Actual SAN:   " + ChessNotation.getSAN(board, castle));

        boolean success = board.makeMove(castle);
        System.out.println("Castling success: " + success);
        System.out.println("FEN after castling:");
        System.out.println(board.toFEN());
    }

    private static void testChess960Generation() {
        System.out.println("\n===== TEST: Chess960 Generation =====");

        for (int i = 1; i <= 10; i++) {
            ChessBoard board = new ChessBoard();
            board.setupChess960();

            String fen = board.toFEN();

            System.out.println("Chess960 position " + i + ":");
            System.out.println(fen);
        }
    }

    private static void testAIOnce() {
        System.out.println("\n===== TEST: AI Once =====");

        try {
            ChessAI ai = new ChessAI(STOCKFISH_PATH);
            ChessBoard board = new ChessBoard();

            long start = System.currentTimeMillis();

            Move move = ai.getBestMove(board.toFEN(), 200);

            long end = System.currentTimeMillis();

            if (move == null) {
                System.out.println("FAIL: AI returned null.");
            } else {
                System.out.println("PASS: AI move = " + move.toNotation());
                System.out.println("SAN = " + ChessNotation.getSAN(board, move));
            }

            System.out.println("Thinking time = " + (end - start) + " ms");

            ai.close();

        } catch (Exception e) {
            System.out.println("FAIL: AI crashed.");
            e.printStackTrace();
        }
    }

    private static void testAIStressGames(int numberOfGames) {
        System.out.println("\n===== TEST: AI Stress Games =====");

        try {
            ChessAI ai = new ChessAI(STOCKFISH_PATH);

            for (int game = 1; game <= numberOfGames; game++) {
                System.out.println("\n===== GAME " + game + " =====");

                ChessBoard board = new ChessBoard();

                for (int moveCount = 1; moveCount <= 500; moveCount++) {

                    Player current = board.getCurrentPlayer();

                    if (board.isCheckmate(current)) {
                        System.out.println("CHECKMATE at game "
                                + game + " move " + moveCount);
                        break;
                    }

                    if (board.isStalemate(current)) {
                        System.out.println("STALEMATE at game "
                                + game + " move " + moveCount);
                        break;
                    }

                    String fen = board.toFEN();

                    long start = System.currentTimeMillis();

                    Move move = ai.getBestMove(fen, 100);

                    long end = System.currentTimeMillis();

                    if (move == null) {
                        System.out.println("NULL MOVE at game "
                                + game + " move " + moveCount);

                        System.out.println("Current player: " + current);
                        System.out.println("FEN: " + fen);
                        System.out.println("Check: " + board.isKingInCheck(current));
                        System.out.println("Checkmate: " + board.isCheckmate(current));
                        System.out.println("Stalemate: " + board.isStalemate(current));

                        ai.close();
                        return;
                    }

                    boolean success = board.makeMove(move);

                    if (!success) {
                        System.out.println("ILLEGAL MOVE at game "
                                + game + " move " + moveCount);

                        System.out.println("Move: " + move.toNotation());
                        System.out.println("FEN: " + fen);

                        ai.close();
                        return;
                    }

                    if (end - start > 3000) {
                        System.out.println("WARNING: Slow AI move at game "
                                + game + " move " + moveCount);

                        System.out.println("Thinking time = "
                                + (end - start) + " ms");

                        System.out.println("FEN: " + fen);
                    }
                }
            }

            ai.close();

            System.out.println("\nPASS: Stress test finished.");

        } catch (Exception e) {
            System.out.println("FAIL: Stress test crashed.");
            e.printStackTrace();
        }
    }

    private static void testChess960Rules() {

        System.out.println("\n===== TEST: Chess960 Rules =====");

        for (int i = 1; i <= 100; i++) {

            ChessBoard board = new ChessBoard();
            board.setupChess960();

            int kingCol = -1;
            int rook1 = -1;
            int rook2 = -1;

            int bishop1 = -1;
            int bishop2 = -1;

            for (int col = 0; col < 8; col++) {

                ChessPiece piece = board.getPiece(7, col);

                if (piece == null) {
                    continue;
                }

                switch (piece.getType()) {

                    case KING -> kingCol = col;

                    case ROOK -> {
                        if (rook1 == -1) {
                            rook1 = col;
                        } else {
                            rook2 = col;
                        }
                    }

                    case BISHOP -> {
                        if (bishop1 == -1) {
                            bishop1 = col;
                        } else {
                            bishop2 = col;
                        }
                    }
                }
            }

            if (!(rook1 < kingCol && kingCol < rook2)) {
                System.out.println("FAIL: King not between rooks");
                return;
            }

            if ((bishop1 % 2) == (bishop2 % 2)) {
                System.out.println("FAIL: Bishops same color");
                return;
            }
        }

        System.out.println("PASS");
    }

    private static void testCastlingNotation() {

        System.out.println("\n===== TEST: Castling SAN =====");

        ChessBoard board = new ChessBoard();

        board.makeMove(new Move(6, 4, 4, 4));
        board.makeMove(new Move(1, 4, 3, 4));

        board.makeMove(new Move(7, 6, 5, 5));
        board.makeMove(new Move(0, 1, 2, 2));

        board.makeMove(new Move(7, 5, 4, 2));
        board.makeMove(new Move(0, 6, 2, 5));

        Move castle = new Move(7, 4, 7, 6);

        String san = ChessNotation.getSAN(board, castle);

        System.out.println("Expected: O-O");
        System.out.println("Actual: " + san);
    }

    private static void testNotation() {

        System.out.println("\n===== TEST: SAN =====");

        ChessBoard board = new ChessBoard();

        Move move = new Move(7, 6, 5, 5);

        String san = ChessNotation.getSAN(board, move);

        System.out.println("Expected: Nf3");
        System.out.println("Actual: " + san);
    }

    private static void testFENGeneration() {

        System.out.println("\n===== TEST: FEN =====");

        for (int i = 0; i < 100; i++) {

            ChessBoard board = new ChessBoard();

            board.setupChess960();

            String fen = board.toFEN();

            if (fen == null
                    || fen.isEmpty()) {

                System.out.println("FAIL");
                return;
            }
        }

        System.out.println("PASS");
    }

    private static void testAISpeed()
            throws Exception {

        System.out.println(
                "\n===== TEST: AI SPEED =====");

        ChessAI ai = new ChessAI(STOCKFISH_PATH);

        ChessBoard board = new ChessBoard();

        long total = 0;

        for (int i = 0; i < 20; i++) {

            long start = System.currentTimeMillis();

            ai.getBestMove(
                    board.toFEN(),
                    100);

            long end = System.currentTimeMillis();

            total += (end - start);
        }

        System.out.println(
                "Average = "
                        + total / 20
                        + " ms");

        ai.close();
    }

    private static void testChess960AI()
            throws Exception {

        System.out.println(
                "\n===== TEST: Chess960 AI =====");

        ChessAI ai = new ChessAI(STOCKFISH_PATH);

        ai.setChess960(true);

        for (int game = 1; game <= 20; game++) {

            ChessBoard board = new ChessBoard();

            board.setupChess960();

            for (int moveCount = 1; moveCount <= 300; moveCount++) {

                if (board.isCheckmate(
                        board.getCurrentPlayer())
                        ||
                        board.isStalemate(
                                board.getCurrentPlayer())) {

                    break;
                }

                Move move = ai.getBestMove(
                        board.toFEN(),
                        50);

                if (move == null) {

                    System.out.println(
                            "FAIL GAME "
                                    + game);

                    System.out.println(
                            board.toFEN());

                    ai.close();
                    return;
                }

                if (!board.makeMove(move)) {

                    System.out.println(
                            "ILLEGAL MOVE");

                    ai.close();
                    return;
                }
            }
        }

        ai.close();

        System.out.println("PASS");
    }

    private static void testPromotion() {

        System.out.println("\n===== TEST: Promotion =====");

        ChessBoard board = new ChessBoard();

        board.clearBoard();

        board.setPiece(7, 4, new ChessPiece(ChessPiece.Type.KING, Player.WHITE));
        board.setPiece(0, 4, new ChessPiece(ChessPiece.Type.KING, Player.BLACK));

        board.setPiece(
                1,
                0,
                new ChessPiece(
                        ChessPiece.Type.PAWN,
                        Player.WHITE));

        Move promote = new Move(1, 0, 0, 0);

        String san = ChessNotation.getSAN(board, promote);

        System.out.println("Expected: a8=Q");
        System.out.println("Actual:   " + san);
    }
}
