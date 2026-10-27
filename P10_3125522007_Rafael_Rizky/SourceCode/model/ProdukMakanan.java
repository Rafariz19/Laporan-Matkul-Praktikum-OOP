package model;

/**
 * Subclass: ProdukMakanan
 * Komoditas konsumsi yang memiliki masa simpan dan tanggal kadaluarsa.
 * Mewarisi abstract class Produk dan mengimplementasikan interface DapatDidiskon.
 * 
 * Sesuai Modul 10:
 * Menegakkan validasi ketat pada tanggal kadaluarsa menggunakan IllegalArgumentException.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ProdukMakanan extends Produk implements DapatDidiskon {
    private String tanggalKadaluarsa;

    /**
     * Constructor ProdukMakanan.
     * 
     * @param kode              kode barang makanan
     * @param nama              nama barang makanan
     * @param harga             harga satuan makanan (> 0)
     * @param stok              jumlah stok makanan (>= 0)
     * @param tanggalKadaluarsa tanggal masa berlaku kadaluarsa (format: DD-MM-YYYY)
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa) {
        super(kode, nama, harga, stok);
        if (tanggalKadaluarsa == null || tanggalKadaluarsa.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tanggal kadaluarsa tidak boleh kosong atau null!");
        }
        this.tanggalKadaluarsa = tanggalKadaluarsa.trim();
    }

    // --- GETTER & MUTATOR ---

    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }

    /**
     * Mengubah tanggal kadaluarsa dengan validasi ketat.
     * 
     * @param tanggalBaru format tanggal baru (DD-MM-YYYY)
     * @throws IllegalArgumentException jika tanggalBaru null atau kosong
     */
    public void ubahTanggalKadaluarsa(String tanggalBaru) {
        if (tanggalBaru == null || tanggalBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tanggal kadaluarsa baru tidak boleh kosong!");
        }
        this.tanggalKadaluarsa = tanggalBaru.trim();
    }

    // --- IMPLEMENTASI ABSTRACT METHODS ---

    @Override
    public String getKategoriInfo() {
        return "Makanan & Minuman Segar (Konsumsi)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Kadaluarsa  : " + tanggalKadaluarsa);
    }
}
