import model.DapatDidiskon;
import model.ItemTransaksi;
import model.Pelanggan;
import model.Produk;
import model.ProdukElektronik;
import model.ProdukMakanan;
import model.Transaksi;

/**
 * Main Class - Program Pengujian Sistem Kasir Sederhana
 * Praktikum PBO Modul 9: "Implementasi UML Class Diagram ke Program Java"
 * 
 * Mahasiswa : Rafael Rizky Luga Tanjung
 * NRP       : 3125522007
 * Kampus    : PENS PSDKU Sumenep (2026)
 * Proyek    : Sistem Kasir Sederhana
 * 
 * Pengujian mencakup:
 * - Seluruh 5 Skenario P7 (Tanpa Regresi)
 * - Skenario 6: Verifikasi Relasi Antarobjek (Association, Aggregation, Composition)
 * - Skenario 7: Verifikasi Validasi State Gagal & Umpan Balik Robustness
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM P9 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Implementasi UML Class Diagram ke Program Java");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP : 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("Proyek  : Sistem Kasir Sederhana (Refaktorisasi UML Model 9)");
        System.out.println("======================================================================\n");

        // ====================================================================
        // SKENARIO 1: MEMBUAT OBJECT SUBCLASS & PEMBUKTIAN KELAS ABSTRAK (P7)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 1: INSTANSIASI SUBCLASS & KONSEP ABSTRACT CLASS       ###");
        System.out.println("######################################################################");
        System.out.println("[AUDIT DESAIN]:");
        System.out.println("1. Class 'Produk' dideklarasikan sebagai 'public abstract class'.");
        System.out.println("2. 'new Produk(...)' DILARANG oleh compiler Java (Cannot instantiate the type Produk).");
        System.out.println("   Hal ini menjamin integritas bisnis: tidak ada produk fiktif tanpa kategori nyata.");
        System.out.println("3. Instansiasi HANYA dapat dilakukan melalui Subclass konkret:\n");

        ProdukMakanan makanan1 = new ProdukMakanan("M01", "Roti Gandum Sehat", 18000, 25, "25-10-2026");
        ProdukMakanan makanan2 = new ProdukMakanan("M02", "Susu UHT Cokelat 1L", 20000, 40, "15-12-2026");
        ProdukElektronik elektro1 = new ProdukElektronik("E01", "Mouse Wireless Ergonomis", 150000, 15, 12);
        ProdukElektronik elektro2 = new ProdukElektronik("E02", "Keyboard Mechanical TKL", 450000, 8, 24);

        System.out.println("[BERHASIL] Objek Subclass 1 (ProdukMakanan) berhasil diinstansiasi: " + makanan1.getNama());
        System.out.println("[BERHASIL] Objek Subclass 2 (ProdukElektronik) berhasil diinstansiasi: " + elektro1.getNama());
        System.out.println();

        // ====================================================================
        // SKENARIO 2: MEMANGGIL ABSTRACT METHOD MELALUI REFERENCE SUPERCLASS (P7)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 2: MEMANGGIL ABSTRACT METHOD VIA REFERENCE SUPERCLASS  ###");
        System.out.println("######################################################################");
        System.out.println("[INFO] Upcasting: Menyimpan referensi Subclass ke variabel tipe Abstract Class Produk.");
        Produk refProduk1 = makanan1;   // Upcasting ProdukMakanan -> Produk
        Produk refProduk2 = elektro1;  // Upcasting ProdukElektronik -> Produk

        System.out.println("\n--- Pengujian Objek 1 via Referensi 'Produk' (Objek Aktual: ProdukMakanan) ---");
        System.out.println("Method getKategoriInfo() : " + refProduk1.getKategoriInfo());
        System.out.println("Memanggil refProduk1.tampilkanData():");
        refProduk1.tampilkanData();

        System.out.println("\n--- Pengujian Objek 2 via Referensi 'Produk' (Objek Aktual: ProdukElektronik) ---");
        System.out.println("Method getKategoriInfo() : " + refProduk2.getKategoriInfo());
        System.out.println("Memanggil refProduk2.tampilkanData():");
        refProduk2.tampilkanData();

        System.out.println("=> KESIMPULAN SKENARIO 2: Dynamic Binding terbukti! JVM mengeksekusi abstract method");
        System.out.println("   dan detail khusus yang dioverride oleh masing-masing subclass saat runtime.\n");

        // ====================================================================
        // SKENARIO 3: MEMANGGIL METHOD MELALUI REFERENCE INTERFACE (P7)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 3: MEMANGGIL METHOD MELALUI REFERENCE INTERFACE        ###");
        System.out.println("######################################################################");
        System.out.println("[INFO] Menguji Polymorphism melalui Interface 'DapatDidiskon' (Default Methods):");
        DapatDidiskon diskonable1 = makanan1;   // Interface reference ke ProdukMakanan
        DapatDidiskon diskonable2 = elektro1;  // Interface reference ke ProdukElektronik

        double promoMakanan = 10.0; // Promo Diskon 10%
        double promoElektro = 15.0; // Promo Diskon 15%

        System.out.println("\n1. Menguji Objek Makanan via 'DapatDidiskon':");
        System.out.println("   Produk                     : " + makanan1.getNama());
        System.out.println("   Harga Asli                 : Rp " + String.format("%,d", makanan1.getHarga()));
        System.out.printf("   Persentase Diskon          : %.0f%%\n", promoMakanan);
        System.out.printf("   Nominal Diskon (dihitung)  : Rp %,.0f\n", diskonable1.hitungDiskon(promoMakanan));
        System.out.printf("   Harga Akhir Setelah Diskon : Rp %,.0f\n", diskonable1.getHargaSetelahDiskon(promoMakanan));

        System.out.println("\n2. Menguji Objek Elektronik via 'DapatDidiskon':");
        System.out.println("   Produk                     : " + elektro1.getNama());
        System.out.println("   Harga Asli                 : Rp " + String.format("%,d", elektro1.getHarga()));
        System.out.printf("   Persentase Diskon          : %.0f%%\n", promoElektro);
        System.out.printf("   Nominal Diskon (dihitung)  : Rp %,.0f\n", diskonable2.hitungDiskon(promoElektro));
        System.out.printf("   Harga Akhir Setelah Diskon : Rp %,.0f\n", diskonable2.getHargaSetelahDiskon(promoElektro));

        System.out.println("=> KESIMPULAN SKENARIO 3: Kontrak Interface dipenuhi tanpa duplikasi kode berkat");
        System.out.println("   penerapan default methods pada interface DapatDidiskon.\n");

        // ====================================================================
        // SKENARIO 4: POLYMORPHIC COLLECTION (ARRAY SUPERCLASS & INTERFACE) (P7)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 4: POLYMORPHIC COLLECTION (SUPERCLASS & INTERFACE)      ###");
        System.out.println("######################################################################");
        System.out.println("[A] Iterasi Array Bertipe Superclass Abstract Produk[]:");
        Produk[] inventarisToko = { makanan1, elektro1, makanan2, elektro2 };

        int index = 1;
        for (Produk p : inventarisToko) {
            System.out.println("\n[Barang Koleksi #" + (index++) + "]");
            p.tampilkanData(); // Polimorfik: format tampilan menyesuaikan subclass konkret
        }

        System.out.println("\n[B] Iterasi Array Bertipe Interface DapatDidiskon[]:");
        DapatDidiskon[] itemDiskonList = { makanan1, elektro1, makanan2, elektro2 };
        double diskonKolektif = 20.0; // Flash Sale Diskon 20%

        System.out.printf("Simulasi Promo Flash Sale Serentak Diskon %.0f%%:\n", diskonKolektif);
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-28s | %-14s | %-14s | %-14s\n", "No", "Nama Produk", "Harga Asli", "Potongan", "Harga Promo");
        System.out.println("----------------------------------------------------------------------------------");
        for (int i = 0; i < itemDiskonList.length; i++) {
            Produk p = inventarisToko[i];
            DapatDidiskon d = itemDiskonList[i];
            double pot = d.hitungDiskon(diskonKolektif);
            double nett = d.getHargaSetelahDiskon(diskonKolektif);
            System.out.printf("%-4d | %-28s | Rp %,11d | Rp %,11.0f | Rp %,11.0f\n",
                    (i + 1), p.getNama(), p.getHarga(), pot, nett);
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("=> KESIMPULAN SKENARIO 4: Polymorphic collection mampu memproses seluruh turunan");
        System.out.println("   melalui satu antarmuka generik tanpa percabangan manual.\n");

        // ====================================================================
        // SKENARIO 5: INTEGRASI BISNIS PENJUALAN KASIR (P7 TETAP UTUH)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 5: INTEGRASI TRANSAKSI PENJUALAN KASIR LENGKAP          ###");
        System.out.println("######################################################################");
        Pelanggan pembeli = new Pelanggan("CUST-P7-001", "Rafael Rizky", "081234567890", "GOLD");
        Transaksi pesanan = new Transaksi("TRX-2026-P7-001", "20-10-2026 10:15", pembeli);

        System.out.println("[LANGKAH 1] Menambahkan item belanjaan (Memanfaatkan Overloading & Polymorphism):");
        pesanan.tambahItem(makanan1, 2);              // Makanan tanpa diskon item
        pesanan.tambahItem(elektro1, 1, 0.10);        // Elektronik dengan diskon item 10%
        pesanan.tambahItem(makanan2, 3);              // Makanan tanpa diskon item

        System.out.println("\n[LANGKAH 2] Memproses transaksi kasir dengan pembayaran tunai (Cash):");
        double uangDibayarkan = 250000.0;
        pesanan.prosesTransaksi(uangDibayarkan);
        System.out.println();

        // ====================================================================
        // SKENARIO 6: RELASI ANTAROBJEK (ASSOCIATION, AGGREGATION, COMPOSITION)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 6: VERIFIKASI RELASI ANTAROBJEK (MODUL 9)              ###");
        System.out.println("######################################################################");

        System.out.println("[A] UJI ASOSIASI (Transaksi --> Pelanggan):");
        System.out.println("1. Transaksi Member VIP:");
        Pelanggan vipCust = new Pelanggan("CUST-VIP-09", "Dr. Budi Santoso", "081399887766", "VIP");
        Transaksi trxVIP = new Transaksi("TRX-2026-M9-001", "20-10-2026 14:00", vipCust);
        trxVIP.tambahItem(elektro2, 1); // Beli Keyboard Rp 450.000
        System.out.printf("   Total Gross: Rp %,d | Diskon Member VIP (15%%): Rp %,.0f | Tagihan: Rp %,.0f\n",
                trxVIP.hitungTotalSubtotal(), trxVIP.hitungDiskonNominal(), trxVIP.hitungTotalBayar());

        System.out.println("\n2. Transaksi Non-Member / Pelanggan Umum (Asosiasi Opsional / Null):");
        Transaksi trxUmum = new Transaksi("TRX-2026-M9-002", "20-10-2026 14:15", null);
        trxUmum.tambahItem(makanan1, 3);
        System.out.printf("   Total Gross: Rp %,d | Diskon Member: Rp %,.0f | Tagihan: Rp %,.0f\n",
                trxUmum.hitungTotalSubtotal(), trxUmum.hitungDiskonNominal(), trxUmum.hitungTotalBayar());

        System.out.println("\n[B] UJI AGREGASI (ItemTransaksi o--> Produk):");
        System.out.println("Objek Produk bersifat independen. Stok sebelum transaksi: " + makanan1.getStok());
        System.out.println("ItemTransaksi hanya memegang referensi polymorphic ke Produk.");

        System.out.println("\n[C] UJI KOMPOSISI (Transaksi *--> ItemTransaksi):");
        System.out.println("ItemTransaksi diciptakan dan dikendalikan penuh di dalam Transaksi.");
        System.out.println("Jumlah item di trxVIP: " + trxVIP.getDaftarItem().size());
        for (ItemTransaksi item : trxVIP.getDaftarItem()) {
            System.out.print("   ");
            item.tampilkanItem();
        }
        System.out.println("=> KESIMPULAN SKENARIO 6: Ketiga pola relasi (Asosiasi, Agregasi, Komposisi)");
        System.out.println("   terbukti terimplementasi secara akurat sesuai kaidah UML Class Diagram.\n");

        // ====================================================================
        // SKENARIO 7: UJI VALIDASI STATE & UMPAN BALIK KEGAGALAN (MODUL 9)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 7: VALIDASI STATE & UMPAN BALIK KEGAGALAN (MODUL 9)   ###");
        System.out.println("######################################################################");

        System.out.println("1. Pengujian Validasi Harga Negatif (Produk.ubahHarga):");
        boolean hasilUbahHarga = makanan1.ubahHarga(-50000);
        System.out.println("   Hasil ubahHarga(-50000) : " + hasilUbahHarga + " (Ditolak dengan aman)");
        System.out.println("   Harga aktual sekarang   : Rp " + makanan1.getHarga());

        System.out.println("\n2. Pengujian Validasi Penambahan Stok Negatif (Produk.tambahStok):");
        boolean hasilTambahStok = makanan1.tambahStok(-10);
        System.out.println("   Hasil tambahStok(-10)   : " + hasilTambahStok + " (Ditolak dengan aman)");

        System.out.println("\n3. Pengujian Pembelian Melebihi Stok Tersedia (Transaksi.tambahItem):");
        System.out.println("   Stok Keyboard Mechanical saat ini: " + elektro2.getStok() + " pcs.");
        boolean hasilBeliOver = trxVIP.tambahItem(elektro2, 999);
        System.out.println("   Hasil tambahItem(elektro2, 999): " + hasilBeliOver + " (Ditolak tanpa merusak data)");
        System.out.println("   Stok Keyboard setelah penolakan: " + elektro2.getStok() + " pcs.");

        System.out.println("\n4. Pengujian Pembayaran Tunai Tidak Mencukupi (Underpayment):");
        System.out.printf("   Total tagihan belanja trxVIP    : Rp %,.0f\n", trxVIP.hitungTotalBayar());
        boolean hasilBayarKurang = trxVIP.prosesTransaksi(100000.0); // Uang kurang
        System.out.println("   Hasil pembayaran Rp 100.000     : " + hasilBayarKurang + " (Status: " + trxVIP.getStatusTransaksi() + ")");

        System.out.println("\n5. Pelunasan Transaksi yang Tertunda:");
        boolean hasilBayarLunas = trxVIP.prosesTransaksi(400000.0); // Uang mencukupi
        System.out.println("   Hasil pembayaran Rp 400.000     : " + hasilBayarLunas + " (Status: " + trxVIP.getStatusTransaksi() + ")");

        System.out.println("\n6. Pengujian Validasi Mutasi Data Pelanggan (Format Nomor HP):");
        boolean hasilUbahHP = vipCust.ubahNomorHP("nomor_palsu_xyz");
        System.out.println("   Hasil ubahNomorHP('nomor_palsu_xyz'): " + hasilUbahHP + " (Ditolak)");
        System.out.println("   Nomor HP tersimpan saat ini          : " + vipCust.getNomorHP());

        System.out.println("\n======================================================================");
        System.out.println("SELURUH 7 SKENARIO PENGUJIAN PRAKTIKUM P9 TELAH SELESAI DIEKSEKUSI");
        System.out.println("Program berjalan 100% konsisten terhadap spesifikasi UML Class Diagram.");
        System.out.println("======================================================================");
    }
}
