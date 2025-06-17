package viewmodel;

import model.Player;
import model.Orc;
import model.Treasure;
import config.Database;
import sound.SoundManager;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GameViewModel {
    // Variabel game.
    private Player player;
    private List<Orc> orcs;
    private List<Treasure> treasures;
    private final int playerSpeed = 7;
    private final int orcSpeed = 6;
    private final int treasureSpeed = 4;
    private final int spawnInterval = 1500;
    private long lastSpawnTime;
    private int gamePanelWidth;
    private int gamePanelHeight;
    private BufferedImage fullSpriteSheetPlayer;
    private BufferedImage fullSpriteSheetPlayerHurt;
    private BufferedImage fullSpriteSheetOrc;
    private BufferedImage treasuresSpriteSheet;
    private Map<Integer, List<BufferedImage>> treasureSprites;
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
    private final long frameDelayPlayer = 70;
    private boolean isPlayerHurt = false;
    private long hurtStartTime = 0;
    private final long hurtDuration = 1000;
    private int currentFramePlayerHurt = 1;
    private long lastFrameTimePlayerHurt;
    private final long frameDelayPlayerHurt = 250;
    private final int scaleFactorPlayer = 5;
    private final int scaleFactorEnemy = 3;
    private final int treasureSpriteWidth = 16;
    private final int treasureSpriteHeight = 16;
    private final int treasureDisplaySize = 48;
    private boolean isLassoActive = false;
    private Point mousePosition;
    private final int treasureCollectSpeed = 10;
    private int score = 0;
    private int treasuresCollectedCount = 0;
    private long gameStartTime;
    private final long gameDuration = 30 * 1000;
    private boolean isGameOver = false;
    private final int topLaneY;
    private final int bottomLaneY;
    private Random random;
    private Database db;
    private String playerName = "Guest";
    private final SoundManager soundManager;

    // inisialisasi dan muat aset.
    public GameViewModel(int panelWidth, int panelHeight, SoundManager soundManager) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;
        this.random = new Random();
        this.db = new Database();
        this.soundManager = soundManager;

        topLaneY = (int) (gamePanelHeight * 0.25);
        bottomLaneY = (int) (gamePanelHeight * 0.75);
        
        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png");
        if (playerImageUrl != null) {
            try {
                fullSpriteSheetPlayer = ImageIO.read(playerImageUrl);
                originalFrameWidthPlayer = fullSpriteSheetPlayer.getWidth() / 8;
                originalFrameHeightPlayer = fullSpriteSheetPlayer.getHeight();
                totalFramesPlayer = 8;
            } catch (IOException e) {
                fullSpriteSheetPlayer = null;
            }
        } else {
            fullSpriteSheetPlayer = null;
        }

        URL playerHurtImageUrl = getClass().getClassLoader().getResource("assets/soldier-hurt.png");
        if (playerHurtImageUrl != null) {
            try {
                fullSpriteSheetPlayerHurt = ImageIO.read(playerHurtImageUrl);
            } catch (IOException e) {
                fullSpriteSheetPlayerHurt = null;
            }
        } else {
            fullSpriteSheetPlayerHurt = null;
        }

        URL backgroundImageUrl = getClass().getClassLoader().getResource("assets/background-cave.png");
        if (backgroundImageUrl != null) {
            try {
                backgroundImage = ImageIO.read(backgroundImageUrl);
            } catch (IOException e) {
                backgroundImage = null;
            }
        } else {
            backgroundImage = null;
        }

        URL orcImageUrl = getClass().getClassLoader().getResource("assets/orc-attack.png");
        if (orcImageUrl != null) {
            try {
                fullSpriteSheetOrc = ImageIO.read(orcImageUrl);
            } catch (IOException e) {
                fullSpriteSheetOrc = null;
            }
        } else {
            fullSpriteSheetOrc = null;
        }

        URL treasuresImageUrl = getClass().getClassLoader().getResource("assets/treasures.png");
        if (treasuresImageUrl != null) {
            try {
                treasuresSpriteSheet = ImageIO.read(treasuresImageUrl);
                uraiSpriteHartaKarun();
            } catch (IOException e) {
                treasuresSpriteSheet = null;
            }
        } else {
            treasuresSpriteSheet = null;
        }

        URL chestImageUrl = getClass().getClassLoader().getResource("assets/chest-open.png");
        if (chestImageUrl != null) {
            try {
                chestOpenImage = ImageIO.read(chestImageUrl);
            } catch (IOException e) {
                chestOpenImage = null;
            }
        } else {
            chestOpenImage = null;
        }

        // objek pemain
        int playerDisplayWidth = originalFrameWidthPlayer * scaleFactorPlayer;
        int playerDisplayHeight = originalFrameHeightPlayer * scaleFactorPlayer;

        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2),
                panelHeight / 2 - (playerDisplayHeight / 2),
                playerDisplayWidth,
                playerDisplayHeight,
                fullSpriteSheetPlayer
        );

        // objek orc
        orcs = new ArrayList<>();
        treasures = new ArrayList<>();

        chestDisplayWidth = 100;
        chestDisplayHeight = 100;
        chestPosX = 10;
        chestPosY = 30 + 10 + 30 + 50;

        lastFrameTimePlayer = System.currentTimeMillis();
        mousePosition = new Point(0,0);

        this.gameStartTime = System.currentTimeMillis();
        this.lastSpawnTime = System.currentTimeMillis();
    }
    
    // gambar harta karun 
    private void uraiSpriteHartaKarun() {
        treasureSprites = new HashMap<>();
        if (treasuresSpriteSheet == null) return;

        int rows = treasuresSpriteSheet.getHeight() / treasureSpriteHeight;
        for (int r = 0; r < rows; r++) {
            List<BufferedImage> rowImages = new ArrayList<>();
            for (int c = 0; c < treasuresSpriteSheet.getWidth() / treasureSpriteWidth; c++) {
                int sourceX = c * treasureSpriteWidth;
                int sourceY = r * treasureSpriteHeight;
                if (sourceX + treasureSpriteWidth <= treasuresSpriteSheet.getWidth() &&
                        sourceY + treasureSpriteHeight <= treasuresSpriteSheet.getHeight()) {
                    BufferedImage sprite = treasuresSpriteSheet.getSubimage(
                            sourceX,
                            sourceY,
                            treasureSpriteWidth,
                            treasureSpriteHeight
                    );
                    rowImages.add(sprite);
                }
            }
            treasureSprites.put(r, rowImages);
        }
    }

    public void aturNamaPemain(String playerName) {
        this.playerName = playerName;
    }

    // reset game ke awal
    public void ulangPermainan() {
        player.setPosX(gamePanelWidth / 2 - (player.getDisplayWidth() / 2));
        player.setPosY(gamePanelHeight / 2 - (player.getDisplayHeight() / 2));
        player.setVelocityX(0);
        player.setVelocityY(0);
        isPlayerHurt = false;
        currentFramePlayer = 0;

        orcs.clear();
        treasures.clear();

        score = 0;
        treasuresCollectedCount = 0;
        isGameOver = false;
        gameStartTime = System.currentTimeMillis();
        lastSpawnTime = System.currentTimeMillis();

        isLassoActive = false;
        mousePosition = new Point(0, 0);
    }

    // posiis gameover dan simpan skor
    public void aturGameOver(boolean gameOver) {
        if (this.isGameOver == gameOver) return;

        this.isGameOver = gameOver;
        if (gameOver) {
            simpanHasilGame(score, treasuresCollectedCount);
            soundManager.playGameOverSound();
        }
    }

    // gerakan pemain (keybiard)
    public void aturGerakPemain(int keyCode, boolean isPressed) {
        if (isGameOver) return;

        if (isPressed) {
            if (keyCode == KeyEvent.VK_LEFT) {
                player.setVelocityX(-playerSpeed);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                player.setVelocityX(playerSpeed);
            } else if (keyCode == KeyEvent.VK_UP) {
                player.setVelocityY(-playerSpeed);
            } else if (keyCode == KeyEvent.VK_DOWN) {
                player.setVelocityY(playerSpeed);
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

    // switch laso
    public void aturLasoAktif(boolean active) {
        if (isGameOver) return;
        this.isLassoActive = active;
    }

    // baca posisi mouse 
    public void perbaruiPosisiMouse(int x, int y) {
        this.mousePosition.setLocation(x, y);
    }
    
    // memunculkan orc dan harta karun secara acak.
    private void munculinEntitas() {
        if (isGameOver) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > spawnInterval) {
            boolean spawnOrc = random.nextBoolean();
            boolean useTopLane = random.nextBoolean();

            int spawnY = useTopLane ? topLaneY : bottomLaneY;
            int initialVelocity;
            int spawnX;

            if (spawnOrc) {
                initialVelocity = useTopLane ? -orcSpeed : orcSpeed;
            } else {
                initialVelocity = useTopLane ? -treasureSpeed : treasureSpeed;
            }

            if (useTopLane) {
                spawnX = gamePanelWidth + 10;
            } else {
                if (spawnOrc) {
                    int orcWidth = (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50) * scaleFactorEnemy;
                    spawnX = -10 - orcWidth;
                } else {
                    int treasureWidth = treasureDisplaySize;
                    spawnX = -10 - treasureWidth;
                }
            }

            if (spawnOrc) {
                int originalOrcFrameWidth = fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getWidth() / 6 : 50;
                int totalOrcFrames = 6;
                int orcDisplayHeight = (fullSpriteSheetOrc != null ? fullSpriteSheetOrc.getHeight() : 50) * scaleFactorEnemy;
                
                if (spawnY + orcDisplayHeight > gamePanelHeight) {
                    spawnY = gamePanelHeight - orcDisplayHeight;
                }
                if (spawnY < 0) {
                    spawnY = 0;
                }

                Orc newOrc = new Orc(
                        spawnX, spawnY,
                        originalOrcFrameWidth * scaleFactorEnemy,
                        orcDisplayHeight,
                        fullSpriteSheetOrc,
                        initialVelocity,
                        originalOrcFrameWidth,
                        totalOrcFrames
                );
                orcs.add(newOrc);
            } else {
                int clusterSize = random.nextInt(3) + 2;
                int treasureLaneY = useTopLane ? topLaneY : bottomLaneY;

                if (treasureLaneY + treasureDisplaySize > gamePanelHeight) {
                    treasureLaneY = gamePanelHeight - treasureDisplaySize;
                }
                if (treasureLaneY < 0) {
                    treasureLaneY = 0;
                }

                if (treasureSprites == null || treasureSprites.isEmpty()) {
                    return;
                }
                int randomRow = random.nextInt(treasureSprites.size());
                List<BufferedImage> selectedSprites = treasureSprites.get(randomRow);
                if (selectedSprites == null || selectedSprites.isEmpty()) {
                    return;
                }
                int baseValue = (randomRow + 1) * 10;

                for (int i = 0; i < clusterSize; i++) {
                    BufferedImage treasureImage = selectedSprites.get(random.nextInt(selectedSprites.size()));
                    int currentSpawnX = spawnX;
                    if (useTopLane) {
                        currentSpawnX = spawnX + (i * (treasureDisplaySize + 2));
                    } else {
                        currentSpawnX = spawnX - (i * (treasureDisplaySize + 2));
                    }

                    Treasure newTreasure = new Treasure(
                            currentSpawnX, treasureLaneY,
                            treasureDisplaySize,
                            treasureDisplaySize,
                            treasureImage,
                            initialVelocity,
                            baseValue + random.nextInt(5)
                    );
                    treasures.add(newTreasure);
                }
            }
            lastSpawnTime = currentTime;
        }
    }
    
    // cek laso kena harta atau orc atau tidak
    private void cekKenaLaso() {
        if (isGameOver) return;

        int playerCenterX = player.getPosX() + player.getDisplayWidth() / 2;
        int playerCenterY = player.getPosY() + player.getDisplayHeight() / 2;
        int mouseX = mousePosition.x;
        int mouseY = mousePosition.y;

        double lassoGrabTolerance = 20;

        Iterator<Orc> orcIterator = orcs.iterator();
        while (orcIterator.hasNext()) {
            Orc orc = orcIterator.next();
            int orcCenterX = orc.getPosX() + orc.getDisplayWidth() / 2;
            int orcCenterY = orc.getPosY() + orc.getDisplayHeight() / 2;

            if (apaTitikAdaDiGaris(playerCenterX, playerCenterY, mouseX, mouseY, orcCenterX, orcCenterY, lassoGrabTolerance)) {
                score -= 100;
                orcIterator.remove();
                isPlayerHurt = true;
                hurtStartTime = System.currentTimeMillis();
                lastFrameTimePlayerHurt = System.currentTimeMillis();
                currentFramePlayerHurt = 1;

                soundManager.playHitSound();
            }
        }

        Iterator<Treasure> treasureIterator = treasures.iterator();
        while (treasureIterator.hasNext()) {
            Treasure treasure = treasureIterator.next();
            if (treasure.isCollected()) {
                continue;
            }

            int treasureCenterX = treasure.getPosX() + treasure.getDisplayWidth() / 2;
            int treasureCenterY = treasure.getPosY() + treasure.getDisplayHeight() / 2;

            if (apaTitikAdaDiGaris(playerCenterX, playerCenterY, mouseX, mouseY, treasureCenterX, treasureCenterY, lassoGrabTolerance)) {
                treasure.setCollected(true);
                treasure.setTargetX(chestPosX + chestDisplayWidth / 2 - treasure.getDisplayWidth() / 2);
                treasure.setTargetY(chestPosY + chestDisplayHeight / 2 - treasure.getDisplayHeight() / 2);
                treasure.setVelocityX(0);
            }
        }
    }

    private boolean apaTitikAdaDiGaris(int x1, int y1, int x2, int y2, int px, int py, double tolerance) {
        double lineLengthSq = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (lineLengthSq == 0) {
            return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2)) < tolerance;
        }

        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / lineLengthSq;
        t = Math.max(0, Math.min(1, t));

        int closestX = (int) (x1 + t * (x2 - x1));
        int closestY = (y1 + (int) (t * (y2 - y1)));

        double distance = Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));

        return distance < tolerance;
    }
    
    // perbarui state game.
    public void perbaruiGame() {
        if (isGameOver) return;

        if (System.currentTimeMillis() - gameStartTime >= gameDuration) {
            aturGameOver(true);
        }

        munculinEntitas();

        if (isPlayerHurt) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - hurtStartTime >= hurtDuration) {
                isPlayerHurt = false;
            } else {
                if (currentTime - lastFrameTimePlayerHurt > frameDelayPlayerHurt) {
                    currentFramePlayerHurt = (currentFramePlayerHurt == 1) ? 2 : 1;
                    lastFrameTimePlayerHurt = currentTime;
                }
            }
        }
        
        int newX = player.getPosX() + player.getVelocityX();
        int newY = player.getPosY() + player.getVelocityY();
        
        if (newX < 0) {
            newX = 0;
        }
        if (newX + player.getDisplayWidth() > gamePanelWidth) {
            newX = gamePanelWidth - player.getDisplayWidth();
        }
        
        if (newY < 0) {
            newY = 0;
        }
        if (newY + player.getDisplayHeight() > gamePanelHeight) {
            newY = gamePanelHeight - player.getDisplayHeight();
        }

        player.setPosX(newX);
        player.setPosY(newY);
        
        Iterator<Orc> orcIterator = orcs.iterator();
        while (orcIterator.hasNext()) {
            Orc orc = orcIterator.next();
            orc.setPosX(orc.getPosX() + orc.getVelocityX());

            if ((orc.getVelocityX() < 0 && orc.getPosX() + orc.getDisplayWidth() < -50) ||
                    (orc.getVelocityX() > 0 && orc.getPosX() > gamePanelWidth + 50)) {
                orcIterator.remove();
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - orc.getLastFrameTime() > orc.getFrameDelay()) {
                orc.setCurrentFrame((orc.getCurrentFrame() + 1) % orc.getTotalFrames());
                orc.setLastFrameTime(currentTime);
            }
        }

        Iterator<Treasure> treasureIterator = treasures.iterator();
        while (treasureIterator.hasNext()) {
            Treasure treasure = treasureIterator.next();
            if (treasure.isCollected()) {
                int dx = treasure.getTargetX() - treasure.getPosX();
                int dy = treasure.getTargetY() - treasure.getPosY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < treasureCollectSpeed) {
                    treasure.setPosX(treasure.getTargetX());
                    treasure.setPosY(treasure.getTargetY());
                    treasureIterator.remove();
                    score += treasure.getValue();
                    treasuresCollectedCount++;

                    soundManager.playCoinSound();

                } else {
                    treasure.setPosX(treasure.getPosX() + (int)(dx / distance * treasureCollectSpeed));
                    treasure.setPosY(treasure.getPosY() + (int)(dy / distance * treasureCollectSpeed));
                }
            } else {
                treasure.setPosX(treasure.getPosX() + treasure.getVelocityX());
                if ((treasure.getVelocityX() < 0 && treasure.getPosX() + treasure.getDisplayWidth() < -50) ||
                        (treasure.getVelocityX() > 0 && treasure.getPosX() > gamePanelWidth + 50)) {
                    treasureIterator.remove();
                }
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTimePlayer > frameDelayPlayer) {
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) {
                currentFramePlayer = (currentFramePlayer + 1) % totalFramesPlayer;
            }
            lastFrameTimePlayer = currentTime;
        }

        if (isLassoActive) {
            cekKenaLaso();
        }
    }

    // menyimpan hasil game ke database.
    private void simpanHasilGame(int skor, int count) {
        String queryCheck = "SELECT skor, count FROM thasil WHERE username = '" + this.playerName + "'";
        try (ResultSet rs = db.selectQuery(queryCheck)) {
            if (rs.next()) {
                int skorLama = rs.getInt("skor");
                int countLama = rs.getInt("count");

                int totalSkorBaru = skorLama + skor;
                int totalCountBaru = countLama + count;

                String sql = "UPDATE thasil SET skor = " + totalSkorBaru + ", count = " + totalCountBaru + " WHERE username = '" + this.playerName + "'";
                db.insertUpdateQuery(sql);
                
            } else {
                String sql = "INSERT INTO thasil (username, skor, count) VALUES ('" + this.playerName + "', " + skor + ", " + count + ")";
                db.insertUpdateQuery(sql);
            }
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    // daftar skor tertinggi dari database.
    public List<String[]> dapetinSkorTertinggi() {
        List<String[]> highScores = new ArrayList<>();
        String sql = "SELECT username, skor, count FROM thasil ORDER BY skor DESC";
        try (ResultSet rs = db.selectQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                int skor = rs.getInt("skor");
                int count = rs.getInt("count");
                highScores.add(new String[]{username, String.valueOf(skor), String.valueOf(count)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highScores;
    }

    // frame animasi pemain saat ini.
    public Image dapetinFramePemainSekarang() {
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

        int totalFramesInSheet = currentSpriteSheet.getWidth() / frameWidth;
        if(frameToUse >= totalFramesInSheet) {
            frameToUse = 0;
        }

        int sourceX = frameToUse * frameWidth;

        if (sourceX + frameWidth > currentSpriteSheet.getWidth()) {
            sourceX = 0;
        }

        return currentSpriteSheet.getSubimage(sourceX, 0, frameWidth, frameHeight);
    }
    
    // getter yang diakses oleh View
    public Image getGambarLatar() { return backgroundImage; }
    public int getXPemain() { return player.getPosX(); }
    public int getYPemain() { return player.getPosY(); }
    public int getLebarPemain() { return player.getDisplayWidth(); }
    public int getTinggiPemain() { return player.getDisplayHeight(); }
    public int getKecepatanXPemain() { return player.getVelocityX(); }
    public List<Orc> getParaOrc() { return orcs; }
    public List<Treasure> getHartaKarun() { return treasures; }
    public boolean apaLasoAktif() { return isLassoActive; }
    public Point getPosisiMouse() { return mousePosition; }
    public int getSkor() { return score; }
    public int getJumlahHartaTerkumpul() { return treasuresCollectedCount; }
    public Image getGambarPeti() { return chestOpenImage; }
    public int getXPeti() { return chestPosX; }
    public int getYPeti() { return chestPosY; }
    public int getLebarPeti() { return chestDisplayWidth; }
    public int getTinggiPeti() { return chestDisplayHeight; }
    public boolean apaPemainLagiSakit() { return isPlayerHurt; }
    public long getSisaWaktu() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        long remaining = gameDuration - elapsed;
        return Math.max(0, remaining);
    }
    public boolean apaGameSelesai() { return isGameOver; }
}