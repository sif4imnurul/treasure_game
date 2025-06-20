package model;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Treasure {
    private int posX;             // posisi horizontal harta karun
    private int posY;             // posisi vertikal harta karun
    private int displayWidth;     // lebar harta karun
    private int displayHeight;    // tinggi harta karun
    private BufferedImage image;  // gambar harta karun
    private int velocityX;        // kecepatan harta karun horizontal
    private boolean isCollected;  // status terkumpul
    private int targetX;          // target posisi x
    private int targetY;          // target posisi y
    private int value;            // nilai skor

    public Treasure(int posX, int posY, int displayWidth, int displayHeight, BufferedImage image, int initialVelocityX, int value) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.image = image;
        this.velocityX = initialVelocityX;
        this.isCollected = false;
        this.targetX = 0;
        this.targetY = 0;
        this.value = value;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public int getVelocityX() { return velocityX; }
    public Image getImage() { return image; }
    public boolean isCollected() { return isCollected; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public int getValue() { return value; }


    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; }
    public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; }
    public void setImage(BufferedImage image) { this.image = image; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }
    public void setCollected(boolean collected) { this.isCollected = collected; }
    public void setTargetX(int targetX) { this.targetX = targetX; }
    public void setTargetY(int targetY) { this.targetY = targetY; }
    public void setValue(int value) { this.value = value; }
}