package test;

import model.*;
import model.exception.*;

/**
 * Pengujian 7 (Poin 3): Uji Tambahan Hapus Produk di Keranjang & Skenario Tutup Jendela
 * 1. Hapus produk yang ada di keranjang transaksi aktif harus ditolak.
 * 2. Skenario penutupan jendela (windowClosing) saat keranjang PENDING memulihkan stok.
 */
public class TestHapusProdukKeranjangDanWindowClosing {
    public static void run() {
        System.out.println("--- [TEST 7] Hapus Produk di Keranjang & Skenario Window Closing ---");
        try {
            KatalogProduk katalog = new KatalogProduk();
            Produk p1 = new ProdukMakanan("MK01", "Biskuit Gandum", 12000, 30, "2026-11-30");
            katalog.tambahProduk(p1);

            Pelanggan cust = new Pelanggan("CUST-07", "Citra", "081234567890", "REGULER");
            Transaksi trx = new Transaksi("TRX-07", "09-11-2026 14:00", cust);

            trx.tambahItem(p1, 5); // Stok RAM tinggal 25

            // Sub-Uji A: Validasi Hapus Produk di Keranjang Aktif via model KatalogProduk.hapusProduk(kode, trx)
            boolean ditolak = false;
            try {
                katalog.hapusProduk("MK01", trx);
            } catch (KasirException e) {
                ditolak = true;
                System.out.println("  1. Model katalog.hapusProduk('MK01', trx) DITOLAK: " + e.getMessage());
            }

            if (!ditolak) {
                throw new AssertionError("Produk di keranjang seharusnya tidak boleh dihapus!");
            }

            // Sub-Uji B: Skenario Window Closing (Kasir keluar saat keranjang PENDING)
            System.out.println("  2. Skenario Window Closing dipicu: memanggil batalkanTransaksi() otomatis.");
            trx.batalkanTransaksi();

            if (p1.getStok() != 30) {
                throw new AssertionError("Stok tidak kembali saat window closing! Sisa stok: " + p1.getStok());
            }
            System.out.println("  3. Stok produk p1 terpulihkan penuh menjadi: " + p1.getStok());

            // Sub-Uji C: Setelah dibatalkan, penghapusan produk sekarang diizinkan
            if (!trx.adaProdukAktif("MK01")) {
                boolean hapusSukses = katalog.hapusProduk("MK01");
                if (hapusSukses && katalog.cariProduk("MK01") == null) {
                    System.out.println("  4. Setelah keranjang batal, produk berhasil dihapus dari katalog.");
                } else {
                    throw new AssertionError("Hapus produk gagal setelah keranjang dibatalkan!");
                }
            }

            System.out.println("✔ [LULUS] Proteksi hapus dan pemulihan stok windowClosing terverifikasi penuh.\n");
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 7] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
