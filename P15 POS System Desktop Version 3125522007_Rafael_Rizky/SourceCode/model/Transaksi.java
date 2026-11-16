package model;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.exception.KasirException;
import model.exception.PembayaranKurangException;
import model.exception.ProdukKadaluarsaException;
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
 * 3. Strategy Pattern: Menggunakan MetodePembayaran untuk eksekusi checkout fleksibel.
 * 
 * Sesuai Modul 10, Fase 1 & Fase 2:
 * - Menjamin Konsistensi State & Atomisitas Transaksi (Fail-Safe Invariants).
 * - Throwing Checked Exception spesifik:
 *   * StokTidakCukupException saat kuantitas barang melebihi stok fisik produk.
 *   * PembayaranKurangException saat uang tunai/digital konsumen tidak mencukupi total tagihan.
 *   * TransaksiKosongException saat checkout dipanggil pada nota tanpa komoditas.
 *   * StatusTransaksiInvalidException saat mutasi dicoba pada nota yang sudah LUNAS.
 *   * ProdukKadaluarsaException saat komoditas makanan kadaluarsa hendak dimasukkan ke keranjang.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class Transaksi {
    private String idTransaksi;
    private String tanggal;
    private Pelanggan pelanggan;
    private ArrayList<ItemTransaksi> daftarItem;
    private String statusTransaksi; // "PENDING", "LUNAS", "DIBATALKAN"
    private MetodePembayaran metodePembayaranTerpilih; // Disimpan saat checkout berhasil

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

    public MetodePembayaran getMetodePembayaranTerpilih() {
        return metodePembayaranTerpilih;
    }

    // =========================================================================
    // METHOD OVERLOADING 1: tambahItem (Penambahan Keranjang & Kontrol Stok)
    // =========================================================================

    /**
     * Menambahkan item belanjaan dengan pengecekan kedaluwarsa & kalkulasi diskon dinamis (jika makanan).
     * Menggunakan Clock sistem default.
     * 
     * @param produk     objek produk polimorfik
     * @param jumlahBeli kuantitas yang dibeli
     * @throws KasirException jika terjadi pelanggaran stok, status, atau expired
     */
    public void tambahItem(Produk produk, int jumlahBeli) throws KasirException {
        tambahItem(produk, jumlahBeli, Clock.systemDefaultZone());
    }

    /**
     * Menambahkan item belanjaan dengan evaluasi kedaluwarsa berbasis Clock yang diinjeksi.
     * Jika produk adalah ProdukMakanan:
     * - Lempar ProdukKadaluarsaException jika sudah lewat tanggal kadaluarsa.
     * - Terapkan diskon dinamis mendekati kadaluarsa otomatis ke baris item belanja.
     */
    public void tambahItem(Produk produk, int jumlahBeli, Clock clock) throws KasirException {
        double diskonPromo = (produk != null) ? produk.getDiskonPromo() : 0.0;
        if (produk instanceof ProdukMakanan) {
            ProdukMakanan pm = (ProdukMakanan) produk;
            if (pm.isKadaluarsa(clock)) {
                throw new ProdukKadaluarsaException(pm.getKode(), pm.getNama(), pm.getTanggalKadaluarsa());
            }
            double diskonExp = pm.hitungDiskonKedaluwarsa(clock);
            // Sinergi Diskon: Memilih diskon tertinggi antara diskon promo manual vs diskon kedaluwarsa
            diskonPromo = Math.max(diskonPromo, diskonExp);
        }
        tambahItem(produk, jumlahBeli, diskonPromo);
    }

    /**
     * Menambahkan item belanjaan dengan diskon promosi per baris barang eksplisit.
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

    /**
     * Mengubah kuantitas item pada keranjang belanja aktif.
     * Menerapkan prinsip Atomic Invariant:
     * - Menghitung selisih kuantitas (delta = jumlahBaru - qtyLama).
     * - Jika delta > 0, kurangi stok fisik dari produk (memverifikasi ketersediaan stok).
     * - Jika delta < 0, kembalikan stok fisik ke objek produk.
     * - Mutasi kuantitas item dilakukan HANYA jika operasi stok berhasil tanpa exception.
     * 
     * @param index      indeks baris item belanja (0-indexed)
     * @param jumlahBaru kuantitas baru yang diminta (> 0)
     * @throws KasirException jika status transaksi bukan PENDING atau stok produk tidak mencukupi
     * @throws IllegalArgumentException jika jumlahBaru <= 0
     * @throws IndexOutOfBoundsException jika index tidak valid
     */
    public void ubahKuantitasItem(int index, int jumlahBaru) throws KasirException {
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "ubahKuantitasItem");
        }
        if (index < 0 || index >= daftarItem.size()) {
            throw new IndexOutOfBoundsException(String.format(
                "[ERROR VALIDASI] Indeks item tidak valid! (Indeks: %d, Jumlah Item: %d)",
                index, daftarItem.size()
            ));
        }
        if (jumlahBaru <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Jumlah beli baru harus lebih besar dari 0! (Diberikan: " + jumlahBaru + ")");
        }

        ItemTransaksi item = daftarItem.get(index);
        int qtyLama = item.getJumlahBeli();
        if (jumlahBaru == qtyLama) {
            return;
        }

        int delta = jumlahBaru - qtyLama;
        Produk p = item.getProduk();
        if (delta > 0) {
            // Memerlukan pengurangan stok tambahan (fail-fast jika stok kurang)
            p.kurangiStok(delta);
        } else {
            // Mengurangi kuantitas di keranjang -> pulihkan stok ke produk
            p.tambahStok(-delta);
        }

        item.ubahJumlahBeli(jumlahBaru);
        System.out.println("[INFO] Kuantitas item " + p.getNama() + " berhasil diubah dari " + qtyLama + " ke " + jumlahBaru + " unit.");
    }

    // --- METODE KALKULASI FINANSIAL TRANSAKSI ---

    /**
     * Membatalkan seluruh transaksi yang sedang berlangsung.
     * Mengembalikan alokasi stok seluruh item di keranjang belanja ke objek produk masing-masing.
     * 
     * @throws KasirException jika status transaksi bukan PENDING
     */
    public void batalkanTransaksi() throws KasirException {
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "batalkanTransaksi");
        }
        for (ItemTransaksi item : this.daftarItem) {
            item.getProduk().tambahStok(item.getJumlahBeli());
        }
        this.daftarItem.clear();
        this.statusTransaksi = "DIBATALKAN";
    }

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
     * Memproses checkout transaksi dengan pembayaran tunai langsung.
     * 
     * @param uangDiterima nominal uang tunai yang diserahkan konsumen
     * @return true jika pembayaran sukses dan lunas
     * @throws KasirException jika terjadi pelanggaran aturan kasir
     * @throws IllegalArgumentException jika nominal uang diserahkan <= 0
     */
    public boolean prosesTransaksi(double uangDiterima) throws KasirException {
        PembayaranTunai strategiTunai = new PembayaranTunai();
        return prosesTransaksi(strategiTunai, uangDiterima);
    }

    /**
     * Memproses checkout transaksi menggunakan pola Strategy (MetodePembayaran).
     * Mendukung PembayaranTunai, PembayaranQrisSimulasi, SplitPayment, atau custom strategy lainnya.
     * 
     * @param metode   strategi pembayaran yang digunakan
     * @param nominal  nominal uang masuk/otorisasi
     * @return true jika transaksi berhasil lunas
     * @throws KasirException jika keranjang kosong, uang kurang, atau status bukan PENDING
     */
    public boolean prosesTransaksi(MetodePembayaran metode, double nominal) throws KasirException {
        // 1. Validasi Status Transaksi
        if (!"PENDING".equals(this.statusTransaksi)) {
            throw new StatusTransaksiInvalidException(this.idTransaksi, this.statusTransaksi, "prosesTransaksi");
        }

        // 2. Validasi Keranjang Belanja Kosong
        if (daftarItem.isEmpty()) {
            throw new TransaksiKosongException(this.idTransaksi);
        }

        // 3. Validasi Strategi
        if (metode == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Metode pembayaran tidak boleh null!");
        }

        double totalTagihan = hitungTotalBayar();

        // 4. Delegasi pemrosesan ke Strategy (Melempar PembayaranKurangException jika tidak cukup)
        metode.bayar(totalTagihan, nominal);

        // 5. Pembayaran Berhasil: Mutasi State ke "LUNAS" & Simpan Strategy
        this.statusTransaksi = "LUNAS";
        this.metodePembayaranTerpilih = metode;

        cetakHeaderStruk();
        cetakDaftarBelanja();
        cetakFooterStruk();

        System.out.println("METODE PEMBAYARAN: " + metode.getNamaMetode());
        System.out.println("Rincian   : " + metode.getInformasiTransaksi());
        System.out.println("======================================================================");
        System.out.println("                 Status Pembayaran: LUNAS                             ");
        System.out.println("======================================================================");
        System.out.println("                 Terima Kasih Atas Kunjungan Anda!                    ");
        System.out.println("======================================================================");
        return true;
    }

    // --- HELPER METHODS UNTUK FORMATTING STRUK ---

    public String generateTeksStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                         STRUK PENJUALAN TOKO                         \n");
        sb.append("======================================================================\n");
        sb.append("No. Transaksi : ").append(idTransaksi).append("\n");
        sb.append("Tanggal       : ").append(tanggal).append("\n");
        if (pelanggan != null) {
            sb.append("Pelanggan     : ").append(pelanggan.getNama()).append(" (MEMBER-").append(pelanggan.getTipeMember()).append(")\n");
        } else {
            sb.append("Pelanggan     : UMUM (Non-Member)\n");
        }
        sb.append("Status        : ").append(statusTransaksi).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("DAFTAR BELANJAAN:\n");
        int no = 1;
        for (ItemTransaksi it : daftarItem) {
            Produk p = it.getProduk();
            String disc = (it.getDiskonItem() > 0.0) ? String.format(" [Disc %.0f%%]", it.getDiskonItem() * 100) : "";
            sb.append(String.format("%d. %s - %s\n", no++, it.getNamaProduk(), disc));
            sb.append(String.format("   %d x Rp %,d = Rp %,d\n", it.getJumlahBeli(), it.getHargaSatuan(), it.hitungSubtotal()));
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format("Total Subtotal                             : Rp %,d\n", hitungTotalSubtotal()));
        if (pelanggan != null && pelanggan.getDiskon() > 0.0) {
            sb.append(String.format("Diskon Member (%s: %.0f%%)                  : Rp %,.0f\n",
                    pelanggan.getTipeMember(), pelanggan.getDiskon() * 100, hitungDiskonNominal()));
        }
        sb.append("======================================================================\n");
        sb.append(String.format("TOTAL AKHIR                                : Rp %,.0f\n", hitungTotalBayar()));
        if (metodePembayaranTerpilih != null) {
            sb.append("Pembayaran    : ").append(metodePembayaranTerpilih.getInformasiTransaksi()).append("\n");
        }
        sb.append("======================================================================\n");
        sb.append("                 Status: ").append(statusTransaksi).append("\n");
        sb.append("                 Terima Kasih Atas Kunjungan Anda!                    \n");
        sb.append("======================================================================\n");
        return sb.toString();
    }

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
                    item.getHargaSatuan(),
                    discText,
                    padSpaces(26 - String.format("%d x Rp %,d%s", item.getJumlahBeli(), item.getHargaSatuan(), discText).length()),
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

    /**
     * Memeriksa apakah produk dengan kode tertentu ada di keranjang transaksi yang masih PENDING.
     * Jika transaksi sudah LUNAS atau DIBATALKAN, mengembalikan false.
     * 
     * @param kodeProduk kode produk yang dicari
     * @return true jika produk ada dalam keranjang transaksi aktif
     */
    public boolean adaProdukAktif(String kodeProduk) {
        if (kodeProduk == null || !"PENDING".equals(statusTransaksi)) {
            return false;
        }
        for (ItemTransaksi item : daftarItem) {
            if (item.getProduk().getKode().equalsIgnoreCase(kodeProduk.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Menghitung total kuantitas produk tertentu yang sedang aktif di keranjang PENDING.
     * 
     * @param kodeProduk kode produk
     * @return total kuantitas jika PENDING, atau 0 jika tidak ada / bukan PENDING
     */
    public int getKuantitasItemAktif(String kodeProduk) {
        if (kodeProduk == null || !"PENDING".equals(statusTransaksi)) {
            return 0;
        }
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            if (item.getProduk().getKode().equalsIgnoreCase(kodeProduk.trim())) {
                total += item.getJumlahBeli();
            }
        }
        return total;
    }

    /**
     * Mendapatkan peta reservasi stok (kode produk -> kuantitas di keranjang)
     * HANYA jika status transaksi saat ini adalah PENDING.
     * Jika status transaksi LUNAS atau DIBATALKAN, mengembalikan peta kosong.
     * 
     * @return Map berisi pasangan kode produk dan jumlah beli
     */
    public java.util.Map<String, Integer> getReservasiStokMap() {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        if ("PENDING".equals(statusTransaksi)) {
            for (ItemTransaksi item : daftarItem) {
                String k = item.getProduk().getKode();
                map.put(k, map.getOrDefault(k, 0) + item.getJumlahBeli());
            }
        }
        return map;
    }
}
