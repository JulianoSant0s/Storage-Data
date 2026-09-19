package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;
    Thread gameThread;

    KeyHandler keyHandler = new KeyHandler();
    Player player = new Player(this, keyHandler);
    TileManager tileManager = new TileManager(this);
    BoxManager boxManager = new BoxManager(this);
    InventoryUI inventoryUI = new InventoryUI(this);

    public int gameState;
    public final int menuState = 0;
    public final int playState = 1;
    public final int inventoryState = 2;
    public final int loadingState = 3;

    // Menu de seleção de mapa
    String[] mapOptions = {"Armazém"}; // adicione novos mapas aqui conforme forem criados
    int mapSelected = 0;

    // Fonte pixel (carregada de res/fonts). Se o arquivo não existir ainda,
    // cai no fallback monoespaçado sem quebrar o jogo.
    Font pixelFont = loadPixelFont();

    private Font loadPixelFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/PressStart2P.ttf")) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is);
            }
        } catch (Exception e) {
            System.out.println("Fonte pixel não encontrada, usando fallback monoespaçado.");
        }
        return new Font("Monospaced", Font.BOLD, 12);
    }

    public GamePanel() {
        this.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        gameState = menuState;

        SaveManager.load(boxManager); //para salvar os itens
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {

        if (gameState == menuState) {
            updateMenuSelection();
        }

        if (gameState == loadingState) {
            // Por enquanto o carregamento é instantâneo (tiles/sprites já
            // foram lidos na construção do GamePanel). Quando os mapas
            // ficarem maiores/mais numerosos, o carregamento de verdade
            // (leitura de arquivos) entra aqui antes de mudar pro playState.
            gameState = playState;
        }

        if (gameState == playState) {
            player.update();
        }

        if (gameState == inventoryState) {
            inventoryUI.updateSelection(keyHandler);
        }

        if (keyHandler.ePressed) {
            handleInteraction();
            keyHandler.ePressed = false;
        }

        if (keyHandler.enterPressed) {
            if (gameState == menuState) {
                gameState = loadingState;
            } else if (gameState == inventoryState) {
                inventoryUI.confirmSelection();
            }
            keyHandler.enterPressed = false;
        }

        if (keyHandler.deletePressed) {
            if (gameState == inventoryState) {
                inventoryUI.clearSelected();
            }
            keyHandler.deletePressed = false;
        }

        if (keyHandler.escPressed) {
            if (gameState == playState) {
                gameState = menuState;
            }
            keyHandler.escPressed = false;
        }
    }

    public void updateMenuSelection() {

        if (keyHandler.menuUpPressed) {
            mapSelected--;
            if (mapSelected < 0) {
                mapSelected = mapOptions.length - 1;
            }
            keyHandler.menuUpPressed = false;
        }

        if (keyHandler.menuDownPressed) {
            mapSelected++;
            if (mapSelected >= mapOptions.length) {
                mapSelected = 0;
            }
            keyHandler.menuDownPressed = false;
        }

        // reseta os laterais mesmo sem uso aqui, pra não "vazar" pro próximo estado
        keyHandler.menuLeftPressed = false;
        keyHandler.menuRightPressed = false;
    }

    public void handleInteraction() {

        if (gameState == inventoryState) {
            gameState = playState;
            inventoryUI.currentBox = null;
            return;
        }

        int playerCol = player.x / tileSize;
        int playerRow = player.y / tileSize;

        Box box = boxManager.getBoxAt(playerCol, playerRow);

        if (box != null) {
            gameState = inventoryState;
            inventoryUI.currentBox = box;
            inventoryUI.selectedIndex = 0;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (gameState == menuState) {
            drawMenuScreen(g2);
            g2.dispose();
            return;
        }

        if (gameState == loadingState) {
            drawLoadingScreen(g2);
            g2.dispose();
            return;
        }

        tileManager.draw(g2);
        player.draw(g2);

        if (gameState == playState) {
            int playerCol = player.x / tileSize;
            int playerRow = player.y / tileSize;

            if (boxManager.getBoxAt(playerCol, playerRow) != null) {
                g2.setColor(Color.white);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString("Aperte E para abrir", player.x - 10, player.y - 10);
            }
        }

        if (gameState == inventoryState) {
            inventoryUI.draw(g2);
        }

        g2.dispose();
    }

    // Paleta "pergaminho" pixelado, nos mesmos tons terrosos do resto do jogo
    static final Color PARCHMENT_BASE = new Color(0xD9, 0xC4, 0x95);
    static final Color PARCHMENT_FLECK = new Color(0xC7, 0xAC, 0x7E);
    static final Color FRAME_DARK = new Color(0x5A, 0x3A, 0x20);
    static final Color FRAME_CORNER = new Color(0x3A, 0x24, 0x12);
    static final Color FRAME_HIGHLIGHT = new Color(0x7A, 0x55, 0x35);
    static final Color INK = new Color(0x4A, 0x2F, 0x1A);
    static final Color SELECTED_INK = new Color(0x7A, 0x1F, 0x1F);
    static final Color MUTED_INK = new Color(0x6B, 0x54, 0x3A);

    private void setupPixelRendering(Graphics2D g2) {
        // Desliga suavização - é isso que faz o texto e as bordas ficarem
        // "quadriculados" em vez de suaves, combinando com os sprites 32x32.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    // Desenha o fundo pergaminho + moldura em blocos, comum ao menu e ao loading
    private void drawParchmentPanel(Graphics2D g2) {

        setupPixelRendering(g2);

        int margin = 40;
        int panelW = screenWidth - margin * 2;
        int panelH = screenHeight - margin * 2;
        int border = 16;

        g2.setColor(PARCHMENT_BASE);
        g2.fillRect(margin, margin, panelW, panelH);

        // "flecks" - pequenos blocos irregulares simulando dither, não um degradê liso
        g2.setColor(PARCHMENT_FLECK);
        int[][] flecks = {
                {8, 8}, {24, 8}, {48, 16}, {80, 8}, {120, 16}, {160, 8},
                {16, panelH - 24}, {60, panelH - 16}, {110, panelH - 24}, {150, panelH - 16}
        };
        for (int[] f : flecks) {
            g2.fillRect(margin + f[0], margin + f[1], 8, 8);
        }

        // moldura externa (blocos maciços)
        g2.setColor(FRAME_DARK);
        g2.fillRect(margin, margin, panelW, border);
        g2.fillRect(margin, margin + panelH - border, panelW, border);
        g2.fillRect(margin, margin, border, panelH);
        g2.fillRect(margin + panelW - border, margin, border, panelH);

        // cantos tipo "rebite"
        g2.setColor(FRAME_CORNER);
        g2.fillRect(margin, margin, border, border);
        g2.fillRect(margin + panelW - border, margin, border, border);
        g2.fillRect(margin, margin + panelH - border, border, border);
        g2.fillRect(margin + panelW - border, margin + panelH - border, border, border);

        // filete interno mais claro, só no topo/esquerda, pra dar profundidade
        g2.setColor(FRAME_HIGHLIGHT);
        g2.fillRect(margin + border + 8, margin + border + 8, panelW - border * 2 - 16, 6);
        g2.fillRect(margin + border + 8, margin + border + 8, 6, panelH - border * 2 - 16);
    }

    // Desenha texto "de verdade" pixelado: renderiza pequeno (baseFontSize)
    // e amplia sem suavizar (pixelScale), igual ao esquema
    // originalTileSize x scale que você já usa pros tiles. Só desligar
    // antialiasing não é suficiente - uma fonte vetorial grande sem AA fica
    // só "serrilhada", não com blocos de pixel de verdade.
    private void drawPixelText(Graphics2D g2, String text, int x, int y, int baseFontSize, int pixelScale, Color color) {

        Font smallFont = pixelFont.deriveFont(Font.PLAIN, (float) baseFontSize);

        BufferedImage measure = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureG2 = measure.createGraphics();
        measureG2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        FontMetrics fm = measureG2.getFontMetrics(smallFont);
        int textWidth = Math.max(fm.stringWidth(text), 1);
        int textHeight = fm.getAscent() + fm.getDescent();
        measureG2.dispose();

        BufferedImage textImg = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = textImg.createGraphics();
        tg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        tg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        // ESSENCIAL: sem isso os glyphs ficam em posições de sub-pixel e a
        // borda semi-transparente vira um "fantasma" duplicado ao ampliar.
        tg.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        tg.setFont(smallFont);
        tg.setColor(color);
        tg.drawString(text, 0, fm.getAscent());
        tg.dispose();

        // desenha ampliado com nearest-neighbor (setado em setupPixelRendering) -
        // é isso que cria os blocos quadrados em vez de suavizar ao escalar
        g2.drawImage(textImg, x, y, textWidth * pixelScale, textHeight * pixelScale, null);
    }

    public void drawMenuScreen(Graphics2D g2) {

        drawParchmentPanel(g2);

        drawPixelText(g2, "ESCOLHA O MAPA", 100, 84, 10, 3, INK);

        g2.setColor(FRAME_HIGHLIGHT);
        g2.fillRect(100, 130, screenWidth - 200, 4);

        for (int i = 0; i < mapOptions.length; i++) {

            int itemY = 160 + (i * 48);

            if (i == mapSelected) {
                g2.setColor(SELECTED_INK);
                int[] xPts = {90, 90, 106};
                int[] yPts = {itemY, itemY + 12, itemY + 6};
                g2.fillPolygon(xPts, yPts, 3);
                drawPixelText(g2, mapOptions[i].toUpperCase(), 118, itemY - 2, 13, 3, SELECTED_INK);
            } else {
                drawPixelText(g2, mapOptions[i].toUpperCase(), 118, itemY - 2, 13, 3, MUTED_INK);
            }
        }

        drawPixelText(g2, "W/S NAVEGAR - ENTER CONFIRMAR", 100, screenHeight - 140, 9, 3, MUTED_INK);
    }

    public void drawLoadingScreen(Graphics2D g2) {

        drawParchmentPanel(g2);

        drawPixelText(g2, "CARREGANDO...", 100, screenHeight / 2, 9, 3, INK);
    }
}