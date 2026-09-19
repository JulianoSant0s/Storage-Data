package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player {

    GamePanel gamePanel;
    KeyHandler keyHandler;

    public int x, y;
    public int speed;

    String direction = "down";

    BufferedImage down1;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
    }

    public void getPlayerImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {

        int newX = x;
        int newY = y;

        if (keyHandler.upPressed) {
            direction = "up";
            newY -= speed;
        }
        if (keyHandler.downPressed) {
            direction = "down";
            newY += speed;
        }
        if (keyHandler.leftPressed) {
            direction = "left";
            newX -= speed;
        }
        if (keyHandler.rightPressed) {
            direction = "right";
            newX += speed;
        }

        // Só move no eixo X se não colidir
        if (!checkCollision(newX, y)) {
            x = newX;
        }

        // Só move no eixo Y se não colidir
        if (!checkCollision(x, newY)) {
            y = newY;
        }

        // Impede o personagem de sair dos limites da tela
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > gamePanel.screenWidth - gamePanel.tileSize) {
            x = gamePanel.screenWidth - gamePanel.tileSize;
        }
        if (y > gamePanel.screenHeight - gamePanel.tileSize) {
            y = gamePanel.screenHeight - gamePanel.tileSize;
        }
    }

    public boolean checkCollision(int testX, int testY) {
        int leftCol = testX / gamePanel.tileSize;
        int rightCol = (testX + gamePanel.tileSize - 1) / gamePanel.tileSize;
        int topRow = testY / gamePanel.tileSize;
        int bottomRow = (testY + gamePanel.tileSize - 1) / gamePanel.tileSize;

        int tileNum1 = gamePanel.tileManager.mapTileNum[leftCol][topRow];
        int tileNum2 = gamePanel.tileManager.mapTileNum[rightCol][topRow];
        int tileNum3 = gamePanel.tileManager.mapTileNum[leftCol][bottomRow];
        int tileNum4 = gamePanel.tileManager.mapTileNum[rightCol][bottomRow];

        return gamePanel.tileManager.tile[tileNum1].collision
                || gamePanel.tileManager.tile[tileNum2].collision
                || gamePanel.tileManager.tile[tileNum3].collision
                || gamePanel.tileManager.tile[tileNum4].collision;
    }

    public void draw(Graphics2D g2) {

        double scale = 1.3; // ajuste esse valor até o tamanho ficar bom

        int drawWidth = (int) (gamePanel.tileSize * scale);
        int drawHeight = (int) (gamePanel.tileSize * scale);

        int offsetX = (drawWidth - gamePanel.tileSize) / 2;  // cresce igual pros dois lados
        int offsetY = drawHeight - gamePanel.tileSize;        // cresce só pra cima, pés ficam no lugar

        g2.drawImage(down1, x - offsetX, y - offsetY, drawWidth, drawHeight, null);
    }
}