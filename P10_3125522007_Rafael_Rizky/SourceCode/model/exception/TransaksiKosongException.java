package model.exception;

/**
 * Custom Checked Exception: TransaksiKosongException
 * Dilempar ketika kasir mencoba memproses checkout/pembayaran pada transaksi
 * yang keranjang belanjanya belum memiliki satupun item (empty cart).
 * 
 * Sesuai Modul 10:
 * - Menolak pembuatan struk atau pemrosesan pembayaran palsu.
 * - Menjaga konsistensi bisnis: tidak boleh ada transaksi lunas dengan 0 komoditas.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class TransaksiKosongException extends KasirException {
    private final String idTransaksi;

    /**
     * Constructor TransaksiKosongException.
     * 
     * @param idTransaksi nomor nota transaksi kasir
     */
    public TransaksiKosongException(String idTransaksi) {
        super("ERR-TRX-001", String.format(
            "Checkout transaksi '%s' ditolak! Keranjang belanja masih kosong tanpa komoditas barang.",
            idTransaksi
        ));
        this.idTransaksi = idTransaksi;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }
}
