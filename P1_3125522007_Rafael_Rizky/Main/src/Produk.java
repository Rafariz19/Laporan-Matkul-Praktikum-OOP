public class Produk {
    String kode;
    String nama;
    int harga;
    int stok;

    void tampilkanData() {
        System.out.println("=== DATA PRODUK ===");
        System.out.println("Kode  : " + kode);
        System.out.println("Nama  : " + nama);
        System.out.println("Harga : Rp" + harga);
        System.out.println("Stok  : " + stok);
    }
}