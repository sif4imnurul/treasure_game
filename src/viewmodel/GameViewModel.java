package viewmodel;

import model.Player;
import java.awt.Image;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameViewModel {
    private Player player;
    private final int PLAYER_SPEED = 5; // Kecepatan gerakan pemain

    private int gamePanelWidth; // Lebar panel game
    private int gamePanelHeight; // Tinggi panel game

    // Konstanta untuk arah gerakan
    public static final int STOP_HORIZONTAL = 0; //
    public static final int LEFT = 1; //
    public static final int RIGHT = 2; //
    public static final int STOP_VERTICAL = 3; //
    public static final int UP = 4; //
    public static final int DOWN = 5; //

    private BufferedImage fullSpriteSheet; // Sprite sheet penuh
    private BufferedImage backgroundImage; // Gambar latar belakang
    private int currentFrame = 0; // Frame animasi saat ini
    private int originalFrameWidth; // Lebar frame asli
    private int originalFrameHeight; // Tinggi frame asli
    private int totalFrames; // Total frame dalam sprite sheet
    private long lastFrameTime; // Waktu terakhir update frame
    private final long FRAME_DELAY = 70; // Mengatur delay frame animasi ke 70ms (sekitar 14 FPS)

    private final int SCALE_FACTOR = 4; // Faktor skala gambar

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth; //
        this.gamePanelHeight = panelHeight; //

        // Memuat sprite sheet pemain
        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png"); //
        if (playerImageUrl != null) { //
            try { //
                fullSpriteSheet = ImageIO.read(playerImageUrl); //
                System.out.println("Sprite sheet berhasil dimuat: " + playerImageUrl); //

                // UBAH KEDUA BARIS INI DARI 9 MENJADI 8
                originalFrameWidth = fullSpriteSheet.getWidth() / 8; // Lebar frame jika ada 8 frame
                originalFrameHeight = fullSpriteSheet.getHeight(); // Tinggi frame
                totalFrames = 8; // Total frame

            } catch (IOException e) { //
                System.err.println("ERROR: Gagal memuat sprite sheet: " + e.getMessage()); //
                fullSpriteSheet = null; //
            }
        } else { //
            System.err.println("ERROR: Sprite sheet tidak ditemukan di assets/soldier-walk.png."); //
            fullSpriteSheet = null; //
        }

        // Memuat gambar latar belakang
        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background_cave.png"); //
        if (backgroundImageUrl != null) { //
            try { //
                backgroundImage = ImageIO.read(backgroundImageUrl); //
                System.out.println("Gambar latar belakang berhasil dimuat: " + backgroundImageUrl); //
            } catch (IOException e) { //
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage()); //
                backgroundImage = null; //
            }
        } else { //
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background_cave.png."); //
            backgroundImage = null; //
        }


        int playerDisplayWidth = originalFrameWidth * SCALE_FACTOR; // Lebar tampilan pemain
        int playerDisplayHeight = originalFrameHeight * SCALE_FACTOR; // Tinggi tampilan pemain

        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2), // Posisi X awal di tengah
                panelHeight / 2 - (playerDisplayHeight / 2), // Posisi Y awal di tengah
                playerDisplayWidth, //
                playerDisplayHeight, //
                fullSpriteSheet //
        );

        lastFrameTime = System.currentTimeMillis(); // Inisialisasi waktu frame terakhir
    }

    public void setPlayerMovementDirection(int direction) {
        switch (direction) { //
            case STOP_HORIZONTAL: //
                player.setVelocityX(0); // Hentikan gerakan X
                if (player.getVelocityY() == 0) { // Jika tidak bergerak vertikal juga
                    currentFrame = 0; // Kembali ke frame idle
                }
                break;
            case LEFT: //
                player.setVelocityX(-PLAYER_SPEED); // Gerak kiri
                break;
            case RIGHT: //
                player.setVelocityX(PLAYER_SPEED); // Gerak kanan
                break;
            case STOP_VERTICAL: //
                player.setVelocityY(0); // Hentikan gerakan Y
                if (player.getVelocityX() == 0) { // Jika tidak bergerak horizontal juga
                    currentFrame = 0; // Kembali ke frame idle
                }
                break;
            case UP: //
                player.setVelocityY(-PLAYER_SPEED); // Gerak atas
                break;
            case DOWN: //
                player.setVelocityY(PLAYER_SPEED); // Gerak bawah
                break;
        }
    }

    public void updateGame() {
        player.updatePosition(); // Perbarui posisi pemain

        // Tidak ada batasan posisi, pemain bisa bergerak bebas di luar frame

        long currentTime = System.currentTimeMillis(); // Waktu saat ini
        if (currentTime - lastFrameTime > FRAME_DELAY) { // Jika sudah waktunya update frame
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) { // Jika pemain bergerak
                currentFrame = (currentFrame + 1) % totalFrames; // Lanjut animasi frame berikutnya
            } else {
                currentFrame = 0; // Jika diam, tampilkan frame pertama (idle)
            }
            lastFrameTime = currentTime; // Perbarui waktu frame terakhir
        }
    }

    public Image getCurrentPlayerFrame() {
        if (fullSpriteSheet == null || originalFrameWidth == 0 || originalFrameHeight == 0) { // Cek apakah sprite sheet valid
            return null; //
        }
        int sourceX = currentFrame * originalFrameWidth; // Hitung posisi X frame saat ini
        return fullSpriteSheet.getSubimage(sourceX, 0, originalFrameWidth, originalFrameHeight); // Ambil sub-gambar frame
    }

    // Getter untuk gambar latar belakang
    public Image getBackgroundImage() {
        return backgroundImage; //
    }

    // Getter untuk properti pemain
    public int getPlayerX() { return player.getPosX(); } //
    public int getPlayerY() { return player.getPosY(); } //
    public int getPlayerDisplayWidth() { return player.getDisplayWidth(); } //
    public int getPlayerDisplayHeight() { return player.getDisplayHeight(); } //
    public int getPlayerVelocityX() { return player.getVelocityX(); } //
}