package model;

/**
 * Subclass ProdukElektronik
 * Mewarisi abstract class Produk dan mengimplementasikan interface DapatDidiskon.
 * 
 * Sesuai ketentuan Modul 9:
 * Tidak lagi menduplikasi kode hitungDiskon dan getHargaSetelahDiskon
 * karena telah terstandarisasi melalui default method pada interface DapatDidiskon.
 */
public class ProdukElektronik extends Produk implements DapatDidiskon {
    private int garansiBulan;

    /**
     * Constructor ProdukElektronik.
     * 
     * @param kode         kode barang elektronik
     * @param nama         nama barang elektronik
     * @param harga        harga satuan barang
     * @param stok         jumlah stok awal
     * @param garansiBulan durasi garansi resmi dalam satuan bulan (harus >= 0)
     */
    public ProdukElektronik(String kode, String nama, int harga, int stok, int garansiBulan) {
        super(kode, nama, harga, stok);
        if (garansiBulan < 0) {
            this.garansiBulan = 0;
            System.out.println("[PERINGATAN] Durasi garansi tidak boleh negatif! Diset ke 0 bulan.");
        } else {
            this.garansiBulan = garansiBulan;
        }
    }

    // --- GETTER & BEHAVIOR METHOD ---

    public int getGaransiBulan() {
        return garansiBulan;
    }

    /**
     * Mengubah masa garansi dengan validasi umpan balik.
     * 
     * @param garansiBaru durasi garansi baru dalam bulan (harus >= 0)
     * @return true jika berhasil, false jika nilai negatif
     */
    public boolean ubahGaransiBulan(int garansiBaru) {
        if (garansiBaru >= 0) {
            this.garansiBulan = garansiBaru;
            return true;
        }
        System.out.println("[ERROR VALIDASI] Masa garansi tidak boleh negatif! (Ditolak: " + garansiBaru + " bulan)");
        return false;
    }

    // --- IMPLEMENTASI ABSTRACT METHOD SUPERCLASS PRODUK ---

    @Override
    public String getKategoriInfo() {
        return "Elektronik & Aksesoris Gadget (Hardware)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Garansi     : " + garansiBulan + " Bulan (Garansi Resmi)");
    }
}
