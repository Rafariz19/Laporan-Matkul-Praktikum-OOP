import java.util.ArrayList;

public class Transaksi {
    private String idTransaksi;
    private String tanggal;

    // 1. Relasi Association: Transaksi memiliki referensi ke Pelanggan (uses-a)
    private Pelanggan pelanggan;

    // 2. Relasi Composition: Transaksi mengelola kumpulan objek ItemTransaksi (part-of)
    private ArrayList<ItemTransaksi> daftarItem;

    // Constructor Transaksi
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan) {
        if (idTransaksi != null && !idTransaksi.trim().isEmpty()) {
            this.idTransaksi = idTransaksi;
        } else {
            this.idTransaksi = "TRX-DEF";
            System.out.println("[ERROR VALIDASI] ID Transaksi tidak boleh kosong! Diset ke: TRX-DEF");
        }
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal : "22-09-2026";
        this.pelanggan = pelanggan;
        this.daftarItem = new ArrayList<>();
    }

    // --- GETTER METHODS ---
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public ArrayList<ItemTransaksi> getDaftarItem() {
        return daftarItem;
    }

    // --- IMPLEMENTASI COMPOSITION: Menambahkan Item ke Transaksi ---
    // Menerima parameter superclass Produk (dapat berupa ProdukMakanan atau ProdukElektronik)
    public boolean tambahItem(Produk produk, int jumlahBeli) {
        if (produk == null) {
            System.out.println("[GAGAL] Produk tidak valid (null)!");
            return false;
        }
        if (jumlahBeli <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah beli harus lebih besar dari 0! (Ditolak: " + jumlahBeli + ")");
            return false;
        }
        if (jumlahBeli > produk.getStok()) {
            System.out.println("[ERROR STOK] Pembelian " + produk.getNama() + " (" + jumlahBeli + " pcs) melebihi stok yang ada (" + produk.getStok() + " pcs)!");
            return false;
        }

        // Pengurangan stok pada produk yang diwarisi dari Superclass
        produk.kurangiStok(jumlahBeli);

        // Composition: Menciptakan objek ItemTransaksi di dalam Transaksi
        ItemTransaksi itemBaru = new ItemTransaksi(produk, jumlahBeli);
        daftarItem.add(itemBaru);
        System.out.println("[INFO] Berhasil menambahkan " + jumlahBeli + " pcs " + produk.getNama() + " ke transaksi " + idTransaksi);
        return true;
    }

    // --- METODE KALKULASI & STRUK ---
    public int hitungTotalSubtotal() {
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    public double hitungDiskonNominal() {
        if (pelanggan != null) {
            return hitungTotalSubtotal() * pelanggan.getDiskon();
        }
        return 0.0;
    }

    public double hitungTotalBayar() {
        return hitungTotalSubtotal() - hitungDiskonNominal();
    }

    public void prosesTransaksi() {
        if (daftarItem.isEmpty()) {
            System.out.println("[GAGAL] Transaksi " + idTransaksi + " tidak dapat diproses: Keranjang belanja kosong!");
            return;
        }

        System.out.println("==========================================================");
        System.out.println("                 STRUK RESMI KASIR TOKO                   ");
        System.out.println("==========================================================");
        System.out.println("No. Transaksi : " + idTransaksi);
        System.out.println("Tanggal       : " + tanggal);
        if (pelanggan != null) {
            System.out.println("Pelanggan     : " + pelanggan.getNama() + " (" + pelanggan.getTipeMember() + ")");
            System.out.println("Nomor HP      : " + pelanggan.getNomorHP());
        } else {
            System.out.println("Pelanggan     : UMUM (Non-Member)");
        }
        System.out.println("----------------------------------------------------------");
        System.out.println("DAFTAR BELANJA (MAKANAN & ELEKTRONIK):");
        System.out.println("  Nama Barang      | Jml     | Harga   | Total");
        System.out.println("  --------------------------------------------------------");
        for (ItemTransaksi item : daftarItem) {
            item.tampilkanItem();
        }
        System.out.println("----------------------------------------------------------");
        System.out.println("Total Subtotal    : Rp" + hitungTotalSubtotal());
        if (pelanggan != null) {
            System.out.println("Diskon Member     : Rp" + (int)hitungDiskonNominal() + " (" + (int)(pelanggan.getDiskon() * 100) + "%)");
        }
        System.out.println("----------------------------------------------------------");
        System.out.println("TOTAL AKHIR BAYAR : Rp" + (int)hitungTotalBayar());
        System.out.println("==========================================================");
        System.out.println("[SUKSES] Transaksi " + idTransaksi + " berhasil diproses dan disimpan.");
        System.out.println();
    }
}
