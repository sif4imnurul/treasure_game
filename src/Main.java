// Saya sifa imania dengan NIM 2312084 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah 
// Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya 
// tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin. 

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

            frame.pack(); // Hitung ukuran window 
            frame.setLocationRelativeTo(null); // atur lokasi ke tengah
            frame.setVisible(true); // Tampilkan window

            startView.requestFocusInWindow();
        });
    }
}