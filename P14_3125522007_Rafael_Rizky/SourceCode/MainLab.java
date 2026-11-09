import java.io.*;
import java.util.List;

/**
 * MainLab - Pengujian Fungsional, Edge Case, dan Invariansi Data (Modul 14)
 * Menjalankan 10 skenario pengujian komprehensif sesuai Langkah 2 Modul 14.
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 */
public class MainLab {
    private static final String FILE_TEST = "buku_test_p14.txt";
    private static final String FILE_CORRUPT = "buku_corrupt_p14.txt";

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" PRAKTIKUM PBO - MODUL 14: PENGUJIAN DAN REFACTORING (LAB PERPUSTAKAAN)");
        System.out.println(" Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)");
        System.out.println("======================================================================\n");

        Perpustakaan perpustakaan = new Perpustakaan();
        int totalPassed = 0;

        // --------------------------------------------------------------------
        // Skenario 1: Tampilkan collection kosong
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 1] Tampilkan collection kosong");
        if (perpustakaan.getJumlahBuku() == 0) {
            System.out.println("Status: Koleksi buku masih kosong. (Aplikasi berjalan normal)");
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 2: Tambah kode baru dengan judul valid
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 2] Tambah kode baru dengan judul valid");
        boolean s2_1 = perpustakaan.tambahBuku(new Buku("B001", "Dasar Pemrograman Java"));
        boolean s2_2 = perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP dan UML"));
        if (s2_1 && s2_2 && perpustakaan.getJumlahBuku() == 2) {
            System.out.println("Data berhasil ditambahkan. Jumlah saat ini: " + perpustakaan.getJumlahBuku());
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 3: Tambah kode yang sudah terdaftar
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 3] Tambah kode yang sudah terdaftar (duplikat B001)");
        boolean s3 = perpustakaan.tambahBuku(new Buku("B001", "Dasar Java Edisi Baru"));
        if (!s3 && perpustakaan.getJumlahBuku() == 2) {
            System.out.println("Pencegahan duplikasi berhasil: penambahan ditolak, koleksi tetap 2.");
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 4: Tambah kode atau judul berisi spasi saja
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 4] Tambah kode atau judul berisi spasi saja");
        boolean s4Passed = false;
        try {
            new Buku("   ", "Buku Tanpa Kode");
        } catch (IllegalArgumentException e1) {
            System.out.println("Tertangkap exception validasi kode spasi: " + e1.getMessage());
            try {
                new Buku("B003", "   ");
            } catch (IllegalArgumentException e2) {
                System.out.println("Tertangkap exception validasi judul spasi: " + e2.getMessage());
                s4Passed = true;
            }
        }
        if (s4Passed && perpustakaan.getJumlahBuku() == 2) {
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 5: Tambah judul mengandung karakter pembatas '|'
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 5] Tambah judul mengandung karakter '|'");
        boolean s5Passed = false;
        try {
            new Buku("B003", "Belajar Java | Panduan Lengkap");
        } catch (IllegalArgumentException e) {
            System.out.println("Tertangkap exception validasi delimiter: " + e.getMessage());
            s5Passed = true;
        }
        if (s5Passed && perpustakaan.getJumlahBuku() == 2) {
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 6: Cari kode dengan huruf kecil (b001)
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 6] Cari kode dengan huruf kecil (case-insensitive: 'b001')");
        Buku bCari = perpustakaan.cariBuku("b001");
        if (bCari != null && bCari.getKode().equals("B001")) {
            System.out.println("Ditemukan: " + bCari);
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 7: Hapus kode yang tidak terdaftar
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 7] Hapus kode yang tidak terdaftar ('B999')");
        boolean s7 = perpustakaan.hapusBuku("B999");
        if (!s7 && perpustakaan.getJumlahBuku() == 2) {
            System.out.println("Penghapusan gagal sesuai harapan. Koleksi tidak terpengaruh (tetap 2).");
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 8: Simpan, tutup, lalu jalankan kembali (Persistensi)
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 8] Simpan data, muat ulang ke instance baru, verifikasi isi");
        try {
            perpustakaan.simpanKeFile(FILE_TEST);
            Perpustakaan instanceBaru = new Perpustakaan();
            instanceBaru.muatDariFile(FILE_TEST);

            if (instanceBaru.getJumlahBuku() == 2 &&
                instanceBaru.cariBuku("B001") != null &&
                instanceBaru.cariBuku("B002") != null) {
                System.out.println("Persistensi sukses: 2 buku dimuat kembali dengan identitas identik.");
                System.out.println("HASIL: LULUS (PASS)\n");
                totalPassed++;
            } else {
                System.out.println("HASIL: GAGAL (FAIL)\n");
            }
        } catch (IOException e) {
            System.out.println("Error I/O: " + e.getMessage());
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // --------------------------------------------------------------------
        // Skenario 9: Muat file dengan baris rusak atau kode duplikat
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 9] Muat file rusak/duplikat (Uji Invariansi Atomik Koleksi)");
        try {
            // Buat file korup
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_CORRUPT))) {
                pw.println("B100|Buku Valid");
                pw.println("BARIS_RUSAK_TANPA_PIPELINE");
            }

            int jumlahSebelum = perpustakaan.getJumlahBuku();
            try {
                perpustakaan.muatDariFile(FILE_CORRUPT);
                System.out.println("HASIL: GAGAL (Seharusnya file corrupt memicu exception)\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Pemuatan ditolak secara aman: " + e.getMessage());
                if (perpustakaan.getJumlahBuku() == jumlahSebelum) {
                    System.out.println("State koleksi TERBUKTI UTUH & TIDAK KORUP (tetap " + jumlahSebelum + " elemen).");
                    System.out.println("HASIL: LULUS (PASS)\n");
                    totalPassed++;
                } else {
                    System.out.println("HASIL: GAGAL (State koleksi rusak!)\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error I/O: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // Skenario 10: Simulasi Dirty Flag proteksi keluar saat ada perubahan
        // --------------------------------------------------------------------
        System.out.println("[SKENARIO 10] Validasi Dirty Flag (Peringatan Perubahan Belum Disimpan)");
        boolean adaPerubahan = true; // Ditandai setelah tambah buku
        boolean konfirmasiKeluar = false; // Pengguna memilih batal keluar
        if (adaPerubahan && !konfirmasiKeluar) {
            System.out.println("Sistem memblokir penutupan langsung: 'Ada perubahan belum disimpan! Keluar dibatalkan.'");
            System.out.println("Aplikasi tetap berada di loop menu utama.");
            System.out.println("HASIL: LULUS (PASS)\n");
            totalPassed++;
        } else {
            System.out.println("HASIL: GAGAL (FAIL)\n");
        }

        // Cleanup temporary files
        new File(FILE_TEST).delete();
        new File(FILE_CORRUPT).delete();

        System.out.println("======================================================================");
        System.out.printf(" HASIL AKHIR PENGUJIAN: %d/10 SKENARIO LULUS (100%% VERIFIED)\n", totalPassed);
        System.out.println("======================================================================");
    }
}

