package test;

import model.*;
import model.exception.*;
import view.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * Automasi Pengambilan Screenshot UI Sistem POS Versi Lengkap Terbaru
 * Menyimpan seluruh 14 tahap alur kerja nyata ke direktori:
 * - D:\Rizky\PBO\Laporan-Matkul-Praktikum-OOP\P15 POS System Desktop Version 3125522007_Rafael_Rizky\Screenshot_Terbaru
 * - D:\Rizky\PBO\P15 POS System Desktop Version 3125522007_Rafael_Rizky\Screenshot_Terbaru
 */
public class GenerateScreenshotsTerbaru {

    private static final String OUT_DIR_LAPORAN = "D:/Rizky/PBO/Laporan-Matkul-Praktikum-OOP/P15 POS System Desktop Version 3125522007_Rafael_Rizky/Screenshot_Terbaru";
    private static final String OUT_DIR_LOCAL = "D:/Rizky/PBO/P15 POS System Desktop Version 3125522007_Rafael_Rizky/Screenshot_Terbaru";

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new File(OUT_DIR_LAPORAN).mkdirs();
        new File(OUT_DIR_LOCAL).mkdirs();

        SwingUtilities.invokeAndWait(() -> {
            try {
                runCaptures();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(">>> SEMUA 14 SCREENSHOT BERHASIL DIAMBIL & DISIMPAN!");
        System.exit(0);
    }

    private static void runCaptures() throws Exception {
        User adminUser = new User("rafael", "saltAdmin123", "hashAdmin123", Role.ADMIN);
        AuthService authService = new AuthService(Paths.get("data", "users.txt"));

        // -------------------------------------------------------------
        // Inisialisasi Frame Utama KasirDesktopFrame
        // -------------------------------------------------------------
        KasirDesktopFrame frame = new KasirDesktopFrame(adminUser);
        frame.setSize(1150, 720);
        frame.setLocationRelativeTo(null);
        frame.addNotify();
        frame.validate();
        frame.getContentPane().setSize(1150, 720);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();

        // -------------------------------------------------------------
        // STEP 1: Dialog Login Otentikasi Multi-Role
        // -------------------------------------------------------------
        JDialog dlgLogin = buatDialogMockLogin(frame);
        saveCompositeScreenshot(frame, dlgLogin, "01_login_dialog.png");
        dlgLogin.dispose();

        // -------------------------------------------------------------
        // STEP 2: Tampilan Utama Katalog & Header Identitas Toko
        // -------------------------------------------------------------
        saveScreenshot(frame, "02_tampilan_utama_katalog.png", "Tampilan Utama Katalog POS: Header Barokah Sumenep & Kategori Scrollable");

        // Refleksi field-field utama
        Field fKatalog = KasirDesktopFrame.class.getDeclaredField("katalog");
        fKatalog.setAccessible(true);
        KatalogProduk katalog = (KatalogProduk) fKatalog.get(frame);

        Field fTxtCari = KasirDesktopFrame.class.getDeclaredField("txtCari");
        fTxtCari.setAccessible(true);
        JTextField txtCari = (JTextField) fTxtCari.get(frame);

        Field fTrxAktif = KasirDesktopFrame.class.getDeclaredField("transaksiAktif");
        fTrxAktif.setAccessible(true);
        Transaksi trx = (Transaksi) fTrxAktif.get(frame);

        Field fTxtNamaPelanggan = KasirDesktopFrame.class.getDeclaredField("txtNamaPelanggan");
        fTxtNamaPelanggan.setAccessible(true);
        JTextField txtNamaPelanggan = (JTextField) fTxtNamaPelanggan.get(frame);

        Field fCmbTipeMember = KasirDesktopFrame.class.getDeclaredField("cmbTipeMember");
        fCmbTipeMember.setAccessible(true);
        @SuppressWarnings("unchecked")
        JComboBox<String> cmbTipeMember = (JComboBox<String>) fCmbTipeMember.get(frame);

        Field fCmbMetodeBayar = KasirDesktopFrame.class.getDeclaredField("cmbMetodeBayar");
        fCmbMetodeBayar.setAccessible(true);
        @SuppressWarnings("unchecked")
        JComboBox<String> cmbMetodeBayar = (JComboBox<String>) fCmbMetodeBayar.get(frame);

        Field fTxtBayarTunai = KasirDesktopFrame.class.getDeclaredField("txtBayarTunai");
        fTxtBayarTunai.setAccessible(true);
        JTextField txtBayarTunai = (JTextField) fTxtBayarTunai.get(frame);

        Method mFilterKatalog = KasirDesktopFrame.class.getDeclaredMethod("filterKatalog", String.class);
        mFilterKatalog.setAccessible(true);

        Method mMuatKatalog = KasirDesktopFrame.class.getDeclaredMethod("muatTabelKatalog");
        mMuatKatalog.setAccessible(true);

        Method mMuatKeranjang = KasirDesktopFrame.class.getDeclaredMethod("muatTabelKeranjang");
        mMuatKeranjang.setAccessible(true);

        Method mUpdateFinansial = KasirDesktopFrame.class.getDeclaredMethod("updateKalkulasiFinansial");
        mUpdateFinansial.setAccessible(true);

        // -------------------------------------------------------------
        // STEP 3: Filter Kategori & Pencarian Cepat Nama Produk ("Donat")
        // -------------------------------------------------------------
        txtCari.setText("Donat");
        mFilterKatalog.invoke(frame, "Donat");
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "03_filter_kategori_dan_cari.png", "Pencarian Produk Berbasis Keyword: 'Donat'");

        // Reset filter
        txtCari.setText("");
        mMuatKatalog.invoke(frame);

        // -------------------------------------------------------------
        // STEP 4: Dialog Registrasi Produk Baru dengan Kalender Picker
        // -------------------------------------------------------------
        JDialog dlgTambah = buatDialogMockTambahProduk(frame);
        saveCompositeScreenshot(frame, dlgTambah, "04_dialog_tambah_produk_kalender.png");
        dlgTambah.dispose();

        // -------------------------------------------------------------
        // STEP 5: Dialog Edit Komoditas Produk (MK01 Roti Tawar)
        // -------------------------------------------------------------
        Produk pEdit = katalog.cariProduk("MK01");
        JDialog dlgEdit = buatDialogMockEditProduk(frame, pEdit);
        saveCompositeScreenshot(frame, dlgEdit, "05_dialog_edit_produk.png");
        dlgEdit.dispose();

        // -------------------------------------------------------------
        // STEP 6: Transaksi Belanja: Masuk Keranjang, Subtotal, & Edit Qty
        // -------------------------------------------------------------
        Produk p1 = katalog.cariProduk("MK01"); // Roti Tawar 15.000 (-10% -> 13.500)
        Produk p2 = katalog.cariProduk("MN02"); // Ultra Milk 7.000 (-15% -> 5.950)
        Produk p3 = katalog.cariProduk("KP01"); // Bimoli 2L 38.500 (-5% -> 36.575)
        if (p1 != null) trx.tambahItem(p1, 2);
        if (p2 != null) trx.tambahItem(p2, 3);
        if (p3 != null) trx.tambahItem(p3, 1);

        mMuatKeranjang.invoke(frame);
        mMuatKatalog.invoke(frame);
        mUpdateFinansial.invoke(frame);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "06_keranjang_belanja_edit_qty.png", "Item Masuk Keranjang Belanja dengan Fitur Ubah Qty");

        // -------------------------------------------------------------
        // STEP 7: Penerapan Keanggotaan Member VIP (Diskon Bertingkat 15%)
        // -------------------------------------------------------------
        txtNamaPelanggan.setText("Ibu Hj. Fatimah (VIP)");
        trx.getPelanggan().ubahTipeMember("VIP");
        cmbTipeMember.setSelectedItem("VIP (15%)");
        mUpdateFinansial.invoke(frame);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "07_membership_diskon_bertingkat.png", "Kalkulasi Diskon Bertingkat Keanggotaan VIP (15%)");

