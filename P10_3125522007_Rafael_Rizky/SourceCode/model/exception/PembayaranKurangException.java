package model.exception;

/**
 * Custom Checked Exception: PembayaranKurangException
 * Dilempar ketika uang tunai yang diserahkan konsumen tidak mencukupi total tagihan akhir.
 * 
 * Sesuai Modul 10:
 * - Menjamin state transaksi tidak berubah menjadi "LUNAS" (tetap "PENDING").
 * - Mengembalikan data numerik: total tagihan, uang yang dibayarkan, dan nominal kekurangan.
 * - Mencegah silent failure dan kerugian kas toko.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class PembayaranKurangException extends KasirException {
    private final String idTransaksi;
    private final double totalTagihan;
    private final double uangDiterima;
    private final double kekurangan;

    /**
     * Constructor dengan detail transaksi kasir.
     * 
     * @param idTransaksi  nomor nota transaksi
     * @param totalTagihan grand total yang wajib dibayar
     * @param uangDiterima nominal tunai yang diserahkan pelanggan
     */
    public PembayaranKurangException(String idTransaksi, double totalTagihan, double uangDiterima) {
        super("ERR-PAY-001", String.format(
            "Pembayaran tunai transaksi '%s' tidak mencukupi! Total tagihan: Rp %,.0f, tunai diserahkan: Rp %,.0f (Kekurangan: Rp %,.0f). Transaksi tetap PENDING.",
            idTransaksi, totalTagihan, uangDiterima, (totalTagihan - uangDiterima)
        ));
        this.idTransaksi = idTransaksi;
        this.totalTagihan = totalTagihan;
        this.uangDiterima = uangDiterima;
        this.kekurangan = Math.max(0.0, totalTagihan - uangDiterima);
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public double getTotalTagihan() {
        return totalTagihan;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    public double getKekurangan() {
        return kekurangan;
    }
}
