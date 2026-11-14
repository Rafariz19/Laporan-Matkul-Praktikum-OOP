package test;

import model.*;
import model.exception.*;

/**
 * Pengujian 5: Pemulihan Stok Saat Reset / Pembatalan Transaksi
 * Memanggil kode produksi asli trx.batalkanTransaksi()
 */
public class TestAlurStokResetTransaksi {
    public static void run() {
        System.out.println("--- [TEST 5] Pemulihan Stok Saat Reset / Pembatalan Transaksi ---");
        KatalogProduk katalog = new KatalogProduk();
        Produk p = new ProdukElektronik("EL05", "Mouse Wireless", 100000, 10, 12);
        katalog.tambahProduk(p);

        Pelanggan cust = new Pelanggan("CUST-05", "Rizky", "081234567890", "REGULER");
        Transaksi trx = new Transaksi("TRX-05", "09-11-2026 14:00", cust);

        try {
            trx.tambahItem(p, 5); // Stok tersisa 5 di RAM
            if (p.getStok() != 5) {
                throw new AssertionError("Stok awal gagal berkurang di RAM!");
            }

            trx.batalkanTransaksi(); // Semua stok dipulihkan, status DIBATALKAN

            if (p.getStok() != 10) {
                throw new AssertionError("Pemulihan stok gagal! Stok saat ini: " + p.getStok());
            }

            if (!"DIBATALKAN".equals(trx.getStatusTransaksi())) {
                throw new AssertionError("Status transaksi bukan DIBATALKAN!");
            }

            boolean exceptionDilempar = false;
            try {
                trx.tambahItem(p, 1);
            } catch (StatusTransaksiInvalidException e) {
                exceptionDilempar = true;
            }

            if (!exceptionDilempar) {
                throw new AssertionError("Transaksi DIBATALKAN seharusnya tidak bisa dimutasi lagi!");
            }

            System.out.println("✔ [LULUS] Pemulihan stok dan kekekalan status DIBATALKAN terverifikasi penuh.\n");
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 5] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
