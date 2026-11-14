package model;

/**
 * Class: ItemTransaksi
 * Merepresentasikan baris item pembelian dalam transaksi penjualan.
 * Menyimpan hargaSatuan sebagai snapshot harga saat item dimasukkan ke keranjang
 * guna menjamin Order Line Immutability (perubahan harga di katalog tidak mengubah transaksi berjalan).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class ItemTransaksi {
    private Produk produk;
    private int jumlahBeli;
    private double diskonItem; // Nilai diskon promosi item (0.0 s/d 1.0)
    private int hargaSatuan;   // Snapshot harga satuan saat item ditambahkan
    private String namaProduk; // Snapshot nama produk saat item ditambahkan

    /**
     * Constructor 1: Item standar tanpa diskon promosi baris.
     * Mengambil snapshot harga dari produk.getHarga().
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     */
    public ItemTransaksi(Produk produk, int jumlahBeli) {
        this(produk, jumlahBeli, 0.0);
    }

    /**
     * Constructor 2: Item dengan diskon promosi baris (Overloaded Constructor).
     * Mengambil snapshot harga dari produk.getHarga().
     * 
     * @param produk     referensi objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli (harus > 0)
     * @param diskonItem rasio diskon barang (0.0 s/d 1.0)
     * @throws IllegalArgumentException jika parameter melanggar batasan bisnis
     */
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem) {
        this(produk, jumlahBeli, diskonItem, produk != null ? produk.getHarga() : 0);
    }

    /**
     * Constructor 3: Item lengkap dengan penentuan snapshot hargaSatuan eksplisit.
     * 
     * @param produk      referensi objek produk polimorfik
     * @param jumlahBeli  kuantitas yang dibeli (harus > 0)
     * @param diskonItem  rasio diskon barang (0.0 s/d 1.0)
     * @param hargaSatuan harga satuan yang dikunci untuk item ini (harus > 0)
     */
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem, int hargaSatuan) {
        this(produk, jumlahBeli, diskonItem, hargaSatuan, produk != null ? produk.getNama() : "");
    }

    /**
     * Constructor 4: Item lengkap dengan penentuan snapshot hargaSatuan dan namaProduk eksplisit.
     * 
     * @param produk      referensi objek produk polimorfik
     * @param jumlahBeli  kuantitas yang dibeli (harus > 0)
     * @param diskonItem  rasio diskon barang (0.0 s/d 1.0)
     * @param hargaSatuan harga satuan yang dikunci untuk item ini (harus > 0)
     * @param namaProduk  nama produk yang dikunci untuk nota ini
     */
    public ItemTransaksi(Produk produk, int jumlahBeli, double diskonItem, int hargaSatuan, String namaProduk) {
        if (produk == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Objek produk tidak boleh null pada ItemTransaksi!");
        }
        if (jumlahBeli <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Kuantitas beli harus lebih dari 0! (Diberikan: " + jumlahBeli + ")");
        }
        if (diskonItem < 0.0 || diskonItem > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Rasio diskon item harus di antara 0.0 dan 1.0! (Diberikan: " + diskonItem + ")");
        }
        if (hargaSatuan <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Harga satuan pada ItemTransaksi harus lebih besar dari 0! (Diberikan: " + hargaSatuan + ")");
        }

        this.produk = produk;
        this.jumlahBeli = jumlahBeli;
        this.diskonItem = diskonItem;
        this.hargaSatuan = hargaSatuan;
        this.namaProduk = (namaProduk != null && !namaProduk.trim().isEmpty()) ? namaProduk.trim() : produk.getNama();
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

    public int getHargaSatuan() {
        return hargaSatuan;
    }

    public String getNamaProduk() {
        return namaProduk;
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
     * Menghitung subtotal kotor berdasarkan snapshot hargaSatuan saat ditambahkan.
     * Menjamin perubahan harga di katalog tidak mengubah nilai kotor item yang sudah di keranjang.
     * 
     * @return subtotal gross dalam Rupiah
     */
    public int hitungSubtotalGross() {
        return hargaSatuan * jumlahBeli;
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
                hargaSatuan,
                diskonStr,
                hitungSubtotal());
    }
}
