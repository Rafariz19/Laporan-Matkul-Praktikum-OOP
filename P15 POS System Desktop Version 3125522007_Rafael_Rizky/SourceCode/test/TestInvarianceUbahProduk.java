package test;

import model.*;
import model.exception.*;

/**
 * Pengujian 4: Atomic Pre-Validation pada KatalogProduk.ubahProduk
 * Membuktikan bahwa state lama objek tidak termutasi sebagian jika ada parameter masukan yang cacat.
 */
public class TestInvarianceUbahProduk {
    public static void run() {
        System.out.println("--- [TEST 4] Atomic Pre-Validation: KatalogProduk.ubahProduk ---");
        KatalogProduk katalog = new KatalogProduk();
        Produk makanan = new ProdukMakanan("MK01", "Roti Gandum", 10000, 20, "2026-12-31");
        Produk elektro = new ProdukElektronik("EL01", "Mouse Optik", 50000, 15, 12);
        katalog.tambahProduk(makanan);
        katalog.tambahProduk(elektro);

        // Uji 4A: Tanggal kadaluarsa kosong
        boolean err1 = false;
        try {
            katalog.ubahProduk("MK01", "Roti Cacat Input", 25000, 99, "   ");
        } catch (IllegalArgumentException e) {
            err1 = true;
        }
        Produk m = katalog.cariProduk("MK01");
        boolean mUtuh = "Roti Gandum".equals(m.getNama()) && m.getHarga() == 10000 && m.getStok() == 20;

        // Uji 4B: Garansi non-numerik
        boolean err2 = false;
        try {
            katalog.ubahProduk("EL01", "Mouse Rusak", 99000, 100, "bukan_angka");
        } catch (IllegalArgumentException e) {
            err2 = true;
        }
        Produk prodE = katalog.cariProduk("EL01");
        boolean eUtuh = "Mouse Optik".equals(prodE.getNama()) && prodE.getHarga() == 50000 && prodE.getStok() == 15;

        // Uji 4C: Garansi negatif
        boolean err3 = false;
        try {
            katalog.ubahProduk("EL01", "Mouse Rusak", 99000, 100, "-6");
        } catch (IllegalArgumentException ex) {
            err3 = true;
        }
        boolean eUtuh2 = "Mouse Optik".equals(prodE.getNama()) && ((ProdukElektronik) prodE).getGaransiBulan() == 12;

        if (err1 && mUtuh && err2 && eUtuh && err3 && eUtuh2) {
            System.out.println("✔ [LULUS] Semua pre-validasi terbukti tuntas sebelum setter pertama dieksekusi.\n");
        } else {
            System.err.println("✘ [GAGAL TEST 4] State objek terlanjur termutasi parsial!");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
