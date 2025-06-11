package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class Coin {
    private int posX;
    private int posY;
    private int displayWidth;
    private int displayHeight;
    private BufferedImage image;
    private int velocityX;

    public Coin(int posX, int posY, int displayWidth, int displayHeight, BufferedImage image, int initialVelocityX) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.image = image;
        this.velocityX = initialVelocityX;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public int getVelocityX() { return velocityX; }

    public Image getImage() {
        return image;
    }

    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }

    public void updatePosition(int panelWidth) {
        this.posX += this.velocityX;

        if (this.posX <= 0 || this.posX + this.displayWidth >= panelWidth) {
            this.velocityX *= -1;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(posX, posY, displayWidth, displayHeight);
    }
}