package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TileManager {

    GamePanel gamePanel;
    Tile[] tile;
    int[][] mapTileNum;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        tile = new Tile[4];
        mapTileNum = new int[gamePanel.maxScreenCol][gamePanel.maxScreenRow];

        getTileColors();
        getTileImages();
        loadMap();
    }

    public void getTileColors() {
        tile[0] = new Tile();
        tile[0].color = new Color(180, 180, 180); // chão

        tile[1] = new Tile();
        tile[1].color = new Color(60, 60, 60);    // parede
        tile[1].collision = true;

        tile[2] = new Tile();
        tile[2].color = new Color(150, 100, 50);  // caixa (reserva, caso a imagem falhe)
        tile[2].drawScale = 1.6;

        tile[3] = new Tile();
        tile[3].color = new Color(110, 70, 20);   // caixa dupla
        tile[3].drawScale = 1.6;
    }

    public void getTileImages() {
        try {
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/box.png"));
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/cabinet.png"));
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/floor.png"));
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/wall.png"));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {

        int[][] map = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,3,0,0,0,2,1,0,0,0,1,2,0,0,3,1},
                {1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
                {1,2,0,2,0,2,1,0,0,0,1,2,0,0,2,1},
                {1,1,1,1,0,1,1,0,0,0,1,1,0,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,1,1,1,0,1,1,0,0,0,1,1,0,1,1,1},
                {1,2,0,2,0,2,1,0,0,0,1,2,0,0,2,1},
                {1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
                {1,3,0,0,0,2,1,0,0,0,1,2,0,0,3,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        for (int row = 0; row < gamePanel.maxScreenRow; row++) {
            for (int col = 0; col < gamePanel.maxScreenCol; col++) {
                mapTileNum[col][row] = map[row][col];
            }
        }
    }

    public void draw(Graphics2D g2) {

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gamePanel.maxScreenCol && row < gamePanel.maxScreenRow) {

            int tileNum = mapTileNum[col][row];
            Tile currentTile = tile[tileNum];

            if (currentTile.image != null) {

                // desenha o CHÃO DE VERDADE por baixo (imagem,)
                if (tileNum != 0) {
                    if (tile[0].image != null) {
                        g2.drawImage(tile[0].image, x, y, gamePanel.tileSize, gamePanel.tileSize, null);
                    } else {
                        g2.setColor(tile[0].color);
                        g2.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);
                    }
                }

                int drawSize = (int) (gamePanel.tileSize * currentTile.drawScale);
                int offset = (drawSize - gamePanel.tileSize) / 2;
                g2.drawImage(currentTile.image, x - offset, y - offset, drawSize, drawSize, null);

            } else {
                g2.setColor(currentTile.color);
                g2.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);
            }

            col++;
            x += gamePanel.tileSize;

            if (col == gamePanel.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gamePanel.tileSize;
            }
        }
    }
}