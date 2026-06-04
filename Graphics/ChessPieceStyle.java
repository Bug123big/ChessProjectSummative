package Graphics;

import GameRole.*;

import java.awt.*;

public class ChessPieceStyle {

    public void drawPieces(Graphics g, ChessBoard board, int tileSize) {
        g.setFont(new Font("Serif", Font.BOLD, tileSize - 15));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(row, col);

                if (piece != null) {
                    drawPiece(g, piece, row, col, tileSize);
                }
            }
        }
    }

    private void drawPiece(Graphics g, ChessPiece piece, int row, int col, int tileSize) {
        g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);

        String symbol = switch (piece.getType()) {
            case KING -> "♔";
            case QUEEN -> "♕";
            case ROOK -> "♖";
            case BISHOP -> "♗";
            case KNIGHT -> "♘";
            case PAWN -> "♙";
        };

        int x = col * tileSize + tileSize / 4;
        int y = row * tileSize + tileSize * 3 / 4;

        g.drawString(symbol, x, y);
    }
}
