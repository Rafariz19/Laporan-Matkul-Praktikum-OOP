package model;

/**
 * Interface DapatDidiskon
 * Kontrak standar bagi entitas komoditas yang memenuhi kualifikasi promosi diskon.
 * 
 * Sesuai ketentuan Modul 9:
 * Menghilangkan duplikasi kode hitungDiskon dan getHargaSetelahDiskon
 * menggunakan default method di Java 8+, sehingga subclass tidak perlu
 * mengulang logika kalkulasi yang sama persis.
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
     * Diimplementasikan sebagai default method untuk menghilangkan duplikasi kode.
     * 
     * @param persentase persentase potongan diskon
     * @return besaran nominal potongan diskon dalam double
     */
    default double hitungDiskon(double persentase) {
        if (persentase <= 0.0) {
            return 0.0;
        }
        if (persentase > 100.0) {
            persentase = 100.0;
        }
        return getHarga() * (persentase / 100.0);
    }

    /**
     * Menghitung harga akhir produk setelah dipotong diskon.
     * Diimplementasikan sebagai default method untuk menghilangkan duplikasi kode.
     * 
     * @param persentase persentase potongan diskon
     * @return harga akhir setelah potongan diskon dalam double
     */
    default double getHargaSetelahDiskon(double persentase) {
        return getHarga() - hitungDiskon(persentase);
    }
}
