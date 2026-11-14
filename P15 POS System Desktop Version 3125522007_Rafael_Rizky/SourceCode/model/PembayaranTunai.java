package model;

import model.exception.PembayaranKurangException;

/**
 * Concrete Strategy: Pembayaran Tunai (Cash)
 * Menerima uang fisik kertas/logam dan menghitung uang kembalian.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class PembayaranTunai implements MetodePembayaran {
    private double uangDiterima;
    private double kembalian;

    @Override
    public String getNamaMetode() {
        return "TUNAI";
    }

    @Override
    public void bayar(double tagihan, double nominalMasuk) throws PembayaranKurangException {
        if (nominalMasuk < tagihan) {
            throw new PembayaranKurangException("TRX-CASH", tagihan, nominalMasuk);
        }
        this.uangDiterima = nominalMasuk;
        this.kembalian = nominalMasuk - tagihan;
    }

    @Override
    public double getKembalian() {
        return kembalian;
    }

    @Override
    public String getInformasiTransaksi() {
        return String.format("Tunai (Cash): Diterima Rp %,.0f | Kembali Rp %,.0f", uangDiterima, kembalian);
    }
}
