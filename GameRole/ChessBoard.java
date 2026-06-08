package GameRole;

public class ChessBoard {
    private ChessPiece[][] board;
    private Player currentPlayer;
    private boolean gameOver = false;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteLeftRookMoved = false;
    private boolean whiteRightRookMoved = false;
    private boolean blackLeftRookMoved = false;
    private boolean blackRightRookMoved = false;

    private Move lastMove;
    private Move lastExecutedMove;

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

        // en passant
        if (isEnPassantMove(move)) {
            int capturedPawnRow = move.getFromRow();
            int capturedPawnCol = move.getToCol();
            move.setCapturedPiece(board[capturedPawnRow][capturedPawnCol]);
            board[capturedPawnRow][capturedPawnCol] = null;
        }

        boolean castling = isCastlingMove(move);
        // normal move
        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;

        // castling
        if (castling) {
            moveRookForCastling(move);
        }

        // promotion
        if (piece.getType() == ChessPiece.Type.PAWN) {
            if ((piece.isWhite() && move.getToRow() == 0)
                    || (!piece.isWhite() && move.getToRow() == 7)) {

                ChessPiece.Type promoteTo = move.getPromotionType();

                if (promoteTo == null) {
                    promoteTo = ChessPiece.Type.QUEEN;
                }

                board[move.getToRow()][move.getToCol()] = new ChessPiece(promoteTo, piece.getOwner());
            }
        }

        updateMovedFlags(piece, move);

