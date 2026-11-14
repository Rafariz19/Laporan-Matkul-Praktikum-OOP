package model.exception;

import java.time.LocalDate;

/**
 * Exception yang dilempar saat kasir mencoba menambahkan produk makanan
 * yang sudah melewati tanggal kadaluarsa (expired).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class ProdukKadaluarsaException extends KasirException {
    private static final long serialVersionUID = 1L;
    private final String kodeProduk;
    private final String namaProduk;
    private final String tanggalKadaluarsa;

    public ProdukKadaluarsaException(String kodeProduk, LocalDate tanggalKadaluarsa) {
        this(kodeProduk, kodeProduk, String.valueOf(tanggalKadaluarsa));
    }

    public ProdukKadaluarsaException(String kodeProduk, String namaProduk, String tanggalKadaluarsa) {
        super("ERR-PROD-EXPIRED",
              "[PRODUK KADALUARSA] Produk '" + namaProduk + " (" + kodeProduk + ")' telah melewati tanggal kadaluarsa (" + 
              tanggalKadaluarsa + ") dan dilarang untuk ditransaksikan!");
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.tanggalKadaluarsa = tanggalKadaluarsa;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }
}
