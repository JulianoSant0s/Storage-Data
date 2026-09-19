package main;

import java.util.ArrayList;
import java.util.List;

public class BoxManager {

    GamePanel gamePanel;
    public List<Box> boxes = new ArrayList<>();

    public BoxManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        createBoxes();
    }

    public void createBoxes() {
        int[][] mapTileNum = gamePanel.tileManager.mapTileNum;

        for (int col = 0; col < gamePanel.maxScreenCol; col++) {
            for (int row = 0; row < gamePanel.maxScreenRow; row++) {

                int tileNum = mapTileNum[col][row];

                if (tileNum == 2) { // caixa normal
                    boxes.add(new Box(col, row, 9));
                }
                if (tileNum == 3) { // caixa dupla
                    boxes.add(new Box(col, row, 18));
                }
            }
        }
    }

    public Box getBoxAt(int col, int row) {
        for (Box box : boxes) {
            if (box.col == col && box.row == row) {
                return box;
            }
        }
        return null;
    }
}