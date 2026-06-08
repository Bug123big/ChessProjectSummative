package Graphics;

import GameRole.*;

import java.awt.*;

public class ChessPieceStyle {

    public void drawPieces(Graphics g, ChessBoard board, int tileSize, int margin) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = new Font("Serif", Font.BOLD, tileSize - 12);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(row, col);

                if (piece != null) {
                    String symbol = getSymbol(piece);

                    int x = margin + col * tileSize + (tileSize - fm.stringWidth(symbol)) / 2;
                    int y = margin + row * tileSize + ((tileSize - fm.getHeight()) / 2) + fm.getAscent();

                    // shadow
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.drawString(symbol, x + 3, y + 3);

                    // piece color
                    if (piece.isWhite()) {
                        g2.setColor(new Color(245, 245, 245));
                    } else {
                        g2.setColor(new Color(25, 25, 25));
                    }

                    g2.drawString(symbol, x, y);
                }
            }
        }
    }

    private String getSymbol(ChessPiece piece) {
        if (piece.isWhite()) {
            return switch (piece.getType()) {
                case KING -> "♔";
                case QUEEN -> "♕";
                case ROOK -> "♖";
                case BISHOP -> "♗";
                case KNIGHT -> "♘";
                case PAWN -> "♙";
            };
        } else {
            return switch (piece.getType()) {
                case KING -> "♚";
                case QUEEN -> "♛";
                case ROOK -> "♜";
                case BISHOP -> "♝";
                case KNIGHT -> "♞";
                case PAWN -> "♟";
            };
        }
    }
}
