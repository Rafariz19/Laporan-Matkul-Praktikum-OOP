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
 * PengaturanSistem (AppConfig) - Model Konfigurasi Sistem POS & CRUD Diskon (Fase 4)
 * Menyimpan dan mengelola:
 * 1. Judul Aplikasi (App Title) & Subtitle (Nama, NRP, Kampus).
 * 2. Warna Tema Sistem (Header, Accent, Tombol).
 * 3. Skema Diskon Keanggotaan (CRUD Diskon Member & Persentasenya).
 * 
 * Persistensi otomatis ke file data/settings.txt secara aman (Crash-Safe).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class PengaturanSistem {
    private static final Path PATH_SETTINGS = Paths.get("data/settings.txt");

    private String judulAplikasi;
    private String teksHeaderSub;
    private Color warnaHeader;
    private Color warnaAksen;
    
    // Map Tipe Member -> Persentase Diskon (0.0 s/d 1.0)
    private final Map<String, Double> daftarDiskonMember;

    public PengaturanSistem() {
        this.judulAplikasi = "SISTEM KASIR POINT OF SALE (POS DESKTOP)";
        this.teksHeaderSub = "PBO 2026 | Rafael Rizky Luga Tanjung (NRP: 3125522007) | PENS PSDKU Sumenep";
        this.warnaHeader = new Color(27, 54, 93);   // Professional Navy Blue
        this.warnaAksen = new Color(0, 150, 136);    // Modern Teal
        this.daftarDiskonMember = new LinkedHashMap<>();

        // Nilai bawaan diskon membership
        daftarDiskonMember.put("REGULER", 0.00);
        daftarDiskonMember.put("GOLD", 0.10);
        daftarDiskonMember.put("VIP", 0.15);
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
    }

    public String getTeksHeaderSub() {
        return teksHeaderSub;
    }

    public void setTeksHeaderSub(String teksHeaderSub) {
        if (teksHeaderSub == null || teksHeaderSub.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Teks subtitle header tidak boleh kosong!");
        }
        this.teksHeaderSub = teksHeaderSub.trim();
    }

    public Color getWarnaHeader() {
        return warnaHeader;
    }

    public void setWarnaHeader(Color warnaHeader) {
        if (warnaHeader != null) {
            this.warnaHeader = warnaHeader;
        }
    }

    public Color getWarnaAksen() {
        return warnaAksen;
    }

    public void setWarnaAksen(Color warnaAksen) {
        if (warnaAksen != null) {
            this.warnaAksen = warnaAksen;
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

    /**
     * Menambah atau memperbarui tipe membership dan nilai diskonnya (CRUD Diskon).
     * 
     * @param tipeMember nama tipe member (misal: "VIP", "PLATINUM", "KARYAWAN")
     * @param diskon     nilai desimal 0.0 s/d 1.0 (misal 0.20 untuk 20%)
     */
    public synchronized void simpanDiskonMember(String tipeMember, double diskon) {
        if (tipeMember == null || tipeMember.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama tipe member tidak boleh kosong!");
        }
        if (diskon < 0.0 || diskon > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Persentase diskon harus di antara 0.0 dan 1.0! (0% s/d 100%)");
        }
        daftarDiskonMember.put(tipeMember.trim().toUpperCase(), diskon);
    }

    /**
     * Menghapus tipe membership dari skema diskon.
     */
    public synchronized boolean hapusDiskonMember(String tipeMember) {
        if (tipeMember == null) return false;
        String key = tipeMember.trim().toUpperCase();
        if ("REGULER".equals(key)) {
            throw new IllegalArgumentException("[DITOLAK] Tipe member 'REGULER' adalah kategori dasar dan tidak dapat dihapus!");
        }
        return daftarDiskonMember.remove(key) != null;
    }

    public synchronized boolean adaTipeMember(String tipeMember) {
        if (tipeMember == null) return false;
        return daftarDiskonMember.containsKey(tipeMember.trim().toUpperCase());
    }

    // --- PERSISTENSI KE FILE (CRASH-SAFE ATOMIC WRITE) ---

    public synchronized void simpanKeFile() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# KONFIGURASI TAMPILAN DAN SKEMA DISKON SISTEM POS");
        lines.add("TITLE=" + judulAplikasi);
        lines.add("SUBTITLE=" + teksHeaderSub);
        lines.add("HEADER_COLOR=" + String.format("#%06X", (0xFFFFFF & warnaHeader.getRGB())));
        lines.add("ACCENT_COLOR=" + String.format("#%06X", (0xFFFFFF & warnaAksen.getRGB())));
        lines.add("# DAFTAR DISKON: TIPE_MEMBER|DISKON_DESIMAL");
        for (Map.Entry<String, Double> entry : daftarDiskonMember.entrySet()) {
            lines.add("MEMBER_DISCOUNT=" + entry.getKey() + "|" + entry.getValue());
        }
        FileIoUtil.atomicWriteLines(PATH_SETTINGS, lines);
    }

    public synchronized void muatDariFile() {
        if (!Files.exists(PATH_SETTINGS)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(PATH_SETTINGS, StandardCharsets.UTF_8);
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("TITLE=")) {
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
                }
            }
        } catch (Exception ex) {
            System.err.println("[WARN SETTINGS] Gagal membaca data/settings.txt: " + ex.getMessage());
        }
    }
}
