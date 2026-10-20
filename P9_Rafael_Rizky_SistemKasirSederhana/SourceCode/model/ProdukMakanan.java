package model;

/**
 * Subclass ProdukMakanan
 * Mewarisi abstract class Produk dan mengimplementasikan interface DapatDidiskon.
 * 
 * Sesuai ketentuan Modul 9:
 * Tidak lagi menduplikasi kode hitungDiskon dan getHargaSetelahDiskon
 * karena telah terstandarisasi melalui default method pada interface DapatDidiskon.
 */
public class ProdukMakanan extends Produk implements DapatDidiskon {
    private String tanggalKadaluarsa;

    /**
     * Constructor ProdukMakanan.
     * 
     * @param kode              kode barang makanan
     * @param nama              nama barang makanan
     * @param harga             harga satuan makanan
     * @param stok              jumlah stok makanan
     * @param tanggalKadaluarsa tanggal masa berlaku kadaluarsa (format: DD-MM-YYYY)
     */
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa) {
        super(kode, nama, harga, stok);
        if (tanggalKadaluarsa == null || tanggalKadaluarsa.trim().isEmpty()) {
            this.tanggalKadaluarsa = "01-01-2027";
            System.out.println("[PERINGATAN] Tanggal kadaluarsa tidak valid! Diset ke default: 01-01-2027");
        } else {
            this.tanggalKadaluarsa = tanggalKadaluarsa.trim();
        }
    }

    // --- GETTER & BEHAVIOR METHOD ---

    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }

    /**
     * Mengubah tanggal kadaluarsa dengan validasi umpan balik.
     * 
     * @param tanggalBaru tanggal kadaluarsa baru (DD-MM-YYYY)
     * @return true jika berhasil, false jika input kosong
     */
    public boolean ubahTanggalKadaluarsa(String tanggalBaru) {
        if (tanggalBaru != null && !tanggalBaru.trim().isEmpty()) {
            this.tanggalKadaluarsa = tanggalBaru.trim();
            return true;
        }
        System.out.println("[ERROR VALIDASI] Tanggal kadaluarsa tidak boleh kosong!");
        return false;
    }

    // --- IMPLEMENTASI ABSTRACT METHOD SUPERCLASS PRODUK ---

    @Override
    public String getKategoriInfo() {
        return "Makanan & Minuman Segar (Konsumsi)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Kadaluarsa  : " + tanggalKadaluarsa);
    }
}
