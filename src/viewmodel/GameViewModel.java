package viewmodel;

import model.Player;
import model.Orc;
import model.Coin; // Import Coin
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.Point;
import java.awt.Rectangle;

public class GameViewModel {
    private Player player;
    private Orc singleOrc;
    private List<Coin> coins; // Menggunakan List<Coin>
    private final int PLAYER_SPEED = 5;
    private final int ENEMY_SPEED = 2;
    private final int COIN_COUNT = 5; // Jumlah koin yang akan muncul

    private int gamePanelWidth;
    private int gamePanelHeight;

    private BufferedImage fullSpriteSheetPlayer;
    private BufferedImage fullSpriteSheetOrc;
    private BufferedImage coinImage; // Gambar tunggal untuk koin
    private BufferedImage backgroundImage;

    private int currentFramePlayer = 0;
    private int originalFrameWidthPlayer;
    private int originalFrameHeightPlayer;
    private int totalFramesPlayer;
    private long lastFrameTimePlayer;
    private final long FRAME_DELAY_PLAYER = 70;

    private final int SCALE_FACTOR_PLAYER = 5;
    private final int SCALE_FACTOR_ENEMY = 3; // Untuk orc/koin

    // Lasso/Fishing state
    private boolean isLassoActive = false;
    private Point mousePosition;
    private final int LASSO_RANGE = 200;
    private final int LASSO_GRAB_TOLERANCE = 30;

    // Skor
    private int score = 0;

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;

        // Memuat sprite sheet pemain
        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png");
        if (playerImageUrl != null) {
            try {
                fullSpriteSheetPlayer = ImageIO.read(playerImageUrl);
                System.out.println("Sprite sheet pemain berhasil dimuat: " + playerImageUrl);
                originalFrameWidthPlayer = fullSpriteSheetPlayer.getWidth() / 8;
                originalFrameHeightPlayer = fullSpriteSheetPlayer.getHeight();
                totalFramesPlayer = 8;
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet pemain: " + e.getMessage());
                fullSpriteSheetPlayer = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet pemain tidak ditemukan di assets/soldier-walk.png.");
            fullSpriteSheetPlayer = null;
        }

        // Memuat gambar latar belakang
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

        // Memuat sprite sheet Orc (orc_attack.png)
        URL orcImageUrl = getClass().getClassLoader().getResource("assets/orc_attack.png");
        if (orcImageUrl != null) {
            try {
                fullSpriteSheetOrc = ImageIO.read(orcImageUrl);
                System.out.println("Sprite sheet orc berhasil dimuat: " + orcImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet orc: " + e.getMessage());
                fullSpriteSheetOrc = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet orc tidak ditemukan di assets/orc_attack.png.");
            fullSpriteSheetOrc = null;
        }

        // Memuat gambar Coin (coin.png) - GAMBAR TUNGGAL
        URL coinImageUrl = getClass().getClassLoader().getResource("assets/coin.png"); // Ganti ke coin.png
        if (coinImageUrl != null) {
            try {
                coinImage = ImageIO.read(coinImageUrl);
                System.out.println("Gambar koin berhasil dimuat: " + coinImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar koin: " + e.getMessage());
                coinImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar koin tidak ditemukan di assets/coin.png.");
            coinImage = null;
        }


        // Inisialisasi Player
        int playerDisplayWidth = originalFrameWidthPlayer * SCALE_FACTOR_PLAYER;
        int playerDisplayHeight = originalFrameHeightPlayer * SCALE_FACTOR_PLAYER;

        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2),
                panelHeight / 2 - (playerDisplayHeight / 2),
                playerDisplayWidth,
                playerDisplayHeight,
                fullSpriteSheetPlayer
        );

        // Inisialisasi SATU Orc (dari orc_attack.png sebagai sprite sheet)
        int originalOrcFrameWidth = fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50;
        int totalOrcFrames = 6;

        singleOrc = new Orc(
                gamePanelWidth - (originalOrcFrameWidth * SCALE_FACTOR_ENEMY), 100,
                originalOrcFrameWidth * SCALE_FACTOR_ENEMY,
                (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getHeight() : 50) * SCALE_FACTOR_ENEMY,
                fullSpriteSheetOrc,
                -ENEMY_SPEED,
                originalOrcFrameWidth,
                totalOrcFrames
        );

        // Inisialisasi Coins (dari coin.png sebagai gambar tunggal)
        coins = new ArrayList<>();
        // Asumsi ukuran koin tetap, misalnya 30x30 setelah scaling
        int coinDisplayWidth = 30 * SCALE_FACTOR_ENEMY / 2; // Sesuaikan ukuran koin jika perlu
        int coinDisplayHeight = 30 * SCALE_FACTOR_ENEMY / 2;

        for (int i = 0; i < COIN_COUNT; i++) {
            // Posisi acak untuk koin di bagian bawah
            int randomX = (int)(Math.random() * (gamePanelWidth - coinDisplayWidth));
            int randomY = (int)(gamePanelHeight * 0.7 + (Math.random() * (gamePanelHeight * 0.2)));
            int initialVelocity = (i % 2 == 0) ? ENEMY_SPEED : -ENEMY_SPEED; // Bergantian arah

            coins.add(new Coin( // Menggunakan Coin
                    randomX,
                    randomY,
                    coinDisplayWidth,
                    coinDisplayHeight,
                    coinImage, // Langsung gambar koin
                    initialVelocity
            ));
        }

        lastFrameTimePlayer = System.currentTimeMillis();
        mousePosition = new Point(0,0);
    }

    public void setPlayerMovementDirection(int keyCode, boolean isPressed) {
        if (isPressed) {
            if (keyCode == KeyEvent.VK_LEFT) {
                player.setVelocityX(-PLAYER_SPEED);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                player.setVelocityX(PLAYER_SPEED);
            } else if (keyCode == KeyEvent.VK_UP) {
                player.setVelocityY(-PLAYER_SPEED);
            } else if (keyCode == KeyEvent.VK_DOWN) {
                player.setVelocityY(PLAYER_SPEED);
            }
        } else {
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
                player.setVelocityX(0);
            } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
                player.setVelocityY(0);
            }

            if (player.getVelocityX() == 0 && player.getVelocityY() == 0) {
                currentFramePlayer = 0;
            }
        }
    }

