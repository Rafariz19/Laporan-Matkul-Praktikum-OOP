package model;

/**
 * Class: ItemTransaksi
 * Merepresentasikan baris item pembelian dalam transaksi penjualan.
 * 
 * Hubungan Relasi:
 * 1. Aggregation (o-->) dengan Produk: ItemTransaksi memegang referensi ke objek Produk
 *    secara polimorfik (independen).
 * 2. Composition (*-->) dari Transaksi: ItemTransaksi dikendalikan penuh oleh Transaksi.
 * 
 * Sesuai Modul 10:
 * Menegakkan validasi ketat dan throwing IllegalArgumentException saat pembuatan
 * atau pembaruan item jika terdapat pelanggaran batasan bisnis.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ItemTransaksi {
    private Produk produk;
    private int jumlahBeli;
    private double diskonItem; // Nilai diskon promosi item (0.0 s/d 1.0)

    /**
     * Constructor 1: Item standar tanpa diskon promosi baris.
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     */
    public ItemTransaksi(Produk produk, int jumlahBeli) {
        this(produk, jumlahBeli, 0.0);
    }

    /**
     * Constructor 2: Item dengan diskon promosi baris (Overloaded Constructor).
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     * @param diskonItem rasio diskon barang (0.0 s/d 1.0)
     * @throws IllegalArgumentException jika parameter melanggar batasan bisnis
     */
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem) {
        if (produk == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Objek produk tidak boleh null pada ItemTransaksi!");
        }
        if (jumlahBeli <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Kuantitas beli harus lebih dari 0! (Diberikan: " + jumlahBeli + ")");
        }
        if (diskonItem < 0.0 || diskonItem > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Rasio diskon item harus di antara 0.0 dan 1.0! (Diberikan: " + diskonItem + ")");
        }

        this.produk = produk;
        this.jumlahBeli = jumlahBeli;
        this.diskonItem = diskonItem;
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

    // --- BEHAVIOR METHODS DENGAN EXCEPTION VALIDATION ---

    /**
     * Mengubah kuantitas jumlah beli pada item dengan validasi.
     * 
     * @param jumlahBaru kuantitas baru (> 0)
     * @throws IllegalArgumentException jika jumlahBaru <= 0
     */
    public void ubahJumlahBeli(int jumlahBaru) {
        if (jumlahBaru <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Jumlah beli baru harus lebih dari 0! (Ditolak: " + jumlahBaru + ")");
        }
        this.jumlahBeli = jumlahBaru;
    }

    /**
     * Mengubah nilai diskon promosi per item.
     * 
     * @param diskonBaru rasio diskon (0.0 s/d 1.0)
     * @throws IllegalArgumentException jika diskonBaru di luar rentang [0.0, 1.0]
     */
    public void ubahDiskonItem(double diskonBaru) {
        if (diskonBaru < 0.0 || diskonBaru > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Diskon item harus antara 0.0 dan 1.0! (Ditolak: " + diskonBaru + ")");
        }
        this.diskonItem = diskonBaru;
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
     * Menghitung subtotal akhir baris item setelah diskon promosi item.
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
