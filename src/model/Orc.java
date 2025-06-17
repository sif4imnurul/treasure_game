package model;

import java.awt.image.BufferedImage;

public class Orc {
    private int posX; // posisi horizontal orc
    private int posY; // posisi vertikal orc
    private int displayWidth; // lebar orc
    private int displayHeight; // tinggi orc
    private BufferedImage fullSpriteSheet; // gambar spritesheet 
    private int velocityX; // kecepatan orc hor
    private int currentFrame; // frame yang ditampilkan
    private int originalFrameWidth; // ukuran 1 gambar di spritesheet
    private int totalFrames; // total frame orc
    private long lastFrameTime; // timestamp frame terakhir update
    private final long FrameDelay = 100; // jeda frame dan animasi

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
        this.currentFrame = 0; 
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public int getVelocityX() { return velocityX; }
    public BufferedImage getFullSpriteSheet() { return fullSpriteSheet; }
    public int getCurrentFrame() { return currentFrame; }
    public int getOriginalFrameWidth() { return originalFrameWidth; }
    public int getTotalFrames() { return totalFrames; }
    public long getLastFrameTime() { return lastFrameTime; }
    public long getFrameDelay() { return FrameDelay; }

    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; }
    public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; }
    public void setFullSpriteSheet(BufferedImage fullSpriteSheet) { this.fullSpriteSheet = fullSpriteSheet; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }
    public void setCurrentFrame(int currentFrame) { this.currentFrame = currentFrame; }
    public void setOriginalFrameWidth(int originalFrameWidth) { this.originalFrameWidth = originalFrameWidth; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }
    public void setLastFrameTime(long lastFrameTime) { this.lastFrameTime = lastFrameTime; }
}