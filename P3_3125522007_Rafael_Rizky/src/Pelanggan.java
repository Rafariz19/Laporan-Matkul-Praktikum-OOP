public class Pelanggan {
    // 1. Penerapan Information Hiding: Seluruh atribut dibuat private
    private String idPelanggan;
    private String nama;
    private String nomorHP;
    private String tipeMember; // VIP, GOLD, REGULER

    // Constructor yang mematuhi aturan validasi
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
        if (idPelanggan != null && !idPelanggan.trim().isEmpty()) {
            this.idPelanggan = idPelanggan;
        } else {
            this.idPelanggan = "CUST-DEF";
            System.out.println("[ERROR VALIDASI] ID Pelanggan tidak boleh kosong! Diset ke: CUST-DEF");
        }
        // Inisialisasi melalui setter agar aturan validasi tetap ditegakkan
        setNama(nama);
        setNomorHP(nomorHP);
        setTipeMember(tipeMember);
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

    // Perhitungan diskon berdasarkan tipe membership
    public double getDiskon() {
        if (tipeMember == null) {
            return 0.0;
        }
        switch (tipeMember.toUpperCase()) {
            case "VIP":
                return 0.15; // Diskon 15%
            case "GOLD":
                return 0.10; // Diskon 10%
            case "REGULER":
            default:
                return 0.0;  // 0% diskon
        }
    }

    // --- SETTER METHODS DENGAN VALIDASI DATA ---

    // Validasi: Nama pelanggan tidak boleh kosong atau null
    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println("[ERROR VALIDASI] Nama pelanggan tidak boleh kosong/null! Nilai tidak diubah.");
        }
    }

    // Validasi: Nomor HP harus berupa 10 hingga 13 digit numerik
    public void setNomorHP(String nomorHP) {
        if (nomorHP != null && nomorHP.matches("^[0-9]{10,13}$")) {
            this.nomorHP = nomorHP;
        } else {
            System.out.println("[ERROR VALIDASI] Format nomor HP tidak valid! Harus berupa 10-13 digit angka (Ditolak: " + nomorHP + ")");
        }
    }

    // Validasi: Tipe member harus berupa VIP, GOLD, atau REGULER
    public void setTipeMember(String tipeMember) {
        if (tipeMember != null && (tipeMember.equalsIgnoreCase("VIP") || 
                                   tipeMember.equalsIgnoreCase("GOLD") || 
                                   tipeMember.equalsIgnoreCase("REGULER"))) {
            this.tipeMember = tipeMember.toUpperCase();
        } else {
            System.out.println("[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, atau REGULER (Ditolak: " + tipeMember + ")");
            if (this.tipeMember == null) {
                this.tipeMember = "REGULER"; // Default fallback saat inisialisasi awal gagal
            }
        }
    }

    // --- METODE OPERASIONAL ---
    public void tampilkanData() {
        System.out.println("=== DATA PELANGGAN ===");
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("Nomor HP     : " + nomorHP);
        System.out.println("Tipe Member  : " + tipeMember);
        System.out.println("Hak Diskon   : " + (int)(getDiskon() * 100) + "%");
        System.out.println("-------------------------");
    }
}
