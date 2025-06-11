package model;

import java.awt.Image;
import java.awt.image.BufferedImage; // Changed to BufferedImage for sprite sheet

public class Player {
    private int posX;
    private int posY;
    private int displayWidth;
    private int displayHeight;
    private BufferedImage image; // Changed to BufferedImage
    private int velocityX;
    private int velocityY;

    public Player(int posX, int posY, int displayWidth, int displayHeight, BufferedImage image) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.image = image;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public Image getImage() { return image; }
    public int getVelocityX() { return velocityX; }
    public int getVelocityY() { return velocityY; }

    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; }
    public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; }
    public void setImage(BufferedImage image) { this.image = image; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(int velocityY) { this.velocityY = velocityY; }
}