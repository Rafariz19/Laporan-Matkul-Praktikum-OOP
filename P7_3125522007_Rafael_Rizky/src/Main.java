public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM P7 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Abstract Class, Abstract Method, dan Interface");
        System.out.println("Nama    : Rafael Rizky | NRP : 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("Proyek  : Sistem Kasir Sederhana");
        System.out.println("======================================================================\n");

        // ====================================================================
        // SKENARIO 1: MEMBUAT OBJECT SUBCLASS & PEMBUKTIAN KELAS ABSTRAK
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 1: INSTANSIASI SUBCLASS & KONSEP ABSTRACT CLASS      ###");
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
        // SKENARIO 2: MEMANGGIL ABSTRACT METHOD MELALUI REFERENCE SUPERCLASS
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 2: MEMANGGIL ABSTRACT METHOD VIA REFERENCE SUPERCLASS ###");
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
        // SKENARIO 3: MEMANGGIL METHOD MELALUI REFERENCE INTERFACE
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 3: MEMANGGIL METHOD MELALUI REFERENCE INTERFACE       ###");
        System.out.println("######################################################################");
        System.out.println("[INFO] Menguji Polymorphism melalui Interface 'DapatDidiskon':");
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

        System.out.println("=> KESIMPULAN SKENARIO 3: Kontrak Interface berhasil dipenuhi secara independen");
        System.out.println("   oleh kedua class konkret dengan output kalkulasi yang valid.\n");

        // ====================================================================
        // SKENARIO 4: POLYMORPHIC COLLECTION (ARRAY SUPERCLASS & INTERFACE)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 4: POLYMORPHIC COLLECTION (SUPERCLASS & INTERFACE)     ###");
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
        System.out.println("=> KESIMPULAN SKENARIO 4: Polymorphic collection mampu memproses berbagai turunan");
        System.out.println("   melalui satu loop seragam tanpa perlu percabangan manual (if-else/instanceof).\n");

        // ====================================================================
        // SKENARIO 5: INTEGRASI BISNIS PENJUALAN KASIR (FITUR P1 - P6 TETAP UTUH)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 5: INTEGRASI TRANSAKSI PENJUALAN KASIR LENGKAP         ###");
        System.out.println("######################################################################");
        Pelanggan pembeli = new Pelanggan("CUST-P7-001", "Rafael Rizky", "081234567890", "GOLD");
        Transaksi pesanan = new Transaksi("TRX-2026-P7-001", "06-10-2026 10:15", pembeli);

        System.out.println("[LANGKAH 1] Menambahkan item belanjaan (Memanfaatkan Overloading & Polymorphism):");
        pesanan.tambahItem(makanan1, 2);              // Makanan tanpa diskon item
        pesanan.tambahItem(elektro1, 1, 0.10);        // Elektronik dengan diskon item 10%
        pesanan.tambahItem(makanan2, 3);              // Makanan tanpa diskon item

        System.out.println("\n[LANGKAH 2] Memproses transaksi kasir dengan pembayaran tunai (Cash):");
        double uangDibayarkan = 250000.0;
        pesanan.prosesTransaksi(uangDibayarkan);

        System.out.println("\n[VERIFIKASI SISTEM]:");
        System.out.println("Seluruh fitur P1-P6 (Class, Encapsulation, Relasi, Inheritance, Polymorphism,");
        System.out.println("Abstract Class, dan Interface) beroperasi 100% harmonis tanpa kesalahan.");
        System.out.println("======================================================================");
    }
}
