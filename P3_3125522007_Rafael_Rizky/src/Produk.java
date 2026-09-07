public class Produk {
    // 1. Penerapan Information Hiding: Seluruh atribut dibuat private
    private String kode;
    private String nama;
    private int harga;
    private int stok;

    // Constructor yang mematuhi aturan validasi
    public Produk(String kode, String nama, int harga, int stok) {
        if (kode != null && !kode.trim().isEmpty()) {
            this.kode = kode;
        } else {
            this.kode = "PROD-DEF";
            System.out.println("[ERROR VALIDASI] Kode produk tidak boleh kosong! Diset ke: PROD-DEF");
        }
        // Inisialisasi melalui setter agar aturan validasi tetap ditegakkan
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

    // --- SETTER METHODS DENGAN VALIDASI DATA ---

    // Validasi: Nama produk tidak boleh null atau kosong
    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println("[ERROR VALIDASI] Nama produk tidak boleh kosong/null! Nilai tidak diubah.");
        }
    }

    // Validasi: Harga produk harus lebih besar dari 0 (tidak boleh 0 atau negatif)
    public void setHarga(int harga) {
        if (harga > 0) {
            this.harga = harga;
        } else {
            System.out.println("[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp" + harga + ")");
        }
    }

    // Validasi: Stok produk tidak boleh negatif (>= 0)
    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("[ERROR VALIDASI] Stok produk tidak boleh bernilai negatif! (Ditolak: " + stok + ")");
        }
    }

    // --- METODE OPERASIONAL ---

    // Menambah stok dengan validasi jumlah positif
    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            System.out.println("[INFO] Stok produk " + nama + " bertambah " + jumlah + ". Total stok: " + this.stok);
        } else {
            System.out.println("[ERROR VALIDASI] Penambahan stok harus lebih dari 0!");
        }
    }

    // Mengurangi stok dengan validasi batas stok
    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah pengurangan stok harus lebih dari 0!");
            return false;
        }
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        } else {
            System.out.println("[ERROR] Stok produk " + nama + " tidak mencukupi! (Tersedia: " + this.stok + ", Diminta: " + jumlah + ")");
            return false;
        }
    }

    // Menghitung total nilai inventaris aset
    public int hitungNilaiInventaris() {
        return this.harga * this.stok;
    }

    // Menampilkan informasi detail produk
    public void tampilkanData() {
        System.out.println("=== DATA PRODUK ===");
        System.out.println("Kode        : " + kode);
        System.out.println("Nama        : " + nama);
        System.out.println("Harga       : Rp" + harga);
        System.out.println("Stok        : " + stok + " pcs");
        System.out.println("Inventaris  : Rp" + hitungNilaiInventaris());
        System.out.println("-------------------------");
    }
}
