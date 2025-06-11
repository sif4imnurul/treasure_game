package view;

import viewmodel.GameViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * StartView - Halaman utama game "Collect The Skill Balls"
 * Berisi form input nama pemain, tombol mulai game, tabel skor tertinggi, dan tombol keluar
 */
public class StartView extends JPanel {

    // ===== KOMPONEN UI =====
    private JTextField inputNamaPemain;           // Field untuk input nama pemain
    private JButton tombolMulai;                  // Tombol untuk memulai game
    private JButton tombolKeluar;                 // Tombol untuk keluar dari game
    private JTable tabelSkorTertinggi;            // Tabel untuk menampilkan high scores
    private DefaultTableModel modelTabel;        // Model data untuk tabel

    // ===== DEPENDENCIES =====
    private GameViewModel viewModel;              // ViewModel untuk logika bisnis
    private JFrame frameUtama;                    // Frame utama aplikasi
    private Image gambarLatar;                    // Gambar background

    // ===== KONSTANTA =====
    private static final Dimension UKURAN_PANEL = new Dimension(1200, 800);
    private static final Dimension UKURAN_TABEL = new Dimension(500, 250);
    private static final String[] KOLOM_TABEL = {"Username", "Score", "Count"};

    // ===== WARNA =====
    private static final Color WARNA_JUDUL = Color.YELLOW;
    private static final Color WARNA_TEKS = Color.WHITE;
    private static final Color WARNA_TOMBOL_MULAI = new Color(50, 150, 50, 220);
    private static final Color WARNA_TRANSPARAN = new Color(0, 0, 0, 0); // Tetap untuk komponen yang ingin transparan penuh
    private static final Color WARNA_INPUT_BACKGROUND = new Color(255, 255, 255, 200);

    // ************ BARU/MODIFIKASI ************
    // Warna semi-transparan untuk background sel tabel
    private static final Color WARNA_SEL_TABEL_BACKGROUND = new Color(0, 0, 0, 100); // Hitam, alpha 100 (sekitar 40% opak)
    // *******************************************


    /**
     * Constructor - Membuat tampilan halaman start
     */
    public StartView(GameViewModel viewModel, JFrame parentFrame) {
        this.viewModel = viewModel;
        this.frameUtama = parentFrame;
        this.gambarLatar = viewModel.getBackgroundImage();

        inisialisasiPanel();
        buatKomponenUI();
        aturEventListener();
        muatDataSkorTertinggi();
    }

    /**
     * Inisialisasi pengaturan dasar panel
     */
    private void inisialisasiPanel() {
        setPreferredSize(UKURAN_PANEL);
        setLayout(new GridBagLayout());
        // ************ BARU/MODIFIKASI ************
        // Pastikan panel ini opaque agar selalu membersihkan area sebelum digambar
        // JPanel defaultnya sudah opaque, tapi tidak ada salahnya memastikan
        setOpaque(true);
        // *******************************************
    }

    /**
     * Membuat semua komponen UI dan menambahkannya ke panel
     */
    private void buatKomponenUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Judul Game
        buatJudulGame(gbc);

        // 2. Label dan Input Nama Pemain
        buatInputNamaPemain(gbc);

        // 3. Tombol Mulai Game
        buatTombolMulai(gbc);

        // 4. Tabel Skor Tertinggi
        buatTabelSkorTertinggi(gbc);

