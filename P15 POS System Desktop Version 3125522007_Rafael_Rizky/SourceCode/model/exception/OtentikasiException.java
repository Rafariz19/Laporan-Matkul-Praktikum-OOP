package model.exception;

/**
 * Exception yang dilempar saat terjadi kegagalan otentikasi login atau registrasi akun.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class OtentikasiException extends KasirException {
    private static final long serialVersionUID = 1L;

    public OtentikasiException(String kodeError, String pesan) {
        super(kodeError, pesan);
    }
}
