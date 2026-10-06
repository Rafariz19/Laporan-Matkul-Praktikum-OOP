public class ProdukElektronik extends Produk implements DapatDidiskon {
    // Subclass 2: Mewarisi Abstract Class Produk dan Mengimplementasikan Interface DapatDidiskon
    private int garansiBulan;

    // Constructor Subclass yang memanggil constructor Superclass Produk
    public ProdukElektronik(String kode, String nama, int harga, int stok, int garansiBulan) {
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

    // --- IMPLEMENTASI ABSTRACT METHODS DARI PRODUK ---
    @Override
    public String getKategoriInfo() {
        return "Elektronik & Aksesoris Gadget (Hardware)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Garansi     : " + garansiBulan + " Bulan (Garansi Resmi)");
    }

    // --- IMPLEMENTASI INTERFACE DAPATDIDISKON ---
    @Override
    public double hitungDiskon(double persentase) {
        if (persentase <= 0.0) return 0.0;
        if (persentase > 100.0) persentase = 100.0;
        return getHarga() * (persentase / 100.0);
    }

    @Override
    public double getHargaSetelahDiskon(double persentase) {
        return getHarga() - hitungDiskon(persentase);
    }
}
