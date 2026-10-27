package model.exception;

/**
 * Custom Checked Exception: StatusTransaksiInvalidException
 * Dilempar ketika operasi bisnis dijalankan pada siklus state transaksi yang tidak diizinkan,
 * misalnya: menambahkan barang, membatalkan item, atau membayar nota yang sudah berstatus "LUNAS".
 * 
 * Sesuai Modul 10 & Prinsip Finite State Machine (FSM):
 * - Melindungi sifat immutability (kekekalan) transaksi yang sudah tervalidasi dan lunas.
 * - Mencegah fraud kasir atau inkonsistensi pembukuan toko.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class StatusTransaksiInvalidException extends KasirException {
    private final String idTransaksi;
    private final String statusSaatIni;
    private final String operasiDiminta;

    /**
     * Constructor StatusTransaksiInvalidException.
     * 
     * @param idTransaksi    nomor nota transaksi
     * @param statusSaatIni  state transaksi saat ini (misal: "LUNAS", "DIBATALKAN")
     * @param operasiDiminta nama operasi yang dicoba dijalankan (misal: "tambahItem", "prosesTransaksi")
     */
    public StatusTransaksiInvalidException(String idTransaksi, String statusSaatIni, String operasiDiminta) {
        super("ERR-STATE-001", String.format(
            "Operasi '%s' pada transaksi '%s' ditolak! Transaksi sudah berstatus '%s' dan bersifat final (immutable).",
            operasiDiminta, idTransaksi, statusSaatIni
        ));
        this.idTransaksi = idTransaksi;
        this.statusSaatIni = statusSaatIni;
        this.operasiDiminta = operasiDiminta;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getStatusSaatIni() {
        return statusSaatIni;
    }

    public String getOperasiDiminta() {
        return operasiDiminta;
    }
}
