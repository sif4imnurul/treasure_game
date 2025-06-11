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
    private JButton playAgainButton;

    private static final int PANEL_WIDTH = 1200; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
    private static final int PANEL_HEIGHT = 800; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

    public GameView(GameViewModel viewModel, JFrame parentFrame) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        this.viewModel = viewModel;
        this.parentFrame = parentFrame;

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        this.setFocusable(true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

        this.addKeyListener(new KeyAdapter() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            @Override
            public void keyPressed(KeyEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                viewModel.setPlayerMovementDirection(e.getKeyCode(), true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }

            @Override
            public void keyReleased(KeyEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                viewModel.setPlayerMovementDirection(e.getKeyCode(), false); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }
        });

        this.addMouseListener(new MouseAdapter() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            @Override
            public void mousePressed(MouseEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                if (e.getButton() == MouseEvent.BUTTON1) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    viewModel.setLassoActive(true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                if (e.getButton() == MouseEvent.BUTTON1) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    viewModel.setLassoActive(false); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            @Override
            public void mouseMoved(MouseEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                viewModel.updateMousePosition(e.getX(), e.getY()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }

            @Override
            public void mouseDragged(MouseEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                viewModel.updateMousePosition(e.getX(), e.getY()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/viewmodel/GameViewModel.java
            }
        });

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 28));
        playAgainButton.setBackground(new Color(0, 150, 0));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.resetGame();
                parentFrame.getContentPane().removeAll();
                // When returning to StartView, create a new instance to refresh high scores
                StartView startView = new StartView(viewModel, parentFrame);
                parentFrame.add(startView);
                parentFrame.revalidate();
                parentFrame.repaint();
                startView.requestFocusInWindow();
            }
        });
        this.setLayout(null);
        this.add(playAgainButton);

        gameLoopTimer = new Timer(15, this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        gameLoopTimer.start(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        draw(g); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
    }

    private void draw(Graphics g) {
        Image backgroundImage = viewModel.getBackgroundImage(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        if (backgroundImage != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        } else {
            g.setColor(Color.BLACK); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.fillRect(0, 0, getWidth(), getHeight()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        }

        Image currentPlayerFrame = viewModel.getCurrentPlayerFrame(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int playerX = viewModel.getPlayerX(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int playerY = viewModel.getPlayerY(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int playerDisplayWidth = viewModel.getPlayerDisplayWidth(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int playerDisplayHeight = viewModel.getPlayerDisplayHeight(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int playerVelocityX = viewModel.getPlayerVelocityX(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

        if (currentPlayerFrame != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            if (playerVelocityX < 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                g.drawImage(currentPlayerFrame, playerX + playerDisplayWidth, playerY, -playerDisplayWidth, playerDisplayHeight, this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            } else {
                g.drawImage(currentPlayerFrame, playerX, playerY, playerDisplayWidth, playerDisplayHeight, this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }
        }

        List<Orc> orcs = viewModel.getOrcs(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        for (Orc orc : orcs) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            Image orcFrame = null; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            if (orc.getFullSpriteSheet() != null && orc.getOriginalFrameWidth() != 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                int sourceX = orc.getCurrentFrame() * orc.getOriginalFrameWidth(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                if (sourceX + orc.getOriginalFrameWidth() <= orc.getFullSpriteSheet().getWidth()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    orcFrame = orc.getFullSpriteSheet().getSubimage(sourceX, 0, orc.getOriginalFrameWidth(), orc.getFullSpriteSheet().getHeight()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                }
            }

            if (orcFrame != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                if (orc.getVelocityX() < 0) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    g.drawImage(orcFrame, orc.getPosX() + orc.getDisplayWidth(), orc.getPosY(), -orc.getDisplayWidth(), orc.getDisplayHeight(), this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                } else {
                    g.drawImage(orcFrame, orc.getPosX(), orc.getPosY(), orc.getDisplayWidth(), orc.getDisplayHeight(), this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                }
            } else {
                g.setColor(Color.RED); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                g.fillRect(orc.getPosX(), orc.getPosY(), orc.getDisplayWidth(), orc.getDisplayHeight()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }
        }

        List<Coin> coins = viewModel.getCoins(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        for (Coin coin : coins) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            Image coinImage = coin.getImage(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            if (coinImage != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                g.drawImage(coinImage, coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight(), this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            } else {
                g.setColor(Color.YELLOW); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                g.fillRect(coin.getPosX(), coin.getPosY(), coin.getDisplayWidth(), coin.getDisplayHeight()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }
        }

        g.setColor(Color.WHITE); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        g.setFont(new Font("Arial", Font.BOLD, 24)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        g.drawString("Score: " + viewModel.getScore(), 10, 30); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        g.drawString("Coins: " + viewModel.getCoinsCollectedCount(), 10, 90); // NEW: Display coins collected count

        long timeLeftMillis = viewModel.getTimeLeft(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        long secondsLeft = timeLeftMillis / 1000; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        long millisecondsLeft = timeLeftMillis % 1000; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

        String timeString = String.format("Time: %d.%03d", secondsLeft, millisecondsLeft); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        if (viewModel.isGameOver()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            timeString = "Time: 0.000 (Game Over!)"; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            gameLoopTimer.stop(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

            displayHighScores(g); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

            playAgainButton.setVisible(true); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            playAgainButton.setBounds((PANEL_WIDTH / 2) - 100, (PANEL_HEIGHT / 2) + 180, 200, 60); // Adjusted Y position
        } else {
            playAgainButton.setVisible(false); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        }
        g.drawString(timeString, 10, 60); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

        Image chestImage = viewModel.getChestOpenImage(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        if (chestImage != null) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.drawImage(chestImage, viewModel.getChestPosX(), viewModel.getChestPosY(), // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    viewModel.getChestDisplayWidth(), viewModel.getChestDisplayHeight(), this); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        } else {
            g.setColor(Color.BLUE); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.fillRect(viewModel.getChestPosX(), viewModel.getChestPosY(), // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                    viewModel.getChestDisplayWidth(), viewModel.getChestDisplayHeight()); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        }

        if (viewModel.isLassoActive()) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int playerCenterX = viewModel.getPlayerX() + viewModel.getPlayerDisplayWidth() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int playerCenterY = viewModel.getPlayerY() + viewModel.getPlayerDisplayHeight() / 2; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int mouseX = viewModel.getMousePosition().x; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int mouseY = viewModel.getMousePosition().y; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

            double distanceToMouse = Math.sqrt(Math.pow(mouseX - playerCenterX, 2) + Math.pow(mouseY - playerCenterY, 2)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int lassoRange = viewModel.getLassoRange(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            int endX, endY; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

            if (distanceToMouse > lassoRange) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                double ratio = lassoRange / distanceToMouse; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                endX = playerCenterX + (int)((mouseX - playerCenterX) * ratio); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                endY = playerCenterY + (int)((mouseY - playerCenterY) * ratio); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            } else {
                endX = mouseX; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
                endY = mouseY; // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            }

            g.setColor(Color.WHITE); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.drawLine(playerCenterX, playerCenterY, endX, endY); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            g.fillOval(endX - 5, endY - 5, 10, 10); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        }
    }

    private void displayHighScores(Graphics g) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        List<String[]> highScores = viewModel.getHighScores();
        g.setColor(Color.WHITE); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        g.setFont(new Font("Arial", Font.BOLD, 30)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        g.drawString("HIGHSCORES", (PANEL_WIDTH / 2) - 100, PANEL_HEIGHT / 2 - 100); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java

        // Draw table headers
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Username", (PANEL_WIDTH / 2) - 150, PANEL_HEIGHT / 2 - 50);
        g.drawString("Score", (PANEL_WIDTH / 2) + 10, PANEL_HEIGHT / 2 - 50);
        g.drawString("Count", (PANEL_WIDTH / 2) + 100, PANEL_HEIGHT / 2 - 50);

        // Draw high score entries
        g.setFont(new Font("Arial", Font.PLAIN, 20)); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        int yOffset = PANEL_HEIGHT / 2 - 20; // Adjusted starting Y for entries
        for (int i = 0; i < highScores.size(); i++) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
            String[] entry = highScores.get(i);
            g.drawString(entry[0], (PANEL_WIDTH / 2) - 150, yOffset + (i * 30));
            g.drawString(entry[1], (PANEL_WIDTH / 2) + 10, yOffset + (i * 30));
            g.drawString(entry[2], (PANEL_WIDTH / 2) + 100, yOffset + (i * 30));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        viewModel.updateGame(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
        repaint(); // cite: sif4imnurul/treasure_game/treasure_game-34ce5aec9cbe85cae6c5a94791c2845928cf197f/src/view/GameView.java
    }
}