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

        Color lightColor;
        Color darkColor;
        Color borderColor;

        switch (theme) {
            case DARK:
                lightColor = new Color(95, 95, 95);
                darkColor = new Color(45, 45, 45);
                borderColor = new Color(20, 20, 20);
                break;
            case BLUE:
                lightColor = new Color(190, 215, 235);
                darkColor = new Color(70, 120, 170);
                borderColor = new Color(30, 60, 100);
                break;
            case GREEN:
                lightColor = new Color(220, 235, 200);
                darkColor = new Color(100, 150, 90);
                borderColor = new Color(50, 90, 50);
                break;
            default:
                lightColor = new Color(240, 217, 181);
                darkColor = new Color(181, 136, 99);
                borderColor = new Color(90, 55, 30);
        }

        // outside 
        g2.setColor(borderColor);
        g2.fillRoundRect(
                margin - 18,
                margin - 18,
                tileSize * 8 + 36,
                tileSize * 8 + 36,
                25,
                25
        );

        // board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                g2.setColor((row + col) % 2 == 0 ? lightColor : darkColor);
                g2.fillRect(
                        margin + col * tileSize,
                        margin + row * tileSize,
                        tileSize,
                        tileSize
                );
            }
        }

        //board side
        g2.setColor(new Color(255, 255, 255, 80));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(margin, margin, tileSize * 8, tileSize * 8);
    }
}