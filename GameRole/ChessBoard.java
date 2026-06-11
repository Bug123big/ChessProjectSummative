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
    private boolean chess960Mode = false;
    private int whiteKingStartCol = 4;
    private int blackKingStartCol = 4;
    private int whiteQueenRookStartCol = 0;
    private int whiteKingRookStartCol = 7;
    private int blackQueenRookStartCol = 0;
    private int blackKingRookStartCol = 7;

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

        move.setMovedPiece(piece);
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

        if (castling) {
            executeCastling(move, piece);
        } else {
            board[move.getToRow()][move.getToCol()] = piece;
            board[move.getFromRow()][move.getFromCol()] = null;
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

        piece.setMoved(true);
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
                if (isCastlingMove(move)) {
                    return canCastle(move, piece.getOwner());
                }

                if (Math.abs(rowDiff) <= 1 && Math.abs(colDiff) <= 1) {
                    return true;
                }

                return false;

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

        if (piece == null || piece.getType() != ChessPiece.Type.KING) {
            return false;
        }

        if (!chess960Mode) {
            return Math.abs(move.getToCol() - move.getFromCol()) == 2;
        }

        // Chess960: king moves onto rook square
        ChessPiece target = getPiece(move.getToRow(), move.getToCol());

        return target != null
                && target.getType() == ChessPiece.Type.ROOK
                && target.getOwner() == piece.getOwner();
    }

    private void executeCastling(Move move, ChessPiece king) {
        int row = move.getFromRow();

        if (!chess960Mode) {
            board[move.getToRow()][move.getToCol()] = king;
            board[move.getFromRow()][move.getFromCol()] = null;

            moveRookForCastling(move);
            king.setMoved(true);
            return;
        }

        int kingFromCol = move.getFromCol();
        int rookFromCol = move.getToCol();

        boolean kingSide = rookFromCol > kingFromCol;

        int kingFinalCol = kingSide ? 6 : 2;
        int rookFinalCol = kingSide ? 5 : 3;

        ChessPiece rook = board[row][rookFromCol];

        board[row][kingFromCol] = null;
        board[row][rookFromCol] = null;

        board[row][kingFinalCol] = king;
        board[row][rookFinalCol] = rook;

        king.setMoved(true);

        if (rook != null) {
            rook.setMoved(true);
        }
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

        boolean castling = isCastlingMove(move);

        if (target != null && target.getOwner() == player && !castling) {
            return false;
        }

        if (!isLegalMove(move)) {
            return false;
        }

        if (isCastlingMove(move)) {
            return isLegalMove(move);
        }

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

            if (empty > 0) {
                fen.append(empty);
            }

            if (row < 7) {
                fen.append("/");
            }
        }

        fen.append(currentPlayer == Player.WHITE ? " w " : " b ");

        String castling;

        if (chess960Mode) {
            castling = getChess960CastlingRights();
        } else {
            castling = getClassicalCastlingRights();
        }

        fen.append(castling);

        fen.append(" - ");
        fen.append("0 1");

        return fen.toString();
    }

    public void makeMoveForNotation(Move move) {
        ChessPiece piece = getPiece(move.getFromRow(), move.getFromCol());
        ChessPiece target = getPiece(move.getToRow(), move.getToCol());

        move.setCapturedPiece(target);

        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;

        currentPlayer = currentPlayer.opposite();
    }

    public void undoMoveForNotation(Move move) {
        ChessPiece piece = getPiece(move.getToRow(), move.getToCol());

        board[move.getFromRow()][move.getFromCol()] = piece;
        board[move.getToRow()][move.getToCol()] = move.getCapturedPiece();

        currentPlayer = currentPlayer.opposite();
    }

    public void setupChess960() {
        chess960Mode = true;
        board = new ChessPiece[8][8];

        ChessPiece.Type[] backRank = generateChess960BackRank();

        for (int col = 0; col < 8; col++) {
            board[7][col] = new ChessPiece(backRank[col], Player.WHITE);
            board[0][col] = new ChessPiece(backRank[col], Player.BLACK);

            board[6][col] = new ChessPiece(ChessPiece.Type.PAWN, Player.WHITE);
            board[1][col] = new ChessPiece(ChessPiece.Type.PAWN, Player.BLACK);

            if (backRank[col] == ChessPiece.Type.KING) {
                whiteKingStartCol = col;
                blackKingStartCol = col;
            }
        }

        int rookCount = 0;

        for (int col = 0; col < 8; col++) {
            if (backRank[col] == ChessPiece.Type.ROOK) {
                if (rookCount == 0) {
                    whiteQueenRookStartCol = col;
                    blackQueenRookStartCol = col;
                } else {
                    whiteKingRookStartCol = col;
                    blackKingRookStartCol = col;
                }

                rookCount++;
            }
        }

        currentPlayer = Player.WHITE;
        lastMove = null;
        gameOver = false;
    }

    private ChessPiece.Type[] generateChess960BackRank() {
        ChessPiece.Type[] rank = new ChessPiece.Type[8];
        java.util.ArrayList<Integer> empty = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 8; i++) {
            empty.add(i);
        }

        int bishop1 = random.nextInt(4) * 2;
        rank[bishop1] = ChessPiece.Type.BISHOP;
        empty.remove(Integer.valueOf(bishop1));

        int bishop2 = random.nextInt(4) * 2 + 1;
        rank[bishop2] = ChessPiece.Type.BISHOP;
        empty.remove(Integer.valueOf(bishop2));

        int queen = empty.remove(random.nextInt(empty.size()));
        rank[queen] = ChessPiece.Type.QUEEN;

        int knight1 = empty.remove(random.nextInt(empty.size()));
        rank[knight1] = ChessPiece.Type.KNIGHT;

        int knight2 = empty.remove(random.nextInt(empty.size()));
        rank[knight2] = ChessPiece.Type.KNIGHT;

        java.util.Collections.sort(empty);

        rank[empty.get(0)] = ChessPiece.Type.ROOK;
        rank[empty.get(1)] = ChessPiece.Type.KING;
        rank[empty.get(2)] = ChessPiece.Type.ROOK;

        return rank;
    }

    private String getClassicalCastlingRights() {
        String castling = "";

        ChessPiece whiteKing = board[7][4];
        ChessPiece blackKing = board[0][4];

        if (whiteKing != null
                && whiteKing.getType() == ChessPiece.Type.KING
                && whiteKing.isWhite()
                && !whiteKing.hasMoved()) {

            ChessPiece whiteKingRook = board[7][7];

            if (whiteKingRook != null
                    && whiteKingRook.getType() == ChessPiece.Type.ROOK
                    && whiteKingRook.isWhite()
                    && !whiteKingRook.hasMoved()) {
                castling += "K";
            }

            ChessPiece whiteQueenRook = board[7][0];

            if (whiteQueenRook != null
                    && whiteQueenRook.getType() == ChessPiece.Type.ROOK
                    && whiteQueenRook.isWhite()
                    && !whiteQueenRook.hasMoved()) {
                castling += "Q";
            }
        }

        if (blackKing != null
                && blackKing.getType() == ChessPiece.Type.KING
                && !blackKing.isWhite()
                && !blackKing.hasMoved()) {

            ChessPiece blackKingRook = board[0][7];

            if (blackKingRook != null
                    && blackKingRook.getType() == ChessPiece.Type.ROOK
                    && !blackKingRook.isWhite()
                    && !blackKingRook.hasMoved()) {
                castling += "k";
            }

            ChessPiece blackQueenRook = board[0][0];

            if (blackQueenRook != null
                    && blackQueenRook.getType() == ChessPiece.Type.ROOK
                    && !blackQueenRook.isWhite()
                    && !blackQueenRook.hasMoved()) {
                castling += "q";
            }
        }

        return castling.isEmpty() ? "-" : castling;
    }

    private String getChess960CastlingRights() {
        String castling = "";

        ChessPiece whiteKing = board[7][whiteKingStartCol];

        if (whiteKing != null
                && whiteKing.getType() == ChessPiece.Type.KING
                && whiteKing.isWhite()
                && !whiteKing.hasMoved()) {

            ChessPiece kingSideRook = board[7][whiteKingRookStartCol];

            if (kingSideRook != null
                    && kingSideRook.getType() == ChessPiece.Type.ROOK
                    && kingSideRook.isWhite()
                    && !kingSideRook.hasMoved()) {
                castling += (char) ('A' + whiteKingRookStartCol);
            }

            ChessPiece queenSideRook = board[7][whiteQueenRookStartCol];

            if (queenSideRook != null
                    && queenSideRook.getType() == ChessPiece.Type.ROOK
                    && queenSideRook.isWhite()
                    && !queenSideRook.hasMoved()) {
                castling += (char) ('A' + whiteQueenRookStartCol);
            }
        }

        ChessPiece blackKing = board[0][blackKingStartCol];

        if (blackKing != null
                && blackKing.getType() == ChessPiece.Type.KING
                && !blackKing.isWhite()
                && !blackKing.hasMoved()) {

            ChessPiece kingSideRook = board[0][blackKingRookStartCol];

            if (kingSideRook != null
                    && kingSideRook.getType() == ChessPiece.Type.ROOK
                    && !kingSideRook.isWhite()
                    && !kingSideRook.hasMoved()) {
                castling += (char) ('a' + blackKingRookStartCol);
            }

            ChessPiece queenSideRook = board[0][blackQueenRookStartCol];

            if (queenSideRook != null
                    && queenSideRook.getType() == ChessPiece.Type.ROOK
                    && !queenSideRook.isWhite()
                    && !queenSideRook.hasMoved()) {
                castling += (char) ('a' + blackQueenRookStartCol);
            }
        }

        return castling.isEmpty() ? "-" : castling;
    }

    private boolean canCastle(Move move, Player player) {
        ChessPiece king = getPiece(move.getFromRow(), move.getFromCol());

        if (king == null || king.getType() != ChessPiece.Type.KING || king.hasMoved()) {
            return false;
        }

        int row = player == Player.WHITE ? 7 : 0;

        if (move.getFromRow() != row || move.getToRow() != row) {
            return false;
        }

        if (!chess960Mode) {
            return canCastleClassical(move, player, row);
        }

        return canCastleChess960(move, player, row);
    }

    private boolean canCastleClassical(Move move, Player player, int row) {
        int fromCol = move.getFromCol();
        int toCol = move.getToCol();

        if (fromCol != 4) {
            return false;
        }

        if (toCol != 6 && toCol != 2) {
            return false;
        }

        boolean kingSide = toCol == 6;
        int rookCol = kingSide ? 7 : 0;

        ChessPiece rook = getPiece(row, rookCol);

        if (rook == null
                || rook.getType() != ChessPiece.Type.ROOK
                || rook.getOwner() != player
                || rook.hasMoved()) {
            return false;
        }

        int step = kingSide ? 1 : -1;

        for (int col = fromCol + step; col != rookCol; col += step) {
            if (getPiece(row, col) != null) {
                return false;
            }
        }

        if (squareUnderAttack(row, fromCol, player.opposite())) {
            return false;
        }

        for (int col = fromCol + step; col != toCol + step; col += step) {
            if (squareUnderAttack(row, col, player.opposite())) {
                return false;
            }
        }

        return true;
    }

    private boolean canCastleChess960(Move move, Player player, int row) {
        int kingFromCol = move.getFromCol();
        int rookFromCol = move.getToCol();

        ChessPiece rook = getPiece(row, rookFromCol);

        if (rook == null
                || rook.getType() != ChessPiece.Type.ROOK
                || rook.getOwner() != player
                || rook.hasMoved()) {
            return false;
        }

        boolean kingSide = rookFromCol > kingFromCol;

        int kingFinalCol = kingSide ? 6 : 2;
        int rookFinalCol = kingSide ? 5 : 3;

        // squares between king and rook must be empty, except king/rook themselves
        int min = Math.min(kingFromCol, rookFromCol);
        int max = Math.max(kingFromCol, rookFromCol);

        for (int col = min + 1; col < max; col++) {
            if (col != kingFromCol && col != rookFromCol && getPiece(row, col) != null) {
                return false;
            }
        }

        // king final square and rook final square must be free unless occupied by
        // king/rook
        if (!isEmptyOrSamePiece(row, kingFinalCol, kingFromCol, rookFromCol)) {
            return false;
        }

        if (!isEmptyOrSamePiece(row, rookFinalCol, kingFromCol, rookFromCol)) {
            return false;
        }

        // king may not be currently in check
        if (squareUnderAttack(row, kingFromCol, player.opposite())) {
            return false;
        }

        // king path from start to final must not pass through attacked squares
        int step = kingFinalCol > kingFromCol ? 1 : -1;

        for (int col = kingFromCol; col != kingFinalCol + step; col += step) {
            if (squareUnderAttack(row, col, player.opposite())) {
                return false;
            }
        }

        return true;
    }

    private boolean isEmptyOrSamePiece(int row, int col, int kingFromCol, int rookFromCol) {
        if (col == kingFromCol || col == rookFromCol) {
            return true;
        }

        return getPiece(row, col) == null;
    }

    public void clearBoard() {
        board = new ChessPiece[8][8];
        lastMove = null;
        lastExecutedMove = null;
        gameOver = false;
        currentPlayer = Player.WHITE;
    }

    public void setPiece(int row, int col, ChessPiece piece) {
        if (isInsideBoard(row, col)) {
            board[row][col] = piece;
        }
    }
}
