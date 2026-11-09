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
import java.util.List;

/**
 * KasirDesktopFrame - Modern Swing Graphical User Interface (GUI)
 * Sistem Kasir Sederhana (Point of Sale) Versi Desktop
 * 
 * Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus    : PENS PSDKU Sumenep (2026)
 */
public class KasirDesktopFrame extends JFrame {
    private static final long serialVersionUID = 1L;

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
        super("Sistem Kasir Sederhana (POS Desktop) - Rafael Rizky (3125522007)");
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
            // Isi default awal jika berkas kosong
            if (katalog.isKosong()) {
                katalog.tambahProduk(new ProdukMakanan("MK01", "Roti Bakar Keju", 15000, 25, "2026-12-31"));
                katalog.tambahProduk(new ProdukMakanan("MK02", "Susu UHT Coklat 250ml", 8500, 40, "2026-10-15"));
                katalog.tambahProduk(new ProdukElektronik("EL01", "Mouse Wireless Silent", 125000, 15, 12));
                katalog.tambahProduk(new ProdukElektronik("EL02", "Keyboard Mechanical RGB", 350000, 8, 24));
                try {
                    produkRepo.simpan(PATH_PRODUK, katalog);
                } catch (IOException ignored) {}
            }
        }
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

        String idTrx = "TRX-" + (System.currentTimeMillis() % 1000000);
        Pelanggan cust = new Pelanggan("CUST-" + idTrx, nama, "081234567890", member);
        this.transaksiAktif = new Transaksi(idTrx, "09-11-2026 14:00", cust);

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
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(27, 54, 93));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("SISTEM KASIR SEDERHANA (POINT OF SALE) - VERSI DESKTOP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("PBO 2026 | Rafael Rizky Luga Tanjung (NRP: 3125522007) | PENS PSDKU Sumenep");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(200, 220, 255));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblSub, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. MAIN SPLIT PANEL (Kiri: Katalog, Kanan: Keranjang)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buatPanelKatalog(), buatPanelKeranjang());
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // 3. FOOTER STATUS BAR
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        footerPanel.setBackground(new Color(240, 243, 246));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 210)));

        JLabel lblStatus = new JLabel("Status Sistem: Terhubung ke file data/produk.txt | Siap melayani transaksi.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(80, 90, 100));
        footerPanel.add(lblStatus);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel buatPanelKatalog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        // Header Katalog & Filter Search
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblSection = new JLabel("Katalog Inventaris Produk");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection.setForeground(new Color(27, 54, 93));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.add(new JLabel("Cari (Kode/Nama):"));
        txtCari = new JTextField(15);
        JButton btnCari = new JButton("Filter");
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

        // Table Katalog
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

        // Button Toolbar Bawah Katalog
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

        // Info Pelanggan
        JPanel infoPelangganPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        infoPelangganPanel.setBorder(BorderFactory.createTitledBorder("Informasi Konsumen & Membership"));

        infoPelangganPanel.add(new JLabel("Nama Pelanggan:"));
        txtNamaPelanggan = new JTextField("Rafael Rizky");
        infoPelangganPanel.add(txtNamaPelanggan);

        infoPelangganPanel.add(new JLabel("Tipe Membership:"));
        cmbTipeMember = new JComboBox<>(new String[]{"VIP (Diskon 15%)", "GOLD (Diskon 10%)", "REGULER (Diskon 0%)"});
        cmbTipeMember.addActionListener(e -> updateMemberDariCombo());
        infoPelangganPanel.add(cmbTipeMember);

        panel.add(infoPelangganPanel, BorderLayout.NORTH);

        // Table Keranjang
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

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        JButton btnHapusItem = new JButton("Batalkan / Hapus Baris Item Terpilih");
        btnHapusItem.addActionListener(e -> aksiBatalkanItemKeranjang());
        centerPanel.add(btnHapusItem, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Checkout & Payment Panel
        JPanel paymentPanel = new JPanel(new GridLayout(6, 2, 6, 6));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Rincian Pembayaran Kasir"));

        paymentPanel.add(new JLabel("Total Pembelian (Gross):"));
        lblTotalKotor = new JLabel("Rp 0");
        lblTotalKotor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentPanel.add(lblTotalKotor);

        paymentPanel.add(new JLabel("Potongan Diskon Member:"));
        lblDiskonMember = new JLabel("Rp 0");
        lblDiskonMember.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDiskonMember.setForeground(new Color(220, 53, 69));
        paymentPanel.add(lblDiskonMember);

        paymentPanel.add(new JLabel("TOTAL AKHIR TAGIHAN:"));
        lblTotalTagihan = new JLabel("Rp 0");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalTagihan.setForeground(new Color(40, 167, 69));
        paymentPanel.add(lblTotalTagihan);

        paymentPanel.add(new JLabel("Uang Tunai Diterima (Rp):"));
        txtBayarTunai = new JTextField();
        txtBayarTunai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBayarTunai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungKembalianLive();
            }
        });
        paymentPanel.add(txtBayarTunai);

        paymentPanel.add(new JLabel("Kembalian:"));
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentPanel.add(lblKembalian);

        JButton btnProsesBayar = new JButton("BAYAR & CETAK STRUK");
        btnProsesBayar.setBackground(new Color(27, 54, 93));
        btnProsesBayar.setForeground(Color.WHITE);
        btnProsesBayar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProsesBayar.addActionListener(e -> aksiProsesPembayaran());

        JButton btnResetTrx = new JButton("Reset Transaksi");
        btnResetTrx.addActionListener(e -> mulaiTransaksiBaru());

        paymentPanel.add(btnProsesBayar);
        paymentPanel.add(btnResetTrx);

        panel.add(paymentPanel, BorderLayout.SOUTH);

        return panel;
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
            // Tambahkan ke transaksi
            transaksiAktif.tambahItem(prod, qty);

            // Perbarui tabel keranjang
            muatTabelKeranjang();
            // Perbarui sisa stok di katalog tabel
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
        if (sel.contains("VIP")) member = "VIP";
        else if (sel.contains("GOLD")) member = "GOLD";

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
                // Catat ke berkas transaksi.txt
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, transaksiAktif, bayar, kembalian);
                // Simpan pengurangan stok ke berkas produk.txt
                produkRepo.simpan(PATH_PRODUK, katalog);

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
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(new Color(252, 252, 248));

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

        JButton btnTutup = new JButton("Selesai / Tutup");
        btnTutup.addActionListener(e -> dlg.dispose());
        dlg.add(btnTutup, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    /**
     * Dialog interaktif untuk mengubah / mengedit data produk yang dipilih di tabel katalog.
     */
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
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtKode = new JTextField(p.getKode());
        txtKode.setEditable(false);
        txtKode.setBackground(new Color(240, 240, 240));

        JTextField txtNama = new JTextField(p.getNama());
        JTextField txtHarga = new JTextField(String.valueOf(p.getHarga()));
        JTextField txtStok = new JTextField(String.valueOf(p.getStok()));

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

        dlg.add(new JLabel("Kode Produk (Tetap):")); dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:")); dlg.add(txtNama);
        dlg.add(new JLabel("Harga Satuan (Rp):")); dlg.add(txtHarga);
        dlg.add(new JLabel("Jumlah Stok:")); dlg.add(txtStok);
        dlg.add(lblExtra); dlg.add(txtExtra);

        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dlg.dispose());

        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(23, 162, 184));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.addActionListener(e -> {
            try {
                String namaBaru = txtNama.getText().trim();
                int hargaBaru = Integer.parseInt(txtHarga.getText().trim());
                int stokBaru = Integer.parseInt(txtStok.getText().trim());
                String extraBaru = txtExtra.getText().trim();

                boolean sukses = katalog.ubahProduk(kode, namaBaru, hargaBaru, stokBaru, extraBaru);
                if (sukses) {
                    produkRepo.simpan(PATH_PRODUK, katalog);
                    muatTabelKatalog();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, 
                        "Data produk [" + kode + "] berhasil diperbarui dan disimpan ke berkas produk.txt!", 
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

        dlg.add(btnBatal);
        dlg.add(btnSimpan);
        dlg.setVisible(true);
    }

    private void aksiBukaDialogProdukBaru() {
        JDialog dlg = new JDialog(this, "Pendaftaran Produk Baru", true);
        dlg.setSize(400, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Makanan", "Elektronik"});
        JTextField txtKode = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtHarga = new JTextField();
        JTextField txtStok = new JTextField();
        JLabel lblExtra = new JLabel("Tanggal Exp (YYYY-MM-DD):");
        JTextField txtExtra = new JTextField("2026-12-31");

        cmbJenis.addActionListener(e -> {
            if (cmbJenis.getSelectedIndex() == 0) {
                lblExtra.setText("Tanggal Exp (YYYY-MM-DD):");
                txtExtra.setText("2026-12-31");
            } else {
                lblExtra.setText("Masa Garansi (Bulan):");
                txtExtra.setText("12");
            }
        });

        dlg.add(new JLabel("Kategori Produk:")); dlg.add(cmbJenis);
        dlg.add(new JLabel("Kode Produk:")); dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:")); dlg.add(txtNama);
        dlg.add(new JLabel("Harga Satuan (Rp):")); dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Awal:")); dlg.add(txtStok);
        dlg.add(lblExtra); dlg.add(txtExtra);

        JButton btnSimpan = new JButton("Simpan Produk");
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
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

        dlg.add(btnSimpan);
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
}
