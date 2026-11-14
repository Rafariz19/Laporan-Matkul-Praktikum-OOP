package model.exception;

/**
 * Exception yang dilempar saat produk yang ingin dihapus sedang berada
 * di dalam keranjang transaksi belanja aktif (PENDING).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class ProdukSedangDigunakanException extends KasirException {
    private static final long serialVersionUID = 1L;
    private final String kodeProduk;

    public ProdukSedangDigunakanException(String kodeProduk) {
        super("ERR-KATALOG-PROD-IN-USE",
              "[PENOLAKAN INTEGRITAS] Produk berkode '" + kodeProduk + 
              "' tidak dapat dihapus karena masih berada di dalam keranjang belanja aktif (PENDING)!\n" +
              "Silakan batalkan item tersebut dari keranjang terlebih dahulu.");
        this.kodeProduk = kodeProduk;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }
}
