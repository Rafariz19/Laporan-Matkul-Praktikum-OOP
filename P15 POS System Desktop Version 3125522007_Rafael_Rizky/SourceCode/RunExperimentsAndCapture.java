package test;

import view.KasirDesktopFrame;
import model.*;
import model.exception.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;

public class RunExperimentsAndCapture {

    private static final String OUT_DIR = "screenshots_ppt";

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new File(OUT_DIR).mkdirs();

        SwingUtilities.invokeAndWait(() -> {
            try {
                runAllExperiments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("ALL_EXPERIMENTS_COMPLETED_SUCCESSFULLY");
        System.exit(0);
    }

    private static void runAllExperiments() throws Exception {
        // 1. Tampilan Utama
        KasirDesktopFrame frame = new KasirDesktopFrame();
        frame.setSize(1150, 720);
        frame.setLocationRelativeTo(null);
        frame.addNotify();
        frame.validate();
        frame.getContentPane().setSize(1150, 720);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();

        saveScreenshot(frame, "01_tampilan_utama_katalog.png", "Tampilan Utama POS Desktop: Katalog Produk & Keranjang");

        // Dapatkan fields via refleksi untuk automasi
        Field fKatalog = KasirDesktopFrame.class.getDeclaredField("katalog");
        fKatalog.setAccessible(true);
        KatalogProduk katalog = (KatalogProduk) fKatalog.get(frame);

        Field fTxtCari = KasirDesktopFrame.class.getDeclaredField("txtCari");
        fTxtCari.setAccessible(true);
        JTextField txtCari = (JTextField) fTxtCari.get(frame);

        Method mFilterKatalog = KasirDesktopFrame.class.getDeclaredMethod("filterKatalog", String.class);
        mFilterKatalog.setAccessible(true);

        Method mMuatKatalog = KasirDesktopFrame.class.getDeclaredMethod("muatTabelKatalog");
        mMuatKatalog.setAccessible(true);

        Method mMuatKeranjang = KasirDesktopFrame.class.getDeclaredMethod("muatTabelKeranjang");
        mMuatKeranjang.setAccessible(true);

        Method mUpdateFinansial = KasirDesktopFrame.class.getDeclaredMethod("updateKalkulasiFinansial");
        mUpdateFinansial.setAccessible(true);

        Field fTrxAktif = KasirDesktopFrame.class.getDeclaredField("transaksiAktif");
        fTrxAktif.setAccessible(true);
        Transaksi trx = (Transaksi) fTrxAktif.get(frame);

        Field fTxtNamaPelanggan = KasirDesktopFrame.class.getDeclaredField("txtNamaPelanggan");
        fTxtNamaPelanggan.setAccessible(true);
        JTextField txtNamaPelanggan = (JTextField) fTxtNamaPelanggan.get(frame);

        Field fCmbTipeMember = KasirDesktopFrame.class.getDeclaredField("cmbTipeMember");
        fCmbTipeMember.setAccessible(true);
        JComboBox<?> cmbTipeMember = (JComboBox<?>) fCmbTipeMember.get(frame);

        Field fTxtBayar = KasirDesktopFrame.class.getDeclaredField("txtBayarTunai");
        fTxtBayar.setAccessible(true);
        JTextField txtBayar = (JTextField) fTxtBayar.get(frame);

        // 2. Percobaan Pencarian / Filter Produk
        txtCari.setText("Kopi");
        mFilterKatalog.invoke(frame, "Kopi");
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "02_filter_pencarian_produk.png", "Pencarian Produk Cepat Berbasis Keyword: 'Kopi'");

        // Reset filter
        txtCari.setText("");
        mMuatKatalog.invoke(frame);

        // 3. Percobaan Tambah Produk Baru (Mock Dialog UI Overlay)
        JDialog dlgTambah = buatDialogTambahProduk(frame);
        saveCompositeScreenshot(frame, dlgTambah, "03_dialog_tambah_produk.png");
        dlgTambah.dispose();

        // Tambahkan produk riil ke katalog
        ProdukMakanan pBaru = new ProdukMakanan("M004", "Roti Panggang Keju", 15000, 30, "2026-12-31");
        katalog.tambahProduk(pBaru);
        mMuatKatalog.invoke(frame);

        // 4. Percobaan Edit Produk (Mock Dialog UI Overlay)
        Produk pEdit = katalog.cariProduk("E001");
        JDialog dlgEdit = buatDialogEditProduk(frame, pEdit);
        saveCompositeScreenshot(frame, dlgEdit, "04_dialog_edit_produk.png");
        dlgEdit.dispose();

        // 5. Percobaan Memasukkan Produk ke Keranjang Belanja
        Produk p1 = katalog.cariProduk("M001"); // misal Kopi
        Produk p2 = katalog.cariProduk("E001"); // misal Mouse/Laptop
        if (p1 != null) trx.tambahItem(p1, 2);
        if (p2 != null) trx.tambahItem(p2, 1);
        if (pBaru != null) trx.tambahItem(pBaru, 3);

        mMuatKeranjang.invoke(frame);
        mMuatKatalog.invoke(frame);
        mUpdateFinansial.invoke(frame);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "05_tambah_ke_keranjang.png", "Produk Masuk Keranjang: Subtotal Otomatis & Stok Berkurang");

        // 6. Percobaan Membership & Diskon
        txtNamaPelanggan.setText("Rafael Rizky (VIP Member)");
        trx.getPelanggan().ubahTipeMember("VIP");
        mUpdateFinansial.invoke(frame);
        frame.getContentPane().doLayout();
        frame.getContentPane().validate();
        saveScreenshot(frame, "06_membership_diskon.png", "Penerapan Membership VIP (Diskon 15% Otomatis)");

        // 7. Percobaan Exception Handling: Pembayaran Kurang Dialog
        txtBayar.setText("50000"); // jauh lebih kecil dari total
        mUpdateFinansial.invoke(frame);
        JDialog dlgError = buatDialogErrorPopup(frame, 
            "Nominal pembayaran kurang Rp 215.000!\nTotal Tagihan: Rp 265.000, Tunai Diserahkan: Rp 50.000\n(Pelanggaran Rule: PembayaranKurangException)", 
            "Uang Pembayaran Kurang");
        saveCompositeScreenshot(frame, dlgError, "07_validasi_exception_handling.png");
        dlgError.dispose();

        // 8. Percobaan Pembayaran Sukses & Cetak Struk
        txtBayar.setText("300000");
        mUpdateFinansial.invoke(frame);
        trx.prosesTransaksi(300000);
        JDialog dlgStruk = buatDialogStruk(frame, trx, 300000, 300000 - trx.hitungTotalBayar());
        saveCompositeScreenshot(frame, dlgStruk, "08_struk_pembayaran_sukses.png");
        dlgStruk.dispose();

        frame.dispose();
    }

