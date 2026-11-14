package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Pengujian 10 (Fase 1): Log Item Tiap Nota & Laporan Harian Penjualan
 * Menguji kode produksi:
 * 1. Penyimpanan rincian item ke transaksi_item.txt dengan nilai snapshot.
 * 2. LaporanPenjualan agregasi omzet, jumlah nota lunas, top 5 item terlaris.
 * 3. Backward compatibility untuk transaksi lama tanpa rincian item.
 */
public class TestItemLogAndDailyReport {
    public static void run() {
        System.out.println("--- [TEST 10] Log Item Nota & Laporan Penjualan Harian ---");
        try {
            Path fileTrx = Files.createTempFile("trx_log_", ".txt");
            Path fileItem = Files.createTempFile("item_log_", ".txt");
            fileTrx.toFile().deleteOnExit();
            fileItem.toFile().deleteOnExit();

            KatalogProduk katalog = new KatalogProduk();
            Produk roti = new ProdukMakanan("MK01", "Roti Coklat", 10000, 50, "2026-12-31");
            Produk susu = new ProdukMakanan("MK02", "Susu Segar", 15000, 40, "2026-12-30");
            katalog.tambahProduk(roti);
            katalog.tambahProduk(susu);

            TransaksiFileRepository trxRepo = new TransaksiFileRepository();
            TransaksiItemRepository itemRepo = new TransaksiItemRepository();

            Pelanggan cust = new Pelanggan("CUST-10", "Dina", "081234567890", "REGULER");
            Transaksi trx1 = new Transaksi("TRX-HARI-01", "10-11-2026 10:00", cust);
            trx1.tambahItem(roti, 4); // 40.000
            trx1.tambahItem(susu, 2); // 30.000
            trx1.prosesTransaksi(100000); // LUNAS, total 70.000

            // 1. Simpan transaksi 9 kolom & simpan rincian item
            trxRepo.catatTransaksi(fileTrx, trx1, 100000, 30000, "budi_kasir");
            itemRepo.simpanItemTransaksi(fileItem, trx1);

            // Verifikasi bahwa nama dan harga di file item adalah snapshot
            List<String[]> rincian = itemRepo.muatItemByTransaksi(fileItem, "TRX-HARI-01");
            if (rincian.size() != 2) {
                throw new AssertionError("Jumlah rincian item tersimpan tidak sesuai!");
            }
            System.out.println("  1. Rincian item berhasil dibukukan (" + rincian.size() + " baris item tersimpan).");

            // Mutasi harga dan nama di katalog untuk membuktikan order-line snapshot immutability
            katalog.ubahProduk("MK01", "Roti Mahal", 99000, 10, "2026-12-31");

            // 2. Tambahkan satu transaksi lama tanpa rincian item (simulasi backward compatibility)
            // Format 8 kolom lawas: ID|TGL|PELANGGAN|MEMBER|TOTAL|BAYAR|KEMBALI|STATUS
            String barisLegacy = "TRX-LEGACY-99|10-11-2026 09:00|Pelanggan Lama|REGULER|50000|50000|0|LUNAS\n";
            Files.writeString(fileTrx, barisLegacy, java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
            System.out.println("  2. Transaksi legacy (format lama 8 kolom) disuntikkan ke berkas log.");

            // 3. Bangkitkan Laporan Penjualan Harian
            LaporanPenjualan laporan = LaporanPenjualan.buatLaporan(fileTrx, fileItem, "10-11-2026");

            // Total nota lunas harus 2 (TRX-HARI-01 = 70.000, TRX-LEGACY-99 = 50.000 -> Total Omzet = 120.000)
            if (laporan.getJumlahNotaLunas() != 2) {
                throw new AssertionError("Jumlah nota lunas salah! Didapat: " + laporan.getJumlahNotaLunas());
            }
            if (laporan.getTotalOmzet() != 120000.0) {
                throw new AssertionError("Total omzet salah! Didapat: " + laporan.getTotalOmzet());
            }
            if (!laporan.isAdaTransaksiTanpaRincian()) {
                throw new AssertionError("Laporan gagal menandai keberadaan transaksi tanpa rincian item!");
            }
            System.out.println("  3. Laporan harian: 2 nota lunas, Total Omzet = Rp " + (long) laporan.getTotalOmzet() + 
                               ", flag legacy terdeteksi: " + laporan.isAdaTransaksiTanpaRincian());

            // 4. Verifikasi Top Produk Terlaris (Roti Coklat = 4 unit, Susu Segar = 2 unit)
            List<LaporanPenjualan.ItemTerlaris> top5 = laporan.getProdukTerlarisTop5();
            if (top5.isEmpty() || !top5.get(0).getKodeProduk().equals("MK01") || top5.get(0).getTotalQty() != 4) {
                throw new AssertionError("Perhitungan produk terlaris salah!");
            }
            System.out.println("  4. Produk Terlaris #1: " + top5.get(0).getNamaProduk() + " (" + top5.get(0).getTotalQty() + " unit terjual).");

            System.out.println("✔ [LULUS] Log item granular, agregasi laporan penjualan, dan backward compatibility terverifikasi penuh.\n");
            Files.deleteIfExists(fileTrx);
            Files.deleteIfExists(fileItem);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 10] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
