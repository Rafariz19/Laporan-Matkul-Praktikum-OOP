public class ProdukElektronik extends Produk {
    // Subclass 2: Mewarisi attribute dan method dari Produk
    // Memiliki attribute khusus: garansiBulan
    private int garansiBulan;

    // Constructor Subclass yang memanggil constructor Superclass menggunakan super(...)
    public ProdukElektronik(String kode, String nama, int harga, int stok, int garansiBulan) {
        // Memanggil constructor Produk(kode, nama, harga, stok)
        super(kode, nama, harga, stok);
        setGaransiBulan(garansiBulan);
    }

    // --- GETTER & SETTER KHUSUS ---
    public int getGaransiBulan() {
        return garansiBulan;
    }

    public void setGaransiBulan(int garansiBulan) {
        if (garansiBulan >= 0) {
            this.garansiBulan = garansiBulan;
        } else {
            System.out.println("[ERROR VALIDASI] Masa garansi tidak boleh negatif! (Ditolak: " + garansiBulan + " bulan)");
        }
    }

    // Memperluas informasi data produk elektronik
    @Override
    public void tampilkanData() {
        System.out.println("=== DATA PRODUK ELEKTRONIK (SUBCLASS) ===");
        System.out.println("Kode        : " + getKode());
        System.out.println("Nama        : " + getNama());
        System.out.println("Harga       : Rp" + getHarga());
        System.out.println("Stok        : " + getStok() + " pcs");
        System.out.println("Garansi     : " + garansiBulan + " Bulan");
        System.out.println("Inventaris  : Rp" + hitungNilaiInventaris());
        System.out.println("-----------------------------------------");
    }
}
