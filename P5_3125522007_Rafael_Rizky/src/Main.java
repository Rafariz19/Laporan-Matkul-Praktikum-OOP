public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("PRAKTIKUM P5 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Inheritance, Generalization, Superclass, dan Subclass");
        System.out.println("Nama : Rafael Rizky | NRP : 3125522007");
        System.out.println("Proyek : Sistem Kasir Sederhana");
        System.out.println("==========================================================\n");

        // =========================================================
        // BAGIAN 1: PENGUJIAN INSTANSIASI SUBCLASS & PENGGUNAAN super()
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 1. PENGUJIAN INSTANSIASI SUBCLASS DENGAN super()   ###");
        System.out.println("##########################################################");

        System.out.println("[INFO] Menginstansiasi Subclass 1: ProdukMakanan...");
        ProdukMakanan makanan1 = new ProdukMakanan("M001", "Ayam Geprek Crispy", 15000, 25, "25-09-2026");
        ProdukMakanan makanan2 = new ProdukMakanan("M002", "Bebek Bakar Madu", 28000, 15, "24-09-2026");

        System.out.println("\n[INFO] Menginstansiasi Subclass 2: ProdukElektronik...");
        ProdukElektronik elektronik1 = new ProdukElektronik("E001", "Kabel Data Type-C Fast", 25000, 40, 6);
        ProdukElektronik elektronik2 = new ProdukElektronik("E002", "Powerbank 10000mAh", 120000, 10, 12);

        System.out.println("\n[INFO] Menampilkan informasi produk menggunakan method subclass:");
        makanan1.tampilkanData();
        makanan2.tampilkanData();
        elektronik1.tampilkanData();
        elektronik2.tampilkanData();

        // =========================================================
        // BAGIAN 2: BUKTI AKSES MEMBER SUPERCLASS DARI SUBCLASS
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 2. BUKTI AKSES MEMBER SUPERCLASS DARI SUBCLASS     ###");
        System.out.println("##########################################################");
        System.out.println("Nama Makanan 1 (via getNama() Superclass) : " + makanan1.getNama());
        System.out.println("Harga Makanan 1 (via getHarga() Superclass): Rp" + makanan1.getHarga());
        System.out.println("Tanggal Kadaluarsa (via Subclass Khusus)  : " + makanan1.getTanggalKadaluarsa());
        System.out.println("Inventaris Makanan 1 (hitungNilaiInventaris): Rp" + makanan1.hitungNilaiInventaris());

        System.out.println("\nNama Elektronik 2 (via getNama() Superclass) : " + elektronik2.getNama());
        System.out.println("Harga Elektronik 2 (via getHarga() Superclass): Rp" + elektronik2.getHarga());
        System.out.println("Masa Garansi (via Subclass Khusus)           : " + elektronik2.getGaransiBulan() + " Bulan");
        System.out.println("Inventaris Elektronik 2 (hitungNilaiInventaris): Rp" + elektronik2.hitungNilaiInventaris());
        System.out.println();

        // =========================================================
        // BAGIAN 3: PENGUJIAN INTEGRASI TRANSAKSI (MEMPERTAHANKAN P4)
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 3. INTEGRASI TRANSAKSI MULTI-KATEGORI (RELASI P4)  ###");
        System.out.println("### (Association, Aggregation, dan Composition)        ###");
        System.out.println("##########################################################");

        // 1. Pelanggan (Association)
        Pelanggan pelangganVIP = new Pelanggan("C001", "Rafael Rizky", "081234567890", "VIP");
        Pelanggan pelangganGold = new Pelanggan("C002", "Budi Santoso", "089876543210", "GOLD");

        // 2. Transaksi 1: Pelanggan VIP membeli Makanan dan Elektronik
        System.out.println("\n--- Transaksi TRX-001 (Pelanggan VIP: Makanan + Elektronik) ---");
        Transaksi transaksi1 = new Transaksi("TRX-001", "22-09-2026", pelangganVIP);
        transaksi1.tambahItem(makanan1, 2);      // 2 Ayam Geprek Crispy
        transaksi1.tambahItem(elektronik1, 1);   // 1 Kabel Data Type-C Fast
        transaksi1.prosesTransaksi();

        // 3. Transaksi 2: Pelanggan GOLD membeli Bebek Bakar dan Powerbank
        System.out.println("--- Transaksi TRX-002 (Pelanggan GOLD: Makanan + Elektronik) ---");
        Transaksi transaksi2 = new Transaksi("TRX-002", "22-09-2026", pelangganGold);
        transaksi2.tambahItem(makanan2, 1);      // 1 Bebek Bakar Madu
        transaksi2.tambahItem(elektronik2, 1);   // 1 Powerbank 10000mAh
        transaksi2.prosesTransaksi();

        // 4. Verifikasi sisa stok pada subclass setelah transaksi
        System.out.println("--- Verifikasi Sisa Stok Setelah Transaksi Kasir ---");
        System.out.println("Sisa stok " + makanan1.getNama() + ": " + makanan1.getStok() + " pcs");
        System.out.println("Sisa stok " + makanan2.getNama() + ": " + makanan2.getStok() + " pcs");
        System.out.println("Sisa stok " + elektronik1.getNama() + ": " + elektronik1.getStok() + " pcs");
        System.out.println("Sisa stok " + elektronik2.getNama() + ": " + elektronik2.getStok() + " pcs");

        System.out.println("\n==========================================================");
        System.out.println("         SELURUH PENGUJIAN P5 SELESAI DENGAN SUKSES       ");
        System.out.println("==========================================================");
    }
}
