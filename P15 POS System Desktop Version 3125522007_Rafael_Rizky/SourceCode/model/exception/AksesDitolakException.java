package model.exception;

/**
 * Exception yang dilempar ketika pengguna mencoba melakukan operasi
 * yang membutuhkan hak akses administratif (ADMIN).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class AksesDitolakException extends KasirException {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String operasi;

    public AksesDitolakException(String username, String operasi) {
        super("ERR-AUTH-FORBIDDEN",
              "[AKSES DITOLAK] Pengguna '" + username + "' tidak memiliki hak akses ADMIN untuk operasi: " + operasi + "!");
        this.username = username;
        this.operasi = operasi;
    }

    public String getUsername() {
        return username;
    }

    public String getOperasi() {
        return operasi;
    }
}
