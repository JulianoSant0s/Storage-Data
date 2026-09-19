package main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class InventoryUI {

    GamePanel gamePanel;
    public Box currentBox;
    public int selectedIndex = 0;

    static final int SLOTS_PER_ROW = 9;

    public InventoryUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void updateSelection(KeyHandler keyHandler) {
        if (currentBox == null) return;

        int totalSlots = currentBox.slotCount;

        if (keyHandler.menuLeftPressed) {
            if (selectedIndex % SLOTS_PER_ROW > 0) selectedIndex--;
            keyHandler.menuLeftPressed = false;
        }
        if (keyHandler.menuRightPressed) {
            if (selectedIndex % SLOTS_PER_ROW < SLOTS_PER_ROW - 1 && selectedIndex + 1 < totalSlots) selectedIndex++;
            keyHandler.menuRightPressed = false;
        }
        if (keyHandler.menuUpPressed) {
            if (selectedIndex - SLOTS_PER_ROW >= 0) selectedIndex -= SLOTS_PER_ROW;
            keyHandler.menuUpPressed = false;
        }
        if (keyHandler.menuDownPressed) {
            if (selectedIndex + SLOTS_PER_ROW < totalSlots) selectedIndex += SLOTS_PER_ROW;
            keyHandler.menuDownPressed = false;
        }
    }

    public void confirmSelection() {
        if (currentBox == null) return;

        ItemSlot slot = currentBox.slots[selectedIndex];

        if (slot.filePath == null) {
            openFileChooser(slot);
        } else {
            openFile(slot.filePath);
        }
    }

    private void openFileChooser(ItemSlot slot) {
        // JFileChooser precisa rodar na thread do Swing (EDT), não na thread do jogo
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(gamePanel);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    slot.filePath = selectedFile.getAbsolutePath();
                    slot.itemName = selectedFile.getName();
                    SaveManager.save(gamePanel.boxManager);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(String filePath) {
        try {
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearSelected() {
        if (currentBox == null) return;

        ItemSlot slot = currentBox.slots[selectedIndex];
        slot.itemName = null;
        slot.filePath = null;
        SaveManager.save(gamePanel.boxManager);
    }

    public void draw(Graphics2D g2) {

        if (currentBox == null) return;

        int slotSize = 50;
        int padding = 10;

        int totalSlots = currentBox.slotCount;
        int rows = (int) Math.ceil((double) totalSlots / SLOTS_PER_ROW);

        int panelWidth = SLOTS_PER_ROW * (slotSize + padding) + padding;
        int panelHeight = rows * (slotSize + padding) + padding + 40;

        int panelX = (gamePanel.screenWidth - panelWidth) / 2;
        int panelY = (gamePanel.screenHeight - panelHeight) / 2;

        g2.setColor(new Color(50, 24, 5, 200));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g2.setColor(Color.white);
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Inventário (" + totalSlots + " espaços) - Enter adiciona/abre | E para sair", panelX + padding, panelY + 25);

        int startY = panelY + 40;

        for (int i = 0; i < totalSlots; i++) {
            int col = i % SLOTS_PER_ROW;
            int row = i / SLOTS_PER_ROW;

            int x = panelX + padding + col * (slotSize + padding);
            int y = startY + padding + row * (slotSize + padding);

            g2.setColor(new Color(101, 54, 24, 255));
            g2.fillRect(x, y, slotSize, slotSize);

            g2.setColor(i == selectedIndex ? Color.yellow : Color.white);
            g2.drawRect(x, y, slotSize, slotSize);

            ItemSlot slot = currentBox.slots[i];
            if (slot.itemName != null) {
                g2.setColor(Color.white);
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                String name = slot.itemName;
                if (name.length() > 10) name = name.substring(0, 9) + "...";
                g2.drawString(name, x + 2, y + slotSize / 2);
            }
        }
    }
}