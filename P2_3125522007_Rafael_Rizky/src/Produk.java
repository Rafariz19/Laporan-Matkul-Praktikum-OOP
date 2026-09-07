public class Produk {
    String kode;
    String nama;
    int harga;
    int stok;

    // Constructor untuk inisialisasi object Produk
    public Produk(String kode, String nama, int harga, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    // Method tanpa parameter: menampilkan detail data produk
    public void tampilkanData() {
        System.out.println("=== DATA PRODUK ===");
        System.out.println("Kode        : " + kode);
        System.out.println("Nama        : " + nama);
        System.out.println("Harga       : Rp" + harga);
        System.out.println("Stok        : " + stok);
        System.out.println("-------------------------");
    }

    // Method dengan parameter: memperbarui harga produk
    public void ubahHarga(int hargaBaru) {
        this.harga = hargaBaru;
        System.out.println("[INFO] Harga produk " + nama + " berhasil diubah menjadi Rp" + hargaBaru);
    }

    // Method dengan parameter: menambah stok produk
    public void tambahStok(int jumlah) {
        this.stok += jumlah;
        System.out.println("[INFO] Stok produk " + nama + " bertambah " + jumlah + ". Total stok saat ini: " + this.stok);
    }

    // Method dengan parameter: mengurangi stok saat transaksi
    public void kurangiStok(int jumlah) {
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
        } else {
            System.out.println("[PERINGATAN] Stok produk " + nama + " tidak mencukupi!");
        }
    }

    // Method dengan return value: menghitung total nilai aset inventaris produk
    public int hitungNilaiInventaris() {
        return this.harga * this.stok;
    }

    // Method dengan return value: mengembalikan nama produk
    public String getNama() {
        return this.nama;
    }
}