        lastMove = move;
        lastExecutedMove = move;

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
                if (Math.abs(rowDiff) <= 1 && Math.abs(colDiff) <= 1) {
                    return true;
                }
                return isLegalCastling(piece, fromRow, fromCol, toRow, toCol);

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
        if (isEnPassantMove(new Move(fromRow, fromCol, toRow, toCol))) {
            return true;
        }
        return false;
    }

    private boolean isLegalCastling(
            ChessPiece king,
            int fromRow,
            int fromCol,
            int toRow,
            int toCol) {
        if (king.getType() != ChessPiece.Type.KING)
            return false;
        if (fromRow != toRow)
            return false;
        if (fromCol != 4)
            return false;
        if (Math.abs(toCol - fromCol) != 2)
            return false;

        Player player = king.getOwner();

        if (isKingInCheck(player))
            return false;

        if (player == Player.WHITE && whiteKingMoved)
            return false;
        if (player == Player.BLACK && blackKingMoved)
            return false;

        // king side
        if (toCol == 6) {
            if (player == Player.WHITE && whiteRightRookMoved)
                return false;
            if (player == Player.BLACK && blackRightRookMoved)
                return false;

            ChessPiece rook = board[fromRow][7];

            if (rook == null || rook.getType() != ChessPiece.Type.ROOK || rook.getOwner() != player) {
                return false;
            }

            if (board[fromRow][5] != null || board[fromRow][6] != null) {
                return false;
            }

            return !squareUnderAttack(fromRow, 5, player.opposite())
                    && !squareUnderAttack(fromRow, 6, player.opposite());
        }

        // queen side
        if (toCol == 2) {
            if (player == Player.WHITE && whiteLeftRookMoved)
                return false;
            if (player == Player.BLACK && blackLeftRookMoved)
                return false;

            ChessPiece rook = board[fromRow][0];

            if (rook == null || rook.getType() != ChessPiece.Type.ROOK || rook.getOwner() != player) {
                return false;
            }

            if (board[fromRow][1] != null || board[fromRow][2] != null || board[fromRow][3] != null) {
                return false;
            }

            return !squareUnderAttack(fromRow, 3, player.opposite())
                    && !squareUnderAttack(fromRow, 2, player.opposite());
        }

        return false;
    }

    private boolean isEnPassantMove(Move move) {
        ChessPiece pawn = getPiece(move.getFromRow(), move.getFromCol());

        if (pawn == null || pawn.getType() != ChessPiece.Type.PAWN) {
            return false;
        }

        if (lastMove == null)
            return false;

        ChessPiece lastPiece = getPiece(lastMove.getToRow(), lastMove.getToCol());

        if (lastPiece == null || lastPiece.getType() != ChessPiece.Type.PAWN) {
            return false;
        }

        if (lastPiece.getOwner() == pawn.getOwner()) {
            return false;
        }

        int direction = pawn.isWhite() ? -1 : 1;

        boolean lastMoveWasTwoSquares = Math.abs(lastMove.getToRow() - lastMove.getFromRow()) == 2;

        boolean pawnBeside = lastMove.getToRow() == move.getFromRow()
                && Math.abs(lastMove.getToCol() - move.getFromCol()) == 1;

        boolean moveDiagonalToEmpty = move.getToRow() == move.getFromRow() + direction
                && move.getToCol() == lastMove.getToCol()
                && getPiece(move.getToRow(), move.getToCol()) == null;

        return lastMoveWasTwoSquares && pawnBeside && moveDiagonalToEmpty;
    }

    private boolean isCastlingMove(Move move) {
        ChessPiece piece = getPiece(move.getFromRow(), move.getFromCol());

        return piece != null
                && piece.getType() == ChessPiece.Type.KING
                && Math.abs(move.getToCol() - move.getFromCol()) == 2;
    }

    private void moveRookForCastling(Move move) {
        int row = move.getFromRow();

        // king side
        if (move.getToCol() == 6) {
            board[row][5] = board[row][7];
            board[row][7] = null;
        }

        // queen side
        if (move.getToCol() == 2) {
            board[row][3] = board[row][0];
            board[row][0] = null;
        }
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int[] findKing(Player player) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];

                if (piece != null
                        && piece.getOwner() == player
                        && piece.getType() == ChessPiece.Type.KING) {
                    return new int[] { row, col };
                }
            }
        }

        return null;
    }

    public boolean squareUnderAttack(int row, int col, Player attacker) {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                ChessPiece piece = board[fromRow][fromCol];

                if (piece != null && piece.getOwner() == attacker) {
                    Move attackMove = new Move(fromRow, fromCol, row, col);

                    if (isLegalMove(attackMove)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void updateMovedFlags(ChessPiece piece, Move move) {
        if (piece.getType() == ChessPiece.Type.KING) {
            if (piece.getOwner() == Player.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }

        if (piece.getType() == ChessPiece.Type.ROOK) {
            if (move.getFromRow() == 7 && move.getFromCol() == 0) {
                whiteLeftRookMoved = true;
            }

            if (move.getFromRow() == 7 && move.getFromCol() == 7) {
                whiteRightRookMoved = true;
            }

            if (move.getFromRow() == 0 && move.getFromCol() == 0) {
                blackLeftRookMoved = true;
            }

            if (move.getFromRow() == 0 && move.getFromCol() == 7) {
                blackRightRookMoved = true;
            }
        }
    }

    public ChessPiece[][] copyBoardArray() {
        ChessPiece[][] copy = new ChessPiece[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];

                if (piece != null) {
                    copy[row][col] = new ChessPiece(piece.getType(), piece.getOwner());
                }
            }
        }

        return copy;
    }

    public void loadBoardArray(ChessPiece[][] newBoard) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = newBoard[row][col];

                if (piece != null) {
                    board[row][col] = new ChessPiece(piece.getType(), piece.getOwner());
                } else {
                    board[row][col] = null;
                }
            }
        }
    }

    public Move getLastExecutedMove() {
        return lastExecutedMove;
    }

    public java.util.ArrayList<Move> getLegalMovesForPiece(int row, int col) {
        java.util.ArrayList<Move> moves = new java.util.ArrayList<>();

        ChessPiece piece = getPiece(row, col);
        if (piece == null)
            return moves;
        if (piece.getOwner() != currentPlayer)
            return moves;

        for (int toRow = 0; toRow < 8; toRow++) {
            for (int toCol = 0; toCol < 8; toCol++) {
                Move move = new Move(row, col, toRow, toCol);

                if (canMoveWithoutCheck(move, currentPlayer)) {
                    moves.add(move);
                }
            }
        }

        return moves;
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
