package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import model.Pelanggan;
import model.PengaturanSistem;

/**
 * TestPengaturanSistemDanDiskon:
 * Menguji fitur Fase 4:
 * 1. CRUD Judul Aplikasi & Subtitle Identitas Sistem Kasir.
 * 2. CRUD Skema Diskon Keanggotaan (Tambah tipe member baru, edit nilai diskon, hapus diskon).
 * 3. Integrasi Pelanggan dengan PengaturanSistem (menghitung diskon kustom secara dinamis).
 * 4. Persistensi Crash-Safe ke file data/settings.txt dan muat ulang saat startup.
 */
public class TestPengaturanSistemDanDiskon {
    public static void main(String[] args) {
        System.out.println(">>> Menjalankan TestPengaturanSistemDanDiskon...");

        PengaturanSistem config = new PengaturanSistem();

        // 1. Uji Default Settings
        if (!config.getJudulAplikasi().contains("SISTEM KASIR")) {
            throw new AssertionError("Gagal: Judul default salah!");
        }
        if (!config.getTeksHeaderSub().contains("Rafael Rizky")) {
            throw new AssertionError("Gagal: Subtitle default salah!");
        }
        System.out.println("  1. Inisialisasi default judul & subtitle berhasil.");

        // 2. Uji CRUD Judul & Subtitle
        String judulBaru = "POS MINIMARKET MAJU JAYA";
        String subBaru = "Admin: Rafael | Versi 2026";
        config.setJudulAplikasi(judulBaru);
        config.setTeksHeaderSub(subBaru);
        if (!judulBaru.equals(config.getJudulAplikasi()) || !subBaru.equals(config.getTeksHeaderSub())) {
            throw new AssertionError("Gagal update judul/subtitle!");
        }
        System.out.println("  2. Update judul & subtitle identitas berhasil.");

        // 3. Uji CRUD Diskon Membership
        // Tambah kategori member baru: "PLATINUM" -> 25% (0.25)
        config.simpanDiskonMember("PLATINUM", 0.25);
        if (Math.abs(config.getDiskon("PLATINUM") - 0.25) > 0.001) {
            throw new AssertionError("Gagal tambah diskon member PLATINUM!");
        }

        // Edit diskon GOLD dari 10% menjadi 12%
        config.simpanDiskonMember("GOLD", 0.12);
        if (Math.abs(config.getDiskon("GOLD") - 0.12) > 0.001) {
            throw new AssertionError("Gagal edit diskon member GOLD!");
        }

        // Tambah member "KARYAWAN" -> 30% lalu Hapus
        config.simpanDiskonMember("KARYAWAN", 0.30);
        if (!config.adaTipeMember("KARYAWAN")) {
            throw new AssertionError("Gagal tambah diskon KARYAWAN!");
        }
        boolean hapusOk = config.hapusDiskonMember("KARYAWAN");
        if (!hapusOk || config.adaTipeMember("KARYAWAN")) {
            throw new AssertionError("Gagal hapus diskon KARYAWAN!");
        }

        // Cek proteksi hapus REGULER
        boolean regProtected = false;
        try {
            config.hapusDiskonMember("REGULER");
        } catch (IllegalArgumentException e) {
            regProtected = true;
            System.out.println("  [OK Proteksi] Tipe REGULER tidak boleh dihapus: " + e.getMessage());
        }
        if (!regProtected) {
            throw new AssertionError("Gagal proteksi hapus member REGULER!");
        }
        System.out.println("  3. CRUD Diskon Membership (Tambah, Edit, Hapus, Proteksi) berhasil.");

        // 4. Uji Integrasi Pelanggan dengan PengaturanSistem Dinamis
        Pelanggan p1 = new Pelanggan("CUST-P", "Budi Platinum", "081234567890", "PLATINUM", config);
        double potongan = p1.hitungPotonganMember(100000); // 100.000 * 25% = 25.000
        if (potongan != 25000.0) {
            throw new AssertionError("Gagal hitung diskon member kustom PLATINUM! Didapat: " + potongan);
        }
        System.out.println("  4. Integrasi Pelanggan dengan PengaturanSistem terverifikasi: Diskon PLATINUM = Rp " + potongan);

        // 5. Uji Persistensi ke Disk dan Muat Ulang
        try {
            config.simpanKeFile();
            PengaturanSistem configMuat = new PengaturanSistem();
            configMuat.muatDariFile();

            if (!judulBaru.equals(configMuat.getJudulAplikasi())) {
                throw new AssertionError("Gagal simpan/muat judul dari file!");
            }
            if (Math.abs(configMuat.getDiskon("PLATINUM") - 0.25) > 0.001) {
                throw new AssertionError("Gagal simpan/muat diskon PLATINUM dari file!");
            }
            System.out.println("  5. Persistensi Crash-Safe data/settings.txt dan muat ulang berhasil 100%.");
        } catch (IOException e) {
            throw new RuntimeException("Gagal simpan ke file: " + e.getMessage(), e);
        }

        System.out.println("[PASS] TestPengaturanSistemDanDiskon berhasil!");
    }
}
