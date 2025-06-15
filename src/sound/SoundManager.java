package sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class SoundManager {

    private Clip coinSound, hitSound, gameOverSound, backgroundMusic;

    public SoundManager() {
        // Muat semua file suara .wav saat kelas ini dibuat
        coinSound = loadSound("/assets/coin.wav");
        hitSound = loadSound("/assets/hit.wav");
        gameOverSound = loadSound("/assets/game-over.wav");
        backgroundMusic = loadSound("/assets/backsound.wav");
    }

    private Clip loadSound(String path) {
        try {
            // Menggunakan getResourceAsStream untuk memuat dari folder resources
            // Ini adalah cara yang paling andal untuk memastikan file ditemukan
            InputStream audioSrc = SoundManager.class.getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("File suara tidak ditemukan di path: " + path);
                return null;
            }

            // Bungkus dengan BufferedInputStream untuk performa yang lebih baik
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            System.err.println("Gagal memuat suara dari path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Metode untuk memainkan suara efek (satu kali)
    private void playClip(Clip clip) {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop(); // Hentikan jika sedang berjalan
            }
            clip.setFramePosition(0); // Selalu mulai dari awal
            clip.start();
        }
    }

    // Metode publik untuk dipanggil dari ViewModel
    public void playCoinSound() {
        playClip(coinSound);
    }

    public void playHitSound() {
        playClip(hitSound);
    }

    public void playGameOverSound() {
        playClip(gameOverSound);
    }

    // Metode untuk memainkan musik latar secara berulang
    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            // HANYA mulai jika musiknya belum berjalan.
            if (backgroundMusic.isRunning()) {
                return;
            }
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Metode untuk menghentikan musik latar
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}