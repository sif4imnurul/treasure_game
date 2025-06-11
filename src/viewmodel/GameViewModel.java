package viewmodel;

import model.Player;
import model.Orc;
import model.Coin;
import config.Database;
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
import java.sql.ResultSet; // Import ResultSet
import java.sql.SQLException; // Import SQLException

public class GameViewModel {
    private Player player;
    private List<Orc> orcs;
    private List<Coin> coins;
    private final int PLAYER_SPEED = 7;
    private final int ORC_SPEED = 6;
    private final int COIN_SPEED = 4;
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
    private final long FRAME_DELAY_PLAYER_HURT = 250; // Correctly defined as uppercase HURT

    private final int SCALE_FACTOR_PLAYER = 5;
    private final int SCALE_FACTOR_ENEMY = 3;

    private boolean isLassoActive = false;
    private Point mousePosition;
    private final int LASSO_RANGE = 200;
    private final int LASSO_GRAB_TOLERANCE = 30;
    private final int COIN_COLLECT_SPEED = 10;

    private int score = 0;
    private int coinsCollectedCount = 0; // NEW: Track collected coins count

    private long gameStartTime;
    private final long GAME_DURATION = 30 * 1000;
    private boolean isGameOver = false;

    private final int TOP_LANE_Y;
    private final int BOTTOM_LANE_Y;
    private Random random;

    private Database db;

    private String playerName = "Guest";

