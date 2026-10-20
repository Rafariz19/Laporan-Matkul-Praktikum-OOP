package model;

/**
 * Class ItemTransaksi
 * Merepresentasikan baris item pembelian dalam keranjang transaksi.
 * 
 * Hubungan Relasi:
 * 1. Aggregation (o-->) dengan Produk: ItemTransaksi memegang referensi ke objek Produk
 *    secara polimorfik (bisa ProdukMakanan atau ProdukElektronik). Siklus hidup Produk
 *    bersifat independen dari ItemTransaksi.
 * 2. Composition (*-->) dari Transaksi: ItemTransaksi diciptakan dan dikelola langsung
 *    oleh Transaksi sebagai bagian utuh dari struk belanja.
 * 
 * Sesuai ketentuan Modul 9:
 * - Menghilangkan anemic setter (setProduk dihilangkan demi konsistensi referensi).
 * - Menambahkan method kalkulasi subtotal kotor, potongan diskon baris, dan subtotal bersih.
 */
public class ItemTransaksi {
    private Produk produk;
    private int jumlahBeli;
    private double diskonItem; // Nilai diskon promosi item (0.0 s/d 1.0)

    /**
     * Constructor 1: Item standar tanpa diskon khusus.
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     */
    public ItemTransaksi(Produk produk, int jumlahBeli) {
        this(produk, jumlahBeli, 0.0);
    }

    /**
     * Constructor 2 (Overloaded Constructor): Item dengan diskon promosi per baris barang.
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     * @param diskonItem rasio diskon barang (0.0 s/d 1.0)
     */
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem) {
        if (produk == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Objek produk tidak boleh null pada ItemTransaksi!");
        }
        if (jumlahBeli <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Jumlah beli harus lebih dari 0! (Diberikan: " + jumlahBeli + ")");
        }

        this.produk = produk;
        this.jumlahBeli = jumlahBeli;
        this.diskonItem = (diskonItem >= 0.0 && diskonItem <= 1.0) ? diskonItem : 0.0;
    }

    // --- GETTER METHODS ---

    public Produk getProduk() {
        return produk;
    }

    public int getJumlahBeli() {
        return jumlahBeli;
    }

    public double getDiskonItem() {
        return diskonItem;
    }

    // --- BEHAVIOR METHODS DENGAN VALIDASI & RESPONSIBILITY JELAS ---

    /**
     * Mengubah kuantitas jumlah beli pada item dengan validasi.
     * 
     * @param jumlahBaru jumlah kuantitas baru (harus > 0)
     * @return true jika berhasil, false jika tidak valid
     */
    public boolean ubahJumlahBeli(int jumlahBaru) {
        if (jumlahBaru > 0) {
            this.jumlahBeli = jumlahBaru;
            return true;
        }
        System.out.println("[ERROR VALIDASI] Jumlah beli harus lebih dari 0! (Ditolak: " + jumlahBaru + ")");
        return false;
    }

    /**
     * Mengubah nilai diskon promosi per item.
     * 
     * @param diskonBaru persentase diskon (0.0 s/d 1.0)
     * @return true jika berhasil, false jika di luar rentang valid
     */
    public boolean ubahDiskonItem(double diskonBaru) {
        if (diskonBaru >= 0.0 && diskonBaru <= 1.0) {
            this.diskonItem = diskonBaru;
            return true;
        }
        System.out.println("[ERROR VALIDASI] Diskon item harus antara 0.0 dan 1.0 (Ditolak: " + diskonBaru + ")");
        return false;
    }

    /**
     * Menghitung subtotal kotor (sebelum diskon item).
     * 
     * @return subtotal gross dalam Rupiah
     */
    public int hitungSubtotalGross() {
        return produk.getHarga() * jumlahBeli;
    }

    /**
     * Menghitung besaran nominal potongan diskon promosi pada item ini.
     * 
     * @return nominal potongan diskon item dalam double
     */
    public double hitungNominalDiskonItem() {
        return hitungSubtotalGross() * diskonItem;
    }

    /**
     * Menghitung subtotal akhir baris item setelah dipotong diskon promosi item.
     * 
     * @return subtotal bersih dalam Rupiah
     */
    public int hitungSubtotal() {
        int gross = hitungSubtotalGross();
        return (int) Math.round(gross - hitungNominalDiskonItem());
    }

    /**
     * Menampilkan informasi rincian item dengan memanfaatkan polimorfisme getKategoriInfo().
     */
    public void tampilkanItem() {
        String diskonStr = (diskonItem > 0.0) ? String.format(" [Disc %.1f%%]", diskonItem * 100) : "";
        System.out.printf("- %s (%s) x %d @ Rp %,d%s = Rp %,d\n",
                produk.getNama(),
                produk.getKategoriInfo(),
                jumlahBeli,
                produk.getHarga(),
                diskonStr,
                hitungSubtotal());
    }
}
