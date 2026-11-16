package test;

import model.*;
import model.exception.*;

/**
 * TestEditQtyKeranjangDanKategori - Uji Otomatis Fitur Edit Qty Keranjang & Kategori Dinamis
 * 
 * Menguji:
 * 1. Peningkatan kuantitas item di keranjang secara atomik (stok fisik katalog berkurang sebanding).
 * 2. Pengurangan kuantitas item di keranjang secara atomik (stok fisik katalog bertambah/pulih sebanding).
 * 3. Proteksi stok: Permintaan kuantitas melebihi sisa stok menolak mutasi (StokHabisException) tanpa merusak status.
 * 4. Validasi kuantitas tidak valid (<= 0 atau index tidak valid).
 * 5. Kategori dinamis produk dan daftar kategori unik pada katalog.
 * 
 * Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 */
public class TestEditQtyKeranjangDanKategori {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println(">>> Menjalankan TestEditQtyKeranjangDanKategori...");

        // 1. Uji Penambahan dan Pengurangan Qty Keranjang Atomik
        KatalogProduk katalog = new KatalogProduk();
        ProdukMakanan p1 = new ProdukMakanan("MK01", "Roti Coklat", 10000, 20, "2026-12-31", 0.0, "Makanan");
        ProdukElektronik p2 = new ProdukElektronik("EL01", "Kabel Type-C", 50000, 10, 12, 0.0, "Elektronik");
        katalog.tambahProduk(p1);
        katalog.tambahProduk(p2);

        Pelanggan cust = new Pelanggan("C01", "Budi", "081234567890", "REGULER");
        Transaksi trx = new Transaksi("TRX-TEST-QTY", "16-11-2026 10:00", cust);

        try {
            // Masukkan 3 unit Roti Coklat (stok awal 20)
            trx.tambahItem(p1, 3);
            if (p1.getStok() != 17) {
                throw new AssertionError("[GAGAL] Stok p1 setelah tambah 3 harus 17, aktual: " + p1.getStok());
            }
            if (trx.getDaftarItem().get(0).getJumlahBeli() != 3) {
                throw new AssertionError("[GAGAL] Kuantitas di keranjang harus 3");
            }
            if (trx.hitungTotalSubtotal() != 30000) {
                throw new AssertionError("[GAGAL] Subtotal harus 30000");
            }
            System.out.println("  1. Tambah 3 unit awal ke keranjang berhasil (stok sisa: 17, subtotal: Rp 30.000).");

            // Naikkan kuantitas dari 3 menjadi 7 (+4 unit)
            trx.ubahKuantitasItem(0, 7);
            if (p1.getStok() != 13) {
                throw new AssertionError("[GAGAL] Stok p1 setelah naik ke 7 harus 13, aktual: " + p1.getStok());
            }
            if (trx.getDaftarItem().get(0).getJumlahBeli() != 7) {
                throw new AssertionError("[GAGAL] Kuantitas di keranjang harus 7");
            }
            if (trx.hitungTotalSubtotal() != 70000) {
                throw new AssertionError("[GAGAL] Subtotal harus 70000");
            }
            System.out.println("  2. Naikkan kuantitas dari 3 ke 7 berhasil (stok sisa: 13, subtotal: Rp 70.000).");

            // Turunkan kuantitas dari 7 menjadi 2 (-5 unit, stok pulih +5)
            trx.ubahKuantitasItem(0, 2);
            if (p1.getStok() != 18) {
                throw new AssertionError("[GAGAL] Stok p1 setelah turun ke 2 harus 18, aktual: " + p1.getStok());
            }
            if (trx.getDaftarItem().get(0).getJumlahBeli() != 2) {
                throw new AssertionError("[GAGAL] Kuantitas di keranjang harus 2");
            }
            if (trx.hitungTotalSubtotal() != 20000) {
                throw new AssertionError("[GAGAL] Subtotal harus 20000");
            }
            System.out.println("  3. Turunkan kuantitas dari 7 ke 2 berhasil (stok pulih ke 18, subtotal: Rp 20.000).");

            // 2. Proteksi Stok Habis / Over-Capacity
            // Stok sisa di rak = 18. Di keranjang = 2. Total maksimal = 20.
            // Coba ubah kuantitas ke 25 (melebihi kapasitas 20).
            boolean caughtStokHabis = false;
            try {
                trx.ubahKuantitasItem(0, 25);
            } catch (StokTidakCukupException ex) {
                caughtStokHabis = true;
            }
            if (!caughtStokHabis) {
                throw new AssertionError("[GAGAL] Permintaan ubah qty melebihi stok harus melempar StokTidakCukupException!");
            }
            // Pastikan invariant tidak korup: qty tetap 2, stok tetap 18
            if (p1.getStok() != 18 || trx.getDaftarItem().get(0).getJumlahBeli() != 2) {
                throw new AssertionError("[GAGAL] Mutasi gagal tidak boleh mengubah stok atau kuantitas!");
            }
            System.out.println("  4. Proteksi over-capacity terverifikasi: Permintaan qty > stok ditolak secara atomik.");

            // 3. Validasi Batas Negatif / Nol
            boolean caughtInvalidQty = false;
            try {
                trx.ubahKuantitasItem(0, 0);
            } catch (IllegalArgumentException ex) {
                caughtInvalidQty = true;
            }
            if (!caughtInvalidQty) {
                throw new AssertionError("[GAGAL] Kuantitas 0 harus melempar IllegalArgumentException!");
            }
            System.out.println("  5. Validasi batas kuantitas <= 0 terverifikasi.");

            // 4. Kategori Dinamis & Pencarian
            if (!"Makanan".equalsIgnoreCase(p1.getKategori())) {
                throw new AssertionError("[GAGAL] Kategori p1 harus 'Makanan'");
            }
            if (!"Elektronik".equalsIgnoreCase(p2.getKategori())) {
                throw new AssertionError("[GAGAL] Kategori p2 harus 'Elektronik'");
            }

            p1.ubahKategori("Minuman Dingin");
            if (!"Minuman Dingin".equalsIgnoreCase(p1.getKategori())) {
                throw new AssertionError("[GAGAL] Kategori p1 harus berubah ke 'Minuman Dingin'");
            }

            java.util.List<String> kategoriUnik = katalog.getDaftarKategoriUnik();
            if (!kategoriUnik.contains("Minuman Dingin") || !kategoriUnik.contains("Elektronik")) {
                throw new AssertionError("[GAGAL] getDaftarKategoriUnik harus memuat 'Minuman Dingin' dan 'Elektronik'");
            }
            System.out.println("  6. Kategori dinamis, mutasi, dan pencarian kategori unik terverifikasi.");

            System.out.println("[PASS] TestEditQtyKeranjangDanKategori berhasil!\n");
        } catch (KasirException e) {
            throw new RuntimeException("Unexpected exception: " + e.getMessage(), e);
        }
    }
}
