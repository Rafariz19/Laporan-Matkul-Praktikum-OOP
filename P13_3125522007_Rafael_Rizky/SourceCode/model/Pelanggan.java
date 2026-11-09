package model;

/**
 * Class: Pelanggan
 * Merepresentasikan data konsumen kasir yang berelasi Asosiasi (Association)
 * dengan class Transaksi (uses-a).
 * 
 * Sesuai Modul 10:
 * - Menegakkan validasi ketat dan throwing IllegalArgumentException saat format data salah.
 * - Menghilangkan silent fallback agar pemanggil sadar atas data masukan yang keliru.
 * - Menjaga enkapsulasi dan prinsip Information Expert dalam menghitung diskon member.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String nomorHP;
    private String tipeMember; // Nilai valid: VIP, GOLD, REGULER

    /**
     * Constructor Pelanggan dengan penegakan validasi ketat.
     * 
     * @param idPelanggan kode identitas unik pelanggan (tidak boleh null/kosong)
     * @param nama        nama lengkap pelanggan (tidak boleh null/kosong)
     * @param nomorHP     nomor handphone/kontak aktif (10-15 digit angka)
     * @param tipeMember  kategori membership (VIP / GOLD / REGULER)
     * @throws IllegalArgumentException jika parameter melanggar format valid
     */
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
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

        // Validasi tipe member
        if (tipeMember == null || !validasiTipeMember(tipeMember)) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, REGULER. (Diberikan: " + tipeMember + ")");
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

    // --- MUTATOR METHODS DENGAN EXCEPTION VALIDATION ---

    /**
     * Mengubah nama pelanggan.
     * 
     * @param namaBaru nama baru pelanggan
     * @throws IllegalArgumentException jika nama baru kosong
     */
    public void ubahNama(String namaBaru) {
        if (namaBaru == null || namaBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama baru pelanggan tidak boleh kosong atau null!");
        }
        this.nama = namaBaru.trim();
    }

    /**
     * Mengubah nomor HP dengan validasi regex format internasional/nasional.
     * 
     * @param nomorHPBaru nomor kontak baru (10-15 digit)
     * @throws IllegalArgumentException jika format tidak valid
     */
    public void ubahNomorHP(String nomorHPBaru) {
        if (nomorHPBaru == null || !nomorHPBaru.trim().matches("^[0-9+]{10,15}$")) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Format nomor HP tidak valid (harus 10-15 digit)! (Ditolak: " + nomorHPBaru + ")");
        }
        this.nomorHP = nomorHPBaru.trim();
    }

    /**
     * Mengubah status tipe membership pelanggan (VIP, GOLD, REGULER).
     * 
     * @param tipeMemberBaru tipe member baru
     * @throws IllegalArgumentException jika tipe member di luar kualifikasi
     */
    public void ubahTipeMember(String tipeMemberBaru) {
        if (tipeMemberBaru == null || !validasiTipeMember(tipeMemberBaru)) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, REGULER. (Ditolak: " + tipeMemberBaru + ")");
        }
        this.tipeMember = tipeMemberBaru.trim().toUpperCase();
    }

    private boolean validasiTipeMember(String tipe) {
        String upper = tipe.trim().toUpperCase();
        return upper.equals("VIP") || upper.equals("GOLD") || upper.equals("REGULER");
    }

    // --- RESPONSIBILITY METHODS ---

    /**
     * Menghitung nilai nominal potongan belanja yang berhak diterima pelanggan.
     * Menerapkan prinsip Information Expert.
     * 
     * @param totalBelanja nilai total belanja kotor sebelum diskon member
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
