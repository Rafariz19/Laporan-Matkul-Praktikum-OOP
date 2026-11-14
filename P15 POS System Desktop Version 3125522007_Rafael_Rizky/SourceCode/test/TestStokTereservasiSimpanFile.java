package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pengujian 3: Skenario Nyata Simpan File saat Keranjang PENDING & Buka Ulang
 * Memanggil kode produksi asli model:
 * - repo.simpanAman(path, katalogAwal, trxPending)
 * - repo.muat(path, katalogBaru)
 */
public class TestStokTereservasiSimpanFile {
    public static void run() {
        System.out.println("--- [TEST 3] Skenario Nyata: Simpan File saat Keranjang PENDING & Buka Ulang ---");
        try {
            Path testFilePath = Files.createTempFile("produk_test_", ".txt");
            testFilePath.toFile().deleteOnExit();

            KatalogProduk katalogAwal = new KatalogProduk();
            Produk mouse = new ProdukElektronik("EL01", "Mouse Gaming", 200000, 15, 24);
            katalogAwal.tambahProduk(mouse);

            Pelanggan cust = new Pelanggan("CUST-03", "Andi", "081234567890", "VIP");
            Transaksi trxPending = new Transaksi("TRX-PENDING-01", "09-11-2026 14:00", cust);

            // 1. Tambah 5 unit ke keranjang belanja
            trxPending.tambahItem(mouse, 5);
            System.out.println("  1. Tambah 5 unit ke keranjang. Stok di RAM berkurang: " + mouse.getStok() + " unit.");

            // 2. Simpan katalog ke berkas menggunakan kode produksi asli repo.simpanAman
            ProdukFileRepository repo = new ProdukFileRepository();
            repo.simpanAman(testFilePath, katalogAwal, trxPending);
            System.out.println("  2. Berkas fisik disimpan via repo.simpanAman() saat transaksi PENDING.");

            // 3. Batalkan transaksi (simulasi sesi ditutup / reset)
            trxPending.batalkanTransaksi();

            // 4. Buka sesi baru: muat berkas dari disk
            KatalogProduk katalogSesiBaru = new KatalogProduk();
            repo.muat(testFilePath, katalogSesiBaru);
            Produk mouseSesiBaru = katalogSesiBaru.cariProduk("EL01");

            System.out.println("  3. Sesi baru dibuka, membaca berkas fisik: Stok = " + mouseSesiBaru.getStok() + " unit.");

            if (mouseSesiBaru.getStok() == 15) {
                System.out.println("✔ [LULUS] Terbukti stok fisik di disk tetap 15 unit (tidak ada kebocoran stok ke file).\n");
            } else {
                throw new AssertionError("Kebocoran stok terdeteksi! Stok di file disk: " + mouseSesiBaru.getStok());
            }

            Files.deleteIfExists(testFilePath);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 3] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
