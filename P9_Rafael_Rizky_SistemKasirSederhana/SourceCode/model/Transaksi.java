package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Transaksi
 * Entitas sentral pengendali proses kasir penjualan.
 * 
 * Hubungan Relasi:
 * 1. Association (-->) ke Pelanggan: Transaksi menggunakan data Pelanggan (uses-a)
 *    untuk perhitungan diskon member. Objek Pelanggan dapat berupa null (pembeli umum).
 * 2. Composition (*-->) ke ItemTransaksi: Transaksi memiliki dan mengontrol siklus
 *    hidup dari ItemTransaksi (part-of). ItemTransaksi tidak berdiri sendiri tanpa Transaksi.
 * 
 * Sesuai ketentuan Modul 9:
 * - Memiliki responsibility menyeluruh atas alur checkout, kalkulasi total,
 *   manajemen stok inventaris, serta umpan balik status pembayaran.
 */
public class Transaksi {
    private String idTransaksi;
    private String tanggal;
    private Pelanggan pelanggan;
    private ArrayList<ItemTransaksi> daftarItem;
    private String statusTransaksi; // "PENDING", "LUNAS", "DIBATALKAN"

    /**
     * Constructor Transaksi.
     * 
     * @param idTransaksi nomor nota transaksi kasir
     * @param tanggal     waktu pencatatan transaksi (DD-MM-YYYY HH:mm)
     * @param pelanggan   objek Pelanggan berelasi asosiasi (bisa null jika pembeli umum)
     */
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan) {
        if (idTransaksi == null || idTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] ID Transaksi tidak boleh null atau kosong!");
        }
        this.idTransaksi = idTransaksi.trim();
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal.trim() : "20-10-2026";
        this.pelanggan = pelanggan;
        this.daftarItem = new ArrayList<>();
        this.statusTransaksi = "PENDING";
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

    /**
     * Mengembalikan daftar item belanja secara unmodifiable list
     * guna melindungi enkapsulasi komposisi internal Transaksi.
     * 
     * @return list read-only dari ItemTransaksi
     */
    public List<ItemTransaksi> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    // =========================================================================
    // METHOD OVERLOADING 1: tambahItem (Penambahan Keranjang & Kontrol Stok)
    // =========================================================================

    /**
     * Menambahkan item belanjaan tanpa diskon promosi baris.
     * 
     * @param produk     objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli
     * @return true jika berhasil ditambahkan, false jika gagal (stok tidak cukup dsb)
     */
    public boolean tambahItem(Produk produk, int jumlahBeli) {
        return tambahItem(produk, jumlahBeli, 0.0);
    }

    /**
     * Menambahkan item belanjaan dengan diskon promosi per baris barang.
     * 
     * @param produk        objek produk polimorfik
     * @param jumlahBeli    kuantitas yang dibeli
     * @param diskonPromosi persentase diskon promosi barang (0.0 s/d 1.0)
     * @return true jika penambahan sukses, false jika validasi/stok gagal
     */
    public boolean tambahItem(Produk produk, int jumlahBeli, double diskonPromosi) {
        if (!"PENDING".equals(this.statusTransaksi)) {
            System.out.println("[ERROR] Tidak dapat menambah item! Transaksi sudah berstatus: " + this.statusTransaksi);
            return false;
        }
        if (produk == null) {
            System.out.println("[ERROR VALIDASI] Objek produk tidak valid (null)!");
            return false;
        }
        if (jumlahBeli <= 0) {
            System.out.println("[ERROR VALIDASI] Jumlah beli harus lebih besar dari 0! (Ditolak: " + jumlahBeli + ")");
            return false;
        }
        if (!produk.cekKetersediaan(jumlahBeli)) {
            System.out.println("[ERROR STOK] Pembelian " + produk.getNama() + " (" + jumlahBeli + " pcs) melebihi stok yang ada (" + produk.getStok() + " pcs)!");
            return false;
        }

        // Pengurangan stok produk secara aman
        boolean stokBerhasilDikurangi = produk.kurangiStok(jumlahBeli);
        if (!stokBerhasilDikurangi) {
            return false;
        }

        // Relasi Komposisi: Membuat objek ItemTransaksi di dalam Transaksi
        ItemTransaksi itemBaru = new ItemTransaksi(produk, jumlahBeli, diskonPromosi);
        daftarItem.add(itemBaru);

        String infoPromo = (diskonPromosi > 0.0) ? String.format(" [PROMO %.0f%%]", diskonPromosi * 100) : "";
        System.out.println("[INFO] Berhasil menambahkan " + jumlahBeli + " pcs " + produk.getNama() + infoPromo + " ke transaksi " + idTransaksi);
        return true;
    }

    /**
     * Membatalkan salah satu baris item belanja dan mengembalikan stok barang ke inventaris.
     * 
     * @param index nomor indeks item dalam daftar (0-indexed)
     * @return true jika berhasil dibatalkan, false jika indeks tidak valid
     */
    public boolean batalkanItem(int index) {
        if (!"PENDING".equals(this.statusTransaksi)) {
            System.out.println("[ERROR] Tidak dapat membatalkan item pada transaksi berstatus: " + this.statusTransaksi);
            return false;
        }
        if (index >= 0 && index < daftarItem.size()) {
            ItemTransaksi itemDihapus = daftarItem.remove(index);
            // Kembalikan stok ke objek Produk
            itemDihapus.getProduk().tambahStok(itemDihapus.getJumlahBeli());
            System.out.println("[INFO] Item " + itemDihapus.getProduk().getNama() + " dibatalkan dari transaksi dan stok dikembalikan.");
            return true;
        }
        System.out.println("[ERROR] Indeks item tidak valid: " + index);
        return false;
    }

    // --- METODE KALKULASI FINANSIAL TRANSAKSI ---

    /**
     * Menghitung total akumulasi subtotal dari seluruh item belanjaan.
     * 
     * @return total subtotal dalam Rupiah
     */
    public int hitungTotalSubtotal() {
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    /**
     * Menghitung besaran nominal potongan diskon member.
     * Mendelegasikan kalkulasi kepada objek Pelanggan (Association responsibility).
     * 
     * @return nominal potongan diskon member
     */
    public double hitungDiskonNominal() {
        if (pelanggan != null) {
            return pelanggan.hitungPotonganMember(hitungTotalSubtotal());
        }
        return 0.0;
    }

    /**
     * Menghitung nilai total akhir yang wajib dibayar konsumen.
     * 
     * @return grand total tagihan belanja
     */
    public double hitungTotalBayar() {
        return hitungTotalSubtotal() - hitungDiskonNominal();
    }

    // =========================================================================
    // METHOD OVERLOADING 2: prosesTransaksi (Checkout & Pembayaran)
    // =========================================================================

    /**
     * Menampilkan struk ringkasan preview tagihan tanpa proses pembayaran tunai.
     */
    public void prosesTransaksi() {
        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();
    }

    /**
     * Memproses checkout transaksi dengan validasi pembayaran tunai,
     * kalkulasi kembalian, serta pembaruan status kelunasan.
     * 
     * @param uangDiterima nominal uang tunai yang diserahkan konsumen
     * @return true jika pembayaran sukses dan lunas, false jika uang kurang atau keranjang kosong
     */
    public boolean prosesTransaksi(double uangDiterima) {
        if (daftarItem.isEmpty()) {
            System.out.println("[ERROR CHECKOUT] Transaksi " + idTransaksi + " tidak dapat diproses karena keranjang belanja kosong!");
            return false;
        }

        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();

        double totalTagihan = hitungTotalBayar();
        System.out.println("PEMBAYARAN TUNAI (CASH):");
        System.out.printf("Tunai Diterima                             : Rp %,.0f\n", uangDiterima);

        if (uangDiterima >= totalTagihan) {
            double kembalian = uangDiterima - totalTagihan;
            this.statusTransaksi = "LUNAS";
            System.out.printf("Kembalian                                  : Rp %,.0f\n", kembalian);
            System.out.println("======================================================================");
            System.out.println("                 Status Pembayaran: LUNAS                             ");
            System.out.println("======================================================================");
            System.out.println("                 Terima Kasih Atas Kunjungan Anda!                    ");
            System.out.println("======================================================================");
            return true;
        } else {
            double kekurangan = totalTagihan - uangDiterima;
            this.statusTransaksi = "PENDING";
            System.out.printf("[PERINGATAN] Uang Kurang                  : Rp %,.0f\n", kekurangan);
            System.out.println("======================================================================");
            System.out.println("                 Status Pembayaran: BELUM LUNAS                       ");
            System.out.println("======================================================================");
            System.out.println("         Silakan tambahkan pembayaran untuk menyelesaikan transaksi   ");
            System.out.println("======================================================================");
            return false;
        }
    }

    // --- HELPER METHODS UNTUK FORMATTING STRUK ---

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
        System.out.println("Status        : " + statusTransaksi);
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
        if (count <= 0) {
            return " ";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
