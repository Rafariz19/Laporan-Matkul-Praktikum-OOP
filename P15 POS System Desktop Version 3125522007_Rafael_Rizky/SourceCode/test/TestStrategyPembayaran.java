package test;

import model.MetodePembayaran;
import model.PembayaranQrisSimulasi;
import model.PembayaranTunai;
import model.Produk;
import model.ProdukElektronik;
import model.SplitPayment;
import model.Transaksi;
import model.exception.KasirException;
import model.exception.PembayaranKurangException;

/**
 * Pengujian Fase 2:
 * Pengujian Pola Strategy Pembayaran (MetodePembayaran):
 * 1. PembayaranTunai (Cash): Jalur sukses, kalkulasi kembalian, dan jalur gagal (uang kurang).
 * 2. PembayaranQrisSimulasi: Jalur sukses (kembalian 0), dan jalur gagal (saldo kurang).
 * 3. SplitPayment: Jalur sukses (kombinasi tunai + qris), jalur gagal (kombinasi kurang).
 * 4. Verifikasi bahwa jika strategi melempar PembayaranKurangException, state transaksi tetap PENDING.
 */
public class TestStrategyPembayaran {
    public static void main(String[] args) {
        System.out.println(">>> Menjalankan TestStrategyPembayaran...");

        // Setup master produk
        Produk p1 = new ProdukElektronik("EL01", "Mouse Gaming", 100000, 10, 12);

        // --- 1. UJI STRATEGI TUNAI ---
        try {
            Transaksi trxCash = new Transaksi("TRX-CASH-01", "01-10-2026 10:00", null);
            trxCash.tambahItem(p1, 1); // Total 100.000

            // Jalur Gagal: Uang 80.000 (< 100.000)
            boolean lemparKurang = false;
            try {
                trxCash.prosesTransaksi(new PembayaranTunai(), 80000);
            } catch (PembayaranKurangException e) {
                lemparKurang = true;
                System.out.println("  [OK Jalur Gagal Cash] Terlempar PembayaranKurangException: " + e.getMessage());
            }
            if (!lemparKurang) {
                throw new AssertionError("Gagal Uji Tunai: Pembayaran kurang tidak melempar exception!");
            }
            if (!"PENDING".equals(trxCash.getStatusTransaksi())) {
                throw new AssertionError("Gagal Uji Tunai: Status transaksi berubah setelah gagal bayar!");
            }

            // Jalur Sukses: Uang 150.000
            PembayaranTunai strategyCash = new PembayaranTunai();
            boolean sukses = trxCash.prosesTransaksi(strategyCash, 150000);
            if (!sukses || !"LUNAS".equals(trxCash.getStatusTransaksi())) {
                throw new AssertionError("Gagal Uji Tunai: Transaksi tidak lunas!");
            }
            if (strategyCash.getKembalian() != 50000) {
                throw new AssertionError("Gagal Uji Tunai: Kembalian salah: " + strategyCash.getKembalian());
            }
            System.out.println("  [OK Jalur Sukses Cash] Lunas dengan kembalian: " + strategyCash.getKembalian());
        } catch (KasirException e) {
            throw new AssertionError("Unexpected exception on cash test: " + e);
        }

        // --- 2. UJI STRATEGI QRIS SIMULASI ---
        try {
            Transaksi trxQris = new Transaksi("TRX-QRIS-01", "01-10-2026 10:15", null);
            trxQris.tambahItem(p1, 1); // Total 100.000

            // Jalur Gagal: Saldo otorisasi QRIS kurang
            boolean lemparKurang = false;
            try {
                trxQris.prosesTransaksi(new PembayaranQrisSimulasi("NMID-TEST-001"), 99000);
            } catch (PembayaranKurangException e) {
                lemparKurang = true;
                System.out.println("  [OK Jalur Gagal QRIS] Terlempar PembayaranKurangException: " + e.getMessage());
            }
            if (!lemparKurang) {
                throw new AssertionError("Gagal Uji QRIS: QRIS kurang tidak melempar exception!");
            }

            // Jalur Sukses: QRIS pas 100.000
            PembayaranQrisSimulasi strategyQris = new PembayaranQrisSimulasi("NMID-TEST-001");
            boolean sukses = trxQris.prosesTransaksi(strategyQris, 100000);
            if (!sukses || !"LUNAS".equals(trxQris.getStatusTransaksi())) {
                throw new AssertionError("Gagal Uji QRIS: Transaksi QRIS tidak lunas!");
            }
            if (strategyQris.getKembalian() != 0.0) {
                throw new AssertionError("Gagal Uji QRIS: QRIS tidak boleh ada kembalian!");
            }
            System.out.println("  [OK Jalur Sukses QRIS] Lunas: " + strategyQris.getInformasiTransaksi());
        } catch (KasirException e) {
            throw new AssertionError("Unexpected exception on QRIS test: " + e);
        }

        // --- 3. UJI STRATEGI SPLIT PAYMENT ---
        try {
            Transaksi trxSplit = new Transaksi("TRX-SPLIT-01", "01-10-2026 10:30", null);
            trxSplit.tambahItem(p1, 2); // 2 x 100.000 = 200.000

            // Jalur Gagal: Tunai 50.000 + QRIS 100.000 = 150.000 (< 200.000)
            SplitPayment splitGagal = new SplitPayment(50000, 100000);
            boolean lemparKurang = false;
            try {
                trxSplit.prosesTransaksi(splitGagal, 150000);
            } catch (PembayaranKurangException e) {
                lemparKurang = true;
                System.out.println("  [OK Jalur Gagal Split] Terlempar PembayaranKurangException: " + e.getMessage());
            }
            if (!lemparKurang) {
                throw new AssertionError("Gagal Uji Split: Kombinasi kurang tidak melempar exception!");
            }

            // Jalur Sukses: Tunai 120.000 + QRIS 100.000 = 220.000 (Total tagihan 200.000)
            // Kembalian harus dari tunai: 220.000 - 200.000 = 20.000
            SplitPayment splitSukses = new SplitPayment(120000, 100000);
            boolean sukses = trxSplit.prosesTransaksi(splitSukses, 220000);
            if (!sukses || !"LUNAS".equals(trxSplit.getStatusTransaksi())) {
                throw new AssertionError("Gagal Uji Split: Transaksi split tidak lunas!");
            }
            if (splitSukses.getKembalian() != 20000) {
                throw new AssertionError("Gagal Uji Split: Kembalian split salah: " + splitSukses.getKembalian());
            }
            System.out.println("  [OK Jalur Sukses Split] Lunas: " + splitSukses.getInformasiTransaksi());

            // Uji Struk teks memuat informasi metode pembayaran
            String struk = trxSplit.generateTeksStruk();
            if (!struk.contains("Split Payment: Tunai Rp 120.000 + QRIS [Simulasi] Rp 100.000")) {
                throw new AssertionError("Gagal Struk: Informasi split payment tidak tercetak di struk!");
            }
            System.out.println("  [OK] Validasi cetak teks struk memuat rincian strategi pembayaran.");
        } catch (KasirException e) {
            throw new AssertionError("Unexpected exception on split test: " + e);
        }

        System.out.println("[PASS] TestStrategyPembayaran berhasil!");
    }
}
