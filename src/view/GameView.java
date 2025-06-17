package view;

import model.Player;
import viewmodel.GameViewModel;
import model.Orc;
import model.Treasure;
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
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!viewModel.apaGameSelesai()) {
                        gameLoopTimer.stop();
                        viewModel.aturGameOver(true);

                        parentFrame.getContentPane().removeAll();
                        StartView startView = new StartView(viewModel, parentFrame);
                        parentFrame.add(startView);
                        parentFrame.revalidate();
                        parentFrame.repaint();
                        startView.requestFocusInWindow();
                    }
                } else {
                    viewModel.aturGerakPemain(e.getKeyCode(), true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                viewModel.aturGerakPemain(e.getKeyCode(), false);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    viewModel.aturLasoAktif(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    viewModel.aturLasoAktif(false);
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                viewModel.perbaruiPosisiMouse(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                viewModel.perbaruiPosisiMouse(e.getX(), e.getY());
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
        Image backgroundImage = viewModel.getGambarLatar();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        Image currentPlayerFrame = viewModel.dapetinFramePemainSekarang();
        int playerX = viewModel.getXPemain();
        int playerY = viewModel.getYPemain();
        int playerDisplayWidth = viewModel.getLebarPemain();
        int playerDisplayHeight = viewModel.getTinggiPemain();
        int playerVelocityX = viewModel.getKecepatanXPemain();

        if (currentPlayerFrame != null) {
            if (playerVelocityX < 0) {
                g.drawImage(currentPlayerFrame, playerX + playerDisplayWidth, playerY, -playerDisplayWidth, playerDisplayHeight, this);
            } else {
                g.drawImage(currentPlayerFrame, playerX, playerY, playerDisplayWidth, playerDisplayHeight, this);
            }
        }

        List<Orc> orcs = viewModel.getParaOrc();
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

        List<Treasure> treasures = viewModel.getHartaKarun();
        for (Treasure treasure : treasures) {
            Image treasureImage = treasure.getImage();
            if (treasureImage != null) {
                g.drawImage(treasureImage, treasure.getPosX(), treasure.getPosY(), treasure.getDisplayWidth(), treasure.getDisplayHeight(), this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(treasure.getPosX(), treasure.getPosY(), treasure.getDisplayWidth(), treasure.getDisplayHeight());
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.BOLD, 24));
        g.drawString("Score: " + viewModel.getSkor(), 10, 30);
        g.drawString("Harta Karun Terkumpul: " + viewModel.getJumlahHartaTerkumpul(), 10, 60);

        long timeLeftMillis = viewModel.getSisaWaktu();
        long secondsLeft = timeLeftMillis / 1000;
        long millisecondsLeft = timeLeftMillis % 1000;

        String timeString = String.format("Time: %d.%03d", secondsLeft, millisecondsLeft);
        if (viewModel.apaGameSelesai()) {
            timeString = "Time: 0.000 (Game Over!)";
            gameLoopTimer.stop();

            parentFrame.getContentPane().removeAll();
            StartView startView = new StartView(viewModel, parentFrame);
            parentFrame.add(startView);
            parentFrame.revalidate();
            parentFrame.repaint();
            startView.requestFocusInWindow();
        }
        g.drawString(timeString, 10, 90);

        Image chestImage = viewModel.getGambarPeti();
        if (chestImage != null) {
            g.drawImage(chestImage, viewModel.getXPeti(), viewModel.getYPeti(),
                    viewModel.getLebarPeti(), viewModel.getTinggiPeti(), this);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(viewModel.getXPeti(), viewModel.getYPeti(),
                    viewModel.getLebarPeti(), viewModel.getTinggiPeti());
        }

        if (viewModel.apaLasoAktif()) {
            int playerCenterX = viewModel.getXPemain() + viewModel.getLebarPemain() / 2;
            int playerCenterY = viewModel.getYPemain() + viewModel.getTinggiPemain() / 2;
            int mouseX = viewModel.getPosisiMouse().x;
            int mouseY = viewModel.getPosisiMouse().y;

            g.setColor(Color.ORANGE); // WARNA LASO DIUBAH DI SINI
            g.drawLine(playerCenterX, playerCenterY, mouseX, mouseY);
            g.fillOval(mouseX - 5, mouseY - 5, 10, 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.perbaruiGame();
        repaint();
    }
}