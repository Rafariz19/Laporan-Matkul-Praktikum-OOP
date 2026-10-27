package model.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base Checked Exception: KasirException
 * Superclass bagi seluruh checked exception spesifik dalam domain Sistem Kasir Sederhana.
 * 
 * Karakteristik Arsitektur:
 * 1. Menuruni java.lang.Exception sehingga berstatus Checked Exception (memaksa penanganan try-catch / throws).
 * 2. Menyimpan metadata diagnostik: kode kesalahan (errorCode) dan waktu kejadian (timestamp).
 * 3. Menerapkan Information Expert & Exception Chaining untuk kemudahan pelacakan bug (traceability).
 * 
 * Praktikum PBO Modul 10: "Exception Handling dan Validasi State Objek"
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public abstract class KasirException extends Exception {
    private final String kodeError;
    private final String waktuKejadian;

    /**
     * Constructor dasar dengan kode error dan pesan rincian.
     * 
     * @param kodeError identifier unik jenis kesalahan domain (misal: "ERR-STOK-01")
     * @param pesan     deskripsi pesan kesalahan yang informatif bagi pengguna
     */
    public KasirException(String kodeError, String pesan) {
        super(pesan);
        this.kodeError = kodeError;
        this.waktuKejadian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    /**
     * Constructor dengan exception cause untuk mendukung exception chaining.
     * 
     * @param kodeError identifier unik jenis kesalahan
     * @param pesan     deskripsi pesan kesalahan
     * @param penyebab  akar penyebab throwable
     */
    public KasirException(String kodeError, String pesan, Throwable penyebab) {
        super(pesan, penyebab);
        this.kodeError = kodeError;
        this.waktuKejadian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public String getKodeError() {
        return kodeError;
    }

    public String getWaktuKejadian() {
        return waktuKejadian;
    }

    /**
     * Format rincian log kesalahan standar sistem kasir.
     * 
     * @return representasi string log diagnostik exception
     */
    public String getDetailDiagnostik() {
        return String.format("[%s | %s] %s", kodeError, waktuKejadian, getMessage());
    }
}
