import view.GameView;
import view.StartView;
import viewmodel.GameViewModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;

public class Main {
    private static final int FRAME_WIDTH = 1200; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java
    private static final int FRAME_HEIGHT = 800; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Treasure Game"); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java
            frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java
            frame.setLocationRelativeTo(null); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java
            frame.setResizable(false); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java

            GameViewModel viewModel = new GameViewModel(FRAME_WIDTH, FRAME_HEIGHT); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java

            // Create and add the StartView initially
            StartView startView = new StartView(viewModel, frame);
            frame.add(startView);

            frame.pack(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java
            frame.setVisible(true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/Main.java

            startView.requestFocusInWindow(); // Ensure startView has focus
        });
    }
}