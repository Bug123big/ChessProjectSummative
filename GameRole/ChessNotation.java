package GameRole;

import java.util.ArrayList;

public class ChessNotation {

    public static String getSAN(ChessBoard board, Move move) {
        ChessPiece piece = board.getPiece(move.getFromRow(), move.getFromCol());

        if (piece == null) {
            return move.toNotation();
        }

        // Castling
        if (piece.getType() == ChessPiece.Type.KING) {

            ChessPiece target = board.getPiece(
                    move.getToRow(),
                    move.getToCol());

            // Classical castling: king moves two squares
            if (Math.abs(move.getToCol() - move.getFromCol()) == 2) {
                return move.getToCol() > move.getFromCol()
                        ? "O-O"
                        : "O-O-O";
            }

            // Chess960 castling: king moves onto own rook square
            if (target != null
                    && target.getType() == ChessPiece.Type.ROOK
                    && target.getOwner() == piece.getOwner()) {
                return move.getToCol() > move.getFromCol()
                        ? "O-O"
                        : "O-O-O";
            }
        }

        ChessPiece target = board.getPiece(move.getToRow(), move.getToCol());
        boolean capture = target != null;

        String targetSquare = squareName(move.getToRow(), move.getToCol());
        String result = "";

        if (piece.getType() == ChessPiece.Type.PAWN) {
            if (capture) {
                char fromFile = (char) ('a' + move.getFromCol());
                result += fromFile + "x" + targetSquare;
            } else {
                result += targetSquare;
            }

            if ((piece.isWhite() && move.getToRow() == 0)
                    || (!piece.isWhite() && move.getToRow() == 7)) {
                result += "=Q";
            }
        } else {
            result += pieceLetter(piece);
            result += disambiguation(board, move, piece);

            if (capture) {
                result += "x";
            }

            result += targetSquare;
        }

        board.makeMoveForNotation(move);

        Player nextPlayer = board.getCurrentPlayer();

        if (board.isCheckmate(nextPlayer)) {
            result += "#";
        } else if (board.isKingInCheck(nextPlayer)) {
            result += "+";
        }

        board.undoMoveForNotation(move);

        return result;
    }

    private static String disambiguation(ChessBoard board, Move move, ChessPiece piece) {
        ArrayList<Move> similarMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row == move.getFromRow() && col == move.getFromCol()) {
                    continue;
                }

                ChessPiece other = board.getPiece(row, col);

                if (other != null
                        && other.getOwner() == piece.getOwner()
                        && other.getType() == piece.getType()) {

                    Move testMove = new Move(row, col, move.getToRow(), move.getToCol());

                    if (board.canMoveWithoutCheck(testMove, piece.getOwner())) {
                        similarMoves.add(testMove);
                    }
                }
            }
        }

        if (similarMoves.isEmpty()) {
            return "";
        }

        boolean sameFile = false;
        boolean sameRank = false;

        for (Move m : similarMoves) {
            if (m.getFromCol() == move.getFromCol()) {
                sameFile = true;
            }

            if (m.getFromRow() == move.getFromRow()) {
                sameRank = true;
            }
        }

        char file = (char) ('a' + move.getFromCol());
        int rank = 8 - move.getFromRow();

        if (!sameFile) {
            return String.valueOf(file);
        }

        if (!sameRank) {
            return String.valueOf(rank);
        }

        return "" + file + rank;
    }

    public static String getNotationFromUci(ChessBoard board, String uci) {
        if (uci == null || uci.length() < 4) {
            return uci;
        }

        int fromCol = uci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(uci.charAt(1));

        int toCol = uci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(uci.charAt(3));

        Move move = new Move(fromRow, fromCol, toRow, toCol);

        return getSAN(board, move);
    }

    private static String pieceLetter(ChessPiece piece) {
        return switch (piece.getType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "";
        };
    }

    private static String squareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}