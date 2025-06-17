
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
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Treasure Game");
            frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // 1. Buat instance SoundManager di sini.
            SoundManager soundManager = new SoundManager();

            // 2. Langsung mainkan musik latar.
            // Musik akan mulai saat aplikasi pertama kali dijalankan.
            soundManager.playBackgroundMusic();

            // 3. Teruskan instance soundManager ke GameViewModel.
            GameViewModel viewModel = new GameViewModel(FRAME_WIDTH, FRAME_HEIGHT, soundManager);

            // StartView akan ditampilkan dengan musik yang sudah berputar.
            StartView startView = new StartView(viewModel, frame);
            frame.add(startView);

            frame.pack();
            frame.setVisible(true);

            startView.requestFocusInWindow();
        });
    }
}