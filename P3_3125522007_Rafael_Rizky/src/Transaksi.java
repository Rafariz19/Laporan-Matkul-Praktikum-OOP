public class Transaksi {
    // 1. Penerapan Information Hiding: Seluruh atribut dibuat private
    private String idTransaksi;
    private String tanggal;
    private Pelanggan pelanggan;
    private Produk produk;
    private int jumlahBeli;

    // Constructor yang mematuhi aturan validasi
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan, Produk produk, int jumlahBeli) {
        if (idTransaksi != null && !idTransaksi.trim().isEmpty()) {
            this.idTransaksi = idTransaksi;
        } else {
            this.idTransaksi = "TRX-DEF";
            System.out.println("[ERROR VALIDASI] ID Transaksi tidak boleh kosong! Diset ke: TRX-DEF");
        }
        
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal : "01-01-2026";
        this.pelanggan = pelanggan;
        this.produk = produk;
        
        // Inisialisasi jumlah beli melalui setter dengan validasi
        setJumlahBeli(jumlahBeli);
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

    public Produk getProduk() {
        return produk;
    }

    public int getJumlahBeli() {
        return jumlahBeli;
    }

    // --- SETTER METHODS DENGAN VALIDASI DATA ---

    // Validasi: Jumlah beli harus > 0 dan tidak boleh melebihi stok produk yang tersedia
    public void setJumlahBeli(int jumlahBeli) {
        if (jumlahBeli <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah beli harus lebih besar dari 0! (Ditolak: " + jumlahBeli + ")");
            return;
        }
        if (produk != null && jumlahBeli > produk.getStok()) {
            System.out.println("[ERROR VALIDASI] Jumlah beli (" + jumlahBeli + " pcs) melebihi stok produk " + produk.getNama() + " yang tersedia (" + produk.getStok() + " pcs)!");
            return;
        }
        this.jumlahBeli = jumlahBeli;
    }

    // --- METODE KALKULASI & OPERASIONAL ---
    public int hitungSubtotal() {
        if (produk == null) return 0;
        return produk.getHarga() * jumlahBeli;
    }

    public double hitungDiskonNominal() {
        if (pelanggan == null) return 0.0;
        return hitungSubtotal() * pelanggan.getDiskon();
    }

    public double hitungTotalBayar() {
        return hitungSubtotal() - hitungDiskonNominal();
    }

    public void prosesTransaksi() {
        if (produk == null || pelanggan == null) {
            System.out.println("[GAGAL] Transaksi tidak dapat diproses karena data produk atau pelanggan tidak lengkap!");
            return;
        }

        if (jumlahBeli <= 0) {
            System.out.println("[GAGAL] Transaksi " + idTransaksi + " gagal diproses: Jumlah beli belum valid (0 atau negatif)!");
            return;
        }

        // Pengurangan stok secara aman melalui method kurangiStok yang telah dienkapsulasi
        boolean stokCukup = produk.kurangiStok(jumlahBeli);
        if (stokCukup) {
            System.out.println("========================================");
            System.out.println("         STRUK TRANSAKSI KASIR          ");
            System.out.println("========================================");
            System.out.println("ID Transaksi  : " + idTransaksi);
            System.out.println("Tanggal       : " + tanggal);
            System.out.println("Pelanggan     : " + pelanggan.getNama() + " (" + pelanggan.getTipeMember() + ")");
            System.out.println("Produk        : " + produk.getNama());
            System.out.println("Harga Satuan  : Rp" + produk.getHarga());
            System.out.println("Jumlah Beli   : " + jumlahBeli + " pcs");
            System.out.println("Subtotal      : Rp" + hitungSubtotal());
            System.out.println("Diskon Member : Rp" + (int) hitungDiskonNominal() + " (" + (int)(pelanggan.getDiskon() * 100) + "%)");
            System.out.println("----------------------------------------");
            System.out.println("TOTAL BAYAR   : Rp" + (int) hitungTotalBayar());
            System.out.println("========================================");
            System.out.println("[SUKSES] Transaksi " + idTransaksi + " berhasil diselesaikan.");
            System.out.println();
        } else {
            System.out.println("[GAGAL] Transaksi " + idTransaksi + " tidak dapat diselesaikan karena stok produk tidak mencukupi!");
            System.out.println();
        }
    }
}
