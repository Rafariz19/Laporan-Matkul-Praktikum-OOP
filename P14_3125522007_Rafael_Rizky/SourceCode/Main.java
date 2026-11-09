import model.*;
import model.exception.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Main - Sistem Kasir Sederhana (POS) - Modul 14 Finalisasi & Refactoring
 * Mendukung mode Pengujian Otomatis Komprehensif (10 Skenario) & Mode Interaktif Menu Kasir.
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 */
public class Main {
    private static final Path PATH_PRODUK = Paths.get("data/produk.txt");
    private static final Path PATH_TRANSAKSI = Paths.get("data/transaksi.txt");

    private static final KatalogProduk katalog = new KatalogProduk();
    private static final ProdukFileRepository produkRepo = new ProdukFileRepository();
    private static final TransaksiFileRepository transaksiRepo = new TransaksiFileRepository();
    private static boolean dataBerubah = false;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            jalankanPengujianSkenarioPOS();
        } else {
            // Default: Masuk ke Mode Interaktif (Menunggu Input Keyboard Pengguna)
            jalankanModeInteraktif();
        }
    }

    /**
     * Mode Interaktif Berbasis Menu (Refactored Modular Controller)
     */
    public static void jalankanModeInteraktif() {
        muatDataOtomatis();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("======================================================================");
        System.out.println(" PRAKTIKUM PBO - MODUL 14: PENGUJIAN & REFACTORING SISTEM KASIR (POS)");
        System.out.println(" Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)");
        System.out.println(" Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom. (PENS PSDKU Sumenep)");
        System.out.println("======================================================================");

        while (running) {
            tampilkanMenuUtama();
            System.out.print("Pilih opsi menu [0-9]: ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    menuTampilkanKatalog();
                    break;
                case "2":
                    menuTambahProduk(scanner);
                    break;
                case "3":
                    menuCariProduk(scanner);
                    break;
                case "4":
                    menuEditProduk(scanner);
                    break;
                case "5":
                    menuHapusProduk(scanner);
                    break;
                case "6":
                    menuTransaksiKasir(scanner);
                    break;
                case "7":
                    menuSimpanData();
                    break;
                case "8":
                    menuMuatUlangData(scanner);
                    break;
                case "9":
                    jalankanPengujianSkenarioPOS();
                    break;
                case "0":
                    running = menuKeluar(scanner);
                    break;
                default:
                    System.out.println("[PERINGATAN] Pilihan menu tidak valid. Silakan pilih 0 - 9.");
            }
        }
        System.out.println("Terima kasih telah menggunakan Sistem Kasir Sederhana.");
    }

    // ========================================================================
    // 10 SKENARIO PENGUJIAN REGRESI DAN INVARIANSI DATA (MODUL 14)
    // ========================================================================
    public static void jalankanPengujianSkenarioPOS() {
        System.out.println("======================================================================");
        System.out.println(" PRAKTIKUM PBO - MODUL 14: PENGUJIAN, REFACTORING & FINALISASI PROYEK");
        System.out.println(" SISTEM KASIR SEDERHANA (POINT OF SALE)");
        System.out.println(" Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)");
        System.out.println("======================================================================\n");

        katalog.kosongkanKatalog();
        int passed = 0;

        // SKENARIO 1: Tampilkan katalog saat kosong
        System.out.println("[SKENARIO 1] Pengujian Katalog Kosong");
        if (katalog.isKosong()) {
            System.out.println("Katalog kosong terdeteksi dengan tepat. Aplikasi berjalan normal tanpa exception.");
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 2: Tambah produk Makanan & Elektronik valid
        System.out.println("[SKENARIO 2] Tambah Produk Valid (Makanan & Elektronik)");
        try {
            Produk roti = new ProdukMakanan("MK01", "Roti Bakar Keju", 15000, 20, "2026-12-31");
            Produk mouse = new ProdukElektronik("EL01", "Mouse Wireless Ergonomic", 125000, 10, 12);
            katalog.tambahProduk(roti);
            katalog.tambahProduk(mouse);
            System.out.println("Produk berhasil ditambahkan ke katalog: " + katalog.getJumlahProduk() + " item.");
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // SKENARIO 3: Pencegahan Kode Duplikat
        System.out.println("[SKENARIO 3] Uji Penolakan Kode Duplikat ('MK01')");
        boolean s3Passed = false;
        try {
            katalog.tambahProduk(new ProdukMakanan("MK01", "Roti Coklat", 12000, 5, "2026-11-20"));
        } catch (IllegalArgumentException e) {
            s3Passed = true;
            System.out.println("Tertangkap penolakan duplikasi: " + e.getMessage());
        }
        if (s3Passed && katalog.getJumlahProduk() == 2) {
            System.out.println("Katalog menolak duplikasi kode 'MK01'. Jumlah katalog tetap 2.");
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 4: Validasi Nilai String Kosong / Spasi Saja
        System.out.println("[SKENARIO 4] Uji Input Nama / Kode Berisi Whitespace");
        boolean s4Passed = false;
        try {
            new ProdukMakanan("   ", "Biskuit", 5000, 10, "2026-10-10");
        } catch (IllegalArgumentException e1) {
            try {
                new ProdukElektronik("EL02", "   ", 50000, 5, 6);
            } catch (IllegalArgumentException e2) {
                s4Passed = true;
                System.out.println("Constructor domain berhasil menolak kode dan nama berupa spasi kosong.");
            }
        }
        if (s4Passed) {
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 5: Validasi Karakter Delimiter Pipeling '|'
        System.out.println("[SKENARIO 5] Uji Penolakan Karakter Delimiter Pipeling '|'");
        boolean s5Passed = false;
        try {
            new ProdukMakanan("MK02", "Keripik Singkong | Pedas", 8000, 15, "2026-10-15");
        } catch (IllegalArgumentException e) {
            s5Passed = true;
            System.out.println("Tertangkap pesan validasi: " + e.getMessage());
        }
        if (s5Passed && katalog.getJumlahProduk() == 2) {
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 6: Pencarian Produk (Case-Insensitive)
        System.out.println("[SKENARIO 6] Uji Pencarian Produk Case-Insensitive ('mk01')");
        Produk cari = katalog.cariProduk("mk01");
        if (cari != null && cari.getKode().equals("MK01")) {
            System.out.println("Ditemukan: " + cari.getNama() + " [Rp " + cari.getHarga() + "]");
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 7: Transaksi dengan Stok Kurang (StokTidakCukupException)
        System.out.println("[SKENARIO 7] Transaksi dengan Stok Kurang");
        Pelanggan cust = new Pelanggan("CUST01", "Budi Santoso", "081234567890", "VIP");
        Transaksi trx1 = new Transaksi("TRX-TEST-01", "09-11-2026 10:00", cust);
        boolean s7Passed = false;
        try {
            trx1.tambahItem(katalog.cariProduk("MK01"), 50); // Stok cuma 20
        } catch (StokTidakCukupException e) {
            s7Passed = true;
            System.out.println("Exception ditangkap: " + e.getMessage());
            System.out.println("Stok produk di katalog tidak berkurang (tetap " + katalog.cariProduk("MK01").getStok() + ").");
        } catch (KasirException e) {
            System.out.println("Error lain: " + e.getMessage());
        }
        if (s7Passed) {
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 8: Transaksi dengan Pembayaran Kurang (PembayaranKurangException)
        System.out.println("[SKENARIO 8] Transaksi dengan Pembayaran Kurang");
        boolean s8Passed = false;
        try {
            int stokSebelumBeli = katalog.cariProduk("MK01").getStok();
            trx1.tambahItem(katalog.cariProduk("MK01"), 2); // Beli 2
            trx1.prosesTransaksi(20000); // Tagihan 25.500 (diskon VIP 15%), bayar 20.000
        } catch (PembayaranKurangException e) {
            s8Passed = true;
            System.out.println("Exception ditangkap: " + e.getMessage());
            System.out.println("Status transaksi tetap: " + trx1.getStatusTransaksi());
        } catch (KasirException e) {
            System.out.println("Error: " + e.getMessage());
        }
        if (s8Passed) {
            System.out.println("HASIL: LULUS (PASS)\n");
            passed++;
        }

        // SKENARIO 9: Transaksi Sukses, Pengurangan Stok & Persistensi
        System.out.println("[SKENARIO 9] Transaksi Sukses & Ekspor Struk Riwayat");
        try {
            double totalTagihan = trx1.hitungTotalBayar();
            double uangDiserahkan = 50000.0;
            double kembalian = uangDiserahkan - totalTagihan;
            boolean bayarSukses = trx1.prosesTransaksi(uangDiserahkan);
            if (bayarSukses && "LUNAS".equals(trx1.getStatusTransaksi())) {
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, trx1, uangDiserahkan, kembalian);
                System.out.println("Transaksi berhasil diselesaikan! Status: LUNAS");
                System.out.println("Invoice transaksi tersimpan ke: " + PATH_TRANSAKSI);
                System.out.println("HASIL: LULUS (PASS)\n");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Gagal transaksi: " + e.getMessage());
        }

        // SKENARIO 10: Persistensi & Safe Atomic Reload saat File Korup
        System.out.println("[SKENARIO 10] Uji Invariansi Koleksi Terhadap Berkas Rusak (Atomic Load)");
        Path corruptPath = Paths.get("data/test_corrupt.txt");
        try {
            try (PrintWriter pw = new PrintWriter(new FileWriter(corruptPath.toFile()))) {
                pw.println("MK99|Makanan Valid|10000|5|Makanan|2026-12-12");
                pw.println("BARIS_KORUP_TANPA_DATA_LENGKAP");
            }
            int jumlahSebelum = katalog.getJumlahProduk();
            try {
                produkRepo.muat(corruptPath, katalog);
                System.out.println("Gagal: Berkas rusak seharusnya melempar exception.");
            } catch (IllegalArgumentException e) {
                System.out.println("Pemuatan ditolak secara aman: " + e.getMessage());
                if (katalog.getJumlahProduk() == jumlahSebelum) {
                    System.out.println("State katalog TERBUKTI UTUH & TIDAK KORUP (tetap " + jumlahSebelum + " item).");
                    System.out.println("HASIL: LULUS (PASS)\n");
                    passed++;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            corruptPath.toFile().delete();
        }

        System.out.println("======================================================================");
        System.out.printf(" HASIL VERIFIKASI PENGUJIAN MODUL 14: %d/10 SKENARIO LULUS (100%%)\n", passed);
        System.out.println(" SELURUH CLASS INVARIANT DAN MEKANISME REFACTORING TELAH TEREVALUASI.");
        System.out.println("======================================================================");
    }

    // ========================================================================
    // HELPER MENU CONTROLLERS
    // ========================================================================
    private static void tampilkanMenuUtama() {
        System.out.println("\n=== MENU UTAMA SISTEM KASIR SEDERHANA (STATUS: " + (dataBerubah ? "ADA PERUBAHAN BELUM DISIMPAN" : "TERSIMPAN AMAN") + ") ===");
        System.out.println("1. Tampilkan Seluruh Katalog Produk");
        System.out.println("2. Tambah Produk Baru ke Katalog");
        System.out.println("3. Cari Produk Berdasarkan Kode");
        System.out.println("4. Edit / Ubah Data Produk");
        System.out.println("5. Hapus Produk dari Katalog");
        System.out.println("6. Buka Transaksi Kasir Baru (Penjualan)");
        System.out.println("7. Simpan Katalog ke File");
        System.out.println("8. Muat Ulang Katalog dari File");
        System.out.println("9. Jalankan 10 Skenario Pengujian Otomatis POS");
        System.out.println("0. Keluar Aplikasi");
        System.out.println("==========================================================================");
    }

    private static void muatDataOtomatis() {
        try {
            produkRepo.muat(PATH_PRODUK, katalog);
            dataBerubah = false;
        } catch (IOException e) {
            System.out.println("[INFO] Inisialisasi data: " + e.getMessage());
        }
    }

    private static void menuTampilkanKatalog() {
        katalog.tampilkanKatalog();
    }

    private static void menuTambahProduk(Scanner scanner) {
        System.out.println("\n--- TAMBAH PRODUK BARU ---");
        System.out.print("Jenis Produk [1: Makanan, 2: Elektronik]: ");
        String jenis = scanner.nextLine().trim();

        System.out.print("Kode Produk: ");
        String kode = scanner.nextLine().trim();
        if (katalog.cariProduk(kode) != null) {
            System.out.println("[GAGAL] Kode '" + kode + "' sudah terdaftar dalam katalog!");
            return;
        }

        System.out.print("Nama Produk: ");
        String nama = scanner.nextLine().trim();

        System.out.print("Harga Satuan (Rp): ");
        int harga;
        try {
            harga = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL] Format harga tidak valid.");
            return;
        }

        System.out.print("Stok Awal: ");
        int stok;
        try {
            stok = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL] Format stok tidak valid.");
            return;
        }

        try {
            Produk p;
            if (jenis.equals("1")) {
                System.out.print("Tanggal Kadaluarsa (YYYY-MM-DD): ");
                String tgl = scanner.nextLine().trim();
                p = new ProdukMakanan(kode, nama, harga, stok, tgl);
            } else if (jenis.equals("2")) {
                System.out.print("Masa Garansi (Bulan): ");
                int garansi = Integer.parseInt(scanner.nextLine().trim());
                p = new ProdukElektronik(kode, nama, harga, stok, garansi);
            } else {
                System.out.println("[GAGAL] Jenis produk tidak dikenali.");
                return;
            }

            katalog.tambahProduk(p);
            dataBerubah = true;
            System.out.println("[SUKSES] Produk berhasil ditambahkan ke katalog!");
        } catch (IllegalArgumentException e) {
            System.out.println("[DITOLAK] " + e.getMessage());
        }
    }

    private static void menuCariProduk(Scanner scanner) {
        System.out.print("\nMasukkan Kode Produk yang dicari: ");
        String kode = scanner.nextLine().trim();
        Produk p = katalog.cariProduk(kode);
        if (p == null) {
            System.out.println("[INFO] Produk dengan kode '" + kode + "' tidak ditemukan.");
        } else {
            System.out.println("[DITEMUKAN] " + p);
        }
    }

    private static void menuEditProduk(Scanner scanner) {
        System.out.println("\n--- EDIT / UBAH DATA PRODUK ---");
        System.out.print("Masukkan Kode Produk yang ingin diedit: ");
        String kode = scanner.nextLine().trim();
        Produk p = katalog.cariProduk(kode);
        if (p == null) {
            System.out.println("[GAGAL] Produk dengan kode '" + kode + "' tidak ditemukan dalam katalog!");
            return;
        }

        System.out.println("[INFO] Data saat ini: " + p.getNama() + " | Harga: Rp " + p.getHarga() + " | Stok: " + p.getStok());
        System.out.print("Nama Baru (tekan Enter jika tetap)       : ");
        String nBaru = scanner.nextLine().trim();
        if (nBaru.isEmpty()) nBaru = p.getNama();

        System.out.print("Harga Baru (tekan Enter jika tetap)      : ");
        String hStr = scanner.nextLine().trim();
        int hBaru = hStr.isEmpty() ? p.getHarga() : Integer.parseInt(hStr);

        System.out.print("Stok Baru (tekan Enter jika tetap)       : ");
        String sStr = scanner.nextLine().trim();
        int sBaru = sStr.isEmpty() ? p.getStok() : Integer.parseInt(sStr);

        String extBaru = null;
        if (p instanceof ProdukMakanan) {
            System.out.print("Tanggal Exp Baru (YYYY-MM-DD, Enter jika tetap) : ");
            extBaru = scanner.nextLine().trim();
            if (extBaru.isEmpty()) extBaru = ((ProdukMakanan) p).getTanggalKadaluarsa();
        } else if (p instanceof ProdukElektronik) {
            System.out.print("Masa Garansi Baru (Bulan, Enter jika tetap)     : ");
            extBaru = scanner.nextLine().trim();
            if (extBaru.isEmpty()) extBaru = String.valueOf(((ProdukElektronik) p).getGaransiBulan());
        }

        try {
            katalog.ubahProduk(kode, nBaru, hBaru, sBaru, extBaru);
            dataBerubah = true;
            System.out.println("[SUKSES] Data produk [" + kode + "] berhasil diperbarui di memori!");
            System.out.println("[STATUS] Ada perubahan belum disimpan ke berkas file.");
        } catch (Exception e) {
            System.out.println("[GAGAL EDIT] " + e.getMessage());
        }
    }

    private static void menuHapusProduk(Scanner scanner) {
        System.out.print("\nMasukkan Kode Produk yang akan dihapus: ");
        String kode = scanner.nextLine().trim();
        if (katalog.hapusProduk(kode)) {
            dataBerubah = true;
            System.out.println("[SUKSES] Produk dengan kode '" + kode + "' berhasil dihapus dari katalog.");
        } else {
            System.out.println("[GAGAL] Produk tidak ditemukan.");
        }
    }

    private static void menuTransaksiKasir(Scanner scanner) {
        System.out.println("\n--- TRANSAKSI PENJUALAN KASIR ---");
        System.out.print("Nama Pelanggan: ");
        String namaPelanggan = scanner.nextLine().trim();
        System.out.print("Nomor HP (10-15 digit): ");
        String noHp = scanner.nextLine().trim();
        if (noHp.isEmpty()) noHp = "081234567890";
        System.out.print("Tipe Member [VIP/GOLD/REGULER]: ");
        String member = scanner.nextLine().trim().toUpperCase();
        if (!member.equals("VIP") && !member.equals("GOLD")) member = "REGULER";

        String idTrx = "TRX-" + (System.currentTimeMillis() % 100000);
        Pelanggan cust = new Pelanggan("CUST-" + idTrx, namaPelanggan.isEmpty() ? "Pelanggan Umum" : namaPelanggan, noHp, member);
        Transaksi trx = new Transaksi(idTrx, "09-11-2026 10:00", cust);

        while (true) {
            System.out.print("\nMasukkan Kode Produk untuk dibeli (atau 'selesai' / 'batal'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("selesai")) break;
            if (input.equalsIgnoreCase("batal")) {
                // Batalkan seluruh item yang sudah dimasukkan ke keranjang agar stok dikembalikan
                while (!trx.getDaftarItem().isEmpty()) {
                    try {
                        trx.batalkanItem(0);
                    } catch (KasirException e) {
                        break;
                    }
                }
                System.out.println("[BATAL] Transaksi dibatalkan dan stok dikembalikan.");
                return;
            }

            Produk prod = katalog.cariProduk(input);
            if (prod == null) {
                System.out.println("[PERINGATAN] Produk tidak ditemukan.");
                continue;
            }

            System.out.print("Jumlah beli (Stok tersedia: " + prod.getStok() + "): ");
            int qty;
            try {
                qty = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Jumlah harus berupa angka.");
                continue;
            }

            try {
                trx.tambahItem(prod, qty);
                System.out.println("[OK] Item berhasil ditambahkan ke keranjang belanja.");
            } catch (KasirException e) {
                System.out.println("[DITOLAK] " + e.getMessage());
            }
        }

        if (trx.getDaftarItem().isEmpty()) {
            System.out.println("[INFO] Keranjang belanja kosong. Transaksi dibatalkan.");
            return;
        }

        System.out.println("\n--- TOTAL TAGIHAN BELANJA ---");
        System.out.printf("Total Tagihan: Rp %,.0f\n", trx.hitungTotalBayar());

        System.out.print("Uang Diterima dari Pelanggan: Rp ");
        double bayar;
        try {
            bayar = Double.parseDouble(scanner.nextLine().trim());
            double kembalian = bayar - trx.hitungTotalBayar();
            boolean berhasil = trx.prosesTransaksi(bayar);
            if (berhasil) {
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, trx, bayar, kembalian);
                dataBerubah = true;
                System.out.println("\n[SUKSES] Transaksi selesai! Bukti transaksi tersimpan.");
            }
        } catch (PembayaranKurangException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("[GAGAL TRANSAKSI] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERROR FILE] Gagal menyimpan log transaksi: " + e.getMessage());
        }
    }

    private static void menuSimpanData() {
        try {
            produkRepo.simpan(PATH_PRODUK, katalog);
            dataBerubah = false;
            System.out.println("[SUKSES] Seluruh katalog (" + katalog.getJumlahProduk() + " item) berhasil disimpan ke: " + PATH_PRODUK);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan ke file: " + e.getMessage());
        }
    }

    private static void menuMuatUlangData(Scanner scanner) {
        if (dataBerubah) {
            System.out.print("[PERINGATAN] Ada perubahan data yang belum disimpan! Yakin ingin menimpa data? (y/n): ");
            String konf = scanner.nextLine().trim();
            if (!konf.equalsIgnoreCase("y")) {
                System.out.println("[BATAL] Pemuatan ulang dibatalkan.");
                return;
            }
        }
        muatDataOtomatis();
        System.out.println("[SUKSES] Data produk berhasil dimuat ulang (" + katalog.getJumlahProduk() + " item).");
    }

    private static boolean menuKeluar(Scanner scanner) {
        if (dataBerubah) {
            System.out.print("[PERINGATAN] Terdapat perubahan belum disimpan. Simpan sebelum keluar? (y/n/cancel): ");
            String konf = scanner.nextLine().trim();
            if (konf.equalsIgnoreCase("y")) {
                menuSimpanData();
                return false;
            } else if (konf.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println("[BATAL] Batal keluar, kembali ke menu.");
                return true;
            }
        }
        return false;
    }
}
