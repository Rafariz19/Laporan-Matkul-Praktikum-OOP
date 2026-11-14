package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Pengujian 8 (Poin 7): Keranjang Dibatalkan Tidak Pernah Ditulis ke transaksi.txt
 * Skenario:
 * 1. Siapkan file transaksi log sementara.
 * 2. Catat satu transaksi sah LUNAS (1 baris).
 * 3. Buat transaksi baru, tambah 3 item, lalu batalkan / reset (batalkanTransaksi).
 * 4. Baca kembali berkas transaksi log fisik.
 * 5. Buktikan jumlah baris tetap 1 dan ID transaksi batal TIDAK PERNAH masuk ke berkas.
 */
public class TestBatalTidakTulisTransaksiFile {
    public static void run() {
        System.out.println("--- [TEST 8] Transaksi Dibatalkan Tidak Ditulis ke transaksi.txt ---");
        try {
            Path logFile = Files.createTempFile("transaksi_log_test_", ".txt");
            logFile.toFile().deleteOnExit();

            TransaksiFileRepository trxRepo = new TransaksiFileRepository();

            // 1. Transaksi sah LUNAS
            Pelanggan cust1 = new Pelanggan("CUST-A", "Eko", "081234567890", "REGULER");
            Transaksi trxLunas = new Transaksi("TRX-SAH-01", "09-11-2026 14:00", cust1);
            Produk prod = new ProdukMakanan("MK01", "Roti", 10000, 20, "2026-12-31");
            trxLunas.tambahItem(prod, 1);
            trxLunas.prosesTransaksi(10000);

            trxRepo.catatTransaksi(logFile, trxLunas, 10000, 0);
            List<String> linesAwal = Files.readAllLines(logFile);
            System.out.println("  1. Transaksi sah LUNAS dicatat. Jumlah baris log: " + linesAwal.size());

            // 2. Transaksi kedua yang dibatalkan / di-reset
            Pelanggan cust2 = new Pelanggan("CUST-B", "Fani", "081234567890", "VIP");
            Transaksi trxBatal = new Transaksi("TRX-BATAL-99", "09-11-2026 14:05", cust2);
            trxBatal.tambahItem(prod, 3);

            // Kasir menekan reset / batalkan transaksi
            trxBatal.batalkanTransaksi();
            System.out.println("  2. Transaksi TRX-BATAL-99 dibatalkan (status: " + trxBatal.getStatusTransaksi() + ")");

            // Uji guard catatTransaksi: wajib menolak pencatatan nota berstatus DIBATALKAN
            boolean ditolak = false;
            try {
                trxRepo.catatTransaksi(logFile, trxBatal, 30000, 0);
            } catch (StatusTransaksiInvalidException ex) {
                ditolak = true;
                System.out.println("  3. Guard repository menolak catatTransaksi nota DIBATALKAN (melempar StatusTransaksiInvalidException).");
            }

            if (!ditolak) {
                throw new AssertionError("Repository gagal menolak pencatatan transaksi yang berstatus DIBATALKAN!");
            }

            // Baca kembali log fisik
            List<String> linesAkhir = Files.readAllLines(logFile);
            System.out.println("  4. Baca berkas transaksi log fisik: Jumlah baris = " + linesAkhir.size());

            boolean idBatalDitemukan = false;
            for (String line : linesAkhir) {
                if (line.contains("TRX-BATAL-99")) {
                    idBatalDitemukan = true;
                    break;
                }
            }

            if (linesAkhir.size() == 1 && !idBatalDitemukan) {
                System.out.println("✔ [LULUS] Terbukti transaksi batal TIDAK PERNAH ditulis ke berkas transaksi.txt.\n");
            } else {
                throw new AssertionError("Transaksi batal bocor ke dalam berkas transaksi.txt!");
            }

            Files.deleteIfExists(logFile);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 8] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
