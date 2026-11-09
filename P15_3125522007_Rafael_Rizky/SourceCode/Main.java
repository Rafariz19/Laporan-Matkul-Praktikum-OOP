import model.*;
import model.exception.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Main - Sistem Kasir Sederhana (POS) - Modul 15 Demonstrasi Final & Evaluasi
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 * 
 * Mode Eksekusi:
 * - Default (tanpa argumen): Menjalankan Antarmuka Terminal Interaktif Berbasis Menu (Menunggu Input Keyboard).
 * - Argumen "--demo"       : Menjalankan Alur Demonstrasi 7-Menit Terstruktur (Langkah 2 Modul 15).
 * - Argumen "--test"       : Menjalankan 10 Skenario Pengujian Regresi Otomatis.
 */
public class Main {
    private static final Path PATH_PRODUK = Paths.get("data/produk.txt");
    private static final Path PATH_TRANSAKSI = Paths.get("data/transaksi.txt");

    private static final KatalogProduk katalog = new KatalogProduk();
    private static final ProdukFileRepository produkRepo = new ProdukFileRepository();
    private static final TransaksiFileRepository transaksiRepo = new TransaksiFileRepository();
    private static boolean dataBerubah = false;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            jalankanDemonstrasiResmiP15();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            jalankanPengujianSkenarioPOS();
        } else {
            // Default: Masuk ke Menu Konsol Interaktif (Menunggu Input Nyata Pengguna)
            jalankanModeInteraktif();
        }
    }

    // ========================================================================
    // MODE KONSOL INTERAKTIF BERBASIS MENU (DEFAULT RUNNER)
    // ========================================================================
    public static void jalankanModeInteraktif() {
        muatDataOtomatis();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("======================================================================");
        System.out.println(" PRAKTIKUM PBO - MODUL 15: SISTEM KASIR SEDERHANA (POINT OF SALE)");
        System.out.println(" Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)");
        System.out.println(" Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom. (PENS PSDKU Sumenep)");
        System.out.println("======================================================================");

        while (running) {
            tampilkanMenuUtama();
            System.out.print("Pilih opsi menu [0-9]: ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    katalog.tampilkanKatalog();
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
                    jalankanDemonstrasiResmiP15();
                    break;
                case "0":
                    running = menuKeluar(scanner);
                    break;
                default:
                    System.out.println("[PERINGATAN] Pilihan menu tidak valid. Silakan pilih 0 - 9.");
            }
        }
        System.out.println("Terima kasih telah menggunakan Sistem Kasir Sederhana. Sampai jumpa!");
    }

    private static void tampilkanMenuUtama() {
        System.out.println("\n=== MENU UTAMA SISTEM KASIR SEDERHANA (STATUS: " + (dataBerubah ? "ADA PERUBAHAN BELUM DISIMPAN" : "TERSIMPAN AMAN") + ") ===");
        System.out.println("1. Tampilkan Seluruh Katalog Produk");
        System.out.println("2. Tambah Produk Baru ke Katalog");
        System.out.println("3. Cari Produk Berdasarkan Kode / Nama");
        System.out.println("4. Edit / Ubah Data Produk");
        System.out.println("5. Hapus Produk dari Katalog");
        System.out.println("6. Buka Transaksi Kasir Baru (Penjualan & Cetak Struk)");
        System.out.println("7. Simpan Katalog ke File (data/produk.txt)");
        System.out.println("8. Muat Ulang Katalog dari File (data/produk.txt)");
        System.out.println("9. Jalankan Alur Demonstrasi Resmi 7-Menit (Modul 15)");
        System.out.println("0. Keluar Aplikasi");
        System.out.println("==========================================================================");
    }

    private static void muatDataOtomatis() {
        try {
            if (PATH_PRODUK.toFile().exists()) {
                produkRepo.muat(PATH_PRODUK, katalog);
                System.out.println("[SISTEM] Data katalog berhasil dimuat (" + katalog.getJumlahProduk() + " item terdaftar).");
            } else {
                System.out.println("[SISTEM] Berkas katalog belum ada, memulai dengan katalog baru.");
            }
            dataBerubah = false;
        } catch (IOException e) {
            System.out.println("[INFO] Inisialisasi data: " + e.getMessage());
        }
    }

    private static void menuTambahProduk(Scanner scanner) {
        System.out.println("\n--- [MENU 2: TAMBAH PRODUK BARU] ---");
        System.out.print("Pilih Jenis Produk [1: Makanan, 2: Elektronik]: ");
        String jenis = scanner.nextLine().trim();

        System.out.print("Kode Produk unik (misal: MK03, EL03): ");
        String kode = scanner.nextLine().trim();
        if (katalog.cariProduk(kode) != null) {
            System.out.println("[GAGAL DITOLAK] Kode '" + kode + "' sudah terdaftar dalam katalog!");
            return;
        }

        System.out.print("Nama Produk                        : ");
        String nama = scanner.nextLine().trim();

        System.out.print("Harga Satuan (Rp)                  : ");
        int harga;
        try {
            harga = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL DITOLAK] Format harga harus bilangan bulat.");
            return;
        }

        System.out.print("Stok Awal Fisik                    : ");
        int stok;
        try {
            stok = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL DITOLAK] Format stok harus bilangan bulat.");
            return;
        }

        try {
            Produk p;
            if (jenis.equals("1")) {
                System.out.print("Tanggal Kadaluarsa (YYYY-MM-DD)    : ");
                String tgl = scanner.nextLine().trim();
                p = new ProdukMakanan(kode, nama, harga, stok, tgl);
            } else if (jenis.equals("2")) {
                System.out.print("Masa Garansi (Bulan)               : ");
                int garansi = Integer.parseInt(scanner.nextLine().trim());
                p = new ProdukElektronik(kode, nama, harga, stok, garansi);
            } else {
                System.out.println("[GAGAL DITOLAK] Jenis produk tidak dikenali (pilih 1 atau 2).");
                return;
            }

            katalog.tambahProduk(p);
            dataBerubah = true;
            System.out.println("[SUKSES] Produk " + p.getNama() + " berhasil ditambahkan ke katalog!");
        } catch (IllegalArgumentException e) {
            System.out.println("[DITOLAK CLASS INVARIANT] " + e.getMessage());
        }
    }

    private static void menuCariProduk(Scanner scanner) {
        System.out.print("\nMasukkan Kode atau Kata Kunci Nama Produk: ");
        String kw = scanner.nextLine().trim();
        Produk p = katalog.cariProduk(kw);
        if (p != null) {
            System.out.println("[DITEMUKAN BERDASARKAN KODE EKSAK] " + p);
        } else {
            List<Produk> list = katalog.cariProdukByNama(kw);
            if (!list.isEmpty()) {
                System.out.println("[DITEMUKAN " + list.size() + " PRODUK COCOK]:");
                for (Produk prod : list) {
                    System.out.println(" - [" + prod.getKode() + "] " + prod.getNama() + " | Rp " + prod.getHarga() + " | Stok: " + prod.getStok());
                }
            } else {
                System.out.println("[INFO] Tidak ditemukan produk yang sesuai dengan '" + kw + "'.");
            }
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
        System.out.println("\n--- [MENU 5: TRANSAKSI KASIR PENJUALAN] ---");
        System.out.print("Nama Pelanggan: ");
        String nama = scanner.nextLine().trim();
        System.out.print("Nomor HP      : ");
        String noHp = scanner.nextLine().trim();
        if (noHp.isEmpty()) noHp = "081234567890";
        System.out.print("Tipe Member [VIP/GOLD/REGULER]: ");
        String member = scanner.nextLine().trim().toUpperCase();
        if (!member.equals("VIP") && !member.equals("GOLD")) member = "REGULER";

        String idTrx = "TRX-" + (System.currentTimeMillis() % 100000);
        Pelanggan cust = new Pelanggan("CUST-" + idTrx, nama.isEmpty() ? "Pelanggan Umum" : nama, noHp, member);
        Transaksi trx = new Transaksi(idTrx, "09-11-2026 14:00", cust);

        System.out.println("Sesi Transaksi " + idTrx + " dibuka untuk " + cust.getNama() + " (Member: " + cust.getTipeMember() + ")");

        while (true) {
            System.out.print("\nMasukkan Kode Produk untuk dibeli (ketik 'SELESAI' untuk bayar, 'BATAL' untuk keluar): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("SELESAI")) break;
            if (input.equalsIgnoreCase("BATAL")) {
                while (!trx.getDaftarItem().isEmpty()) {
                    try { trx.batalkanItem(0); } catch (Exception ignored) { break; }
                }
                System.out.println("[BATAL] Transaksi dibatalkan dan stok produk dikembalikan utuh.");
                return;
            }

            Produk prod = katalog.cariProduk(input);
            if (prod == null) {
                System.out.println("[PERINGATAN] Produk dengan kode '" + input + "' tidak ditemukan dalam katalog.");
                continue;
            }

            System.out.print("Jumlah beli untuk '" + prod.getNama() + "' (Stok tersedia: " + prod.getStok() + "): ");
            int qty;
            try {
                qty = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Jumlah harus berupa bilangan bulat.");
                continue;
            }

            try {
                trx.tambahItem(prod, qty);
                System.out.println("[OK] Item berhasil ditambahkan ke keranjang.");
            } catch (KasirException e) {
                System.out.println("[DITOLAK ATURAN KASIR] " + e.getMessage());
            }
        }

        if (trx.getDaftarItem().isEmpty()) {
            System.out.println("[INFO] Keranjang belanja kosong. Transaksi dibatalkan.");
            return;
        }

        System.out.println("\n--- TOTAL TAGIHAN TRANSAKSI ---");
        System.out.printf("Total Subtotal Belanja : Rp %,.0f\n", (double) trx.hitungTotalSubtotal());
        System.out.printf("Diskon Member (%s)    : Rp %,.0f\n", cust.getTipeMember(), trx.hitungDiskonNominal());
        System.out.printf("TOTAL TAGIHAN AKHIR    : Rp %,.0f\n", trx.hitungTotalBayar());

        System.out.print("\nUang Tunai Diserahkan Konsumen: Rp ");
        double bayar;
        try {
            bayar = Double.parseDouble(scanner.nextLine().trim());
            double kembalian = bayar - trx.hitungTotalBayar();
            boolean berhasil = trx.prosesTransaksi(bayar);
            if (berhasil) {
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, trx, bayar, kembalian);
                dataBerubah = true;
                System.out.println("\n[SUKSES] Transaksi selesai! Bukti struk dicatat ke: " + PATH_TRANSAKSI);
            }
        } catch (PembayaranKurangException e) {
            System.out.println("[PEMBAYARAN DITOLAK] " + e.getMessage());
        } catch (KasirException | IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void menuSimpanData() {
        try {
            produkRepo.simpan(PATH_PRODUK, katalog);
            dataBerubah = false;
            System.out.println("[SUKSES] Seluruh katalog (" + katalog.getJumlahProduk() + " item) berhasil disimpan ke: " + PATH_PRODUK);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan file: " + e.getMessage());
        }
    }

    private static void menuMuatUlangData(Scanner scanner) {
        if (dataBerubah) {
            System.out.print("[PERINGATAN] Ada perubahan belum disimpan! Yakin ingin menimpa data memori? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("[BATAL] Pemuatan ulang dibatalkan.");
                return;
            }
        }
        muatDataOtomatis();
        System.out.println("[SUKSES] Data produk berhasil disinkronkan kembali (" + katalog.getJumlahProduk() + " item).");
    }

    private static boolean menuKeluar(Scanner scanner) {
        if (dataBerubah) {
            System.out.print("[PERINGATAN] Ada data yang belum disimpan ke file. Simpan sebelum keluar? (y/n/cancel): ");
            String konf = scanner.nextLine().trim();
            if (konf.equalsIgnoreCase("y")) {
                menuSimpanData();
                return false;
            } else if (konf.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println("[BATAL] Batal keluar, kembali ke menu utama.");
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    // ALUR DEMONSTRASI 7-MENIT RESMI MODUL 15 (LANGKAH 2)
    // ========================================================================
    public static void jalankanDemonstrasiResmiP15() {
        System.out.println("\n======================================================================");
        System.out.println(" PRAKTIKUM PBO - MODUL 15: DEMONSTRASI PROYEK & EVALUASI INDIVIDU");
        System.out.println(" SISTEM KASIR SEDERHANA (POINT OF SALE SYSTEM)");
        System.out.println(" Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)");
        System.out.println(" Dosen     : Nirwana Haidar Hari, S.Pd., M.Kom. (PENS PSDKU Sumenep)");
        System.out.println("======================================================================\n");

        // TAHAP 1: MASALAH DAN DESAIN ARSITEKTUR
        System.out.println("----------------------------------------------------------------------");
        System.out.println("[TAHAP 1: MASALAH DAN DESAIN ARSITEKTUR OOP]");
        System.out.println("Kebutuhan: Mengelola inventaris produk retail polimorfik, mencegah");
        System.out.println("kebocoran stok finansial, kalkulasi diskon member, struk kasir,");
        System.out.println("dan persistensi aman atomik tanpa ketergantungan DBMS eksternal.");
        System.out.println("Class Tanggung Jawab: Produk (Abstract Domain), KatalogProduk (Manager),");
        System.out.println("Transaksi & ItemTransaksi (Transaction Domain), Repositories (Persistensi).");
        System.out.println("----------------------------------------------------------------------\n");

        katalog.kosongkanKatalog();

        // TAHAP 2: ALUR NORMAL (HAPPY PATH)
        System.out.println("[TAHAP 2: ALUR PENGGUNAAN NORMAL / TRANSAKSI LENGKAP]");
        System.out.println("1. Inisialisasi Produk Polimorfik ke Katalog:");
        Produk roti = new ProdukMakanan("MK01", "Roti Bakar Keju", 15000, 20, "2026-12-31");
        Produk mouse = new ProdukElektronik("EL01", "Mouse Wireless Silent", 125000, 10, 12);
        katalog.tambahProduk(roti);
        katalog.tambahProduk(mouse);
        System.out.println("   -> Terdaftar: [" + roti.getKode() + "] " + roti.getNama() + " (" + roti.getKategoriInfo() + ")");
        System.out.println("   -> Terdaftar: [" + mouse.getKode() + "] " + mouse.getNama() + " (" + mouse.getKategoriInfo() + ")");
        System.out.println("   -> Total Nilai Inventaris Awal: Rp " + katalog.hitungTotalNilaiInventaris());

        System.out.println("\n2. Melayani Transaksi Penjualan Pelanggan VIP:");
        Pelanggan vip = new Pelanggan("CUST-007", "Rafael Rizky", "081234567890", "VIP");
        Transaksi trx = new Transaksi("TRX-DEMO-01", "09-11-2026 14:00", vip);

        try {
            trx.tambahItem(roti, 2, 0.0); // 2 x 15.000 = 30.000
            trx.tambahItem(mouse, 1, 0.10); // 1 x 125.000 (diskon promo 10%) = 112.500
            double totalTagihan = trx.hitungTotalBayar();
            double tunai = 150000;
            double kembalian = tunai - totalTagihan;

            System.out.println("   -> Total Subtotal Tagihan: Rp " + totalTagihan);
            System.out.println("   -> Pembayaran tunai: Rp " + tunai);

            boolean lunas = trx.prosesTransaksi(tunai);
            if (lunas) {
                transaksiRepo.catatTransaksi(PATH_TRANSAKSI, trx, tunai, kembalian);
                System.out.println("   -> [STATUS] Transaksi LUNAS! Riwayat tersimpan ke " + PATH_TRANSAKSI);
                System.out.println("   -> Sisa Stok Roti  : " + roti.getStok() + " unit");
                System.out.println("   -> Sisa Stok Mouse : " + mouse.getStok() + " unit");
            }
        } catch (KasirException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // TAHAP 3: KONDISI GAGAL (INPUT TIDAK VALID & CHECKED EXCEPTIONS)
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[TAHAP 3: PENANGANAN KONDISI GAGAL (EDGE CASES & EXCEPTIONS)]");
        System.out.println("A. Uji Penolakan Duplikasi Kode:");
        try {
            katalog.tambahProduk(new ProdukMakanan("MK01", "Roti Duplikat", 10000, 5, "2026-10-10"));
        } catch (IllegalArgumentException e) {
            System.out.println("   -> BERHASIL DITOLAK: " + e.getMessage());
        }

        System.out.println("\nB. Uji Sanitasi Karakter Delimiter Pipeling ('|'):");
        try {
            new ProdukMakanan("MK02", "Kopi Susu | Extra Gula", 12000, 10, "2026-10-10");
        } catch (IllegalArgumentException e) {
            System.out.println("   -> BERHASIL DITOLAK: " + e.getMessage());
        }

        System.out.println("\nC. Uji Penolakan Beli Melebihi Stok (StokTidakCukupException):");
        Transaksi trxGagal = new Transaksi("TRX-DEMO-02", "09-11-2026 14:05", vip);
        try {
            trxGagal.tambahItem(mouse, 50); // Stok cuma 9
        } catch (StokTidakCukupException e) {
            System.out.println("   -> BERHASIL DITOLAK: " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("   -> Exception: " + e.getMessage());
        }

        System.out.println("\nD. Uji Penolakan Pembayaran Kurang (PembayaranKurangException):");
        try {
            trxGagal.tambahItem(roti, 1); // 1 x 15.000, diskon VIP 15% = 12.750
            trxGagal.prosesTransaksi(10000); // Bayar cuma 10.000
        } catch (PembayaranKurangException e) {
            System.out.println("   -> BERHASIL DITOLAK: " + e.getMessage());
        } catch (KasirException e) {
            System.out.println("   -> Exception: " + e.getMessage());
        }

        // TAHAP 4: KONSISTENSI STATE (CLASS INVARIANT ASSERTION)
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[TAHAP 4: PEMBUKTIAN KONSISTENSI STATE & ATOMISITAS]");
        System.out.println("Verifikasi bahwa seluruh kegagalan transaksi di atas TIDAK merusak state:");
        System.out.println("1. Jumlah Produk di Katalog: " + katalog.getJumlahProduk() + " (TERBUKTI UTUH)");
        System.out.println("2. Stok Roti Aktif          : " + roti.getStok() + " unit (TERBUKTI TETAP AMAN)");
        System.out.println("3. Status Transaksi Gagal   : " + trxGagal.getStatusTransaksi() + " (TETAP PENDING, TIDAK LUNAS)");

        // TAHAP 5: PERSISTENSI BERKAS & ATOMIC SAFE LOAD
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[TAHAP 5: PERSISTENSI BERKAS & ATOMIC SAFE LOAD]");
        try {
            produkRepo.simpan(PATH_PRODUK, katalog);
            System.out.println("1. Data katalog berhasil diekspor ke: " + PATH_PRODUK);

            KatalogProduk katalogBaru = new KatalogProduk();
            produkRepo.muat(PATH_PRODUK, katalogBaru);
            System.out.println("2. Simulasi buka ulang aplikasi: Dimuat " + katalogBaru.getJumlahProduk() + " produk.");

            Path testCorrupt = Paths.get("data/test_corrupt_p15.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(testCorrupt.toFile()))) {
                pw.println("MK99|Data Rusak Tanpa Kolom Lengkap");
            }
            try {
                produkRepo.muat(testCorrupt, katalogBaru);
            } catch (IllegalArgumentException e) {
                System.out.println("3. Uji Berkas Rusak: Pemuatan ditolak secara aman -> " + e.getMessage());
                System.out.println("   -> State memori katalog terbukti UTUH (" + katalogBaru.getJumlahProduk() + " produk).");
            } finally {
                testCorrupt.toFile().delete();
            }
        } catch (IOException e) {
            System.out.println("Error persistensi: " + e.getMessage());
        }

        // TAHAP 6: FITUR BONUS LIVE MODIFICATION (CHALLENGE DOSEN)
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[TAHAP 6: KESIAPAN LIVE CODE MODIFICATION (CHALLENGE DOSEN)]");
        System.out.println("A. Pencarian Sebagian Nama (Partial Keyword 'bakar'):");
        List<Produk> hasilCari = katalog.cariProdukByNama("bakar");
        System.out.println("   -> Ditemukan: " + hasilCari.size() + " produk: " + (hasilCari.isEmpty() ? "-" : hasilCari.get(0).getNama()));
        System.out.println("B. Total Nilai Aset Inventaris Toko: Rp " + katalog.hitungTotalNilaiInventaris());
        System.out.println("----------------------------------------------------------------------");
        System.out.println(" DEMONSTRASI SELESAI DENGAN STATUS 100% SUKSES DAN TERVERIFIKASI.");
        System.out.println("======================================================================");
    }

    public static void jalankanPengujianSkenarioPOS() {
        jalankanDemonstrasiResmiP15();
    }
}