    public void setLassoActive(boolean active) {
        this.isLassoActive = active;
    }

    public void updateMousePosition(int x, int y) {
        this.mousePosition.setLocation(x, y);
    }

    private void checkLassoCollision() {
        // Cek Orc
        if (singleOrc != null) {
            double distanceToOrc = player.getDistanceTo(singleOrc.getPosX() + singleOrc.getDisplayWidth() / 2,
                    singleOrc.getPosY() + singleOrc.getDisplayHeight() / 2);
            double distanceMouseToOrc = mousePosition.distance(singleOrc.getPosX() + singleOrc.getDisplayWidth() / 2,
                    singleOrc.getPosY() + singleOrc.getDisplayHeight() / 2);

            if (distanceToOrc < LASSO_RANGE && distanceMouseToOrc < LASSO_GRAB_TOLERANCE) {
                score += 100;
                singleOrc = null;
            }
        }

        // Cek Coin
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            double distanceToCoin = player.getDistanceTo(coin.getPosX() + coin.getDisplayWidth() / 2,
                    coin.getPosY() + coin.getDisplayHeight() / 2);
            double distanceMouseToCoin = mousePosition.distance(coin.getPosX() + coin.getDisplayWidth() / 2,
                    coin.getPosY() + coin.getDisplayHeight() / 2);

            if (distanceToCoin < LASSO_RANGE && distanceMouseToCoin < LASSO_GRAB_TOLERANCE) {
                score += 50;
                iterator.remove();
            }
        }
    }

    public void updateGame() {
        player.updatePosition();

        if (singleOrc != null) {
            singleOrc.updatePosition(gamePanelWidth);
        }

        for (Coin coin : coins) { // Untuk setiap koin
            coin.updatePosition(gamePanelWidth);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTimePlayer > FRAME_DELAY_PLAYER) {
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) {
                currentFramePlayer = (currentFramePlayer + 1) % totalFramesPlayer;
            }
            lastFrameTimePlayer = currentTime;
        }

        if (isLassoActive) {
            checkLassoCollision();
        }
    }

    public Image getCurrentPlayerFrame() {
        if (fullSpriteSheetPlayer == null || originalFrameWidthPlayer == 0 || originalFrameHeightPlayer == 0) {
            return null;
        }
        int sourceX = currentFramePlayer * originalFrameWidthPlayer;
        return fullSpriteSheetPlayer.getSubimage(sourceX, 0, originalFrameWidthPlayer, originalFrameHeightPlayer);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public int getPlayerX() { return player.getPosX(); }
    public int getPlayerY() { return player.getPosY(); }
    public int getPlayerDisplayWidth() { return player.getDisplayWidth(); }
    public int getPlayerDisplayHeight() { return player.getDisplayHeight(); }
    public int getPlayerVelocityX() { return player.getVelocityX(); }

    public Orc getSingleOrc() { return singleOrc; }
    public List<Coin> getCoins() { return coins; } // Getter untuk koin

    public boolean isLassoActive() { return isLassoActive; }
    public Point getMousePosition() { return mousePosition; }

    public int getScore() { return score; }
}