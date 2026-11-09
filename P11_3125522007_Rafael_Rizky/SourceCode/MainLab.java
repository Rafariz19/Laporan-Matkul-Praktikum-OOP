import java.util.ArrayList;
import java.util.List;

/**
 * Program Praktikum Dasar Modul 11: Collection dan Pengelolaan Kumpulan Objek
 * Mengikuti Langkah Praktikum 1 - 3 pada Modul 11 (Studi Kasus Perpustakaan).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung
 * NRP         : 3125522007
 * Kampus      : PENS PSDKU Sumenep (2026)
 */

class Buku {
    private final String kode;
    private final String judul;

    public Buku(String kode, String judul) {
        if (kode == null || kode.trim().isEmpty() || judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
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
}

class Perpustakaan {
    private final List<Buku> daftarBuku = new ArrayList<>();

    public void tambahBuku(Buku buku) {
        if (buku == null) {
            throw new IllegalArgumentException("Objek buku tidak boleh null.");
        }
        if (cariBuku(buku.getKode()) != null) {
            throw new IllegalArgumentException("Kode buku sudah terdaftar.");
        }
        daftarBuku.add(buku);
    }

    public Buku cariBuku(String kode) {
        if (kode == null) {
            return null;
        }
        for (Buku buku : daftarBuku) {
            if (buku.getKode().equalsIgnoreCase(kode.trim())) {
                return buku;
            }
        }
        return null;
    }

    public boolean hapusBuku(String kode) {
        Buku buku = cariBuku(kode);
        if (buku != null) {
            return daftarBuku.remove(buku);
        }
        return false;
    }

    public void tampilkanBuku() {
        System.out.println("--- Daftar Koleksi Buku Perpustakaan ---");
        if (daftarBuku.isEmpty()) {
            System.out.println("[Koleksi kosong / belum ada buku]");
        } else {
            for (Buku buku : daftarBuku) {
                System.out.println("- [" + buku.getKode() + "] " + buku.getJudul());
            }
        }
        System.out.println("Total buku tersimpan: " + daftarBuku.size() + " buku.\n");
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }
}

public class MainLab {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM DASAR MODUL 11 - COLLECTION DAN PENGELOLAAN KUMPULAN OBJEK");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("======================================================================\n");

        Perpustakaan perpustakaan = new Perpustakaan();

        // Uji Tampilkan koleksi awal (kosong)
        System.out.println("[UJI 0] Menampilkan Koleksi Saat Masih Kosong:");
        perpustakaan.tampilkanBuku();

        // 1. Tambah B001 dan B002
        System.out.println("[UJI 1] Menambahkan Buku B001 (\"Dasar Java\") dan B002 (\"Konsep OOP\"):");
        perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
        perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
        perpustakaan.tampilkanBuku();

        // 2. Cari B002
        System.out.println("[UJI 2] Mencari Buku dengan kode \"B002\":");
        Buku bukuCari = perpustakaan.cariBuku("B002");
        if (bukuCari != null) {
            System.out.println("Hasil: Ditemukan -> [" + bukuCari.getKode() + "] " + bukuCari.getJudul());
        } else {
            System.out.println("Hasil: Tidak ditemukan.");
        }
        System.out.println("Jumlah data koleksi: " + perpustakaan.getJumlahBuku() + " buku.\n");

        // 3. Tambah B001 kembali (Uji Pencegahan Kode Duplikat)
        System.out.println("[UJI 3] Menambahkan kembali Buku dengan kode duplikat \"B001\":");
        try {
            perpustakaan.tambahBuku(new Buku("B001", "Buku Duplikat"));
        } catch (IllegalArgumentException e) {
            System.out.println("Ditolak Sesuai Ekspektasi: " + e.getMessage());
        }
        System.out.println("Jumlah data koleksi tetap: " + perpustakaan.getJumlahBuku() + " buku.\n");

        // 4. Hapus B001
        System.out.println("[UJI 4] Menghapus Buku dengan kode \"B001\":");
        boolean hasilHapusB001 = perpustakaan.hapusBuku("B001");
        System.out.println("Status penghapusan B001: " + hasilHapusB001 + " (Berhasil dihapus)");
        System.out.println("Jumlah data koleksi: " + perpustakaan.getJumlahBuku() + " buku.\n");

        // 5. Cari B001 pasca penghapusan
        System.out.println("[UJI 5] Mencari kembali Buku \"B001\" yang telah dihapus:");
        Buku bukuCariLagi = perpustakaan.cariBuku("B001");
        System.out.println("Hasil pencarian B001: " + bukuCariLagi + " (null / tidak ditemukan)");
        System.out.println("Jumlah data koleksi: " + perpustakaan.getJumlahBuku() + " buku.\n");

        // 6. Hapus B999 (Kode tidak terdaftar)
        System.out.println("[UJI 6] Menghapus Buku dengan kode fiktif \"B999\":");
        boolean hasilHapusB999 = perpustakaan.hapusBuku("B999");
        System.out.println("Status penghapusan B999: " + hasilHapusB999 + " (false / gagal)");
        System.out.println("Jumlah data koleksi: " + perpustakaan.getJumlahBuku() + " buku.\n");

        // Uji Tambahan: Judul kosong dan Objek null
        System.out.println("[UJI 7] Uji Validasi Parameter Tambahan (Judul Kosong & Objek Null):");
        try {
            new Buku("B003", "   ");
        } catch (IllegalArgumentException e) {
            System.out.println("- Pembuatan judul kosong ditolak : " + e.getMessage());
        }
        try {
            perpustakaan.tambahBuku(null);
        } catch (IllegalArgumentException e) {
            System.out.println("- Penambahan objek null ditolak  : " + e.getMessage());
        }
        System.out.println();

        System.out.println("[STATE AKHIR KOLEKSI PERPUSTAKAAN]:");
        perpustakaan.tampilkanBuku();

        System.out.println("======================================================================");
        System.out.println("KESIMPULAN PENGUJIAN PRAKTIKUM DASAR MODUL 11:");
        System.out.println("1. Operasi add, search, remove, dan display berjalan presisi sesuai modul.");
        System.out.println("2. Enkapsulasi List mencegah mutasi ilegal dari luar class Perpustakaan.");
        System.out.println("3. Seluruh kegagalan/penolakan data terbukti tidak mengubah state koleksi.");
        System.out.println("======================================================================");
    }
}
