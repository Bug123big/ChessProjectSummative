package GameRole;

public class ChessBoard {
    private ChessPiece[][] board;
    private Player currentPlayer;

    public ChessBoard() {
        board = new ChessPiece[8][8];
        currentPlayer = Player.WHITE;
        setupBoard();
    }

    private void setupBoard() {
        board[0][0] = new ChessPiece(ChessPiece.Type.ROOK, Player.BLACK);
        board[0][1] = new ChessPiece(ChessPiece.Type.KNIGHT, Player.BLACK);
        board[0][2] = new ChessPiece(ChessPiece.Type.BISHOP, Player.BLACK);
        board[0][3] = new ChessPiece(ChessPiece.Type.QUEEN, Player.BLACK);
        board[0][4] = new ChessPiece(ChessPiece.Type.KING, Player.BLACK);
        board[0][5] = new ChessPiece(ChessPiece.Type.BISHOP, Player.BLACK);
        board[0][6] = new ChessPiece(ChessPiece.Type.KNIGHT, Player.BLACK);
        board[0][7] = new ChessPiece(ChessPiece.Type.ROOK, Player.BLACK);

        for (int col = 0; col < 8; col++) {
            board[1][col] = new ChessPiece(ChessPiece.Type.PAWN, Player.BLACK);
        }

        for (int col = 0; col < 8; col++) {
            board[6][col] = new ChessPiece(ChessPiece.Type.PAWN, Player.WHITE);
        }

        board[7][0] = new ChessPiece(ChessPiece.Type.ROOK, Player.WHITE);
        board[7][1] = new ChessPiece(ChessPiece.Type.KNIGHT, Player.WHITE);
        board[7][2] = new ChessPiece(ChessPiece.Type.BISHOP, Player.WHITE);
        board[7][3] = new ChessPiece(ChessPiece.Type.QUEEN, Player.WHITE);
        board[7][4] = new ChessPiece(ChessPiece.Type.KING, Player.WHITE);
        board[7][5] = new ChessPiece(ChessPiece.Type.BISHOP, Player.WHITE);
        board[7][6] = new ChessPiece(ChessPiece.Type.KNIGHT, Player.WHITE);
        board[7][7] = new ChessPiece(ChessPiece.Type.ROOK, Player.WHITE);
    }

    public ChessPiece getPiece(int row, int col) {
        if (!isInsideBoard(row, col)) return null;
        return board[row][col];
    }

    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean makeMove(Move move) {
        ChessPiece piece = getPiece(move.getFromRow(), move.getFromCol());

        if (piece == null) return false;
        if (piece.getOwner() != currentPlayer) return false;

        ChessPiece target = getPiece(move.getToRow(), move.getToCol());

        if (target != null && target.getOwner() == piece.getOwner()) {
            return false;
        }

        move.setCapturedPiece(target);

        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;

        currentPlayer = currentPlayer.opposite();
        return true;
    }

    public String toFEN() {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int empty = 0;

            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];

                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(piece.getFenChar());
                }
            }

            if (empty > 0) fen.append(empty);
            if (row < 7) fen.append("/");
        }

        fen.append(currentPlayer == Player.WHITE ? " w " : " b ");
        fen.append("KQkq - 0 1");

        return fen.toString();
    }
}
