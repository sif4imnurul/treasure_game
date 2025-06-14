package view;

import viewmodel.GameViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class StartView extends JPanel {

    private JTextField inputNamaPemain;
    private JButton tombolMulai;
    private JButton tombolKeluar;
    private JTable tabelSkorTertinggi;
    private DefaultTableModel modelTabel;

    private GameViewModel viewModel;
    private JFrame frameUtama;
    private Image gambarLatar;

    private static final Dimension UKURAN_PANEL = new Dimension(1200, 800);
    private static final Dimension UKURAN_TABEL = new Dimension(500, 250);
    private static final String[] KOLOM_TABEL = {"Username", "Score", "Count"};

    private static final Color WARNA_JUDUL = Color.YELLOW;
    private static final Color WARNA_TEKS = Color.WHITE;
    private static final Color WARNA_TOMBOL_MULAI = new Color(50, 150, 50, 220);
    private static final Color WARNA_TRANSPARAN = new Color(0, 0, 0, 0);
    private static final Color WARNA_INPUT_BACKGROUND = new Color(255, 255, 255, 200);

    private static final Color WARNA_SEL_TABEL_BACKGROUND = new Color(0, 0, 0, 100);

    public StartView(GameViewModel viewModel, JFrame parentFrame) {
        this.viewModel = viewModel;
        this.frameUtama = parentFrame;
        this.gambarLatar = viewModel.getBackgroundImage();

        inisialisasiPanel();
        buatKomponenUI();
        aturEventListener();
        muatDataSkorTertinggi();
    }

    private void inisialisasiPanel() {
        setPreferredSize(UKURAN_PANEL);
        setLayout(new GridBagLayout());
        setOpaque(true);
    }

    private void buatKomponenUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        buatJudulGame(gbc);

        buatInputNamaPemain(gbc);

        buatTombolMulai(gbc);

        buatTabelSkorTertinggi(gbc);

        buatTombolKeluar(gbc);
    }

    private void buatJudulGame(GridBagConstraints gbc) {
        JLabel labelJudul = new JLabel("COLLECT THE SKILL BALLS");
        labelJudul.setFont(new Font("Arial", Font.BOLD, 48));
        labelJudul.setForeground(WARNA_JUDUL);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(labelJudul, gbc);
    }

    private void buatInputNamaPemain(GridBagConstraints gbc) {
        JLabel labelUsername = new JLabel("Username");
        labelUsername.setFont(new Font("Arial", Font.BOLD, 24));
        labelUsername.setForeground(WARNA_TEKS);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelUsername, gbc);

        inputNamaPemain = new JTextField(15);
        inputNamaPemain.setFont(new Font("Arial", Font.PLAIN, 24));
        inputNamaPemain.setBackground(WARNA_INPUT_BACKGROUND);
        inputNamaPemain.setForeground(Color.BLACK);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(inputNamaPemain, gbc);
    }

    private void buatTombolMulai(GridBagConstraints gbc) {
        tombolMulai = new JButton("Play");
        tombolMulai.setFont(new Font("Arial", Font.BOLD, 36));
        tombolMulai.setBackground(WARNA_TOMBOL_MULAI);
        tombolMulai.setForeground(WARNA_TEKS);
        tombolMulai.setFocusPainted(false);
        tombolMulai.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(tombolMulai, gbc);
    }

    private void buatTabelSkorTertinggi(GridBagConstraints gbc) {
        modelTabel = new DefaultTableModel(KOLOM_TABEL, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelSkorTertinggi = new JTable(modelTabel);

        aturGayaTabel();
        aturRendererTabel();

        JScrollPane scrollPane = new JScrollPane(tabelSkorTertinggi);
        scrollPane.setPreferredSize(UKURAN_TABEL);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(scrollPane, gbc);
    }

    private void aturGayaTabel() {
        tabelSkorTertinggi.setFont(new Font("Monospaced", Font.PLAIN, 20));
        tabelSkorTertinggi.setRowHeight(28);
        tabelSkorTertinggi.setFillsViewportHeight(true);
        tabelSkorTertinggi.setShowGrid(true);
        tabelSkorTertinggi.setGridColor(Color.BLACK);
        tabelSkorTertinggi.setSelectionForeground(WARNA_TEKS);

        tabelSkorTertinggi.setOpaque(false);
        tabelSkorTertinggi.setBackground(WARNA_TRANSPARAN);

        JTableHeader header = tabelSkorTertinggi.getTableHeader();
        header.setOpaque(true);
        header.setBackground(new Color(50, 50, 50, 200));
        header.setForeground(WARNA_TEKS);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void aturRendererTabel() {
        DefaultTableCellRenderer rendererSel = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(WARNA_TEKS);
                c.setBackground(WARNA_SEL_TABEL_BACKGROUND);

                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        for (int i = 0; i < tabelSkorTertinggi.getColumnCount(); i++) {
            tabelSkorTertinggi.getColumnModel().getColumn(i).setCellRenderer(rendererSel);
        }
    }

    private void buatTombolKeluar(GridBagConstraints gbc) {
        tombolKeluar = new JButton("Quit");
        tombolKeluar.setFont(new Font("Arial", Font.BOLD, 36));
        tombolKeluar.setOpaque(false);
        tombolKeluar.setContentAreaFilled(false);
        tombolKeluar.setBorderPainted(false);
        tombolKeluar.setForeground(WARNA_TEKS);
        tombolKeluar.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(tombolKeluar, gbc);
    }

    private void aturEventListener() {
        tombolMulai.addActionListener((ActionEvent e) -> {
            String namaPemain = inputNamaPemain.getText().trim();

            if (namaPemain.isEmpty()) {
                tampilkanPesanPeringatan();
            } else {
                mulaiGame(namaPemain);
            }
        });

        tombolKeluar.addActionListener((ActionEvent e) -> System.exit(0));
    }

    private void tampilkanPesanPeringatan() {
        JOptionPane.showMessageDialog(
                this,
                "Silakan masukkan nama Anda untuk memulai!",
                "Masukan Diperlukan",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mulaiGame(String namaPemain) {
        viewModel.setPlayerName(namaPemain);
        viewModel.resetGame();

        frameUtama.getContentPane().removeAll();
        GameView gameView = new GameView(viewModel, frameUtama);
        frameUtama.add(gameView);
        frameUtama.revalidate();
        frameUtama.repaint();
        gameView.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gambarLatar != null) {
            g.drawImage(gambarLatar, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void muatDataSkorTertinggi() {
        modelTabel.setRowCount(0);

        List<String[]> daftarSkorTertinggi = viewModel.getHighScores();
        for (String[] dataBaris : daftarSkorTertinggi) {
            modelTabel.addRow(dataBaris);
        }
    }

    public void refreshHighScores() {
        muatDataSkorTertinggi();
    }
}