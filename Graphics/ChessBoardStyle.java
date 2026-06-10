package Graphics;

import java.awt.*;

public class ChessBoardStyle {

    public enum BoardTheme {
        CLASSIC, DARK, BLUE, GREEN
    }

    private BoardTheme theme = BoardTheme.CLASSIC;

    public void setTheme(BoardTheme theme) {
        this.theme = theme;
    }

    public void drawBoard(Graphics g, int tileSize, int margin) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        Color lightColor;
        Color darkColor;
        Color borderColor;
        Color goldColor = new Color(190, 150, 80);

        switch (theme) {
            case DARK:
                lightColor = new Color(85, 80, 72);
                darkColor = new Color(32, 28, 24);
                borderColor = new Color(12, 10, 8);
                break;

            case BLUE:
                lightColor = new Color(135, 155, 175);
                darkColor = new Color(35, 55, 80);
                borderColor = new Color(18, 28, 45);
                break;

            case GREEN:
                lightColor = new Color(155, 165, 125);
                darkColor = new Color(55, 75, 45);
                borderColor = new Color(25, 35, 22);
                break;

            default:
                lightColor = new Color(160, 135, 95);
                darkColor = new Color(65, 48, 35);
                borderColor = new Color(25, 18, 12);
        }

        // shadow behind board
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(
                margin - 28,
                margin - 24,
                tileSize * 8 + 56,
                tileSize * 8 + 56,
                32,
                32
        );

        // outer dark wooden frame
        g2.setColor(borderColor);
        g2.fillRoundRect(
                margin - 24,
                margin - 24,
                tileSize * 8 + 48,
                tileSize * 8 + 48,
                30,
                30
        );

        // gold frame
        g2.setColor(goldColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(
                margin - 20,
                margin - 20,
                tileSize * 8 + 40,
                tileSize * 8 + 40,
                26,
                26
        );

        // inner board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color base = (row + col) % 2 == 0 ? lightColor : darkColor;

                g2.setColor(base);
                g2.fillRect(
                        margin + col * tileSize,
                        margin + row * tileSize,
                        tileSize,
                        tileSize
                );

                // slight texture line
                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawLine(
                        margin + col * tileSize,
                        margin + row * tileSize,
                        margin + (col + 1) * tileSize,
                        margin + row * tileSize + tileSize
                );

                g2.setColor(new Color(0, 0, 0, 18));
                g2.drawLine(
                        margin + col * tileSize,
                        margin + row * tileSize + tileSize,
                        margin + (col + 1) * tileSize,
                        margin + row * tileSize
                );
            }
        }

        // inner gold border
        g2.setColor(new Color(230, 190, 110, 160));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(
                margin,
                margin,
                tileSize * 8,
                tileSize * 8
        );

        // subtle dark grid
        g2.setColor(new Color(0, 0, 0, 45));
        g2.setStroke(new BasicStroke(1));

        for (int i = 0; i <= 8; i++) {
            g2.drawLine(
                    margin + i * tileSize,
                    margin,
                    margin + i * tileSize,
                    margin + tileSize * 8
            );

            g2.drawLine(
                    margin,
                    margin + i * tileSize,
                    margin + tileSize * 8,
                    margin + i * tileSize
            );
        }
    }
}