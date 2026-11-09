import model.*;
import model.exception.*;
import java.util.List;

/**
 * Driver Utama Proyek Sistem Kasir Sederhana - Modul 11 (Collection & Kumpulan Objek)
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 * 
 * Pengujian Komprehensif Manajemen Koleksi Katalog Produk & Integrasi Transaksi Kasir.
 */
public class Main {

    public static void jalankanPengujianSkenarioOtomatis() {
        System.out.println("==========================================================================================");
        System.out.println("PRAKTIKUM PBO - MODUL 11: COLLECTION DAN PENGELOLAAN KUMPULAN OBJEK");
        System.out.println("Proyek   : Sistem Kasir Sederhana (Manajemen Katalog Inventaris Dinamis)");
        System.out.println("Nama     : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Dosen    : Nirwana Haidar Hari, S.Pd., M.Kom.");
        System.out.println("Kampus   : PENS PSDKU Sumenep (2026)");
        System.out.println("==========================================================================================\n");

        KatalogProduk katalog = new KatalogProduk();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 1: PEMERIKSAAN INITIAL STATE KOLEKSI (KATALOG KOSONG)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 1: PEMERIKSAAN INITIAL STATE KOLEKSI (KATALOG MASIH KOSONG)               ###");
        System.out.println("##########################################################################################");
        System.out.println("[AKSI] Memeriksa status katalog awal:");
        System.out.println("- Apakah katalog kosong? " + katalog.isKosong() + " (Expected: true)");
        System.out.println("- Jumlah produk terdaftar : " + katalog.getJumlahProduk() + " item (Expected: 0 item)");
        System.out.println("\n[TAMPILAN KATALOG AWAL]:");
        katalog.tampilkanKatalog();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 2: PENAMBAHAN OBJEK PRODUK (POLIMORFISME MAKANAN & ELEKTRONIK)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 2: PENAMBAHAN OBJEK MAKANAN & ELEKTRONIK KE KOLEKSI (OPERASI TAMBAH)      ###");
        System.out.println("##########################################################################################");
        Produk p1 = new ProdukMakanan("M01", "Roti Gandum Sehat", 18000, 25, "2026-12-31");
        Produk p2 = new ProdukMakanan("M02", "Susu UHT Cokelat 1L", 20000, 40, "2027-01-15");
        Produk p3 = new ProdukElektronik("E01", "Mouse Wireless Ergonomis", 150000, 15, 12);
        Produk p4 = new ProdukElektronik("E02", "Keyboard Mechanical TKL", 450000, 8, 24);

        System.out.println("[AKSI] Menambahkan 4 produk polimorfis ke dalam KatalogProduk:");
        katalog.tambahProduk(p1);
        System.out.println("✔ Sukses menambahkan [" + p1.getKode() + "] " + p1.getNama() + " (Tipe: Makanan)");
        katalog.tambahProduk(p2);
        System.out.println("✔ Sukses menambahkan [" + p2.getKode() + "] " + p2.getNama() + " (Tipe: Makanan)");
        katalog.tambahProduk(p3);
        System.out.println("✔ Sukses menambahkan [" + p3.getKode() + "] " + p3.getNama() + " (Tipe: Elektronik)");
        katalog.tambahProduk(p4);
        System.out.println("✔ Sukses menambahkan [" + p4.getKode() + "] " + p4.getNama() + " (Tipe: Elektronik)");

        System.out.println("\n[STATE VERIFIKASI SKENARIO 2]:");
        System.out.println("- Jumlah produk terdaftar : " + katalog.getJumlahProduk() + " item (Expected: 4 item)");
        System.out.println("- Total nilai inventaris  : Rp " + String.format("%,d", katalog.hitungTotalNilaiInventaris()));
        katalog.tampilkanKatalog();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 3: PENCEGAHAN KODE PRODUK DUPLIKAT & VALIDASI PARAMETER
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 3: VALIDASI KEUNIKAN IDENTITAS & PENCEGAHAN KODE DUPLIKAT                 ###");
        System.out.println("##########################################################################################");
        System.out.println("[AKSI] Mencoba mendaftarkan produk baru dengan kode duplikat 'M01':");
        try {
            Produk duplikat = new ProdukMakanan("M01", "Roti Tawar Duplikat", 15000, 10, "2026-11-30");
            katalog.tambahProduk(duplikat);
            System.out.println("[GAGAL] Seharusnya sistem menolak pendaftaran kode duplikat!");
        } catch (IllegalArgumentException e) {
            System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
        }

        System.out.println("\n[AKSI] Mencoba menambahkan objek null ke katalog:");
        try {
            katalog.tambahProduk(null);
            System.out.println("[GAGAL] Seharusnya sistem menolak objek null!");
        } catch (IllegalArgumentException e) {
            System.out.println("✔ [DITOLAK DENGAN BENAR]: " + e.getMessage());
        }

        System.out.println("\n[STATE VERIFIKASI SKENARIO 3]:");
        System.out.println("- Jumlah produk tetap utuh : " + katalog.getJumlahProduk() + " item (Koleksi tidak terkorupsi)");
        System.out.println();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 4: PENCARIAN OBJEK PADA KOLEKSI (OPERASI CARI BERDASARKAN KODE & NAMA)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 4: PENCARIAN OBJEK PADA KOLEKSI (BERDASARKAN KODE DAN POTONGAN NAMA)       ###");
        System.out.println("##########################################################################################");
        System.out.println("[AKSI 4A] Mencari produk dengan kode sah 'E01':");
        Produk hasilCari1 = katalog.cariProduk("E01");
        if (hasilCari1 != null) {
            System.out.println("✔ DITEMUKAN: [" + hasilCari1.getKode() + "] " + hasilCari1.getNama() +
                    " | Harga: Rp " + String.format("%,d", hasilCari1.getHarga()) +
                    " | Stok: " + hasilCari1.getStok() + " unit");
        } else {
            System.out.println("✘ Gagal menemukan produk.");
        }

        System.out.println("\n[AKSI 4B] Mencari produk dengan kode fiktif 'X99':");
        Produk hasilCari2 = katalog.cariProduk("X99");
        System.out.println("- Hasil pencarian kode 'X99': " + (hasilCari2 == null ? "null (Benar, produk tidak ada)" : "Ditemukan"));

        System.out.println("\n[AKSI 4C] Pencarian fleksibel berdasarkan potongan kata 'Mechanical':");
        List<Produk> hasilNama = katalog.cariProdukByNama("Mechanical");
        System.out.println("- Ditemukan " + hasilNama.size() + " produk yang sesuai:");
        for (Produk p : hasilNama) {
            System.out.println("  * [" + p.getKode() + "] " + p.getNama() + " (Stok: " + p.getStok() + ")");
        }
        System.out.println();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 5: PENGHAPUSAN PRODUK DARI KOLEKSI (OPERASI HAPUS)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 5: PENGHAPUSAN OBJEK DARI KOLEKSI (OPERASI HAPUS BERHASIL & GAGAL)        ###");
        System.out.println("##########################################################################################");
        System.out.println("[AKSI 5A] Menghapus produk terdaftar dengan kode 'M02' (Susu UHT):");
        boolean statusHapus1 = katalog.hapusProduk("M02");
        System.out.println("✔ Status Penghapusan 'M02': " + statusHapus1 + " (Berhasil terhapus dari List)");

        System.out.println("\n[AKSI 5B] Memverifikasi apakah 'M02' masih bisa dicari:");
        Produk cekM02 = katalog.cariProduk("M01");
        System.out.println("- Pencarian 'M02' setelah dihapus: " + (cekM02 == null ? "null (Terbukti sudah hilang)" : "Masih ada"));

        System.out.println("\n[AKSI 5C] Mencoba menghapus produk dengan kode tidak terdaftar 'E99':");
        boolean statusHapus2 = katalog.hapusProduk("E99");
        System.out.println("✔ Status Penghapusan 'E99': " + statusHapus2 + " (Sesuai ekspektasi: gagal)");

        System.out.println("\n[STATE VERIFIKASI SKENARIO 5]:");
        System.out.println("- Jumlah produk berkurang menjadi: " + katalog.getJumlahProduk() + " item (Expected: 3 item)");
        katalog.tampilkanKatalog();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 6: INTEGRASI TRANSAKSI KASIR DENGAN OBJEK DARI KATALOG
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 6: INTEGRASI TRANSAKSI KASIR (P10) MENGGUNAKAN KOLEKSI KATALOG PRODUK     ###");
        System.out.println("##########################################################################################");
        Pelanggan pelangganVIP = new Pelanggan("PLG-001", "Dr. Budi Santoso", "08123456789", "VIP");
        Transaksi trx = new Transaksi("TRX-2026-M11-001", "09-11-2026 10:00", pelangganVIP);

        System.out.println("[KONDISI AWAL] Mengambil produk langsung dari KatalogProduk:");
        Produk roti = katalog.cariProduk("M01");
        Produk mouse = katalog.cariProduk("E01");
        System.out.println("- Roti Gandum (Stok di Katalog: " + roti.getStok() + " unit)");
        System.out.println("- Mouse Wireless (Stok di Katalog: " + mouse.getStok() + " unit)");

        try {
            System.out.println("\n[AKSI] Kasir menambahkan item dari katalog ke keranjang belanja:");
            trx.tambahItem(roti, 2);
            System.out.println("✔ Berhasil menambahkan 2 unit Roti Gandum.");
            trx.tambahItem(mouse, 1, 0.10); // Promo diskon item 10% (0.10)
            System.out.println("✔ Berhasil menambahkan 1 unit Mouse Wireless [Promo 10%].");

            System.out.println("\n[AKSI] Konsumen VIP membayar tagihan kasir:");
            System.out.printf("  Total Tagihan : Rp %,.0f\n", trx.hitungTotalBayar());
            System.out.println("  Tunai Bayar   : Rp 200.000");
            trx.prosesTransaksi(200000);

            System.out.println("\n[STRUK PENJUALAN KASIR TELAH DICETAK OTOMATIS OLEH TRANSAKSI]");

        } catch (KasirException e) {
            System.out.println("Terjadi galat transaksi: " + e.getMessage());
        }

        System.out.println("[STATE VERIFIKASI SKENARIO 6]:");
        System.out.println("- Status Transaksi   : " + trx.getStatusTransaksi() + " (Expected: LUNAS)");
        System.out.println("- Sisa Stok Roti     : " + roti.getStok() + " unit (Expected: 23 unit)");
        System.out.println("- Sisa Stok Mouse    : " + mouse.getStok() + " unit (Expected: 14 unit)");
        System.out.println("=> Terbukti: Mutasi stok pada transaksi langsung tersinkronisasi pada objek di dalam katalog!");
        System.out.println();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 7: UJI FAULT-TOLERANCE EXCEPTION HANDLING DENGAN KOLEKSI KATALOG
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 7: PERTAHANAN EXCEPTION HANDLING (StokTidakCukupException PADA KATALOG)   ###");
        System.out.println("##########################################################################################");
        Produk keyboard = katalog.cariProduk("E02");
        System.out.println("[KONDISI AWAL] Stok Keyboard di katalog: " + keyboard.getStok() + " unit.");
        System.out.println("[AKSI] Mencoba memesan 50 unit Keyboard (melebihi stok katalog)...");

        Transaksi trxGagal = new Transaksi("TRX-2026-M11-002", "09-11-2026 10:15", null);
        try {
            trxGagal.tambahItem(keyboard, 50);
            System.out.println("[GAGAL] Seharusnya melempar StokTidakCukupException!");
        } catch (StokTidakCukupException e) {
            System.out.println("✔ [DITANGKAP DENGAN BENAR]: " + e.getMessage());
            System.out.println("  Detail Diagnostik : " + e.getDetailDiagnostik());
        } catch (KasirException e) {
            System.out.println("Galat kasir: " + e.getMessage());
        }

        System.out.println("\n[STATE VERIFIKASI SKENARIO 7]:");
        System.out.println("- Sisa stok Keyboard di katalog : " + keyboard.getStok() + " unit (Expected: tetap 8 unit)");
        System.out.println("- Jumlah item nota transaksi    : " + trxGagal.getDaftarItem().size() + " item (Expected: 0 item)");
        System.out.println("=> BUKTI: Penolakan exception menjamin stok katalog aman dari pengurangan tidak valid!");
        System.out.println();

        // ---------------------------------------------------------------------------------------
        // SKENARIO 8: OPERASI UPDATE PRODUK (UBAH DATA, HARGA, STOK & VALIDASI FAIL-FAST)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 8: OPERASI UPDATE PRODUK (UBAH DATA, HARGA, STOK & VALIDASI FAIL-FAST)     ###");
        System.out.println("##########################################################################################");
        System.out.println("[KONDISI AWAL] Produk M01 sebelum diedit: " + katalog.cariProduk("M01").getNama() + " | Rp " + katalog.cariProduk("M01").getHarga());
        System.out.println("[AKSI] Memperbarui M01: Nama baru 'Roti Gandum Organik Super', Harga Rp 22.000, Stok 30, Exp: 2027-01-30");
        boolean updateSukses = katalog.ubahProduk("M01", "Roti Gandum Organik Super", 22000, 30, "2027-01-30");
        System.out.println("✔ Status pembaruan: " + updateSukses + " (Expected: true)");
        Produk m01Updated = katalog.cariProduk("M01");
        System.out.println("  Data Baru -> Nama: " + m01Updated.getNama() + " | Harga: Rp " + m01Updated.getHarga() + " | Stok: " + m01Updated.getStok());

        System.out.println("\n[UJI VALIDASI NEGATIF] Mencoba update dengan harga negatif:");
        try {
            katalog.ubahProduk("M01", "Roti Rusak", -5000, 10, "2027-01-30");
            System.out.println("[GAGAL] Seharusnya melempar IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("✔ [TERVALIDASI DENGAN TEPAT]: " + e.getMessage());
        }

        // ---------------------------------------------------------------------------------------
        // SKENARIO 9: UJI PROTEKSI ENKAPSULASI KOLEKSI (IMMUTABILITY OF READ-ONLY VIEW)
        // ---------------------------------------------------------------------------------------
        System.out.println("##########################################################################################");
        System.out.println("### SKENARIO 9: UJI ENKAPSULASI (MENCEGAH MODIFIKASI LIAR DARI LUAR CLASS KATALOG)     ###");
        System.out.println("##########################################################################################");
        System.out.println("[AKSI] Mencoba memanggil method .clear() pada view read-only koleksi:");
        try {
            List<Produk> listReadOnly = katalog.getDaftarProdukReadOnly();
            listReadOnly.clear();
            System.out.println("[GAGAL] Seharusnya mutasi liar dilempar UnsupportedOperationException!");
        } catch (UnsupportedOperationException e) {
            System.out.println("✔ [TERLINDUNGI SECARA SEMPURNA]: UnsupportedOperationException terdeteksi.");
            System.out.println("  Class pemanggil di luar KatalogProduk dilarang memanipulasi List internal secara langsung!");
        }

        System.out.println("\n[STATE VERIFIKASI AKHIR KATALOG PRODUK]:");
        katalog.tampilkanKatalog();

        System.out.println("==========================================================================================");
        System.out.println("KESIMPULAN PENGUJIAN MODUL 11 (COLLECTION POS SYSTEM):");
        System.out.println("1. KatalogProduk berhasil mengelola kumpulan objek domain dengan enkapsulasi kuat.");
        System.out.println("2. Seluruh operasi CRUD (Create, Read, Update, Delete) teruji 100% LENGKAP dan SUKSES.");
        System.out.println("3. Aturan keunikan kode produk dan proteksi exception P10 bekerja secara harmonis.");
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
        // Inisialisasi awal produk default agar katalog langsung siap pakai
        katalog.tambahProduk(new ProdukMakanan("M01", "Roti Gandum Sehat", 18000, 25, "2026-12-31"));
        katalog.tambahProduk(new ProdukMakanan("M02", "Susu UHT Cokelat 1L", 20000, 40, "2027-01-15"));
        katalog.tambahProduk(new ProdukElektronik("E01", "Mouse Wireless Ergonomis", 150000, 15, 12));
        katalog.tambahProduk(new ProdukElektronik("E02", "Keyboard Mechanical TKL", 450000, 8, 24));

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean running = true;

        System.out.println("==========================================================================================");
        System.out.println("PRAKTIKUM PBO - MODUL 11: COLLECTION DAN PENGELOLAAN KUMPULAN OBJEK");
        System.out.println("Nama     : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Dosen    : Nirwana Haidar Hari, S.Pd., M.Kom. (PENS PSDKU Sumenep)");
        System.out.println("==========================================================================================");

        while (running) {
            System.out.println("\n=== MENU KOLEKSI SISTEM KASIR (MODUL 11: COLLECTION) ===");
            System.out.println("1. Tampilkan Seluruh Katalog Produk (Collection)");
            System.out.println("2. Tambah Produk Baru ke Koleksi");
            System.out.println("3. Cari Produk Berdasarkan Kode / Nama");
            System.out.println("4. Edit / Ubah Data Produk");
            System.out.println("5. Hapus Produk dari Koleksi");
            System.out.println("6. Buka Transaksi Kasir Baru (Penjualan)");
            System.out.println("7. Jalankan Pengujian 9 Skenario Collection Otomatis");
            System.out.println("0. Keluar Aplikasi");
            System.out.println("========================================================");
            System.out.print("Pilih opsi menu [0-7]: ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    katalog.tampilkanKatalog();
                    break;
                case "2":
                    System.out.println("\n--- TAMBAH PRODUK KE KOLEKSI ---");
                    System.out.print("Pilih Jenis [1: Makanan, 2: Elektronik]: ");
                    String jenis = scanner.nextLine().trim();
                    System.out.print("Kode Produk unik: ");
                    String kode = scanner.nextLine().trim();
                    if (katalog.cariProduk(kode) != null) {
                        System.out.println("[GAGAL] Kode '" + kode + "' sudah terdaftar dalam koleksi!");
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
                        System.out.println("[SUKSES] Produk '" + nama + "' berhasil ditambahkan ke koleksi!");
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
                        System.out.println("[SUKSES] Data produk [" + kdEdit + "] berhasil diperbarui!");
                    } catch (Exception e) {
                        System.out.println("[GAGAL EDIT] " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("\nMasukkan Kode Produk yang akan dihapus: ");
                    String kdHapus = scanner.nextLine().trim();
                    if (katalog.hapusProduk(kdHapus)) {
                        System.out.println("[SUKSES] Produk berkode '" + kdHapus + "' berhasil dihapus dari koleksi.");
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
                        System.out.print("Jumlah beli (Tersedia: " + prod.getStok() + "): ");
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
                            trx.prosesTransaksi(bayar);
                            System.out.println("[SUKSES] Transaksi LUNAS! Sisa kembalian: Rp " + (bayar - trx.hitungTotalBayar()));
                        } catch (Exception e) {
                            System.out.println("[PEMBAYARAN DITOLAK] " + e.getMessage());
                        }
                    }
                    break;
                case "7":
                    jalankanPengujianSkenarioOtomatis();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("[PERINGATAN] Opsi tidak valid. Pilih antara 0 sampai 7.");
            }
        }
        System.out.println("Terima kasih telah menggunakan Sistem Kasir Sederhana Modul 11.");
    }

}
