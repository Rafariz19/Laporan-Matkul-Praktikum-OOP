public class Transaksi {
    String idTransaksi;
    String tanggal;
    Pelanggan pelanggan;
    Produk produk;
    int jumlahBeli;

    // Constructor untuk inisialisasi object Transaksi
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan, Produk produk, int jumlahBeli) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.jumlahBeli = jumlahBeli;
    }

    // Method dengan return value: menghitung subtotal pembelian
    public int hitungSubtotal() {
        return produk.harga * jumlahBeli;
    }

    // Method dengan return value: menghitung besaran potongan diskon member
    public double hitungDiskonNominal() {
        return hitungSubtotal() * pelanggan.getDiskon();
    }

    // Method dengan return value: menghitung total akhir yang harus dibayar
    public double hitungTotalBayar() {
        return hitungSubtotal() - hitungDiskonNominal();
    }

    // Method dengan parameter: memperbarui jumlah barang yang dibeli
    public void ubahJumlahBeli(int jumlahBaru) {
        this.jumlahBeli = jumlahBaru;
        System.out.println("[INFO] Jumlah beli pada transaksi " + idTransaksi + " berhasil diubah menjadi: " + jumlahBaru + " pcs");
    }

    // Method tanpa parameter: memproses checkout transaksi, mencetak struk, dan mengurangi stok
    public void prosesTransaksi() {
        if (produk.stok >= jumlahBeli) {
            produk.kurangiStok(jumlahBeli);
            System.out.println("========================================");
            System.out.println("         STRUK TRANSAKSI KASIR          ");
            System.out.println("========================================");
            System.out.println("ID Transaksi  : " + idTransaksi);
            System.out.println("Tanggal       : " + tanggal);
            System.out.println("Pelanggan     : " + pelanggan.nama + " (" + pelanggan.tipeMember + ")");
            System.out.println("Produk        : " + produk.nama);
            System.out.println("Harga Satuan  : Rp" + produk.harga);
            System.out.println("Jumlah Beli   : " + jumlahBeli + " pcs");
            System.out.println("Subtotal      : Rp" + hitungSubtotal());
            System.out.println("Diskon Member : Rp" + (int) hitungDiskonNominal() + " (" + (int) (pelanggan.getDiskon() * 100) + "%)");
            System.out.println("----------------------------------------");
            System.out.println("TOTAL BAYAR   : Rp" + (int) hitungTotalBayar());
            System.out.println("========================================");
            System.out.println("[SUKSES] Transaksi " + idTransaksi + " berhasil diselesaikan.");
            System.out.println();
        } else {
            System.out.println("[GAGAL] Transaksi " + idTransaksi + " gagal diproses: Stok " + produk.nama + " tidak mencukupi!");
            System.out.println();
        }
    }
}
