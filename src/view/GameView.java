package view;

import viewmodel.GameViewModel;
import model.Orc;
import model.Coin; // Import Coin
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class GameView extends JPanel implements ActionListener {
    private GameViewModel viewModel;
    private Timer gameLoopTimer;

    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 800;

    public GameView(GameViewModel viewModel) {
        this.viewModel = viewModel;

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                viewModel.setPlayerMovementDirection(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                viewModel.setPlayerMovementDirection(e.getKeyCode(), false);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    viewModel.setLassoActive(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    viewModel.setLassoActive(false);
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                viewModel.updateMousePosition(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                viewModel.updateMousePosition(e.getX(), e.getY());
            }
        });

        gameLoopTimer = new Timer(15, this);
        gameLoopTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Gambar latar belakang
        Image backgroundImage = viewModel.getBackgroundImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Gambar pemain
        Image currentPlayerFrame = viewModel.getCurrentPlayerFrame();
        int playerX = viewModel.getPlayerX();
        int playerY = viewModel.getPlayerY();
        int playerDisplayWidth = viewModel.getPlayerDisplayWidth();
        int playerDisplayHeight = viewModel.getPlayerDisplayHeight();
        int playerVelocityX = viewModel.getPlayerVelocityX();

        if (currentPlayerFrame != null) {
            if (playerVelocityX < 0) {
                g.drawImage(currentPlayerFrame, playerX + playerDisplayWidth, playerY, -playerDisplayWidth, playerDisplayHeight, this);
            } else {
                g.drawImage(currentPlayerFrame, playerX, playerY, playerDisplayWidth, playerDisplayHeight, this);
            }
        }

        // Gambar Orc tunggal (jika masih ada)
        Orc singleOrc = viewModel.getSingleOrc();
        if (singleOrc != null) {
            Image orcFrame = singleOrc.getCurrentFrameImage();
            if (orcFrame != null) {
                if (singleOrc.getVelocityX() < 0) {
                    g.drawImage(orcFrame, singleOrc.getPosX() + singleOrc.getDisplayWidth(), singleOrc.getPosY(), -singleOrc.getDisplayWidth(), singleOrc.getDisplayHeight(), this);
                } else {
                    g.drawImage(orcFrame, singleOrc.getPosX(), singleOrc.getPosY(), singleOrc.getDisplayWidth(), singleOrc.getDisplayHeight(), this);
                }
            } else {
                g.setColor(Color.RED);
                g.fillRect(singleOrc.getPosX(), singleOrc.getPosY(), singleOrc.getDisplayWidth(), singleOrc.getDisplayHeight());
            }
        }

        // Gambar Coins
        List<Coin> coins = viewModel.getCoins(); // Menggunakan getCoins()
        for (Coin coin : coins) {
            Image coinImage = coin.getImage(); // Menggunakan getImage()
            if (coinImage != null) {
                if (coin.getVelocityX() < 0) { // Jika koin bergerak ke kiri
                    g.drawImage(coinImage, coin.getPosX() + coin.getDisplayWidth(), coin.getPosY(), -coin.getDisplayWidth(), coin.getDisplayHeight(), this);
                } else { // Jika koin bergerak ke kanan
                    g.drawImage(coinImage, coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight(), this);
                }
            } else {
                g.setColor(Color.YELLOW); // Warna default jika gambar tidak ada
                g.fillRect(coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight());
            }
        }

        // Gambar laso
        if (viewModel.isLassoActive()) {
            g.setColor(Color.WHITE);
            g.drawLine(playerX + playerDisplayWidth / 2, playerY + playerDisplayHeight / 2,
                    viewModel.getMousePosition().x, viewModel.getMousePosition().y);
            g.fillOval(viewModel.getMousePosition().x - 5, viewModel.getMousePosition().y - 5, 10, 10);
        }

        // Gambar skor
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + viewModel.getScore(), 10, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.updateGame();
        repaint();
    }
}