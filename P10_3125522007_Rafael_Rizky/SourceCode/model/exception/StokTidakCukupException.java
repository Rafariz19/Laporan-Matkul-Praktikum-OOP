package model.exception;

/**
 * Custom Checked Exception: StokTidakCukupException
 * Dilempar ketika operasi pengurangan stok atau penambahan barang belanja
 * meminta kuantitas yang melampaui sisa unit stok inventaris yang tersedia.
 * 
 * Sesuai Modul 10:
 * - Menginformasikan kegagalan transaksi kasir secara eksplisit.
 * - Menyimpan informasi state: nama produk, stok saat ini, dan jumlah yang diminta.
 * - Menjaga integritas data: operasi ditolak tanpa mengurangi stok inventaris.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class StokTidakCukupException extends KasirException {
    private final String namaProduk;
    private final int stokTersedia;
    private final int jumlahDiminta;

    /**
     * Constructor lengkap dengan rincian state produk.
     * 
     * @param namaProduk    nama komoditas produk yang kehabisan stok
     * @param stokTersedia  sisa unit stok fisik saat ini
     * @param jumlahDiminta kuantitas pembelian yang diminta konsumen
     */
    public StokTidakCukupException(String namaProduk, int stokTersedia, int jumlahDiminta) {
        super("ERR-STOK-001", String.format(
            "Stok produk '%s' tidak mencukupi! Tersedia: %d unit, diminta: %d unit (Kurang: %d unit).",
            namaProduk, stokTersedia, jumlahDiminta, (jumlahDiminta - stokTersedia)
        ));
        this.namaProduk = namaProduk;
        this.stokTersedia = stokTersedia;
        this.jumlahDiminta = jumlahDiminta;
    }

    /**
     * Constructor fleksibel dengan pesan kustom (kompatibel praktikum dasar Modul 10).
     * 
     * @param pesan deskripsi pesan kesalahan
     */
    public StokTidakCukupException(String pesan) {
        super("ERR-STOK-002", pesan);
        this.namaProduk = "Unknown";
        this.stokTersedia = 0;
        this.jumlahDiminta = 0;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public int getStokTersedia() {
        return stokTersedia;
    }

    public int getJumlahDiminta() {
        return jumlahDiminta;
    }

    public int getDefisitStok() {
        return Math.max(0, jumlahDiminta - stokTersedia);
    }
}
