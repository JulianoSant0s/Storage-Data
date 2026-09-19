package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean ePressed;
    public boolean enterPressed;
    public boolean deletePressed;
    public boolean escPressed;

    // Navegação do menu de inventário (um "toque" por vez, não contínuo)
    public boolean menuUpPressed, menuDownPressed, menuLeftPressed, menuRightPressed;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) { upPressed = true; menuUpPressed = true; }
        if (code == KeyEvent.VK_S) { downPressed = true; menuDownPressed = true; }
        if (code == KeyEvent.VK_A) { leftPressed = true; menuLeftPressed = true; }
        if (code == KeyEvent.VK_D) { rightPressed = true; menuRightPressed = true; }
        if (code == KeyEvent.VK_E) ePressed = true;
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
        if (code == KeyEvent.VK_BACK_SPACE) deletePressed = true;
        if (code == KeyEvent.VK_ESCAPE) escPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        // ePressed, enterPressed, deletePressed, escPressed e menu*Pressed NÃO
        // são resetados aqui — quem consome a ação (GamePanel/InventoryUI)
        // reseta depois de usar
    }
}