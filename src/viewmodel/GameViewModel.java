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
import java.util.Random;

public class GameViewModel {
    private Player player;
    private List<Orc> orcs;
    private List<Coin> coins;
    private final int PLAYER_SPEED = 7; // Kecepatan pemain dipercepat sedikit dari 5 menjadi 7
    private final int ORC_SPEED = 6;   // Kecepatan Orc (lebih cepat dari koin)
    private final int COIN_SPEED = 4;  // Kecepatan Koin (lebih lambat dari orc)
    private final int SPAWN_INTERVAL = 1500;
    private long lastSpawnTime;

    private int gamePanelWidth;
    private int gamePanelHeight;

    private BufferedImage fullSpriteSheetPlayer;
    private BufferedImage fullSpriteSheetPlayerHurt;
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

    private boolean isPlayerHurt = false;
    private long hurtStartTime = 0;
    private final long HURT_DURATION = 1000;

    private int currentFramePlayerHurt = 1;
    private long lastFrameTimePlayerHurt;
    private final long FRAME_DELAY_PLAYER_HURT = 250;

    private final int SCALE_FACTOR_PLAYER = 5;
    private final int SCALE_FACTOR_ENEMY = 3;

    private boolean isLassoActive = false;
    private Point mousePosition;
    private final int LASSO_RANGE = 200;
    private final int LASSO_GRAB_TOLERANCE = 30;
    private final int COIN_COLLECT_SPEED = 10;

    private int score = 0;

    private long gameStartTime;
    private final long GAME_DURATION = 30 * 1000;
    private boolean isGameOver = false;

    private final int TOP_LANE_Y;
    private final int BOTTOM_LANE_Y;
    private Random random;

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;
        this.random = new Random();

