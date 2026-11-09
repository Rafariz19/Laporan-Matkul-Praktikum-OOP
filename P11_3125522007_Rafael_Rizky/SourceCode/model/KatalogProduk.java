package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class KatalogProduk - Pengelola Kumpulan Objek Produk (Modul 11: Collection)
 * Mengelola koleksi List<Produk> dengan identitas unik (Kode Produk).
 * Menerapkan enkapsulasi ketat sehingga koleksi internal tidak dapat dimutasi langsung oleh class lain.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Dosen       : Nirwana Haidar Hari, S.Pd., M.Kom.
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class KatalogProduk {

    // Koleksi internal dienkapsulasi secara private dan final
    private final List<Produk> daftarProduk = new ArrayList<>();

    public KatalogProduk() {
    }

    /**
     * Menambahkan produk baru ke dalam katalog.
     * Validasi: Objek tidak boleh null dan kode produk harus unik (belum pernah terdaftar).
     */
    public void tambahProduk(Produk produk) {
        if (produk == null) {
            throw new IllegalArgumentException("[ERROR] Objek produk tidak boleh null.");
        }
        if (cariProduk(produk.getKode()) != null) {
            throw new IllegalArgumentException("[ERROR] Kode produk '" + produk.getKode() + "' sudah terdaftar dalam katalog!");
        }
        daftarProduk.add(produk);
    }

    /**
     * Mencari produk berdasarkan kode unik.
     * Mengembalikan objek Produk jika ditemukan, atau null jika tidak ada.
     */
    public Produk cariProduk(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            return null;
        }
        for (Produk p : daftarProduk) {
            if (p.getKode().equalsIgnoreCase(kode.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Mencari produk berdasarkan potongan nama (case-insensitive).
     */
    public List<Produk> cariProdukByNama(String keyword) {
        List<Produk> hasil = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return hasil;
        }
        String kwLower = keyword.trim().toLowerCase();
        for (Produk p : daftarProduk) {
            if (p.getNama().toLowerCase().contains(kwLower)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    /**
     * Memperbarui / mengedit atribut data produk yang sudah terdaftar di katalog.
     * Menerapkan prinsip Atomic Invariant: Validasi menyeluruh dilakukan SEBELUM mutasi state.
     * Jika terjadi kegagalan validasi, state objek lama dijamin 100% tidak berubah.
     * 
     * @param kode       kode produk yang akan diedit (sebagai kunci identitas unik)
     * @param namaBaru   nama baru produk
     * @param hargaBaru  harga satuan baru produk
     * @param stokBaru   jumlah stok baru produk
     * @param extraBaru  info khusus (tanggal kadaluarsa untuk makanan, durasi garansi untuk elektronik)
     * @return true jika produk berhasil diperbarui, false jika kode tidak ditemukan
     * @throws IllegalArgumentException jika parameter melanggar invariant bisnis
     */
    public boolean ubahProduk(String kode, String namaBaru, int hargaBaru, int stokBaru, String extraBaru) {
        Produk p = cariProduk(kode);
        if (p == null) {
            return false;
        }

        // --- PRE-VALIDASI KETAT (ATOMIC INVARIANCE MODUL 10 & 11) ---
        if (namaBaru == null || namaBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nama produk baru tidak boleh kosong atau null!");
        }
        if (hargaBaru <= 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Harga produk harus lebih besar dari 0! (Ditolak: Rp " + hargaBaru + ")");
        }
        if (stokBaru < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Stok produk tidak boleh negatif! (Diberikan: " + stokBaru + " unit)");
        }

        int parsedGaransi = 0;
        if (p instanceof ProdukMakanan) {
            if (extraBaru == null || extraBaru.trim().isEmpty()) {
                throw new IllegalArgumentException("[ERROR VALIDASI] Tanggal kadaluarsa tidak boleh kosong!");
            }
        } else if (p instanceof ProdukElektronik) {
            if (extraBaru == null || extraBaru.trim().isEmpty()) {
                throw new IllegalArgumentException("[ERROR VALIDASI] Masa garansi tidak boleh kosong!");
            }
            try {
                parsedGaransi = Integer.parseInt(extraBaru.trim());
                if (parsedGaransi < 0) {
                    throw new IllegalArgumentException("[ERROR VALIDASI] Masa garansi baru tidak boleh negatif! (Ditolak: " + parsedGaransi + " bulan)");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[ERROR VALIDASI] Durasi garansi harus berupa angka bulat! (Diberikan: '" + extraBaru + "')");
            }
        }

        // --- MUTASI HANYA SETELAH SELURUH VALIDASI LOLOS ---
        p.ubahNama(namaBaru);
        p.ubahHarga(hargaBaru);
        p.ubahStok(stokBaru);

        if (p instanceof ProdukMakanan) {
            ((ProdukMakanan) p).ubahTanggalKadaluarsa(extraBaru.trim());
        } else if (p instanceof ProdukElektronik) {
            ((ProdukElektronik) p).ubahGaransiBulan(parsedGaransi);
        }

        return true;
    }

    public boolean hapusProduk(String kode) {
        Produk p = cariProduk(kode);
        if (p != null) {
            return daftarProduk.remove(p);
        }
        return false;
    }

    /**
     * Menampilkan seluruh isi katalog produk dalam format tabel rapi.
     */
    public void tampilkanKatalog() {
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-6s | %-26s | %-12s | %-14s | %-6s | %-20s |\n",
                "KODE", "NAMA PRODUK", "KATEGORI", "HARGA (RP)", "STOK", "INFO KHUSUS");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        if (daftarProduk.isEmpty()) {
            System.out.println("|                                    [KATALOG MASIH KOSONG / BELUM ADA DATA]                            |");
        } else {
            for (Produk p : daftarProduk) {
                String infoKhusus = "-";
                if (p instanceof ProdukMakanan) {
                    infoKhusus = "Exp: " + ((ProdukMakanan) p).getTanggalKadaluarsa();
                } else if (p instanceof ProdukElektronik) {
                    infoKhusus = "Garansi: " + ((ProdukElektronik) p).getGaransiBulan() + " Bln";
                }
                System.out.printf("| %-6s | %-26s | %-12s | Rp %,11d | %-6d | %-20s |\n",
                        p.getKode(),
                        p.getNama(),
                        (p instanceof ProdukMakanan ? "Makanan" : "Elektronik"),
                        p.getHarga(),
                        p.getStok(),
                        infoKhusus);
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("Total Produk Terdaftar: %d item | Total Nilai Aset: Rp %,d\n",
                daftarProduk.size(), hitungTotalNilaiInventaris());
        System.out.println();
    }

    /**
     * Menghitung total nilai seluruh inventaris produk di katalog.
     */
    public long hitungTotalNilaiInventaris() {
        long total = 0;
        for (Produk p : daftarProduk) {
            total += p.hitungNilaiInventaris();
        }
        return total;
    }

    /**
     * Mengembalikan jumlah item dalam collection.
     */
    public int getJumlahProduk() {
        return daftarProduk.size();
    }

    /**
     * Memeriksa apakah katalog kosong.
     */
    public boolean isKosong() {
        return daftarProduk.isEmpty();
    }

    /**
     * Mengembalikan tampilan read-only dari collection untuk mencegah modifikasi eksternal liar.
     */
    public List<Produk> getDaftarProdukReadOnly() {
        return Collections.unmodifiableList(daftarProduk);
    }
}
