package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("SD - Storage Data");

        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);

        window.pack(); // ajusta o tamanho da janela ao tamanho do painel
        window.setLocationRelativeTo(null); // centraliza na tela
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}