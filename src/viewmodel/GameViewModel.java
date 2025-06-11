package viewmodel;

import model.Player;
import java.awt.Image;
import java.net.URL;
import java.awt.image.BufferedImage; // Untuk bekerja dengan sub-gambar (frame)
import javax.imageio.ImageIO; // Untuk membaca gambar dari file
import java.io.IOException; // Untuk menangani kesalahan I/O

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

    // --- Variabel baru untuk animasi dan penskalaan ---
    private BufferedImage fullSpriteSheet; // Menyimpan seluruh gambar sprite sheet
    private int currentFrame = 0; // Indeks frame yang sedang ditampilkan
    private int originalFrameWidth; // Lebar asli satu frame sprite (misal 20px)
    private int originalFrameHeight; // Tinggi asli satu frame sprite (misal 30px)
    private int totalFrames; // Jumlah total frame dalam animasi
    private long lastFrameTime; // Waktu terakhir frame diperbarui
    private final long FRAME_DELAY = 100; // Penundaan (ms) antar frame untuk kecepatan animasi (ubah sesuai keinginan)

    private final int SCALE_FACTOR = 4; // Faktor penskalaan (misal 2x, 3x, 4x)
    // --- Akhir variabel baru ---

    public GameViewModel(int panelWidth, int panelHeight) {
        this.gamePanelWidth = panelWidth;
        this.gamePanelHeight = panelHeight;

        // Mendapatkan URL ke gambar sprite sheet Anda
        // Pastikan 'soldier-walk.png' ada di folder 'assets' (src/main/resources/assets/)
        URL playerImageUrl = getClass().getClassLoader().getResource("assets/soldier-walk.png");

        if (playerImageUrl != null) {
            try {
                // Membaca seluruh sprite sheet sebagai BufferedImage
                fullSpriteSheet = ImageIO.read(playerImageUrl);
                System.out.println("Sprite sheet berhasil dimuat dari: " + playerImageUrl);

                // --- Tentukan dimensi sprite asli dan total frame ---
                // PENTING: Anda HARUS menyesuaikan nilai-nilai ini berdasarkan gambar
                // 'soldier-walk.png' Anda yang sebenarnya.
                // Dari gambar yang Anda berikan, asumsi lebar satu sprite sekitar 20px
                // dan tinggi sekitar 30px. Dan ada 9 frame total.
                originalFrameWidth = fullSpriteSheet.getWidth() / 9; // Total lebar gambar dibagi jumlah frame
                originalFrameHeight = fullSpriteSheet.getHeight(); // Jika semua frame berada dalam satu baris
                totalFrames = 9; // Total jumlah frame dalam sprite sheet Anda
                // --- Akhir penentuan dimensi sprite ---

            } catch (IOException e) {
                System.err.println("ERROR: Gagal memuat sprite sheet: " + e.getMessage());
                fullSpriteSheet = null;
            }
        } else {
            System.err.println("ERROR: Sprite sheet tidak ditemukan di assets/soldier-walk.png. Pastikan jalur dan penempatan file benar.");
            fullSpriteSheet = null;
        }

        // Hitung lebar dan tinggi tampilan pemain setelah diskalakan
        int playerDisplayWidth = originalFrameWidth * SCALE_FACTOR;
        int playerDisplayHeight = originalFrameHeight * SCALE_FACTOR;

        // Menginisialisasi objek Player
        // Ukuran lebar dan tinggi pemain sekarang adalah ukuran yang diskalakan
        this.player = new Player(
                panelWidth / 2 - (playerDisplayWidth / 2), // Posisikan pemain di tengah horizontal
                panelHeight / 2 - (playerDisplayHeight / 2), // Posisikan pemain di tengah vertikal
                playerDisplayWidth, // Lebar tampilan pemain (sudah diskalakan)
                playerDisplayHeight, // Tinggi tampilan pemain (sudah diskalakan)
                fullSpriteSheet // Player object akan menyimpan seluruh sprite sheet
        );

        lastFrameTime = System.currentTimeMillis(); // Inisialisasi waktu frame terakhir
    }

    // Mengatur arah gerakan pemain berdasarkan input keyboard
    public void setPlayerMovementDirection(int direction) {
        switch (direction) {
            case STOP_HORIZONTAL:
                player.setVelocityX(0);
                // Ketika berhenti, bisa diatur ke frame idle (misal frame 0)
                // currentFrame = 0;
                break;
            case LEFT:
                player.setVelocityX(-PLAYER_SPEED);
                break;
            case RIGHT:
                player.setVelocityX(PLAYER_SPEED);
                break;
            case STOP_VERTICAL:
                player.setVelocityY(0);
                // Ketika berhenti, bisa diatur ke frame idle (misal frame 0)
                // currentFrame = 0;
                break;
            case UP:
                player.setVelocityY(-PLAYER_SPEED);
                break;
            case DOWN:
                player.setVelocityY(PLAYER_SPEED);
                break;
        }
    }

    // Memperbarui logika game, termasuk posisi pemain dan animasi
    public void updateGame() {
        player.updatePosition(); // Perbarui posisi pemain

        // Batasi posisi pemain agar tidak keluar dari batas panel
        int newX = player.getPosX();
        int newY = player.getPosY();

        // Menggunakan getDisplayWidth/Height untuk batasan agar sesuai ukuran tampilan
        newX = Math.max(0, Math.min(newX, gamePanelWidth - player.getDisplayWidth()));
        newY = Math.max(0, Math.min(newY, gamePanelHeight - player.getDisplayHeight()));

        player.setPosX(newX);
        player.setPosY(newY);

        // --- Logika pembaruan animasi ---
        long currentTime = System.currentTimeMillis();
        // Jika sudah waktunya untuk mengganti frame
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            // Animasi hanya berjalan jika pemain bergerak
            if (player.getVelocityX() != 0 || player.getVelocityY() != 0) {
                currentFrame = (currentFrame + 1) % totalFrames; // Lanjut ke frame berikutnya, putar kembali ke awal jika sudah habis
            } else {
                currentFrame = 0; // Jika tidak bergerak, tampilkan frame pertama (idle)
            }
            lastFrameTime = currentTime; // Perbarui waktu frame terakhir
        }
        // --- Akhir logika pembaruan animasi ---
    }

    // --- Metode baru untuk mendapatkan frame pemain saat ini untuk digambar ---
    public Image getCurrentPlayerFrame() {
        if (fullSpriteSheet == null || originalFrameWidth == 0 || originalFrameHeight == 0) {
            return null; // Pastikan sprite sheet sudah dimuat dan dimensi valid
        }
        // Hitung posisi X dari frame saat ini dalam sprite sheet
        int sourceX = currentFrame * originalFrameWidth;
        // Mengambil sub-gambar (frame) dari sprite sheet penuh
        return fullSpriteSheet.getSubimage(sourceX, 0, originalFrameWidth, originalFrameHeight);
    }
    // --- Akhir metode baru ---

    // --- Getter untuk properti pemain (digunakan oleh GameView) ---
    public int getPlayerX() { return player.getPosX(); }
    public int getPlayerY() { return player.getPosY(); }
    public int getPlayerDisplayWidth() { return player.getDisplayWidth(); } // Mengembalikan lebar tampilan pemain (setelah diskalakan)
    public int getPlayerDisplayHeight() { return player.getDisplayHeight(); } // Mengembalikan tinggi tampilan pemain (setelah diskalakan)
}