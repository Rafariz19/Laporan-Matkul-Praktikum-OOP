package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pengujian 6 (Poin 1): CHECKOUT vs SAFE-SAVE End-to-End
 * Skenario:
 * 1. Stok awal = 25 unit.
 * 2. Tambah item belanja N = 7 unit (stok RAM jadi 18).
 * 3. Checkout transaksi sampai status LUNAS.
 * 4. Panggil repo.simpanAman(file, katalog, trx).
 *    Membuktikan bahwa saat status LUNAS, getReservasiStokMap() bernilai kosong
 *    sehingga yang disimpan ke disk adalah persis stok aktual (stok awal - N = 18).
 * 5. Baca ulang file ke objek KatalogProduk baru dan asersi stok == 18.
 */
public class TestCheckoutVsSafeSave {
    public static void run() {
        System.out.println("--- [TEST 6] CHECKOUT vs SAFE-SAVE End-to-End ---");
        try {
            Path tempProdukFile = Files.createTempFile("produk_checkout_", ".txt");
            tempProdukFile.toFile().deleteOnExit();

            KatalogProduk katalog = new KatalogProduk();
            Produk keyboard = new ProdukElektronik("EL09", "Keyboard Mechanical", 500000, 25, 24);
            katalog.tambahProduk(keyboard);

            Pelanggan cust = new Pelanggan("CUST-06", "Dewi", "081234567890", "GOLD");
            Transaksi trx = new Transaksi("TRX-06", "09-11-2026 14:00", cust);

            int stokAwal = 25;
            int nBeli = 7;

            // 1. Tambah item 7 unit
            trx.tambahItem(keyboard, nBeli);
            System.out.println("  1. Tambah " + nBeli + " unit ke keranjang. Stok di RAM: " + keyboard.getStok());

            // 2. Sebelum checkout, simpan aman harus tetap menulis stok awal (25)
            ProdukFileRepository repo = new ProdukFileRepository();
            repo.simpanAman(tempProdukFile, katalog, trx);
            KatalogProduk katalogPendingCek = new KatalogProduk();
            repo.muat(tempProdukFile, katalogPendingCek);
            if (katalogPendingCek.cariProduk("EL09").getStok() != stokAwal) {
                throw new AssertionError("Saat PENDING, stok di berkas harus tetap stok awal (" + stokAwal + ")!");
            }
            System.out.println("  2. Saat PENDING: Berkas disk tetap menyimpan stok awal = " + stokAwal);

            // 3. Checkout transaksi sampai LUNAS
            double tagihan = trx.hitungTotalBayar();
            trx.prosesTransaksi(tagihan + 50000); // Bayar lebih
            if (!"LUNAS".equals(trx.getStatusTransaksi())) {
                throw new AssertionError("Status transaksi gagal berubah menjadi LUNAS!");
            }
            System.out.println("  3. Checkout berhasil: Status Transaksi = " + trx.getStatusTransaksi());

            // 4. Verifikasi method getReservasiStokMap() pada status LUNAS harus kosong
            if (!trx.getReservasiStokMap().isEmpty()) {
                throw new AssertionError("Reservasi stok harus kosong saat transaksi sudah LUNAS!");
            }
            System.out.println("  4. Terbukti getReservasiStokMap() kosong saat LUNAS (tidak menambah reservasi).");

            // 5. Simpan aman pasca checkout
            repo.simpanAman(tempProdukFile, katalog, trx);

            // 6. Baca ulang file ke instance katalog baru
            KatalogProduk katalogPascaCheckout = new KatalogProduk();
            repo.muat(tempProdukFile, katalogPascaCheckout);
            Produk hasilBaca = katalogPascaCheckout.cariProduk("EL09");

            int stokAkhirFile = hasilBaca.getStok();
            int ekspektasiStok = stokAwal - nBeli;
            System.out.println("  5. Baca ulang file pasca LUNAS: Stok di file = " + stokAkhirFile + " (Ekspektasi: " + ekspektasiStok + ")");

            if (stokAkhirFile == ekspektasiStok) {
                System.out.println("✔ [LULUS] Terbukti stok di file fisik persis sama dengan (stok awal - N) setelah LUNAS.\n");
            } else {
                throw new AssertionError("Stok di file tidak sesuai! Didapat: " + stokAkhirFile + ", Diharapkan: " + ekspektasiStok);
            }

            Files.deleteIfExists(tempProdukFile);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 6] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
