package model;

/**
 * Interface DapatDidiskon
 * Kontrak standar bagi entitas komoditas yang memenuhi kualifikasi promosi diskon.
 * 
 * Sesuai Modul 9 & 10:
 * - Menghilangkan duplikasi logika diskon dengan default methods (DRY Principle).
 * - Menegakkan validasi state nilai persentase diskon (0% - 100%).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public interface DapatDidiskon {

    /**
     * Kontrak method getter harga dasar produk yang akan didiskon.
     * Otomatis terpuaskan oleh inheritance getHarga() dari superclass Produk.
     * 
     * @return harga satuan produk dalam integer (Rupiah)
     */
    int getHarga();

    /**
     * Menghitung besaran nominal potongan diskon berdasarkan persentase (0% - 100%).
     * 
     * @param persentase persentase potongan diskon
     * @return besaran nominal potongan diskon dalam double
     * @throws IllegalArgumentException jika persentase bernilai negatif atau > 100%
     */
    default double hitungDiskon(double persentase) {
        if (persentase < 0.0 || persentase > 100.0) {
            throw new IllegalArgumentException(String.format(
                "[ERROR VALIDASI] Persentase diskon harus di antara 0%% dan 100%%! (Diberikan: %.2f%%)",
                persentase
            ));
        }
        return getHarga() * (persentase / 100.0);
    }

    /**
     * Menghitung harga akhir produk setelah dipotong diskon.
     * 
     * @param persentase persentase potongan diskon
     * @return harga akhir setelah potongan diskon dalam double
     * @throws IllegalArgumentException jika persentase di luar rentang [0, 100]
     */
    default double getHargaSetelahDiskon(double persentase) {
        return getHarga() - hitungDiskon(persentase);
    }
}
