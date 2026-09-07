public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("PRAKTIKUM P3 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Encapsulation, Access Modifier, Getter-Setter & Validasi");
        System.out.println("Nama : Rafael Rizky | NRP : 3125522007");
        System.out.println("Proyek : Sistem Kasir Sederhana");
        System.out.println("=================================================\n");

        // =========================================================
        // BAGIAN 1: PENGUJIAN DATA VALID (TEST VALID)
        // =========================================================
        System.out.println("#################################################");
        System.out.println("### 1. PENGUJIAN DATA VALID (TEST VALID)      ###");
        System.out.println("#################################################");

        System.out.println("\n--- [1.1] Instansiasi Object dengan Data Valid via Constructor ---");
        Produk produk1 = new Produk("P001", "Ayam Geprek", 15000, 25);
        Produk produk2 = new Produk("P002", "Es Teh Manis", 5000, 40);

        Pelanggan pelanggan1 = new Pelanggan("C001", "Rafael Rizky", "081234567890", "VIP");
        Pelanggan pelanggan2 = new Pelanggan("C002", "Budi Santoso", "089876543210", "Reguler");

        System.out.println("\n--- [1.2] Membaca Data Menggunakan Method Getter ---");
        System.out.println("Nama Produk 1 : " + produk1.getNama() + " | Harga: Rp" + produk1.getHarga() + " | Stok: " + produk1.getStok());
        System.out.println("Nama Produk 2 : " + produk2.getNama() + " | Harga: Rp" + produk2.getHarga() + " | Stok: " + produk2.getStok());
        System.out.println("Pelanggan 1   : " + pelanggan1.getNama() + " | Member: " + pelanggan1.getTipeMember() + " | Diskon: " + (int)(pelanggan1.getDiskon() * 100) + "%");
        System.out.println("Pelanggan 2   : " + pelanggan2.getNama() + " | Member: " + pelanggan2.getTipeMember() + " | Diskon: " + (int)(pelanggan2.getDiskon() * 100) + "%");

        System.out.println("\n--- [1.3] Mengubah Data Menggunakan Method Setter Valid ---");
        System.out.println("Memperbarui harga produk1 menjadi Rp17000...");
        produk1.setHarga(17000);
        System.out.println("Nilai baru harga produk1 (via getter): Rp" + produk1.getHarga());

        System.out.println("\nMemperbarui nomor HP pelanggan2...");
        pelanggan2.setNomorHP("081298765432");
        System.out.println("Nilai baru nomor HP pelanggan2 (via getter): " + pelanggan2.getNomorHP());

        System.out.println("\nMemperbarui tipe member pelanggan2 menjadi GOLD (diskon 10%)...");
        pelanggan2.setTipeMember("GOLD");
        System.out.println("Tipe member baru (via getter): " + pelanggan2.getTipeMember() + " (Diskon: " + (int)(pelanggan2.getDiskon() * 100) + "%)");

        System.out.println("\n--- [1.4] Memproses Transaksi Valid ---");
        Transaksi transaksi1 = new Transaksi("TRX-001", "08-09-2026", pelanggan1, produk1, 5);
        Transaksi transaksi2 = new Transaksi("TRX-002", "08-09-2026", pelanggan2, produk2, 10);

        transaksi1.prosesTransaksi();
        transaksi2.prosesTransaksi();

        System.out.println("Status Stok Setelah Transaksi:");
        System.out.println("Sisa stok " + produk1.getNama() + ": " + produk1.getStok() + " pcs");
        System.out.println("Sisa stok " + produk2.getNama() + ": " + produk2.getStok() + " pcs\n");

        // =========================================================
        // BAGIAN 2: PENGUJIAN DATA TIDAK VALID (TEST INVALID)
        // =========================================================
        System.out.println("#################################################");
        System.out.println("### 2. PENGUJIAN DATA TIDAK VALID (TEST INVALID) ###");
        System.out.println("#################################################");

        System.out.println("\n--- [2.1] Uji Validasi Harga Negatif (Produk.setHarga) ---");
        System.out.println("Harga saat ini: Rp" + produk1.getHarga());
        System.out.println("Mencoba mengubah harga menjadi -10000...");
        produk1.setHarga(-10000);
        System.out.println("Verifikasi Nilai: Harga produk1 TETAP: Rp" + produk1.getHarga() + " [INTEGRITAS TERJAGA]");

        System.out.println("\n--- [2.2] Uji Validasi Stok Negatif (Produk.setStok) ---");
        System.out.println("Stok saat ini: " + produk1.getStok() + " pcs");
        System.out.println("Mencoba mengubah stok menjadi -15...");
        produk1.setStok(-15);
        System.out.println("Verifikasi Nilai: Stok produk1 TETAP: " + produk1.getStok() + " pcs [INTEGRITAS TERJAGA]");

        System.out.println("\n--- [2.3] Uji Validasi Nama Kosong / Null (Produk.setNama) ---");
        System.out.println("Nama saat ini: " + produk1.getNama());
        System.out.println("Mencoba mengubah nama menjadi string kosong (\"\")...");
        produk1.setNama("");
        System.out.println("Verifikasi Nilai: Nama produk1 TETAP: " + produk1.getNama() + " [INTEGRITAS TERJAGA]");

        System.out.println("\n--- [2.4] Uji Validasi Format Nomor HP (Pelanggan.setNomorHP) ---");
        System.out.println("Nomor HP saat ini: " + pelanggan1.getNomorHP());
        System.out.println("Mencoba mengubah nomor HP menjadi '0812-SALAH-XYZ'...");
        pelanggan1.setNomorHP("0812-SALAH-XYZ");
        System.out.println("Mencoba nomor HP terlalu pendek '123'...");
        pelanggan1.setNomorHP("123");
        System.out.println("Verifikasi Nilai: Nomor HP pelanggan1 TETAP: " + pelanggan1.getNomorHP() + " [INTEGRITAS TERJAGA]");

        System.out.println("\n--- [2.5] Uji Validasi Tipe Member Invalid (Pelanggan.setTipeMember) ---");
        System.out.println("Tipe member saat ini: " + pelanggan1.getTipeMember());
        System.out.println("Mencoba mengubah tipe member menjadi 'PLATINUM_DIAMOND'...");
        pelanggan1.setTipeMember("PLATINUM_DIAMOND");
        System.out.println("Verifikasi Nilai: Tipe member pelanggan1 TETAP: " + pelanggan1.getTipeMember() + " [INTEGRITAS TERJAGA]");

        System.out.println("\n--- [2.6] Uji Validasi Jumlah Beli Melebihi Stok (Transaksi.setJumlahBeli) ---");
        System.out.println("Sisa stok " + produk1.getNama() + " saat ini: " + produk1.getStok() + " pcs");
        System.out.println("Mencoba transaksi baru dengan jumlah beli 100 pcs (melebihi stok)...");
        Transaksi transaksiInvalid = new Transaksi("TRX-ERR", "08-09-2026", pelanggan1, produk1, 100);
        System.out.println("Mencoba memproses transaksi...");
        transaksiInvalid.prosesTransaksi();

        System.out.println("=================================================");
        System.out.println("       SELURUH PENGUJIAN SELESAI (SUKSES)        ");
        System.out.println("=================================================");
    }
}