        // 5. Tombol Keluar
        buatTombolKeluar(gbc);
    }

    /**
     * Membuat dan menambahkan judul game
     */
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

    /**
     * Membuat label dan field input nama pemain
     */
    private void buatInputNamaPemain(GridBagConstraints gbc) {
        // Label Username
        JLabel labelUsername = new JLabel("Username");
        labelUsername.setFont(new Font("Arial", Font.BOLD, 24));
        labelUsername.setForeground(WARNA_TEKS);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelUsername, gbc);

        // Field Input Nama
        inputNamaPemain = new JTextField(15);
        inputNamaPemain.setFont(new Font("Arial", Font.PLAIN, 24));
        inputNamaPemain.setBackground(WARNA_INPUT_BACKGROUND);
        inputNamaPemain.setForeground(Color.BLACK);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(inputNamaPemain, gbc);
    }

    /**
     * Membuat tombol untuk memulai game
     */
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

    /**
     * Membuat tabel untuk menampilkan skor tertinggi
     */
    private void buatTabelSkorTertinggi(GridBagConstraints gbc) {
        // Inisialisasi model tabel
        modelTabel = new DefaultTableModel(KOLOM_TABEL, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit
            }
        };

        // Membuat tabel normal (tidak override header)
        tabelSkorTertinggi = new JTable(modelTabel);

        aturGayaTabel();
        aturRendererTabel();

        // Membungkus tabel dalam scroll pane
        JScrollPane scrollPane = new JScrollPane(tabelSkorTertinggi);
        scrollPane.setPreferredSize(UKURAN_TABEL);
        // ************ MODIFIKASI ************
        // Biarkan viewport tidak opak, tapi scrollPane itu sendiri bisa punya background
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false); // Biarkan scroll pane transparan agar background panel terlihat
        // ************************************
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(scrollPane, gbc);
    }

    /**
     * Mengatur gaya tampilan tabel
     */
    private void aturGayaTabel() {
        tabelSkorTertinggi.setFont(new Font("Monospaced", Font.PLAIN, 20));
        tabelSkorTertinggi.setRowHeight(28);
        tabelSkorTertinggi.setFillsViewportHeight(true);
        tabelSkorTertinggi.setShowGrid(true);
        tabelSkorTertinggi.setGridColor(Color.BLACK);
        tabelSkorTertinggi.setSelectionForeground(WARNA_TEKS);

        // Membuat body tabel transparan (hanya isi data)
        // ************ MODIFIKASI ************
        // Biarkan tabel itu sendiri opaque, dan kita akan mengatur transparansi di cell renderer.
        // Ini seringkali lebih stabil untuk JTable.
        tabelSkorTertinggi.setOpaque(false);
        // Background tabel tetap transparan, karena cell renderer yang akan menggambar background setiap sel.
        tabelSkorTertinggi.setBackground(WARNA_TRANSPARAN);
        // ************************************

        // Header tetap solid dengan background gelap untuk kontras
        JTableHeader header = tabelSkorTertinggi.getTableHeader();
        header.setOpaque(true);
        header.setBackground(new Color(50, 50, 50, 200)); // Background gelap semi-transparan
        header.setForeground(WARNA_TEKS);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        // ************ BARU/MODIFIKASI ************
        // Pastikan header menggunakan renderer default atau renderer kustom jika diperlukan
        // untuk memastikan teks di tengah atau gaya lainnya.
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        // *******************************************
    }

    /**
     * Mengatur renderer untuk sel tabel (header sudah diatur di aturGayaTabel)
     */
    private void aturRendererTabel() {
        // Renderer untuk sel data (transparan dengan teks putih)
        DefaultTableCellRenderer rendererSel = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(WARNA_TEKS);
                // ************ MODIFIKASI UTAMA ************
                // Beri background semi-transparan pada setiap sel, bukan transparan penuh
                c.setBackground(WARNA_SEL_TABEL_BACKGROUND); // Gunakan warna semi-transparan baru
                // *******************************************

                // Tengahkan teks di semua kolom
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        // Menerapkan renderer hanya ke sel data (bukan header)
        for (int i = 0; i < tabelSkorTertinggi.getColumnCount(); i++) {
            tabelSkorTertinggi.getColumnModel().getColumn(i).setCellRenderer(rendererSel);
        }
    }

    /**
     * Membuat tombol untuk keluar dari game
     */
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

    /**
     * Mengatur event listener untuk tombol-tombol
     */
    private void aturEventListener() {
        // Event untuk tombol mulai game
        tombolMulai.addActionListener((ActionEvent e) -> {
            String namaPemain = inputNamaPemain.getText().trim();

            if (namaPemain.isEmpty()) {
                tampilkanPesanPeringatan();
            } else {
                mulaiGame(namaPemain);
            }
        });

        // Event untuk tombol keluar
        tombolKeluar.addActionListener((ActionEvent e) -> System.exit(0));
    }

    /**
     * Menampilkan pesan peringatan jika nama pemain kosong
     */
    private void tampilkanPesanPeringatan() {
        JOptionPane.showMessageDialog(
                this,
                "Silakan masukkan nama Anda untuk memulai!",
                "Masukan Diperlukan",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Memulai game dengan nama pemain yang sudah diinput
     */
    private void mulaiGame(String namaPemain) {
        viewModel.setPlayerName(namaPemain);

        // Membersihkan frame dan menampilkan game view
        frameUtama.getContentPane().removeAll();
        GameView gameView = new GameView(viewModel, frameUtama);
        frameUtama.add(gameView);
        frameUtama.revalidate();
        frameUtama.repaint();
        gameView.requestFocusInWindow();
    }

    /**
     * Menggambar background panel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Penting: Ini memastikan area panel dibersihkan

        if (gambarLatar != null) {
            // Menggambar gambar background yang menyesuaikan ukuran panel
            g.drawImage(gambarLatar, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback jika tidak ada gambar background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Memuat data skor tertinggi dari viewModel ke tabel
     */
    private void muatDataSkorTertinggi() {
        modelTabel.setRowCount(0); // Hapus semua data lama

        List<String[]> daftarSkorTertinggi = viewModel.getHighScores();
        for (String[] dataBaris : daftarSkorTertinggi) {
            modelTabel.addRow(dataBaris);
        }
    }

    /**
     * Method public untuk refresh data skor tertinggi (dipanggil dari luar)
     */
    public void refreshHighScores() {
        muatDataSkorTertinggi();
    }
}