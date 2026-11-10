package view;

import model.*;
import model.exception.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * KasirDesktopFrame - Modern Fintech SaaS GUI
 * Sistem Kasir Sederhana (Point of Sale) Versi Desktop
 * 
 * Color Palette: "Trust Ledger Fintech Dashboard" (SwatchPilot)
 * - #EAF2FF : Background & Soft Highlight
 * - #C7DDFF : Border, Badges, & Selection Background
 * - #7DA8FF : Secondary Accent & Soft Action
 * - #2563EB : Primary Action & Brand Focus
 * - #0F172A : Midnight Slate Navy (Sidebar/Header & Typography)
 * 
 * Desain: Soft Rounded Cards & Rounded Buttons (Corner Radius 12px - 18px)
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep 2026)
 */
public class KasirDesktopFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- SWATCHPILOT FINTECH COLOR TOKENS ---
    public static final Color COLOR_BG         = new Color(0xEA, 0xF2, 0xFF); // #EAF2FF
    public static final Color COLOR_BORDER     = new Color(0xC7, 0xDD, 0xFF); // #C7DDFF
    public static final Color COLOR_ACCENT     = new Color(0x7D, 0xA8, 0xFF); // #7DA8FF
    public static final Color COLOR_PRIMARY    = new Color(0x25, 0x63, 0xEB); // #2563EB
    public static final Color COLOR_DARK       = new Color(0x0F, 0x17, 0x2A); // #0F172A
    public static final Color COLOR_WHITE      = Color.WHITE;
    public static final Color COLOR_TEXT_MUTED = new Color(0x64, 0x74, 0x8B); // Slate Muted
    public static final Color COLOR_DANGER     = new Color(0xDC, 0x26, 0x26); // Crimson Red
    public static final Color COLOR_SUCCESS    = new Color(0x16, 0xA3, 0x4A); // Emerald Green

    private static final Path PATH_PRODUK = Paths.get("data/produk.txt");
    private static final Path PATH_TRANSAKSI = Paths.get("data/transaksi.txt");

    private final transient KatalogProduk katalog;
    private final transient ProdukFileRepository produkRepo;
    private final transient TransaksiFileRepository transaksiRepo;

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

    @SuppressWarnings("this-escape")
    public KasirDesktopFrame() {
        super("Trust Ledger POS • Modern Fintech Cashier System - Rafael Rizky (3125522007)");
        this.katalog = new KatalogProduk();
        this.produkRepo = new ProdukFileRepository();
        this.transaksiRepo = new TransaksiFileRepository();

        initData();
        initUI();
        mulaiTransaksiBaru();
    }

    private void initData() {
        try {
            produkRepo.muat(PATH_PRODUK, katalog);
        } catch (Exception e) {
            System.err.println("Inisialisasi berkas: " + e.getMessage());
            // Isi default awal jika berkas belum ada
            if (katalog.getDaftarProdukReadOnly().isEmpty()) {
                katalog.tambahProduk(new ProdukMakanan("M001", "Kopi Susu Gula Aren", 18000, 50, "2026-11-20"));
                katalog.tambahProduk(new ProdukMakanan("M002", "Roti Bakar Coklat", 15000, 30, "2026-11-15"));
                katalog.tambahProduk(new ProdukElektronik("E001", "Mouse Wireless Logitech", 150000, 20, 12));
                katalog.tambahProduk(new ProdukElektronik("E002", "Keyboard Mechanical TKL", 350000, 15, 24));
                try {
                    produkRepo.simpan(PATH_PRODUK, katalog);
                } catch (IOException ignored) {}
            }
        }
    }

    private void mulaiTransaksiBaru() {
        Pelanggan p = new Pelanggan("CUST-001", txtNamaPelanggan != null ? txtNamaPelanggan.getText() : "Rafael Rizky", "081234567890", "REGULER");
        if (cmbTipeMember != null) {
            String sel = (String) cmbTipeMember.getSelectedItem();
            if (sel != null && sel.contains("VIP")) p.ubahTipeMember("VIP");
            else if (sel != null && sel.contains("GOLD")) p.ubahTipeMember("GOLD");
        }
        this.transaksiAktif = new Transaksi("TRX-" + System.currentTimeMillis() % 1000000, "10-11-2026 10:00", p);
        if (modelKeranjang != null) {
            modelKeranjang.setRowCount(0);
        }
        if (txtBayarTunai != null) {
            txtBayarTunai.setText("");
        }
        updateKalkulasiFinansial();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));

        // 1. TOP NAVBAR / HEADER (Midnight Slate Navy #0F172A)
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);
        headerWrap.setBorder(BorderFactory.createEmptyBorder(10, 14, 4, 14));

        RoundedPanel headerPanel = new RoundedPanel(16, COLOR_DARK);
        headerPanel.setLayout(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        // Brand Info (Left)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel lblTag = new JLabel(" TRUST LEDGER POS ");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(COLOR_WHITE);
        lblTag.setBackground(COLOR_PRIMARY);
        lblTag.setOpaque(true);
        lblTag.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JLabel lblTitle = new JLabel("Trust Ledger Fintech Dashboard");
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        lblTitle.setForeground(COLOR_WHITE);

        brandPanel.add(lblTag);
        brandPanel.add(lblTitle);

        // Student & Status Badge (Right)
        JPanel rightBadgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBadgePanel.setOpaque(false);

        JLabel lblAuthor = new JLabel("Rafael Rizky (NRP: 3125522007) • PENS PSDKU Sumenep");
        lblAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAuthor.setForeground(COLOR_ACCENT);

        JLabel lblLivePill = new JLabel(" ● SYSTEM ONLINE ");
        lblLivePill.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLivePill.setForeground(COLOR_WHITE);
        lblLivePill.setBackground(new Color(0x16, 0x65, 0x34)); // Dark Green pill
        lblLivePill.setOpaque(true);
        lblLivePill.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        rightBadgePanel.add(lblAuthor);
        rightBadgePanel.add(lblLivePill);

        headerPanel.add(brandPanel, BorderLayout.WEST);
        headerPanel.add(rightBadgePanel, BorderLayout.EAST);
        headerWrap.add(headerPanel, BorderLayout.CENTER);
        add(headerWrap, BorderLayout.NORTH);

        // 2. MAIN SPLIT PANE (Kiri: Katalog, Kanan: Kasir)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buatPanelKatalog(), buatPanelKeranjang());
        splitPane.setDividerLocation(630);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        splitPane.setDividerSize(10);
        add(splitPane, BorderLayout.CENTER);

        // 3. FOOTER STATUS BAR
        JPanel footerWrap = new JPanel(new BorderLayout());
        footerWrap.setOpaque(false);
        footerWrap.setBorder(BorderFactory.createEmptyBorder(0, 14, 10, 14));

        RoundedPanel footerPanel = new RoundedPanel(12, COLOR_WHITE, COLOR_BORDER, 1);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        JLabel lblStatus = new JLabel("Data Source: Persistensi aktif di file data/produk.txt & data/transaksi.txt");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(COLOR_TEXT_MUTED);

        JLabel lblCopyright = new JLabel("PBO 2026 • Clean Architecture & SOLID Implementation");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(COLOR_TEXT_MUTED);

        footerPanel.add(lblStatus, BorderLayout.WEST);
        footerPanel.add(lblCopyright, BorderLayout.EAST);
        footerWrap.add(footerPanel, BorderLayout.CENTER);
        add(footerWrap, BorderLayout.SOUTH);
    }

    private JPanel buatPanelKatalog() {
        RoundedPanel container = new RoundedPanel(18, COLOR_WHITE, COLOR_BORDER, 1);
        container.setLayout(new BorderLayout(0, 10));
        container.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Header Katalog & Filter Search
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel lblSection = new JLabel("Katalog Inventaris Produk");
        lblSection.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        lblSection.setForeground(COLOR_DARK);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);

        JLabel lblCari = new JLabel("Cari:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(COLOR_DARK);

        txtCari = new JTextField(12);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCari.setBorder(new RoundedBorder(10, COLOR_BORDER, 1));
        txtCari.setBackground(COLOR_BG);

        JButton btnCari = buatTombolAksen("Filter", COLOR_PRIMARY, COLOR_WHITE, 11);
        JButton btnReset = buatTombolAksen("Reset", COLOR_BG, COLOR_DARK, 11);

        btnCari.addActionListener(e -> filterKatalog(txtCari.getText()));
        btnReset.addActionListener(e -> { txtCari.setText(""); muatTabelKatalog(); });
        txtCari.addActionListener(e -> filterKatalog(txtCari.getText()));

        searchPanel.add(lblCari);
        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnReset);

        topPanel.add(lblSection, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        container.add(topPanel, BorderLayout.NORTH);

        // Table Katalog
        String[] cols = {"Kode", "Nama Produk", "Kategori", "Harga (Rp)", "Stok", "Info Khusus"};
        modelKatalog = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKatalog = new JTable(modelKatalog);
        styleTable(tabelKatalog);
        muatTabelKatalog();

        JScrollPane scrollKatalog = new JScrollPane(tabelKatalog);
        scrollKatalog.setBorder(new RoundedBorder(12, COLOR_BORDER, 1));
        scrollKatalog.getViewport().setBackground(COLOR_WHITE);
        container.add(scrollKatalog, BorderLayout.CENTER);

        // Button Toolbar Bawah Katalog
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JButton btnTambahKeranjang = buatTombolAksen("+ Tambah ke Keranjang", COLOR_PRIMARY, COLOR_WHITE, 12);
        JButton btnBaru = buatTombolAksen("+ Produk Baru", COLOR_ACCENT, COLOR_WHITE, 12);
        JButton btnEdit = buatTombolAksen("✎ Edit Produk", COLOR_DARK, COLOR_WHITE, 12);
        JButton btnHapus = buatTombolAksen("🗑 Hapus Produk", COLOR_DANGER, COLOR_WHITE, 12);
        JButton btnSimpanKatalog = buatTombolAksen("💾 Simpan ke File", COLOR_BG, COLOR_DARK, 12);

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
        container.add(btnPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel buatPanelKeranjang() {
        RoundedPanel container = new RoundedPanel(18, COLOR_WHITE, COLOR_BORDER, 1);
        container.setLayout(new BorderLayout(0, 10));
        container.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // 1. Info Pelanggan Card (Soft Rounded #EAF2FF)
        RoundedPanel infoPelangganPanel = new RoundedPanel(14, COLOR_BG, COLOR_BORDER, 1);
        infoPelangganPanel.setLayout(new GridLayout(2, 2, 8, 8));
        infoPelangganPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblCust = new JLabel("Nama Pelanggan:");
        lblCust.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblCust.setForeground(COLOR_DARK);
        infoPelangganPanel.add(lblCust);

        txtNamaPelanggan = new JTextField("Rafael Rizky");
        txtNamaPelanggan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNamaPelanggan.setBackground(COLOR_WHITE);
        txtNamaPelanggan.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));
        infoPelangganPanel.add(txtNamaPelanggan);

        JLabel lblMember = new JLabel("Tipe Membership:");
        lblMember.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblMember.setForeground(COLOR_DARK);
        infoPelangganPanel.add(lblMember);

        cmbTipeMember = new JComboBox<>(new String[]{"VIP (Diskon 15%)", "GOLD (Diskon 10%)", "REGULER (Diskon 0%)"});
        cmbTipeMember.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        cmbTipeMember.setBackground(COLOR_WHITE);
        cmbTipeMember.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));
        cmbTipeMember.addActionListener(e -> updateMemberDariCombo());
        infoPelangganPanel.add(cmbTipeMember);

        container.add(infoPelangganPanel, BorderLayout.NORTH);

        // 2. Table Keranjang Card
        String[] cols = {"Kode", "Nama Produk", "Qty", "Diskon Item", "Subtotal (Rp)"};
        modelKeranjang = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        styleTable(tabelKeranjang);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setOpaque(false);

        JScrollPane scrollKeranjang = new JScrollPane(tabelKeranjang);
        scrollKeranjang.setBorder(new RoundedBorder(12, COLOR_BORDER, 1));
        scrollKeranjang.getViewport().setBackground(COLOR_WHITE);
        centerPanel.add(scrollKeranjang, BorderLayout.CENTER);

        JButton btnHapusItem = buatTombolAksen("Batalkan / Hapus Baris Item Terpilih", COLOR_BG, COLOR_DANGER, 11);
        btnHapusItem.addActionListener(e -> aksiBatalkanItemKeranjang());
        centerPanel.add(btnHapusItem, BorderLayout.SOUTH);

        container.add(centerPanel, BorderLayout.CENTER);

        // 3. Checkout & Payment Summary Panel (Fintech Style Soft Rounded Card)
        RoundedPanel paymentPanel = new RoundedPanel(16, COLOR_BG, COLOR_BORDER, 1);
        paymentPanel.setLayout(new BorderLayout(0, 8));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel gridPay = new JPanel(new GridLayout(5, 2, 6, 6));
        gridPay.setOpaque(false);

        JLabel l1 = new JLabel("Total Pembelian (Gross):");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l1.setForeground(COLOR_TEXT_MUTED);
        gridPay.add(l1);

        lblTotalKotor = new JLabel("Rp 0");
        lblTotalKotor.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        lblTotalKotor.setForeground(COLOR_DARK);
        gridPay.add(lblTotalKotor);

        JLabel l2 = new JLabel("Potongan Diskon Member:");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l2.setForeground(COLOR_TEXT_MUTED);
        gridPay.add(l2);

        lblDiskonMember = new JLabel("Rp 0");
        lblDiskonMember.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        lblDiskonMember.setForeground(COLOR_PRIMARY);
        gridPay.add(lblDiskonMember);

        JLabel l3 = new JLabel("TOTAL AKHIR TAGIHAN:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l3.setForeground(COLOR_DARK);
        gridPay.add(l3);

        lblTotalTagihan = new JLabel("Rp 0");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalTagihan.setForeground(COLOR_PRIMARY);
        gridPay.add(lblTotalTagihan);

        JLabel l4 = new JLabel("Uang Tunai Diterima (Rp):");
        l4.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        l4.setForeground(COLOR_DARK);
        gridPay.add(l4);

        txtBayarTunai = new JTextField();
        txtBayarTunai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBayarTunai.setBackground(COLOR_WHITE);
        txtBayarTunai.setBorder(new RoundedBorder(8, COLOR_PRIMARY, 1));
        txtBayarTunai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungKembalianLive();
            }
        });
        gridPay.add(txtBayarTunai);

        JLabel l5 = new JLabel("Kembalian:");
        l5.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        l5.setForeground(COLOR_DARK);
        gridPay.add(l5);

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembalian.setForeground(COLOR_DARK);
        gridPay.add(lblKembalian);

        paymentPanel.add(gridPay, BorderLayout.CENTER);

        // Buttons Checkout
        JPanel payActionPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        payActionPanel.setOpaque(false);

        JButton btnProsesBayar = buatTombolAksen("💳 BAYAR & CETAK STRUK", COLOR_PRIMARY, COLOR_WHITE, 13);
        btnProsesBayar.addActionListener(e -> aksiProsesPembayaran());

        JButton btnResetTrx = buatTombolAksen("↺ Reset Transaksi", COLOR_WHITE, COLOR_DARK, 12);
        btnResetTrx.addActionListener(e -> mulaiTransaksiBaru());

        payActionPanel.add(btnProsesBayar);
        payActionPanel.add(btnResetTrx);

        paymentPanel.add(payActionPanel, BorderLayout.SOUTH);
        container.add(paymentPanel, BorderLayout.SOUTH);

        return container;
    }

    /**
     * Menerapkan style tabel kustom dengan Header TableCellRenderer eksplisit
     * sehingga judul kolom ("Kode", "Nama Produk", "Kategori", dll.) dijamin selalu
     * terlihat berkontras tinggi (Midnight Navy #0F172A dengan teks putih tebal).
     */
    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(COLOR_BORDER);
        table.setSelectionForeground(COLOR_DARK);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BG);
        table.setIntercellSpacing(new Dimension(1, 1));

        // 1. Custom Header Renderer (Menjamin warna Midnight Navy #0F172A dan teks putih tebal selalu nampak)
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                lbl.setOpaque(true);
                lbl.setBackground(COLOR_DARK);
                lbl.setForeground(COLOR_WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_PRIMARY),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                return lbl;
            }
        });

        // 2. Custom Cell Renderer (Alternating row stripes & highlighted Kode column)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? COLOR_WHITE : new Color(0xF8, 0xFB, 0xFF));
                    c.setForeground(COLOR_DARK);
                }
                if (col == 0) { // Kode Produk
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (!isSel) setForeground(COLOR_PRIMARY);
                } else if (col == 2) { // Kategori
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (!isSel && val != null) {
                        if (val.toString().equalsIgnoreCase("Makanan")) {
                            setForeground(new Color(0x16, 0x65, 0x34)); // Dark Green
                        } else {
                            setForeground(COLOR_PRIMARY); // Fintech Blue
                        }
                    }
                } else if (col == 3 || col == 4) { // Harga / Stok
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
    }

    private void muatTabelKatalog() {
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.getDaftarProdukReadOnly()) {
            String kat = (p instanceof ProdukMakanan) ? "Makanan" : "Elektronik";
            String extra = (p instanceof ProdukMakanan) ? "Exp: " + ((ProdukMakanan) p).getTanggalKadaluarsa() 
                           : "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " Bln";
            modelKatalog.addRow(new Object[]{
                p.getKode(), p.getNama(), kat, String.format("%,d", p.getHarga()), p.getStok(), extra
            });
        }
    }

    private void filterKatalog(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            muatTabelKatalog();
            return;
        }
        modelKatalog.setRowCount(0);
        for (Produk p : katalog.getDaftarProdukReadOnly()) {
            if (p.getKode().toLowerCase().contains(kw.toLowerCase()) || p.getNama().toLowerCase().contains(kw.toLowerCase())) {
                String kat = (p instanceof ProdukMakanan) ? "Makanan" : "Elektronik";
                String extra = (p instanceof ProdukMakanan) ? "Exp: " + ((ProdukMakanan) p).getTanggalKadaluarsa() 
                               : "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " Bln";
                modelKatalog.addRow(new Object[]{
                    p.getKode(), p.getNama(), kat, String.format("%,d", p.getHarga()), p.getStok(), extra
                });
            }
        }
    }

    private void aksiTambahKeKeranjang() {
        int row = tabelKatalog.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih produk dari tabel katalog terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
                item.getProduk().getNama(),
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
        if (sel != null && sel.contains("VIP")) member = "VIP";
        else if (sel != null && sel.contains("GOLD")) member = "GOLD";

        transaksiAktif.getPelanggan().ubahTipeMember(member);
        updateKalkulasiFinansial();
    }

    private void updateKalkulasiFinansial() {
        if (transaksiAktif == null) return;
        double subtotal = transaksiAktif.hitungTotalSubtotal();
        double diskon = transaksiAktif.hitungDiskonNominal();
        double totalAkhir = transaksiAktif.hitungTotalBayar();

        lblTotalKotor.setText(String.format("Rp %,.0f", subtotal));
        lblDiskonMember.setText(String.format("Rp %,.0f", diskon));
        lblTotalTagihan.setText(String.format("Rp %,.0f", totalAkhir));

        hitungKembalianLive();
    }

    private void hitungKembalianLive() {
        if (txtBayarTunai == null || lblKembalian == null || transaksiAktif == null) return;
        String str = txtBayarTunai.getText().trim();
        if (str.isEmpty()) {
            lblKembalian.setText("Rp 0");
            lblKembalian.setForeground(COLOR_DARK);
            return;
        }
        try {
            double bayar = Double.parseDouble(str);
            double tagihan = transaksiAktif.hitungTotalBayar();
            double kembalian = bayar - tagihan;
            if (kembalian >= 0) {
                lblKembalian.setText(String.format("Rp %,.0f", kembalian));
                lblKembalian.setForeground(COLOR_SUCCESS);
            } else {
                lblKembalian.setText(String.format("Kurang: Rp %,.0f", Math.abs(kembalian)));
                lblKembalian.setForeground(COLOR_DANGER);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Angka tidak valid");
            lblKembalian.setForeground(COLOR_DANGER);
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
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, transaksiAktif, bayar, kembalian);
                produkRepo.simpan(PATH_PRODUK, katalog);

                tampilkanDialogStruk(transaksiAktif, bayar, kembalian);
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
        dlg.setSize(490, 580);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel headerSlip = new JPanel(new BorderLayout());
        headerSlip.setBackground(COLOR_DARK);
        headerSlip.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel lblSlipTitle = new JLabel("RECEIPT • TRUST LEDGER POS");
        lblSlipTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSlipTitle.setForeground(COLOR_WHITE);
        headerSlip.add(lblSlipTitle, BorderLayout.WEST);
        dlg.add(headerSlip, BorderLayout.NORTH);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(COLOR_BG);
        ta.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("            STRUK PEMBELIAN KASIR RESMI            \n");
        sb.append("         TOKO RETAIL RAFAEL RIZKY (PBO 2026)        \n");
        sb.append("====================================================\n");
        sb.append(String.format("No. Nota  : %s\n", trx.getIdTransaksi()));
        sb.append(String.format("Tanggal   : %s\n", trx.getTanggal()));
        sb.append(String.format("Pelanggan : %s (%s)\n", trx.getPelanggan().getNama(), trx.getPelanggan().getTipeMember()));
        sb.append(String.format("Status    : %s\n", trx.getStatusTransaksi()));
        sb.append("----------------------------------------------------\n");
        sb.append("RINCIAN BARANG:\n");
        int no = 1;
        for (ItemTransaksi it : trx.getDaftarItem()) {
            sb.append(String.format("%d. %s - %s\n", no++, it.getProduk().getKode(), it.getProduk().getNama()));
            sb.append(String.format("   %d unit x Rp %,d = Rp %,d\n", it.getJumlahBeli(), it.getProduk().getHarga(), it.hitungSubtotal()));
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

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        pBtn.setBackground(COLOR_WHITE);
        pBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        JButton btnTutup = buatTombolAksen("Selesai / Cetak Struk", COLOR_PRIMARY, COLOR_WHITE, 12);
        btnTutup.addActionListener(e -> dlg.dispose());
        pBtn.add(btnTutup);
        dlg.add(pBtn, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void aksiBukaDialogEditProduk() {
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
        dlg.setSize(450, 380);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(COLOR_BG);
        dlg.setLayout(new BorderLayout());

        RoundedPanel formPanel = new RoundedPanel(16, COLOR_WHITE, COLOR_BORDER, 1);
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField txtKode = new JTextField(p.getKode());
        txtKode.setEditable(true);
        txtKode.setBackground(COLOR_WHITE);
        txtKode.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtKode.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtNama = new JTextField(p.getNama());
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNama.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtHarga = new JTextField(String.valueOf(p.getHarga()));
        txtHarga.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHarga.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtStok = new JTextField(String.valueOf(p.getStok()));
        txtStok.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtStok.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JLabel lblExtra;
        JTextField txtExtra;
        if (p instanceof ProdukMakanan) {
            lblExtra = new JLabel("Tanggal Exp (YYYY-MM-DD):");
            txtExtra = new JTextField(((ProdukMakanan) p).getTanggalKadaluarsa());
        } else if (p instanceof ProdukElektronik) {
            lblExtra = new JLabel("Masa Garansi (Bulan):");
            txtExtra = new JTextField(String.valueOf(((ProdukElektronik) p).getGaransiBulan()));
        } else {
            lblExtra = new JLabel("Info Khusus:");
            txtExtra = new JTextField("-");
            txtExtra.setEditable(false);
        }
        txtExtra.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtExtra.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JLabel lKode = new JLabel("Kode Produk (Bisa Diedit):"); lKode.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lNama = new JLabel("Nama Produk:");            lNama.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lHarga = new JLabel("Harga Satuan (Rp):");      lHarga.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lStok = new JLabel("Jumlah Stok:");             lStok.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        lblExtra.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));

        formPanel.add(lKode); formPanel.add(txtKode);
        formPanel.add(lNama); formPanel.add(txtNama);
        formPanel.add(lHarga); formPanel.add(txtHarga);
        formPanel.add(lStok); formPanel.add(txtStok);
        formPanel.add(lblExtra); formPanel.add(txtExtra);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBtn.setOpaque(false);

        JButton btnBatal = buatTombolAksen("Batal", COLOR_BG, COLOR_DARK, 12);
        btnBatal.addActionListener(e -> dlg.dispose());

        JButton btnSimpan = buatTombolAksen("✔ Simpan Perubahan", COLOR_PRIMARY, COLOR_WHITE, 12);
        btnSimpan.addActionListener(e -> {
            try {
                String kodeLama = kode;
                String kodeBaru = txtKode.getText().trim();
                String namaBaru = txtNama.getText().trim();
                int hargaBaru = Integer.parseInt(txtHarga.getText().trim());
                int stokBaru = Integer.parseInt(txtStok.getText().trim());
                String extraBaru = txtExtra.getText().trim();

                boolean sukses = katalog.ubahProduk(kodeLama, kodeBaru, namaBaru, hargaBaru, stokBaru, extraBaru);
                if (sukses) {
                    produkRepo.simpan(PATH_PRODUK, katalog);
                    muatTabelKatalog();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, 
                        "Data produk [" + kodeBaru + "] berhasil diperbarui dan disimpan ke berkas produk.txt!", 
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengubah produk: Produk tidak ditemukan di memori.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dlg, "Harga, stok, dan durasi garansi harus berupa bilangan angka valid!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Kesalahan Validasi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pBtn.add(btnBatal);
        pBtn.add(btnSimpan);

        dlg.add(formPanel, BorderLayout.CENTER);
        dlg.add(pBtn, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void aksiBukaDialogProdukBaru() {
        JDialog dlg = new JDialog(this, "Pendaftaran Produk Baru", true);
        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(COLOR_BG);
        dlg.setLayout(new BorderLayout());

        RoundedPanel formPanel = new RoundedPanel(16, COLOR_WHITE, COLOR_BORDER, 1);
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Makanan", "Elektronik"});
        cmbJenis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbJenis.setBackground(COLOR_WHITE);
        cmbJenis.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtKode = new JTextField();
        txtKode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtKode.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNama.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtHarga = new JTextField();
        txtHarga.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHarga.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JTextField txtStok = new JTextField();
        txtStok.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtStok.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        JLabel lblExtra = new JLabel("Tanggal Exp (YYYY-MM-DD):");
        lblExtra.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JTextField txtExtra = new JTextField("2026-12-31");
        txtExtra.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtExtra.setBorder(new RoundedBorder(8, COLOR_BORDER, 1));

        cmbJenis.addActionListener(e -> {
            if (cmbJenis.getSelectedIndex() == 0) {
                lblExtra.setText("Tanggal Exp (YYYY-MM-DD):");
                txtExtra.setText("2026-12-31");
            } else {
                lblExtra.setText("Masa Garansi (Bulan):");
                txtExtra.setText("12");
            }
        });

        JLabel lKat = new JLabel("Kategori Produk:"); lKat.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lKode = new JLabel("Kode Produk:");    lKode.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lNama = new JLabel("Nama Produk:");    lNama.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lHarga = new JLabel("Harga Satuan (Rp):"); lHarga.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        JLabel lStok = new JLabel("Stok Awal:");      lStok.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));

        formPanel.add(lKat); formPanel.add(cmbJenis);
        formPanel.add(lKode); formPanel.add(txtKode);
        formPanel.add(lNama); formPanel.add(txtNama);
        formPanel.add(lHarga); formPanel.add(txtHarga);
        formPanel.add(lStok); formPanel.add(txtStok);
        formPanel.add(lblExtra); formPanel.add(txtExtra);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBtn.setOpaque(false);

        JButton btnBatal = buatTombolAksen("Batal", COLOR_BG, COLOR_DARK, 12);
        btnBatal.addActionListener(e -> dlg.dispose());

        JButton btnSimpan = buatTombolAksen("✔ Simpan Produk", COLOR_PRIMARY, COLOR_WHITE, 12);
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
                produkRepo.simpan(PATH_PRODUK, katalog);
                muatTabelKatalog();
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "Produk baru berhasil didaftarkan ke katalog dan berkas disk!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        pBtn.add(btnBatal);
        pBtn.add(btnSimpan);

        dlg.add(formPanel, BorderLayout.CENTER);
        dlg.add(pBtn, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void aksiHapusProdukKatalog() {
        int row = tabelKatalog.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = (String) modelKatalog.getValueAt(row, 0);
        int konf = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk berkode '" + kode + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konf == JOptionPane.YES_OPTION) {
            if (katalog.hapusProduk(kode)) {
                try {
                    produkRepo.simpan(PATH_PRODUK, katalog);
                } catch (IOException ignored) {}
                muatTabelKatalog();
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void aksiSimpanKatalogFile() {
        try {
            produkRepo.simpan(PATH_PRODUK, katalog);
            JOptionPane.showMessageDialog(this, "Seluruh katalog berhasil disinkronkan ke berkas " + PATH_PRODUK, "Sinkronisasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan berkas: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Membuat tombol aksi dengan sudut melengkung halus (Rounded Button radius 14px),
     * rendering anti-aliasing berkualitas tinggi, serta teks yang sangat kontras.
     */
    public static JButton buatTombolAksen(String text, Color bgColor, Color fgColor, int fontSize) {
        return new RoundedButton(text, bgColor, fgColor, 14, fontSize);
    }

    // ========================================================================
    // CUSTOM ROUNDED COMPONENTS FOR SOFT FINTECH UI/UX
    // ========================================================================

    /**
     * RoundedPanel - Panel kustom dengan sudut melengkung halus (Anti-Aliased Fill & Stroke)
     */
    public static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color bgColor;
        private final Color borderColor;
        private final int borderWidth;

        public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderWidth) {
            super();
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            setOpaque(false);
        }

        public RoundedPanel(int radius, Color bgColor) {
            this(radius, bgColor, null, 0);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (bgColor != null) {
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
            }

            if (borderColor != null && borderWidth > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderWidth));
                g2.drawRoundRect(borderWidth / 2, borderWidth / 2, w - borderWidth - 1, h - borderWidth - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * RoundedButton - Tombol kustom dengan radius melengkung dan hover animation
     */
    public static class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color normalBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color borderColor;

        public RoundedButton(String text, Color bg, Color fg, int radius, int fontSize) {
            super(text);
            this.radius = radius;
            this.normalBg = bg;
            if (bg.equals(COLOR_PRIMARY)) {
                this.hoverBg = new Color(0x1D, 0x4E, 0xD8);
            } else if (bg.equals(COLOR_WHITE) || bg.equals(COLOR_BG)) {
                this.hoverBg = COLOR_BORDER;
            } else {
                this.hoverBg = bg.brighter();
            }
            this.pressedBg = bg.darker();
            this.borderColor = (bg.equals(COLOR_BG) || bg.equals(COLOR_WHITE)) ? COLOR_BORDER : bg.darker();

            setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            setForeground(fg);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color paintBg = normalBg;
            ButtonModel model = getModel();
            if (model.isPressed()) {
                paintBg = pressedBg;
            } else if (model.isRollover()) {
                paintBg = hoverBg;
            }

            int w = getWidth();
            int h = getHeight();

            g2.setColor(paintBg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * RoundedBorder - Border kustom untuk JTextField dan JScrollPane agar berpenampilan lembut
     */
    public static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        private final int thickness;

        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 3, thickness + 6, thickness + 3, thickness + 6);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2, width - thickness - 1, height - thickness - 1, radius, radius);
            g2.dispose();
        }
    }
}
