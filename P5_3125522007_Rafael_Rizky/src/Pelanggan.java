public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String nomorHP;
    private String tipeMember; // VIP, GOLD, REGULER

    // Constructor dengan penegakan validasi
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
        if (idPelanggan != null && !idPelanggan.trim().isEmpty()) {
            this.idPelanggan = idPelanggan;
        } else {
            this.idPelanggan = "CUST-DEF";
            System.out.println("[ERROR VALIDASI] ID Pelanggan tidak boleh kosong! Diset ke default: CUST-DEF");
        }
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

    // --- SETTER METHODS DENGAN VALIDASI ---
    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println("[ERROR VALIDASI] Nama pelanggan tidak boleh kosong/null!");
        }
    }

    public void setNomorHP(String nomorHP) {
        if (nomorHP != null && nomorHP.matches("^[0-9]{10,13}$")) {
            this.nomorHP = nomorHP;
        } else {
            System.out.println("[ERROR VALIDASI] Format nomor HP tidak valid! Harus berupa 10-13 digit angka (Ditolak: " + nomorHP + ")");
        }
    }

    public void setTipeMember(String tipeMember) {
        if (tipeMember != null && (tipeMember.equalsIgnoreCase("VIP") || 
                                   tipeMember.equalsIgnoreCase("GOLD") || 
                                   tipeMember.equalsIgnoreCase("REGULER"))) {
            this.tipeMember = tipeMember.toUpperCase();
        } else {
            System.out.println("[ERROR VALIDASI] Tipe member tidak valid! Pilihan: VIP, GOLD, atau REGULER (Ditolak: " + tipeMember + ")");
            if (this.tipeMember == null) {
                this.tipeMember = "REGULER";
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
