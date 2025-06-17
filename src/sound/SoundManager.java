package sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class SoundManager {

    private Clip coinSound, hitSound, gameOverSound, backgroundMusic;

    // memuat semua file suara 
    public SoundManager() {
        coinSound = loadSound("/assets/coin.wav");
        hitSound = loadSound("/assets/hit.wav");
        gameOverSound = loadSound("/assets/game-over.wav");
        backgroundMusic = loadSound("/assets/backsound.wav");
    }


    // ubah file suara supaya bisa di play
    private Clip loadSound(String path) {
        try {
            InputStream audioSrc = SoundManager.class.getResourceAsStream(path);
            if (audioSrc == null) {
                // System.err.println(" gagal " + path);
                return null;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            // System.err.println("gagal " + path);
            e.printStackTrace();
            return null;
        }
    }

    // memainkan suara sekali
    private void playClip(Clip clip) {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop(); // hentikan kalau sedang berjalan
            }
            clip.setFramePosition(0); // mulai dari awal
            clip.start();
        }
    }

    // suara koin.
    public void playCoinSound() {
        playClip(coinSound);
    }

    // suara pemain kena serangan orc
    public void playHitSound() {
        playClip(hitSound);
    }
    // suara game over.
    public void playGameOverSound() {
        playClip(gameOverSound);
    }

    // memainkan musik latar 
    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            // hanya akan mulai jika si musiknya belum berjalan.
            if (backgroundMusic.isRunning()) {
                return;
            }
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // menghentikan musik latar berulang kali.
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}