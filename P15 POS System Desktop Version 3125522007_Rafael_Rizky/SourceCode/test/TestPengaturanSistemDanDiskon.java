package test;

import java.io.IOException;
import java.util.Map;
import model.Pelanggan;
import model.PengaturanSistem;
import model.ProfilPengaturan;

/**
 * TestPengaturanSistemDanDiskon:
 * Menguji fitur Fase 4 & 5:
 * 1. CRUD Judul Aplikasi & Subtitle Identitas Sistem Kasir.
 * 2. CRUD Skema Diskon Keanggotaan (Tambah tipe member baru, edit nilai diskon, hapus diskon).
 * 3. Integrasi Pelanggan dengan PengaturanSistem (menghitung diskon kustom secara dinamis).
 * 4. Persistensi Crash-Safe ke file data/settings.txt dan muat ulang saat startup.
 * 5. Multi-Profile / Template Pengaturan (CRUD Template Settingan & Switch Profil).
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

        // 5. Uji Multi-Profile / Template Settingan (Fase 5)
        config.buatProfilBaruDariAktif("Settingan 2");
        if (!"Settingan 2".equals(config.getNamaProfilAktif())) {
            throw new AssertionError("Gagal mengaktifkan template Settingan 2!");
        }

        // Modifikasi Settingan 2 dengan title beda, diskon beda, dan kategori beda
        config.setJudulAplikasi("SUPERMARKET CABANG MALANG");
        config.simpanDiskonMember("VIP", 0.40); // 40%
        config.tambahKategori("Peralatan");
        if (!"SUPERMARKET CABANG MALANG".equals(config.getJudulAplikasi()) 
                || Math.abs(config.getDiskon("VIP") - 0.40) > 0.001
                || !config.getDaftarKategori().contains("Peralatan")) {
            throw new AssertionError("Gagal modifikasi Settingan 2!");
        }

        // Switch kembali ke profil lama "Settingan 1"
        config.aktifkanProfil("Settingan 1");
        if (!judulBaru.equals(config.getJudulAplikasi())) {
            throw new AssertionError("Gagal switch kembali ke Settingan 1! Didapat judul: " + config.getJudulAplikasi());
        }
        if (Math.abs(config.getDiskon("VIP") - 0.15) > 0.001) {
            throw new AssertionError("Gagal memulihkan diskon VIP di Settingan 1!");
        }
        if (config.getDaftarKategori().contains("Peralatan")) {
            throw new AssertionError("Kategori 'Peralatan' seharusnya tidak ada di Settingan 1!");
        }

        // Switch lagi ke "Settingan 2"
        config.aktifkanProfil("Settingan 2");
        if (!"SUPERMARKET CABANG MALANG".equals(config.getJudulAplikasi()) 
                || Math.abs(config.getDiskon("VIP") - 0.40) > 0.001
                || !config.getDaftarKategori().contains("Peralatan")) {
            throw new AssertionError("Gagal switch kembali ke Settingan 2!");
        }
        System.out.println("  5. Multi-Profile Template CRUD & Switching (Identitas, Diskon, & Kategori Terisolasi) terverifikasi.");

        // 6. Uji Persistensi Multi-Profile ke Disk dan Muat Ulang
        try {
            config.simpanKeFile();
            PengaturanSistem configMuat = new PengaturanSistem();
            configMuat.muatDariFile();

            if (!"Settingan 2".equals(configMuat.getNamaProfilAktif())) {
                throw new AssertionError("Gagal simpan/muat active profile!");
            }
            if (!"SUPERMARKET CABANG MALANG".equals(configMuat.getJudulAplikasi())) {
                throw new AssertionError("Gagal simpan/muat judul Settingan 2 dari file!");
            }
            if (Math.abs(configMuat.getDiskon("VIP") - 0.40) > 0.001) {
                throw new AssertionError("Gagal simpan/muat diskon VIP 40% Settingan 2!");
            }
            if (!configMuat.getDaftarKategori().contains("Peralatan")) {
                throw new AssertionError("Gagal simpan/muat kategori 'Peralatan' Settingan 2 dari file!");
            }

            // Buktikan Settingan 1 juga ada di file hasil muat dan kategorinya independen
            ProfilPengaturan prof1 = configMuat.getProfil("Settingan 1");
            if (prof1 == null || !judulBaru.equals(prof1.getJudulAplikasi())) {
                throw new AssertionError("Gagal memuat template Settingan 1 dari file!");
            }
            if (prof1.getDaftarKategori().contains("Peralatan")) {
                throw new AssertionError("Settingan 1 seharusnya tidak memuat kategori 'Peralatan'!");
            }
            System.out.println("  6. Persistensi Multi-Profile data/settings.txt dan muat ulang berhasil 100%.");
        } catch (IOException e) {
            throw new RuntimeException("Gagal simpan ke file: " + e.getMessage(), e);
        }

        System.out.println("[PASS] TestPengaturanSistemDanDiskon berhasil!");
    }
}
