package model;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PengaturanSistem (AppConfig) - Model Konfigurasi Sistem POS & Template Manager (Fase 4 & 5)
 * Menyimpan dan mengelola:
 * 1. Template / Preset Pengaturan Tersimpan (Multi-Profile CRUD).
 * 2. Judul Aplikasi (App Title) & Subtitle (Nama, NRP, Kampus).
 * 3. Warna Tema Sistem (Header, Accent, Tombol).
 * 4. Skema Diskon Keanggotaan (CRUD Diskon Member & Persentasenya).
 * 
 * Persistensi otomatis ke file data/settings.txt secara aman (Crash-Safe Atomic Write).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class PengaturanSistem {
    private static final Path PATH_SETTINGS = Paths.get("data/settings.txt");

    private String namaProfilAktif;
    private String judulAplikasi;
    private String teksHeaderSub;
    private Color warnaHeader;
    private Color warnaAksen;
    
    // Map Tipe Member -> Persentase Diskon (0.0 s/d 1.0)
    private final Map<String, Double> daftarDiskonMember;

    // Multi-Profile Storage (Nama Profil -> ProfilPengaturan)
    private final Map<String, ProfilPengaturan> daftarProfil;

    public PengaturanSistem() {
        this.namaProfilAktif = "Settingan 1";
        this.judulAplikasi = "SISTEM KASIR POINT OF SALE (POS DESKTOP)";
        this.teksHeaderSub = "PBO 2026 | Rafael Rizky Luga Tanjung (NRP: 3125522007) | PENS PSDKU Sumenep";
        this.warnaHeader = new Color(0x1D, 0x1D, 0x1F);   // Apple Space Black (#1D1D1F)
        this.warnaAksen = new Color(0x00, 0x7A, 0xFF);    // Apple Vibrant Blue (#007AFF)
        this.daftarDiskonMember = new LinkedHashMap<>();
        this.daftarProfil = new LinkedHashMap<>();

        // Nilai bawaan diskon membership
        daftarDiskonMember.put("REGULER", 0.00);
        daftarDiskonMember.put("GOLD", 0.10);
        daftarDiskonMember.put("VIP", 0.15);

        // Buat profil default pertama
        sinkronkanProfilAktifKeMap();
    }

    // --- MANAJEMEN MULTI-PROFILE / TEMPLATE SETTINGAN ---

    public synchronized String getNamaProfilAktif() {
        return namaProfilAktif;
    }

    public synchronized void setNamaProfilAktif(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.namaProfilAktif = nama.trim();
        }
    }

    public synchronized Map<String, ProfilPengaturan> getSemuaProfil() {
        sinkronkanProfilAktifKeMap();
        return new LinkedHashMap<>(daftarProfil);
    }

    public synchronized ProfilPengaturan getProfil(String nama) {
        if (nama == null) return null;
        sinkronkanProfilAktifKeMap();
        return daftarProfil.get(nama.trim());
    }

    /**
     * Menyimpan profil baru atau memperbarui profil yang sudah ada.
     */
    public synchronized void simpanProfil(ProfilPengaturan profil) {
        if (profil == null) {
            throw new IllegalArgumentException("[ERROR] Objek profil tidak boleh null!");
        }
        daftarProfil.put(profil.getNamaProfil(), profil);
        if (profil.getNamaProfil().equalsIgnoreCase(namaProfilAktif)) {
            terapkanProfil(profil);
        }
    }

    /**
     * Mengaktifkan salah satu template/profil yang tersimpan.
     * Mengubah judul, subtitle, warna, dan seluruh skema diskon seketika.
     */
    public synchronized void aktifkanProfil(String namaProfil) {
        if (namaProfil == null || !daftarProfil.containsKey(namaProfil.trim())) {
            throw new IllegalArgumentException("[ERROR] Template profil '" + namaProfil + "' tidak ditemukan!");
        }
        ProfilPengaturan p = daftarProfil.get(namaProfil.trim());
        terapkanProfil(p);
    }

    private void terapkanProfil(ProfilPengaturan p) {
        this.namaProfilAktif = p.getNamaProfil();
        this.judulAplikasi = p.getJudulAplikasi();
        this.teksHeaderSub = p.getTeksHeaderSub();
        this.warnaHeader = p.getWarnaHeader();
        this.warnaAksen = p.getWarnaAksen();
        this.daftarDiskonMember.clear();
        this.daftarDiskonMember.putAll(p.getDaftarDiskonMember());
        if (!this.daftarDiskonMember.containsKey("REGULER")) {
            this.daftarDiskonMember.put("REGULER", 0.0);
        }
    }

    /**
     * Membuat template baru berdasarkan konfigurasi yang sedang aktif.
     */
    public synchronized void buatProfilBaruDariAktif(String namaProfilBaru) {
        if (namaProfilBaru == null || namaProfilBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Nama template baru tidak boleh kosong!");
        }
        String namaBersih = namaProfilBaru.trim();
        if (daftarProfil.containsKey(namaBersih)) {
            throw new IllegalArgumentException("[ERROR] Template dengan nama '" + namaBersih + "' sudah ada!");
        }
        ProfilPengaturan baru = new ProfilPengaturan(namaBersih, this.judulAplikasi, this.teksHeaderSub,
                this.warnaHeader, this.warnaAksen, this.daftarDiskonMember);
        daftarProfil.put(namaBersih, baru);
        this.namaProfilAktif = namaBersih;
    }

    /**
     * Menghapus salah satu template yang tersimpan.
     */
    public synchronized boolean hapusProfil(String namaProfil) {
        if (namaProfil == null) return false;
        String key = namaProfil.trim();
        if (daftarProfil.size() <= 1) {
            throw new IllegalArgumentException("[DITOLAK] Minimal harus ada 1 template settingan yang tersimpan!");
        }
        if (!daftarProfil.containsKey(key)) {
            return false;
        }
        daftarProfil.remove(key);
        // Jika yang dihapus sedang aktif, beralih ke profil pertama yang tersisa
        if (key.equalsIgnoreCase(namaProfilAktif)) {
            String firstKey = daftarProfil.keySet().iterator().next();
            aktifkanProfil(firstKey);
        }
        return true;
    }

    private void sinkronkanProfilAktifKeMap() {
        if (namaProfilAktif == null || namaProfilAktif.trim().isEmpty()) {
            namaProfilAktif = "Settingan 1";
        }
        ProfilPengaturan aktif = new ProfilPengaturan(namaProfilAktif, this.judulAplikasi, this.teksHeaderSub,
                this.warnaHeader, this.warnaAksen, this.daftarDiskonMember);
        daftarProfil.put(namaProfilAktif, aktif);
    }

    // --- GETTER & SETTER UMUM ---

    public String getJudulAplikasi() {
        return judulAplikasi;
    }

    public void setJudulAplikasi(String judulAplikasi) {
        if (judulAplikasi == null || judulAplikasi.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Judul aplikasi tidak boleh kosong!");
        }
        this.judulAplikasi = judulAplikasi.trim();
        sinkronkanProfilAktifKeMap();
    }

    public String getTeksHeaderSub() {
        return teksHeaderSub;
    }

    public void setTeksHeaderSub(String teksHeaderSub) {
        if (teksHeaderSub == null || teksHeaderSub.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Teks subtitle header tidak boleh kosong!");
        }
        this.teksHeaderSub = teksHeaderSub.trim();
        sinkronkanProfilAktifKeMap();
    }

    public Color getWarnaHeader() {
        return warnaHeader;
    }

    public void setWarnaHeader(Color warnaHeader) {
        if (warnaHeader != null) {
            this.warnaHeader = warnaHeader;
            sinkronkanProfilAktifKeMap();
        }
    }

    public Color getWarnaAksen() {
        return warnaAksen;
    }

    public void setWarnaAksen(Color warnaAksen) {
        if (warnaAksen != null) {
            this.warnaAksen = warnaAksen;
            sinkronkanProfilAktifKeMap();
        }
    }

    // --- CRUD DISKON MEMBERSHIP ---

    public synchronized Map<String, Double> getDaftarDiskonMember() {
        return new LinkedHashMap<>(daftarDiskonMember);
    }

    public synchronized double getDiskon(String tipeMember) {
        if (tipeMember == null) return 0.0;
        return daftarDiskonMember.getOrDefault(tipeMember.trim().toUpperCase(), 0.0);
    }

    public synchronized void simpanDiskonMember(String tipeMember, double diskon) {
        if (tipeMember == null || tipeMember.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama tipe member tidak boleh kosong!");
        }
        if (diskon < 0.0 || diskon > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Persentase diskon harus di antara 0.0 dan 1.0! (0% s/d 100%)");
        }
        daftarDiskonMember.put(tipeMember.trim().toUpperCase(), diskon);
        sinkronkanProfilAktifKeMap();
    }

    public synchronized boolean hapusDiskonMember(String tipeMember) {
        if (tipeMember == null) return false;
        String key = tipeMember.trim().toUpperCase();
        if ("REGULER".equals(key)) {
            throw new IllegalArgumentException("[DITOLAK] Tipe member 'REGULER' adalah kategori dasar dan tidak dapat dihapus!");
        }
        boolean ok = daftarDiskonMember.remove(key) != null;
        if (ok) {
            sinkronkanProfilAktifKeMap();
        }
        return ok;
    }

    public synchronized boolean adaTipeMember(String tipeMember) {
        if (tipeMember == null) return false;
        return daftarDiskonMember.containsKey(tipeMember.trim().toUpperCase());
    }

    // --- PERSISTENSI KE FILE (CRASH-SAFE ATOMIC WRITE) ---

    public synchronized void simpanKeFile() throws IOException {
        sinkronkanProfilAktifKeMap();
        List<String> lines = new ArrayList<>();
        lines.add("# KONFIGURASI TAMPILAN DAN SKEMA DISKON SISTEM POS");
        lines.add("ACTIVE_PROFILE=" + namaProfilAktif);
        lines.add("TITLE=" + judulAplikasi);
        lines.add("SUBTITLE=" + teksHeaderSub);
        lines.add("HEADER_COLOR=" + String.format("#%06X", (0xFFFFFF & warnaHeader.getRGB())));
        lines.add("ACCENT_COLOR=" + String.format("#%06X", (0xFFFFFF & warnaAksen.getRGB())));
        lines.add("# DAFTAR DISKON AKTIF: TIPE_MEMBER|DISKON_DESIMAL");
        for (Map.Entry<String, Double> entry : daftarDiskonMember.entrySet()) {
            lines.add("MEMBER_DISCOUNT=" + entry.getKey() + "|" + entry.getValue());
        }

        lines.add("# DAFTAR TEMPLATE TERSIMPAN: PROFILE_ENTRY=NAMA;;;TITLE;;;SUBTITLE;;;HEADER_HEX;;;ACCENT_HEX;;;DISCOUNT_ENTRIES");
        for (ProfilPengaturan p : daftarProfil.values()) {
            StringBuilder sbDisc = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Double> disc : p.getDaftarDiskonMember().entrySet()) {
                if (!first) sbDisc.append(",");
                sbDisc.append(disc.getKey()).append(":").append(disc.getValue());
                first = false;
            }
            String hdrHex = String.format("#%06X", (0xFFFFFF & p.getWarnaHeader().getRGB()));
            String accHex = String.format("#%06X", (0xFFFFFF & p.getWarnaAksen().getRGB()));
            lines.add("PROFILE_ENTRY=" + p.getNamaProfil() + ";;;" + p.getJudulAplikasi() + ";;;" +
                    p.getTeksHeaderSub() + ";;;" + hdrHex + ";;;" + accHex + ";;;" + sbDisc.toString());
        }

        FileIoUtil.atomicWriteLines(PATH_SETTINGS, lines);
    }

    public synchronized void muatDariFile() {
        if (!Files.exists(PATH_SETTINGS)) {
            sinkronkanProfilAktifKeMap();
            return;
        }
        try {
            List<String> lines = Files.readAllLines(PATH_SETTINGS, StandardCharsets.UTF_8);
            boolean adaProfileEntry = false;

            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("ACTIVE_PROFILE=")) {
                    this.namaProfilAktif = line.substring("ACTIVE_PROFILE=".length()).trim();
                } else if (line.startsWith("TITLE=")) {
                    this.judulAplikasi = line.substring("TITLE=".length()).trim();
                } else if (line.startsWith("SUBTITLE=")) {
                    this.teksHeaderSub = line.substring("SUBTITLE=".length()).trim();
                } else if (line.startsWith("HEADER_COLOR=")) {
                    try {
                        this.warnaHeader = Color.decode(line.substring("HEADER_COLOR=".length()).trim());
                    } catch (Exception ignored) {}
                } else if (line.startsWith("ACCENT_COLOR=")) {
                    try {
                        this.warnaAksen = Color.decode(line.substring("ACCENT_COLOR=".length()).trim());
                    } catch (Exception ignored) {}
                } else if (line.startsWith("MEMBER_DISCOUNT=")) {
                    String[] parts = line.substring("MEMBER_DISCOUNT=".length()).split("\\|");
                    if (parts.length == 2) {
                        try {
                            String m = parts[0].trim().toUpperCase();
                            double d = Double.parseDouble(parts[1].trim());
                            daftarDiskonMember.put(m, d);
                        } catch (Exception ignored) {}
                    }
                } else if (line.startsWith("PROFILE_ENTRY=")) {
                    adaProfileEntry = true;
                    muatProfileEntryLine(line.substring("PROFILE_ENTRY=".length()));
                }
            }

            if (!adaProfileEntry) {
                // Berkas lama / pertama kali migrasi: jadikan konfigurasi yang termuat sebagai profil awal
                sinkronkanProfilAktifKeMap();
            } else if (daftarProfil.containsKey(this.namaProfilAktif)) {
                // Terapkan profil yang aktif
                terapkanProfil(daftarProfil.get(this.namaProfilAktif));
            }
        } catch (Exception ex) {
            System.err.println("[WARN SETTINGS] Gagal membaca data/settings.txt: " + ex.getMessage());
            sinkronkanProfilAktifKeMap();
        }
    }

    private void muatProfileEntryLine(String content) {
        String[] parts = content.split(";;;");
        if (parts.length >= 6) {
            try {
                String nama = parts[0].trim();
                String title = parts[1].trim();
                String sub = parts[2].trim();
                Color hdr = Color.decode(parts[3].trim());
                Color acc = Color.decode(parts[4].trim());
                Map<String, Double> discMap = new LinkedHashMap<>();
                String[] pairs = parts[5].split(",");
                for (String pr : pairs) {
                    String[] kv = pr.split(":");
                    if (kv.length == 2) {
                        discMap.put(kv[0].trim().toUpperCase(), Double.parseDouble(kv[1].trim()));
                    }
                }
                ProfilPengaturan p = new ProfilPengaturan(nama, title, sub, hdr, acc, discMap);
                daftarProfil.put(nama, p);
            } catch (Exception ignored) {}
        }
    }
}
