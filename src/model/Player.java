package model;

import java.awt.Image;

public class Player {
    private int posX;
    private int posY;
    private int displayWidth; // Lebar yang akan digunakan untuk menampilkan pemain (sudah diskalakan)
    private int displayHeight; // Tinggi yang akan digunakan untuk menampilkan pemain (sudah diskalakan)
    private Image image; // Ini akan menyimpan seluruh sprite sheet (jika digunakan untuk animasi)

    private int velocityX;
    private int velocityY;

    // Konstruktor untuk inisialisasi pemain
    public Player(int posX, int posY, int displayWidth, int displayHeight, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.displayWidth = displayWidth; // Lebar tampilan (sudah diskalakan)
        this.displayHeight = displayHeight; // Tinggi tampilan (sudah diskalakan)
        this.image = image; // Menginisialisasi dengan gambar sprite sheet penuh
        this.velocityX = 0;
        this.velocityY = 0;
    }

    // --- Getter Methods ---
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getDisplayWidth() { return displayWidth; } // Mengembalikan lebar tampilan pemain (setelah diskalakan)
    public int getDisplayHeight() { return displayHeight; } // Mengembalikan tinggi tampilan pemain (setelah diskalakan)
    public Image getImage() { return image; } // Mengembalikan seluruh gambar (sprite sheet)
    public int getVelocityX() { return velocityX; }
    public int getVelocityY() { return velocityY; }

    // --- Setter Methods ---
    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; } // Mengatur lebar tampilan
    public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; } // Mengatur tinggi tampilan
    public void setImage(Image image) { this.image = image; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(int velocityY) { this.velocityY = velocityY; }

    // Memperbarui posisi pemain berdasarkan kecepatan
    public void updatePosition() {
        this.posX += this.velocityX;
        this.posY += this.velocityY;
    }
}