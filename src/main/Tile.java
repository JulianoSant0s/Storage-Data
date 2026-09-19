package main;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image; // usaremos depois, quando tiver sprites
    public Color color;         // usamos por enquanto
    public boolean collision = false;
    public double drawScale = 1.0; //tamanho do tile
}