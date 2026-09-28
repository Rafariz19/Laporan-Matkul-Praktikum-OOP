public class ProdukMakanan extends Produk {
    // Subclass 1: Mewarisi attribute dan method dari Produk
    // Memiliki attribute khusus: tanggalKadaluarsa
    private String tanggalKadaluarsa;

    // Constructor Subclass yang memanggil constructor Superclass menggunakan super(...)
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa) {
        // Memanggil constructor Produk(kode, nama, harga, stok)
        super(kode, nama, harga, stok);
        setTanggalKadaluarsa(tanggalKadaluarsa);
    }

    // --- GETTER & SETTER KHUSUS ---
    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }

    public void setTanggalKadaluarsa(String tanggalKadaluarsa) {
        if (tanggalKadaluarsa != null && !tanggalKadaluarsa.trim().isEmpty()) {
            this.tanggalKadaluarsa = tanggalKadaluarsa;
        } else {
            this.tanggalKadaluarsa = "01-01-2027";
            System.out.println("[ERROR VALIDASI] Tanggal kadaluarsa tidak valid! Diset ke default: 01-01-2027");
        }
    }

    // Memperluas informasi data produk makanan
    @Override
    public void tampilkanData() {
        System.out.println("=== DATA PRODUK MAKANAN (SUBCLASS) ===");
        System.out.println("Kode        : " + getKode());
        System.out.println("Nama        : " + getNama());
        System.out.println("Harga       : Rp" + getHarga());
        System.out.println("Stok        : " + getStok() + " pcs");
        System.out.println("Kadaluarsa  : " + tanggalKadaluarsa);
        System.out.println("Inventaris  : Rp" + hitungNilaiInventaris());
        System.out.println("--------------------------------------");
    }
}
