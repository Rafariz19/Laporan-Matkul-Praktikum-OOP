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
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal : "06-10-2026";
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

        // Pengurangan stok pada produk yang diwarisi dari Superclass Abstract Produk
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

    // Overload 2A: Proses standar pencetakan ringkasan struk
    public void prosesTransaksi() {
        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();
    }

    // Overload 2B: Proses transaksi dengan pembayaran tunai dan kalkulasi kembalian
    public void prosesTransaksi(double uangDiterima) {
        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();

        double totalTagihan = hitungTotalBayar();
        System.out.println("PEMBAYARAN TUNAI (CASH):");
        System.out.printf("Tunai Diterima                             : Rp %,.0f\n", uangDiterima);

        if (uangDiterima >= totalTagihan) {
            double kembalian = uangDiterima - totalTagihan;
            System.out.printf("Kembalian                                  : Rp %,.0f\n", kembalian);
            System.out.println("======================================================================");
            System.out.println("                 Status Pembayaran: LUNAS                             ");
        } else {
            double kekurangan = totalTagihan - uangDiterima;
            System.out.printf("[PERINGATAN] Uang Kurang                  : Rp %,.0f\n", kekurangan);
            System.out.println("======================================================================");
            System.out.println("                 Status Pembayaran: BELUM LUNAS                       ");
        }
        System.out.println("======================================================================");
        System.out.println("                 Terima Kasih Atas Kunjungan Anda!                    ");
        System.out.println("======================================================================");
    }

    // Helper method untuk format tampilan struk profesional
    private void cetakHeaderStruk() {
        System.out.println("======================================================================");
        System.out.println("                         STRUK PENJUALAN TOKO                         ");
        System.out.println("======================================================================");
        System.out.println("No. Transaksi : " + idTransaksi);
        System.out.println("Tanggal       : " + tanggal);
        if (pelanggan != null) {
            System.out.println("Pelanggan     : " + pelanggan.getNama() + " (MEMBER-" + pelanggan.getTipeMember() + ")");
        } else {
            System.out.println("Pelanggan     : UMUM (Non-Member)");
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private void cetakDaftarBelanja() {
        System.out.println("DAFTAR BELANJAAN:");
        if (daftarItem.isEmpty()) {
            System.out.println("(Belum ada item belanja)");
            return;
        }
        int nomor = 1;
        for (ItemTransaksi item : daftarItem) {
            Produk p = item.getProduk();
            String discText = (item.getDiskonItem() > 0.0) ? String.format(" [Disc %.1f%%]", item.getDiskonItem() * 100) : "";
            System.out.printf("%d. %s - %s (%s)\n", nomor++, p.getKode(), p.getNama(), p.getKategoriInfo());
            System.out.printf("   %d x Rp %,d%s%s = Rp %,d\n",
                    item.getJumlahBeli(),
                    p.getHarga(),
                    discText,
                    padSpaces(26 - String.format("%d x Rp %,d%s", item.getJumlahBeli(), p.getHarga(), discText).length()),
                    item.hitungSubtotal());
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private void cetakFooterStruk() {
        int subtotal = hitungTotalSubtotal();
        double diskon = hitungDiskonNominal();
        double grandTotal = hitungTotalBayar();

        System.out.printf("Total Pembelian                            : Rp %,d\n", subtotal);
        if (pelanggan != null && pelanggan.getDiskon() > 0.0) {
            System.out.printf("Diskon Member (%s: %.0f%%)                  : Rp %,.0f\n",
                    pelanggan.getTipeMember(),
                    pelanggan.getDiskon() * 100,
                    diskon);
        }
        System.out.println("======================================================================");
        System.out.printf("TOTAL AKHIR                                : Rp %,.0f\n", grandTotal);
        System.out.println("======================================================================");
    }

    private String padSpaces(int count) {
        if (count <= 0) return " ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
