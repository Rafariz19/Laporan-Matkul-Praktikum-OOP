package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ProfilPengaturan - Blueprint & Data Holder Template Konfigurasi Sistem POS.
 * Mengemas snapshot profil/template lengkap:
 * 1. Nama Template / Profil (e.g. "Settingan 1", "Promo Lebaran", "Toko Cabang").
 * 2. Judul Aplikasi & Teks Subtitle Identitas.
 * 3. Skema Warna Header & Aksen.
 * 4. Skema Diskon Keanggotaan Member.
 * 5. Pengaturan Kategori Produk Independen per Template.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class ProfilPengaturan {
    private String namaProfil;
    private String judulAplikasi;
    private String teksHeaderSub;
    private Color warnaHeader;
    private Color warnaAksen;
    private final Map<String, Double> daftarDiskonMember;
    private final List<String> daftarKategori;

    public ProfilPengaturan(String namaProfil, String judulAplikasi, String teksHeaderSub,
                            Color warnaHeader, Color warnaAksen, Map<String, Double> diskonMap) {
        this(namaProfil, judulAplikasi, teksHeaderSub, warnaHeader, warnaAksen, diskonMap, null);
    }

    @SuppressWarnings("this-escape")
    public ProfilPengaturan(String namaProfil, String judulAplikasi, String teksHeaderSub,
                            Color warnaHeader, Color warnaAksen, Map<String, Double> diskonMap,
                            List<String> kategoriList) {
        setNamaProfil(namaProfil);
        setJudulAplikasi(judulAplikasi);
        setTeksHeaderSub(teksHeaderSub);
        this.warnaHeader = (warnaHeader != null) ? warnaHeader : new Color(0x1D, 0x1D, 0x1F);
        this.warnaAksen = (warnaAksen != null) ? warnaAksen : new Color(0x00, 0x7A, 0xFF);
        this.daftarDiskonMember = new LinkedHashMap<>();
        if (diskonMap != null && !diskonMap.isEmpty()) {
            this.daftarDiskonMember.putAll(diskonMap);
        }
        if (!this.daftarDiskonMember.containsKey("REGULER")) {
            this.daftarDiskonMember.put("REGULER", 0.0);
        }
        this.daftarKategori = new ArrayList<>();
        if (kategoriList != null && !kategoriList.isEmpty()) {
            for (String k : kategoriList) {
                if (k != null && !k.trim().isEmpty() && !this.daftarKategori.contains(k.trim())) {
                    this.daftarKategori.add(k.trim());
                }
            }
        }
        if (!this.daftarKategori.contains("Makanan")) this.daftarKategori.add(0, "Makanan");
        if (!this.daftarKategori.contains("Minuman")) this.daftarKategori.add(1, "Minuman");
        if (!this.daftarKategori.contains("Elektronik")) this.daftarKategori.add(2, "Elektronik");
    }

    public String getNamaProfil() {
        return namaProfil;
    }

    public void setNamaProfil(String namaProfil) {
        if (namaProfil == null || namaProfil.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama template/profil tidak boleh kosong!");
        }
        this.namaProfil = namaProfil.trim();
    }

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

    public Map<String, Double> getDaftarDiskonMember() {
        return new LinkedHashMap<>(daftarDiskonMember);
    }

    public void setDaftarDiskonMember(Map<String, Double> diskonBaru) {
        this.daftarDiskonMember.clear();
        if (diskonBaru != null) {
            this.daftarDiskonMember.putAll(diskonBaru);
        }
        if (!this.daftarDiskonMember.containsKey("REGULER")) {
            this.daftarDiskonMember.put("REGULER", 0.0);
        }
    }

    public List<String> getDaftarKategori() {
        return new ArrayList<>(daftarKategori);
    }

    public void setDaftarKategori(List<String> kategoriBaru) {
        this.daftarKategori.clear();
        if (kategoriBaru != null) {
            for (String k : kategoriBaru) {
                if (k != null && !k.trim().isEmpty() && !this.daftarKategori.contains(k.trim())) {
                    this.daftarKategori.add(k.trim());
                }
            }
        }
        if (!this.daftarKategori.contains("Makanan")) this.daftarKategori.add(0, "Makanan");
        if (!this.daftarKategori.contains("Minuman")) this.daftarKategori.add(1, "Minuman");
        if (!this.daftarKategori.contains("Elektronik")) this.daftarKategori.add(2, "Elektronik");
    }

    /**
     * Membuat salinan mandiri (deep copy) dari profil pengaturan.
     */
    public ProfilPengaturan salin(String namaBaru) {
        return new ProfilPengaturan(namaBaru, this.judulAplikasi, this.teksHeaderSub,
                this.warnaHeader, this.warnaAksen, this.daftarDiskonMember, this.daftarKategori);
    }
}
