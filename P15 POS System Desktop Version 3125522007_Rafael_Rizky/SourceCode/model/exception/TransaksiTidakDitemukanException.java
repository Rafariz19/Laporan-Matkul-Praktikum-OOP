package model.exception;

/**
 * Exception yang dilempar saat nota transaksi yang dicari (misal untuk VOID)
 * tidak ditemukan di dalam berkas log transaksi.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class TransaksiTidakDitemukanException extends KasirException {
    private static final long serialVersionUID = 1L;
    private final String idTransaksi;

    public TransaksiTidakDitemukanException(String idTransaksi) {
        super("ERR-TRX-NOT-FOUND", "[TRANSAKSI TIDAK DITEMUKAN] Nota dengan ID '" + idTransaksi + "' tidak ditemukan dalam log transaksi!");
        this.idTransaksi = idTransaksi;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }
}
