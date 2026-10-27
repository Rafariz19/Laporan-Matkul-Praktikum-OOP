package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.exception.KasirException;
import model.exception.PembayaranKurangException;
import model.exception.StatusTransaksiInvalidException;
import model.exception.StokTidakCukupException;
import model.exception.TransaksiKosongException;

/**
 * Class: Transaksi
 * Entitas sentral pengendali proses kasir penjualan toko.
 * 
 * Hubungan Relasi:
 * 1. Association (-->) ke Pelanggan: Transaksi menggunakan data Pelanggan (uses-a).
 * 2. Composition (*-->) ke ItemTransaksi: Transaksi mengontrol siklus hidup ItemTransaksi.
 * 
 * Sesuai Modul 10:
 * - Menjamin Konsistensi State & Atomisitas Transaksi (Fail-Safe Invariants).
 * - Mengganti silent failure / return boolean dengan throwing Checked Exception spesifik:
 *   * StokTidakCukupException saat kuantitas barang melebihi stok fisik produk.
 *   * PembayaranKurangException saat uang tunai konsumen tidak mencukupi total tagihan.
 *   * TransaksiKosongException saat checkout dipanggil pada nota tanpa komoditas.
 *   * StatusTransaksiInvalidException saat mutasi dicoba pada nota yang sudah LUNAS.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
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
     * @param idTransaksi nomor nota transaksi kasir (tidak boleh null atau kosong)
     * @param tanggal     waktu pencatatan transaksi (DD-MM-YYYY HH:mm)
     * @param pelanggan   objek Pelanggan (asosiasi opsional, bisa null untuk non-member)
     * @throws IllegalArgumentException jika idTransaksi null atau kosong
     */
    public Transaksi(String idTransaksi, String tanggal, Pelanggan pelanggan) {
        if (idTransaksi == null || idTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] ID Transaksi tidak boleh null atau kosong!");
        }
        this.idTransaksi = idTransaksi.trim();
        this.tanggal = (tanggal != null && !tanggal.trim().isEmpty()) ? tanggal.trim() : "26-10-2026 10:00";
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
     * @throws KasirException jika terjadi pelanggaran stok atau status transaksi
     * @throws IllegalArgumentException jika parameter produk atau jumlah tidak valid
     */
    public void tambahItem(Produk produk, int jumlahBeli) throws KasirException {
        tambahItem(produk, jumlahBeli, 0.0);
    }

    /**
     * Menambahkan item belanjaan dengan diskon promosi per baris barang.
     * Menjamin prinsip Atomisitas: stok berkurang HANYA jika validasi lolos,
     * dan item masuk ke daftar belanja HANYA jika stok berhasil dikurangi.
     * 
     * @param produk        objek produk polimorfik (tidak boleh null)
     * @param jumlahBeli    kuantitas yang dibeli (> 0)
     * @param diskonPromosi persentase diskon promosi barang (0.0 s/d 1.0)
     * @throws KasirException jika stok tidak cukup atau status bukan PENDING
     * @throws IllegalArgumentException jika parameter bernilai di luar batas
     */
    public void tambahItem(Produk produk, int jumlahBeli, double diskonPromosi) throws KasirException {
        // 1. Validasi State Transaksi
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "tambahItem");
        }

        // 2. Validasi Parameter Masukan
        if (produk == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Objek produk tidak valid (null)!");
        }
        if (jumlahBeli <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Jumlah beli harus lebih besar dari 0! (Diberikan: " + jumlahBeli + ")");
        }
        if (diskonPromosi < 0.0 || diskonPromosi > 1.0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Diskon promosi harus di antara 0.0 dan 1.0! (Diberikan: " + diskonPromosi + ")");
        }

        // 3. Mutasi State Stok Produk (Melempar StokTidakCukupException jika melebihi stok)
        // Operasi ini menjamin jika exception dilempar, baris berikutnya TIDAK dieksekusi.
        produk.kurangiStok(jumlahBeli);

        // 4. Relasi Komposisi: Membuat objek ItemTransaksi di dalam Transaksi
        ItemTransaksi itemBaru = new ItemTransaksi(produk, jumlahBeli, diskonPromosi);
        daftarItem.add(itemBaru);

        String infoPromo = (diskonPromosi > 0.0) ? String.format(" [PROMO %.0f%%]", diskonPromosi * 100) : "";
        System.out.println("[INFO] Berhasil menambahkan " + jumlahBeli + " unit " + produk.getNama() + infoPromo + " ke transaksi " + idTransaksi);
    }

    /**
     * Membatalkan salah satu baris item belanja dan mengembalikan stok barang ke inventaris.
     * 
     * @param index nomor indeks item dalam daftar (0-indexed)
     * @throws KasirException jika transaksi bukan PENDING
     * @throws IndexOutOfBoundsException jika indeks di luar jangkauan list
     */
    public void batalkanItem(int index) throws KasirException {
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "batalkanItem");
        }
        if (index < 0 || index >= daftarItem.size()) {
            throw new IndexOutOfBoundsException(String.format(
                "[ERROR VALIDASI] Indeks item tidak valid! (Indeks: %d, Jumlah Item: %d)",
                index, daftarItem.size()
            ));
        }

        ItemTransaksi itemDihapus = daftarItem.remove(index);
        // Mengembalikan stok secara aman ke objek Produk
        itemDihapus.getProduk().tambahStok(itemDihapus.getJumlahBeli());
        System.out.println("[INFO] Item " + itemDihapus.getProduk().getNama() + " (" + itemDihapus.getJumlahBeli() + " unit) dibatalkan dari transaksi dan stok inventaris dikembalikan.");
    }

    // --- METODE KALKULASI FINANSIAL TRANSAKSI ---

    public int hitungTotalSubtotal() {
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    public double hitungDiskonNominal() {
        if (pelanggan != null) {
            return pelanggan.hitungPotonganMember(hitungTotalSubtotal());
        }
        return 0.0;
    }

    public double hitungTotalBayar() {
        return hitungTotalSubtotal() - hitungDiskonNominal();
    }

    // =========================================================================
    // METHOD OVERLOADING 2: prosesTransaksi (Checkout & Pembayaran)
    // =========================================================================

    /**
     * Menampilkan struk ringkasan preview tagihan tanpa proses pembayaran tunai.
     * 
     * @throws KasirException jika keranjang belanja masih kosong
     */
    public void prosesTransaksi() throws KasirException {
        if (daftarItem.isEmpty()) {
            throw new TransaksiKosongException(this.idTransaksi);
        }
        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();
    }

    /**
     * Memproses checkout transaksi dengan validasi pembayaran tunai,
     * kalkulasi kembalian, serta penegakan konsistensi state.
     * 
     * Sesuai Modul 10:
     * - Jika keranjang kosong -> lempar TransaksiKosongException
     * - Jika uang kurang -> lempar PembayaranKurangException, state statusTransaksi tetap "PENDING"
     * - Jika uang mencukupi -> state statusTransaksi menjadi "LUNAS", cetak struk lengkap
     * 
     * @param uangDiterima nominal uang tunai yang diserahkan konsumen
     * @return true jika pembayaran sukses dan lunas
     * @throws KasirException jika terjadi pelanggaran aturan kasir
     * @throws IllegalArgumentException jika nominal uang diserahkan <= 0
     */
    public boolean prosesTransaksi(double uangDiterima) throws KasirException {
        // 1. Validasi Status Transaksi
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "prosesTransaksi");
        }

        // 2. Validasi Keranjang Belanja Kosong
        if (daftarItem.isEmpty()) {
            throw new TransaksiKosongException(this.idTransaksi);
        }

        // 3. Validasi Uang Masuk Positif
        if (uangDiterima <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nominal pembayaran harus lebih besar dari 0! (Diberikan: Rp " + uangDiterima + ")");
        }

        double totalTagihan = hitungTotalBayar();

        // 4. Validasi Kecukupan Uang Tunai (PembayaranKurangException)
        if (uangDiterima < totalTagihan) {
            // State transaksi dijamin TIDAK BERUBAH (tetap PENDING)
            throw new PembayaranKurangException(this.idTransaksi, totalTagihan, uangDiterima);
        }

        // 5. Pembayaran Berhasil: Mutasi State ke "LUNAS"
        this.statusTransaksi = "LUNAS";
        double kembalian = uangDiterima - totalTagihan;

        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();

        System.out.println("PEMBAYARAN TUNAI (CASH):");
        System.out.printf("Tunai Diterima                             : Rp %,.0f\n", uangDiterima);
        System.out.printf("Kembalian                                  : Rp %,.0f\n", kembalian);
        System.out.println("======================================================================");
        System.out.println("                 Status Pembayaran: LUNAS                             ");
        System.out.println("======================================================================");
        System.out.println("                 Terima Kasih Atas Kunjungan Anda!                    ");
        System.out.println("======================================================================");
        return true;
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
