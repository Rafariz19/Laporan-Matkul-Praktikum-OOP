package model;

import model.exception.PembayaranKurangException;

/**
 * Interface MetodePembayaran (Pola Desain Strategy - Modul OOP / Fase 2)
 * Menyediakan kontrak standar untuk berbagai strategi pemrosesan pembayaran POS.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public interface MetodePembayaran {

    /**
     * Mendapatkan nama identitas metode pembayaran.
     */
    String getNamaMetode();

    /**
     * Memproses pembayaran dengan validasi fail-fast terhadap tagihan.
     * 
     * @param tagihan      total tagihan akhir
     * @param nominalMasuk jumlah dana yang diserahkan/diotorisasi
     * @throws PembayaranKurangException jika dana kurang dari tagihan
     */
    void bayar(double tagihan, double nominalMasuk) throws PembayaranKurangException;

    /**
     * Mendapatkan nominal uang kembalian (jika ada).
     */
    double getKembalian();

    /**
     * Mendapatkan rincian deskripsi transaksi pembayaran untuk struk.
     */
    String getInformasiTransaksi();
}
