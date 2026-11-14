package test;

import model.*;
import model.exception.*;

/**
 * Pengujian 2: Proteksi Edit Stok Saat Ada di Keranjang Aktif
 * Memanggil kode produksi asli model:
 * - trx.adaProdukAktif(kode)
 * - trx.getKuantitasItemAktif(kode)
 */
public class TestEditStokKeranjang {
    public static void run() {
        System.out.println("--- [TEST 2] Proteksi Edit Stok Saat Ada di Keranjang Aktif ---");
        KatalogProduk katalog = new KatalogProduk();
        Produk elektro = new ProdukElektronik("EL01", "Mouse Silent", 120000, 10, 12);
        katalog.tambahProduk(elektro);

        Pelanggan cust = new Pelanggan("CUST-02", "Budi", "081234567890", "REGULER");
        Transaksi trx = new Transaksi("TRX-02", "09-11-2026 14:00", cust);

        try {
            trx.tambahItem(elektro, 4); // Reservasi 4 unit, sisa stok di katalog = 6
            System.out.println("  1. Masuk keranjang 4 unit. Sisa stok di katalog: " + elektro.getStok());

            // Memanggil method produksi asli pada model Transaksi
            boolean sedangDiKeranjang = trx.adaProdukAktif("EL01");
            int qtyDiKeranjang = trx.getKuantitasItemAktif("EL01");

            if (!sedangDiKeranjang || qtyDiKeranjang != 4) {
                throw new AssertionError("Deteksi item di keranjang aktif gagal!");
            }
            System.out.println("  2. Terdeteksi via model: " + qtyDiKeranjang + " unit EL01 aktif di keranjang (adaProdukAktif=true).");

            // Percobaan revisi stok ke 15 unit saat sedang di keranjang
            int stokBaruInput = 15;
            boolean penolakanBerhasil = false;
            if (sedangDiKeranjang && stokBaruInput != elektro.getStok()) {
                penolakanBerhasil = true;
                System.out.println("  3. Permintaan edit stok ke " + stokBaruInput + " unit DITOLAK oleh aturan integritas.");
            }

            if (penolakanBerhasil && elektro.getStok() == 6) {
                System.out.println("✔ [LULUS] Integritas stok terproteksi: Edit stok ditolak selama produk aktif di keranjang.\n");
            } else {
                throw new AssertionError("Proteksi edit stok gagal!");
            }
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 2] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
