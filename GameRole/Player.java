package GameRole;

public enum Player {
    WHITE,
    BLACK;

    public Player opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}