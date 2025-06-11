// package viewmodel;
// File: viewmodel/GameViewModel.java
package viewmodel;

import model.Player;
import model.Orc;
import model.Coin;
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
    private List<Coin> coins;
    private final int PLAYER_SPEED = 5;
    private final int ENEMY_SPEED = 2;
    private final int COIN_COUNT = 5;

    private int gamePanelWidth;
    private int gamePanelHeight;

    private BufferedImage fullSpriteSheetPlayer;
    private BufferedImage fullSpriteSheetPlayerHurt; // Sprite sheet untuk soldier-hurt
    private BufferedImage fullSpriteSheetOrc;
    private BufferedImage coinImage;
    private BufferedImage backgroundImage;
    private BufferedImage chestOpenImage;

    private int chestPosX;
    private int chestPosY;
    private int chestDisplayWidth;
    private int chestDisplayHeight;

    private int currentFramePlayer = 0;
    private int originalFrameWidthPlayer;
    private int originalFrameHeightPlayer;
    private int totalFramesPlayer;
    private long lastFrameTimePlayer;
    private final long FRAME_DELAY_PLAYER = 70;

    private boolean isPlayerHurt = false; // Status apakah player sedang hurt
    private long hurtStartTime = 0; // Waktu mulai hurt
    private final long HURT_DURATION = 1000; // Durasi hurt dalam milliseconds (1 detik)

    private int currentFramePlayerHurt = 1; // Frame hurt dimulai dari frame 1 (frame ke-2)
    private long lastFrameTimePlayerHurt;
    private final long FRAME_DELAY_PLAYER_HURT = 250; // Delay antar frame hurt (0.25 detik)

    private final int SCALE_FACTOR_PLAYER = 5;
    private final int SCALE_FACTOR_ENEMY = 3;

    private boolean isLassoActive = false;
    private Point mousePosition;
    private final int LASSO_RANGE = 200;
    private final int LASSO_GRAB_TOLERANCE = 30;
    private final int COIN_COLLECT_SPEED = 10; // Speed at which coin moves to chest

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

        // Memuat sprite sheet pemain hurt
        URL playerHurtImageUrl = getClass().getClassLoader().getResource("assets/soldier-hurt.png");
        if (playerHurtImageUrl != null) {
            try {
                fullSpriteSheetPlayerHurt = ImageIO.read(playerHurtImageUrl);
                System.out.println("Sprite sheet pemain hurt berhasil dimuat: " + playerHurtImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet pemain hurt: " + e.getMessage());
                fullSpriteSheetPlayerHurt = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet pemain hurt tidak ditemukan di assets/soldier-hurt.png.");
            fullSpriteSheetPlayerHurt = null;
        }

        // Memuat gambar latar belakang (background-cave.png)
        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background-cave.png");
        if (backgroundImageUrl != null) {
            try {
                backgroundImage = ImageIO.read(backgroundImageUrl);
                System.out.println("Gambar latar belakang berhasil dimuat: " + backgroundImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage());
                backgroundImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background-cave.png.");
            backgroundImage = null;
        }

        // Memuat sprite sheet Orc (orc-attack.png)
        URL orcImageUrl = getClass().getClassLoader().getResource("assets/orc-attack.png");
        if (orcImageUrl != null) {
            try {
                fullSpriteSheetOrc = ImageIO.read(orcImageUrl);
                System.out.println("Sprite sheet orc berhasil dimuat: " + orcImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet orc: " + e.getMessage());
                fullSpriteSheetOrc = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet orc tidak ditemukan di assets/orc-attack.png.");
            fullSpriteSheetOrc = null;
        }

        // Memuat gambar Coin (coin.png)
        URL coinImageUrl = getClass().getClassLoader().getResource("assets/coin.png");
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

        // Memuat gambar Peti Terbuka (chest-open.png)
        URL chestImageUrl = getClass().getClassLoader().getResource("assets/chest-open.png");
        if (chestImageUrl != null) {
            try {
                chestOpenImage = ImageIO.read(chestImageUrl);
                System.out.println("Gambar peti terbuka berhasil dimuat: " + chestImageUrl);
            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar peti terbuka: " + e.getMessage());
                chestOpenImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar peti terbuka tidak ditemukan di assets/chest-open.png.");
            chestOpenImage = null;
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

        // Inisialisasi SATU Orc
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

        // Inisialisasi posisi dan ukuran Peti Harta Karun
        chestDisplayWidth = 100;
        chestDisplayHeight = 100;
        chestPosX = 10;
        chestPosY = 30 + 10; // Y di bawah skor + sedikit padding

        // Inisialisasi Coins
        coins = new ArrayList<>();
        int coinDisplayWidth = 30 * SCALE_FACTOR_ENEMY / 2;
        int coinDisplayHeight = 30 * SCALE_FACTOR_ENEMY / 2;

        for (int i = 0; i < COIN_COUNT; i++) {
            int randomX = (int)(Math.random() * (gamePanelWidth - coinDisplayWidth));
            int randomY = (int)(gamePanelHeight * 0.7 + (Math.random() * (gamePanelHeight * 0.2)));
            int initialVelocity = (i % 2 == 0) ? ENEMY_SPEED : -ENEMY_SPEED;

            coins.add(new Coin(
                    randomX,
                    randomY,
                    coinDisplayWidth,
                    coinDisplayHeight,
                    coinImage,
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
            int orcCenterX = singleOrc.getPosX() + singleOrc.getDisplayWidth() / 2;
            int orcCenterY = singleOrc.getPosY() + singleOrc.getDisplayHeight() / 2;
            double distanceToOrc = Math.sqrt(Math.pow(orcCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(orcCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2));
            double distanceMouseToOrc = mousePosition.distance(orcCenterX, orcCenterY);

            if (distanceToOrc < LASSO_RANGE && distanceMouseToOrc < LASSO_GRAB_TOLERANCE) {
                score += 100;
                singleOrc = null; // Orc hilang
                isPlayerHurt = true; // Player berubah jadi hurt setelah nangkep orc
                hurtStartTime = System.currentTimeMillis(); // Catat waktu mulai hurt
                lastFrameTimePlayerHurt = System.currentTimeMillis(); // Reset timer frame hurt
                currentFramePlayerHurt = 1; // Mulai dari frame ke-2 (index 1)
                System.out.println("Player menangkap Orc! Status berubah menjadi hurt untuk " + (HURT_DURATION/1000.0) + " detik.");
            }
        }

        // Cek Coin
        // Iterate using a standard for loop to avoid ConcurrentModificationException if removing
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            if (coin.isCollected()) { // Skip if already collected and moving to chest
                continue;
            }

            int coinCenterX = coin.getPosX() + coin.getDisplayWidth() / 2;
            int coinCenterY = coin.getPosY() + coin.getDisplayHeight() / 2;
            double distanceToCoin = Math.sqrt(Math.pow(coinCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(coinCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2));
            double distanceMouseToCoin = mousePosition.distance(coinCenterX, coinCenterY);

            if (distanceToCoin < LASSO_RANGE && distanceMouseToCoin < LASSO_GRAB_TOLERANCE) {
                score += 50;
                coin.setCollected(true);
                coin.setTargetX(chestPosX + chestDisplayWidth / 2 - coin.getDisplayWidth() / 2);
                coin.setTargetY(chestPosY + chestDisplayHeight / 2 - coin.getDisplayHeight() / 2);
                coin.setVelocityX(0); // Stop horizontal movement
            }
        }
    }

    public void updateGame() {
        // Update status hurt player - cek apakah sudah waktunya kembali normal
        if (isPlayerHurt) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - hurtStartTime >= HURT_DURATION) {
                isPlayerHurt = false;
                System.out.println("Player kembali normal dari status hurt.");
            } else {
                // Update animasi hurt (frame 1 dan 2, yaitu foto ke-2 dan ke-3)
                if (currentTime - lastFrameTimePlayerHurt > FRAME_DELAY_PLAYER_HURT) {
                    currentFramePlayerHurt = (currentFramePlayerHurt == 1) ? 2 : 1; // Toggle antara frame 1 dan 2
                    lastFrameTimePlayerHurt = currentTime;
                }
            }
        }

        // Update posisi pemain - TIDAK ADA BATASAN LAGI!
        player.setPosX(player.getPosX() + player.getVelocityX());
        player.setPosY(player.getPosY() + player.getVelocityY());

        // KODE PEMBATASAN PLAYER SUDAH DIHAPUS - Player bisa bergerak bebas keluar layar

        // Update posisi Orc
        if (singleOrc != null) {
            singleOrc.setPosX(singleOrc.getPosX() + singleOrc.getVelocityX());
            if (singleOrc.getPosX() <= 0 || singleOrc.getPosX() + singleOrc.getDisplayWidth() >= gamePanelWidth) {
                singleOrc.setVelocityX(singleOrc.getVelocityX() * -1);
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - singleOrc.getLastFrameTime() > singleOrc.getFrameDelay()) {
                singleOrc.setCurrentFrame((singleOrc.getCurrentFrame() + 1) % singleOrc.getTotalFrames());
                singleOrc.setLastFrameTime(currentTime);
            }
        }

        // Update posisi Coins
        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            if (coin.isCollected()) {
                // Move coin towards the chest
                int dx = coin.getTargetX() - coin.getPosX();
                int dy = coin.getTargetY() - coin.getPosY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < COIN_COLLECT_SPEED) {
                    coin.setPosX(coin.getTargetX());
                    coin.setPosY(coin.getTargetY());
                    coinIterator.remove(); // Remove coin once it reaches the chest
                } else {
                    coin.setPosX(coin.getPosX() + (int)(dx / distance * COIN_COLLECT_SPEED));
                    coin.setPosY(coin.getPosY() + (int)(dy / distance * COIN_COLLECT_SPEED));
                }
            } else {
                coin.setPosX(coin.getPosX() + coin.getVelocityX());
                if (coin.getPosX() <= 0 || coin.getPosX() + coin.getDisplayWidth() >= gamePanelWidth) {
                    coin.setVelocityX(coin.getVelocityX() * -1);
                }
            }
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
        BufferedImage currentSpriteSheet;
        int frameToUse = 0;
        int frameWidth = originalFrameWidthPlayer;
        int frameHeight = originalFrameHeightPlayer;

        // Pilih sprite sheet berdasarkan status player
        if (isPlayerHurt && fullSpriteSheetPlayerHurt != null) {
            currentSpriteSheet = fullSpriteSheetPlayerHurt;
            frameToUse = currentFramePlayerHurt; // Gunakan frame hurt (1 atau 2)
            // Asumsi sprite sheet hurt memiliki struktur yang sama
        } else {
            currentSpriteSheet = fullSpriteSheetPlayer;
            frameToUse = currentFramePlayer; // Gunakan frame normal
        }

        if (currentSpriteSheet == null || frameWidth == 0 || frameHeight == 0) {
            return null;
        }

        int sourceX = frameToUse * frameWidth;
        return currentSpriteSheet.getSubimage(sourceX, 0, frameWidth, frameHeight);
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
    public List<Coin> getCoins() { return coins; }

    public boolean isLassoActive() { return isLassoActive; }
    public Point getMousePosition() { return mousePosition; }
    public int getLassoRange() { return LASSO_RANGE; } // Getter untuk LASSO_RANGE

    public int getScore() { return score; }

    // Getters untuk Peti Harta Karun
    public Image getChestOpenImage() { return chestOpenImage; }
    public int getChestPosX() { return chestPosX; }
    public int getChestPosY() { return chestPosY; }
    public int getChestDisplayWidth() { return chestDisplayWidth; }
    public int getChestDisplayHeight() { return chestDisplayHeight; }

    // Getter untuk status player hurt
    public boolean isPlayerHurt() { return isPlayerHurt; }
}