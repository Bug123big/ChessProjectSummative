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
        if (!isInsideBoard(row, col))
            return null;
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

        if (piece == null)
            return false;
        if (piece.getOwner() != currentPlayer)
            return false;

        if (!canMoveWithoutCheck(move, currentPlayer)) {
            return false;
        }

        ChessPiece target = getPiece(move.getToRow(), move.getToCol());
        move.setCapturedPiece(target);

        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;

        currentPlayer = currentPlayer.opposite();

        return true;
    }

    public boolean isLegalMove(Move move) {
        ChessPiece piece = getPiece(move.getFromRow(), move.getFromCol());

        if (piece == null)
            return false;

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        switch (piece.getType()) {
            case PAWN:
                return isLegalPawnMove(piece, fromRow, fromCol, toRow, toCol);

            case ROOK:
                return isStraightMove(rowDiff, colDiff) && isPathClear(fromRow, fromCol, toRow, toCol);

            case BISHOP:
                return isDiagonalMove(rowDiff, colDiff) && isPathClear(fromRow, fromCol, toRow, toCol);

            case QUEEN:
                return (isStraightMove(rowDiff, colDiff) || isDiagonalMove(rowDiff, colDiff))
                        && isPathClear(fromRow, fromCol, toRow, toCol);

            case KNIGHT:
                return isLegalKnightMove(rowDiff, colDiff);

            case KING:
                return Math.abs(rowDiff) <= 1 && Math.abs(colDiff) <= 1;

            default:
                return false;
        }
    }

    private boolean isStraightMove(int rowDiff, int colDiff) {
        return rowDiff == 0 || colDiff == 0;
    }

    private boolean isDiagonalMove(int rowDiff, int colDiff) {
        return Math.abs(rowDiff) == Math.abs(colDiff);
    }

    private boolean isLegalKnightMove(int rowDiff, int colDiff) {
        return (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1)
                || (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        while (row != toRow || col != toCol) {
            if (board[row][col] != null) {
                return false;
            }

            row += rowStep;
            col += colStep;
        }

        return true;
    }

    private boolean isLegalPawnMove(ChessPiece piece, int fromRow, int fromCol, int toRow, int toCol) {
        int direction = piece.isWhite() ? -1 : 1;
        int startRow = piece.isWhite() ? 6 : 1;

        ChessPiece target = getPiece(toRow, toCol);

        if (fromCol == toCol && toRow == fromRow + direction && target == null) {
            return true;
        }

        if (fromCol == toCol
                && fromRow == startRow
                && toRow == fromRow + 2 * direction
                && target == null
                && getPiece(fromRow + direction, fromCol) == null) {
            return true;
        }

        if (Math.abs(toCol - fromCol) == 1
                && toRow == fromRow + direction
                && target != null
                && target.getOwner() != piece.getOwner()) {
            return true;
        }

        return false;
    }

    public boolean isKingInCheck(Player player) {
        int kingRow = -1;
        int kingCol = -1;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];

                if (piece != null
                        && piece.getOwner() == player
                        && piece.getType() == ChessPiece.Type.KING) {
                    kingRow = row;
                    kingCol = col;
                }
            }
        }

        if (kingRow == -1) {
            return true;
        }

        Player enemy = player.opposite();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];

                if (piece != null && piece.getOwner() == enemy) {
                    Move attackMove = new Move(row, col, kingRow, kingCol);

                    if (isLegalMove(attackMove)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean hasAnyLegalMove(Player player) {
        Player oldPlayer = currentPlayer;
        currentPlayer = player;

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                ChessPiece piece = board[fromRow][fromCol];

                if (piece != null && piece.getOwner() == player) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Move move = new Move(fromRow, fromCol, toRow, toCol);

                            if (canMoveWithoutCheck(move, player)) {
                                currentPlayer = oldPlayer;
                                return true;
                            }
                        }
                    }
                }
            }
        }

        currentPlayer = oldPlayer;
        return false;
    }

    public boolean canMoveWithoutCheck(Move move, Player player) {
        ChessPiece piece = getPiece(move.getFromRow(), move.getFromCol());

        if (piece == null)
            return false;
        if (piece.getOwner() != player)
            return false;

        ChessPiece target = getPiece(move.getToRow(), move.getToCol());

        if (target != null && target.getOwner() == player) {
            return false;
        }

        if (!isLegalMove(move)) {
            return false;
        }

        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;

        boolean stillInCheck = isKingInCheck(player);

        board[move.getFromRow()][move.getFromCol()] = piece;
        board[move.getToRow()][move.getToCol()] = target;

        return !stillInCheck;
    }

    public boolean isCheckmate(Player player) {
        return isKingInCheck(player) && !hasAnyLegalMove(player);
    }

    public boolean isStalemate(Player player) {
        return !isKingInCheck(player) && !hasAnyLegalMove(player);
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

            if (empty > 0)
                fen.append(empty);
            if (row < 7)
                fen.append("/");
        }

        fen.append(currentPlayer == Player.WHITE ? " w " : " b ");
        fen.append("KQkq - 0 1");

        return fen.toString();
    }
}
