package Graphics;

import GameRole.*;
import GUI.MainPanel;

import java.awt.*;

public class ChessPieceStyle {

    public enum PieceTheme {
        UNICODE, MINIMAL
    }

    private PieceTheme theme = PieceTheme.UNICODE;

    public void setTheme(PieceTheme theme) {
        this.theme = theme;
    }

    public void drawPieces(
            Graphics g,
            ChessBoard board,
            int tileSize,
            int margin,
            MainPanel.BoardCanvas canvas
    ) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (canvas.isDraggingPieceAt(row, col)) {
                    continue;
                }

                ChessPiece piece = board.getPiece(row, col);

                if (piece != null) {
                    int centerX = margin + col * tileSize + tileSize / 2;
                    int centerY = margin + row * tileSize + tileSize / 2;

                    drawSinglePiece(g, piece, centerX, centerY, tileSize);
                }
            }
        }
    }

    public void drawSinglePieceAtMouse(
            Graphics g,
            ChessPiece piece,
            int mouseX,
            int mouseY,
            int tileSize
    ) {
        drawSinglePiece(g, piece, mouseX, mouseY, tileSize);
    }

    private void drawSinglePiece(
            Graphics g,
            ChessPiece piece,
            int centerX,
            int centerY,
            int tileSize
    ) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        String symbol;

        if (theme == PieceTheme.MINIMAL) {
            symbol = getLetter(piece);
        } else {
            symbol = getSymbol(piece);
        }

        Font font;

        if (theme == PieceTheme.MINIMAL) {
            font = new Font("Arial", Font.BOLD, tileSize / 2);
        } else {
            font = new Font("Serif", Font.PLAIN, tileSize - 8);
        }

        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();

        int x = centerX - fm.stringWidth(symbol) / 2;
        int y = centerY - fm.getHeight() / 2 + fm.getAscent();

        if (piece.isWhite()) {
            // 白棋：黑色边框
            g2.setColor(Color.BLACK);
            g2.drawString(symbol, x - 1, y);
            g2.drawString(symbol, x + 1, y);
            g2.drawString(symbol, x, y - 1);
            g2.drawString(symbol, x, y + 1);

            // 白棋：白色主体
            g2.setColor(new Color(250, 250, 250));
            g2.drawString(symbol, x, y);
        } else {
            // 黑棋
            g2.setColor(new Color(20, 20, 20));
            g2.drawString(symbol, x, y);
        }
    }

    private String getLetter(ChessPiece piece) {
        return switch (piece.getType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
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
