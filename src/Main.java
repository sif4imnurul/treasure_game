import view.GameView;
import viewmodel.GameViewModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities; // Untuk menjalankan kode Swing di Event Dispatch Thread (EDT)

public class Main {
    private static final int FRAME_WIDTH = 1200; // Lebar jendela utama
    private static final int FRAME_HEIGHT = 800; // Tinggi jendela utama

    public static void main(String[] args) {
        // Memastikan kode Swing berjalan di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Membuat instance GameViewModel dengan ukuran panel
            GameViewModel viewModel = new GameViewModel(FRAME_WIDTH, FRAME_HEIGHT);
            // Membuat instance GameView dengan ViewModel
            GameView gameView = new GameView(viewModel);

            // Membuat JFrame (jendela utama aplikasi)
            JFrame frame = new JFrame("MVVM Game");
            frame.add(gameView); // Menambahkan panel game ke frame
            frame.pack(); // Mengatur ukuran frame agar sesuai dengan preferredSize dari komponen
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Mengatur operasi penutupan frame
            frame.setLocationRelativeTo(null); // Menempatkan frame di tengah layar
            frame.setResizable(false); // Mencegah jendela diubah ukurannya
            frame.setVisible(true); // Menampilkan jendela

            // Memastikan GameView mendapatkan fokus untuk menerima input keyboard
            gameView.requestFocusInWindow();
        });
    }
}