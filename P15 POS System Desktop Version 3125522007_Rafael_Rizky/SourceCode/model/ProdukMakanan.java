package model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Subclass: ProdukMakanan
 * Komoditas konsumsi yang memiliki masa simpan dan tanggal kadaluarsa.
 * Mewarisi abstract class Produk dan mengimplementasikan interface DapatDidiskon.
 * 
 * Sesuai Modul 10 & Fase 2:
 * - Menegakkan validasi ketat pada tanggal kadaluarsa menggunakan LocalDate.
 * - Parsing fleksibel: mendukung format standar ISO (yyyy-MM-dd) dan format Indonesia (dd-MM-yyyy).
 * - Logika diskon dinamis kedaluwarsa berbasis java.time.Clock yang dapat diinjeksi.
 *   Jenjang Diskon:
 *   * Sisa > 14 hari  : 0%
 *   * Sisa 8 - 14 hari : 10%
 *   * Sisa 4 - 7 hari  : 25%
 *   * Sisa 1 - 3 hari  : 40%
 *   * Hari H (0 hari)  : 50%
 *   * Sisa < 0 hari    : Kedaluwarsa (kadaluarsa)
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ProdukMakanan extends Produk implements DapatDidiskon {
    private LocalDate tanggalKadaluarsaDate;
    private static final DateTimeFormatter FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMAT_ID = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor ProdukMakanan standar tanpa promo khusus (diskonPromo = 0.0).
     * 
     * @param kode              kode barang makanan
     * @param nama              nama barang makanan
     * @param harga             harga satuan makanan (> 0)
     * @param stok              jumlah stok makanan (>= 0)
     * @param tanggalKadaluarsa tanggal masa berlaku kadaluarsa (format: yyyy-MM-dd atau dd-MM-yyyy)
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa) {
        this(kode, nama, harga, stok, tanggalKadaluarsa, 0.0);
    }

    /**
     * Constructor Overloaded ProdukMakanan dengan diskon promo barang.
     * 
     * @param kode              kode barang makanan
     * @param nama              nama barang makanan
     * @param harga             harga satuan makanan (> 0)
     * @param stok              jumlah stok makanan (>= 0)
     * @param tanggalKadaluarsa tanggal masa berlaku kadaluarsa (format: yyyy-MM-dd atau dd-MM-yyyy)
     * @param diskonPromo       rasio diskon promosi barang (0.0 s/d 1.0)
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public ProdukMakanan(String kode, String nama, int harga, int stok, String tanggalKadaluarsa, double diskonPromo) {
        super(kode, nama, harga, stok, diskonPromo);
        this.tanggalKadaluarsaDate = parseTanggal(tanggalKadaluarsa);
    }

    private static LocalDate parseTanggal(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Tanggal kadaluarsa tidak boleh kosong atau null!");
        }
        if (str.contains("|") || str.contains("\n") || str.contains("\r")) {
            throw new IllegalArgumentException("[ERROR SANITASI] Tanggal kadaluarsa tidak boleh mengandung pemisah '|' atau baris baru!");
        }
        String clean = str.trim();
        // Coba format yyyy-MM-dd dulu
        try {
            return LocalDate.parse(clean, FORMAT_ISO);
        } catch (DateTimeParseException e1) {
            // Coba format dd-MM-yyyy
            try {
                return LocalDate.parse(clean, FORMAT_ID);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("[ERROR FORMAT] Format tanggal kadaluarsa harus yyyy-MM-dd atau dd-MM-yyyy! (Diberikan: " + clean + ")");
            }
        }
    }

    // --- GETTER & MUTATOR ---

    /**
     * Mengembalikan representasi tanggal kadaluarsa dalam format standar yyyy-MM-dd
     * untuk konsistensi repositori dan backward compatibility.
     */
    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsaDate.format(FORMAT_ISO);
    }

    public LocalDate getTanggalKadaluarsaDate() {
        return tanggalKadaluarsaDate;
    }

    /**
     * Mengubah tanggal kadaluarsa dengan validasi ketat.
     * 
     * @param tanggalBaru format tanggal baru (yyyy-MM-dd atau dd-MM-yyyy)
     * @throws IllegalArgumentException jika tanggalBaru null atau tidak valid
     */
    public void ubahTanggalKadaluarsa(String tanggalBaru) {
        this.tanggalKadaluarsaDate = parseTanggal(tanggalBaru);
    }

    // --- LOGIKA DISKON & KADALUARSA BERBASIS CLOCK ---

    /**
     * Menghitung sisa hari hingga tanggal kedaluwarsa berdasarkan Clock yang diinjeksi.
     * 
     * @param clock objek java.time.Clock pengukur waktu
     * @return sisa hari (positif jika belum exp, 0 jika hari H, negatif jika telah lewat)
     */
    public long hitungSisaHari(Clock clock) {
        LocalDate hariIni = (clock != null) ? LocalDate.now(clock) : LocalDate.now();
        return ChronoUnit.DAYS.between(hariIni, this.tanggalKadaluarsaDate);
    }

    public long hitungSisaHari() {
        return hitungSisaHari(Clock.systemDefaultZone());
    }

    /**
     * Mengecek apakah komoditas makanan sudah kadaluarsa (sisa hari < 0).
     */
    public boolean isKadaluarsa(Clock clock) {
        return hitungSisaHari(clock) < 0;
    }

    public boolean isKadaluarsa() {
        return isKadaluarsa(Clock.systemDefaultZone());
    }

    /**
     * Menghitung persentase diskon dinamis kedaluwarsa berdasarkan jenjang sisa hari.
     * Sisa > 14 hari  : 0%
     * Sisa 8 - 14 hari : 10% (0.10)
     * Sisa 4 - 7 hari  : 25% (0.25)
     * Sisa 1 - 3 hari  : 40% (0.40)
     * Hari H (0 hari)  : 50% (0.50)
     * Sisa < 0 hari    : 0.0 (sudah kadaluarsa, dilarang dijual)
     */
    public double hitungDiskonKedaluwarsa(Clock clock) {
        long sisa = hitungSisaHari(clock);
        if (sisa < 0) {
            return 0.0;
        } else if (sisa == 0) {
            return 0.50; // Hari H: Diskon 50%
        } else if (sisa <= 3) {
            return 0.40; // 1-3 hari: Diskon 40%
        } else if (sisa <= 7) {
            return 0.25; // 4-7 hari: Diskon 25%
        } else if (sisa <= 14) {
            return 0.10; // 8-14 hari: Diskon 10%
        } else {
            return 0.0;  // > 14 hari: Harga Normal
        }
    }

    public double hitungDiskonKedaluwarsa() {
        return hitungDiskonKedaluwarsa(Clock.systemDefaultZone());
    }

    // --- IMPLEMENTASI ABSTRACT METHODS ---

    @Override
    public String getKategoriInfo() {
        return "Makanan & Minuman Segar (Konsumsi)";
    }

    @Override
    public void tampilkanDetailKhusus() {
        System.out.println("Kadaluarsa  : " + getTanggalKadaluarsa() + " (Sisa: " + hitungSisaHari() + " hari)");
    }
}
