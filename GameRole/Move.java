package GameRole;

public class Move {
    private int fromRow, fromCol;
    private int toRow, toCol;
    private ChessPiece capturedPiece;
    private ChessPiece.Type promotionType;
    private ChessPiece movedPiece;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(ChessPiece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public ChessPiece.Type getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(ChessPiece.Type promotionType) {
        this.promotionType = promotionType;
    }

    public String toNotation() {
        return squareName(fromRow, fromCol) + " → " + squareName(toRow, toCol);
    }

    private String squareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    public ChessPiece getMovedPiece() {
        return movedPiece;
    }

    public void setMovedPiece(ChessPiece movedPiece) {
        this.movedPiece = movedPiece;
    }

    @Override
    public String toString() {
        return "(" + fromRow + "," + fromCol + ") -> (" + toRow + "," + toCol + ")";
    }
}
