package view;

import viewmodel.GameViewModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameView extends JPanel implements ActionListener {
    private GameViewModel viewModel; // ViewModel untuk data game
    private Timer gameLoopTimer; // Timer untuk loop game

    private static final int PANEL_WIDTH = 1200; // Lebar panel game
    private static final int PANEL_HEIGHT = 800; // Tinggi panel game

    public GameView(GameViewModel viewModel) {
        this.viewModel = viewModel; // Inisialisasi ViewModel

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); // Atur ukuran panel
        this.setFocusable(true); // Panel dapat menerima input keyboard

        this.addKeyListener(new KeyAdapter() { // Tambahkan listener keyboard
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode(); // Dapatkan kode tombol
                if (key == KeyEvent.VK_LEFT) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.LEFT); //
                } else if (key == KeyEvent.VK_RIGHT) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.RIGHT); //
                } else if (key == KeyEvent.VK_UP) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.UP); //
                } else if (key == KeyEvent.VK_DOWN) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.DOWN); //
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode(); // Dapatkan kode tombol
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.STOP_HORIZONTAL); //
                } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) { //
                    viewModel.setPlayerMovementDirection(GameViewModel.STOP_VERTICAL); //
                }
            }
        });

        gameLoopTimer = new Timer(15, this); // Timer untuk ~60 FPS (1000ms / 60)
        gameLoopTimer.start(); // Mulai timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Panggil implementasi JPanel
        draw(g); // Panggil metode draw
    }

    private void draw(Graphics g) {
        // Gambar latar belakang
        Image backgroundImage = viewModel.getBackgroundImage(); //
        if (backgroundImage != null) { //
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Gambar latar belakang
        } else {
            g.setColor(Color.BLACK); // Jika gagal, gunakan latar hitam
            g.fillRect(0, 0, getWidth(), getHeight()); //
        }

        // Gambar pemain
        Image currentPlayerFrame = viewModel.getCurrentPlayerFrame(); // Frame pemain saat ini
        int playerX = viewModel.getPlayerX(); // Posisi X pemain
        int playerY = viewModel.getPlayerY(); // Posisi Y pemain
        int playerDisplayWidth = viewModel.getPlayerDisplayWidth(); // Lebar tampilan pemain
        int playerDisplayHeight = viewModel.getPlayerDisplayHeight(); // Tinggi tampilan pemain
        int playerVelocityX = viewModel.getPlayerVelocityX(); // Kecepatan X pemain

        if (currentPlayerFrame != null) { // Jika frame pemain valid
            if (playerVelocityX < 0) { // Jika bergerak ke kiri
                // Gambar dibalik secara horizontal
                g.drawImage(currentPlayerFrame, playerX + playerDisplayWidth, playerY, -playerDisplayWidth, playerDisplayHeight, this);
            } else { // Bergerak ke kanan atau diam
                g.drawImage(currentPlayerFrame, playerX, playerY, playerDisplayWidth, playerDisplayHeight, this); // Gambar normal
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.updateGame(); // Perbarui logika game
        repaint(); // Gambar ulang panel
    }
}