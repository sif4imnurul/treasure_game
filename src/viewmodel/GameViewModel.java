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

    private int gamePanelWidth;
    private int gamePanelHeight;

    // Konstanta untuk arah gerakan
    public static final int STOP_HORIZONTAL = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int STOP_VERTICAL = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;

    private BufferedImage fullSpriteSheet;
    private BufferedImage backgroundImage; // Tambah variabel untuk gambar latar belakang
    private int currentFrame = 0;
    private int originalFrameWidth;
    private int originalFrameHeight;
    private int totalFrames;
    private long lastFrameTime;
    private final long FRAME_DELAY = 70; // Mengurangi delay untuk animasi lebih halus (misal 70ms)

    private final int SCALE_FACTOR = 4;

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;

        // Memuat sprite sheet pemain
        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png");
        if (playerImageUrl != null) {
            try {
                fullSpriteSheet = ImageIO.read(playerImageUrl);
                System.out.println("Sprite sheet berhasil dimuat dari: " + playerImageUrl);

                // Sesuaikan ini dengan sprite sheet Anda yang sebenarnya
                originalFrameWidth = fullSpriteSheet.getWidth() / 9; // Jika ada 9 frame
                originalFrameHeight = fullSpriteSheet.getHeight();
                totalFrames = 9;

            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet: " + e.getMessage());
                fullSpriteSheet = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet tidak ditemukan di assets/soldier-walk.png. Pastikan jalur dan penempatan file benar.");
            fullSpriteSheet = null;
        }

        // Memuat gambar latar belakang
        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background_cave.png"); // Asumsi nama file: background.png
        if (backgroundImageUrl != null) {
            try {
                backgroundImage = ImageIO.read(backgroundImageUrl);
                System.out.println("Gambar latar belakang berhasil dimuat dari: " + backgroundImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage());
                backgroundImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background_cave.png. Pastikan jalur dan penempatan file benar.");
            backgroundImage = null;
        }


        int playerDisplayWidth = originalFrameWidth * SCALE_FACTOR;
        int playerDisplayHeight = originalFrameHeight * SCALE_FACTOR;

        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2),
                panelHeight / 2 - (playerDisplayHeight / 2),
                playerDisplayWidth,
                playerDisplayHeight,
                fullSpriteSheet
        );

        lastFrameTime = System.currentTimeMillis();
    }

    public void setPlayerMovementDirection(int direction) {
        switch (direction) {
            case STOP_HORIZONTAL:
                player.setVelocityX(0);
                // Ketika berhenti horizontal, reset animasi ke frame idle (frame 0)
                if (player.getVelocityY() == 0) { // Hanya jika tidak bergerak vertikal juga
                    currentFrame = 0;
                }
                break;
            case LEFT:
                player.setVelocityX(-PLAYER_SPEED);
                break;
            case RIGHT:
                player.setVelocityX(PLAYER_SPEED);
                break;
            case STOP_VERTICAL:
                player.setVelocityY(0);
                // Ketika berhenti vertikal, reset animasi ke frame idle (frame 0)
                if (player.getVelocityX() == 0) { // Hanya jika tidak bergerak horizontal juga
                    currentFrame = 0;
                }
                break;
            case UP:
                player.setVelocityY(-PLAYER_SPEED);
                break;
            case DOWN:
                player.setVelocityY(PLAYER_SPEED);
                break;
        }
    }

    public void updateGame() {
        player.updatePosition();

        int newX = player.getPosX();
        int newY = player.getPosY();

        newX = Math.max(0, Math.min(newX, gamePanelWidth - player.getDisplayWidth()));
        newY = Math.max(0, Math.min(newY, gamePanelHeight - player.getDisplayHeight()));

        player.setPosX(newX);
        player.setPosY(newY);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) {
                currentFrame = (currentFrame + 1) % totalFrames;
            } else {
                currentFrame = 0; // Jika tidak bergerak, tampilkan frame pertama (idle)
            }
            lastFrameTime = currentTime;
        }
    }

    public Image getCurrentPlayerFrame() {
        if (fullSpriteSheet == null || originalFrameWidth == 0 || originalFrameHeight == 0) {
            return null;
        }
        int sourceX = currentFrame * originalFrameWidth;
        // Mengembalikan sub-gambar (frame) dari sprite sheet penuh
        return fullSpriteSheet.getSubimage(sourceX, 0, originalFrameWidth, originalFrameHeight);
    }

    // Getter baru untuk gambar latar belakang
    public Image getBackgroundImage() {
        return backgroundImage;
    }

    // --- Getter untuk properti pemain (digunakan oleh GameView) ---
    public int getPlayerX() { return player.getPosX(); }
    public int getPlayerY() { return player.getPosY(); }
    public int getPlayerDisplayWidth() { return player.getDisplayWidth(); }
    public int getPlayerDisplayHeight() { return player.getDisplayHeight(); }
    public int getPlayerVelocityX() { return player.getVelocityX(); } // Tambahkan getter untuk velocityX
}