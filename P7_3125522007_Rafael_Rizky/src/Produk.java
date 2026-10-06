public abstract class Produk {
    // Superclass Abstract: Kerangka dasar umum seluruh komoditas barang toko kasir.
    // Tidak dapat diinstansiasi secara langsung menggunakan keyword new.
    private String kode;
    private String nama;
    private int harga;
    private int stok;

    // Constructor Superclass
    public Produk(String kode, String nama, int harga, int stok) {
        if (kode != null && !kode.trim().isEmpty()) {
            this.kode = kode;
        } else {
            this.kode = "PROD-DEF";
            System.out.println("[ERROR VALIDASI] Kode produk tidak boleh kosong! Diset ke default: PROD-DEF");
        }
        setNama(nama);
        setHarga(harga);
        setStok(stok);
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

    // --- SETTER METHODS DENGAN VALIDASI ---
    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println("[ERROR VALIDASI] Nama produk tidak boleh kosong/null!");
        }
    }

    public void setHarga(int harga) {
        if (harga > 0) {
            this.harga = harga;
        } else {
            System.out.println("[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp" + harga + ")");
        }
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("[ERROR VALIDASI] Stok produk tidak boleh negatif! (Ditolak: " + stok + ")");
        }
    }

    // --- METODE OPERASIONAL UMUM ---
    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            System.out.println("[INFO] Stok produk " + nama + " bertambah " + jumlah + ". Total stok: " + this.stok);
        } else {
            System.out.println("[ERROR VALIDASI] Penambahan stok harus lebih dari 0!");
        }
    }

    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah pengurangan stok harus lebih dari 0!");
            return false;
        }
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        } else {
            System.out.println("[ERROR] Stok " + nama + " tidak mencukupi! (Tersedia: " + this.stok + ", Diminta: " + jumlah + ")");
            return false;
        }
    }

    public int hitungNilaiInventaris() {
        return this.harga * this.stok;
    }

    // --- ABSTRACT METHODS (KONTRAK KONSEPTUAL YANG WAJIB DIIMPLEMENTASIKAN SUBCLASS) ---
    // Abstract Method 1: Mengharuskan setiap subclass mendefinisikan kategori spesifiknya
    public abstract String getKategoriInfo();

    // Abstract Method 2: Mengharuskan setiap subclass menampilkan atribut khusus uniknya
    public abstract void tampilkanDetailKhusus();

    // --- TEMPLATE METHOD KONKRET (MEMANFAATKAN DYNAMIC BINDING) ---
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
