package model;

/**
 * Subclass: ProdukElektronik
 * Komoditas perangkat keras dan aksesoris yang memiliki masa garansi resmi.
 * Mewarisi abstract class Produk dan mengimplementasikan interface DapatDidiskon.
 * 
 * Sesuai Modul 10:
 * Menegakkan validasi ketat pada masa garansi menggunakan IllegalArgumentException.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ProdukElektronik extends Produk implements DapatDidiskon {
    private int garansiBulan;

    /**
     * Constructor ProdukElektronik.
     * 
     * @param kode         kode barang elektronik
     * @param nama         nama barang elektronik
     * @param harga        harga satuan barang (> 0)
     * @param stok         jumlah stok awal (>= 0)
     * @param garansiBulan durasi garansi resmi dalam satuan bulan (harus >= 0)
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public ProdukElektronik(String kode, String nama, int harga, int stok, int garansiBulan) {
        super(kode, nama, harga, stok);
        if (garansiBulan < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Durasi garansi tidak boleh negatif! (Diberikan: " + garansiBulan + " bulan)");
        }
        this.garansiBulan = garansiBulan;
    }

    // --- GETTER & MUTATOR ---

    public int getGaransiBulan() {
        return garansiBulan;
    }

    /**
     * Mengubah masa garansi resmi.
     * 
     * @param garansiBaru durasi garansi baru dalam bulan (harus >= 0)
     * @throws IllegalArgumentException jika garansiBaru negatif
     */
    public void ubahGaransiBulan(int garansiBaru) {
        if (garansiBaru < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Masa garansi baru tidak boleh negatif! (Ditolak: " + garansiBaru + " bulan)");
        }
        this.garansiBulan = garansiBaru;
    }

    // --- IMPLEMENTASI ABSTRACT METHODS ---

    @Override
    public String getKategoriInfo() {
        return "Elektronik & Aksesoris Gadget (Hardware)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Garansi     : " + garansiBulan + " Bulan (Garansi Resmi)");
    }
}
