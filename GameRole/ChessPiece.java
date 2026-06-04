package GameRole;

public class ChessPiece {
    public enum Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    private Type type;
    private Player owner;

    public ChessPiece(Type type, Player owner) {
        this.type = type;
        this.owner = owner;
    }

    public Type getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isWhite() {
        return owner == Player.WHITE;
    }

    public char getFenChar() {
        char c = switch (type) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case ROOK -> 'r';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            case PAWN -> 'p';
        };

        return isWhite() ? Character.toUpperCase(c) : c;
    }
}
