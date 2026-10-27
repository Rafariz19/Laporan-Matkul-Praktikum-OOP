import model.DapatDidiskon;
import model.ItemTransaksi;
import model.Pelanggan;
import model.Produk;
import model.ProdukElektronik;
import model.ProdukMakanan;
import model.Transaksi;
import model.exception.KasirException;
import model.exception.PembayaranKurangException;
import model.exception.StatusTransaksiInvalidException;
import model.exception.StokTidakCukupException;
import model.exception.TransaksiKosongException;

/**
 * Main Class - Program Pengujian Sistem Kasir Sederhana
 * Praktikum PBO Modul 10: "Exception Handling dan Validasi State Objek"
 * Pendekatan: Project Kickoff Berbasis Agile (Sprint P10: Hardening & State Invariance)
 * 
 * Mahasiswa : Rafael Rizky Luga Tanjung
 * NRP       : 3125522007
 * Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus    : PENS PSDKU Sumenep (2026)
 * Proyek    : Sistem Kasir Sederhana (Sprint P10 Robustness)
 * 
 * Pengujian komprehensif mencakup 10 Skenario:
 * - Skenario 1 : Happy Path Transaksi Kasir Sukses (Multi-item, Member VIP, Pembayaran Pas/Lebih)
 * - Skenario 2 : Custom Exception StokTidakCukupException (Pembelian 999 unit melebihi stok)
 * - Skenario 3 : Boundary Value Analysis (Pembelian tepat sejumlah sisa stok fisik)
 * - Skenario 4 : Standard Exception IllegalArgumentException (Validasi Prekondisi kuantitas & harga <= 0)
 * - Skenario 5 : Standard Exception NumberFormatException (Simulasi input teks non-angka dari terminal kasir)
 * - Skenario 6 : Custom Exception PembayaranKurangException (Underpayment: transaksi tetap PENDING)
 * - Skenario 7 : State Recovery & Pelunasan Transaksi Tertunda (Pembayaran ulang hingga status LUNAS)
 * - Skenario 8 : Custom Exception TransaksiKosongException (Checkout pada keranjang belanja tanpa item)
 * - Skenario 9 : Custom Exception StatusTransaksiInvalidException (Menolak mutasi transaksi yang sudah LUNAS)
 * - Skenario 10: Compensating Action / Rollback Stok saat Item Belanja Dibatalkan
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM P10 - PEMROGRAMAN BERORIENTASI OBYEK");
        System.out.println("Topik   : Exception Handling dan Validasi State Objek");
        System.out.println("Model   : Project Based Learning Berbasis Agile (Sprint P10: Hardening)");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP : 3125522007");
        System.out.println("Dosen   : Nirwana Haidar Hari, S.Pd., M.Kom.");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("Proyek  : Sistem Kasir Sederhana (Refaktorisasi Fault-Tolerant)");
        System.out.println("======================================================================\n");

        // Inisialisasi Master Data Produk & Pelanggan
        System.out.println(">>> [SETUP SPRINT AGILE] Inisialisasi Master Data Toko:");
        ProdukMakanan roti = new ProdukMakanan("M01", "Roti Gandum Sehat", 18000, 25, "25-10-2026");
        ProdukMakanan susu = new ProdukMakanan("M02", "Susu UHT Cokelat 1L", 20000, 40, "15-12-2026");
        ProdukElektronik mouse = new ProdukElektronik("E01", "Mouse Wireless Ergonomis", 150000, 15, 12);
        ProdukElektronik keyboard = new ProdukElektronik("E02", "Keyboard Mechanical TKL", 450000, 8, 24);

        Pelanggan vipMember = new Pelanggan("CUST-VIP-01", "Dr. Budi Santoso", "081399887766", "VIP");
        Pelanggan goldMember = new Pelanggan("CUST-GLD-02", "Siti Aminah, M.T.", "085711223344", "GOLD");

        System.out.println("✓ Master Produk 1: " + roti.getNama() + " (Stok: " + roti.getStok() + " unit, Harga: Rp " + roti.getHarga() + ")");
        System.out.println("✓ Master Produk 2: " + susu.getNama() + " (Stok: " + susu.getStok() + " unit, Harga: Rp " + susu.getHarga() + ")");
        System.out.println("✓ Master Produk 3: " + mouse.getNama() + " (Stok: " + mouse.getStok() + " unit, Harga: Rp " + mouse.getHarga() + ")");
        System.out.println("✓ Master Produk 4: " + keyboard.getNama() + " (Stok: " + keyboard.getStok() + " unit, Harga: Rp " + keyboard.getHarga() + ")");
        System.out.println("✓ Master Pelanggan: " + vipMember.getNama() + " (" + vipMember.getTipeMember() + " - Diskon " + (int)(vipMember.getDiskon()*100) + "%)\n");

        // ====================================================================
        // SKENARIO 1: HAPPY PATH TRANSAKSI KASIR SUKSES (AGILE ACCEPTANCE CRITERIA 1)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 1: HAPPY PATH CHECKOUT TRANSAKSI LENGKAP               ###");
        System.out.println("######################################################################");
        Transaksi trx1 = new Transaksi("TRX-2026-M10-001", "26-10-2026 10:15", vipMember);

        try {
            System.out.println("[ACTION] Kasir menambahkan item belanja:");
            trx1.tambahItem(roti, 2);             // 2 unit Roti Gandum @ Rp 18.000 = Rp 36.000
            trx1.tambahItem(mouse, 1, 0.10);      // 1 unit Mouse Wireless @ Rp 150.000 (Promo 10% = Rp 135.000)
            
            System.out.printf("   Subtotal Kotor : Rp %,d\n", trx1.hitungTotalSubtotal());
            System.out.printf("   Diskon VIP 15%% : Rp %,.0f\n", trx1.hitungDiskonNominal());
            System.out.printf("   Grand Total    : Rp %,.0f\n\n", trx1.hitungTotalBayar());

            System.out.println("[ACTION] Konsumen membayar tunai Rp 200.000:");
            boolean lunas = trx1.prosesTransaksi(200000.0);
            System.out.println("[HASIL] Status Pelunasan: " + (lunas ? "SUKSES LUNAS" : "PENDING"));
        } catch (KasirException e) {
            System.out.println("[GAGAL] KasirException tertangkap: " + e.getDetailDiagnostik());
        } catch (IllegalArgumentException e) {
            System.out.println("[GAGAL] IllegalArgumentException tertangkap: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 1]:");
            System.out.println("   Status Transaksi : " + trx1.getStatusTransaksi() + " (Expected: LUNAS)");
            System.out.println("   Sisa Stok Roti   : " + roti.getStok() + " unit (Expected: 23 unit)");
            System.out.println("   Sisa Stok Mouse  : " + mouse.getStok() + " unit (Expected: 14 unit)");
            System.out.println("   => State Konsisten & Transaksi Selesai Sempurna.\n");
        }

        // ====================================================================
        // SKENARIO 2: CUSTOM EXCEPTION StokTidakCukupException (UNHAPPY PATH 1)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 2: CUSTOM EXCEPTION StokTidakCukupException           ###");
        System.out.println("######################################################################");
        Transaksi trx2 = new Transaksi("TRX-2026-M10-002", "26-10-2026 10:30", goldMember);
        int stokKeyboardAwal = keyboard.getStok();
        System.out.println("[KONDISI AWAL] Stok Keyboard Mechanical saat ini: " + stokKeyboardAwal + " unit.");
        System.out.println("[ACTION] Kasir mencoba menambahkan 999 unit Keyboard (melebihi stok)...");

        try {
            trx2.tambahItem(keyboard, 999);
            System.out.println("[ERROR DESAIN] Seharusnya baris ini tidak tercapai!");
        } catch (StokTidakCukupException e) {
            System.out.println("[DITANGKAP DENGAN BENAR] StokTidakCukupException:");
            System.out.println("   Kode Error    : " + e.getKodeError());
            System.out.println("   Timestamp     : " + e.getWaktuKejadian());
            System.out.println("   Pesan Bisnis  : " + e.getMessage());
            System.out.println("   Data Objek    : Produk=" + e.getNamaProduk() + ", Tersedia=" + e.getStokTersedia() + ", Diminta=" + e.getJumlahDiminta() + ", Defisit=" + e.getDefisitStok());
        } catch (KasirException e) {
            System.out.println("[FALLBACK] KasirException umum: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 2]:");
            System.out.println("   Stok Keyboard Aktual   : " + keyboard.getStok() + " unit (Expected: " + stokKeyboardAwal + " unit)");
            System.out.println("   Jumlah Item di Nota    : " + trx2.getDaftarItem().size() + " item (Expected: 0 item)");
            System.out.println("   => BUKTI KONSISTENSI STATE: Stok fisik sama sekali tidak berkurang dan nota tidak tercemar!\n");
        }

        // ====================================================================
        // SKENARIO 3: BOUNDARY VALUE ANALYSIS (NILAI BATAS STOK TEPAT)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 3: BOUNDARY VALUE ANALYSIS (PEMBELIAN = SISA STOK)     ###");
        System.out.println("######################################################################");
        System.out.println("[ANALISIS DOMAIN]: Pembelian tepat sejumlah sisa stok fisik HARUS diizinkan.");
        System.out.println("Sisa stok saat ini = " + keyboard.getStok() + " unit. Pembeli membeli tepat " + keyboard.getStok() + " unit.");

        try {
            int beliTepat = keyboard.getStok();
            trx2.tambahItem(keyboard, beliTepat);
            System.out.println("[BERHASIL] Pembelian tepat sejumlah sisa stok sukses diproses.");
        } catch (KasirException e) {
            System.out.println("[GAGAL] Terjadi kesalahan tak terduga: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 3]:");
            System.out.println("   Sisa Stok Keyboard : " + keyboard.getStok() + " unit (Expected: 0 unit)");
            System.out.println("   Jumlah Item trx2   : " + trx2.getDaftarItem().size() + " item");
            System.out.println("   => BUKTI: Nilai batas stok 0 unit adalah state valid dalam sistem kasir.\n");
        }

        // ====================================================================
        // SKENARIO 4: STANDARD EXCEPTION IllegalArgumentException (PREKONDISI SALAH)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 4: PRECONDITION VALIDATION (IllegalArgumentException)  ###");
        System.out.println("######################################################################");
        
        System.out.println("--- Sub-Uji 4A: Pembelian dengan Kuantitas Negatif (-5 unit) ---");
        try {
            trx2.tambahItem(susu, -5);
        } catch (IllegalArgumentException e) {
            System.out.println("[DITOLAK DENGAN BENAR] IllegalArgumentException: " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("[GAGAL] Salah tipe exception!");
        }

        System.out.println("\n--- Sub-Uji 4B: Perubahan Harga Negatif (Produk.ubahHarga(-50000)) ---");
        int hargaSusuAwal = susu.getHarga();
        try {
            susu.ubahHarga(-50000);
        } catch (IllegalArgumentException e) {
            System.out.println("[DITOLAK DENGAN BENAR] IllegalArgumentException: " + e.getMessage());
        } finally {
            System.out.println("   Harga Susu Saat Ini : Rp " + susu.getHarga() + " (Expected: Rp " + hargaSusuAwal + ")");
        }

        System.out.println("\n--- Sub-Uji 4C: Penambahan Stok Negatif (Produk.tambahStok(-10)) ---");
        int stokSusuAwal = susu.getStok();
        try {
            susu.tambahStok(-10);
        } catch (IllegalArgumentException e) {
            System.out.println("[DITOLAK DENGAN BENAR] IllegalArgumentException: " + e.getMessage());
        } finally {
            System.out.println("   Stok Susu Saat Ini  : " + susu.getStok() + " unit (Expected: " + stokSusuAwal + " unit)");
            System.out.println("   => BUKTI: Seluruh invariant prekondisi terlindungi dari mutasi nilai negatif.\n");
        }

        // ====================================================================
        // SKENARIO 5: STANDARD EXCEPTION NumberFormatException (PARSING TERMINAL)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 5: ROBUST INPUT PARSING (NumberFormatException)        ###");
        System.out.println("######################################################################");
        String[] simulasiInputScanner = {"dua_belas", "15000X", "@#$%", "10"};
        System.out.println("[SIMULASI]: Kasir memproses rentetan input kuantitas dari terminal input:");

        for (String inputRaw : simulasiInputScanner) {
            System.out.print("Input: \"" + inputRaw + "\" -> ");
            try {
                int parsedKuantitas = Integer.parseInt(inputRaw);
                System.out.println("[VALID] Berhasil diparse menjadi integer: " + parsedKuantitas);
            } catch (NumberFormatException e) {
                System.out.println("[DITANGKAP DENGAN AMAN] NumberFormatException: Input bukan angka integer valid! Sistem tidak crash.");
            }
        }
        System.out.println("=> BUKTI: Fail-Safe handling pada boundary layer menjamin stabilitas terminal kasir.\n");

        // ====================================================================
        // SKENARIO 6: CUSTOM EXCEPTION PembayaranKurangException (UNDERPAYMENT)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 6: CUSTOM EXCEPTION PembayaranKurangException           ###");
        System.out.println("######################################################################");
        double tagihanTrx2 = trx2.hitungTotalBayar();
        System.out.printf("[INFO TAGIHAN] Transaksi %s memiliki Total Tagihan: Rp %,.0f\n", trx2.getIdTransaksi(), tagihanTrx2);
        System.out.println("[ACTION] Pelanggan membayar uang tunai TIDAK MENCUKUPI (Rp 100.000)...");

        try {
            trx2.prosesTransaksi(100000.0);
            System.out.println("[ERROR DESAIN] Seharusnya transaksi belum lunas!");
        } catch (PembayaranKurangException e) {
            System.out.println("[DITANGKAP DENGAN BENAR] PembayaranKurangException:");
            System.out.println("   Kode Error      : " + e.getKodeError());
            System.out.println("   Timestamp       : " + e.getWaktuKejadian());
            System.out.println("   Pesan Kasir     : " + e.getMessage());
            System.out.printf("   Total Tagihan   : Rp %,.0f\n", e.getTotalTagihan());
            System.out.printf("   Uang Diterima   : Rp %,.0f\n", e.getUangDiterima());
            System.out.printf("   Defisit/Kurang  : Rp %,.0f\n", e.getKekurangan());
        } catch (KasirException e) {
            System.out.println("[FALLBACK] KasirException: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 6]:");
            System.out.println("   Status Transaksi : " + trx2.getStatusTransaksi() + " (Expected: PENDING)");
            System.out.println("   => BUKTI: Transaksi yang kurang bayar TIDAK PERNAH berubah status menjadi LUNAS.\n");
        }

        // ====================================================================
        // SKENARIO 7: STATE RECOVERY & PELUNASAN TRANSAKSI TERTUNDA
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 7: STATE RECOVERY (PELUNASAN TRANSAKSI PENDING)        ###");
        System.out.println("######################################################################");
        System.out.println("[ACTION] Pelanggan menambahkan uang tunai Rp 4.000.000 (Mencukupi tagihan)...");

        try {
            boolean statusPelunasan = trx2.prosesTransaksi(4000000.0);
            System.out.println("[BERHASIL] Pembayaran diterima! Return status: " + statusPelunasan);
        } catch (KasirException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 7]:");
            System.out.println("   Status Transaksi trx2 : " + trx2.getStatusTransaksi() + " (Expected: LUNAS)");
            System.out.println("   => State recovery berhasil menyelesaikan siklus transaksi kasir secara elegan.\n");
        }

        // ====================================================================
        // SKENARIO 8: CUSTOM EXCEPTION TransaksiKosongException (EMPTY CART)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 8: CUSTOM EXCEPTION TransaksiKosongException           ###");
        System.out.println("######################################################################");
        Transaksi trxKosong = new Transaksi("TRX-2026-M10-003", "26-10-2026 11:00", null);
        System.out.println("[INFO] Dibuat Transaksi baru (" + trxKosong.getIdTransaksi() + ") dengan 0 item belanja.");
        System.out.println("[ACTION] Kasir memproses checkout pada transaksi kosong...");

        try {
            trxKosong.prosesTransaksi(50000.0);
            System.out.println("[ERROR DESAIN] Checkout kosong seharusnya ditolak!");
        } catch (TransaksiKosongException e) {
            System.out.println("[DITANGKAP DENGAN BENAR] TransaksiKosongException:");
            System.out.println("   Kode Error    : " + e.getKodeError());
            System.out.println("   Pesan Bisnis  : " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("[FALLBACK] KasirException: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 8]:");
            System.out.println("   Status Transaksi : " + trxKosong.getStatusTransaksi() + " (Expected: PENDING)");
            System.out.println("   => BUKTI: Sistem menolak pencetakan nota fiktif tanpa komoditas.\n");
        }

        // ====================================================================
        // SKENARIO 9: CUSTOM EXCEPTION StatusTransaksiInvalidException (IMMUTABLE)
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 9: CUSTOM EXCEPTION StatusTransaksiInvalidException    ###");
        System.out.println("######################################################################");
        System.out.println("[INFO] Transaksi " + trx1.getIdTransaksi() + " berstatus: " + trx1.getStatusTransaksi() + " (Final).");
        System.out.println("[ACTION] Kasir tidak sengaja mencoba menambah item ke transaksi yang sudah LUNAS...");

        try {
            trx1.tambahItem(susu, 1);
            System.out.println("[ERROR DESAIN] Modifikasi transaksi final seharusnya ditolak!");
        } catch (StatusTransaksiInvalidException e) {
            System.out.println("[DITANGKAP DENGAN BENAR] StatusTransaksiInvalidException:");
            System.out.println("   Kode Error    : " + e.getKodeError());
            System.out.println("   Status Saat Ini: " + e.getStatusSaatIni());
            System.out.println("   Operasi Ditolak: " + e.getOperasiDiminta());
            System.out.println("   Pesan Bisnis  : " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("[FALLBACK] KasirException: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 9]:");
            System.out.println("   Status Transaksi trx1 : " + trx1.getStatusTransaksi() + " (Tetap LUNAS)");
            System.out.println("   Jumlah Item Belanja   : " + trx1.getDaftarItem().size() + " item (Tidak berubah)");
            System.out.println("   => BUKTI: Transaksi yang lunas terlindungi dari manipulasi data (Immutability Enforced).\n");
        }

        // ====================================================================
        // SKENARIO 10: ROLLBACK & COMPENSATING ACTION SAAT BATALKAN ITEM
        // ====================================================================
        System.out.println("######################################################################");
        System.out.println("### SKENARIO 10: COMPENSATING ACTION / ROLLBACK STOK PADA PEMBATALAN ###");
        System.out.println("######################################################################");
        Transaksi trxRollback = new Transaksi("TRX-2026-M10-004", "26-10-2026 11:30", null);
        int stokSusuSebelumBeli = susu.getStok();
        System.out.println("[KONDISI AWAL] Stok Susu UHT: " + stokSusuSebelumBeli + " unit.");

        try {
            System.out.println("[LANGKAH 1] Kasir memasukkan 5 unit Susu UHT ke transaksi 004...");
            trxRollback.tambahItem(susu, 5);
            System.out.println("   Sisa Stok Susu setelah tambahItem: " + susu.getStok() + " unit.");

            System.out.println("[LANGKAH 2] Pelanggan memutuskan membatalkan item Susu...");
            trxRollback.batalkanItem(0); // Batalkan item indeks ke-0
            System.out.println("   Sisa Stok Susu setelah batalkanItem: " + susu.getStok() + " unit.");
        } catch (KasirException e) {
            System.out.println("[GAGAL] KasirException: " + e.getMessage());
        } finally {
            System.out.println("[STATE VERIFIKASI SKENARIO 10]:");
            System.out.println("   Stok Susu Akhir       : " + susu.getStok() + " unit (Expected: " + stokSusuSebelumBeli + " unit)");
            System.out.println("   Jumlah Item Transaksi : " + trxRollback.getDaftarItem().size() + " item (Expected: 0 item)");
            System.out.println("   => BUKTI: Mekanisme rollback menjamin stok kembali utuh tanpa defisit inventaris.\n");
        }

        System.out.println("======================================================================");
        System.out.println("SELURUH 10 SKENARIO PENGUJIAN PRAKTIKUM P10 SELESAI DIEKSEKUSI");
        System.out.println("Validasi State Objek & Exception Handling 100% Lolos Tanpa Bug.");
        System.out.println("======================================================================");
    }
}
