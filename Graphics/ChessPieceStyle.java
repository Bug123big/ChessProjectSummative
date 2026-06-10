package Graphics;

import GameRole.*;
import GUI.MainPanel;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class ChessPieceStyle {

    public enum PieceTheme {
        HOLLOW, SOLID
    }

    private PieceTheme theme = PieceTheme.HOLLOW;

    public void setTheme(PieceTheme theme) {
        this.theme = theme;
    }

    public void drawPieces(
            Graphics g,
            ChessBoard board,
            int tileSize,
            int margin,
            MainPanel.BoardCanvas canvas) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                if (canvas.isDraggingPieceAt(row, col)) {
                    continue;
                }

                ChessPiece piece = board.getPiece(row, col);

                if (piece != null) {

                    int displayRow = canvas.displayRow(row);
                    int displayCol = canvas.displayCol(col);

                    int centerX = margin + displayCol * tileSize + tileSize / 2;

                    int centerY = margin + displayRow * tileSize + tileSize / 2;

                    drawSinglePiece(
                            g,
                            piece,
                            centerX,
                            centerY,
                            tileSize);
                }
            }
        }
    }

    public void drawSinglePieceAtMouse(
            Graphics g,
            ChessPiece piece,
            int mouseX,
            int mouseY,
            int tileSize) {
        drawSinglePiece(g, piece, mouseX, mouseY, tileSize);
    }

    private void drawSinglePiece(
            Graphics g,
            ChessPiece piece,
            int centerX,
            int centerY,
            int tileSize) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String symbol = getShapeA(piece);

        Font font;

        if (theme == PieceTheme.SOLID) {
            font = new Font(
                    "Arial",
                    Font.BOLD,
                    tileSize - 10);

            symbol = getShapeB(piece);

        } else {
            font = new Font(
                    "Serif",
                    Font.PLAIN,
                    tileSize - 10);
        }

        g2.setFont(font);

        FontRenderContext frc = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(symbol, font, frc);
        Rectangle2D bounds = layout.getBounds();

        int x = (int) Math.round(centerX - bounds.getCenterX());
        int y = (int) Math.round(centerY - bounds.getCenterY());

        if (piece.isWhite()) {
            g2.setColor(new Color(70, 55, 40));
            g2.drawString(symbol, x - 1, y);
            g2.drawString(symbol, x + 1, y);
            g2.drawString(symbol, x, y - 1);
            g2.drawString(symbol, x, y + 1);

            g2.setColor(new Color(220, 205, 170));
            g2.drawString(symbol, x, y);
        } else {

            g2.setColor(new Color(235, 225, 195));
            g2.drawString(symbol, x - 1, y);
            g2.drawString(symbol, x + 1, y);
            g2.drawString(symbol, x, y - 1);
            g2.drawString(symbol, x, y + 1);

            g2.setColor(new Color(45, 30, 20));
            g2.drawString(symbol, x, y);
        }
    }

    private String getShapeB(ChessPiece piece) {
        return switch (piece.getType()) {
            case KING -> "♚";
            case QUEEN -> "♛";
            case ROOK -> "♜";
            case BISHOP -> "♝";
            case KNIGHT -> "♞";
            case PAWN -> "♟";
        };
    }

    private String getShapeA(ChessPiece piece) {
        return switch (piece.getType()) {
            case KING -> "♔";
            case QUEEN -> "♕";
            case ROOK -> "♖";
            case BISHOP -> "♗";
            case KNIGHT -> "♘";
            case PAWN -> "♙";
        };
    }
}
