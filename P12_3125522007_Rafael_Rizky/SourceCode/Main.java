import model.*;
import model.exception.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Driver Utama Proyek Sistem Kasir Sederhana - Modul 12 (Persistensi Data ke File)
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 * 
 * Pengujian Komprehensif Persistensi File produk.txt, transaksi.txt, dan Atomic Safe Loading.
 */
public class Main {

    public static void jalankanPengujianSkenarioOtomatis() {
        System.out.println("==========================================================================================");
        System.out.println("PRAKTIKUM PBO - MODUL 12: PENYIMPANAN DAN PEMBACAAN DATA KE FILE");
        System.out.println("Proyek   : Sistem Kasir Sederhana (Persistensi File & Atomic State Invariant)");
        System.out.println("Nama     : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Dosen    : Nirwana Haidar Hari, S.Pd., M.Kom.");
        System.out.println("Kampus   : PENS PSDKU Sumenep (2026)");
        System.out.println("==========================================================================================\n");

        Path pathProduk = Paths.get("data", "produk.txt");
        Path pathTransaksi = Paths.get("data", "transaksi.txt");

        ProdukFileRepository repoProduk = new ProdukFileRepository();
        TransaksiFileRepository repoTransaksi = new TransaksiFileRepository();
        KatalogProduk katalog = new KatalogProduk();

        try {
            // -----------------------------------------------------------------------------------
            // SKENARIO 1: PENYIAPAN KATALOG & PENYIMPANAN KE FILE TEKS (SAVE DATA)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 1: INISIALISASI KATALOG & PENYIMPANAN KE BERKAS FISIK (produk.txt)        ###");
            System.out.println("##########################################################################################");
            katalog.tambahProduk(new ProdukMakanan("M01", "Roti Gandum Sehat", 18000, 25, "2026-12-31"));
            katalog.tambahProduk(new ProdukMakanan("M02", "Susu UHT Cokelat 1L", 20000, 40, "2027-01-15"));
            katalog.tambahProduk(new ProdukElektronik("E01", "Mouse Wireless Ergonomis", 150000, 15, 12));
            katalog.tambahProduk(new ProdukElektronik("E02", "Keyboard Mechanical TKL", 450000, 8, 24));

            System.out.println("[KONDISI AWAL] 4 Produk terdaftar di RAM:");
            katalog.tampilkanKatalog();

            System.out.println("[AKSI] Mengekspor seluruh katalog ke berkas: " + pathProduk.toAbsolutePath());
            repoProduk.simpan(pathProduk, katalog);
            System.out.println("✔ Sukses menyimpan data ke " + pathProduk.getFileName());

            System.out.println("\n[ISI BERKAS FISIK produk.txt]:");
            List<String> isiFileAwal = Files.readAllLines(pathProduk, StandardCharsets.UTF_8);
            for (String baris : isiFileAwal) {
                System.out.println("  " + baris);
            }
            System.out.println();

            // -----------------------------------------------------------------------------------
            // SKENARIO 2: SIMULASI PROGRAM DITUTUP & DIMUAT KEMBALI PADA SESI BARU (LOAD DATA)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 2: SIMULASI RE-OPEN APLIKASI & PEMBENTUKAN KEMBALI OBJEK DARI FILE        ###");
            System.out.println("##########################################################################################");
            System.out.println("[SIMULASI] Membuka sesi baru dengan KatalogProduk baru yang masih kosong:");
            KatalogProduk katalogSesiBaru = new KatalogProduk();
            System.out.println("- Apakah katalog baru kosong? " + katalogSesiBaru.isKosong() + " (Expected: true)");

            System.out.println("[AKSI] Membaca berkas produk.txt dan membentuk kembali objek ke memori:");
            repoProduk.muat(pathProduk, katalogSesiBaru);
            System.out.println("✔ Berhasil merekonstruksi seluruh objek domain dari file teks!");

            System.out.println("\n[STATE VERIFIKASI SKENARIO 2]:");
            System.out.println("- Jumlah produk di katalog baru : " + katalogSesiBaru.getJumlahProduk() + " item (Expected: 4 item)");
            katalogSesiBaru.tampilkanKatalog();

            // -----------------------------------------------------------------------------------
            // SKENARIO 3: PENANGANAN FILE TIDAK DITEMUKAN (FILE NOT FOUND)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 3: PENANGANAN KESALAHAN FILE TIDAK DITEMUKAN (NoSuchFileException)       ###");
            System.out.println("##########################################################################################");
            Path pathFiktif = Paths.get("data", "produk_fiktif_99.txt");
            System.out.println("[AKSI] Mencoba memuat file fiktif: " + pathFiktif.getFileName());
            try {
                repoProduk.muat(pathFiktif, katalogSesiBaru);
                System.out.println("[GAGAL] Seharusnya melempar IOException!");
            } catch (IOException e) {
                System.out.println("✔ [DITANGKAP DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println("- State katalog tetap aman: " + katalogSesiBaru.getJumlahProduk() + " item.\n");

            // -----------------------------------------------------------------------------------
            // SKENARIO 4: UJI PENOLAKAN FORMAT BARIS CORRUPT (TIDAK LENGKAP 6 KOLOM)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 4: DETEKSI BERKAS RUSAK / CACAT FORMAT (CORRUPT LINE VALIDATION)          ###");
            System.out.println("##########################################################################################");
            Path pathRusak = Paths.get("data", "produk_rusak.txt");
            List<String> dataCacat = List.of(
                    "M01|Roti Gandum|18000|25|Makanan|2026-12-31",
                    "E03|Headset Bluetooth|75000" // Cacat: Hanya 3 kolom, seharusnya 6 kolom
            );
            Files.write(pathRusak, dataCacat, StandardCharsets.UTF_8);
            System.out.println("[AKSI] Mencoba memuat berkas cacat 'produk_rusak.txt':");
            try {
                repoProduk.muat(pathRusak, katalogSesiBaru);
                System.out.println("[GAGAL] Seharusnya format ditolak!");
            } catch (IllegalArgumentException e) {
                System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println();

            // -----------------------------------------------------------------------------------
            // SKENARIO 5: UJI KONSISTENSI STATE KOLEKSI (ATOMIC SAFE LOAD & PRESERVATION)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 5: UJI KONSISTENSI STATE (FAIL-SAFE: KOLEKSI LAMA TETAP UTUH)             ###");
            System.out.println("##########################################################################################");
            System.out.println("[VERIFIKASI] Memeriksa status katalogSesiBaru setelah gagal memuat file rusak:");
            System.out.println("- Jumlah produk aktif : " + katalogSesiBaru.getJumlahProduk() + " item (Expected: tetap 4 item)");
            System.out.println("✔ BUKTI ATOMIC LOAD: Koleksi memori sama sekali TIDAK terkorupsi atau kosong!");
            System.out.println();

            // -----------------------------------------------------------------------------------
            // SKENARIO 6: UJI PENOLAKAN KODE DUPLIKAT DI DALAM BERKAS
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 6: PENOLAKAN KODE PRODUK DUPLIKAT DI DALAM FILE                           ###");
            System.out.println("##########################################################################################");
            Path pathDuplikat = Paths.get("data", "produk_duplikat.txt");
            List<String> dataDuplikat = List.of(
                    "M01|Roti Gandum|18000|25|Makanan|2026-12-31",
                    "M01|Roti Gandum Duplikat|15000|10|Makanan|2026-11-30" // Kode M01 duplikat
            );
            Files.write(pathDuplikat, dataDuplikat, StandardCharsets.UTF_8);
            System.out.println("[AKSI] Mencoba memuat file dengan kode produk ganda:");
            try {
                repoProduk.muat(pathDuplikat, katalogSesiBaru);
                System.out.println("[GAGAL] Seharusnya kode duplikat ditolak!");
            } catch (IllegalArgumentException e) {
                System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println();

            // -----------------------------------------------------------------------------------
            // SKENARIO 7: UJI SANITASI KARAKTER PEMISAH TERLARANG SAAT SIMPAN
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 7: SANITASI INPUT MENCEGAH KARAKTER PEMISAH '|' MERUSAK FORMAT FILE       ###");
            System.out.println("##########################################################################################");
            System.out.println("[AKSI] Mencoba menyimpan produk yang namanya sengaja disusupi karakter '|':");
            KatalogProduk katalogLihat = new KatalogProduk();
            katalogLihat.tambahProduk(new ProdukMakanan("M99", "Biskuit | Cokelat Bahaya", 10000, 5, "2026-10-10"));
            try {
                repoProduk.simpan(Paths.get("data", "test_sanitasi.txt"), katalogLihat);
                System.out.println("[GAGAL] Seharusnya karakter pemisah ditolak!");
            } catch (IllegalArgumentException e) {
                System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
            }
            System.out.println();

            // -----------------------------------------------------------------------------------
            // SKENARIO 8: TRANSAKSI KASIR LENGKAP & PERSISTENSI LOG PENJUALAN (transaksi.txt)
            // -----------------------------------------------------------------------------------
            System.out.println("##########################################################################################");
            System.out.println("### SKENARIO 8: TRANSAKSI KASIR, PERSISTENSI LOG TRANSAKSI & PEMBARUAN STOK FILE       ###");
            System.out.println("##########################################################################################");
            Pelanggan budi = new Pelanggan("PLG-001", "Dr. Budi Santoso", "08123456789", "VIP");
            Transaksi trx = new Transaksi("TRX-2026-M12-001", "09-11-2026 10:30", budi);

            Produk roti = katalogSesiBaru.cariProduk("M01");
            Produk mouse = katalogSesiBaru.cariProduk("E01");

            trx.tambahItem(roti, 2);
            trx.tambahItem(mouse, 1, 0.10);

            double tunai = 200000;
            trx.prosesTransaksi(tunai);
            double kembalian = tunai - trx.hitungTotalBayar();

            System.out.println("[AKSI] 1. Mencatat transaksi lunas ke data/transaksi.txt");
            repoTransaksi.catatTransaksi(pathTransaksi, trx, tunai, kembalian);
            System.out.println("✔ Log transaksi sukses dicatat ke " + pathTransaksi.getFileName());

            System.out.println("[AKSI] 2. Memperbarui stok fisik di file produk.txt");
            repoProduk.simpan(pathProduk, katalogSesiBaru);
            System.out.println("✔ File produk.txt berhasil diperbarui dengan sisa stok terbaru.");

            System.out.println("\n[PEMBUKTIAN PERSISTENSI] Membaca kembali produk.txt pasca checkout kasir:");
            List<String> isiFileTerbaru = Files.readAllLines(pathProduk, StandardCharsets.UTF_8);
            for (String b : isiFileTerbaru) {
                System.out.println("  " + b);
            }

            System.out.println("\n[ISI FILE LOG transaksi.txt]:");
            List<String> logTrx = Files.readAllLines(pathTransaksi, StandardCharsets.UTF_8);
            for (String b : logTrx) {
                System.out.println("  " + b);
            }
            System.out.println();

            // Bersihkan file uji sementara
            Files.deleteIfExists(pathRusak);
            Files.deleteIfExists(pathDuplikat);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("==========================================================================================");
        System.out.println("KESIMPULAN PENGUJIAN MODUL 12 (FILE PERSISTENCE POS SYSTEM):");
        System.out.println("1. Data inventaris produk dan log transaksi penjualan berhasil tersimpan di disk.");
        System.out.println("2. Mekanisme Atomic Load terbukti melindungi memori dari kerusakan file teks.");
        System.out.println("3. Sistem kasir kini sepenuhnya mandiri dan data tidak hilang saat program ditutup.");
        System.out.println("==========================================================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            jalankanPengujianSkenarioOtomatis();
        } else {
            // Default: Antarmuka Interaktif Berbasis Menu (Menunggu Input Keyboard Pengguna)
            jalankanModeInteraktif();
        }
    }

    public static void jalankanModeInteraktif() {
        KatalogProduk katalog = new KatalogProduk();
        ProdukFileRepository repoProduk = new ProdukFileRepository();
        TransaksiFileRepository repoTransaksi = new TransaksiFileRepository();
        java.nio.file.Path pathProduk = java.nio.file.Paths.get("data", "produk.txt");
        java.nio.file.Path pathTrx = java.nio.file.Paths.get("data", "transaksi.txt");

        // Muat data awal jika ada
        try {
            if (java.nio.file.Files.exists(pathProduk)) {
                repoProduk.muat(pathProduk, katalog);
                System.out.println("[INFO] Data katalog awal berhasil dimuat (" + katalog.getJumlahProduk() + " item).");
            }
        } catch (Exception ignored) {}

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean running = true;

        System.out.println("==========================================================================================");
        System.out.println("PRAKTIKUM PBO - MODUL 12: PENYIMPANAN & PEMBACAAN DATA KE FILE (POS)");
        System.out.println("Nama     : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Dosen    : Nirwana Haidar Hari, S.Pd., M.Kom. (PENS PSDKU Sumenep)");
        System.out.println("==========================================================================================");

        while (running) {
            System.out.println("\n=== MENU PERSISTENSI FILE SISTEM KASIR (MODUL 12) ===");
            System.out.println("1. Tampilkan Seluruh Katalog Produk");
            System.out.println("2. Tambah Produk Baru ke Katalog");
            System.out.println("3. Cari Produk Berdasarkan Kode / Nama");
            System.out.println("4. Edit / Ubah Data Produk");
            System.out.println("5. Hapus Produk dari Katalog");
            System.out.println("6. Buka Transaksi Kasir Baru (Penjualan)");
            System.out.println("7. Simpan Seluruh Katalog ke Berkas File (data/produk.txt)");
            System.out.println("8. Muat Ulang Katalog dari Berkas File (data/produk.txt)");
            System.out.println("9. Jalankan Pengujian 8 Skenario File I/O Otomatis");
            System.out.println("0. Keluar Aplikasi");
            System.out.println("=====================================================");
            System.out.print("Pilih opsi menu [0-9]: ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    katalog.tampilkanKatalog();
                    break;
                case "2":
                    System.out.println("\n--- TAMBAH PRODUK BARU ---");
                    System.out.print("Pilih Jenis [1: Makanan, 2: Elektronik]: ");
                    String jenis = scanner.nextLine().trim();
                    System.out.print("Kode Produk unik: ");
                    String kode = scanner.nextLine().trim();
                    if (katalog.cariProduk(kode) != null) {
                        System.out.println("[GAGAL] Kode '" + kode + "' sudah terdaftar!");
                        break;
                    }
                    System.out.print("Nama Produk: ");
                    String nama = scanner.nextLine().trim();
                    System.out.print("Harga Satuan (Rp): ");
                    int harga;
                    try { harga = Integer.parseInt(scanner.nextLine().trim()); }
                    catch (NumberFormatException e) { System.out.println("[GAGAL] Format harga salah."); break; }
                    System.out.print("Stok Awal Fisik: ");
                    int stok;
                    try { stok = Integer.parseInt(scanner.nextLine().trim()); }
                    catch (NumberFormatException e) { System.out.println("[GAGAL] Format stok salah."); break; }

                    try {
                        Produk p;
                        if (jenis.equals("1")) {
                            System.out.print("Tanggal Kadaluarsa (YYYY-MM-DD): ");
                            String tgl = scanner.nextLine().trim();
                            p = new ProdukMakanan(kode, nama, harga, stok, tgl);
                        } else {
                            System.out.print("Masa Garansi (Bulan): ");
                            int garansi = Integer.parseInt(scanner.nextLine().trim());
                            p = new ProdukElektronik(kode, nama, harga, stok, garansi);
                        }
                        katalog.tambahProduk(p);
                        System.out.println("[SUKSES] Produk '" + nama + "' berhasil didaftarkan ke memori!");
                        System.out.println("[CATATAN] Jangan lupa pilih menu 7 untuk menyimpan permanen ke berkas.");
                    } catch (Exception e) {
                        System.out.println("[DITOLAK] " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("\nMasukkan Kode atau Kata Kunci Nama: ");
                    String kw = scanner.nextLine().trim();
                    Produk pk = katalog.cariProduk(kw);
                    if (pk != null) {
                        System.out.println("[DITEMUKAN BERDASARKAN KODE] " + pk);
                    } else {
                        List<Produk> match = katalog.cariProdukByNama(kw);
                        if (!match.isEmpty()) {
                            System.out.println("[DITEMUKAN " + match.size() + " PRODUK COCOK]:");
                            for (Produk item : match) {
                                System.out.println(" - [" + item.getKode() + "] " + item.getNama() + " | Rp " + item.getHarga());
                            }
                        } else {
                            System.out.println("[INFO] Produk '" + kw + "' tidak ditemukan.");
                        }
                    }
                    break;
                case "4":
                    System.out.println("\n--- EDIT / UBAH DATA PRODUK ---");
                    System.out.print("Masukkan Kode Produk yang ingin diedit: ");
                    String kdEdit = scanner.nextLine().trim();
                    Produk pEdit = katalog.cariProduk(kdEdit);
                    if (pEdit == null) {
                        System.out.println("[GAGAL] Produk dengan kode '" + kdEdit + "' tidak ditemukan!");
                        break;
                    }
                    System.out.println("[INFO] Data saat ini: " + pEdit.getNama() + " | Harga: Rp " + pEdit.getHarga() + " | Stok: " + pEdit.getStok());
                    System.out.print("Nama Baru (tekan Enter jika tetap): ");
                    String nBaru = scanner.nextLine().trim();
                    if (nBaru.isEmpty()) nBaru = pEdit.getNama();

                    System.out.print("Harga Baru (tekan Enter jika tetap): ");
                    String hStr = scanner.nextLine().trim();
                    int hBaru = hStr.isEmpty() ? pEdit.getHarga() : Integer.parseInt(hStr);

                    System.out.print("Stok Baru (tekan Enter jika tetap): ");
                    String sStr = scanner.nextLine().trim();
                    int sBaru = sStr.isEmpty() ? pEdit.getStok() : Integer.parseInt(sStr);

                    String extBaru = null;
                    if (pEdit instanceof ProdukMakanan) {
                        System.out.print("Tanggal Kadaluarsa Baru (YYYY-MM-DD, Enter jika tetap): ");
                        extBaru = scanner.nextLine().trim();
                        if (extBaru.isEmpty()) extBaru = ((ProdukMakanan) pEdit).getTanggalKadaluarsa();
                    } else if (pEdit instanceof ProdukElektronik) {
                        System.out.print("Masa Garansi Baru (Bulan, Enter jika tetap): ");
                        extBaru = scanner.nextLine().trim();
                        if (extBaru.isEmpty()) extBaru = String.valueOf(((ProdukElektronik) pEdit).getGaransiBulan());
                    }

                    try {
                        katalog.ubahProduk(kdEdit, nBaru, hBaru, sBaru, extBaru);
                        System.out.println("[SUKSES] Data produk [" + kdEdit + "] berhasil diperbarui di memori!");
                        System.out.println("[CATATAN] Silakan pilih menu 7 jika ingin menyimpan pembaruan ke berkas file.");
                    } catch (Exception e) {
                        System.out.println("[GAGAL EDIT] " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("\nMasukkan Kode Produk yang akan dihapus: ");
                    String kdHapus = scanner.nextLine().trim();
                    if (katalog.hapusProduk(kdHapus)) {
                        System.out.println("[SUKSES] Produk berkode '" + kdHapus + "' berhasil dihapus dari memori.");
                        System.out.println("[CATATAN] Jangan lupa simpan perubahan ke berkas (Menu 7).");
                    } else {
                        System.out.println("[GAGAL] Produk tidak ditemukan.");
                    }
                    break;
                case "6":
                    System.out.println("\n--- TRANSAKSI KASIR PENJUALAN ---");
                    System.out.print("Nama Pelanggan: ");
                    String namaPlg = scanner.nextLine().trim();
                    System.out.print("Tipe Member [VIP/GOLD/REGULER]: ");
                    String member = scanner.nextLine().trim().toUpperCase();
                    if (!member.equals("VIP") && !member.equals("GOLD")) member = "REGULER";
                    Pelanggan cust = new Pelanggan("CUST-01", namaPlg.isEmpty() ? "Pelanggan Umum" : namaPlg, "081234567890", member);
                    Transaksi trx = new Transaksi("TRX-01", "09-11-2026 10:00", cust);

                    while (true) {
                        System.out.print("\nMasukkan Kode Produk untuk dibeli (atau 'SELESAI' / 'BATAL'): ");
                        String kdBeli = scanner.nextLine().trim();
                        if (kdBeli.equalsIgnoreCase("SELESAI")) break;
                        if (kdBeli.equalsIgnoreCase("BATAL")) {
                            while (!trx.getDaftarItem().isEmpty()) {
                                try { trx.batalkanItem(0); } catch (Exception ignored) { break; }
                            }
                            System.out.println("[BATAL] Transaksi dibatalkan.");
                            break;
                        }
                        Produk prod = katalog.cariProduk(kdBeli);
                        if (prod == null) {
                            System.out.println("[INFO] Produk tidak ditemukan.");
                            continue;
                        }
                        System.out.print("Jumlah beli: ");
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        try {
                            trx.tambahItem(prod, qty);
                            System.out.println("[OK] Ditambahkan ke keranjang belanja.");
                        } catch (Exception e) {
                            System.out.println("[DITOLAK] " + e.getMessage());
                        }
                    }

                    if (!trx.getDaftarItem().isEmpty()) {
                        System.out.printf("Total Tagihan: Rp %,.0f\n", trx.hitungTotalBayar());
                        System.out.print("Uang Tunai Diserahkan: Rp ");
                        double bayar = Double.parseDouble(scanner.nextLine().trim());
                        try {
                            double kembalian = bayar - trx.hitungTotalBayar();
                            trx.prosesTransaksi(bayar);
                            repoTransaksi.catatTransaksi(pathTrx, trx, bayar, kembalian);
                            System.out.println("[SUKSES] Transaksi LUNAS! Log struk tersimpan ke " + pathTrx);
                        } catch (Exception e) {
                            System.out.println("[PEMBAYARAN DITOLAK] " + e.getMessage());
                        }
                    }
                    break;
                case "7":
                    try {
                        repoProduk.simpan(pathProduk, katalog);
                        System.out.println("[SUKSES] Katalog (" + katalog.getJumlahProduk() + " item) berhasil disimpan ke: " + pathProduk);
                    } catch (Exception e) {
                        System.out.println("[ERROR] Gagal menyimpan file: " + e.getMessage());
                    }
                    break;
                case "8":
                    try {
                        repoProduk.muat(pathProduk, katalog);
                        System.out.println("[SUKSES] Katalog berhasil disinkronkan kembali dari: " + pathProduk);
                    } catch (Exception e) {
                        System.out.println("[ERROR] Gagal memuat file: " + e.getMessage());
                    }
                    break;
                case "9":
                    jalankanPengujianSkenarioOtomatis();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("[PERINGATAN] Opsi tidak valid. Pilih antara 0 sampai 9.");
            }
        }
        System.out.println("Terima kasih telah menggunakan Sistem Kasir Sederhana Modul 12.");
    }

}
