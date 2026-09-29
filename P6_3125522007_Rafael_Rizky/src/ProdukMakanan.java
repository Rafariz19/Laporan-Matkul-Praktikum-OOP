public class ProdukMakanan extends Produk {
    // Subclass 1: Mewarisi Produk dengan attribute khusus tanggalKadaluarsa
    private String tanggalKadaluarsa;

    // Constructor Subclass yang memanggil constructor Superclass menggunakan super(...)
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa) {
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

    // --- METHOD OVERRIDING 1 ---
    @Override
    public String getKategoriInfo() {
        return "Makanan & Minuman Segar (Konsumsi)";
    }

    // --- METHOD OVERRIDING 2 ---
    @Override
    public void tampilkanData() {
        System.out.println("=== DATA PRODUK MAKANAN (SUBCLASS) ===");
        System.out.println("Kategori    : " + getKategoriInfo());
        System.out.println("Kode        : " + getKode());
        System.out.println("Nama        : " + getNama());
        System.out.println("Harga       : Rp" + getHarga());
        System.out.println("Stok        : " + getStok() + " pcs");
        System.out.println("Kadaluarsa  : " + tanggalKadaluarsa);
        System.out.println("Inventaris  : Rp" + hitungNilaiInventaris());
        System.out.println("--------------------------------------");
    }
}
