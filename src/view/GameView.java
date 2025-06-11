package view;

import viewmodel.GameViewModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameView extends JPanel implements ActionListener {
    private GameViewModel viewModel;
    private Timer gameLoopTimer; // Timer untuk loop game

    private static final int PANEL_WIDTH = 1200; // Lebar panel game
    private static final int PANEL_HEIGHT = 800; // Tinggi panel game

    public GameView(GameViewModel viewModel) {
        this.viewModel = viewModel;

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        // Latar belakang sekarang akan digambar oleh gambar, bukan warna solid
        // this.setBackground(Color.BLACK); // Dihapus karena akan diganti dengan gambar latar belakang
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    viewModel.setPlayerMovementDirection(GameViewModel.LEFT);
                } else if (key == KeyEvent.VK_RIGHT) {
                    viewModel.setPlayerMovementDirection(GameViewModel.RIGHT);
                } else if (key == KeyEvent.VK_UP) {
                    viewModel.setPlayerMovementDirection(GameViewModel.UP);
                } else if (key == KeyEvent.VK_DOWN) {
                    viewModel.setPlayerMovementDirection(GameViewModel.DOWN);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                    viewModel.setPlayerMovementDirection(GameViewModel.STOP_HORIZONTAL);
                } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                    viewModel.setPlayerMovementDirection(GameViewModel.STOP_VERTICAL);
                }
            }
        });

        gameLoopTimer = new Timer(16, this); // 1000ms / 60fps = ~16ms per frame
        gameLoopTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // 1. Gambar latar belakang terlebih dahulu
        Image backgroundImage = viewModel.getBackgroundImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Jika gambar latar belakang gagal dimuat, fallback ke latar belakang hitam
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Gambar pemain
        // Mempertimbangkan arah gerakan untuk mirroring (flipping)
        Image currentPlayerFrame = viewModel.getCurrentPlayerFrame();
        int playerX = viewModel.getPlayerX();
        int playerY = viewModel.getPlayerY();
        int playerDisplayWidth = viewModel.getPlayerDisplayWidth();
        int playerDisplayHeight = viewModel.getPlayerDisplayHeight();
        int playerVelocityX = viewModel.getPlayerVelocityX(); // Dapatkan kecepatan X dari ViewModel

        if (currentPlayerFrame != null) {
            if (playerVelocityX < 0) { // Jika bergerak ke kiri, balik gambar
                g.drawImage(currentPlayerFrame, playerX + playerDisplayWidth, playerY, -playerDisplayWidth, playerDisplayHeight, this);
            } else { // Bergerak ke kanan atau diam, gambar normal
                g.drawImage(currentPlayerFrame, playerX, playerY, playerDisplayWidth, playerDisplayHeight, this);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.updateGame();
        repaint();
    }
}