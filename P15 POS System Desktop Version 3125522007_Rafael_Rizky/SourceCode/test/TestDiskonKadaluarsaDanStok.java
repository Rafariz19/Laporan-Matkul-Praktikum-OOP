package test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import model.Produk;
import model.ProdukMakanan;
import model.Transaksi;
import model.exception.KasirException;
import model.exception.ProdukKadaluarsaException;

/**
 * Pengujian Fase 2:
 * 1. Logika diskon dinamis kedaluwarsa pada ProdukMakanan dengan Clock yang diinjeksi.
 * 2. Pelemparan ProdukKadaluarsaException saat komoditas kadaluarsa ditambahkan.
 * 3. Indikator ambang batas stok menipis (AMBANG_STOK_MENIPIS = 5).
 */
public class TestDiskonKadaluarsaDanStok {
    public static void main(String[] args) {
        System.out.println(">>> Menjalankan TestDiskonKadaluarsaDanStok...");

        // Base date: 2026-10-01T00:00:00Z
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        Instant baseInstant = LocalDate.of(2026, 10, 1).atStartOfDay(zone).toInstant();
        Clock clockBase = Clock.fixed(baseInstant, zone);

        // Kasus 1: Produk Makanan dengan Expired 2026-10-20 (> 14 hari, sisa 19 hari) -> Diskon 0%
        ProdukMakanan pm1 = new ProdukMakanan("MK01", "Roti Tawar A", 15000, 20, "2026-10-20");
        long sisa1 = pm1.hitungSisaHari(clockBase);
        double d1 = pm1.hitungDiskonKedaluwarsa(clockBase);
        if (sisa1 != 19 || Math.abs(d1 - 0.0) > 0.0001) {
            throw new AssertionError("Gagal Kasus 1: sisa=" + sisa1 + ", diskon=" + d1);
        }

        // Kasus 2: Expired 2026-10-15 (14 hari) -> Diskon 10%
        ProdukMakanan pm2 = new ProdukMakanan("MK02", "Roti Tawar B", 15000, 20, "2026-10-15");
        long sisa2 = pm2.hitungSisaHari(clockBase);
        double d2 = pm2.hitungDiskonKedaluwarsa(clockBase);
        if (sisa2 != 14 || Math.abs(d2 - 0.10) > 0.0001) {
            throw new AssertionError("Gagal Kasus 2 (14 hari): sisa=" + sisa2 + ", diskon=" + d2);
        }

        // Kasus 3: Expired 2026-10-09 (8 hari) -> Diskon 10%
        ProdukMakanan pm3 = new ProdukMakanan("MK03", "Roti Tawar C", 15000, 20, "2026-10-09");
        long sisa3 = pm3.hitungSisaHari(clockBase);
        double d3 = pm3.hitungDiskonKedaluwarsa(clockBase);
        if (sisa3 != 8 || Math.abs(d3 - 0.10) > 0.0001) {
            throw new AssertionError("Gagal Kasus 3 (8 hari): sisa=" + sisa3 + ", diskon=" + d3);
        }

        // Kasus 4: Expired 2026-10-08 (7 hari) -> Diskon 25%
        ProdukMakanan pm4 = new ProdukMakanan("MK04", "Susu Segar A", 20000, 10, "2026-10-08");
        long sisa4 = pm4.hitungSisaHari(clockBase);
        double d4 = pm4.hitungDiskonKedaluwarsa(clockBase);
        if (sisa4 != 7 || Math.abs(d4 - 0.25) > 0.0001) {
            throw new AssertionError("Gagal Kasus 4 (7 hari): sisa=" + sisa4 + ", diskon=" + d4);
        }

        // Kasus 5: Expired 2026-10-05 (4 hari) -> Diskon 25%
        ProdukMakanan pm5 = new ProdukMakanan("MK05", "Susu Segar B", 20000, 10, "2026-10-05");
        long sisa5 = pm5.hitungSisaHari(clockBase);
        double d5 = pm5.hitungDiskonKedaluwarsa(clockBase);
        if (sisa5 != 4 || Math.abs(d5 - 0.25) > 0.0001) {
            throw new AssertionError("Gagal Kasus 5 (4 hari): sisa=" + sisa5 + ", diskon=" + d5);
        }

        // Kasus 6: Expired 2026-10-04 (3 hari) -> Diskon 40%
        ProdukMakanan pm6 = new ProdukMakanan("MK06", "Kue Basah A", 10000, 10, "2026-10-04");
        long sisa6 = pm6.hitungSisaHari(clockBase);
        double d6 = pm6.hitungDiskonKedaluwarsa(clockBase);
        if (sisa6 != 3 || Math.abs(d6 - 0.40) > 0.0001) {
            throw new AssertionError("Gagal Kasus 6 (3 hari): sisa=" + sisa6 + ", diskon=" + d6);
        }

        // Kasus 7: Expired 2026-10-02 (1 hari) -> Diskon 40%
        ProdukMakanan pm7 = new ProdukMakanan("MK07", "Kue Basah B", 10000, 10, "2026-10-02");
        long sisa7 = pm7.hitungSisaHari(clockBase);
        double d7 = pm7.hitungDiskonKedaluwarsa(clockBase);
        if (sisa7 != 1 || Math.abs(d7 - 0.40) > 0.0001) {
            throw new AssertionError("Gagal Kasus 7 (1 hari): sisa=" + sisa7 + ", diskon=" + d7);
        }

        // Kasus 8: Hari H (2026-10-01) -> Diskon 50%
        ProdukMakanan pm8 = new ProdukMakanan("MK08", "Sandwich Hari H", 12000, 5, "2026-10-01");
        long sisa8 = pm8.hitungSisaHari(clockBase);
        double d8 = pm8.hitungDiskonKedaluwarsa(clockBase);
        if (sisa8 != 0 || Math.abs(d8 - 0.50) > 0.0001) {
            throw new AssertionError("Gagal Kasus 8 (Hari H): sisa=" + sisa8 + ", diskon=" + d8);
        }

        // Kasus 9: Sudah Kedaluwarsa (2026-09-30, -1 hari) -> Lempar ProdukKadaluarsaException di Transaksi
        ProdukMakanan pmBasi = new ProdukMakanan("MK99", "Yogurt Basi", 15000, 10, "2026-09-30");
        if (!pmBasi.isKadaluarsa(clockBase)) {
            throw new AssertionError("Gagal Kasus 9: Harus terdeteksi kadaluarsa!");
        }

        Transaksi trx = new Transaksi("TRX-TEST-EXP", "01-10-2026 09:00", null);
        boolean lemparExp = false;
        try {
            trx.tambahItem(pmBasi, 2, clockBase);
        } catch (ProdukKadaluarsaException e) {
            lemparExp = true;
            System.out.println("  [OK Jalur Gagal] Terlempar ProdukKadaluarsaException: " + e.getMessage());
        } catch (KasirException e) {
            throw new AssertionError("Melempar KasirException selain ProdukKadaluarsaException: " + e);
        }
        if (!lemparExp) {
            throw new AssertionError("Gagal Kasus 9: Produk kadaluarsa tidak melempar ProdukKadaluarsaException!");
        }
        // Pastikan stok tidak terpotong
        if (pmBasi.getStok() != 10) {
            throw new AssertionError("Gagal Kasus 9: Stok produk kadaluarsa terpotong padahal gagal!");
        }

        // Kasus 10: Tambah item valid dengan auto diskon kedaluwarsa ke Transaksi
        try {
            trx.tambahItem(pm8, 2, clockBase); // Diskon 50%: 12.000 * 50% = 6.000 per unit -> total 12.000
            if (trx.getDaftarItem().size() != 1) {
                throw new AssertionError("Gagal Kasus 10: Item tidak masuk keranjang");
            }
            int subtotal = trx.getDaftarItem().get(0).hitungSubtotal();
            if (subtotal != 12000) {
                throw new AssertionError("Gagal Kasus 10: Subtotal salah! Harusnya 12000 (diskon 50%), didapat: " + subtotal);
            }
            System.out.println("  [OK Jalur Sukses] Tambah item dengan diskon kedaluwarsa 50% berhasil. Subtotal = " + subtotal);
        } catch (KasirException e) {
            throw new AssertionError("Gagal Kasus 10: " + e.getMessage());
        }

        // Kasus 11: Uji Ambang Stok Menipis
        Produk pmStokTipis = new ProdukMakanan("MK-TP", "Susu Tipis", 10000, 5, "2026-12-31");
        Produk pmStokAman = new ProdukMakanan("MK-AM", "Susu Aman", 10000, 6, "2026-12-31");
        if (!pmStokTipis.isStokMenipis() || pmStokAman.isStokMenipis()) {
            throw new AssertionError("Gagal Kasus 11: Ambang batas stok menipis (<=5) gagal dihitung!");
        }
        System.out.println("  [OK] Pengecekan ambang stok menipis berhasil.");

        System.out.println("[PASS] TestDiskonKadaluarsaDanStok berhasil!");
    }
}
