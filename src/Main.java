// File: Main.java
import view.GameView;
import viewmodel.GameViewModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameViewModel viewModel = new GameViewModel(FRAME_WIDTH, FRAME_HEIGHT);
            GameView gameView = new GameView(viewModel);

            JFrame frame = new JFrame("MVVM Game");
            frame.add(gameView);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);

            gameView.requestFocusInWindow();
        });
    }
}