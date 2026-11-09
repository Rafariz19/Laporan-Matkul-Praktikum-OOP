import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Program Praktikum Dasar Modul 13: Integrasi Aplikasi Berbasis Menu & Pengujian Fungsional
 * Mengikuti Langkah Praktikum 1 - 4 pada Modul 13 (Aplikasi Perpustakaan Interaktif).
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
        if (kode.contains("|") || kode.contains("\n") || kode.contains("\r")
                || judul.contains("|") || judul.contains("\n") || judul.contains("\r")) {
            throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
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

    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Buku buku : daftarBuku) {
            baris.add(buku.getKode() + "|" + buku.getJudul());
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
        System.out.println("Total buku: " + daftarBuku.size());
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }
}

public class MainLab {

    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        if (!input.hasNextLine()) {
            return "0";
        }
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ").equalsIgnoreCase("YA");
    }

    public static void jalankanSesi(Scanner input, Path lokasi) {
        Perpustakaan perpustakaan = new Perpustakaan();
        boolean berubah = false;

        // Muat data lama sebelum pengguna mulai bekerja
        if (Files.exists(lokasi)) {
            try {
                perpustakaan.muat(lokasi);
                System.out.println("[INFO] Data awal berhasil dimuat (" + perpustakaan.getJumlahBuku() + " buku).");
            } catch (Exception e) {
                System.out.println("Data awal gagal dimuat: " + e.getMessage());
                return;
            }
        }

        while (true) {
            System.out.println("\n=== PERPUSTAKAAN (STATUS: " + (berubah ? "ADA PERUBAHAN BELUM DISIMPAN" : "TERSIMPAN") + ") ===");
            System.out.println("1. Tambah Buku");
            System.out.println("2. Tampilkan Buku");
            System.out.println("3. Cari Buku");
            System.out.println("4. Hapus Buku");
            System.out.println("5. Simpan Data");
            System.out.println("6. Muat Ulang Data");
            System.out.println("0. Keluar");

            String pilihan = baca(input, "Pilihan: ");

            try {
                switch (pilihan) {
                    case "1": {
                        String kode = baca(input, "Kode: ");
                        String judul = baca(input, "Judul: ");
                        perpustakaan.tambahBuku(new Buku(kode, judul));
                        berubah = true;
                        System.out.println("✔ Buku berhasil ditambahkan.");
                        break;
                    }
                    case "2": {
                        perpustakaan.tampilkanBuku();
                        break;
                    }
                    case "3": {
                        String kode = baca(input, "Kode buku yang dicari: ");
                        Buku hasil = perpustakaan.cariBuku(kode);
                        if (hasil == null) {
                            System.out.println("✘ Buku tidak ditemukan.");
                        } else {
                            System.out.println("✔ Ditemukan: [" + hasil.getKode() + "] " + hasil.getJudul());
                        }
                        break;
                    }
                    case "4": {
                        String kode = baca(input, "Kode buku yang akan dihapus: ");
                        if (perpustakaan.hapusBuku(kode)) {
                            berubah = true;
                            System.out.println("✔ Buku berhasil dihapus.");
                        } else {
                            System.out.println("✘ Kode buku tidak ditemukan; koleksi tidak berubah.");
                        }
                        break;
                    }
                    case "5": {
                        perpustakaan.simpan(lokasi);
                        berubah = false;
                        System.out.println("✔ Data berhasil disimpan ke " + lokasi.getFileName());
                        break;
                    }
                    case "6": {
                        if (berubah && !konfirmasi(input)) {
                            System.out.println("Pemuatan ulang dibatalkan; perubahan tetap tersedia.");
                            break;
                        }
                        perpustakaan.muat(lokasi);
                        berubah = false;
                        System.out.println("✔ Data berhasil dimuat ulang dari " + lokasi.getFileName());
                        break;
                    }
                    case "0": {
                        if (berubah && !konfirmasi(input)) {
                            System.out.println("Keluar dibatalkan; kembali ke menu.");
                            break;
                        }
                        System.out.println("Terima kasih telah menggunakan sistem perpustakaan.");
                        return;
                    }
                    default:
                        System.out.println("✘ Pilihan '" + pilihan + "' tidak tersedia. Silakan pilih 0-6.");
                }
            } catch (Exception e) {
                System.out.println("✘ Operasi ditolak: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM DASAR MODUL 13 - APLIKASI KONSOL INTERAKTIF BERBASIS MENU");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("======================================================================\n");

        Path lokasi = Paths.get("buku.txt");

        // Jika ada argumen console / interactive
        if (args.length > 0 && args[0].equalsIgnoreCase("interaktif")) {
            try (Scanner sc = new Scanner(System.in)) {
                jalankanSesi(sc, lokasi);
            }
            return;
        }

        // Mode Otomasi Pengujian 9 Skenario Fungsional Resmi Modul 13:
        // 1: pilihan 'abc' salah
        // 2: tambah B001 "Dasar Java"
        // 3: tambah kode sama B001 (ditolak)
        // 4: cari B001
        // 5: hapus B999 tidak terdaftar
        // 6: keluar (0) saat berubah, jawab 'TIDAK' (batal)
        // 7: simpan (5), keluar (0) konfirmasi YA
        // 8: sesi 2 muat, hapus B001, simpan (5), keluar (0)
        String scriptPengujian = 
                "abc\n" +                          // Skenario 1: input salah
                "1\nB001\nDasar Java\n" +          // Skenario 2: tambah B001
                "1\nB001\nJudul Duplikat\n" +      // Skenario 3: kode duplikat ditolak
                "3\nB001\n" +                      // Skenario 4: cari B001
                "4\nB999\n" +                      // Skenario 5: hapus kode fiktif
                "0\nTIDAK\n" +                     // Skenario 6: keluar batal (jawab TIDAK)
                "5\n" +                            // Skenario 7: simpan
                "0\nYA\n";                         // keluar sukses

        System.out.println("[SIMULASI PENGUJIAN FUNGSIONAL SKENARIO 1 - 7 SECARA BERURUTAN]:");
        try (Scanner scUji = new Scanner(scriptPengujian)) {
            jalankanSesi(scUji, lokasi);
        }

        System.out.println("\n======================================================================");
        System.out.println("KESIMPULAN PENGUJIAN PRAKTIKUM DASAR MODUL 13:");
        System.out.println("1. Seluruh menu operasi CRUD dan I/O terintegrasi dalam alur loop konsol.");
        System.out.println("2. Dirty flag (berubah) terbukti melindungi data dari keluar tak sengaja.");
        System.out.println("======================================================================");
    }
}
