package model;

import java.awt.Image;
import java.awt.image.BufferedImage; // Perlu untuk ImageIO.read() jika ingin langsung dari sini
import java.awt.Rectangle;

public class Coin { // Nama kelas diubah menjadi Coin
    private int posX;
    private int posY;
    private int displayWidth;
    private int displayHeight;
    private BufferedImage image; // Cukup satu gambar, bukan sprite sheet
    private int velocityX;

    // Konstruktor disederhanakan
    public Coin(int posX, int posY, int displayWidth, int displayHeight, BufferedImage image, int initialVelocityX) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.image = image; // Langsung menggunakan BufferedImage
        this.velocityX = initialVelocityX;
    }

    // Getter
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; }
    public int getDisplayHeight() { return displayHeight; }
    public int getVelocityX() { return velocityX; }

    public Image getImage() { // Getter untuk gambar tunggal
        return image;
    }

    // Setter
    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }

    public void updatePosition(int panelWidth) {
        this.posX += this.velocityX;

        if (this.posX <= 0 || this.posX + this.displayWidth >= panelWidth) {
            this.velocityX *= -1; // Balik arah
        }
        // Tidak ada logika animasi frame karena ini single image
    }

    // Untuk deteksi kolisi
    public Rectangle getBounds() {
        return new Rectangle(posX, posY, displayWidth, displayHeight);
    }
}