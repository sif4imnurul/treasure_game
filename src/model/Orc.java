package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class Orc { // Nama kelas diubah menjadi Orc
    private int posX;
    private int posY;
    private int displayWidth;
    private int displayHeight;
    private BufferedImage fullSpriteSheet;
    private int velocityX;

    private int currentFrame = 0;
    private int originalFrameWidth;
    private int totalFrames;
    private long lastFrameTime;
    private final long FRAME_DELAY = 100; // Delay animasi Orc

    public Orc(int posX, int posY, int displayWidth, int displayHeight, BufferedImage fullSpriteSheet, int initialVelocityX, int originalFrameWidth, int totalFrames) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.fullSpriteSheet = fullSpriteSheet;
        this.velocityX = initialVelocityX;

        this.originalFrameWidth = originalFrameWidth;
        this.totalFrames = totalFrames;
        this.lastFrameTime = System.currentTimeMillis();
    }

    // Getter
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public int getVelocityX() { return velocityX; }

    public Image getCurrentFrameImage() {
        if (fullSpriteSheet == null || originalFrameWidth == 0) {
            return null;
        }
        int sourceX = currentFrame * originalFrameWidth;
        return fullSpriteSheet.getSubimage(sourceX, 0, originalFrameWidth, fullSpriteSheet.getHeight());
    }

    // Setter
    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }

    public void updatePosition(int panelWidth) {
        this.posX += this.velocityX;

        if (this.posX <= 0 || this.posX + this.displayWidth >= panelWidth) {
            this.velocityX *= -1;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            currentFrame = (currentFrame + 1) % totalFrames;
            lastFrameTime = currentTime;
        }
    }

    // Untuk deteksi kolisi (misalnya dengan laso)
    public Rectangle getBounds() {
        return new Rectangle(posX, posY, displayWidth, displayHeight);
    }
}