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

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); // Mengatur ukuran panel
        this.setBackground(Color.BLACK); // Mengatur warna latar belakang
        this.setFocusable(true); // Memungkinkan panel menerima input keyboard

        // Menambahkan KeyListener untuk menangani input keyboard
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

        // Mengatur game loop timer (sekitar 60 FPS)
        gameLoopTimer = new Timer(16, this); // 1000ms / 60fps = ~16ms per frame
        gameLoopTimer.start(); // Memulai timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Panggil implementasi superclass
        draw(g); // Panggil metode draw untuk menggambar elemen game
    }

    private void draw(Graphics g) {
        // Menggambar frame pemain saat ini dengan ukuran yang diskalakan
        g.drawImage(viewModel.getCurrentPlayerFrame(), // Mendapatkan frame animasi saat ini
                viewModel.getPlayerX(), // Posisi X pemain di layar
                viewModel.getPlayerY(), // Posisi Y pemain di layar
                viewModel.getPlayerDisplayWidth(), // Lebar tampilan pemain (setelah diskalakan)
                viewModel.getPlayerDisplayHeight(), // Tinggi tampilan pemain (setelah diskalakan)
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.updateGame(); // Perbarui status game
        repaint(); // Minta panel untuk digambar ulang (memanggil paintComponent)
    }
}