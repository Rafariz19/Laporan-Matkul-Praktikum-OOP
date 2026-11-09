import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Program Praktikum Dasar Modul 12: Penyimpanan dan Pembacaan Data ke File
 * Mengikuti Langkah Praktikum 1 - 5 pada Modul 12 (Studi Kasus Perpustakaan).
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

    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Buku buku : daftarBuku) {
            String kode = buku.getKode();
            String judul = buku.getJudul();
            for (String nilai : new String[]{kode, judul}) {
                if (nilai.contains("|") || nilai.contains("\n") || nilai.contains("\r")) {
                    throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
                }
            }
            baris.add(kode + "|" + judul);
        }
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    public void muat(Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        List<Buku> dataBaru = new ArrayList<>();

        for (String satuBaris : baris) {
            if (satuBaris.trim().isEmpty()) {
                continue;
            }
            String[] bagian = satuBaris.split("\\|", -1);
            if (bagian.length != 2) {
                throw new IllegalArgumentException("Format baris tidak valid: " + satuBaris);
            }
            String kode = bagian[0].trim();
            String judul = bagian[1].trim();

            for (Buku b : dataBaru) {
                if (b.getKode().equalsIgnoreCase(kode)) {
                    throw new IllegalArgumentException("Kode buku duplikat dalam file: " + kode);
                }
            }
            dataBaru.add(new Buku(kode, judul));
        }

        // Koleksi lama hanya diganti jika seluruh file berhasil diperiksa tanpa error (Atomic Load)
        daftarBuku.clear();
        daftarBuku.addAll(dataBaru);
    }

    public void tampilkanBuku() {
        System.out.println("--- Daftar Koleksi Buku Perpustakaan ---");
        if (daftarBuku.isEmpty()) {
            System.out.println("[Koleksi kosong / belum ada data]");
        } else {
            for (Buku buku : daftarBuku) {
                System.out.println("- [" + buku.getKode() + "] " + buku.getJudul());
            }
        }
        System.out.println("Total buku: " + daftarBuku.size() + "\n");
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }
}

public class MainLab {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM DASAR MODUL 12 - PENYIMPANAN DAN PEMBACAAN DATA KE FILE");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("======================================================================\n");

        Path lokasi = Paths.get("buku.txt");
        Perpustakaan perpustakaan = new Perpustakaan();

        // Jika dipanggil dengan argumen konsol spesifik (Langkah 5)
        if (args.length > 0) {
            try {
                if (args[0].equalsIgnoreCase("simpan")) {
                    perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
                    perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
                    perpustakaan.simpan(lokasi);
                    System.out.println("Data berhasil disimpan ke " + lokasi.getFileName());
                } else if (args[0].equalsIgnoreCase("muat")) {
                    perpustakaan.muat(lokasi);
                    perpustakaan.tampilkanBuku();
                } else {
                    System.out.println("Perintah tidak dikenal. Gunakan 'simpan' atau 'muat'.");
                }
            } catch (IOException e) {
                System.out.println("Operasi file gagal: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Data ditolak: " + e.getMessage());
            }
            return;
        }

        // Mode Otomasi Pengujian Lengkap (Skenario 1 - 6 Modul 12)
        try {
            // Skenario 1: Simpan dua buku valid
            System.out.println("[SKENARIO 1] Menambahkan & Menyimpan 2 Buku Valid ke 'buku.txt':");
            perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
            perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
            perpustakaan.simpan(lokasi);
            System.out.println("✔ Sukses menulis ke file " + lokasi.toAbsolutePath());
            perpustakaan.tampilkanBuku();

            // Skenario 2: Muat kembali file dari disk ke instance baru
            System.out.println("[SKENARIO 2] Membuka Instance Baru & Memuat Data dari 'buku.txt':");
            Perpustakaan p2 = new Perpustakaan();
            p2.muat(lokasi);
            System.out.println("✔ Berhasil merekonstruksi objek dari file teks!");
            p2.tampilkanBuku();

            // Skenario 3: Muat file yang tidak ada
            System.out.println("[SKENARIO 3] Mencoba Memuat File Fiktif ('file_tidak_ada.txt'):");
            try {
                p2.muat(Paths.get("file_tidak_ada.txt"));
            } catch (IOException e) {
                System.out.println("✔ [DITANGKAP DENGAN BENAR]: Operasi file gagal (File tidak ditemukan).");
            }
            System.out.println();

            // Skenario 4: Muat file rusak (Format baris salah tanpa pemisah '|')
            System.out.println("[SKENARIO 4] Uji Integritas File Rusak (Baris 'B003' tanpa pemisah '|'):");
            Path lokasiRusak = Paths.get("buku_rusak.txt");
            Files.write(lokasiRusak, List.of("B001|Dasar Java", "B003_FormatSalah"), StandardCharsets.UTF_8);
            try {
                p2.muat(lokasiRusak);
            } catch (IllegalArgumentException e) {
                System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println();

            // Skenario 5: Uji Konsistensi State (Atomic Rollback)
            System.out.println("[SKENARIO 5] Uji Konsistensi State (Koleksi Lama Tetap Utuh Pasca Muat Gagal):");
            System.out.println("Status koleksi p2 saat ini:");
            p2.tampilkanBuku();
            System.out.println("=> Terbukti: Pemuatan file rusak tidak menghapus data buku yang sedang digunakan!");

            // Skenario 6: Uji File dengan Kode Duplikat
            System.out.println("[SKENARIO 6] Uji File dengan Kode Duplikat ('B001' muncul 2x):");
            Path lokasiDuplikat = Paths.get("buku_duplikat.txt");
            Files.write(lokasiDuplikat, List.of("B001|Dasar Java", "B001|Judul Lain"), StandardCharsets.UTF_8);
            try {
                p2.muat(lokasiDuplikat);
            } catch (IllegalArgumentException e) {
                System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println();

            // Bersihkan file uji sementara
            Files.deleteIfExists(lokasiRusak);
            Files.deleteIfExists(lokasiDuplikat);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("======================================================================");
        System.out.println("KESIMPULAN PENGUJIAN PRAKTIKUM DASAR MODUL 12:");
        System.out.println("1. Persistensi simpan dan muat berhasil bekerja lintas sesi eksekusi.");
        System.out.println("2. Atomic Loading menjamin koleksi memori terlindungi dari file korup.");
        System.out.println("======================================================================");
    }
}
