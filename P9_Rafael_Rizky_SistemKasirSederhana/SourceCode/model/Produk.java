package model;

/**
 * Superclass Abstract: Produk
 * Kerangka dasar umum untuk seluruh komoditas barang pada sistem kasir toko.
 * 
 * Sesuai ketentuan Modul 9:
 * 1. Enkapsulasi ketat: seluruh attribute berstatus private.
 * 2. Mengeliminasi setter bebas (setStok dan setHarga dihilangkan).
 * 3. Memanfaatkan method berperilaku nyata (tambahStok, kurangiStok, ubahHarga).
 * 4. Memberikan umpan balik eksplisit melalui return boolean dan validasi exception.
 */
public abstract class Produk {
    private String kode;
    private String nama;
    private int harga;
    private int stok;

    /**
     * Constructor Superclass Produk dengan validasi parameter awal.
     * 
     * @param kode  kode unik produk (tidak boleh null atau kosong)
     * @param nama  nama produk (tidak boleh null atau kosong)
     * @param harga harga satuan produk (harus lebih besar dari 0)
     * @param stok  jumlah stok awal produk (tidak boleh negatif)
     * @throws IllegalArgumentException jika parameter melanggar batasan bisnis
     */
    public Produk(String kode, String nama, int harga, int stok) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Kode produk tidak boleh null atau kosong!");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama produk tidak boleh null atau kosong!");
        }
        if (harga <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Harga awal harus lebih besar dari 0! (Diberikan: " + harga + ")");
        }
        if (stok < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Stok awal tidak boleh negatif! (Diberikan: " + stok + ")");
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

    // --- BEHAVIOR METHODS (MUTATOR BERPERILAKU DENGAN UMPAN BALIK BOOLEAN) ---

    /**
     * Mengubah nama produk dengan validasi ketat.
     * 
     * @param namaBaru nama produk yang baru
     * @return true jika berhasil diperbarui, false jika input tidak valid
     */
    public boolean ubahNama(String namaBaru) {
        if (namaBaru != null && !namaBaru.trim().isEmpty()) {
            this.nama = namaBaru.trim();
            return true;
        }
        System.out.println("[ERROR VALIDASI] Nama produk tidak boleh kosong/null!");
        return false;
    }

    /**
     * Mengubah harga produk dengan validasi bisnis (menggantikan setter bebas).
     * 
     * @param hargaBaru harga baru produk dalam Rupiah (harus > 0)
     * @return true jika perubahan disetujui, false jika ditolak
     */
    public boolean ubahHarga(int hargaBaru) {
        if (hargaBaru > 0) {
            this.harga = hargaBaru;
            return true;
        }
        System.out.println("[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp " + hargaBaru + ")");
        return false;
    }

    /**
     * Menambah stok produk dengan validasi jumlah penambahan.
     * 
     * @param jumlah kuantitas barang masuk (harus > 0)
     * @return true jika stok berhasil ditambahkan, false jika jumlah tidak valid
     */
    public boolean tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            System.out.println("[INFO] Stok produk " + nama + " bertambah " + jumlah + ". Total stok saat ini: " + this.stok);
            return true;
        }
        System.out.println("[ERROR VALIDASI] Penambahan stok harus lebih dari 0! (Ditolak: " + jumlah + ")");
        return false;
    }

    /**
     * Mengurangi stok barang (misal saat checkout kasir).
     * 
     * @param jumlah kuantitas barang yang dikeluarkan (harus > 0 dan <= stok saat ini)
     * @return true jika pengurangan stok berhasil, false jika stok tidak mencukupi atau jumlah <= 0
     */
    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah pengurangan stok harus lebih dari 0! (Ditolak: " + jumlah + ")");
            return false;
        }
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        }
        System.out.println("[ERROR STOK] Stok " + nama + " tidak mencukupi! (Tersedia: " + this.stok + ", Diminta: " + jumlah + ")");
        return false;
    }

    /**
     * Mengecek apakah ketersediaan stok mencukupi untuk kuantitas yang diminta.
     * 
     * @param jumlah jumlah yang ingin dicek
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

    /**
     * Mengharuskan setiap subclass mendefinisikan kategori spesifik komoditasnya.
     * 
     * @return deskripsi kategori produk
     */
    public abstract String getKategoriInfo();

    /**
     * Mengharuskan setiap subclass menampilkan atribut khusus uniknya.
     */
    public abstract void tampilkanDetailKhusus();

    // --- TEMPLATE METHOD (POLIMORFISME DENGAN DYNAMIC BINDING) ---

    /**
     * Template method untuk menampilkan rincian umum produk yang memanggil
     * abstract method getKategoriInfo() dan tampilkanDetailKhusus().
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