        TOP_LANE_Y = (int) (gamePanelHeight * 0.25);
        BOTTOM_LANE_Y = (int) (gamePanelHeight * 0.75);

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

        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background-cave.png");
        if (backgroundImageUrl != null) {
            try {
                backgroundImage = ImageIO.read(backgroundImageUrl);
                System.out.println("Gambar latar belakang berhasil dimuat: " + backgroundImageUrl);
            }
            catch (IOException e) {
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage());
                backgroundImage = null;
            }
        } else {
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background-cave.png.");
            backgroundImage = null;
        }

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

        int playerDisplayWidth = originalFrameWidthPlayer * SCALE_FACTOR_PLAYER;
        int playerDisplayHeight = originalFrameHeightPlayer * SCALE_FACTOR_PLAYER;

        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2),
                panelHeight / 2 - (playerDisplayHeight / 2),
                playerDisplayWidth,
                playerDisplayHeight,
                fullSpriteSheetPlayer
        );

        orcs = new ArrayList<>();
        coins = new ArrayList<>();

        chestDisplayWidth = 100;
        chestDisplayHeight = 100;
        chestPosX = 10;
        chestPosY = 30 + 10 + 30;

        lastFrameTimePlayer = System.currentTimeMillis();
        mousePosition = new Point(0,0);

        this.gameStartTime = System.currentTimeMillis();
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void setPlayerMovementDirection(int keyCode, boolean isPressed) {
        if (isGameOver) return;

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
        if (isGameOver) return;
        this.isLassoActive = active;
    }

    public void updateMousePosition(int x, int y) {
        this.mousePosition.setLocation(x, y);
    }

    private void spawnEntity() {
        if (isGameOver) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            boolean spawnOrc = random.nextBoolean();
            boolean useTopLane = random.nextBoolean();

            int spawnY = useTopLane ? TOP_LANE_Y : BOTTOM_LANE_Y;
            int initialVelocity;
            int spawnX;

            if (spawnOrc) { // Jika Orc
                initialVelocity = useTopLane ? -ORC_SPEED : ORC_SPEED; // Gunakan ORC_SPEED
            } else { // Jika Koin
                initialVelocity = useTopLane ? -COIN_SPEED : COIN_SPEED; // Gunakan COIN_SPEED
            }

            if (useTopLane) { // Bergerak ke kiri (muncul dari kanan)
                spawnX = gamePanelWidth + 10;
            } else { // Bergerak ke kanan (muncul dari kiri)
                // Sesuaikan spawnX berdasarkan jenis entitas untuk memastikan jarak yang konsisten
                if (spawnOrc) {
                    int orcWidth = (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50) * SCALE_FACTOR_ENEMY;
                    spawnX = -10 - orcWidth;
                } else {
                    int coinWidth = 30 * SCALE_FACTOR_ENEMY / 2;
                    spawnX = -10 - coinWidth;
                }
            }


            if (spawnOrc) {
                int originalOrcFrameWidth = fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50;
                int totalOrcFrames = 6;
                Orc newOrc = new Orc(
                        spawnX, spawnY,
                        originalOrcFrameWidth * SCALE_FACTOR_ENEMY,
                        (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getHeight() : 50) * SCALE_FACTOR_ENEMY,
                        fullSpriteSheetOrc,
                        initialVelocity, // Gunakan initialVelocity yang sudah disesuaikan
                        originalOrcFrameWidth,
                        totalOrcFrames
                );
                orcs.add(newOrc);
                System.out.println("Orc baru muncul di jalur " + (useTopLane ? "atas" : "bawah") + " dengan kecepatan " + initialVelocity);
            } else {
                int coinDisplayWidth = 30 * SCALE_FACTOR_ENEMY / 2;
                int coinDisplayHeight = 30 * SCALE_FACTOR_ENEMY / 2;
                Coin newCoin = new Coin(
                        spawnX, spawnY,
                        coinDisplayWidth,
                        coinDisplayHeight,
                        coinImage,
                        initialVelocity // Gunakan initialVelocity yang sudah disesuaikan
                );
                coins.add(newCoin);
                System.out.println("Coin baru muncul di jalur " + (useTopLane ? "atas" : "bawah") + " dengan kecepatan " + initialVelocity);
            }
            lastSpawnTime = currentTime;
        }
    }

    private void checkLassoCollision() {
        if (isGameOver) return;

        Iterator<Orc> orcIterator = orcs.iterator();
        while (orcIterator.hasNext()) {
            Orc orc = orcIterator.next();
            int orcCenterX = orc.getPosX() + orc.getDisplayWidth() / 2;
            int orcCenterY = orc.getPosY() + orc.getDisplayHeight() / 2;
            double distanceToOrc = Math.sqrt(Math.pow(orcCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(orcCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2));
            double distanceMouseToOrc = mousePosition.distance(orcCenterX, orcCenterY);

            if (distanceToOrc < LASSO_RANGE && distanceMouseToOrc < LASSO_GRAB_TOLERANCE) {
                score -= 100;
                orcIterator.remove();
                isPlayerHurt = true;
                hurtStartTime = System.currentTimeMillis();
                lastFrameTimePlayerHurt = System.currentTimeMillis();
                currentFramePlayerHurt = 1;
                System.out.println("Player menangkap Orc! Status berubah menjadi hurt untuk " + (HURT_DURATION/1000.0) + " detik. Skor: " + score);
            }
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            if (coin.isCollected()) {
                continue;
            }

            int coinCenterX = coin.getPosX() + coin.getDisplayWidth() / 2;
            int coinCenterY = coin.getPosY() + coin.getDisplayHeight() / 2;
            double distanceToCoin = Math.sqrt(Math.pow(coinCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(coinCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2));
            double distanceMouseToCoin = mousePosition.distance(coinCenterX, coinCenterY);

            if (distanceToCoin < LASSO_RANGE && distanceMouseToCoin < LASSO_GRAB_TOLERANCE) {
                coin.setCollected(true);
                coin.setTargetX(chestPosX + chestDisplayWidth / 2 - coin.getDisplayWidth() / 2);
                coin.setTargetY(chestPosY + chestDisplayHeight / 2 - coin.getDisplayHeight() / 2);
                coin.setVelocityX(0);
                System.out.println("Player menangkap Coin! Skor: " + score);
            }
        }
    }

    public void updateGame() {
        if (isGameOver) return;

        if (System.currentTimeMillis() - gameStartTime >= GAME_DURATION) {
            isGameOver = true;
            System.out.println("Game Over! Waktu Habis. Skor Akhir: " + score);
            return;
        }

        spawnEntity();

        if (isPlayerHurt) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - hurtStartTime >= HURT_DURATION) {
                isPlayerHurt = false;
                System.out.println("Player kembali normal dari status hurt.");
            } else {
                if (currentTime - lastFrameTimePlayerHurt > FRAME_DELAY_PLAYER_HURT) {
                    currentFramePlayerHurt = (currentFramePlayerHurt == 1) ? 2 : 1;
                    lastFrameTimePlayerHurt = currentTime;
                }
            }
        }

        // Update posisi pemain - TIDAK ADA BATASAN LAYAR
        player.setPosX(player.getPosX() + player.getVelocityX());
        player.setPosY(player.getPosY() + player.getVelocityY());

        Iterator<Orc> orcIterator = orcs.iterator();
        while (orcIterator.hasNext()) {
            Orc orc = orcIterator.next();
            orc.setPosX(orc.getPosX() + orc.getVelocityX());

            if (orc.getVelocityX() < 0 && orc.getPosX() + orc.getDisplayWidth() < 0) {
                orcIterator.remove();
            } else if (orc.getVelocityX() > 0 && orc.getPosX() > gamePanelWidth) {
                orcIterator.remove();
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - orc.getLastFrameTime() > orc.getFrameDelay()) {
                orc.setCurrentFrame((orc.getCurrentFrame() + 1) % orc.getTotalFrames());
                orc.setLastFrameTime(currentTime);
            }
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            if (coin.isCollected()) {
                int dx = coin.getTargetX() - coin.getPosX();
                int dy = coin.getTargetY() - coin.getPosY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < COIN_COLLECT_SPEED) {
                    coin.setPosX(coin.getTargetX());
                    coin.setPosY(coin.getTargetY());
                    coinIterator.remove();
                    score += 50;
                } else {
                    coin.setPosX(coin.getPosX() + (int)(dx / distance * COIN_COLLECT_SPEED));
                    coin.setPosY(coin.getPosY() + (int)(dy / distance * COIN_COLLECT_SPEED));
                }
            } else {
                coin.setPosX(coin.getPosX() + coin.getVelocityX());
                if (coin.getVelocityX() < 0 && coin.getPosX() + coin.getDisplayWidth() < 0) {
                    coinIterator.remove();
                } else if (coin.getVelocityX() > 0 && coin.getPosX() > gamePanelWidth) {
                    coinIterator.remove();
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

        if (isPlayerHurt && fullSpriteSheetPlayerHurt != null) {
            currentSpriteSheet = fullSpriteSheetPlayerHurt;
            frameToUse = currentFramePlayerHurt;
        } else {
            currentSpriteSheet = fullSpriteSheetPlayer;
            frameToUse = currentFramePlayer;
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

    public List<Orc> getOrcs() { return orcs; }
    public List<Coin> getCoins() { return coins; }

    public boolean isLassoActive() { return isLassoActive; }
    public Point getMousePosition() { return mousePosition; }
    public int getLassoRange() { return LASSO_RANGE; }

    public int getScore() { return score; }

    public Image getChestOpenImage() { return chestOpenImage; }
    public int getChestPosX() { return chestPosX; }
    public int getChestPosY() { return chestPosY; }
    public int getChestDisplayWidth() { return chestDisplayWidth; }
    public int getChestDisplayHeight() { return chestDisplayHeight; }

    public boolean isPlayerHurt() { return isPlayerHurt; }

    public long getTimeLeft() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        long remaining = GAME_DURATION - elapsed;
        return Math.max(0, remaining);
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}