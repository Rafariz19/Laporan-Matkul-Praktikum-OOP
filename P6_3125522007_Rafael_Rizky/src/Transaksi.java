import java.util.ArrayList;

public class Transaksi {
    private String idTransaksi;
    private String tanggal;

    // Relasi Association: Transaksi memiliki referensi ke Pelanggan (uses-a)
    private Pelanggan pelanggan;

    // Relasi Composition: Transaksi mengelola kumpulan objek ItemTransaksi (part-of)
    private ArrayList<ItemTransaksi> daftarItem;

    // Constructor Transaksi
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan) {
        if (idTransaksi != null && !idTransaksi.trim().isEmpty()) {
            this.idTransaksi = idTransaksi;
        } else {
            this.idTransaksi = "TRX-DEF";
            System.out.println("[ERROR VALIDASI] ID Transaksi tidak boleh kosong! Diset ke: TRX-DEF");
        }
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal : "29-09-2026";
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

    // =========================================================================
    // METHOD OVERLOADING 1: tambahItem
    // =========================================================================

    // Overload 1A: Penambahan item reguler tanpa diskon promosi per item
    public boolean tambahItem(Produk produk, int jumlahBeli) {
        return tambahItem(produk, jumlahBeli, 0.0);
    }

    // Overload 1B: Penambahan item belanja dengan diskon promosi khusus barang
    public boolean tambahItem(Produk produk, int jumlahBeli, double diskonPromosi) {
        if (produk == null) {
            System.out.println("[GAGAL] Objek produk tidak valid (null)!");
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
        ItemTransaksi itemBaru = new ItemTransaksi(produk, jumlahBeli, diskonPromosi);
        daftarItem.add(itemBaru);
        
        String infoPromo = (diskonPromosi > 0.0) ? String.format(" [PROMO %.0f%%]", diskonPromosi * 100) : "";
        System.out.println("[INFO] Berhasil menambahkan " + jumlahBeli + " pcs " + produk.getNama() + infoPromo + " ke transaksi " + idTransaksi);
        return true;
    }

    // --- METODE KALKULASI TOTAL ---
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

    // =========================================================================
    // METHOD OVERLOADING 2: prosesTransaksi
    // =========================================================================

    // Overload 2A: Proses transaksi standar (tanpa parameter uang tunai)
    public void prosesTransaksi() {
        cetakHeaderStruk();
        System.out.println("Status Bayar      : LUNAS (Non-Tunai / Otomatis)");
        System.out.println("==========================================================");
        System.out.println("[SUKSES] Transaksi " + idTransaksi + " berhasil diselesaikan.");
        System.out.println();
    }

    // Overload 2B: Proses transaksi dengan parameter uang tunai & hitung kembalian
    public void prosesTransaksi(double uangDiterima) {
        cetakHeaderStruk();
        double totalTagihan = hitungTotalBayar();
        System.out.println("Uang Diterima     : Rp" + (int)uangDiterima);
        if (uangDiterima >= totalTagihan) {
            double kembalian = uangDiterima - totalTagihan;
            System.out.println("Uang Kembalian    : Rp" + (int)kembalian);
            System.out.println("Status Bayar      : LUNAS (Pembayaran Tunai)");
            System.out.println("==========================================================");
            System.out.println("[SUKSES] Transaksi " + idTransaksi + " berhasil diselesaikan.");
        } else {
            double kekurangan = totalTagihan - uangDiterima;
            System.out.println("Uang Kurang       : Rp" + (int)kekurangan);
            System.out.println("Status Bayar      : BELUM LUNAS (Uang Pembayaran Kurang)");
            System.out.println("==========================================================");
            System.out.println("[PERINGATAN] Pembayaran transaksi " + idTransaksi + " belum mencukupi!");
        }
        System.out.println();
    }

    // Helper privat untuk mencetak isi rincian nota belanja
    private void cetakHeaderStruk() {
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
        System.out.println("DAFTAR BELANJA (POLYMORPHIC PRODUCT ITEMS):");
        System.out.println("  Nama Barang            | Jml     | Harga Satuan | Subtotal");
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
        System.out.println("TOTAL TAGIHAN     : Rp" + (int)hitungTotalBayar());
    }
}