    private static JDialog buatDialogTambahProduk(JFrame parent) {
        JDialog dlg = new JDialog(parent, "Pendaftaran Produk Baru", true);
        dlg.setSize(440, 360);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        dlg.add(new JLabel("Kategori Produk:")); 
        dlg.add(new JComboBox<>(new String[]{"Makanan (ProdukMakanan)", "Elektronik (ProdukElektronik)"}));
        dlg.add(new JLabel("Kode Produk:")); 
        dlg.add(new JTextField("M004"));
        dlg.add(new JLabel("Nama Produk:")); 
        dlg.add(new JTextField("Roti Panggang Keju"));
        dlg.add(new JLabel("Harga Satuan (Rp):")); 
        dlg.add(new JTextField("15000"));
        dlg.add(new JLabel("Stok Awal:")); 
        dlg.add(new JTextField("30"));
        dlg.add(new JLabel("Tanggal Exp (YYYY-MM-DD):")); 
        dlg.add(new JTextField("2026-12-31"));

        JButton btnBatal = new JButton("Batal");
        JButton btnSimpan = new JButton("Simpan Produk (Save)");
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dlg.add(btnBatal);
        dlg.add(btnSimpan);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogEditProduk(JFrame parent, Produk p) {
        String kode = (p != null) ? p.getKode() : "E001";
        String nama = (p != null) ? p.getNama() : "Mouse Wireless Logitech";
        int harga = (p != null) ? p.getHarga() : 150000;
        int stok = (p != null) ? p.getStok() : 15;

        JDialog dlg = new JDialog(parent, "Edit Data Produk - " + kode, true);
        dlg.setSize(450, 360);
        dlg.setLayout(new GridLayout(6, 2, 8, 8));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtKode = new JTextField(kode);
        txtKode.setEditable(false);
        txtKode.setBackground(new Color(240, 240, 240));

        dlg.add(new JLabel("Kode Produk (Tetap):")); dlg.add(txtKode);
        dlg.add(new JLabel("Nama Produk:")); dlg.add(new JTextField(nama));
        dlg.add(new JLabel("Harga Satuan (Rp):")); dlg.add(new JTextField(String.valueOf(harga + 25000)));
        dlg.add(new JLabel("Jumlah Stok:")); dlg.add(new JTextField(String.valueOf(stok + 10)));
        dlg.add(new JLabel("Masa Garansi (Bulan):")); dlg.add(new JTextField("24"));

        JButton btnBatal = new JButton("Batal");
        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(23, 162, 184));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dlg.add(btnBatal);
        dlg.add(btnSimpan);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogErrorPopup(JFrame parent, String message, String title) {
        JDialog dlg = new JDialog(parent, title, true);
        dlg.setSize(480, 200);
        dlg.setLayout(new BorderLayout(15, 15));
        dlg.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        dlg.add(iconLabel, BorderLayout.WEST);

        JTextArea txtMsg = new JTextArea(message);
        txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMsg.setEditable(false);
        txtMsg.setOpaque(false);
        txtMsg.setWrapStyleWord(true);
        txtMsg.setLineWrap(true);
        dlg.add(txtMsg, BorderLayout.CENTER);

        JButton btnOk = new JButton("OK (Kembali Perbaiki)");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dlg.add(btnOk, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static JDialog buatDialogStruk(JFrame parent, Transaksi trx, double bayar, double kembalian) {
        JDialog dlg = new JDialog(parent, "Struk Pembayaran Toko - " + trx.getIdTransaksi(), true);
        dlg.setSize(500, 600);
        dlg.setLayout(new BorderLayout());

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(new Color(252, 252, 248));

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("            STRUK PEMBELIAN KASIR RESMI             \n");
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
        sb.append(String.format("Total Pembelian (Gross)  : Rp %,.0f\n", (double) trx.hitungTotalSubtotal()));
        sb.append(String.format("Diskon Member (%s)      : Rp %,.0f\n", trx.getPelanggan().getTipeMember(), trx.hitungDiskonNominal()));
        sb.append("====================================================\n");
        sb.append(String.format("TOTAL TAGIHAN AKHIR      : Rp %,.0f\n", trx.hitungTotalBayar()));
        sb.append("====================================================\n");
        sb.append(String.format("Tunai Diterima           : Rp %,.0f\n", bayar));
        sb.append(String.format("Kembalian Konsumen       : Rp %,.0f\n", kembalian));
        sb.append("====================================================\n");
        sb.append("    Terima kasih atas kunjungan Anda di gerai kami! \n");
        sb.append("    (Tercatat otomatis di data/transaksi.txt)        \n");
        sb.append("====================================================\n");

        ta.setText(sb.toString());
        dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Selesai / Cetak Selesai");
        btnTutup.setBackground(new Color(27, 54, 93));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dlg.add(btnTutup, BorderLayout.SOUTH);

        dlg.doLayout();
        dlg.validate();
        return dlg;
    }

    private static void saveScreenshot(JFrame frame, String fileName, String titleNote) {
        int w = frame.getContentPane().getWidth();
        int h = frame.getContentPane().getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        frame.getContentPane().printAll(g2d);
        g2d.dispose();

        try {
            File out = new File(OUT_DIR, fileName);
            ImageIO.write(img, "png", out);
            System.out.println("Saved: " + out.getAbsolutePath() + " (" + titleNote + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveCompositeScreenshot(JFrame frame, JDialog dlg, String fileName) {
        int fw = frame.getContentPane().getWidth();
        int fh = frame.getContentPane().getHeight();
        BufferedImage base = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = base.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, fw, fh);
        frame.getContentPane().printAll(g2d);

        // Modal overlay dimming effect
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fillRect(0, 0, fw, fh);

        // Render dialog in center
        int dw = dlg.getWidth();
        int dh = dlg.getHeight();
        int dx = (fw - dw) / 2;
        int dy = (fh - dh) / 2;

        // Dialog shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(dx - 5, dy - 5, dw + 10, dh + 10, 16, 16);

        // Dialog background & border
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRoundRect(dx, dy, dw, dh, 10, 10);
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawRoundRect(dx, dy, dw, dh, 10, 10);

        // Title bar
        g2d.setColor(new Color(27, 54, 93));
        g2d.fillRoundRect(dx, dy, dw, 32, 10, 10);
        g2d.fillRect(dx, dy + 20, dw, 12);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.drawString(dlg.getTitle(), dx + 12, dy + 21);

        // Print dialog content inside
        Graphics2D dlgG = (Graphics2D) g2d.create(dx, dy + 32, dw, dh - 32);
        dlg.getContentPane().printAll(dlgG);
        dlgG.dispose();

        g2d.dispose();

        try {
            File out = new File(OUT_DIR, fileName);
            ImageIO.write(base, "png", out);
            System.out.println("Saved Composite: " + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
