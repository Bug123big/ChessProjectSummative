package Graphics;

import java.awt.*;

public class ChessBoardStyle {

    private Color lightColor = new Color(240, 217, 181);
    private Color darkColor = new Color(181, 136, 99);

    public void drawBoard(Graphics g, int tileSize) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(lightColor);
                } else {
                    g.setColor(darkColor);
                }

                g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }
}