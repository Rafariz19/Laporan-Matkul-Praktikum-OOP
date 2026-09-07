public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("PRAKTIKUM P2 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Sistem Kasir Sederhana");
        System.out.println("Nama : Rafael Rizky | NRP : 3125522007");
        System.out.println("=================================================\n");

        // 1. Instansiasi Object menggunakan Constructor (Minimal 2 object per class)
        System.out.println(">>> 1. INISIALISASI OBJECT MELALUI CONSTRUCTOR <<<");
        Produk produk1 = new Produk("P001", "Ayam Geprek", 15000, 20);
        Produk produk2 = new Produk("P002", "Es Teh Manis", 5000, 50);

        Pelanggan pelanggan1 = new Pelanggan("C001", "Rafael Rizky", "081234567890", "VIP");
        Pelanggan pelanggan2 = new Pelanggan("C002", "Budi Santoso", "089876543210", "Reguler");

        // Menampilkan data awal produk dan pelanggan
        produk1.tampilkanData();
        produk2.tampilkanData();
        pelanggan1.tampilkanData();
        pelanggan2.tampilkanData();

        // 2. Pengujian Method dengan Return Value
        System.out.println(">>> 2. PENGUJIAN METHOD DENGAN RETURN VALUE <<<");
        System.out.println("Total nilai inventaris produk " + produk1.getNama() + ": Rp" + produk1.hitungNilaiInventaris());
        System.out.println("Total nilai inventaris produk " + produk2.getNama() + ": Rp" + produk2.hitungNilaiInventaris());
        System.out.println("Diskon pelanggan " + pelanggan1.getNama() + ": " + (int)(pelanggan1.getDiskon() * 100) + "%");
        System.out.println("Diskon pelanggan " + pelanggan2.getNama() + ": " + (int)(pelanggan2.getDiskon() * 100) + "%");
        System.out.println();

        // 3. Pengujian Method dengan Parameter (Mengubah Data)
        System.out.println(">>> 3. PENGUJIAN METHOD DENGAN PARAMETER (UBAH DATA) <<<");
        produk1.ubahHarga(16000);
        produk1.tambahStok(10);
        pelanggan2.ubahNomorHP("081122334455");
        pelanggan2.ubahTipeMember("Gold"); // Budi sekarang menjadi member Gold (diskon 10%)
        System.out.println();

        System.out.println("Data setelah perubahan:");
        produk1.tampilkanData();
        pelanggan2.tampilkanData();

        // 4. Pengujian Class Transaksi
        System.out.println(">>> 4. INISIALISASI DAN PENGUJIAN OBJECT TRANSAKSI <<<");
        Transaksi transaksi1 = new Transaksi("TRX-001", "07-09-2026", pelanggan1, produk1, 3);
        Transaksi transaksi2 = new Transaksi("TRX-002", "07-09-2026", pelanggan2, produk2, 5);

        // Simulasi pengubahan jumlah beli via method berparameter
        transaksi1.ubahJumlahBeli(4);
        System.out.println("Estimasi total bayar TRX-001 (via return value): Rp" + (int)transaksi1.hitungTotalBayar());
        System.out.println("Estimasi total bayar TRX-002 (via return value): Rp" + (int)transaksi2.hitungTotalBayar());
        System.out.println();

        // Memproses transaksi (method tanpa parameter)
        System.out.println(">>> 5. MEMPROSES TRANSAKSI (STRUK & UPDATE STOK) <<<");
        transaksi1.prosesTransaksi();
        transaksi2.prosesTransaksi();

        // 5. Cek Stok Akhir setelah Transaksi
        System.out.println(">>> 6. STATUS STOK AKHIR SETELAH TRANSAKSI <<<");
        produk1.tampilkanData();
        produk2.tampilkanData();

        System.out.println("=================================================");
        System.out.println("         PENGUJIAN SELESAI DENGAN SUKSES         ");
        System.out.println("=================================================");
    }
}
