package view;

import model.*;
import model.exception.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * KasirDesktopFrame - Modern Swing Graphical User Interface (GUI)
 * Sistem Kasir Sederhana (Point of Sale) Versi Desktop (Fase 1: Multi-role, Item Log, Laporan & VOID)
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

    // Sesi Pengguna Aktif
    private transient User currentUser;

    // GUI Components - Table Models
    private DefaultTableModel modelKatalog;
    private DefaultTableModel modelKeranjang;
    private JTable tabelKatalog;
    private JTable tabelKeranjang;

    // Search & Filter
    private JTextField txtCari;

    // Pelanggan & Transaksi Active State
    private JTextField txtNamaPelanggan;
    private JComboBox<String> cmbTipeMember;
    private transient Transaksi transaksiAktif;

    // Financial Display Labels
    private JLabel lblTotalKotor;
    private JLabel lblDiskonMember;
    private JLabel lblTotalTagihan;
    private JTextField txtBayarTunai;
    private JLabel lblKembalian;
    private JLabel lblUserBadge;

    @SuppressWarnings("this-escape")
    public KasirDesktopFrame() {
        super("Sistem Kasir Sederhana (POS Desktop) - Rafael Rizky (3125522007)");
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
    }

    private void prosesLoginAwal() {
        if (authService.isUsersFileEmpty()) {
            JPasswordField pwdAdmin = new JPasswordField(15);
            JPanel pnl = new JPanel(new GridLayout(3, 1, 5, 5));
            pnl.add(new JLabel("First-Run Setup: Berkas data/users.txt belum memiliki akun."));
            pnl.add(new JLabel("Buat Password untuk Akun Administrator ('admin'):"));
            pnl.add(pwdAdmin);

            int opt = JOptionPane.showConfirmDialog(null, pnl, "Setup Akun Administrator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            String pwdStr = new String(pwdAdmin.getPassword()).trim();
            if (opt != JOptionPane.OK_OPTION || pwdStr.isEmpty()) {
                pwdStr = "admin123";
            }
            try {
                authService.inisialisasiAdminAwal(pwdStr);
                authService.register("kasir", "kasir123", Role.KASIR);
            } catch (Exception ex) {
                System.err.println("Gagal setup admin awal: " + ex.getMessage());
            }
        }

        boolean sukses = false;
        while (!sukses) {
            JTextField txtUser = new JTextField("admin", 15);
            JPasswordField txtPass = new JPasswordField(15);
            JPanel pnlLogin = new JPanel(new GridLayout(4, 1, 5, 5));
            pnlLogin.add(new JLabel("Username:"));
            pnlLogin.add(txtUser);
            pnlLogin.add(new JLabel("Password:"));
            pnlLogin.add(txtPass);

            int opt = JOptionPane.showConfirmDialog(null, pnlLogin, "Login Sistem Kasir POS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            try {
                this.currentUser = authService.login(txtUser.getText(), new String(txtPass.getPassword()));
                sukses = true;
            } catch (KasirException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Gagal Otentikasi", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (lblUserBadge != null) {
            lblUserBadge.setText(currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        }
        setTitle("Sistem Kasir POS - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
    }

    private void mulaiTransaksiBaru() {
        String nama = (txtNamaPelanggan != null && !txtNamaPelanggan.getText().trim().isEmpty()) 
                      ? txtNamaPelanggan.getText().trim() : "Pelanggan Umum";
        String rawMember = (cmbTipeMember != null) ? (String) cmbTipeMember.getSelectedItem() : "REGULER";
        String member = "REGULER";
        if (rawMember != null) {
            if (rawMember.contains("VIP")) member = "VIP";
            else if (rawMember.contains("GOLD")) member = "GOLD";
        }

        String autoId = "TRX-" + System.currentTimeMillis();
        String tgl = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Pelanggan p = new Pelanggan("CUST-" + (System.currentTimeMillis() % 1000), nama, "081234567890", member);

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
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. HEADER PANEL DENGAN ROLE INDICATOR & MENU ADMIN
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(27, 54, 93));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("SISTEM KASIR POINT OF SALE (POS DESKTOP)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("PBO 2026 | Rafael Rizky Luga Tanjung (NRP: 3125522007) | PENS PSDKU Sumenep");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(200, 220, 255));
        headerLeft.add(lblTitle);
        headerLeft.add(lblSub);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        headerRight.setOpaque(false);

        lblUserBadge = new JLabel(currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        lblUserBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserBadge.setForeground(new Color(255, 230, 100));

        JButton btnLaporan = new JButton("📊 Laporan Harian");
        btnLaporan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLaporan.setBackground(new Color(52, 152, 219));
        btnLaporan.setForeground(Color.WHITE);
        btnLaporan.addActionListener(e -> aksiBukaLaporanHarian());

        JButton btnRiwayatVoid = new JButton("📜 Riwayat / VOID");
        btnRiwayatVoid.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRiwayatVoid.setBackground(new Color(230, 126, 34));
        btnRiwayatVoid.setForeground(Color.WHITE);
        btnRiwayatVoid.addActionListener(e -> aksiBukaRiwayatVoid());

        JButton btnGantiAkun = new JButton("👤 Ganti Akun");
        btnGantiAkun.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnGantiAkun.addActionListener(e -> {
            prosesLoginAwal();
            muatTabelKatalog();
        });

        headerRight.add(new JLabel("Petugas: "));
        headerRight.getComponent(0).setForeground(Color.WHITE);
        headerRight.add(lblUserBadge);
        headerRight.add(btnLaporan);
        headerRight.add(btnRiwayatVoid);
        headerRight.add(btnGantiAkun);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. MAIN SPLIT PANEL (Katalog vs Keranjang)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buatPanelKatalog(), buatPanelKeranjang());
        splitPane.setDividerLocation(620);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // 3. FOOTER STATUS BAR
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        footerPanel.setBackground(new Color(240, 243, 246));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 210)));

        JLabel lblStatus = new JLabel("Status Sistem: Multi-role aktif (Admin/Kasir) | Terhubung ke data/produk.txt & data/transaksi_item.txt.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(80, 90, 100));
        footerPanel.add(lblStatus);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel buatPanelKatalog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblSection = new JLabel("KATALOG INVENTARIS PRODUK");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection.setForeground(new Color(40, 60, 80));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        txtCari = new JTextField(12);
        JButton btnCari = new JButton("Cari");
        JButton btnReset = new JButton("Reset");

        btnCari.addActionListener(e -> filterKatalog(txtCari.getText()));
        btnReset.addActionListener(e -> { txtCari.setText(""); muatTabelKatalog(); });
        txtCari.addActionListener(e -> filterKatalog(txtCari.getText()));

        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnReset);

        topPanel.add(lblSection, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Kode", "Nama Produk", "Kategori", "Harga (Rp)", "Stok", "Info Khusus"};
        modelKatalog = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKatalog = new JTable(modelKatalog);
        tabelKatalog.setRowHeight(24);
        tabelKatalog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelKatalog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelKatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        muatTabelKatalog();

        panel.add(new JScrollPane(tabelKatalog), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton btnTambahKeranjang = new JButton("+ Tambah ke Keranjang");
        btnTambahKeranjang.setBackground(new Color(40, 167, 69));
        btnTambahKeranjang.setForeground(Color.WHITE);
        btnTambahKeranjang.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnBaru = new JButton("+ Produk Baru");
        JButton btnEdit = new JButton("Edit Produk");
        btnEdit.setBackground(new Color(23, 162, 184));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnHapus = new JButton("Hapus Produk");
        JButton btnSimpanKatalog = new JButton("Simpan ke File");

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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        JPanel customerPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Informasi Konsumen & Member"));

        customerPanel.add(new JLabel("Nama Pelanggan:"));
        txtNamaPelanggan = new JTextField("Pelanggan Umum");
        customerPanel.add(txtNamaPelanggan);

        customerPanel.add(new JLabel("Tipe Membership:"));
        cmbTipeMember = new JComboBox<>(new String[]{"REGULER (Diskon 0%)", "GOLD (Diskon 10%)", "VIP (Diskon 15%)"});
        cmbTipeMember.addActionListener(e -> updateMemberDariCombo());
        customerPanel.add(cmbTipeMember);

        panel.add(customerPanel, BorderLayout.NORTH);

        String[] cols = {"Kode", "Nama Produk", "Qty", "Diskon Item", "Subtotal (Rp)"};
        modelKeranjang = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        tabelKeranjang.setRowHeight(24);
        tabelKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelKeranjang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));

        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHapusItem = new JButton("Batalkan Item Terpilih");
        JButton btnResetTrx = new JButton("Reset Transaksi");

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
        cartActions.add(btnHapusItem);
        cartActions.add(btnResetTrx);
        bottomContainer.add(cartActions, BorderLayout.NORTH);

        JPanel paymentPanel = new JPanel(new GridLayout(5, 2, 8, 6));
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Rincian Pembayaran Kasir"),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        lblTotalKotor = new JLabel("Rp 0");
        lblDiskonMember = new JLabel("Rp 0");
        lblTotalTagihan = new JLabel("Rp 0");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalTagihan.setForeground(new Color(200, 35, 51));

        txtBayarTunai = new JTextField();
        txtBayarTunai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBayarTunai.addKeyListener(new KeyAdapter() {
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
        paymentPanel.add(new JLabel("Uang Tunai Diterima (Rp):"));
        paymentPanel.add(txtBayarTunai);
        paymentPanel.add(new JLabel("Kembalian Konsumen:"));
        paymentPanel.add(lblKembalian);

        bottomContainer.add(paymentPanel, BorderLayout.CENTER);

        JButton btnBayar = new JButton("BAYAR & PROSES CHECKOUT");
        btnBayar.setBackground(new Color(40, 167, 69));
        btnBayar.setForeground(Color.WHITE);
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBayar.setPreferredSize(new Dimension(0, 42));
        btnBayar.addActionListener(e -> aksiProsesPembayaran());
        bottomContainer.add(btnBayar, BorderLayout.SOUTH);

        panel.add(bottomContainer, BorderLayout.SOUTH);
        return panel;
    }

    private void muatTabelKatalog() {
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.getDaftarProdukReadOnly()) {
            String infoKhusus = "";
            String kategori = (p instanceof ProdukMakanan) ? "Makanan" : "Elektronik";
            if (p instanceof ProdukMakanan) {
                infoKhusus = "Exp: " + ((ProdukMakanan) p).getTanggalKadaluarsa();
            } else if (p instanceof ProdukElektronik) {
                infoKhusus = "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " bln";
            }
            modelKatalog.addRow(new Object[]{
                p.getKode(), p.getNama(), kategori,
                String.format("%,d", p.getHarga()), p.getStok(), infoKhusus
            });
        }
    }

    private void filterKatalog(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            muatTabelKatalog();
            return;
        }
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.cariProdukByNama(kw)) {
            String infoKhusus = (p instanceof ProdukMakanan) ? "Exp: " + ((ProdukMakanan) p).getTanggalKadaluarsa()
                    : "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " bln";
            modelKatalog.addRow(new Object[]{
                p.getKode(), p.getNama(), (p instanceof ProdukMakanan ? "Makanan" : "Elektronik"),
                String.format("%,d", p.getHarga()), p.getStok(), infoKhusus
            });
        }
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

    private void updateMemberDariCombo() {
        if (transaksiAktif == null) return;
        String sel = (String) cmbTipeMember.getSelectedItem();
        String member = "REGULER";
        if (sel != null) {
            if (sel.contains("VIP")) member = "VIP";
            else if (sel.contains("GOLD")) member = "GOLD";
        }
        transaksiAktif.getPelanggan().ubahTipeMember(member);
        updateKalkulasiFinansial();
    }

    private void updateKalkulasiFinansial() {
        if (transaksiAktif == null) return;
        lblTotalKotor.setText(String.format("Rp %,d", transaksiAktif.hitungTotalSubtotal()));
        lblDiskonMember.setText(String.format("Rp %,.0f", transaksiAktif.hitungDiskonNominal()));
        lblTotalTagihan.setText(String.format("Rp %,.0f", transaksiAktif.hitungTotalBayar()));
        hitungKembalianLive();
    }

    private void hitungKembalianLive() {
        if (txtBayarTunai == null || lblKembalian == null || transaksiAktif == null) return;
        String str = txtBayarTunai.getText().trim();
        if (str.isEmpty()) {
            lblKembalian.setText("Rp 0");
            lblKembalian.setForeground(Color.BLACK);
            return;
        }
        try {
            double bayar = Double.parseDouble(str);
            double tagihan = transaksiAktif.hitungTotalBayar();
            double kembalian = bayar - tagihan;
            if (kembalian >= 0) {
                lblKembalian.setText(String.format("Rp %,.0f", kembalian));
                lblKembalian.setForeground(new Color(40, 167, 69));
            } else {
                lblKembalian.setText(String.format("Kurang: Rp %,.0f", Math.abs(kembalian)));
                lblKembalian.setForeground(new Color(220, 53, 69));
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Angka tidak valid");
            lblKembalian.setForeground(Color.RED);
        }
    }

    private void aksiProsesPembayaran() {
        if (transaksiAktif.getDaftarItem().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String inputTunai = txtBayarTunai.getText().trim();
        if (inputTunai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah uang tunai yang diserahkan konsumen!", "Validasi Pembayaran", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double bayar = Double.parseDouble(inputTunai);
            double tagihan = transaksiAktif.hitungTotalBayar();
            double kembalian = bayar - tagihan;

            boolean sukses = transaksiAktif.prosesTransaksi(bayar);
            if (sukses) {
                String namaKasir = (currentUser != null) ? currentUser.getUsername() : "system";

                // 1. Catat ke berkas transaksi.txt (Format 9 kolom dengan nama kasir)
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, transaksiAktif, bayar, kembalian, namaKasir);

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

                // Tampilkan Struk Pembayaran Modal
                tampilkanDialogStruk(transaksiAktif, bayar, kembalian);

                // Buat transaksi baru
                mulaiTransaksiBaru();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran tunai tidak valid!", "Error Format", JOptionPane.ERROR_MESSAGE);
        } catch (PembayaranKurangException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Uang Pembayaran Kurang", JOptionPane.ERROR_MESSAGE);
        } catch (KasirException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Pelanggaran Kasir", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menulis riwayat transaksi: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanDialogStruk(Transaksi trx, double bayar, double kembalian) {
        JDialog dlg = new JDialog(this, "Struk Pembayaran Toko - " + trx.getIdTransaksi(), true);
        dlg.setSize(480, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("               STRUK PEMBAYARAN KASIR               \n");
        sb.append("====================================================\n");
        sb.append("ID Transaksi : ").append(trx.getIdTransaksi()).append("\n");
        sb.append("Waktu        : ").append(trx.getTanggal()).append("\n");
        sb.append("Kasir        : ").append(currentUser != null ? currentUser.getUsername() : "system").append("\n");
        sb.append("Pelanggan    : ").append(trx.getPelanggan().getNama()).append("\n");
        sb.append("Tipe Member  : ").append(trx.getPelanggan().getTipeMember()).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append("RINCIAN BARANG:\n");

        int no = 1;
        for (ItemTransaksi it : trx.getDaftarItem()) {
            sb.append(String.format("%d. %s - %s\n", no++, it.getProduk().getKode(), it.getNamaProduk()));
            sb.append(String.format("   %d unit x Rp %,d = Rp %,d\n", it.getJumlahBeli(), it.getHargaSatuan(), it.hitungSubtotal()));
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Total Pembelian      : Rp %,.0f\n", (double) trx.hitungTotalSubtotal()));
        sb.append(String.format("Diskon Member (%s) : Rp %,.0f\n", trx.getPelanggan().getTipeMember(), trx.hitungDiskonNominal()));
        sb.append("====================================================\n");
        sb.append(String.format("TOTAL TAGIHAN AKHIR  : Rp %,.0f\n", trx.hitungTotalBayar()));
        sb.append("====================================================\n");
        sb.append(String.format("Tunai Diterima       : Rp %,.0f\n", bayar));
        sb.append(String.format("Kembalian Konsumen   : Rp %,.0f\n", kembalian));
        sb.append("====================================================\n");
        sb.append("    Terima kasih atas kunjungan Anda di gerai kami! \n");
        sb.append("====================================================\n");

        ta.setText(sb.toString());
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Selesai / Tutup");
        btnTutup.addActionListener(e -> dlg.dispose());
        dlg.add(btnTutup, BorderLayout.SOUTH);

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
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtKode = new JTextField(p.getKode());
        txtKode.setEditable(false);
        txtKode.setBackground(new Color(240, 240, 240));

        final boolean sedangDiKeranjang = (transaksiAktif != null && transaksiAktif.adaProdukAktif(kode));
        final int qtyDiKeranjang = (transaksiAktif != null ? transaksiAktif.getKuantitasItemAktif(kode) : 0);

        JTextField txtNama = new JTextField(p.getNama());
        JTextField txtHarga = new JTextField(String.valueOf(p.getHarga()));
        JTextField txtStok = new JTextField(String.valueOf(p.getStok()));

        if (sedangDiKeranjang) {
            txtStok.setEditable(false);
            txtStok.setBackground(new Color(240, 240, 240));
            txtStok.setToolTipText("Stok dikunci: Terdapat " + qtyDiKeranjang + " unit produk ini di keranjang belanja aktif.");
        }

        JLabel lblExtra;
        JTextField txtExtra;

        if (p instanceof ProdukMakanan) {
            lblExtra = new JLabel("Tgl Kadaluarsa (DD-MM-YYYY):");
            txtExtra = new JTextField(((ProdukMakanan) p).getTanggalKadaluarsa());
        } else {
            lblExtra = new JLabel("Garansi Resmi (Bulan):");
            txtExtra = new JTextField(String.valueOf(((ProdukElektronik) p).getGaransiBulan()));
        }

        dlg.add(new JLabel("Kode Produk:"));
        dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Tersedia:"));
        dlg.add(txtStok);
        dlg.add(lblExtra);
        dlg.add(txtExtra);

        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dlg.dispose());
        dlg.add(btnBatal);

        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnSimpan.addActionListener(e -> {
            try {
                String namaBaru = txtNama.getText().trim();
                int hargaBaru = Integer.parseInt(txtHarga.getText().trim());
                int stokBaru = Integer.parseInt(txtStok.getText().trim());
                String extraBaru = txtExtra.getText().trim();

                if (sedangDiKeranjang && stokBaru != p.getStok()) {
                    JOptionPane.showMessageDialog(dlg, 
                        "Stok produk '" + p.getKode() + "' tidak dapat diubah karena sedang aktif di keranjang belanja!\n" +
                        "Batalkan transaksi atau selesaikan checkout terlebih dahulu.", 
                        "Integritas Data Terkunci", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean ok = katalog.ubahProduk(p.getKode(), namaBaru, hargaBaru, stokBaru, extraBaru);
                if (ok) {
                    simpanKatalogAmanKeFile(PATH_PRODUK);
                    muatTabelKatalog();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, "Data produk '" + p.getKode() + "' berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dlg, "Gagal mengedit produk. Kode tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Harga dan stok harus berupa angka bulat positif!", "Error Format Angka", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.add(btnSimpan);
        dlg.setVisible(true);
    }

    private void aksiBukaDialogProdukBaru() {
        try {
            AuthService.enforceAdmin(currentUser, "Menambah Produk Baru");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(this, "Registrasi Produk Baru ke Katalog", true);
        dlg.setSize(440, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(7, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtKode = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtHarga = new JTextField();
        JTextField txtStok = new JTextField();
        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Makanan / Minuman", "Elektronik / Gadget"});
        JLabel lblExtra = new JLabel("Tgl Kadaluarsa (DD-MM-YYYY):");
        JTextField txtExtra = new JTextField("31-12-2026");

        cmbJenis.addActionListener(e -> {
            if (cmbJenis.getSelectedIndex() == 0) {
                lblExtra.setText("Tgl Kadaluarsa (DD-MM-YYYY):");
                txtExtra.setText("31-12-2026");
            } else {
                lblExtra.setText("Garansi Resmi (Bulan):");
                txtExtra.setText("12");
            }
        });

        dlg.add(new JLabel("Kategori Produk:"));
        dlg.add(cmbJenis);
        dlg.add(new JLabel("Kode Unik Produk:"));
        dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga Satuan (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Awal:"));
        dlg.add(txtStok);
        dlg.add(lblExtra);
        dlg.add(txtExtra);

        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dlg.dispose());
        dlg.add(btnBatal);

        JButton btnSimpan = new JButton("Daftarkan Produk");
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnSimpan.addActionListener(e -> {
            try {
                String kode = txtKode.getText().trim();
                String nama = txtNama.getText().trim();
                int harga = Integer.parseInt(txtHarga.getText().trim());
                int stok = Integer.parseInt(txtStok.getText().trim());
                String extra = txtExtra.getText().trim();

                Produk p;
                if (cmbJenis.getSelectedIndex() == 0) {
                    p = new ProdukMakanan(kode, nama, harga, stok, extra);
                } else {
                    int garansi = Integer.parseInt(extra);
                    p = new ProdukElektronik(kode, nama, harga, stok, garansi);
                }

                katalog.tambahProduk(p);
                simpanKatalogAmanKeFile(PATH_PRODUK);
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
        String tglDefault = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        JTextField txtTgl = new JTextField(tglDefault, 10);
        JButton btnFilter = new JButton("Tampilkan Laporan");

        topBar.add(new JLabel("Pilih Tanggal Transaksi (DD-MM-YYYY):"));
        topBar.add(txtTgl);
        topBar.add(btnFilter);
        dlg.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        JTextArea txtRingkasan = new JTextArea();
        txtRingkasan.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRingkasan.setEditable(false);

        String[] colsTop = {"Kode Produk", "Nama Produk", "Total Terjual (Qty)", "Total Omzet (Rp)"};
        DefaultTableModel modelTop = new DefaultTableModel(colsTop, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblTop = new JTable(modelTop);

        tabs.addTab("Ringkasan Finansial", new JScrollPane(txtRingkasan));
        tabs.addTab("Top 5 Produk Terlaris", new JScrollPane(tblTop));
        dlg.add(tabs, BorderLayout.CENTER);

        Runnable muatLaporan = () -> {
            try {
                LaporanPenjualan lap = LaporanPenjualan.buatLaporan(PATH_TRANSAKSI, PATH_TRANSAKSI_ITEM, txtTgl.getText().trim());
                StringBuilder sb = new StringBuilder();
                sb.append("======================================================================\n");
                sb.append("                  LAPORAN PENJUALAN HARIAN POS                       \n");
                sb.append("======================================================================\n");
                sb.append("Tanggal Laporan : ").append(lap.getFilterTanggal().isEmpty() ? "Semua Tanggal" : lap.getFilterTanggal()).append("\n");
                sb.append("Dicetak Oleh    : ").append(currentUser.getUsername()).append(" (").append(currentUser.getRole()).append(")\n");
                sb.append("----------------------------------------------------------------------\n");
                sb.append(String.format("Jumlah Nota LUNAS Berhasil : %d transaksi\n", lap.getJumlahNotaLunas()));
                sb.append(String.format("Jumlah Nota Dibatalkan(VOID): %d transaksi\n", lap.getJumlahNotaVoid()));
                sb.append(String.format("TOTAL OMZET BERSIH         : Rp %,.0f\n", lap.getTotalOmzet()));
                sb.append(String.format("Total Uang Tunai Diterima   : Rp %,.0f\n", lap.getTotalTunai()));
                sb.append("======================================================================\n");

                if (lap.isAdaTransaksiTanpaRincian()) {
                    sb.append("\n[CATATAN AUDIT] Terdeteksi transaksi format lama tanpa rincian item.\nData omzet tetap sah dan dihitung penuh.\n");
                }

                txtRingkasan.setText(sb.toString());

                modelTop.setRowCount(0);
                for (LaporanPenjualan.ItemTerlaris it : lap.getProdukTerlarisTop5()) {
                    modelTop.addRow(new Object[]{it.getKodeProduk(), it.getNamaProduk(), it.getTotalQty() + " unit", String.format("Rp %,d", it.getTotalNominal())});
                }
            } catch (Exception ex) {
                txtRingkasan.setText("Gagal menghasilkan laporan: " + ex.getMessage());
            }
        };

        btnFilter.addActionListener(e -> muatLaporan.run());
        muatLaporan.run();

        dlg.setVisible(true);
    }

    private void aksiBukaRiwayatVoid() {
        JDialog dlg = new JDialog(this, "Riwayat Transaksi & Pembatalan (VOID Nota)", true);
        dlg.setSize(850, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));

        String[] cols = {"ID Transaksi", "Tanggal", "Pelanggan", "Member", "Total Bayar", "Status", "Kasir"};
        DefaultTableModel modelTrx = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblTrx = new JTable(modelTrx);
        tblTrx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable refreshData = () -> {
            modelTrx.setRowCount(0);
            try {
                for (String[] t : transaksiRepo.muatSemuaTransaksi(PATH_TRANSAKSI)) {
                    modelTrx.addRow(new Object[]{t[0], t[1], t[2], t[3], "Rp " + t[4], t[7], t[8]});
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(dlg, "Gagal memuat log transaksi: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        };

        refreshData.run();
        dlg.add(new JScrollPane(tblTrx), BorderLayout.CENTER);

        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnRincian = new JButton("Lihat Rincian Item");
        JButton btnVoid = new JButton("VOID Nota Terpilih (Admin Only)");
        btnVoid.setBackground(new Color(220, 53, 69));
        btnVoid.setForeground(Color.WHITE);
        btnVoid.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnRincian.addActionListener(e -> {
            int row = tblTrx.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dlg, "Pilih nota pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idTrx = (String) modelTrx.getValueAt(row, 0);
            try {
                List<String[]> items = transaksiItemRepo.muatItemByTransaksi(PATH_TRANSAKSI_ITEM, idTrx);
                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Rincian item tidak tersedia (Transaksi Format Lama / Legacy).", "Informasi Item", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("Rincian Belanja Nota " + idTrx + ":\n\n");
                    for (String[] it : items) {
                        sb.append(String.format("- %s (%s) x %s unit = Rp %s\n", it[0], it[1], it[3], it[5]));
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
            String status = (String) modelTrx.getValueAt(row, 5);

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
