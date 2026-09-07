public class Pelanggan {
    String idPelanggan;
    String nama;
    String nomorHP;
    String tipeMember; // "VIP", "Gold", atau "Reguler"

    // Constructor untuk inisialisasi object Pelanggan
    public Pelanggan(String idPelanggan, String nama, String nomorHP, String tipeMember) {
        this.idPelanggan = idPelanggan;
        this.nama = nama;
        this.nomorHP = nomorHP;
        this.tipeMember = tipeMember;
    }

    // Method tanpa parameter: menampilkan detail data pelanggan
    public void tampilkanData() {
        System.out.println("=== DATA PELANGGAN ===");
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("Nomor HP     : " + nomorHP);
        System.out.println("Tipe Member  : " + tipeMember);
        System.out.println("Diskon       : " + (int)(getDiskon() * 100) + "%");
        System.out.println("-------------------------");
    }

    // Method dengan parameter: memperbarui nomor telepon pelanggan
    public void ubahNomorHP(String nomorBaru) {
        this.nomorHP = nomorBaru;
        System.out.println("[INFO] Nomor HP pelanggan " + nama + " berhasil diperbarui menjadi: " + nomorBaru);
    }

    // Method dengan parameter: mengubah jenis keanggotaan/tipe member
    public void ubahTipeMember(String tipeBaru) {
        this.tipeMember = tipeBaru;
        System.out.println("[INFO] Status member pelanggan " + nama + " berhasil diubah menjadi: " + tipeBaru);
    }

    // Method dengan return value: mengembalikan rate diskon berdasarkan tipe member
    public double getDiskon() {
        if (tipeMember.equalsIgnoreCase("VIP")) {
            return 0.15; // Diskon 15%
        } else if (tipeMember.equalsIgnoreCase("Gold")) {
            return 0.10; // Diskon 10%
        } else {
            return 0.0;  // Reguler tanpa diskon
        }
    }

    // Method dengan return value: mengembalikan nama pelanggan
    public String getNama() {
        return this.nama;
    }
}