        // -------------------------------------------------------------
        // STEP 8: Strategi Pembayaran Modern QRIS Dinamis (Auto-Locked)
        // -------------------------------------------------------------
        cmbMetodeBayar.setSelectedItem("QRIS (Simulasi Pas)");
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "08_metode_pembayaran_qris_split.png", "Metode Pembayaran QRIS Dinamis Terkunci Otomatis ke Total Tagihan");

        // -------------------------------------------------------------
        // STEP 9: Penanganan Eksepsi Validasi Transaksi (PembayaranKurangException)
        // -------------------------------------------------------------
        cmbMetodeBayar.setSelectedItem("TUNAI");
        txtBayarTunai.setText("50000"); // Tagihan ~Rp 69.206, bayar 50.000
        mUpdateFinansial.invoke(frame);
        JDialog dlgError = buatDialogMockError(frame,
                "Pembayaran tunai transaksi 'TRX-BAROKAH-01' tidak mencukupi!\n" +
                "Total tagihan : Rp 69.206\n" +
                "Tunai diserahkan: Rp 50.000 (Kekurangan: Rp 19.206)\n\n" +
                "Aturan Bisnis: PembayaranKurangException tertangani dengan aman,\n" +
                "status transaksi tetap PENDING dan stok belanjaan terlindungi.");
        saveCompositeScreenshot(frame, dlgError, "09_exception_handling_validasi.png");
        dlgError.dispose();

        // -------------------------------------------------------------
        // STEP 10: Struk Penjualan Kasir Resmi Sukses
        // -------------------------------------------------------------
        txtBayarTunai.setText("100000");
        mUpdateFinansial.invoke(frame);
        JDialog dlgStruk = buatDialogMockStruk(frame, trx, 100000, 30794);
        saveCompositeScreenshot(frame, dlgStruk, "10_struk_pembayaran_sukses.png");
        dlgStruk.dispose();

        // -------------------------------------------------------------
        // STEP 11: Laporan Penjualan Harian & Tutup Kasir (16-11-2026)
        // -------------------------------------------------------------
        JDialog dlgLaporan = buatDialogMockLaporanHarian(frame, "16-11-2026", 0);
        saveCompositeScreenshot(frame, dlgLaporan, "11_laporan_penjualan_harian.png");
        dlgLaporan.dispose();

        // -------------------------------------------------------------
        // STEP 12: Produk Terlaris Bulanan (Akumulasi November 2026)
        // -------------------------------------------------------------
        JDialog dlgTopBulan = buatDialogMockLaporanHarian(frame, "16-11-2026", 1);
        saveCompositeScreenshot(frame, dlgTopBulan, "12_produk_terlaris_bulanan.png");
        dlgTopBulan.dispose();

        // -------------------------------------------------------------
        // STEP 13: Riwayat Transaksi & Pembatalan Nota (VOID Lunas)
        // -------------------------------------------------------------
        JDialog dlgVoid = buatDialogMockRiwayatVoid(frame);
        saveCompositeScreenshot(frame, dlgVoid, "13_riwayat_transaksi_dan_void.png");
        dlgVoid.dispose();

        // -------------------------------------------------------------
        // STEP 14: Pengaturan Sistem & Multi-Profile Template
        // -------------------------------------------------------------
        PengaturanSistem config = new PengaturanSistem();
        config.muatDariFile();
        DialogPengaturan dlgPengaturan = new DialogPengaturan(frame, config, adminUser);
        dlgPengaturan.setSize(620, 720);
        dlgPengaturan.doLayout();
        dlgPengaturan.validate();
        saveCompositeScreenshot(frame, dlgPengaturan, "14_pengaturan_sistem_multi_profile.png");
        dlgPengaturan.dispose();
    }

    // --- HELPER DIALOG MOCKING UNTUK SCREENSHOT MAKSIMAL ---

    private static JDialog buatDialogMockLogin(JFrame parent) {
        JDialog dlg = new JDialog(parent, "🔐 Login Sistem Kasir POS", true);
        dlg.setSize(440, 360);
        dlg.setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlHeader.setBackground(new Color(29, 29, 31));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("SISTEM KASIR POINT OF SALE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Otentikasi Petugas & Manajemen Pengguna Multi-Role");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(142, 142, 147));
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        dlg.add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        pnlBody.setBackground(new Color(245, 245, 247));

        JLabel lblUser = new JLabel("Username Petugas Kasir / Admin:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtU = new JTextField("rafael");
        txtU.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblPass = new JLabel("Kata Sandi (Password):");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPasswordField txtP = new JPasswordField("••••••••");
        txtP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        pnlBody.add(lblUser);
        pnlBody.add(Box.createVerticalStrut(5));
        pnlBody.add(txtU);
        pnlBody.add(Box.createVerticalStrut(10));
        pnlBody.add(lblPass);
        pnlBody.add(Box.createVerticalStrut(5));
        pnlBody.add(txtP);
        pnlBody.add(Box.createVerticalStrut(15));

        JPanel pnlRole = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlRole.setOpaque(false);
        JLabel lblRoleDesc = new JLabel("Role: ADMIN (Akses Penuh: Katalog, VOID, Laporan, Pengaturan)");
        lblRoleDesc.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRoleDesc.setForeground(new Color(0, 122, 255));
        pnlRole.add(lblRoleDesc);
        pnlBody.add(pnlRole);
        dlg.add(pnlBody, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBtn.setBackground(new Color(245, 245, 247));
        JButton btnRegister = new JButton("+ Tambah User Baru");
        JButton btnLogin = new JButton("Masuk Sistem");
        btnLogin.setBackground(new Color(0, 122, 255));
        btnLogin.setForeground(Color.WHITE);
        pnlBtn.add(btnRegister);
        pnlBtn.add(btnLogin);
        dlg.add(pnlBtn, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockTambahProduk(JFrame parent) {
        JDialog dlg = new JDialog(parent, "Registrasi Komoditas Produk Baru", true);
        dlg.setSize(460, 480);
        dlg.setLayout(new GridLayout(9, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Makanan & Minuman", "Elektronik & Gadget"});
        JComboBox<String> cmbKategori = new JComboBox<>(new String[]{"Makanan", "Minuman", "Snack & Biskuit", "Kebutuhan Pokok"});
        JTextField txtKode = new JTextField("MK07");
        JTextField txtNama = new JTextField("Roti Gandum Premium Barokah");
        JTextField txtHarga = new JTextField("16500");
        JTextField txtStok = new JTextField("30");
        JTextField txtDiskon = new JTextField("10");

        KalenderDatePicker picker = new KalenderDatePicker(dlg, "yyyy-MM-dd", LocalDate.of(2027, 1, 15));

        dlg.add(new JLabel("Jenis Komoditas:"));
        dlg.add(cmbJenis);
        dlg.add(new JLabel("Kategori Produk:"));
        dlg.add(cmbKategori);
        dlg.add(new JLabel("Kode Produk (Unik):"));
        dlg.add(txtKode);
        dlg.add(new JLabel("Nama Komoditas:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga Satuan (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Stok Awal (Unit):"));
        dlg.add(txtStok);
        dlg.add(new JLabel("Diskon Promo Toko (%):"));
        dlg.add(txtDiskon);
        dlg.add(new JLabel("Tgl Kadaluarsa (Kalender):"));
        dlg.add(picker);

        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnSimpan = new JButton("Simpan Komoditas");
        btnSimpan.setBackground(new Color(0, 122, 255));
        btnSimpan.setForeground(Color.WHITE);
        pnlTombol.add(btnSimpan);
        dlg.add(new JLabel());
        dlg.add(pnlTombol);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockEditProduk(JFrame parent, Produk p) {
        JDialog dlg = new JDialog(parent, "Edit Komoditas: " + (p != null ? p.getKode() : "MK01"), true);
        dlg.setSize(440, 360);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNama = new JTextField(p != null ? p.getNama() : "Roti Tawar Sari Roti");
        JTextField txtHarga = new JTextField(p != null ? String.valueOf(p.getHarga()) : "15000");
        JTextField txtStok = new JTextField(p != null ? String.valueOf(p.getStok()) : "25");
        JTextField txtDiskon = new JTextField(p != null ? String.valueOf((int)(p.getDiskonPromo() * 100)) : "10");
        JTextField txtExtra = new JTextField("2026-12-31");

        dlg.add(new JLabel("Nama Komoditas:"));
        dlg.add(txtNama);
        dlg.add(new JLabel("Harga Satuan (Rp):"));
        dlg.add(txtHarga);
        dlg.add(new JLabel("Jumlah Stok:"));
        dlg.add(txtStok);
        dlg.add(new JLabel("Diskon Promo Toko (%):"));
        dlg.add(txtDiskon);
        dlg.add(new JLabel("Tgl Kadaluarsa:"));
        dlg.add(txtExtra);

        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnUpdate = new JButton("Perbarui Data");
        btnUpdate.setBackground(new Color(0, 122, 255));
        btnUpdate.setForeground(Color.WHITE);
        pnlTombol.add(btnUpdate);
        dlg.add(new JLabel());
        dlg.add(pnlTombol);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockError(JFrame parent, String message) {
        JDialog dlg = new JDialog(parent, "Peringatan Aturan Bisnis & Validasi", true);
        dlg.setSize(480, 240);
        dlg.setLayout(new BorderLayout(15, 15));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblIcon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        JTextArea txt = new JTextArea(message);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);

        JPanel pnlCenter = new JPanel(new BorderLayout(15, 0));
        pnlCenter.add(lblIcon, BorderLayout.WEST);
        pnlCenter.add(txt, BorderLayout.CENTER);
        dlg.add(pnlCenter, BorderLayout.CENTER);

        JButton btnOk = new JButton("Mengerti & Perbaiki");
        btnOk.setBackground(new Color(255, 59, 48));
        btnOk.setForeground(Color.WHITE);
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.add(btnOk);
        dlg.add(pnlBtn, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockStruk(JFrame parent, Transaksi trx, double bayar, double kembali) {
        JDialog dlg = new JDialog(parent, "Struk Penjualan Transaksi Sukses", true);
        dlg.setSize(520, 620);
        dlg.setLayout(new BorderLayout(10, 10));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(new Color(252, 252, 250));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         MINIMARKET BAROKAH CABANG SUMENEP          \n");
        sb.append("        Kasir: Rafael Rizky | POS Version 2026       \n");
        sb.append("====================================================\n");
        sb.append("No. Nota  : TRX-20261116-011\n");
        sb.append("Tanggal   : 16-11-2026 17:30\n");
        sb.append("Pelanggan : Ibu Hj. Fatimah (VIP)\n");
        sb.append("Kasir     : rafael (ADMIN)\n");
        sb.append("Status    : LUNAS\n");
        sb.append("----------------------------------------------------\n");
        sb.append("DAFTAR BELANJA:\n");
        int no = 1;
        for (ItemTransaksi it : trx.getDaftarItem()) {
            sb.append(String.format("%d. %s - %s\n", no++, it.getProduk().getKode(), it.getProduk().getNama()));
            double pct = it.getDiskonItem() * 100;
            if (pct > 0) {
                sb.append(String.format("   %d unit x Rp %,d (-%.0f%%)  = Rp %,d\n",
                        it.getJumlahBeli(), it.getHargaSatuan(), pct, it.hitungSubtotal()));
            } else {
                sb.append(String.format("   %d unit x Rp %,d             = Rp %,d\n",
                        it.getJumlahBeli(), it.getHargaSatuan(), it.hitungSubtotal()));
            }
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Total Belanja Kotor (Gross) : Rp %,d\n", trx.hitungTotalSubtotal()));
        sb.append(String.format("Diskon Member (VIP 15%%)     : Rp %,.0f\n", trx.hitungDiskonNominal()));
        sb.append("====================================================\n");
        sb.append(String.format("TOTAL AKHIR TAGIHAN         : Rp %,.0f\n", trx.hitungTotalBayar()));
        sb.append("====================================================\n");
        sb.append(String.format("Metode Bayar                : TUNAI\n"));
        sb.append(String.format("Tunai Diterima              : Rp %,.0f\n", bayar));
        sb.append(String.format("Uang Kembalian              : Rp %,.0f\n", kembali));
        sb.append("====================================================\n");
        sb.append("      Terima Kasih Atas Kunjungan Anda di           \n");
        sb.append("         MINIMARKET BAROKAH CABANG SUMENEP          \n");
        sb.append("====================================================\n");

        ta.setText(sb.toString());
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Selesai & Cetak");
        btnTutup.setBackground(new Color(52, 199, 89));
        btnTutup.setForeground(Color.WHITE);
        JPanel pnlBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBawah.add(btnTutup);
        dlg.add(pnlBawah, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockLaporanHarian(JFrame parent, String tgl, int tabActiveIndex) {
        JDialog dlg = new JDialog(parent, "Laporan Penjualan Harian & Tutup Kasir", true);
        dlg.setSize(750, 540);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        KalenderDatePicker picker = new KalenderDatePicker(dlg, "dd-MM-yyyy", LocalDate.of(2026, 11, 16));
        JButton btnFilter = new JButton("Tampilkan Laporan");
        btnFilter.setBackground(new Color(0, 122, 255));
        btnFilter.setForeground(Color.WHITE);

        topBar.add(new JLabel("Pilih Tanggal Transaksi:"));
        topBar.add(picker);
        topBar.add(btnFilter);
        dlg.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: Ringkasan Finansial
        JTextArea txtRingkasan = new JTextArea();
        txtRingkasan.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRingkasan.setEditable(false);
        try {
            LaporanPenjualan lap = LaporanPenjualan.buatLaporan(
                    Paths.get("data", "transaksi.txt"),
                    Paths.get("data", "transaksi_item.txt"),
                    tgl
            );

            StringBuilder sb = new StringBuilder();
            sb.append("===================================================================\n");
            sb.append("                 RINGKASAN LAPORAN PENJUALAN HARIAN                \n");
            sb.append("===================================================================\n");
            sb.append("Tanggal Laporan        : ").append(lap.getFilterTanggal()).append("\n");
            sb.append("Periode Produk Terlaris: ").append(lap.getNamaBulanTahun()).append(" (Akumulasi Bulanan)\n");
            sb.append("Petugas Verifikasi     : rafael (ADMIN)\n");
            sb.append("-------------------------------------------------------------------\n");
            sb.append("Jumlah Nota LUNAS      : ").append(lap.getJumlahNotaLunas()).append(" Transaksi\n");
            sb.append(String.format("Total Omzet Penjualan  : Rp %,.0f\n", lap.getTotalOmzet()));
            sb.append(String.format("Total Tunai Masuk      : Rp %,.0f\n", lap.getTotalTunai()));
            sb.append("-------------------------------------------------------------------\n");
            sb.append("Jumlah Nota VOID/Batal : ").append(lap.getJumlahNotaVoid()).append(" Transaksi (Dikecualikan dari Omzet)\n");
            sb.append("===================================================================\n");
            txtRingkasan.setText(sb.toString());

            // Tab 2: Produk Terlaris Bulanan
            DefaultTableModel modelTop = new DefaultTableModel(new String[]{"No", "Kode", "Nama Produk", "Total Terjual (Unit)"}, 0);
            int no = 1;
            for (LaporanPenjualan.ItemTerlaris it : lap.getProdukTerlarisTop5()) {
                modelTop.addRow(new Object[]{no++, it.getKodeProduk(), it.getNamaProduk(), it.getTotalQty()});
            }
            JTable tblTop = new JTable(modelTop);
            tblTop.setRowHeight(25);
            tblTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tblTop.getTableHeader().setPreferredSize(new Dimension(0, 32));
            tblTop.getTableHeader().setBackground(new Color(29, 29, 31));
            tblTop.getTableHeader().setForeground(Color.WHITE);

            if (tabActiveIndex == 1) {
                tabs.addTab("Produk Terlaris (" + lap.getNamaBulanTahun() + ")", new JScrollPane(tblTop));
                tabs.addTab("Ringkasan Finansial", new JScrollPane(txtRingkasan));
            } else {
                tabs.addTab("Ringkasan Finansial", new JScrollPane(txtRingkasan));
                tabs.addTab("Produk Terlaris (" + lap.getNamaBulanTahun() + ")", new JScrollPane(tblTop));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        dlg.add(tabs, BorderLayout.CENTER);

        JPanel pnlBwh = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnTutup = new JButton("Tutup");
        pnlBwh.add(btnTutup);
        dlg.add(pnlBwh, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogMockRiwayatVoid(JFrame parent) {
        JDialog dlg = new JDialog(parent, "Riwayat Transaksi & Pembatalan (VOID Nota)", true);
        dlg.setSize(850, 480);
        dlg.setLayout(new BorderLayout(10, 10));

        DefaultTableModel modelTrx = new DefaultTableModel(new String[]{
                "ID Transaksi", "Waktu", "Pelanggan", "Member", "Total Bayar", "Status", "Kasir"
        }, 0);

        try {
            TransaksiFileRepository repo = new TransaksiFileRepository();
            List<String[]> list = repo.muatSemuaTransaksi(Paths.get("data", "transaksi.txt"));
            for (String[] t : list) {
                modelTrx.addRow(new Object[]{t[0], t[1], t[2], t[3], String.format("Rp %,.0f", Double.parseDouble(t[4])), t[7], t[8]});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable tbl = new JTable(modelTrx);
        tbl.setRowHeight(25);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tbl.getTableHeader().setBackground(new Color(29, 29, 31));
        tbl.getTableHeader().setForeground(Color.WHITE);

        // Pilih baris VOID untuk highlight visual
        for (int r = 0; r < modelTrx.getRowCount(); r++) {
            if ("VOID".equals(modelTrx.getValueAt(r, 5))) {
                tbl.setRowSelectionInterval(r, r);
                break;
            }
        }

        dlg.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnRincian = new JButton("Lihat Rincian Item");
        btnRincian.setBackground(new Color(0, 122, 255));
        btnRincian.setForeground(Color.WHITE);
        JButton btnVoid = new JButton("⚠️ VOID Nota Lunas");
        btnVoid.setBackground(new Color(255, 59, 48));
        btnVoid.setForeground(Color.WHITE);

        pnlAksi.add(btnRincian);
        pnlAksi.add(btnVoid);
        dlg.add(pnlAksi, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    // --- SCREENSHOT RENDERING METHODS ---

    private static void saveComponentStandalone(Container comp, int w, int h, String fileName, String note) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(245, 245, 247));
        g2d.fillRect(0, 0, w, h);
        comp.printAll(g2d);
        g2d.dispose();

        writeBoth(img, fileName, note);
    }

    private static void saveScreenshot(JFrame frame, String fileName, String note) {
        int w = frame.getContentPane().getWidth();
        int h = frame.getContentPane().getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        frame.getContentPane().printAll(g2d);
        g2d.dispose();

        writeBoth(img, fileName, note);
    }

    private static void saveCompositeScreenshot(JFrame frame, JDialog dlg, String fileName) {
        int fw = frame.getContentPane().getWidth();
        int fh = frame.getContentPane().getHeight();
        BufferedImage base = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = base.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, fw, fh);
        frame.getContentPane().printAll(g2d);

        // Dimming overlay
        g2d.setColor(new Color(0, 0, 0, 95));
        g2d.fillRect(0, 0, fw, fh);

        int dw = dlg.getWidth();
        int dh = dlg.getHeight();
        int dx = Math.max(0, (fw - dw) / 2);
        int dy = Math.max(0, (fh - dh) / 2);

        // Shadow & Dialog background
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillRoundRect(dx - 6, dy - 6, dw + 12, dh + 12, 16, 16);

        g2d.setColor(new Color(248, 248, 250));
        g2d.fillRoundRect(dx, dy, dw, dh, 10, 10);
        g2d.setColor(new Color(200, 200, 205));
        g2d.drawRoundRect(dx, dy, dw, dh, 10, 10);

        // Header bar dialog
        g2d.setColor(new Color(29, 29, 31));
        g2d.fillRoundRect(dx, dy, dw, 34, 10, 10);
        g2d.fillRect(dx, dy + 22, dw, 12);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.drawString(dlg.getTitle(), dx + 14, dy + 22);

        // Render dialog content
        Graphics2D dlgG = (Graphics2D) g2d.create(dx, dy + 34, dw, dh - 34);
        dlg.getContentPane().printAll(dlgG);
        dlgG.dispose();

        g2d.dispose();

        writeBoth(base, fileName, dlg.getTitle());
    }

    private static void writeBoth(BufferedImage img, String fileName, String note) {
        try {
            File f1 = new File(OUT_DIR_LAPORAN, fileName);
            ImageIO.write(img, "png", f1);
            File f2 = new File(OUT_DIR_LOCAL, fileName);
            ImageIO.write(img, "png", f2);
            System.out.printf("  [OK] Saved: %-36s | %s%n", fileName, note);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
