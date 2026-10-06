public class Pelanggan {
    // Relasi Association dengan Transaksi (uses-a)
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
        if (nomorHP != null && nomorHP.matches("^[0-9+]{10,15}$")) {
            this.nomorHP = nomorHP;
        } else {
            this.nomorHP = "080000000000";
            System.out.println("[ERROR VALIDASI] Format nomor HP tidak valid! Diset ke default: 080000000000");
        }
    }

    public void setTipeMember(String tipeMember) {
        if (tipeMember != null) {
            String upper = tipeMember.trim().toUpperCase();
            if (upper.equals("VIP") || upper.equals("GOLD") || upper.equals("REGULER")) {
                this.tipeMember = upper;
                return;
            }
        }
        this.tipeMember = "REGULER";
        System.out.println("[ERROR VALIDASI] Tipe member tidak valid (pilihan: VIP/GOLD/REGULER)! Diset ke default: REGULER");
    }

    public void tampilkanData() {
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("Nomor HP     : " + nomorHP);
        System.out.println("Tipe Member  : " + tipeMember + " (Diskon: " + (int)(getDiskon() * 100) + "%)");
    }
}
