public class ProdukMakanan extends Produk implements DapatDidiskon {
    // Subclass 1: Mewarisi Abstract Class Produk dan Mengimplementasikan Interface DapatDidiskon
    private String tanggalKadaluarsa;

    // Constructor Subclass yang memanggil constructor Superclass Produk
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

    // --- IMPLEMENTASI ABSTRACT METHODS DARI PRODUK ---
    @Override
    public String getKategoriInfo() {
        return "Makanan & Minuman Segar (Konsumsi)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Kadaluarsa  : " + tanggalKadaluarsa);
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
