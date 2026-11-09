import java.io.Serializable;

/**
 * Class Domain Buku untuk Pengujian Modul 14 (Refactored & Verified)
 * Mengimplementasikan validasi class invariant ketat pada constructor.
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 */
public class Buku implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String kode;
    private final String judul;

    public Buku(String kode, String judul) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Validasi Gagal: Kode buku tidak boleh kosong.");
        }
        if (judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Validasi Gagal: Judul buku tidak boleh kosong.");
        }
        if (kode.contains("|") || kode.contains("\n") || kode.contains("\r")) {
            throw new IllegalArgumentException("Validasi Gagal: Kode buku tidak boleh mengandung karakter pembatas '|' atau baris baru.");
        }
        if (judul.contains("|") || judul.contains("\n") || judul.contains("\r")) {
            throw new IllegalArgumentException("Validasi Gagal: Judul buku tidak boleh mengandung karakter pembatas '|' atau baris baru.");
        }
        this.kode = kode.trim();
        this.judul = judul.trim();
    }

    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    public String toFileFormat() {
        return kode + "|" + judul;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", kode, judul);
    }
}

