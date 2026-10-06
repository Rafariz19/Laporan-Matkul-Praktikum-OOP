public interface DapatDidiskon {
    // Kontrak Interface: Menstandarisasi perhitungan diskon promosi bagi komoditas yang memenuhi syarat promo.
    // Seluruh method dalam interface secara default bersifat public dan abstract.

    // Menghitung besaran nominal potongan diskon berdasarkan persentase (0 - 100%)
    double hitungDiskon(double persentase);

    // Menghitung harga akhir produk setelah dipotong diskon
    double getHargaSetelahDiskon(double persentase);
}
