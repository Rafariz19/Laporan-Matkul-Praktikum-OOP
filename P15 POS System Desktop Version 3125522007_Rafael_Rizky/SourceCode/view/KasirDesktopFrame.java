package view;

import model.*;
import model.exception.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * KasirDesktopFrame - Modern Swing Graphical User Interface (GUI)
 * Sistem Kasir Sederhana (Point of Sale) Versi Desktop (Fase 4: Responsif, Desain Modern, Settings CRUD Diskon & Tampilan)
 * 
 * Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus    : PENS PSDKU Sumenep (2026)
 */
public class KasirDesktopFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Path PATH_PRODUK = Paths.get("data/produk.txt");
    private static final Path PATH_TRANSAKSI = Paths.get("data/transaksi.txt");
    private static final Path PATH_TRANSAKSI_ITEM = Paths.get("data/transaksi_item.txt");
    private static final Path PATH_USERS = Paths.get("data/users.txt");

    private final transient KatalogProduk katalog;
    private final transient ProdukFileRepository produkRepo;
    private final transient TransaksiFileRepository transaksiRepo;
    private final transient TransaksiItemRepository transaksiItemRepo;
    private final transient AuthService authService;
    private final transient PengaturanSistem pengaturanSistem;

    // Sesi Pengguna Aktif
    private transient User currentUser;

    // GUI Components - Table Models
    private DefaultTableModel modelKatalog;
    private DefaultTableModel modelKeranjang;
    private JTable tabelKatalog;
    private JTable tabelKeranjang;

    // Search & Filter & Barcode Wedge
    private JTextField txtCari;
    private JTextField txtBarcodeScan;

    // Pelanggan & Transaksi Active State
    private JTextField txtNamaPelanggan;
    private JComboBox<String> cmbTipeMember;
    private transient Transaksi transaksiAktif;

    // Financial & Payment Strategy Components
    private JLabel lblTotalKotor;
    private JLabel lblDiskonMember;
    private JLabel lblTotalTagihan;
    private JComboBox<String> cmbMetodeBayar;
    private JTextField txtBayarTunai;
    private JTextField txtNominalQris;
    private JLabel lblKembalian;
    private JLabel lblUserBadge;

    // Header Components (Dapat Diperbarui Dinamis dari Settings)
    private JPanel headerPanel;
    private JLabel lblTitle;
    private JLabel lblSub;
    private ModernButton btnBayar;
    private ModernButton btnPengaturan;
    private ModernButton btnLaporan;
    private ModernButton btnRiwayatVoid;
    private ModernButton btnGantiAkun;
    private ModernTableHeaderRenderer headerRenderer;

    // Category Bar Components (Scrollable Horizontal via Mouse Wheel)
    private JPanel pnlBarKategori;
    private JScrollPane scrollKategori;
    private String kategoriAktif = "Semua";

    @SuppressWarnings("this-escape")
    public KasirDesktopFrame() {
        super("Sistem Kasir Sederhana (POS Desktop) - Rafael Rizky (3125522007)");
        this.pengaturanSistem = new PengaturanSistem();
        this.pengaturanSistem.muatDariFile();

        this.katalog = new KatalogProduk();
        this.produkRepo = new ProdukFileRepository();
        this.transaksiRepo = new TransaksiFileRepository();
        this.transaksiItemRepo = new TransaksiItemRepository();
        this.authService = new AuthService(PATH_USERS);

        initData();
        prosesLoginAwal();
        initUI();
        mulaiTransaksiBaru();
    }

    private void initData() {
        try {
            produkRepo.muat(PATH_PRODUK, katalog);
        } catch (Exception e) {
            System.err.println("Inisialisasi berkas katalog: " + e.getMessage());
            if (katalog.isKosong()) {
                katalog.tambahProduk(new ProdukMakanan("MK01", "Roti Bakar Keju", 15000, 25, "2026-12-31"));
                katalog.tambahProduk(new ProdukMakanan("MK02", "Susu UHT Coklat 250ml", 8500, 40, "2026-10-15"));
                katalog.tambahProduk(new ProdukElektronik("EL01", "Mouse Wireless Silent", 125000, 15, 12));
                katalog.tambahProduk(new ProdukElektronik("EL02", "Keyboard Mechanical RGB", 350000, 8, 24));
                try {
                    simpanKatalogAmanKeFile(PATH_PRODUK);
                } catch (IOException ignored) {}
            }
        }
        for (String kat : katalog.getDaftarKategoriUnik()) {
            if (!"Semua".equalsIgnoreCase(kat) && !pengaturanSistem.getDaftarKategori().contains(kat)) {
                try {
                    pengaturanSistem.tambahKategori(kat);
                } catch (Exception ignored) {}
            }
        }
    }

    public KatalogProduk getKatalog() {
        return katalog;
    }

    public void simpanKatalogKeDisk() {
        try {
            simpanKatalogAmanKeFile(PATH_PRODUK);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan katalog: " + e.getMessage());
        }
    }

    private void prosesLoginAwal() {
        if (authService.isUsersFileEmpty()) {
            try {
                authService.inisialisasiAdminAwal("admin123");
                authService.register("kasir", "kasir123", Role.KASIR);
            } catch (Exception ex) {
                System.err.println("Gagal setup admin awal: " + ex.getMessage());
            }
        }

        DialogLogin dlg = new DialogLogin(this, authService);
        dlg.setVisible(true);

        this.currentUser = dlg.getAuthenticatedUser();
        if (this.currentUser == null) {
            System.exit(0);
        }

        if (lblUserBadge != null) {
            lblUserBadge.setText(currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        }
        setTitle(pengaturanSistem.getJudulAplikasi() + " - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
    }

    private void mulaiTransaksiBaru() {
        String nama = (txtNamaPelanggan != null && !txtNamaPelanggan.getText().trim().isEmpty()) 
                      ? txtNamaPelanggan.getText().trim() : "Pelanggan Umum";
        String member = "REGULER";
        if (cmbTipeMember != null && cmbTipeMember.getSelectedItem() != null) {
            String rawMember = (String) cmbTipeMember.getSelectedItem();
            int idxKurung = rawMember.indexOf(" (");
            if (idxKurung > 0) {
                member = rawMember.substring(0, idxKurung).trim();
            } else {
                member = rawMember.trim();
            }
        }

        String autoId = "TRX-" + System.currentTimeMillis();
        String tgl = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Pelanggan p = new Pelanggan("CUST-" + (System.currentTimeMillis() % 1000), nama, "081234567890", member, pengaturanSistem);

        this.transaksiAktif = new Transaksi(autoId, tgl, p);

        if (modelKeranjang != null) {
            modelKeranjang.setRowCount(0);
        }
        updateKalkulasiFinansial();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (transaksiAktif != null && "PENDING".equals(transaksiAktif.getStatusTransaksi()) && !transaksiAktif.getDaftarItem().isEmpty()) {
                    int konf = JOptionPane.showConfirmDialog(KasirDesktopFrame.this,
                        "Ada " + transaksiAktif.getDaftarItem().size() + " item di keranjang belanja yang belum diselesaikan (PENDING).\n" +
                        "Jika Anda keluar sekarang, seluruh item keranjang akan dibatalkan dan stok fisik akan dipulihkan.\n\n" +
                        "Yakin ingin keluar?", "Konfirmasi Keluar Kasir", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (konf != JOptionPane.YES_OPTION) {
                        return;
                    }
                    try {
                        transaksiAktif.batalkanTransaksi();
                        System.out.println("[INFO SHUTDOWN] Transaksi PENDING dibatalkan otomatis dan stok dikembalikan.");
                    } catch (Exception ex) {
                        System.err.println("[WARN SHUTDOWN] Gagal membatalkan transaksi saat window closing: " + ex.getMessage());
                    }
                } else {
                    int konf = JOptionPane.showConfirmDialog(KasirDesktopFrame.this,
                        "Apakah Anda yakin ingin menutup aplikasi Kasir?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (konf != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                dispose();
                System.exit(0);
            }
        });
        setSize(1240, 740);
        setMinimumSize(new Dimension(860, 580)); // Mencegah window terlalu mengecil
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. HEADER PANEL DENGAN ROLE INDICATOR, MENU ADMIN & PENGATURAN
        headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBackground(pengaturanSistem.getWarnaHeader());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        lblTitle = new JLabel(pengaturanSistem.getJudulAplikasi());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        lblSub = new JLabel(pengaturanSistem.getTeksHeaderSub());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TemaWarna.TEKS_SUBTITLE);
        headerLeft.add(lblTitle);
        headerLeft.add(lblSub);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        headerRight.setOpaque(false);

        Color aksenAwal = pengaturanSistem.getWarnaAksen();
        Color fgAksenAwal = TemaWarna.getKontrasForeground(aksenAwal);

        lblUserBadge = new JLabel(currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        lblUserBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserBadge.setForeground(aksenAwal);

        btnPengaturan = new ModernButton("⚙️ Pengaturan", aksenAwal, fgAksenAwal);
        btnPengaturan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnPengaturan.addActionListener(e -> aksiBukaDialogPengaturan());

        btnLaporan = new ModernButton("📊 Laporan Harian", aksenAwal, fgAksenAwal);
        btnLaporan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLaporan.addActionListener(e -> aksiBukaLaporanHarian());

        btnRiwayatVoid = new ModernButton("📜 Riwayat / VOID", aksenAwal, fgAksenAwal);
        btnRiwayatVoid.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRiwayatVoid.addActionListener(e -> aksiBukaRiwayatVoid());

        btnGantiAkun = new ModernButton("👤 Ganti Akun", aksenAwal, fgAksenAwal);
        btnGantiAkun.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnGantiAkun.addActionListener(e -> {
            prosesLoginAwal();
            muatTabelKatalog();
        });

        JLabel lblPetugasText = new JLabel("Petugas: ");
        lblPetugasText.setForeground(TemaWarna.TEKS_SUBTITLE);
        headerRight.add(lblPetugasText);
        headerRight.add(lblUserBadge);
        headerRight.add(btnPengaturan);
        headerRight.add(btnLaporan);
        headerRight.add(btnRiwayatVoid);
        headerRight.add(btnGantiAkun);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        getContentPane().setBackground(TemaWarna.APPLE_CANVAS);

        // 2. MAIN SPLIT PANEL (Katalog vs Keranjang)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buatPanelKatalog(), buatPanelKeranjang());
        splitPane.setDividerLocation(620);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(TemaWarna.APPLE_CANVAS);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        add(splitPane, BorderLayout.CENTER);

        // 3. FOOTER STATUS BAR
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        footerPanel.setBackground(TemaWarna.APPLE_CANVAS);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TemaWarna.BORDER_DIVIDER));

        JLabel lblStatus = new JLabel("Status Sistem: Apple Modern Theme (#F5F5F7, #1D1D1F, #AAAAAA, #007AFF) | Multi-role aktif (Admin/Kasir).");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TemaWarna.APPLE_BLACK);
        footerPanel.add(lblStatus);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Menerapkan pembaruan pengaturan tampilan & diskon secara real-time ke seluruh antarmuka GUI.
     */
    public void terapkanPengaturan() {
        lblTitle.setText(pengaturanSistem.getJudulAplikasi());
        lblSub.setText(pengaturanSistem.getTeksHeaderSub());
        Color headerColor = pengaturanSistem.getWarnaHeader();
        Color fgHeader = TemaWarna.getKontrasForeground(headerColor);
        headerPanel.setBackground(headerColor);
        setTitle(pengaturanSistem.getJudulAplikasi() + " - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        
        // Terapkan WARNA AKSEN secara dinamis ke seluruh elemen aksen UI
        Color aksen = pengaturanSistem.getWarnaAksen();
        Color fgAksen = TemaWarna.getKontrasForeground(aksen);

        if (lblUserBadge != null) {
            lblUserBadge.setForeground(aksen);
        }
        if (btnPengaturan != null) {
            btnPengaturan.setCustomColors(aksen, fgAksen);
        }
        if (btnLaporan != null) {
            btnLaporan.setCustomColors(aksen, fgAksen);
        }
        if (btnRiwayatVoid != null) {
            btnRiwayatVoid.setCustomColors(aksen, fgAksen);
        }
        if (btnGantiAkun != null) {
            btnGantiAkun.setCustomColors(aksen, fgAksen);
        }
        if (btnBayar != null) {
            btnBayar.setCustomColors(aksen, fgAksen);
        }
        if (tabelKatalog != null) {
            tabelKatalog.setSelectionBackground(aksen);
            tabelKatalog.setSelectionForeground(fgAksen);
        }
        if (tabelKeranjang != null) {
            tabelKeranjang.setSelectionBackground(aksen);
            tabelKeranjang.setSelectionForeground(fgAksen);
        }
        if (headerRenderer != null) {
            headerRenderer.setColors(headerColor, fgHeader);
            if (tabelKatalog != null) tabelKatalog.getTableHeader().repaint();
            if (tabelKeranjang != null) tabelKeranjang.getTableHeader().repaint();
        }

        // Perbarui pilihan membership di ComboBox
        updateComboBoxMember();
        renderBarKategori();
        if (transaksiAktif != null) {
            transaksiAktif.getPelanggan().setPengaturanSistem(pengaturanSistem);
            updateKalkulasiFinansial();
        }
        repaint();
    }

    private void updateComboBoxMember() {
        if (cmbTipeMember == null) return;
        String terpilihSekarang = (String) cmbTipeMember.getSelectedItem();
        cmbTipeMember.removeAllItems();
        for (java.util.Map.Entry<String, Double> entry : pengaturanSistem.getDaftarDiskonMember().entrySet()) {
            cmbTipeMember.addItem(String.format("%s (Diskon %.0f%%)", entry.getKey(), entry.getValue() * 100));
        }
        if (terpilihSekarang != null) {
            for (int i = 0; i < cmbTipeMember.getItemCount(); i++) {
                if (cmbTipeMember.getItemAt(i).startsWith(terpilihSekarang.split(" ")[0])) {
                    cmbTipeMember.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void aksiBukaDialogPengaturan() {
        DialogPengaturan dlg = new DialogPengaturan(this, pengaturanSistem, currentUser);
        dlg.setVisible(true);
    }

    private JPanel buatPanelKatalog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(TemaWarna.APPLE_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        // Top Header Katalog: Menggunakan GridBagLayout / BoxLayout Responsif 2-Baris agar TIDAK TERTAMPAI
        JPanel topPanel = new JPanel();
        topPanel.setBackground(TemaWarna.APPLE_CANVAS);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout(5, 5));
        row1.setBackground(TemaWarna.APPLE_CANVAS);
        JLabel lblSection = new JLabel("KATALOG INVENTARIS PRODUK");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection.setForeground(TemaWarna.APPLE_BLACK);
        row1.add(lblSection, BorderLayout.WEST);

        // Barcode Wedge & Search Row (Wrap & Responsif)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        searchPanel.setBackground(TemaWarna.APPLE_CANVAS);
        searchPanel.add(new JLabel("Scan Barcode:"));
        txtBarcodeScan = new JTextField(9);
        txtBarcodeScan.setToolTipText("Arahkan scanner barcode atau ketik kode lalu tekan Enter");
        txtBarcodeScan.addActionListener(e -> aksiScanBarcodeEnter());
        searchPanel.add(txtBarcodeScan);

        searchPanel.add(new JLabel("Nama:"));
        txtCari = new JTextField(9);
        ModernButton btnCari = new ModernButton("Cari", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnReset = new ModernButton("Reset", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);

        btnCari.addActionListener(e -> filterKatalog(txtCari.getText()));
        btnReset.addActionListener(e -> { txtCari.setText(""); muatTabelKatalog(); });
        txtCari.addActionListener(e -> filterKatalog(txtCari.getText()));

        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnReset);
        row1.add(searchPanel, BorderLayout.EAST);

        topPanel.add(row1);
        topPanel.add(Box.createVerticalStrut(4));

        // Category Bar Row (Scrollable Horizontal via Mouse Wheel, no visible scrollbar)
        pnlBarKategori = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlBarKategori.setBackground(TemaWarna.APPLE_CANVAS);

        scrollKategori = new JScrollPane(pnlBarKategori);
        scrollKategori.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollKategori.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollKategori.setBorder(null);
        scrollKategori.getViewport().setBackground(TemaWarna.APPLE_CANVAS);
        scrollKategori.setBackground(TemaWarna.APPLE_CANVAS);
        scrollKategori.setPreferredSize(new Dimension(0, 36));

        MouseWheelListener mwl = e -> {
            JScrollBar hBar = scrollKategori.getHorizontalScrollBar();
            if (hBar != null) {
                int delta = (int) (e.getPreciseWheelRotation() * 32);
                if (delta == 0 && e.getWheelRotation() != 0) {
                    delta = e.getWheelRotation() * 32;
                }
                hBar.setValue(hBar.getValue() + delta);
            }
        };
        scrollKategori.addMouseWheelListener(mwl);
        pnlBarKategori.addMouseWheelListener(mwl);

        topPanel.add(scrollKategori);
        topPanel.add(Box.createVerticalStrut(4));
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Kode", "Nama Produk", "Harga (Rp)", "Stok", "Info Khusus"};
        modelKatalog = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKatalog = new JTable(modelKatalog) {
            private static final long serialVersionUID = 1L;
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                if (isRowSelected(rowIndex) && !extend && !toggle) {
                    clearSelection();
                } else {
                    super.changeSelection(rowIndex, columnIndex, toggle, extend);
                }
            }
        };
        tabelKatalog.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tabelKatalog.rowAtPoint(e.getPoint());
                if (row == -1) {
                    tabelKatalog.clearSelection();
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelKatalog.getSelectedRow() >= 0) {
                    aksiTambahKeKeranjang();
                }
            }
        });
        tabelKatalog.setRowHeight(25);
        tabelKatalog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (headerRenderer == null) {
            Color hdr = pengaturanSistem.getWarnaHeader();
            headerRenderer = new ModernTableHeaderRenderer(hdr, TemaWarna.getKontrasForeground(hdr));
        }
        tabelKatalog.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelKatalog.getTableHeader().setDefaultRenderer(headerRenderer);
        tabelKatalog.setGridColor(TemaWarna.BORDER_DIVIDER);
        tabelKatalog.setSelectionBackground(pengaturanSistem.getWarnaAksen());
        tabelKatalog.setSelectionForeground(TemaWarna.getKontrasForeground(pengaturanSistem.getWarnaAksen()));
        tabelKatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelKatalog.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if (str.startsWith("<html>") && str.contains("<font color=")) {
                        if (isSelected) {
                            // Saat baris produk diklik/terpilih (muncul warna highlight tema kasir):
                            // ubah warna persentase diskon (-10%) menjadi warna hitam pekat kontras tinggi
                            c.setText(str.replaceAll("(?i)<font color=['\"][^'\"]*['\"]>", "<font color='#000000'>"));
                        } else {
                            // Saat baris tidak dipilih: tampilkan warna merah standar
                            c.setText(str);
                        }
                    }
                }
                return c;
            }
        });
        
        renderBarKategori();
        muatTabelKatalog();

        JScrollPane scrollKatalog = new JScrollPane(tabelKatalog);
        scrollKatalog.getViewport().setBackground(Color.WHITE);
        scrollKatalog.setBorder(BorderFactory.createLineBorder(TemaWarna.BORDER_DIVIDER));
        panel.add(scrollKatalog, BorderLayout.CENTER);

        // Button Toolbar Bawah Katalog: Palet Apple Modern (#1D1D1F & #AAAAAA)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btnPanel.setBackground(TemaWarna.APPLE_CANVAS);
        ModernButton btnTambahKeranjang = new ModernButton("+ Keranjang", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnBaru = new ModernButton("+ Produk Baru", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnEdit = new ModernButton("Edit Produk", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnHapus = new ModernButton("Hapus", TemaWarna.TOMBOL_BATAL, TemaWarna.TEKS_PUTIH);
        ModernButton btnSimpanKatalog = new ModernButton("💾 Simpan File", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);

        btnTambahKeranjang.addActionListener(e -> aksiTambahKeKeranjang());
        btnBaru.addActionListener(e -> aksiBukaDialogProdukBaru());
        btnEdit.addActionListener(e -> aksiBukaDialogEditProduk());
        btnHapus.addActionListener(e -> aksiHapusProdukKatalog());
        btnSimpanKatalog.addActionListener(e -> aksiSimpanKatalogFile());

        btnPanel.add(btnTambahKeranjang);
        btnPanel.add(btnBaru);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnSimpanKatalog);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatPanelKeranjang() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(TemaWarna.APPLE_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        JPanel customerPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        customerPanel.setBackground(TemaWarna.APPLE_CANVAS);
        customerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TemaWarna.BORDER_DIVIDER), "Informasi Konsumen & Member"),
                BorderFactory.createEmptyBorder(4, 8, 6, 8)
        ));

        customerPanel.add(new JLabel("Nama Pelanggan:"));
        txtNamaPelanggan = new JTextField("Pelanggan Umum");
        customerPanel.add(txtNamaPelanggan);

        customerPanel.add(new JLabel("Tipe Membership:"));
        cmbTipeMember = new JComboBox<>();
        updateComboBoxMember();
        cmbTipeMember.addActionListener(e -> updateMemberDariCombo());
        customerPanel.add(cmbTipeMember);

        panel.add(customerPanel, BorderLayout.NORTH);

        String[] cols = {"Kode", "Nama Produk", "Qty", "Diskon Item", "Subtotal (Rp)"};
        modelKeranjang = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang) {
            private static final long serialVersionUID = 1L;
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                if (isRowSelected(rowIndex) && !extend && !toggle) {
                    clearSelection();
                } else {
                    super.changeSelection(rowIndex, columnIndex, toggle, extend);
                }
            }
        };
        tabelKeranjang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tabelKeranjang.rowAtPoint(e.getPoint());
                if (row == -1) {
                    tabelKeranjang.clearSelection();
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelKeranjang.getSelectedRow() >= 0) {
                    aksiUbahQtyItemKeranjang();
                }
            }
        });
        tabelKeranjang.setRowHeight(25);
        tabelKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelKeranjang.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelKeranjang.getTableHeader().setDefaultRenderer(headerRenderer);
        tabelKeranjang.setGridColor(TemaWarna.BORDER_DIVIDER);
        tabelKeranjang.setSelectionBackground(pengaturanSistem.getWarnaAksen());
        tabelKeranjang.setSelectionForeground(TemaWarna.getKontrasForeground(pengaturanSistem.getWarnaAksen()));
        tabelKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollKeranjang = new JScrollPane(tabelKeranjang);
        scrollKeranjang.getViewport().setBackground(Color.WHITE);
        scrollKeranjang.setBorder(BorderFactory.createLineBorder(TemaWarna.BORDER_DIVIDER));
        panel.add(scrollKeranjang, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));
        bottomContainer.setBackground(TemaWarna.APPLE_CANVAS);

        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        cartActions.setBackground(TemaWarna.APPLE_CANVAS);
        ModernButton btnEditQty = new ModernButton("Ubah Qty", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnHapusItem = new ModernButton("Batalkan Item", TemaWarna.TOMBOL_BATAL, TemaWarna.TEKS_PUTIH);
        ModernButton btnResetTrx = new ModernButton("Reset Transaksi", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);

        btnEditQty.addActionListener(e -> aksiUbahQtyItemKeranjang());

        btnHapusItem.addActionListener(e -> aksiBatalkanItemKeranjang());
        btnResetTrx.addActionListener(e -> {
            if (transaksiAktif != null) {
                try {
                    transaksiAktif.batalkanTransaksi();
                } catch (Exception ignored) {}
            }
            mulaiTransaksiBaru();
            muatTabelKatalog();
        });
        cartActions.add(btnEditQty);
        cartActions.add(btnHapusItem);
        cartActions.add(btnResetTrx);
        bottomContainer.add(cartActions, BorderLayout.NORTH);

        JPanel paymentPanel = new JPanel(new GridLayout(7, 2, 8, 4));
        paymentPanel.setBackground(TemaWarna.APPLE_CANVAS);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TemaWarna.BORDER_DIVIDER), "Rincian Pembayaran Kasir (Pola Strategy)"),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        lblTotalKotor = new JLabel("Rp 0");
        lblDiskonMember = new JLabel("Rp 0");
        lblTotalTagihan = new JLabel("Rp 0");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalTagihan.setForeground(new Color(200, 35, 51));

        cmbMetodeBayar = new JComboBox<>(new String[]{"TUNAI (CASH)", "QRIS (SIMULASI DINAMIS)", "SPLIT PAYMENT (TUNAI + QRIS)"});
        cmbMetodeBayar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbMetodeBayar.addActionListener(e -> updateModePembayaran());

        txtBayarTunai = new JTextField();
        txtBayarTunai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtBayarTunai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { hitungKembalianLive(); }
        });

        txtNominalQris = new JTextField();
        txtNominalQris.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtNominalQris.setEnabled(false);
        txtNominalQris.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { hitungKembalianLive(); }
        });

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));

        paymentPanel.add(new JLabel("Total Pembelian Kotor:"));
        paymentPanel.add(lblTotalKotor);
        paymentPanel.add(new JLabel("Potongan Diskon Member:"));
        paymentPanel.add(lblDiskonMember);
        paymentPanel.add(new JLabel("TOTAL TAGIHAN AKHIR:"));
        paymentPanel.add(lblTotalTagihan);
        paymentPanel.add(new JLabel("Metode Pembayaran:"));
        paymentPanel.add(cmbMetodeBayar);
        paymentPanel.add(new JLabel("Nominal Tunai (Rp):"));
        paymentPanel.add(txtBayarTunai);
        paymentPanel.add(new JLabel("Nominal QRIS [Simulasi] (Rp):"));
        paymentPanel.add(txtNominalQris);
        paymentPanel.add(new JLabel("Kembalian Konsumen:"));
        paymentPanel.add(lblKembalian);

        bottomContainer.add(paymentPanel, BorderLayout.CENTER);

        btnBayar = new ModernButton("BAYAR & PROSES CHECKOUT", pengaturanSistem.getWarnaAksen(), TemaWarna.getKontrasForeground(pengaturanSistem.getWarnaAksen()));
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBayar.setPreferredSize(new Dimension(0, 42));
        btnBayar.addActionListener(e -> aksiProsesPembayaran());
        bottomContainer.add(btnBayar, BorderLayout.SOUTH);

        panel.add(bottomContainer, BorderLayout.SOUTH);
        return panel;
    }

    private void renderBarKategori() {
        if (pnlBarKategori == null) return;
        pnlBarKategori.removeAll();

        List<String> semuaKategori = new ArrayList<>();
        semuaKategori.add("Semua");
        for (String k : pengaturanSistem.getDaftarKategori()) {
            if (!semuaKategori.contains(k)) {
                semuaKategori.add(k);
            }
        }

        boolean ada = false;
        for (String k : semuaKategori) {
            if (k.equalsIgnoreCase(kategoriAktif)) {
                ada = true;
                break;
            }
        }
        if (!ada) {
            kategoriAktif = "Semua";
        }

        Color aksen = pengaturanSistem.getWarnaAksen();
        Color fgAksen = TemaWarna.getKontrasForeground(aksen);

        MouseWheelListener mwl = e -> {
            if (scrollKategori != null) {
                JScrollBar hBar = scrollKategori.getHorizontalScrollBar();
                if (hBar != null) {
                    int delta = (int) (e.getPreciseWheelRotation() * 32);
                    if (delta == 0 && e.getWheelRotation() != 0) {
                        delta = e.getWheelRotation() * 32;
                    }
                    hBar.setValue(hBar.getValue() + delta);
                }
            }
        };

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKategori.setForeground(TemaWarna.APPLE_BLACK);
        lblKategori.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        lblKategori.addMouseWheelListener(mwl);
        pnlBarKategori.add(lblKategori);

        for (String kat : semuaKategori) {
            boolean isAktif = kat.equalsIgnoreCase(kategoriAktif);
            ModernButton btnKat;
            if (isAktif) {
                btnKat = new ModernButton(kat, aksen, fgAksen);
                btnKat.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                btnKat = new ModernButton(kat, TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
                btnKat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
            btnKat.setMargin(new Insets(3, 10, 3, 10));
            btnKat.addActionListener(e -> {
                kategoriAktif = kat;
                renderBarKategori();
                if (txtCari != null && !txtCari.getText().trim().isEmpty()) {
                    filterKatalog(txtCari.getText().trim());
                } else {
                    muatTabelKatalog();
                }
            });
            btnKat.addMouseWheelListener(mwl);
            pnlBarKategori.add(btnKat);
        }

        pnlBarKategori.revalidate();
        pnlBarKategori.repaint();
    }

    private void sesuaikanLebarKolomKatalog() {
        if (tabelKatalog == null || tabelKatalog.getColumnModel().getColumnCount() < 5) return;
        TableColumnModel cm = tabelKatalog.getColumnModel();
        // 0: Kode (disesuaikan dengan string AAAA: ~55px)
        cm.getColumn(0).setPreferredWidth(55);
        cm.getColumn(0).setMinWidth(45);
        cm.getColumn(0).setMaxWidth(75);
        // 1: Nama Produk
        cm.getColumn(1).setPreferredWidth(160);
        // 2: Harga (Rp) (disesuaikan dengan angka + diskon '999999 -100%': ~125px)
        cm.getColumn(2).setPreferredWidth(125);
        cm.getColumn(2).setMinWidth(110);
        cm.getColumn(2).setMaxWidth(155);
        // 3: Stok (disesuaikan dengan integer 999999: ~65px)
        cm.getColumn(3).setPreferredWidth(65);
        cm.getColumn(3).setMinWidth(55);
        cm.getColumn(3).setMaxWidth(85);
        // 4: Info Khusus
        cm.getColumn(4).setPreferredWidth(140);
    }

    private void sesuaikanLebarKolomKeranjang() {
        if (tabelKeranjang == null || tabelKeranjang.getColumnModel().getColumnCount() < 5) return;
        TableColumnModel cm = tabelKeranjang.getColumnModel();
        // 0: Kode (AAAA)
        cm.getColumn(0).setPreferredWidth(55);
        cm.getColumn(0).setMinWidth(45);
        cm.getColumn(0).setMaxWidth(75);
        // 1: Nama Produk
        cm.getColumn(1).setPreferredWidth(150);
        // 2: Qty (int)
        cm.getColumn(2).setPreferredWidth(45);
        cm.getColumn(2).setMinWidth(40);
        cm.getColumn(2).setMaxWidth(65);
        // 3: Diskon Item
        cm.getColumn(3).setPreferredWidth(75);
        cm.getColumn(3).setMinWidth(65);
        cm.getColumn(3).setMaxWidth(95);
        // 4: Subtotal (Rp)
        cm.getColumn(4).setPreferredWidth(95);
        cm.getColumn(4).setMinWidth(85);
        cm.getColumn(4).setMaxWidth(130);
    }

    private void tambahBarisProdukKeModelKatalog(Produk p) {
        if (!kategoriAktif.equalsIgnoreCase("Semua") && !p.getKategori().equalsIgnoreCase(kategoriAktif)) {
            return;
        }
        String infoKhusus = "";
        double diskonExp = 0.0;
        if (p instanceof ProdukMakanan) {
            ProdukMakanan pm = (ProdukMakanan) p;
            long sisaHari = pm.hitungSisaHari();
            diskonExp = pm.hitungDiskonKedaluwarsa();
            if (sisaHari < 0) {
                infoKhusus = "[KADALUARSA] " + pm.getTanggalKadaluarsa();
            } else if (sisaHari <= 7) {
                infoKhusus = String.format("[EXP %d HR (Disc %.0f%%)] %s", sisaHari, diskonExp * 100, pm.getTanggalKadaluarsa());
            } else {
                infoKhusus = "Exp: " + pm.getTanggalKadaluarsa();
            }
        } else if (p instanceof ProdukElektronik) {
            infoKhusus = "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " bln";
        }
        if (p.getDiskonPromo() > 0.0) {
            infoKhusus += String.format(java.util.Locale.US, " [PROMO %.0f%%]", p.getDiskonPromo() * 100);
        }
        String labelStok = String.valueOf(p.getStok());
        if (p.isStokMenipis()) {
            labelStok = p.getStok() + " [MENIPIS]";
        }

        // Diskon efektif produk (diskon promo toko vs diskon dinamis kedaluwarsa)
        double diskon = Math.max(p.getDiskonPromo(), diskonExp);
        String labelHarga = String.format("%,d", p.getHarga());
        if (diskon > 0.0) {
            String pctStr = (diskon * 100 % 1 == 0)
                    ? String.format(java.util.Locale.US, "%.0f%%", diskon * 100)
                    : String.format(java.util.Locale.US, "%.1f%%", diskon * 100);
            labelHarga = "<html>" + labelHarga + " <font color='#FF3B30'><b>-" + pctStr + "</b></font></html>";
        }

        modelKatalog.addRow(new Object[]{
            p.getKode(), p.getNama(),
            labelHarga, labelStok, infoKhusus
        });
    }

    private void muatTabelKatalog() {
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.getDaftarProdukReadOnly()) {
            tambahBarisProdukKeModelKatalog(p);
        }
        sesuaikanLebarKolomKatalog();
    }

    private void filterKatalog(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            muatTabelKatalog();
            return;
        }
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.cariProdukByNama(kw)) {
            tambahBarisProdukKeModelKatalog(p);
        }
        sesuaikanLebarKolomKatalog();
    }

    private void aksiTambahKeKeranjang() {
        int row = tabelKatalog.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk pada tabel katalog terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = (String) modelKatalog.getValueAt(row, 0);
        Produk prod = katalog.cariProduk(kode);
        if (prod == null) return;

        String inputQty = JOptionPane.showInputDialog(this, "Masukkan Jumlah Beli untuk '" + prod.getNama() + "'\n(Stok Tersedia: " + prod.getStok() + "):", "1");
        if (inputQty == null || inputQty.trim().isEmpty()) return;

        try {
            int qty = Integer.parseInt(inputQty.trim());
            transaksiAktif.tambahItem(prod, qty);
            muatTabelKeranjang();
            muatTabelKatalog();
            updateKalkulasiFinansial();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah beli harus berupa angka bulat positif!", "Error Format", JOptionPane.ERROR_MESSAGE);
        } catch (KasirException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ditolak Aturan Kasir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatTabelKeranjang() {
        modelKeranjang.setRowCount(0);
        for (ItemTransaksi item : transaksiAktif.getDaftarItem()) {
            modelKeranjang.addRow(new Object[]{
                item.getProduk().getKode(),
                item.getNamaProduk(),
                item.getJumlahBeli(),
                (item.getDiskonItem() > 0 ? String.format("%.0f%%", item.getDiskonItem() * 100) : "-"),
                String.format("%,d", item.hitungSubtotal())
            });
        }
        sesuaikanLebarKolomKeranjang();
    }

    private void aksiUbahQtyItemKeranjang() {
        int row = tabelKeranjang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris item belanja di keranjang yang ingin diubah kuantitasnya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemTransaksi item = transaksiAktif.getDaftarItem().get(row);
        Produk prod = item.getProduk();
        int qtyLama = item.getJumlahBeli();
        int sisaStokToko = prod.getStok();
        int maxBoleh = qtyLama + sisaStokToko;

        String input = JOptionPane.showInputDialog(this,
            "Ubah kuantitas untuk: " + item.getNamaProduk() + "\n" +
            "Kuantitas saat ini di keranjang: " + qtyLama + "\n" +
            "Sisa stok di rak inventaris: " + sisaStokToko + " (Maksimal dapat diubah ke: " + maxBoleh + ")\n\n" +
            "Masukkan jumlah kuantitas baru (ketik 0 untuk batalkan item):",
            String.valueOf(qtyLama));

        if (input == null || input.trim().isEmpty()) return;

        try {
            int qtyBaru = Integer.parseInt(input.trim());
            if (qtyBaru == qtyLama) return;
            if (qtyBaru < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah kuantitas tidak boleh negatif!", "Format Salah", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (qtyBaru == 0) {
                transaksiAktif.batalkanItem(row);
            } else {
                transaksiAktif.ubahKuantitasItem(row, qtyBaru);
            }
            muatTabelKeranjang();
            muatTabelKatalog();
            updateKalkulasiFinansial();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah kuantitas harus berupa angka bulat!", "Error Format", JOptionPane.ERROR_MESSAGE);
        } catch (KasirException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aturan Kasir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiBatalkanItemKeranjang() {
        int row = tabelKeranjang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris item belanja yang ingin dibatalkan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            transaksiAktif.batalkanItem(row);
            muatTabelKeranjang();
            muatTabelKatalog();
            updateKalkulasiFinansial();
        } catch (KasirException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Batalkan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateModePembayaran() {
        if (cmbMetodeBayar == null || transaksiAktif == null) return;
        int idx = cmbMetodeBayar.getSelectedIndex();
        double tagihan = transaksiAktif.hitungTotalBayar();
        if (idx == 0) { // TUNAI
            txtBayarTunai.setEnabled(true);
            txtBayarTunai.setEditable(true);
            txtNominalQris.setEnabled(false);
            txtNominalQris.setEditable(false);
            txtNominalQris.setText("0");
        } else if (idx == 1) { // QRIS
            txtBayarTunai.setEnabled(false);
            txtBayarTunai.setEditable(false);
            txtBayarTunai.setText("0");
            txtNominalQris.setEnabled(false); // Nilai nominal QRIS terkunci & tidak bisa diubah-ubah
            txtNominalQris.setEditable(false);
            txtNominalQris.setText(String.format("%.0f", tagihan));
        } else if (idx == 2) { // SPLIT
            txtBayarTunai.setEnabled(true);
            txtBayarTunai.setEditable(true);
            txtNominalQris.setEnabled(true);
            txtNominalQris.setEditable(true);
            double separuh = Math.floor(tagihan / 2);
            txtBayarTunai.setText(String.format("%.0f", separuh));
            txtNominalQris.setText(String.format("%.0f", tagihan - separuh));
        }
        hitungKembalianLive();
    }

    private void aksiScanBarcodeEnter() {
        String barcode = txtBarcodeScan.getText().trim();
        if (barcode.isEmpty()) return;

        Produk p = katalog.cariProduk(barcode);
        if (p == null) {
            JOptionPane.showMessageDialog(this, 
                "Produk dengan kode barcode '" + barcode + "' tidak ditemukan dalam katalog!", 
                "Barcode Tidak Dikenal", JOptionPane.WARNING_MESSAGE);
            txtBarcodeScan.selectAll();
            txtBarcodeScan.requestFocus();
            return;
        }

        try {
            transaksiAktif.tambahItem(p, 1);
            muatTabelKeranjang();
            muatTabelKatalog();
            updateKalkulasiFinansial();
            txtBarcodeScan.setText("");
            txtBarcodeScan.requestFocus();
        } catch (KasirException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ditolak Aturan Kasir", JOptionPane.ERROR_MESSAGE);
            txtBarcodeScan.selectAll();
            txtBarcodeScan.requestFocus();
        }
    }

    private void updateMemberDariCombo() {
        if (transaksiAktif == null) return;
        String sel = (String) cmbTipeMember.getSelectedItem();
        String member = "REGULER";
        if (sel != null) {
            int idxKurung = sel.indexOf(" (");
            if (idxKurung > 0) {
                member = sel.substring(0, idxKurung).trim();
            } else {
                member = sel.trim();
            }
        }
        transaksiAktif.getPelanggan().ubahTipeMember(member);
        updateKalkulasiFinansial();
    }

    private void updateKalkulasiFinansial() {
        if (transaksiAktif == null) return;
        double tagihan = transaksiAktif.hitungTotalBayar();
        lblTotalKotor.setText(String.format("Rp %,d", transaksiAktif.hitungTotalSubtotal()));
        lblDiskonMember.setText(String.format("Rp %,.0f", transaksiAktif.hitungDiskonNominal()));
        lblTotalTagihan.setText(String.format("Rp %,.0f", tagihan));

        if (cmbMetodeBayar != null && cmbMetodeBayar.getSelectedIndex() == 1 && txtNominalQris != null) {
            txtNominalQris.setText(String.format("%.0f", tagihan));
        }
        hitungKembalianLive();
    }

    private void hitungKembalianLive() {
        if (lblKembalian == null || transaksiAktif == null) return;
        double tagihan = transaksiAktif.hitungTotalBayar();
        int modeIdx = (cmbMetodeBayar != null) ? cmbMetodeBayar.getSelectedIndex() : 0;

        try {
            if (modeIdx == 0) { // TUNAI
                String strTunai = (txtBayarTunai != null) ? txtBayarTunai.getText().trim() : "";
                if (strTunai.isEmpty()) {
                    lblKembalian.setText("Rp 0");
                    lblKembalian.setForeground(Color.BLACK);
                    return;
                }
                double bayar = Double.parseDouble(strTunai);
                double kembalian = bayar - tagihan;
                if (kembalian >= 0) {
                    lblKembalian.setText(String.format("Rp %,.0f", kembalian));
                    lblKembalian.setForeground(new Color(39, 174, 96));
                } else {
                    lblKembalian.setText(String.format("Kurang: Rp %,.0f", Math.abs(kembalian)));
                    lblKembalian.setForeground(new Color(220, 53, 69));
                }
            } else if (modeIdx == 1) { // QRIS
                lblKembalian.setText("Rp 0 (Pas via QRIS)");
                lblKembalian.setForeground(new Color(39, 174, 96));
            } else if (modeIdx == 2) { // SPLIT
                String strTunai = (txtBayarTunai != null) ? txtBayarTunai.getText().trim() : "0";
                String strQris = (txtNominalQris != null) ? txtNominalQris.getText().trim() : "0";
                double tunai = strTunai.isEmpty() ? 0 : Double.parseDouble(strTunai);
                double qris = strQris.isEmpty() ? 0 : Double.parseDouble(strQris);
                double totalMasuk = tunai + qris;
                double selisih = totalMasuk - tagihan;
                if (selisih >= 0) {
                    double kembalianTunai = Math.max(0, tunai - Math.max(0, tagihan - qris));
                    lblKembalian.setText(String.format("Kembali Tunai: Rp %,.0f", kembalianTunai));
                    lblKembalian.setForeground(new Color(39, 174, 96));
                } else {
                    lblKembalian.setText(String.format("Kombinasi Kurang: Rp %,.0f", Math.abs(selisih)));
                    lblKembalian.setForeground(new Color(220, 53, 69));
                }
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Nominal tidak valid");
            lblKembalian.setForeground(Color.RED);
        }
    }

    private void aksiProsesPembayaran() {
        if (transaksiAktif.getDaftarItem().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modeIdx = (cmbMetodeBayar != null) ? cmbMetodeBayar.getSelectedIndex() : 0;
        MetodePembayaran strategi;
        double nominalInput;

        try {
            if (modeIdx == 0) { // TUNAI
                String inputTunai = txtBayarTunai.getText().trim();
                if (inputTunai.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Masukkan jumlah uang tunai yang diserahkan konsumen!", "Validasi Pembayaran", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nominalInput = Double.parseDouble(inputTunai);
                strategi = new PembayaranTunai();
            } else if (modeIdx == 1) { // QRIS
                String inputQris = txtNominalQris.getText().trim();
                if (inputQris.isEmpty()) {
                    nominalInput = transaksiAktif.hitungTotalBayar();
                } else {
                    nominalInput = Double.parseDouble(inputQris);
                }
                strategi = new PembayaranQrisSimulasi("NMID-SIM-" + System.currentTimeMillis());
            } else { // SPLIT
                String inputTunai = txtBayarTunai.getText().trim();
                String inputQris = txtNominalQris.getText().trim();
                double t = inputTunai.isEmpty() ? 0 : Double.parseDouble(inputTunai);
                double q = inputQris.isEmpty() ? 0 : Double.parseDouble(inputQris);
                nominalInput = t + q;
                strategi = new SplitPayment(t, q);
            }

            boolean sukses = transaksiAktif.prosesTransaksi(strategi, nominalInput);
            if (sukses) {
                String namaKasir = (currentUser != null) ? currentUser.getUsername() : "system";

                // 1. Catat ke berkas transaksi.txt (Format 9 kolom dengan nama kasir)
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, transaksiAktif, nominalInput, strategi.getKembalian(), namaKasir);

                // 2. Simpan rincian per baris belanjaan ke data/transaksi_item.txt
                try {
                    transaksiItemRepo.simpanItemTransaksi(PATH_TRANSAKSI_ITEM, transaksiAktif);
                } catch (IOException exItem) {
                    System.err.println("[RISIKO NON-ACID] Gagal menulis transaksi_item.txt: " + exItem.getMessage());
                    JOptionPane.showMessageDialog(this, 
                        "Nota berhasil dibukukan, namun rincian item ke transaksi_item.txt gagal disimpan:\n" + exItem.getMessage(), 
                        "Peringatan I/O Item", JOptionPane.WARNING_MESSAGE);
                }

                // 3. Simpan pengurangan stok ke berkas produk.txt via jalur aman
                simpanKatalogAmanKeFile(PATH_PRODUK);

                // Tampilkan Struk Pembayaran Modal dengan opsi Cetak & Ekspor
                tampilkanDialogStruk(transaksiAktif, nominalInput, strategi.getKembalian(), strategi);

                // Buat transaksi baru
                mulaiTransaksiBaru();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran tidak valid!", "Error Format", JOptionPane.ERROR_MESSAGE);
        } catch (PembayaranKurangException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Uang Pembayaran Kurang", JOptionPane.ERROR_MESSAGE);
        } catch (KasirException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Pelanggaran Kasir", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menulis riwayat transaksi: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanDialogStruk(Transaksi trx, double nominalInput, double kembalian, MetodePembayaran metode) {
        JDialog dlg = new JDialog(this, "Struk Pembayaran Toko - " + trx.getIdTransaksi(), true);
        dlg.setSize(520, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);

        String teksStruk = trx.generateTeksStruk();
        ta.setText(teksStruk);
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

        JPanel pnlAksiStruk = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        ModernButton btnCetakKertas = new ModernButton("🖨️ Cetak ke Printer", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnEksporFile = new ModernButton("💾 Simpan Berkas Teks", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnTutup = new ModernButton("Selesai / Tutup", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);

        btnCetakKertas.addActionListener(e -> {
            try {
                boolean selesai = ta.print();
                if (selesai) {
                    JOptionPane.showMessageDialog(dlg, "Pencetakan struk selesai!", "Printer POS", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception exPrint) {
                JOptionPane.showMessageDialog(dlg, "Pencetakan gagal / dibatalkan: " + exPrint.getMessage(), "Printer Status", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEksporFile.addActionListener(e -> {
            try {
                Path strukPath = Paths.get("data", "struk_" + trx.getIdTransaksi() + ".txt");
                java.nio.file.Files.write(strukPath, teksStruk.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(dlg, "Struk tersimpan di: " + strukPath.toAbsolutePath(), "Ekspor Struk Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException exIo) {
                JOptionPane.showMessageDialog(dlg, "Gagal menyimpan struk: " + exIo.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnTutup.addActionListener(e -> dlg.dispose());

        pnlAksiStruk.add(btnCetakKertas);
        pnlAksiStruk.add(btnEksporFile);
        pnlAksiStruk.add(btnTutup);
        dlg.add(pnlAksiStruk, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void aksiBukaDialogEditProduk() {
        try {
            AuthService.enforceAdmin(currentUser, "Edit Data Produk");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = tabelKatalog.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Silakan klik salah satu baris produk pada tabel katalog terlebih dahulu untuk diedit!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = (String) modelKatalog.getValueAt(selectedRow, 0);
        Produk p = katalog.cariProduk(kode);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Produk dengan kode '" + kode + "' tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(this, "Edit Data Produk - " + p.getKode(), true);
        dlg.setSize(440, 430);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(8, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtKode = new JTextField(p.getKode());
        txtKode.setEditable(false);
        txtKode.setBackground(new Color(240, 240, 240));

        final boolean sedangDiKeranjang = (transaksiAktif != null && transaksiAktif.adaProdukAktif(kode));
        final int qtyDiKeranjang = (transaksiAktif != null ? transaksiAktif.getKuantitasItemAktif(kode) : 0);

        JComboBox<String> cmbKategori = new JComboBox<>();
        for (String k : pengaturanSistem.getDaftarKategori()) {
            cmbKategori.addItem(k);
        }
        if (p.getKategori() != null && !p.getKategori().isEmpty() && !pengaturanSistem.getDaftarKategori().contains(p.getKategori())) {
            cmbKategori.addItem(p.getKategori());
        }
        cmbKategori.setSelectedItem(p.getKategori());

        JTextField txtNama = new JTextField(p.getNama());
        JTextField txtHarga = new JTextField(String.valueOf(p.getHarga()));
        JTextField txtStok = new JTextField(String.valueOf(p.getStok()));
        JTextField txtDiskonPromo = new JTextField(String.format(java.util.Locale.US, "%.0f", p.getDiskonPromo() * 100));

        if (sedangDiKeranjang) {
            txtStok.setEditable(false);
            txtStok.setBackground(new Color(240, 240, 240));
            txtStok.setToolTipText("Stok dikunci: Terdapat " + qtyDiKeranjang + " unit produk ini di keranjang belanja aktif.");
        }

        JLabel lblExtra;
        JComponent compExtra;
        KalenderDatePicker pickerKadaluarsa = null;
        JTextField txtGaransi = null;

        if (p instanceof ProdukMakanan) {
            lblExtra = new JLabel("Tgl Kadaluarsa:");
            LocalDate tglAwal;
            try {
                tglAwal = LocalDate.parse(((ProdukMakanan) p).getTanggalKadaluarsa());
            } catch (Exception ex) {
                tglAwal = LocalDate.now().plusMonths(6);
            }
            pickerKadaluarsa = new KalenderDatePicker(dlg, "yyyy-MM-dd", tglAwal);
            compExtra = pickerKadaluarsa;
        } else {
            lblExtra = new JLabel("Garansi Resmi (Bulan):");
            txtGaransi = new JTextField(String.valueOf(((ProdukElektronik) p).getGaransiBulan()));
            compExtra = txtGaransi;
        }

        dlg.add(new JLabel("Kode Produk:"));
        dlg.add(txtKode);
        dlg.add(new JLabel("Kategori Produk:"));
        dlg.add(cmbKategori);
        dlg.add(new JLabel("Nama Produk:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Tersedia:"));
        dlg.add(txtStok);
        dlg.add(new JLabel("Diskon Promo Barang (%):"));
        dlg.add(txtDiskonPromo);
        dlg.add(lblExtra);
        dlg.add(compExtra);

        ModernButton btnBatal = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnBatal.addActionListener(e -> dlg.dispose());
        dlg.add(btnBatal);

        final KalenderDatePicker finalPicker = pickerKadaluarsa;
        final JTextField finalTxtGaransi = txtGaransi;

        ModernButton btnSimpan = new ModernButton("Simpan Perubahan", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnSimpan.addActionListener(e -> {
            try {
                int hargaBaru = Integer.parseInt(txtHarga.getText().trim());
                int stokBaru = Integer.parseInt(txtStok.getText().trim());
                String namaBaru = txtNama.getText().trim();
                String extraBaru = (finalPicker != null) ? finalPicker.getText() : finalTxtGaransi.getText().trim();
                String rawDiskon = txtDiskonPromo.getText().trim().replace("%", "").replace(",", ".");
                double valDiskon = Double.parseDouble(rawDiskon);
                double diskonPromoBaru;
                if (rawDiskon.startsWith("0.") && valDiskon > 0.0 && valDiskon < 1.0) {
                    diskonPromoBaru = valDiskon;
                } else {
                    if (valDiskon < 0.0 || valDiskon > 100.0) {
                        throw new IllegalArgumentException("[ERROR VALIDASI] Diskon promo harus di antara 0% dan 100%!");
                    }
                    diskonPromoBaru = valDiskon / 100.0;
                }

                String kategoriBaru = (String) cmbKategori.getSelectedItem();
                if (kategoriBaru == null || kategoriBaru.trim().isEmpty()) {
                    kategoriBaru = p.getKategori();
                }

                katalog.ubahProduk(p.getKode(), namaBaru, hargaBaru, stokBaru, extraBaru, diskonPromoBaru, kategoriBaru);
                simpanKatalogAmanKeFile(PATH_PRODUK);

                if (!pengaturanSistem.getDaftarKategori().contains(kategoriBaru)) {
                    try {
                        pengaturanSistem.tambahKategori(kategoriBaru);
                        pengaturanSistem.simpanKeFile();
                    } catch (Exception ignored) {}
                }
                renderBarKategori();
                muatTabelKatalog();
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "Data produk '" + p.getKode() + "' berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.add(btnSimpan);
        dlg.setVisible(true);
    }

    private void aksiBukaDialogProdukBaru() {
        try {
            AuthService.enforceAdmin(currentUser, "Tambah Produk Baru");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(this, "Registrasi Komoditas Produk Baru", true);
        dlg.setSize(440, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(9, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Makanan & Minuman", "Elektronik & Gadget"});
        JComboBox<String> cmbKategori = new JComboBox<>();
        cmbKategori.setEditable(true);
        for (String k : pengaturanSistem.getDaftarKategori()) {
            cmbKategori.addItem(k);
        }
        cmbKategori.setSelectedItem("Makanan");

        JTextField txtKode = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtHarga = new JTextField();
        JTextField txtStok = new JTextField();
        JTextField txtDiskonPromo = new JTextField("0");
        JLabel lblExtra = new JLabel("Tgl Kadaluarsa:");
        KalenderDatePicker pickerKadaluarsa = new KalenderDatePicker(dlg, "yyyy-MM-dd", LocalDate.of(2026, 12, 31));
        JTextField txtGaransi = new JTextField("12");

        CardLayout cardExtra = new CardLayout();
        JPanel pnlExtraContainer = new JPanel(cardExtra);
        pnlExtraContainer.add(pickerKadaluarsa, "MAKANAN");
        pnlExtraContainer.add(txtGaransi, "ELEKTRONIK");
        cardExtra.show(pnlExtraContainer, "MAKANAN");

        cmbJenis.addActionListener(e -> {
            if (cmbJenis.getSelectedIndex() == 0) {
                lblExtra.setText("Tgl Kadaluarsa:");
                cardExtra.show(pnlExtraContainer, "MAKANAN");
                if ("Elektronik".equalsIgnoreCase((String) cmbKategori.getSelectedItem())) {
                    cmbKategori.setSelectedItem("Makanan");
                }
            } else {
                lblExtra.setText("Garansi Resmi (Bulan):");
                cardExtra.show(pnlExtraContainer, "ELEKTRONIK");
                if ("Makanan".equalsIgnoreCase((String) cmbKategori.getSelectedItem()) || "Minuman".equalsIgnoreCase((String) cmbKategori.getSelectedItem())) {
                    cmbKategori.setSelectedItem("Elektronik");
                }
            }
        });

        dlg.add(new JLabel("Jenis Komoditas:"));
        dlg.add(cmbJenis);
        dlg.add(new JLabel("Kategori Produk:"));
        dlg.add(cmbKategori);
        dlg.add(new JLabel("Kode Produk (Unik):"));
        dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga Jual (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Awal Inventaris:"));
        dlg.add(txtStok);
        dlg.add(new JLabel("Diskon Promo Barang (%):"));
        dlg.add(txtDiskonPromo);
        dlg.add(lblExtra);
        dlg.add(pnlExtraContainer);

        ModernButton btnBatal = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnBatal.addActionListener(e -> dlg.dispose());
        dlg.add(btnBatal);

        ModernButton btnSimpan = new ModernButton("Daftarkan Produk", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnSimpan.addActionListener(e -> {
            try {
                String kode = txtKode.getText().trim();
                String nama = txtNama.getText().trim();
                int harga = Integer.parseInt(txtHarga.getText().trim());
                int stok = Integer.parseInt(txtStok.getText().trim());
                String extra = (cmbJenis.getSelectedIndex() == 0) ? pickerKadaluarsa.getText() : txtGaransi.getText().trim();
                String rawDiskon = txtDiskonPromo.getText().trim().replace("%", "").replace(",", ".");
                double valDiskon = Double.parseDouble(rawDiskon);
                double diskonPromo;
                if (rawDiskon.startsWith("0.") && valDiskon > 0.0 && valDiskon < 1.0) {
                    diskonPromo = valDiskon;
                } else {
                    if (valDiskon < 0.0 || valDiskon > 100.0) {
                        throw new IllegalArgumentException("[ERROR VALIDASI] Diskon promo harus di antara 0% dan 100%!");
                    }
                    diskonPromo = valDiskon / 100.0;
                }

                Object katObj = cmbKategori.getSelectedItem();
                String kategoriPilihan = (katObj != null) ? katObj.toString().trim() : "";
                if (kategoriPilihan.isEmpty()) {
                    kategoriPilihan = (cmbJenis.getSelectedIndex() == 0) ? "Makanan" : "Elektronik";
                }

                Produk p;
                if (cmbJenis.getSelectedIndex() == 0) {
                    p = new ProdukMakanan(kode, nama, harga, stok, extra, diskonPromo, kategoriPilihan);
                } else {
                    int garansi = Integer.parseInt(extra);
                    p = new ProdukElektronik(kode, nama, harga, stok, garansi, diskonPromo, kategoriPilihan);
                }

                katalog.tambahProduk(p);
                simpanKatalogAmanKeFile(PATH_PRODUK);

                if (!pengaturanSistem.getDaftarKategori().contains(kategoriPilihan)) {
                    try {
                        pengaturanSistem.tambahKategori(kategoriPilihan);
                        pengaturanSistem.simpanKeFile();
                    } catch (Exception ignored) {}
                }
                renderBarKategori();
                muatTabelKatalog();
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "Produk baru berhasil didaftarkan ke katalog dan berkas disk!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.add(btnSimpan);
        dlg.setVisible(true);
    }

    private void aksiHapusProdukKatalog() {
        try {
            AuthService.enforceAdmin(currentUser, "Hapus Produk");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabelKatalog.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = (String) modelKatalog.getValueAt(row, 0);

        int konf = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk berkode '" + kode + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konf == JOptionPane.YES_OPTION) {
            try {
                if (katalog.hapusProduk(kode, transaksiAktif)) {
                    simpanKatalogAmanKeFile(PATH_PRODUK);
                    muatTabelKatalog();
                    JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Produk tidak ditemukan dalam katalog!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (KasirException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Penolakan Hapus", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                System.err.println("[ERROR I/O] Gagal sinkronisasi katalog pasca hapus: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Gagal memperbarui file produk: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void simpanKatalogAmanKeFile(Path path) throws IOException {
        produkRepo.simpanAman(path, katalog, transaksiAktif);
    }

    private void aksiSimpanKatalogFile() {
        try {
            simpanKatalogAmanKeFile(PATH_PRODUK);
            JOptionPane.showMessageDialog(this, "Seluruh katalog berhasil disinkronkan ke berkas " + PATH_PRODUK, "Sinkronisasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan berkas: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiBukaLaporanHarian() {
        try {
            AuthService.enforceAdmin(currentUser, "Melihat Laporan Harian");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Ditolak (Hanya Admin)", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(this, "Laporan Penjualan Harian & Tutup Kasir", true);
        dlg.setSize(750, 540);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        KalenderDatePicker pickerLaporan = new KalenderDatePicker(dlg, "dd-MM-yyyy", LocalDate.now());
        ModernButton btnFilter = new ModernButton("Tampilkan Laporan", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);

        topBar.add(new JLabel("Pilih Tanggal Transaksi:"));
        topBar.add(pickerLaporan);
        topBar.add(btnFilter);
        dlg.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        JTextArea txtRingkasan = new JTextArea();
        txtRingkasan.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRingkasan.setEditable(false);

        DefaultTableModel modelTop = new DefaultTableModel(new String[]{"No", "Kode", "Nama Produk", "Total Terjual (Unit)"}, 0);
        JTable tblTop = new JTable(modelTop);
        tblTop.setRowHeight(25);
        tblTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTop.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tblTop.getTableHeader().setDefaultRenderer(new ModernTableHeaderRenderer());
        tblTop.setGridColor(TemaWarna.BORDER_DIVIDER);

        Runnable muatLaporan = () -> {
            try {
                LaporanPenjualan lap = LaporanPenjualan.buatLaporan(PATH_TRANSAKSI, PATH_TRANSAKSI_ITEM, pickerLaporan.getText());
                StringBuilder sb = new StringBuilder();
                sb.append("===================================================================\n");
                sb.append("                 RINGKASAN LAPORAN PENJUALAN HARIAN                \n");
                sb.append("===================================================================\n");
                sb.append("Tanggal Laporan        : ").append(lap.getFilterTanggal()).append("\n");
                sb.append("Periode Produk Terlaris: ").append(lap.getNamaBulanTahun()).append(" (Akumulasi Bulanan)\n");
                sb.append("Petugas Verifikasi     : ").append(currentUser.getUsername()).append(" (ADMIN)\n");
                sb.append("-------------------------------------------------------------------\n");
                sb.append("Jumlah Nota LUNAS      : ").append(lap.getJumlahNotaLunas()).append(" Transaksi\n");
                sb.append(String.format("Total Omzet Penjualan  : Rp %,.0f\n", lap.getTotalOmzet()));
                sb.append(String.format("Total Tunai Masuk      : Rp %,.0f\n", lap.getTotalTunai()));
                sb.append("-------------------------------------------------------------------\n");
                sb.append("Jumlah Nota VOID/Batal : ").append(lap.getJumlahNotaVoid()).append(" Transaksi (Dikecualikan dari Omzet)\n");
                if (lap.isAdaTransaksiTanpaRincian()) {
                    sb.append("Catatan Audit          : Terdapat transaksi lama tanpa rincian item (data legacy).\n");
                }
                sb.append("===================================================================\n");
                txtRingkasan.setText(sb.toString());

                modelTop.setRowCount(0);
                int no = 1;
                for (LaporanPenjualan.ItemTerlaris it : lap.getProdukTerlarisTop5()) {
                    modelTop.addRow(new Object[]{no++, it.getKodeProduk(), it.getNamaProduk(), it.getTotalQty()});
                }
                if (tabs.getTabCount() > 1) {
                    tabs.setTitleAt(1, "Produk Terlaris (" + lap.getNamaBulanTahun() + ")");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal membuat laporan: " + ex.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        };

        tabs.addTab("Ringkasan Finansial", new JScrollPane(txtRingkasan));
        tabs.addTab("Produk Terlaris", new JScrollPane(tblTop));
        dlg.add(tabs, BorderLayout.CENTER);

        btnFilter.addActionListener(e -> muatLaporan.run());
        muatLaporan.run();

        JPanel pnlBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ModernButton btnTutup = new ModernButton("Tutup", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnTutup.addActionListener(e -> dlg.dispose());
        pnlBawah.add(btnTutup);
        dlg.add(pnlBawah, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void aksiBukaRiwayatVoid() {
        JDialog dlg = new JDialog(this, "Riwayat Transaksi & Pembatalan (VOID Nota)", true);
        dlg.setSize(850, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        DefaultTableModel modelTrx = new DefaultTableModel(new String[]{
            "ID Transaksi", "Waktu", "Pelanggan", "Member", "Total Bayar", "Status", "Kasir"
        }, 0);
        JTable tblTrx = new JTable(modelTrx);
        tblTrx.setRowHeight(25);
        tblTrx.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTrx.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tblTrx.getTableHeader().setDefaultRenderer(new ModernTableHeaderRenderer());
        tblTrx.setGridColor(TemaWarna.BORDER_DIVIDER);
        tblTrx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable refreshData = () -> {
            try {
                modelTrx.setRowCount(0);
                List<String[]> list = transaksiRepo.muatSemuaTransaksi(PATH_TRANSAKSI);
                for (String[] t : list) {
                    modelTrx.addRow(new Object[]{t[0], t[1], t[2], t[3], String.format("Rp %,.0f", Double.parseDouble(t[4])), t[7], t[8]});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal memuat transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        refreshData.run();
        dlg.add(new JScrollPane(tblTrx), BorderLayout.CENTER);

        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        ModernButton btnRincian = new ModernButton("Lihat Rincian Item", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnVoid = new ModernButton("⚠️ VOID Nota Lunas", TemaWarna.TOMBOL_BATAL, TemaWarna.TEKS_PUTIH);

        btnRincian.addActionListener(e -> {
            int row = tblTrx.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dlg, "Pilih salah satu baris transaksi terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idTrx = (String) modelTrx.getValueAt(row, 0);
            try {
                List<String[]> items = transaksiItemRepo.muatItemByTransaksi(PATH_TRANSAKSI_ITEM, idTrx);
                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Transaksi '" + idTrx + "' tidak memiliki log item tersimpan (mungkin data lama).", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("Rincian Item Belanja - " + idTrx + ":\n\n");
                    int no = 1;
                    for (String[] it : items) {
                        sb.append(String.format("%d. %s - %s | Qty: %s x Rp %s | Subtotal: Rp %s\n",
                                no++, it[0], it[1], it[3], it[2], it[5]));
                    }
                    JOptionPane.showMessageDialog(dlg, sb.toString(), "Rincian Item Nota", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal membaca item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVoid.addActionListener(e -> {
            try {
                AuthService.enforceAdmin(currentUser, "VOID Nota");
            } catch (AksesDitolakException ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int row = tblTrx.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dlg, "Pilih nota yang ingin di-VOID terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idTrx = (String) modelTrx.getValueAt(row, 0);

            int konf = JOptionPane.showConfirmDialog(dlg,
                "Anda akan membatalkan (VOID) nota '" + idTrx + "'.\n" +
                "Semua stok produk pada nota ini akan dikembalikan ke katalog inventaris.\n\n" +
                "Yakin ingin melanjutkan operasi VOID?", "Konfirmasi VOID Nota", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (konf != JOptionPane.YES_OPTION) return;

            try {
                transaksiRepo.voidTransaksi(PATH_TRANSAKSI, PATH_TRANSAKSI_ITEM, idTrx, katalog, currentUser, transaksiItemRepo);
                simpanKatalogAmanKeFile(PATH_PRODUK);
                muatTabelKatalog();
                refreshData.run();
                JOptionPane.showMessageDialog(dlg, "Nota '" + idTrx + "' berhasil di-VOID dan stok inventaris telah dipulihkan!", "Sukses VOID", JOptionPane.INFORMATION_MESSAGE);
            } catch (KasirException ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "VOID Ditolak", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal memperbarui berkas: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlAksi.add(btnRincian);
        pnlAksi.add(btnVoid);
        dlg.add(pnlAksi, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
}
