package viewmodel;

import model.Player;
import java.awt.Image;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameViewModel {
    private Player player;
    private final int PLAYER_SPEED = 5;

    private int gamePanelWidth;
    private int gamePanelHeight;

    public static final int STOP_HORIZONTAL = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int STOP_VERTICAL = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;

    private BufferedImage fullSpriteSheet;
    private BufferedImage backgroundImage;
    private int currentFrame = 0;
    private int originalFrameWidth;
    private int originalFrameHeight;
    private int totalFrames;
    private long lastFrameTime;
    private final long FRAME_DELAY = 70;

    private final int SCALE_FACTOR = 5;

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;

        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png");
        if (playerImageUrl != null) {
            try {
                fullSpriteSheet = ImageIO.read(playerImageUrl);
                System.out.println("Sprite sheet berhasil dimuat: " + playerImageUrl);
                originalFrameWidth = fullSpriteSheet.getWidth() / 8;
                originalFrameHeight = fullSpriteSheet.getHeight();
                totalFrames = 8;
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet: " + e.getMessage());
                fullSpriteSheet = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet tidak ditemukan di assets/soldier-walk.png.");
            fullSpriteSheet = null;
        }

        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background_cave.png");
        if (backgroundImageUrl != null) {
            try {
                backgroundImage = ImageIO.read(backgroundImageUrl);
                System.out.println("Gambar latar belakang berhasil dimuat: " + backgroundImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage());
                backgroundImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background_cave.png.");
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
                if (player.getVelocityY() == 0) {
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
                if (player.getVelocityX() == 0) {
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

        // Removed boundary checks here
        // The player can now move freely beyond the panel dimensions.

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) {
                currentFrame = (currentFrame + 1) % totalFrames;
            } else {
                currentFrame = 0;
            }
            lastFrameTime = currentTime;
        }
    }

    public Image getCurrentPlayerFrame() {
        if (fullSpriteSheet == null || originalFrameWidth == 0 || originalFrameHeight == 0) {
            return null;
        }
        int sourceX = currentFrame * originalFrameWidth;
        return fullSpriteSheet.getSubimage(sourceX, 0, originalFrameWidth, originalFrameHeight);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public int getPlayerX() { return player.getPosX(); }
    public int getPlayerY() { return player.getPosY(); }
    public int getPlayerDisplayWidth() { return player.getDisplayWidth(); }
    public int getPlayerDisplayHeight() { return player.getDisplayHeight(); }
    public int getPlayerVelocityX() { return player.getVelocityX(); }
}