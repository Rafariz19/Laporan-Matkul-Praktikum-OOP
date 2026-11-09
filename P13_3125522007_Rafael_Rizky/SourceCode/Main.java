import model.*;
import model.exception.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Driver Aplikasi Utama Sistem Kasir Sederhana Berbasis Menu Interaktif (Modul 13)
 * Menerapkan Separation of Concerns (SoC), pemecahan method menu, dan Dirty Flag tracking.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class Main {

    private static final Path PATH_PRODUK = Paths.get("data", "produk.txt");
    private static final Path PATH_TRANSAKSI = Paths.get("data", "transaksi.txt");

    private static final ProdukFileRepository repoProduk = new ProdukFileRepository();
    private static final TransaksiFileRepository repoTransaksi = new TransaksiFileRepository();
    private static final KatalogProduk katalog = new KatalogProduk();

    private static boolean berubah = false;
    private static int counterTrx = 1;

    // Helper I/O Scanner konsisten
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        if (!input.hasNextLine()) {
            return "0";
        }
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input, String pesan) {
        return baca(input, pesan + " (Ketik YA untuk abaikan / konfirmasi): ").equalsIgnoreCase("YA");
    }

    // ===========================================================================================
    // CONTROLLER METHODS TERPISAH (SEPARATION OF CONCERNS - MODUL 13)
    // ===========================================================================================

    private static void menuTambah(Scanner input) {
        System.out.println("\n--- [MENU 1: TAMBAH PRODUK BARU] ---");
        System.out.println("Pilih Jenis Produk:");
        System.out.println("1. Produk Makanan (dengan Tanggal Kadaluarsa)");
        System.out.println("2. Produk Elektronik (dengan Garansi Bulan)");
        String tipe = baca(input, "Pilihan jenis (1/2): ");

        String kode = baca(input, "Kode Produk unik (misal: M03, E03): ");
        String nama = baca(input, "Nama Produk                        : ");
        int harga = Integer.parseInt(baca(input, "Harga Satuan (Rp)                  : "));
        int stok = Integer.parseInt(baca(input, "Jumlah Stok Fisik                  : "));

        if (tipe.equals("1")) {
            String exp = baca(input, "Tanggal Kadaluarsa (DD-MM-YYYY)    : ");
            katalog.tambahProduk(new ProdukMakanan(kode, nama, harga, stok, exp));
        } else if (tipe.equals("2")) {
            int garansi = Integer.parseInt(baca(input, "Masa Garansi (Jumlah Bulan)        : "));
            katalog.tambahProduk(new ProdukElektronik(kode, nama, harga, stok, garansi));
        } else {
            System.out.println("✘ Jenis produk tidak valid. Penambahan dibatalkan.");
            return;
        }

        berubah = true;
        System.out.println("✔ Sukses menambahkan produk [" + kode + "] " + nama + " ke katalog.");
    }

    private static void menuTampilkan() {
        System.out.println("\n--- [MENU 2: DAFTAR KATALOG PRODUK AKTIF] ---");
        katalog.tampilkanKatalog();
    }

    private static void menuCari(Scanner input) {
        System.out.println("\n--- [MENU 3: PENCARIAN PRODUK] ---");
        System.out.println("1. Cari Berdasarkan Kode Unik");
        System.out.println("2. Cari Berdasarkan Potongan Nama");
        String opsi = baca(input, "Pilihan (1/2): ");

        if (opsi.equals("1")) {
            String kode = baca(input, "Masukkan Kode Produk: ");
            Produk p = katalog.cariProduk(kode);
            if (p != null) {
                System.out.println("✔ DITEMUKAN: [" + p.getKode() + "] " + p.getNama() +
                        " | Kategori: " + (p instanceof ProdukMakanan ? "Makanan" : "Elektronik") +
                        " | Harga: Rp " + String.format("%,d", p.getHarga()) +
                        " | Sisa Stok: " + p.getStok() + " unit");
            } else {
                System.out.println("✘ Produk dengan kode '" + kode + "' tidak ditemukan.");
            }
        } else if (opsi.equals("2")) {
            String kw = baca(input, "Masukkan Potongan Nama Produk: ");
            List<Produk> hasil = katalog.cariProdukByNama(kw);
            if (hasil.isEmpty()) {
                System.out.println("✘ Tidak ditemukan produk yang mengandung kata '" + kw + "'.");
            } else {
                System.out.println("✔ Ditemukan " + hasil.size() + " produk yang sesuai:");
                for (Produk p : hasil) {
                    System.out.println("  * [" + p.getKode() + "] " + p.getNama() + " (Stok: " + p.getStok() + ")");
                }
            }
        } else {
            System.out.println("✘ Opsi pencarian tidak tersedia.");
        }
    }

    private static void menuEdit(Scanner input) {
        System.out.println("\n--- [MENU 4: EDIT / UBAH DATA PRODUK] ---");
        String kode = baca(input, "Masukkan Kode Produk yang ingin diubah: ");
        Produk p = katalog.cariProduk(kode);
        if (p == null) {
            System.out.println("✘ Produk dengan kode '" + kode + "' tidak ditemukan.");
            return;
        }

        System.out.println("✔ Ditemukan: [" + p.getKode() + "] " + p.getNama() + " | Harga: Rp " + p.getHarga() + " | Stok: " + p.getStok());
        String nBaru = baca(input, "Nama Produk Baru (tekan Enter jika tetap)       : ");
        if (nBaru.isEmpty()) nBaru = p.getNama();

        String hStr = baca(input, "Harga Satuan Baru (tekan Enter jika tetap)      : ");
        int hBaru = hStr.isEmpty() ? p.getHarga() : Integer.parseInt(hStr);

        String sStr = baca(input, "Jumlah Stok Fisik Baru (tekan Enter jika tetap) : ");
        int sBaru = sStr.isEmpty() ? p.getStok() : Integer.parseInt(sStr);

        String extBaru = null;
        if (p instanceof ProdukMakanan) {
            extBaru = baca(input, "Tanggal Exp Baru (DD-MM-YYYY, Enter jika tetap) : ");
            if (extBaru.isEmpty()) extBaru = ((ProdukMakanan) p).getTanggalKadaluarsa();
        } else if (p instanceof ProdukElektronik) {
            extBaru = baca(input, "Masa Garansi Baru (Bulan, Enter jika tetap)     : ");
            if (extBaru.isEmpty()) extBaru = String.valueOf(((ProdukElektronik) p).getGaransiBulan());
        }

        katalog.ubahProduk(kode, nBaru, hBaru, sBaru, extBaru);
        berubah = true;
        System.out.println("✔ Sukses memperbarui produk [" + kode + "]. (Status: PERUBAHAN BELUM DISIMPAN)");
    }

    private static void menuHapus(Scanner input) {
        System.out.println("\n--- [MENU 4: HAPUS PRODUK DARI KATALOG] ---");
        String kode = baca(input, "Masukkan Kode Produk yang ingin dihapus: ");
        if (katalog.hapusProduk(kode)) {
            berubah = true;
            System.out.println("✔ Produk dengan kode '" + kode + "' berhasil dihapus dari katalog.");
        } else {
            System.out.println("✘ Kode produk '" + kode + "' tidak terdaftar; katalog tidak berubah.");
        }
    }

    private static void menuTransaksiKasir(Scanner input) {
        System.out.println("\n--- [MENU 5: BUKA TRANSAKSI KASIR BARU (CHECKOUT)] ---");
        String tanggalSekarang = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        String idTrx = "TRX-2026-M13-" + String.format("%03d", counterTrx++);

        System.out.println("Identitas Pelanggan:");
        System.out.println("1. Pelanggan Reguler / Non-Member");
        System.out.println("2. Member VIP Toko (Dr. Budi Santoso - Diskon 15%)");
        String tipePlg = baca(input, "Pilihan Pelanggan (1/2): ");

        Pelanggan pelanggan = null;
        if (tipePlg.equals("2")) {
            pelanggan = new Pelanggan("PLG-VIP-001", "Dr. Budi Santoso", "08123456789", "VIP");
            System.out.println("✔ Pelanggan VIP terhubung (Berhak diskon member 15%).");
        } else {
            System.out.println("✔ Pelanggan Umum / Non-Member terpilih.");
        }

        Transaksi trx = new Transaksi(idTrx, tanggalSekarang, pelanggan);

        // Loop penambahan item belanja
        while (true) {
            System.out.println("\nKeranjang Belanja Saat Ini: " + trx.getDaftarItem().size() + " item.");
            String kode = baca(input, "Masukkan Kode Produk yang dibeli (atau ketik 'SELESAI' untuk bayar): ");
            if (kode.equalsIgnoreCase("SELESAI")) {
                break;
            }

            Produk p = katalog.cariProduk(kode);
            if (p == null) {
                System.out.println("✘ Kode produk '" + kode + "' tidak terdaftar di katalog!");
                continue;
            }

            System.out.println("Produk Dipilih: " + p.getNama() + " | Sisa Stok Fisik: " + p.getStok() + " unit.");
            try {
                int jumlah = Integer.parseInt(baca(input, "Jumlah Beli: "));
                trx.tambahItem(p, jumlah);
                System.out.println("✔ Berhasil menambahkan " + jumlah + " unit " + p.getNama() + " ke keranjang.");
            } catch (NumberFormatException e) {
                System.out.println("✘ Kuantitas harus berupa angka bulat!");
            } catch (StokTidakCukupException e) {
                System.out.println("✘ [STOK DEFISIT]: " + e.getMessage());
            } catch (KasirException e) {
                System.out.println("✘ [GALAT TRANSAKSI]: " + e.getMessage());
            }

            String lagi = baca(input, "Tambah barang belanjaan lain? (Y/T): ");
            if (lagi.equalsIgnoreCase("T")) {
                break;
            }
        }

        // Proses Pelunasan
        try {
            System.out.println("\n========================================================");
            System.out.printf("TOTAL TAGIHAN KASIR : Rp %,.0f\n", trx.hitungTotalBayar());
            System.out.println("========================================================");
            double tunai = Double.parseDouble(baca(input, "Nominal Uang Tunai Diterima Kasir: Rp "));

            trx.prosesTransaksi(tunai);
            double kembalian = tunai - trx.hitungTotalBayar();

            // Cetak log ke file transaksi.txt
            repoTransaksi.catatTransaksi(PATH_TRANSAKSI, trx, tunai, kembalian);
            berubah = true; // Stok produk telah berkurang di RAM

            System.out.println("\n✔ Transaksi selesai secara sukses! Log tercatat ke " + PATH_TRANSAKSI.getFileName());
            System.out.println("[PERINGATAN]: Sisa stok katalog di memori telah termutasi. Jangan lupa simpan (Menu 6).");

        } catch (NumberFormatException e) {
            System.out.println("✘ Nominal uang pembayaran harus berupa angka valid!");
        } catch (PembayaranKurangException e) {
            System.out.println("✘ [PEMBAYARAN DITOLAK]: " + e.getMessage());
        } catch (TransaksiKosongException e) {
            System.out.println("✘ [NOTA KOSONG]: " + e.getMessage());
        } catch (KasirException | IOException e) {
            System.out.println("✘ [GALAT PELUNASAN]: " + e.getMessage());
        }
    }

    private static void menuSimpan() {
        System.out.println("\n--- [MENU 6: SIMPAN DATA INVENTARIS KE BERKAS] ---");
        try {
            repoProduk.simpan(PATH_PRODUK, katalog);
            berubah = false; // Reset dirty flag
            System.out.println("✔ Seluruh data inventaris katalog berhasil disimpan ke " + PATH_PRODUK.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("✘ Gagal menyimpan berkas: " + e.getMessage());
        }
    }

    private static void menuMuatUlang(Scanner input) {
        System.out.println("\n--- [MENU 7: MUAT ULANG DATA DARI BERKAS] ---");
        if (berubah && !konfirmasi(input, "Ada perubahan di memori yang belum disimpan. Yakin muat ulang?")) {
            System.out.println("Pemuatan ulang dibatalkan; perubahan tetap tersedia.");
            return;
        }

        try {
            repoProduk.muat(PATH_PRODUK, katalog);
            berubah = false;
            System.out.println("✔ Data inventaris berhasil disinkronkan kembali dari " + PATH_PRODUK.getFileName());
            katalog.tampilkanKatalog();
        } catch (IOException e) {
            System.out.println("✘ Gagal memuat berkas: " + e.getMessage());
        }
    }

    // ===========================================================================================
    // MAIN LOOP APLIKASI
    // ===========================================================================================

    public static void jalankanLoop(Scanner input) {
        // Muat data awal otomatis saat aplikasi dinyalakan
        if (Files.exists(PATH_PRODUK)) {
            try {
                repoProduk.muat(PATH_PRODUK, katalog);
                System.out.println("[SISTEM] Data awal berhasil dimuat (" + katalog.getJumlahProduk() + " produk terdaftar).");
            } catch (Exception e) {
                System.out.println("[PERINGATAN] Berkas data awal gagal dimuat: " + e.getMessage());
            }
        }

        while (true) {
            System.out.println("\n==========================================================================================");
            System.out.println("           SISTEM KASIR SEDERHANA TOKO (STATUS: " + (berubah ? "PERUBAHAN BELUM DISIMPAN" : "TERSIMPAN AMAN") + ")");
            System.out.println("==========================================================================================");
            System.out.println("1. Tambah Produk Baru ke Katalog");
            System.out.println("2. Tampilkan Daftar Katalog Produk");
            System.out.println("3. Cari Produk (Berdasarkan Kode / Nama)");
            System.out.println("4. Edit / Ubah Data Produk");
            System.out.println("5. Hapus Produk dari Katalog");
            System.out.println("6. Buka Transaksi Kasir Baru (Keranjang Belanja & Checkout)");
            System.out.println("7. Simpan Data Inventaris ke File (produk.txt)");
            System.out.println("8. Muat Ulang Data Inventaris dari File (produk.txt)");
            System.out.println("9. Jalankan Simulasi Otomatis (Demo POS)");
            System.out.println("0. Keluar Aplikasi");
            System.out.println("==========================================================================================");

            String pilihan = baca(input, "Pilih Menu [0-9]: ");

            try {
                switch (pilihan) {
                    case "1":
                        menuTambah(input);
                        break;
                    case "2":
                        menuTampilkan();
                        break;
                    case "3":
                        menuCari(input);
                        break;
                    case "4":
                        menuEdit(input);
                        break;
                    case "5":
                        menuHapus(input);
                        break;
                    case "6":
                        menuTransaksiKasir(input);
                        break;
                    case "7":
                        menuSimpan();
                        break;
                    case "8":
                        menuMuatUlang(input);
                        break;
                    case "9":
                        jalankanSimulasiOtomatis();
                        break;
                    case "0":
                        if (berubah && !konfirmasi(input, "Perubahan belum disimpan ke file. Abaikan dan tetap keluar?")) {
                            System.out.println("Keluar dibatalkan; kembali ke menu utama.");
                            break;
                        }
                        System.out.println("Terima kasih telah menggunakan Sistem Kasir Sederhana Toko. Sampai jumpa!");
                        return;
                    default:
                        System.out.println("✘ Pilihan '" + pilihan + "' tidak tersedia. Silakan pilih menu antara 0 sampai 9.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("✘ [DATA DITOLAK]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("✘ [GALAT SISTEM]: " + e.getMessage());
            }
        }
    }

    public static void jalankanSimulasiOtomatis() {
        String scriptSkenario = 
                "99\n" +                                                 // 1: Pilihan salah
                "2\n" +                                                   // 2: Tampilkan katalog
                "1\n1\nM03\nBiskuit Gandum Roma\n12000\n30\n10-10-2027\n" + // 3: Tambah Makanan M03
                "1\n1\nM04\nRoti | Cacat Format\n10000\n10\n10-10-2027\n" + // 4: Ditolak karakter '|'
                "3\n1\nE01\n" +                                           // 5: Cari by Kode E01
                "4\nM03\nBiskuit Roma Super\n15000\n50\n10-10-2027\n" +    // 6: Edit M03
                "6\n2\nM01\n2\nT\n50000\n" +                          // 7: Transaksi Kasir VIP beli M01 bayar 50.000
                "6\n1\nE01\n1\nT\n10000\n" +                          // 8: Transaksi Kasir Kurang Bayar (ditolak)
                "0\nTIDAK\n" +                                            // 9: Keluar batal (jawab TIDAK)
                "7\n" +                                                   // 10: Simpan data (berubah = false)
                "0\n";                                                    // 11: Keluar sukses bersih;

        System.out.println("\n[MENJALANKAN SIMULASI 10 SKENARIO OTOMATIS]:\n");
        try (Scanner scSimulasi = new Scanner(scriptSkenario)) {
            jalankanLoop(scSimulasi);
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("PRAKTIKUM PBO - MODUL 13: INTEGRASI APLIKASI BERBASIS MENU & PENGUJIAN FUNGSIONAL");
        System.out.println("Aplikasi : Sistem Kasir Sederhana Terintegrasi (CLI Menu Loop & Dirty State Invariant)");
        System.out.println("Nama     : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Dosen    : Nirwana Haidar Hari, S.Pd., M.Kom.");
        System.out.println("Kampus   : PENS PSDKU Sumenep (2026)");
        System.out.println("==========================================================================================\n");

        if (args.length > 0 && (args[0].equalsIgnoreCase("--test") || args[0].equalsIgnoreCase("simulasi"))) {
            jalankanSimulasiOtomatis();
            return;
        }

        // Default: Mode Interaktif Terminal Langsung (Menunggu Input Keyboard Pengguna)
        try (Scanner inputManual = new Scanner(System.in)) {
            jalankanLoop(inputManual);
        }
    }
}
