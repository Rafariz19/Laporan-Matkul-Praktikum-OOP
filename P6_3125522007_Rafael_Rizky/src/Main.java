public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("PRAKTIKUM P6 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Polymorphism, Method Overriding, Overloading, Dynamic Binding");
        System.out.println("Nama : Rafael Rizky | NRP : 3125522007");
        System.out.println("Proyek : Sistem Kasir Sederhana");
        System.out.println("==========================================================\n");

        // =========================================================
        // BAGIAN 1: UPCASTING & POLYMORPHIC REFERENCE (Bagian D)
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 1. PENGUJIAN UPCASTING & POLYMORPHIC REFERENCE     ###");
        System.out.println("##########################################################");
        System.out.println("[PENJELASAN] Tipe referensi Superclass (Produk) merujuk ke objek Subclass (ProdukMakanan & ProdukElektronik).\n");

        // Upcasting: Reference Produk menunjuk ke objek nyata ProdukMakanan dan ProdukElektronik
        Produk p1 = new ProdukMakanan("M001", "Ayam Geprek Crispy", 15000, 30, "25-10-2026");
        Produk p2 = new ProdukElektronik("E001", "Kabel Data Type-C Fast", 25000, 40, 6);
        Produk p3 = new ProdukMakanan("M002", "Bebek Bakar Madu", 28000, 15, "24-10-2026");
        Produk p4 = new ProdukElektronik("E002", "Powerbank 10000mAh", 120000, 12, 12);

        System.out.println("Objek p1 dideklarasikan sebagai Produk, objek aktual: " + p1.getClass().getSimpleName());
        System.out.println("Objek p2 dideklarasikan sebagai Produk, objek aktual: " + p2.getClass().getSimpleName());
        System.out.println("Objek p3 dideklarasikan sebagai Produk, objek aktual: " + p3.getClass().getSimpleName());
        System.out.println("Objek p4 dideklarasikan sebagai Produk, objek aktual: " + p4.getClass().getSimpleName());
        System.out.println();

        // =========================================================
        // BAGIAN 2: DYNAMIC BINDING / DYNAMIC METHOD DISPATCH (Bagian B & F)
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 2. PENGUJIAN DYNAMIC BINDING (METHOD OVERRIDING)   ###");
        System.out.println("##########################################################");
        System.out.println("[PENJELASAN] Memanggil method tampilkanData() melalui referensi Produk.");
        System.out.println("JVM menentukan implementasi method saat runtime berdasarkan tipe objek aktual.\n");

        // Pemanggilan method yang dioverride:
        p1.tampilkanData(); // Mengeksekusi ProdukMakanan.tampilkanData()
        p2.tampilkanData(); // Mengeksekusi ProdukElektronik.tampilkanData()

        // =========================================================
        // BAGIAN 3: POLYMORPHIC COLLECTION (Bagian E)
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 3. PENGUJIAN POLYMORPHIC COLLECTION (ARRAY OF SUPER)###");
        System.out.println("##########################################################");
        System.out.println("[PENJELASAN] Menyimpan 4 objek subclass berbeda ke dalam satu array Produk[].\n");

        Produk[] katalogToko = { p1, p2, p3, p4 };

        System.out.println("Iterasi Polymorphic Collection:");
        System.out.println("----------------------------------------------------------");
        for (int i = 0; i < katalogToko.length; i++) {
            System.out.print("[" + (i + 1) + "] ");
            // Pemanggilan polimorfik method getKategoriInfo() dan tampilkanData()
            System.out.println("Kategori Objek: " + katalogToko[i].getKategoriInfo());
            System.out.println("    Nama Barang : " + katalogToko[i].getNama());
            System.out.println("    Harga       : Rp" + katalogToko[i].getHarga());
            System.out.println("    Stok        : " + katalogToko[i].getStok() + " pcs");
            System.out.println("    Inventaris  : Rp" + katalogToko[i].hitungNilaiInventaris());
            System.out.println("----------------------------------------------------------");
        }
        System.out.println();

        // =========================================================
        // BAGIAN 4: METHOD OVERLOADING PADA SISTEM KASIR (Bagian C)
        // =========================================================
        System.out.println("##########################################################");
        System.out.println("### 4. PENGUJIAN METHOD OVERLOADING PADA TRANSAKSI     ###");
        System.out.println("##########################################################");

        Pelanggan pelangganVIP = new Pelanggan("C001", "Rafael Rizky", "081234567890", "VIP");
        Pelanggan pelangganGold = new Pelanggan("C002", "Budi Santoso", "089876543210", "GOLD");

        // Transaksi 1: Menguji Overloaded tambahItem (reguler vs diskon promosi)
        System.out.println("\n--- [4.1] Transaksi TRX-001 (Overloading tambahItem) ---");
        Transaksi transaksi1 = new Transaksi("TRX-001", "29-09-2026", pelangganVIP);
        
        // Overload 1A: tambahItem(Produk, int) -> Reguler
        transaksi1.tambahItem(p1, 2); 
        
        // Overload 1B: tambahItem(Produk, int, double) -> Promo khusus diskon 10%
        transaksi1.tambahItem(p2, 1, 0.10); 
        
        // Overload 2A: prosesTransaksi() -> Standar non-tunai
        transaksi1.prosesTransaksi();

        // Transaksi 2: Menguji Overloaded prosesTransaksi (dengan uang tunai & kembalian)
        System.out.println("--- [4.2] Transaksi TRX-002 (Overloading prosesTransaksi Tunai) ---");
        Transaksi transaksi2 = new Transaksi("TRX-002", "29-09-2026", pelangganGold);
        
        transaksi2.tambahItem(p3, 2);       // 2 Bebek Bakar Madu
        transaksi2.tambahItem(p4, 1, 0.05); // 1 Powerbank Promo diskon 5%
        
        // Overload 2B: prosesTransaksi(double uangDiterima) -> Pembayaran tunai Rp200.000
        transaksi2.prosesTransaksi(200000);

        // Verifikasi sisa stok akhir
        System.out.println("--- Verifikasi Sisa Stok Produk Setelah Transaksi Kasir ---");
        System.out.println("Sisa stok " + p1.getNama() + ": " + p1.getStok() + " pcs");
        System.out.println("Sisa stok " + p2.getNama() + ": " + p2.getStok() + " pcs");
        System.out.println("Sisa stok " + p3.getNama() + ": " + p3.getStok() + " pcs");
        System.out.println("Sisa stok " + p4.getNama() + ": " + p4.getStok() + " pcs");

        System.out.println("\n==========================================================");
        System.out.println("         SELURUH PENGUJIAN P6 SELESAI DENGAN SUKSES       ");
        System.out.println("==========================================================");
    }
}
