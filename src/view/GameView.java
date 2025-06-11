package view;

import model.Player;
import viewmodel.GameViewModel;
import model.Orc;
import model.Coin;
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
    private JFrame parentFrame;

    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 800;

    public GameView(GameViewModel viewModel, JFrame parentFrame) {
        this.viewModel = viewModel;
        this.parentFrame = parentFrame;

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // If Spacebar is pressed, stop the game and return to StartView
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!viewModel.isGameOver()) { // Only do this if the game isn't already over
                        gameLoopTimer.stop();
                        viewModel.setGameOver(true); // Set game over explicitly, this will save the score

                        // Navigate back to StartView
                        parentFrame.getContentPane().removeAll();
                        StartView startView = new StartView(viewModel, parentFrame);
                        parentFrame.add(startView);
                        parentFrame.revalidate();
                        parentFrame.repaint();
                        startView.requestFocusInWindow();
                    }
                } else {
                    viewModel.setPlayerMovementDirection(e.getKeyCode(), true);
                }
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

        this.setLayout(null);

        gameLoopTimer = new Timer(15, this);
        gameLoopTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        Image backgroundImage = viewModel.getBackgroundImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

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

        List<Orc> orcs = viewModel.getOrcs();
        for (Orc orc : orcs) {
            Image orcFrame = null;
            if (orc.getFullSpriteSheet() != null && orc.getOriginalFrameWidth() != 0) {
                int sourceX = orc.getCurrentFrame() * orc.getOriginalFrameWidth();
                if (sourceX + orc.getOriginalFrameWidth() <= orc.getFullSpriteSheet().getWidth()) {
                    orcFrame = orc.getFullSpriteSheet().getSubimage(sourceX, 0, orc.getOriginalFrameWidth(), orc.getFullSpriteSheet().getHeight());
                }
            }

            if (orcFrame != null) {
                if (orc.getVelocityX() < 0) {
                    g.drawImage(orcFrame, orc.getPosX() + orc.getDisplayWidth(), orc.getPosY(), -orc.getDisplayWidth(), orc.getDisplayHeight(), this);
                } else {
                    g.drawImage(orcFrame, orc.getPosX(), orc.getPosY(), orc.getDisplayWidth(), orc.getDisplayHeight(), this);
                }
            } else {
                g.setColor(Color.RED);
                g.fillRect(orc.getPosX(), orc.getPosY(), orc.getDisplayWidth(), orc.getDisplayHeight());
            }
        }

        List<Coin> coins = viewModel.getCoins();
        for (Coin coin : coins) {
            Image coinImage = coin.getImage();
            if (coinImage != null) {
                g.drawImage(coinImage, coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight(), this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight());
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + viewModel.getScore(), 10, 30);
        g.drawString("Coins: " + viewModel.getCoinsCollectedCount(), 10, 90);

        long timeLeftMillis = viewModel.getTimeLeft();
        long secondsLeft = timeLeftMillis / 1000;
        long millisecondsLeft = timeLeftMillis % 1000;

        String timeString = String.format("Time: %d.%03d", secondsLeft, millisecondsLeft);
        if (viewModel.isGameOver()) {
            timeString = "Time: 0.000 (Game Over!)";
            gameLoopTimer.stop(); // Stop the game loop when game is over

            // Immediately navigate back to StartView when game is over
            parentFrame.getContentPane().removeAll();
            StartView startView = new StartView(viewModel, parentFrame);
            parentFrame.add(startView);
            parentFrame.revalidate();
            parentFrame.repaint();
            startView.requestFocusInWindow();
        }
        g.drawString(timeString, 10, 60);

        Image chestImage = viewModel.getChestOpenImage();
        if (chestImage != null) {
            g.drawImage(chestImage, viewModel.getChestPosX(), viewModel.getChestPosY(),
                    viewModel.getChestDisplayWidth(), viewModel.getChestDisplayHeight(), this);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(viewModel.getChestPosX(), viewModel.getChestPosY(),
                    viewModel.getChestDisplayWidth(), viewModel.getChestDisplayHeight());
        }

        if (viewModel.isLassoActive()) {
            int playerCenterX = viewModel.getPlayerX() + viewModel.getPlayerDisplayWidth() / 2;
            int playerCenterY = viewModel.getPlayerY() + viewModel.getPlayerDisplayHeight() / 2;
            int mouseX = viewModel.getMousePosition().x;
            int mouseY = viewModel.getMousePosition().y;

            double distanceToMouse = Math.sqrt(Math.pow(mouseX - playerCenterX, 2) + Math.pow(mouseY - playerCenterY, 2));
            int lassoRange = viewModel.getLassoRange();
            int endX, endY;

            if (distanceToMouse > lassoRange) {
                double ratio = lassoRange / distanceToMouse;
                endX = playerCenterX + (int)((mouseX - playerCenterX) * ratio);
                endY = playerCenterY + (int)((mouseY - playerCenterY) * ratio);
            } else {
                endX = mouseX;
                endY = mouseY;
            }

            g.setColor(Color.WHITE);
            g.drawLine(playerCenterX, playerCenterY, endX, endY);
            g.fillOval(endX - 5, endY - 5, 10, 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.updateGame();
        repaint();
    }
}