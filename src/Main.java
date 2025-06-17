// Saya Sifa Imania Nurul Hidayah mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah 
// Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya 
// tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin. 

// Lisensi
// backsound-game = https://freetouse.com/music (Zambolino-playing games)
// collect treasure = https://mixkit.co/free-sound-effects (mixkit-coins-sound-2003)
// minus score = https://mixkit.co/free-sound-effects/ (mixkit-paper-quick-slice-2384)
// gameover =  https://mixkit.co/free-sound-effects (mixkit-funny-fail-low-tone-2876)

import view.StartView;
import viewmodel.GameViewModel;
import sound.SoundManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

public class Main {
    private static final int frameWidth = 1200;
    private static final int frameHeight = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Treasure Lasso");
            frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SoundManager soundManager = new SoundManager();
            soundManager.playBackgroundMusic();

            GameViewModel viewModel = new GameViewModel(frameWidth, frameHeight, soundManager);
            StartView startView = new StartView(viewModel, frame);
            frame.add(startView);

            frame.pack(); 
            frame.setLocationRelativeTo(null);
            frame.setVisible(true); 

            startView.requestFocusInWindow();
        });
    }
}