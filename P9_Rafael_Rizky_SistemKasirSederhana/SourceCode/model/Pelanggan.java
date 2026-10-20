package model;

/**
 * Class Pelanggan
 * Merepresentasikan data konsumen kasir yang berelasi Asosiasi (Association)
 * dengan class Transaksi (uses-a).
 * 
 * Sesuai ketentuan Modul 9:
 * 1. Seluruh atribut berstatus private (enkapsulasi penuh).
 * 2. Mengurangi anemic setter bebas; mutator memberikan umpan balik return boolean.
 * 3. Memiliki tanggung jawab (responsibility) menghitung hak diskon member.
 */
public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String nomorHP;
    private String tipeMember; // Nilai valid: VIP, GOLD, REGULER

    /**
     * Constructor Pelanggan dengan penegakan validasi ketat.
     * 
     * @param idPelanggan kode identitas unik pelanggan
     * @param nama        nama lengkap pelanggan
     * @param nomorHP     nomor handphone/kontak aktif (10-15 digit)
     * @param tipeMember  kategori membership (VIP / GOLD / REGULER)
     */
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] ID Pelanggan tidak boleh kosong!");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama pelanggan tidak boleh kosong!");
        }

        this.idPelanggan = idPelanggan.trim();
        this.nama = nama.trim();

        // Validasi nomor HP
        if (nomorHP != null && nomorHP.matches("^[0-9+]{10,15}$")) {
            this.nomorHP = nomorHP.trim();
        } else {
            this.nomorHP = "080000000000";
            System.out.println("[PERINGATAN] Format nomor HP tidak valid! Diset ke default: 080000000000");
        }

        // Validasi tipe member
        if (tipeMember != null && validasiTipeMember(tipeMember)) {
            this.tipeMember = tipeMember.trim().toUpperCase();
        } else {
            this.tipeMember = "REGULER";
            System.out.println("[PERINGATAN] Tipe member tidak valid! Diset ke default: REGULER");
        }
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

    /**
     * Mendapatkan rasio diskon berdasarkan tipe membership.
     * VIP = 15% (0.15), GOLD = 10% (0.10), REGULER = 0% (0.0).
     * 
     * @return persentase diskon dalam bentuk desimal
     */
    public double getDiskon() {
        if (tipeMember == null) {
            return 0.0;
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

    // --- MUTATOR METHODS DENGAN UMPAN BALIK BOOLEAN ---

    /**
     * Mengubah nama pelanggan.
     * 
     * @param namaBaru nama baru pelanggan
     * @return true jika berhasil diubah, false jika tidak valid
     */
    public boolean ubahNama(String namaBaru) {
        if (namaBaru != null && !namaBaru.trim().isEmpty()) {
            this.nama = namaBaru.trim();
            return true;
        }
        System.out.println("[ERROR VALIDASI] Nama pelanggan tidak boleh kosong!");
        return false;
    }

    /**
     * Mengubah nomor HP dengan validasi format regex (10-15 digit numerik atau +).
     * 
     * @param nomorHPBaru nomor kontak baru
     * @return true jika nomor valid dan berhasil diubah, false jika gagal
     */
    public boolean ubahNomorHP(String nomorHPBaru) {
        if (nomorHPBaru != null && nomorHPBaru.matches("^[0-9+]{10,15}$")) {
            this.nomorHP = nomorHPBaru.trim();
            return true;
        }
        System.out.println("[ERROR VALIDASI] Format nomor HP tidak valid (harus 10-15 digit)! (Ditolak: " + nomorHPBaru + ")");
        return false;
    }

    /**
     * Mengubah status tipe membership pelanggan (VIP, GOLD, REGULER).
     * 
     * @param tipeMemberBaru tipe member baru
     * @return true jika berhasil, false jika tipe member tidak dikenal
     */
    public boolean ubahTipeMember(String tipeMemberBaru) {
        if (tipeMemberBaru != null && validasiTipeMember(tipeMemberBaru)) {
            this.tipeMember = tipeMemberBaru.trim().toUpperCase();
            return true;
        }
        System.out.println("[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, REGULER. (Ditolak: " + tipeMemberBaru + ")");
        return false;
    }

    private boolean validasiTipeMember(String tipe) {
        String upper = tipe.trim().toUpperCase();
        return upper.equals("VIP") || upper.equals("GOLD") || upper.equals("REGULER");
    }

    // --- RESPONSIBILITY METHODS ---

    /**
     * Menghitung nilai nominal potongan belanja yang berhak diterima pelanggan.
     * Tanggung jawab (responsibility) perhitungan hak potongan diletakkan
     * pada entitas Pelanggan (Information Expert Pattern).
     * 
     * @param totalBelanja nilai total belanja sebelum diskon member
     * @return besaran nominal potongan diskon member
     */
    public double hitungPotonganMember(double totalBelanja) {
        if (totalBelanja <= 0) {
            return 0.0;
        }
        return totalBelanja * getDiskon();
    }

    /**
     * Menampilkan profil data pelanggan dan hak persentase diskonnya.
     */
    public void tampilkanData() {
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("Nomor HP     : " + nomorHP);
        System.out.println("Tipe Member  : " + tipeMember + " (Hak Diskon: " + (int) (getDiskon() * 100) + "%)");
    }
}
