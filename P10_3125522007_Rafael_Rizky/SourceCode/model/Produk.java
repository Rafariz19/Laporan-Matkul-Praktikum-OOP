package model;

import model.exception.StokTidakCukupException;

/**
 * Superclass Abstract: Produk
 * Kerangka dasar umum untuk seluruh komoditas barang pada sistem kasir toko.
 * 
 * Sesuai Modul 10:
 * 1. Enkapsulasi murni: seluruh atribut berstatus private.
 * 2. Mengeliminasi silent failure: kegagalan validasi melempar exception eksplisit (Fail-Fast).
 * 3. Prinsip Konsistensi State: pemeriksaan stok dilakukan SEBELUM pengurangan stok.
 *    Jika terjadi kegagalan (exception), nilai stok lama dijamin 100% tidak berubah (Rollback/Invariance).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public abstract class Produk {
    private String kode;
    private String nama;
    private int harga;
    private int stok;

    /**
     * Constructor Superclass Produk dengan validasi parameter awal (Preconditions).
     * 
     * @param kode  kode unik produk (tidak boleh null atau kosong)
     * @param nama  nama produk (tidak boleh null atau kosong)
     * @param harga harga satuan produk (harus lebih besar dari 0)
     * @param stok  jumlah stok awal produk (tidak boleh negatif)
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public Produk(String kode, String nama, int harga, int stok) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Kode produk tidak boleh null atau kosong!");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama produk tidak boleh null atau kosong!");
        }
        if (harga <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Harga awal harus lebih besar dari 0! (Diberikan: Rp " + harga + ")");
        }
        if (stok < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Stok awal tidak boleh negatif! (Diberikan: " + stok + " unit)");
        }

        this.kode = kode.trim();
        this.nama = nama.trim();
        this.harga = harga;
        this.stok = stok;
    }

    // --- GETTER METHODS ---

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    // --- BEHAVIOR METHODS DENGAN VALIDASI & EXCEPTION HANDLING TEPAT ---

    /**
     * Mengubah nama produk dengan validasi input string.
     * 
     * @param namaBaru nama produk yang baru
     * @throws IllegalArgumentException jika nama baru kosong atau null
     */
    public void ubahNama(String namaBaru) {
        if (namaBaru == null || namaBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama produk baru tidak boleh kosong atau null!");
        }
        this.nama = namaBaru.trim();
    }

    /**
     * Mengubah harga produk dengan validasi bisnis positif.
     * 
     * @param hargaBaru harga baru produk dalam Rupiah (harus > 0)
     * @throws IllegalArgumentException jika harga baru <= 0
     */
    public void ubahHarga(int hargaBaru) {
        if (hargaBaru <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp " + hargaBaru + ")");
        }
        this.harga = hargaBaru;
    }

    /**
     * Menambah stok produk dengan validasi kuantitas masuk.
     * 
     * @param jumlah kuantitas barang masuk (harus > 0)
     * @throws IllegalArgumentException jika jumlah penambahan <= 0
     */
    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Penambahan stok harus lebih dari 0 unit! (Ditolak: " + jumlah + ")");
        }
        this.stok += jumlah;
    }

    /**
     * Mengurangi stok barang (misal saat checkout kasir atau pesanan).
     * Menerapkan prinsip utama Modul 10:
     * - Pemeriksaan dilakukan SEBELUM mutasi state.
     * - Operasi yang ditolak melempar checked exception dan mempertahankan stok sebelumnya.
     * 
     * @param jumlah kuantitas barang yang dikeluarkan (harus > 0 dan <= stok)
     * @throws IllegalArgumentException  jika jumlah kuantitas <= 0
     * @throws StokTidakCukupException jika jumlah kuantitas melebihi sisa stok saat ini
     */
    public void kurangiStok(int jumlah) throws StokTidakCukupException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Jumlah pengurangan stok harus lebih dari 0 unit! (Ditolak: " + jumlah + ")");
        }
        if (jumlah > this.stok) {
            // Throw custom exception dan pertahankan stok lama tanpa mutasi
            throw new StokTidakCukupException(this.nama, this.stok, jumlah);
        }

        // State hanya berubah setelah seluruh pemeriksaan berhasil lolos
        this.stok -= jumlah;
    }

    /**
     * Mengecek apakah ketersediaan stok mencukupi untuk kuantitas yang diminta.
     * 
     * @param jumlah kuantitas yang dicek
     * @return true jika stok mencukupi, false jika kurang atau jumlah <= 0
     */
    public boolean cekKetersediaan(int jumlah) {
        return jumlah > 0 && this.stok >= jumlah;
    }

    /**
     * Menghitung nilai total aset inventaris dari produk ini.
     * 
     * @return total nilai inventaris (harga * stok)
     */
    public int hitungNilaiInventaris() {
        return this.harga * this.stok;
    }

    // --- ABSTRACT METHODS ---

    public abstract String getKategoriInfo();

    public abstract void tampilkanDetailKhusus();

    // --- TEMPLATE METHOD ---

    /**
     * Template method untuk menampilkan rincian komoditas produk.
     */
    public void tampilkanData() {
        System.out.println("--------------------------------------------------");
        System.out.println("Kategori    : " + getKategoriInfo());
        System.out.println("Kode        : " + getKode());
        System.out.println("Nama        : " + getNama());
        System.out.println("Harga       : Rp " + String.format("%,d", getHarga()).replace(',', '.'));
        System.out.println("Stok        : " + getStok() + " unit");
        System.out.println("Inventaris  : Rp " + String.format("%,d", hitungNilaiInventaris()).replace(',', '.'));
        tampilkanDetailKhusus();
        System.out.println("--------------------------------------------------");
    }
}
