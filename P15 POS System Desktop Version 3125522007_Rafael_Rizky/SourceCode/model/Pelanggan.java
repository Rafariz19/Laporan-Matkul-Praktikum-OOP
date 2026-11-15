package model;

/**
 * Class: Pelanggan
 * Merepresentasikan data konsumen kasir yang berelasi Asosiasi (Association)
 * dengan class Transaksi (uses-a).
 * 
 * Sesuai Modul 10 & Fase 4:
 * - Menegakkan validasi ketat dan throwing IllegalArgumentException saat format data salah.
 * - Menjaga enkapsulasi dan prinsip Information Expert dalam menghitung diskon member.
 * - Mendukung skema diskon dinamis yang dapat dikonfigurasi lewat PengaturanSistem
 *   serta tetap backward compatible dengan default VIP (15%), GOLD (10%), REGULER (0%).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String nomorHP;
    private String tipeMember;
    private PengaturanSistem pengaturanSistem; // Opsional referensi konfigurasi dinamis

    /**
     * Constructor Pelanggan dengan penegakan validasi ketat.
     * 
     * @param idPelanggan kode identitas unik pelanggan (tidak boleh null/kosong)
     * @param nama        nama lengkap pelanggan (tidak boleh null/kosong)
     * @param nomorHP     nomor handphone/kontak aktif (10-15 digit angka)
     * @param tipeMember  kategori membership (VIP / GOLD / REGULER / kustom)
     * @throws IllegalArgumentException jika parameter melanggar format valid
     */
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
        this(idPelanggan, nama, nomorHP, tipeMember, null);
    }

    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember, PengaturanSistem pengaturan) {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] ID Pelanggan tidak boleh kosong atau null!");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama pelanggan tidak boleh kosong atau null!");
        }

        this.idPelanggan = idPelanggan.trim();
        this.nama = nama.trim();

        // Validasi nomor HP (10-15 digit numerik atau tanda +)
        if (nomorHP == null || !nomorHP.trim().matches("^[0-9+]{10,15}$")) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Format nomor HP tidak valid (wajib 10-15 digit numerik)! (Diberikan: " + nomorHP + ")");
        }
        this.nomorHP = nomorHP.trim();

        this.pengaturanSistem = pengaturan;

        // Validasi tipe member
        if (tipeMember == null || !validasiTipeMember(tipeMember)) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tipe member tidak valid! (Diberikan: " + tipeMember + ")");
        }
        this.tipeMember = tipeMember.trim().toUpperCase();
    }

    // --- GETTER METHODS ---

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public String getNama() {
        return nama;
    }

    public String getNomorHP() {
        return nomorHP;
    }

    public String getTipeMember() {
        return tipeMember;
    }

    public PengaturanSistem getPengaturanSistem() {
        return pengaturanSistem;
    }

    public void setPengaturanSistem(PengaturanSistem pengaturanSistem) {
        this.pengaturanSistem = pengaturanSistem;
    }

    /**
     * Mendapatkan rasio diskon berdasarkan tipe membership.
     * Jika terhubung dengan PengaturanSistem, membaca dari skema dinamis hasil konfigurasi.
     * Jika tidak, fallback ke standar bawaan: VIP = 15%, GOLD = 10%, REGULER = 0%.
     * 
     * @return persentase diskon dalam bentuk desimal (0.0 s/d 1.0)
     */
    public double getDiskon() {
        if (tipeMember == null) {
            return 0.0;
        }
        if (pengaturanSistem != null) {
            return pengaturanSistem.getDiskon(tipeMember);
        }
        switch (tipeMember.toUpperCase()) {
            case "VIP":
                return 0.15;
            case "GOLD":
                return 0.10;
            case "REGULER":
            default:
                return 0.0;
        }
    }

    // --- MUTATOR METHODS DENGAN EXCEPTION VALIDATION ---

    public void ubahNama(String namaBaru) {
        if (namaBaru == null || namaBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama baru pelanggan tidak boleh kosong atau null!");
        }
        this.nama = namaBaru.trim();
    }

    public void ubahNomorHP(String nomorHPBaru) {
        if (nomorHPBaru == null || !nomorHPBaru.trim().matches("^[0-9+]{10,15}$")) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Format nomor HP tidak valid (harus 10-15 digit)! (Ditolak: " + nomorHPBaru + ")");
        }
        this.nomorHP = nomorHPBaru.trim();
    }

    public void ubahTipeMember(String tipeMemberBaru) {
        if (tipeMemberBaru == null || !validasiTipeMember(tipeMemberBaru)) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tipe member tidak valid! (Ditolak: " + tipeMemberBaru + ")");
        }
        this.tipeMember = tipeMemberBaru.trim().toUpperCase();
    }

    private boolean validasiTipeMember(String tipe) {
        if (tipe == null || tipe.trim().isEmpty()) return false;
        String upper = tipe.trim().toUpperCase();
        if (pengaturanSistem != null && pengaturanSistem.adaTipeMember(upper)) {
            return true;
        }
        return upper.equals("VIP") || upper.equals("GOLD") || upper.equals("REGULER");
    }

    // --- RESPONSIBILITY METHODS ---

    public double hitungPotonganMember(double totalBelanja) {
        if (totalBelanja <= 0) {
            return 0.0;
        }
        return totalBelanja * getDiskon();
    }

    public void tampilkanData() {
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("Nomor HP     : " + nomorHP);
        System.out.println("Tipe Member  : " + tipeMember + " (Hak Diskon: " + (int) (getDiskon() * 100) + "%)");
    }
}
