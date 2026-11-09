package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ProdukFileRepository - Komponen Persistensi Data File (Modul 12)
 * Memisahkan tanggung jawab I/O file teks dari class KatalogProduk (Separation of Concerns).
 * Format per baris: KODE|NAMA|HARGA|STOK|KATEGORI|EXTRA_ATTRIBUTES
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class ProdukFileRepository {

    public ProdukFileRepository() {
    }

    /**
     * Menyimpan seluruh koleksi produk dari KatalogProduk ke dalam file teks.
     * Sanitasi: Memastikan tidak ada nilai yang mengandung karakter pemisah '|', '\n', atau '\r'.
     */
    public void simpan(Path lokasi, KatalogProduk katalog) throws IOException {
        if (lokasi == null || katalog == null) {
            throw new IllegalArgumentException("[ERROR] Lokasi file dan katalog tidak boleh null.");
        }

        List<String> barisList = new ArrayList<>();
        for (Produk p : katalog.getDaftarProdukReadOnly()) {
            String kode = p.getKode();
            String nama = p.getNama();
            String harga = String.valueOf(p.getHarga());
            String stok = String.valueOf(p.getStok());
            String kategori = (p instanceof ProdukMakanan) ? "Makanan" : "Elektronik";
            String extra = "";

            if (p instanceof ProdukMakanan) {
                extra = ((ProdukMakanan) p).getTanggalKadaluarsa();
            } else if (p instanceof ProdukElektronik) {
                extra = String.valueOf(((ProdukElektronik) p).getGaransiBulan());
            }

            // Sanitasi karakter pemisah terlarang
            for (String nilai : new String[]{kode, nama, harga, stok, kategori, extra}) {
                if (nilai.contains("|") || nilai.contains("\n") || nilai.contains("\r")) {
                    throw new IllegalArgumentException("[ERROR SANITASI] Data produk mengandung karakter terlarang ('|', '\\n', '\\r'): " + nilai);
                }
            }

            barisList.add(kode + "|" + nama + "|" + harga + "|" + stok + "|" + kategori + "|" + extra);
        }

        // Tulis ke disk
        if (lokasi.getParent() != null) {
            Files.createDirectories(lokasi.getParent());
        }
        Files.write(lokasi, barisList, StandardCharsets.UTF_8);
    }

    /**
     * Membaca file teks dan membentuk kembali kumpulan objek Produk ke dalam KatalogProduk.
     * Menerapkan prinsip ATOMIC/SAFE LOAD: Seluruh baris diverifikasi ke dalam temporary list.
     * Jika terjadi kegagalan format/validasi di tengah jalan, katalog lama tetap dipertahankan utuh.
     */
    public void muat(Path lokasi, KatalogProduk katalog) throws IOException {
        if (lokasi == null || katalog == null) {
            throw new IllegalArgumentException("[ERROR] Lokasi file dan katalog tidak boleh null.");
        }
        if (!Files.exists(lokasi)) {
            throw new IOException("Berkas tidak ditemukan pada lokasi: " + lokasi.toAbsolutePath());
        }

        List<String> daftarBaris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        List<Produk> dataBaru = new ArrayList<>();

        int nomorBaris = 0;
        for (String satuBaris : daftarBaris) {
            nomorBaris++;
            if (satuBaris.trim().isEmpty()) {
                continue; // Lewati baris kosong
            }

            String[] kolom = satuBaris.split("\\|", -1);
            if (kolom.length != 6) {
                throw new IllegalArgumentException("[ERROR FORMAT] Baris ke-" + nomorBaris + " tidak valid (harus 6 kolom): '" + satuBaris + "'");
            }

            String kode = kolom[0].trim();
            String nama = kolom[1].trim();
            String strHarga = kolom[2].trim();
            String strStok = kolom[3].trim();
            String kategori = kolom[4].trim();
            String extra = kolom[5].trim();

            int harga;
            int stok;
            try {
                harga = Integer.parseInt(strHarga);
                stok = Integer.parseInt(strStok);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[ERROR PARSING] Baris ke-" + nomorBaris + " harga/stok bukan bilangan bulat: " + e.getMessage());
            }

            // Validasi keunikan kode dalam berkas
            for (Produk p : dataBaru) {
                if (p.getKode().equalsIgnoreCase(kode)) {
                    throw new IllegalArgumentException("[ERROR DUPLIKAT] Kode produk '" + kode + "' duplikat pada baris ke-" + nomorBaris);
                }
            }

            // Rekonstruksi objek polimorfis
            if (kategori.equalsIgnoreCase("Makanan")) {
                dataBaru.add(new ProdukMakanan(kode, nama, harga, stok, extra));
            } else if (kategori.equalsIgnoreCase("Elektronik")) {
                int garansi;
                try {
                    garansi = Integer.parseInt(extra);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("[ERROR GARANSI] Baris ke-" + nomorBaris + " garansi bulan harus angka: " + extra);
                }
                dataBaru.add(new ProdukElektronik(kode, nama, harga, stok, garansi));
            } else {
                throw new IllegalArgumentException("[ERROR KATEGORI] Baris ke-" + nomorBaris + " kategori '" + kategori + "' tidak dikenali.");
            }
        }

        // SWAP ATOMIK: Hanya digantikan jika seluruh baris sukses diproses
        katalog.gantiSemuaProduk(dataBaru);
    }
}
