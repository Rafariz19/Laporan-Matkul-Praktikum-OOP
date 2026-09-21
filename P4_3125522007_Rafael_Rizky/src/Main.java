public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("PRAKTIKUM P4 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Relasi Antarobject: Association, Aggregation, & Composition");
        System.out.println("Nama : Rafael Rizky | NRP : 3125522007");
        System.out.println("Proyek : Sistem Kasir Sederhana");
        System.out.println("==========================================================\n");

        // =========================================================
        // SKENARIO TEST 1: OBJECT BERHASIL DIBUAT
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### TEST 1: PENGUJIAN INSTANSIASI OBJECT (BERHASIL)    ###");
        System.out.println("##########################################################");

        // 1. Membuat object Produk (Master Data Produk)
        System.out.println("[1] Membuat master data produk...");
        Produk produk1 = new Produk("P001", "Ayam Geprek", 15000, 30);
        Produk produk2 = new Produk("P002", "Es Teh Manis", 5000, 50);
        Produk produk3 = new Produk("P003", "Bebek Goreng", 25000, 15);

        produk1.tampilkanData();
        produk2.tampilkanData();
        produk3.tampilkanData();

        // 2. Membuat object Pelanggan
        System.out.println("[2] Membuat master data pelanggan...");
        Pelanggan pelangganVIP = new Pelanggan("C001", "Rafael Rizky", "081234567890", "VIP");
        Pelanggan pelangganGold = new Pelanggan("C002", "Budi Santoso", "089876543210", "GOLD");
        Pelanggan pelangganReguler = new Pelanggan("C003", "Siti Aminah", "081122334455", "REGULER");

        pelangganVIP.tampilkanData();
        pelangganGold.tampilkanData();
        pelangganReguler.tampilkanData();

        // 3. Membuat object Transaksi
        System.out.println("[3] Menginisialisasi transaksi kasir...");
        Transaksi transaksi1 = new Transaksi("TRX-001", "21-09-2026", pelangganVIP);
        Transaksi transaksi2 = new Transaksi("TRX-002", "21-09-2026", pelangganGold);

        System.out.println("Transaksi 1 terdaftar: " + transaksi1.getIdTransaksi() + " untuk pelanggan " + transaksi1.getPelanggan().getNama());
        System.out.println("Transaksi 2 terdaftar: " + transaksi2.getIdTransaksi() + " untuk pelanggan " + transaksi2.getPelanggan().getNama());
        System.out.println("[STATUS] Seluruh object berhasil dibuat dengan sempurna.\n");

        // =========================================================
        // SKENARIO TEST 2: DUA ATAU LEBIH OBJECT DAPAT BERINTERAKSI
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### TEST 2: PENGUJIAN INTERAKSI ANTAROBJECT            ###");
        System.out.println("### (Association, Aggregation, dan Composition)        ###");
        System.out.println("##########################################################");

        System.out.println("\n--- Menambahkan Item ke Transaksi 1 (Composition & Aggregation) ---");
        // Transaksi (Composition) menciptakan ItemTransaksi yang mengagregasikan Produk
        transaksi1.tambahItem(produk1, 2); // 2 Ayam Geprek
        transaksi1.tambahItem(produk2, 3); // 3 Es Teh Manis
        transaksi1.tambahItem(produk3, 1); // 1 Bebek Goreng

        System.out.println("\n--- Menambahkan Item ke Transaksi 2 (Composition & Aggregation) ---");
        transaksi2.tambahItem(produk1, 4); // 4 Ayam Geprek
        transaksi2.tambahItem(produk2, 5); // 5 Es Teh Manis

        // Uji validasi stok saat interaksi melebihi kapasitas
        System.out.println("\n--- Pengujian Validasi Interaksi Melebihi Stok Produk ---");
        System.out.println("Mencoba membeli 50 Bebek Goreng (stok hanya " + produk3.getStok() + ")...");
        boolean hasilBeliGagal = transaksi2.tambahItem(produk3, 50);
        System.out.println("Hasil penambahan: " + (hasilBeliGagal ? "Berhasil" : "Ditolak oleh sistem"));
        System.out.println("[STATUS] Interaksi multi-objek berjalan harmonis dan aman.\n");

        // =========================================================
        // SKENARIO TEST 3: DATA DARI OBJECT LAIN DIGUNAKAN METHOD
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### TEST 3: PEMANFAATAN DATA OBJECT LAIN LEWAT METHOD  ###");
        System.out.println("### (Kalkulasi Multi-Item, Diskon & Cetak Struk)       ###");
        System.out.println("##########################################################");

        System.out.println("\n--- Memproses dan Mencetak Struk Transaksi 1 ---");
        transaksi1.prosesTransaksi();

        System.out.println("--- Memproses dan Mencetak Struk Transaksi 2 ---");
        transaksi2.prosesTransaksi();

        // 4. Verifikasi sisa stok pada master produk setelah transaksi selesai
        System.out.println("--- Verifikasi Sisa Stok Produk Setelah Interaksi Transaksi ---");
        System.out.println("Stok akhir " + produk1.getNama() + ": " + produk1.getStok() + " pcs");
        System.out.println("Stok akhir " + produk2.getNama() + ": " + produk2.getStok() + " pcs");
        System.out.println("Stok akhir " + produk3.getNama() + ": " + produk3.getStok() + " pcs");

        System.out.println("\n==========================================================");
        System.out.println("         SELURUH PENGUJIAN P4 SELESAI DENGAN SUKSES       ");
        System.out.println("==========================================================");
    }
}
