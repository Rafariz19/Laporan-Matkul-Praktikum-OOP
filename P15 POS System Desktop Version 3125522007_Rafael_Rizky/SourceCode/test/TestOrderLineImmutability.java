package test;

import model.*;
import model.exception.*;

/**
 * Pengujian 1: Order Line Immutability (Snapshot Harga Keranjang)
 * Memanggil kode produksi asli:
 * - trx.tambahItem(...)
 * - katalog.ubahProduk(...)
 * - item.hitungSubtotal()
 */
public class TestOrderLineImmutability {
    public static void run() {
        System.out.println("--- [TEST 1] Order Line Immutability (Snapshot Harga Keranjang) ---");
        KatalogProduk katalog = new KatalogProduk();
        Produk roti = new ProdukMakanan("MK01", "Roti Gandum", 10000, 20, "2026-12-31");
        katalog.tambahProduk(roti);

        Pelanggan cust = new Pelanggan("CUST-01", "Rafael", "081234567890", "REGULER");
        Transaksi trx = new Transaksi("TRX-01", "09-11-2026 14:00", cust);

        try {
            trx.tambahItem(roti, 2);
            ItemTransaksi itemKeranjang = trx.getDaftarItem().get(0);
            int subtotalAwal = itemKeranjang.hitungSubtotal();
            System.out.println("  1. Item masuk keranjang: 2 x Rp 10.000 = Rp " + subtotalAwal);

            katalog.ubahProduk("MK01", "Roti Gandum Spesial", 25000, 18, "2026-12-31");
            System.out.println("  2. Harga produk di katalog diubah menjadi Rp " + roti.getHarga());

            int subtotalSetelahKatalogBerubah = itemKeranjang.hitungSubtotal();
            System.out.println("  3. Subtotal keranjang TETAP Rp " + subtotalSetelahKatalogBerubah + " (TIDAK TERPENGARUH)");

            if (subtotalSetelahKatalogBerubah == subtotalAwal && itemKeranjang.getHargaSatuan() == 10000) {
                System.out.println("✔ [LULUS] Snapshot hargaSatuan berhasil mengisolasi transaksi dari perubahan harga katalog.\n");
            } else {
                throw new AssertionError("Subtotal keranjang terpengaruh perubahan harga katalog!");
            }
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 1] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