    public GameViewModel(int panelWidth, int panelHeight) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.gamePanelWidth = panelWidth; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.gamePanelHeight = panelHeight; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.random = new Random(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.db = new Database(); // Inisialisasi database // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        TOP_LANE_Y = (int) (gamePanelHeight * 0.25); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        BOTTOM_LANE_Y = (int) (gamePanelHeight * 0.75); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (playerImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetPlayer = ImageIO.read(playerImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Sprite sheet pemain berhasil dimuat: " + playerImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                originalFrameWidthPlayer = fullSpriteSheetPlayer.getWidth() / 8; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                originalFrameHeightPlayer = fullSpriteSheetPlayer.getHeight(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                totalFramesPlayer = 8; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat sprite sheet pemain: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetPlayer = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Sprite sheet pemain tidak ditemukan di assets/soldier-walk.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            fullSpriteSheetPlayer = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        URL playerHurtImageUrl = getClass().getClassLoader().getResource("assets/soldier-hurt.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (playerHurtImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetPlayerHurt = ImageIO.read(playerHurtImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Sprite sheet pemain hurt berhasil dimuat: " + playerHurtImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat sprite sheet pemain hurt: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetPlayerHurt = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Sprite sheet pemain hurt tidak ditemukan di assets/soldier-hurt.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            fullSpriteSheetPlayerHurt = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background-cave.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (backgroundImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                backgroundImage = ImageIO.read(backgroundImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Gambar latar belakang berhasil dimuat: " + backgroundImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
            catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat gambar latar belakang: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                backgroundImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Gambar latar belakang tidak ditemukan di assets/background-cave.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            backgroundImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        URL orcImageUrl = getClass().getClassLoader().getResource("assets/orc-attack.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (orcImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetOrc = ImageIO.read(orcImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Sprite sheet orc berhasil dimuat: " + orcImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat sprite sheet orc: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetOrc = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Sprite sheet orc tidak ditemukan di assets/orc-attack.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            fullSpriteSheetOrc = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        URL coinImageUrl = getClass().getClassLoader().getResource("assets/coin.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (coinImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coinImage = ImageIO.read(coinImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Gambar koin berhasil dimuat: " + coinImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat gambar koin: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coinImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Gambar koin tidak ditemukan di assets/coin.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            coinImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        URL chestImageUrl = getClass().getClassLoader().getResource("assets/chest-open.png"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (chestImageUrl != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            try { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                chestOpenImage = ImageIO.read(chestImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Gambar peti terbuka berhasil dimuat: " + chestImageUrl); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } catch (IOException e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.err.println("ERROR: Gagal memuat gambar peti terbuka: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                chestOpenImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            System.err.println("ERROR: Gambar peti terbuka tidak ditemukan di assets/chest-open.png."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            chestOpenImage = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        int playerDisplayWidth = originalFrameWidthPlayer * SCALE_FACTOR_PLAYER; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        int playerDisplayHeight = originalFrameHeightPlayer * SCALE_FACTOR_PLAYER; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        this.player = new Player( // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                panelWidth / 2 - (playerDisplayWidth / 2), // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                panelHeight / 2 - (playerDisplayHeight / 2), // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                playerDisplayWidth, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                playerDisplayHeight, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                fullSpriteSheetPlayer // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        );

        orcs = new ArrayList<>(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        coins = new ArrayList<>(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        chestDisplayWidth = 100; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        chestDisplayHeight = 100; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        chestPosX = 10; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        chestPosY = 30 + 10 + 30; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        lastFrameTimePlayer = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        mousePosition = new Point(0,0); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        this.gameStartTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.lastSpawnTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
    }

    public void setPlayerName(String playerName) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.playerName = playerName; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
    }

    public void resetGame() {
        // Reset player position and state
        player.setPosX(gamePanelWidth / 2 - (player.getDisplayWidth() / 2));
        player.setPosY(gamePanelHeight / 2 - (player.getDisplayHeight() / 2));
        player.setVelocityX(0);
        player.setVelocityY(0);
        isPlayerHurt = false;
        currentFramePlayer = 0;

        // Clear all orcs and coins
        orcs.clear();
        coins.clear();

        // Reset score, coins collected, and game state
        score = 0;
        coinsCollectedCount = 0; // NEW: Reset coins collected count
        isGameOver = false;
        gameStartTime = System.currentTimeMillis();
        lastSpawnTime = System.currentTimeMillis();

        // Reset lasso state
        isLassoActive = false;
        mousePosition = new Point(0, 0);

        System.out.println("Game state has been reset.");
    }

    public void setPlayerMovementDirection(int keyCode, boolean isPressed) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (isGameOver) return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        if (isPressed) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (keyCode == KeyEvent.VK_LEFT) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityX(-PLAYER_SPEED); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else if (keyCode == KeyEvent.VK_RIGHT) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityX(PLAYER_SPEED); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else if (keyCode == KeyEvent.VK_UP) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityY(-PLAYER_SPEED); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else if (keyCode == KeyEvent.VK_DOWN) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityY(PLAYER_SPEED); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        } else {
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityX(0); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                player.setVelocityY(0); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }

            if (player.getVelocityX() == 0 && player.getVelocityY() == 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                currentFramePlayer = 0; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        }
    }

    public void setLassoActive(boolean active) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (isGameOver) return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.isLassoActive = active; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
    }

    public void updateMousePosition(int x, int y) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        this.mousePosition.setLocation(x, y); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
    }

    private void spawnEntity() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (isGameOver) return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        long currentTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            boolean spawnOrc = random.nextBoolean(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            boolean useTopLane = random.nextBoolean(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

            int spawnY = useTopLane ? TOP_LANE_Y : BOTTOM_LANE_Y; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            int initialVelocity; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            int spawnX; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

            if (spawnOrc) { // Jika Orc // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                initialVelocity = useTopLane ? -ORC_SPEED : ORC_SPEED; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else { // Jika Koin
                initialVelocity = useTopLane ? -COIN_SPEED : COIN_SPEED; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }

            if (useTopLane) { // Bergerak ke kiri (muncul dari kanan) // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                spawnX = gamePanelWidth + 10; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else { // Bergerak ke kanan (muncul dari kiri) // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                if (spawnOrc) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    int orcWidth = (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50) * SCALE_FACTOR_ENEMY; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    spawnX = -10 - orcWidth; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                } else {
                    int coinWidth = 30 * SCALE_FACTOR_ENEMY / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    spawnX = -10 - coinWidth; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                }
            }


            if (spawnOrc) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                int originalOrcFrameWidth = fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                int totalOrcFrames = 6; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                Orc newOrc = new Orc( // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        spawnX, spawnY, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        originalOrcFrameWidth * SCALE_FACTOR_ENEMY, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getHeight() : 50) * SCALE_FACTOR_ENEMY, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        fullSpriteSheetOrc, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        initialVelocity, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        originalOrcFrameWidth, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        totalOrcFrames // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                );
                orcs.add(newOrc); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Orc baru muncul di jalur " + (useTopLane ? "atas" : "bawah") + " dengan kecepatan " + initialVelocity); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else {
                int coinDisplayWidth = 30 * SCALE_FACTOR_ENEMY / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                int coinDisplayHeight = 30 * SCALE_FACTOR_ENEMY / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                Coin newCoin = new Coin( // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        spawnX, spawnY, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        coinDisplayWidth, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        coinDisplayHeight, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        coinImage, // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                        initialVelocity // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                );
                coins.add(newCoin); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Coin baru muncul di jalur " + (useTopLane ? "atas" : "bawah") + " dengan kecepatan " + initialVelocity); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
            lastSpawnTime = currentTime; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }
    }

    private void checkLassoCollision() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (isGameOver) return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        Iterator<Orc> orcIterator = orcs.iterator(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        while (orcIterator.hasNext()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            Orc orc = orcIterator.next(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            int orcCenterX = orc.getPosX() + orc.getDisplayWidth() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            int orcCenterY = orc.getPosY() + orc.getDisplayHeight() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            double distanceToOrc = Math.sqrt(Math.pow(orcCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(orcCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            double distanceMouseToOrc = mousePosition.distance(orcCenterX, orcCenterY); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

            if (distanceToOrc < LASSO_RANGE && distanceMouseToOrc < LASSO_GRAB_TOLERANCE) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                score -= 100; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                orcIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                isPlayerHurt = true; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                hurtStartTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                lastFrameTimePlayerHurt = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                currentFramePlayerHurt = 1; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Player menangkap Orc! Status berubah menjadi hurt untuk " + (HURT_DURATION/1000.0) + " detik. Skor: " + score); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        }

        Iterator<Coin> coinIterator = coins.iterator(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        while (coinIterator.hasNext()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            Coin coin = coinIterator.next(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (coin.isCollected()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                continue; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }

            int coinCenterX = coin.getPosX() + coin.getDisplayWidth() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            int coinCenterY = coin.getPosY() + coin.getDisplayHeight() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            double distanceToCoin = Math.sqrt(Math.pow(coinCenterX - (player.getPosX() + player.getDisplayWidth() / 2), 2) + Math.pow(coinCenterY - (player.getPosY() + player.getDisplayHeight() / 2), 2)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            double distanceMouseToCoin = mousePosition.distance(coinCenterX, coinCenterY); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

            if (distanceToCoin < LASSO_RANGE && distanceMouseToCoin < LASSO_GRAB_TOLERANCE) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coin.setCollected(true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coin.setTargetX(chestPosX + chestDisplayWidth / 2 - coin.getDisplayWidth() / 2); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coin.setTargetY(chestPosY + chestDisplayHeight / 2 - coin.getDisplayHeight() / 2); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                coin.setVelocityX(0); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Player menangkap Coin! Skor: " + score); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        }
    }

    public void updateGame() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (isGameOver) return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        if (System.currentTimeMillis() - gameStartTime >= GAME_DURATION) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            isGameOver = true; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            System.out.println("Game Over! Waktu Habis. Skor Akhir: " + score); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            saveGameResult(score, coinsCollectedCount); // MODIFIED: Pass score and coinsCollectedCount
            return; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        spawnEntity(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        if (isPlayerHurt) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            long currentTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (currentTime - hurtStartTime >= HURT_DURATION) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                isPlayerHurt = false; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                System.out.println("Player kembali normal dari status hurt."); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else {
                if (currentTime - lastFrameTimePlayerHurt > FRAME_DELAY_PLAYER_HURT) { // FIX: Changed to FRAME_DELAY_PLAYER_HURT (uppercase HURT)
                    currentFramePlayerHurt = (currentFramePlayerHurt == 1) ? 2 : 1;
                    lastFrameTimePlayerHurt = currentTime;
                }
            }
        }

        player.setPosX(player.getPosX() + player.getVelocityX()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        player.setPosY(player.getPosY() + player.getVelocityY()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        Iterator<Orc> orcIterator = orcs.iterator(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        while (orcIterator.hasNext()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            Orc orc = orcIterator.next(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            orc.setPosX(orc.getPosX() + orc.getVelocityX()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

            if (orc.getVelocityX() < 0 && orc.getPosX() + orc.getDisplayWidth() < 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                orcIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            } else if (orc.getVelocityX() > 0 && orc.getPosX() > gamePanelWidth) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                orcIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }

            long currentTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (currentTime - orc.getLastFrameTime() > orc.getFrameDelay()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                orc.setCurrentFrame((orc.getCurrentFrame() + 1) % orc.getTotalFrames()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                orc.setLastFrameTime(currentTime); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        }

        Iterator<Coin> coinIterator = coins.iterator(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        while (coinIterator.hasNext()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            Coin coin = coinIterator.next(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (coin.isCollected()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                int dx = coin.getTargetX() - coin.getPosX(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                int dy = coin.getTargetY() - coin.getPosY(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                double distance = Math.sqrt(dx * dx + dy * dy); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

                if (distance < COIN_COLLECT_SPEED) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coin.setPosX(coin.getTargetX()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coin.setPosY(coin.getTargetY()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coinIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    score += 50; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coinsCollectedCount++; // NEW: Increment collected coins count
                } else {
                    coin.setPosX(coin.getPosX() + (int)(dx / distance * COIN_COLLECT_SPEED)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coin.setPosY(coin.getPosY() + (int)(dy / distance * COIN_COLLECT_SPEED)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                }
            } else {
                coin.setPosX(coin.getPosX() + coin.getVelocityX()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                if (coin.getVelocityX() < 0 && coin.getPosX() + coin.getDisplayWidth() < 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coinIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                } else if (coin.getVelocityX() > 0 && coin.getPosX() > gamePanelWidth) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                    coinIterator.remove(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                }
            }
        }

        long currentTime = System.currentTimeMillis(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        if (currentTime - lastFrameTimePlayer > FRAME_DELAY_PLAYER) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
                currentFramePlayer = (currentFramePlayer + 1) % totalFramesPlayer; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
            lastFrameTimePlayer = currentTime; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        if (isLassoActive) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            checkLassoCollision(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }
    }

    // MODIFIED: saveGameResult instead of saveScore
    private void saveGameResult(int skor, int count) {
        String queryCheck = "SELECT COUNT(*) FROM thasil WHERE username = '" + this.playerName + "'";
        try (ResultSet rs = db.selectQuery(queryCheck)) {
            if (rs.next() && rs.getInt(1) > 0) {
                // Username exists, update skor and count
                String sql = "UPDATE thasil SET skor = skor + " + skor + ", count = count + " + count + " WHERE username = '" + this.playerName + "'";
                db.insertUpdateDeleteQuery(sql);
                System.out.println("Skor " + skor + " dan Count " + count + " berhasil diupdate untuk " + playerName);
            } else {
                // Username does not exist, insert new record
                String sql = "INSERT INTO thasil (username, skor, count) VALUES ('" + this.playerName + "', " + skor + ", " + count + ")";
                db.insertUpdateDeleteQuery(sql);
                System.out.println("Skor " + skor + " dan Count " + count + " berhasil disimpan untuk " + playerName);
            }
        } catch (SQLException | RuntimeException e) {
            System.err.println("Gagal menyimpan/mengupdate skor dan count: " + e.getMessage());
        }
    }


    // MODIFIED: Get high scores with count
    public List<String[]> getHighScores() {
        List<String[]> highScores = new ArrayList<>();
        // Changed table name to 'thasil' and column names to 'skor' and 'count'
        String sql = "SELECT username, skor, count FROM thasil ORDER BY skor DESC LIMIT 5";
        try (ResultSet rs = db.selectQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                int skor = rs.getInt("skor");
                int count = rs.getInt("count");
                highScores.add(new String[]{username, String.valueOf(skor), String.valueOf(count)});
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil highscores: " + e.getMessage()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }
        return highScores;
    }

    public Image getCurrentPlayerFrame() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        BufferedImage currentSpriteSheet; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        int frameToUse = 0; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        int frameWidth = originalFrameWidthPlayer; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        int frameHeight = originalFrameHeightPlayer; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java

        if (isPlayerHurt && fullSpriteSheetPlayerHurt != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            currentSpriteSheet = fullSpriteSheetPlayerHurt; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            frameToUse = currentFramePlayerHurt; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        } else {
            currentSpriteSheet = fullSpriteSheetPlayer; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            frameToUse = currentFramePlayer; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        if (currentSpriteSheet == null || frameWidth == 0 || frameHeight == 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            return null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        }

        int sourceX = frameToUse * frameWidth; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
        return currentSpriteSheet.getSubimage(sourceX, 0, frameWidth, frameHeight); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
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
    public int getCoinsCollectedCount() { return coinsCollectedCount; } // NEW: Getter for coins collected count

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