package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import model.*;
import model.exception.KasirException;

/**
 * Suite Pengujian Fitur: Promo Barang Tertentu (Diskon Manual Per Komoditas)
 * Memvalidasi:
 * 1. Enkapsulasi & Invariant Precondition diskonPromo pada model Produk, ProdukMakanan, ProdukElektronik.
 * 2. Transmisi otomatis diskon promo ke ItemTransaksi saat produk dimasukkan ke keranjang kasir.
 * 3. Logika kalkulasi diskon bertingkat (Promo Item diproses sebelum Diskon Member).
 * 4. Sinergi hierarki diskon (Promo Toko vs Diskon Dinamis Kedaluwarsa - Math.max).
 * 5. Atomic Invariant pada KatalogProduk.ubahProduk saat pembaharuan promo item.
 * 6. Persistensi File ProdukFileRepository: Dukungan 7 kolom dan backward-compatibility 6 kolom.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class TestPromoBarangTertentu {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println(">>> Menjalankan TestPromoBarangTertentu...");

        // 1. UJI PREKONDISI & ENKAPSULASI MODEL
        ProdukMakanan donat = new ProdukMakanan("MK01", "Donat Coklat", 5000, 50, "2026-12-31", 0.10);
        ProdukElektronik mouse = new ProdukElektronik("EL01", "Mouse Wireless", 100000, 20, 12, 0.20);

        if (Math.abs(donat.getDiskonPromo() - 0.10) > 0.0001) {
            throw new AssertionError("[GAGAL] Diskon promo donat salah: " + donat.getDiskonPromo());
        }
        if (Math.abs(mouse.getDiskonPromo() - 0.20) > 0.0001) {
            throw new AssertionError("[GAGAL] Diskon promo mouse salah: " + mouse.getDiskonPromo());
        }

        boolean errNegatif = false;
        try {
            donat.ubahDiskonPromo(-0.05);
        } catch (IllegalArgumentException e) {
            errNegatif = true;
        }
        if (!errNegatif || Math.abs(donat.getDiskonPromo() - 0.10) > 0.0001) {
            throw new AssertionError("[GAGAL] Validasi diskon negatif bocor!");
        }

        boolean errLebih100 = false;
        try {
            mouse.ubahDiskonPromo(1.25);
        } catch (IllegalArgumentException e) {
            errLebih100 = true;
        }
        if (!errLebih100 || Math.abs(mouse.getDiskonPromo() - 0.20) > 0.0001) {
            throw new AssertionError("[GAGAL] Validasi diskon > 100% bocor!");
        }
        System.out.println("  1. Validasi batas diskon promo (0.0 s/d 1.0) pada model terverifikasi.");

        // 2. UJI TRANSMISI DISKON KE ITEM TRANSAKSI & SUBROW KALKULASI
        Pelanggan custUmum = new Pelanggan("CUST-01", "Budi", "081234567890", "REGULER");
        Transaksi trx1 = new Transaksi("TRX-PROMO-01", "16-11-2026 10:00", custUmum);

        try {
            trx1.tambahItem(donat, 5); // 5 x 5.000 = 25.000, diskon 10% = 2.500 -> net = 22.500
        } catch (KasirException e) {
            throw new AssertionError("[GAGAL] Gagal tambah item donat: " + e.getMessage());
        }

        if (trx1.getDaftarItem().size() != 1) {
            throw new AssertionError("[GAGAL] Item tidak masuk ke keranjang!");
        }
        ItemTransaksi itemDonat = trx1.getDaftarItem().get(0);
        if (Math.abs(itemDonat.getDiskonItem() - 0.10) > 0.0001) {
            throw new AssertionError("[GAGAL] Diskon item tidak terisi dari produk: " + itemDonat.getDiskonItem());
        }
        if (itemDonat.hitungSubtotalGross() != 25000) {
            throw new AssertionError("[GAGAL] Gross item salah: " + itemDonat.hitungSubtotalGross());
        }
        if (itemDonat.hitungSubtotal() != 22500) {
            throw new AssertionError("[GAGAL] Subtotal bersih salah: " + itemDonat.hitungSubtotal());
        }
        System.out.println("  2. Transmisi diskon promo ke ItemTransaksi (5 x 5.000 - 10% = Rp 22.500) terverifikasi.");

        // 3. UJI DISKON BERTINGKAT (PROMO ITEM + DISKON MEMBER VIP 15%)
        Pelanggan custVip = new Pelanggan("CUST-VIP", "Siti", "081987654321", "VIP");
        Transaksi trxVip = new Transaksi("TRX-PROMO-VIP", "16-11-2026 10:30", custVip);

        try {
            trxVip.tambahItem(donat, 5); // Subtotal kotor transaksi = Rp 22.500
        } catch (KasirException e) {
            throw new AssertionError("[GAGAL] Tambah item VIP: " + e.getMessage());
        }

        int totalKotor = trxVip.hitungTotalSubtotal();
        double diskonMember = trxVip.hitungDiskonNominal(); // 15% dari 22.500 = 3.375
        double tagihanAkhir = trxVip.hitungTotalBayar();    // 22.500 - 3.375 = 19.125

        if (totalKotor != 22500) {
            throw new AssertionError("[GAGAL] Total kotor salah: " + totalKotor);
        }
        if (Math.abs(diskonMember - 3375.0) > 0.001) {
            throw new AssertionError("[GAGAL] Diskon member salah: " + diskonMember);
        }
        if (Math.abs(tagihanAkhir - 19125.0) > 0.001) {
            throw new AssertionError("[GAGAL] Tagihan akhir bertingkat salah: " + tagihanAkhir);
        }
        System.out.println("  3. Diskon bertingkat terverifikasi: Kotor Rp 22.500 - VIP 15% (Rp 3.375) = Rp 19.125.");

        // 4. UJI SINERGI DISKON TOKO VS KEDALUWARSA (Math.max)
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        Instant baseInstant = LocalDate.of(2026, 10, 1).atStartOfDay(zone).toInstant();
        Clock clockBase = Clock.fixed(baseInstant, zone);

        // Kasus 4A: Promo Toko 10%, tapi Hari H Expired (Diskon Exp 50%) -> Sistem memilih 50%
        ProdukMakanan rotiHariH = new ProdukMakanan("MK-H0", "Roti H-0", 10000, 10, "2026-10-01", 0.10);
        Transaksi trxExp1 = new Transaksi("TRX-EXP-01", "01-10-2026 08:00", custUmum);
        try {
            trxExp1.tambahItem(rotiHariH, 1, clockBase);
            ItemTransaksi it = trxExp1.getDaftarItem().get(0);
            if (Math.abs(it.getDiskonItem() - 0.50) > 0.0001) {
                throw new AssertionError("[GAGAL] Seharusnya memilih diskon kedaluwarsa 50% daripada promo toko 10%!");
            }
        } catch (KasirException e) {
            throw new AssertionError("[GAGAL] Tambah item rotiHariH: " + e.getMessage());
        }

        // Kasus 4B: Promo Toko 30%, tapi Kedaluwarsa 8 hari lagi (Diskon Exp 10%) -> Sistem memilih 30%
        ProdukMakanan rotiPromoBesar = new ProdukMakanan("MK-P30", "Roti Promo 30%", 10000, 10, "2026-10-09", 0.30);
        Transaksi trxExp2 = new Transaksi("TRX-EXP-02", "01-10-2026 08:00", custUmum);
        try {
            trxExp2.tambahItem(rotiPromoBesar, 1, clockBase);
            ItemTransaksi it = trxExp2.getDaftarItem().get(0);
            if (Math.abs(it.getDiskonItem() - 0.30) > 0.0001) {
                throw new AssertionError("[GAGAL] Seharusnya memilih promo toko 30% daripada diskon kedaluwarsa 10%!");
            }
        } catch (KasirException e) {
            throw new AssertionError("[GAGAL] Tambah item rotiPromoBesar: " + e.getMessage());
        }
        System.out.println("  4. Sinergi hierarki diskon (Math.max antara Promo Toko vs Kedaluwarsa) terverifikasi.");

        // 5. UJI ATOMIC INVARIANCE PADA KatalogProduk.ubahProduk
        KatalogProduk katalog = new KatalogProduk();
        katalog.tambahProduk(donat);

        boolean errUbahPromo = false;
        try {
            katalog.ubahProduk("MK01", "Donat Coklat", 5000, 50, "2026-12-31", -0.20);
        } catch (IllegalArgumentException e) {
            errUbahPromo = true;
        }
        if (!errUbahPromo || Math.abs(donat.getDiskonPromo() - 0.10) > 0.0001) {
            throw new AssertionError("[GAGAL] Atomic invariance gagal: Diskon promo berubah padahal input cacat!");
        }

        katalog.ubahProduk("MK01", "Donat Coklat Special", 6000, 45, "2026-12-31", 0.25);
        if (Math.abs(donat.getDiskonPromo() - 0.25) > 0.0001 || donat.getHarga() != 6000) {
            throw new AssertionError("[GAGAL] Ubah produk dengan promo baru gagal!");
        }
        System.out.println("  5. Atomic Invariance & Pembaruan Promo pada KatalogProduk terverifikasi.");

        // 6. UJI PERSISTENSI FILE & BACKWARD COMPATIBILITY
        try {
            Path tempFile = Files.createTempFile("test_produk_promo_", ".txt");
            tempFile.toFile().deleteOnExit();

            // 6A. Simpan katalog dengan promo ke format 7 kolom
            ProdukFileRepository repo = new ProdukFileRepository();
            repo.simpan(tempFile, katalog);

            List<String> lines = Files.readAllLines(tempFile);
            if (lines.size() != 1 || !lines.get(0).endsWith("|0.25")) {
                throw new AssertionError("[GAGAL] Format penyimpanan 7-kolom salah: " + lines);
            }

            // 6B. Muat ulang dari berkas 7 kolom
            KatalogProduk kMuat = new KatalogProduk();
            repo.muat(tempFile, kMuat);
            Produk pMuat = kMuat.cariProduk("MK01");
            if (pMuat == null || Math.abs(pMuat.getDiskonPromo() - 0.25) > 0.0001) {
                throw new AssertionError("[GAGAL] Gagal membaca kembali diskon promo dari file!");
            }

            // 6C. Backward compatibility: Tulis file 6-kolom lama dan pastikan bisa dibaca tanpa error
            List<String> legacyLines = Arrays.asList(
                "LG01|Kopi Susu Legasi|8000|30|Makanan|2026-12-31",
                "LG02|Flashdisk Legasi|75000|15|Elektronik|24"
            );
            Files.write(tempFile, legacyLines);

            KatalogProduk kLegacy = new KatalogProduk();
            repo.muat(tempFile, kLegacy);
            if (kLegacy.getJumlahProduk() != 2) {
                throw new AssertionError("[GAGAL] Gagal membaca berkas legasi 6-kolom!");
            }
            if (kLegacy.cariProduk("LG01").getDiskonPromo() != 0.0 || kLegacy.cariProduk("LG02").getDiskonPromo() != 0.0) {
                throw new AssertionError("[GAGAL] Diskon default berkas legasi harus 0.0!");
            }

            Files.deleteIfExists(tempFile);
            System.out.println("  6. Persistensi berkas 7-kolom dan Backward-Compatibility 6-kolom terverifikasi 100%.");
        } catch (IOException e) {
            throw new AssertionError("[GAGAL] I/O Exception saat uji file: " + e.getMessage());
        }

        System.out.println("[PASS] TestPromoBarangTertentu berhasil!\n");
    }
}
