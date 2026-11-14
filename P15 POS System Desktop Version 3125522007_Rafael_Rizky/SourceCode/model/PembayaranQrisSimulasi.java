package model;

import model.exception.PembayaranKurangException;

/**
 * Concrete Strategy: Pembayaran QRIS Dinamis (HANYA SIMULASI / TANPA GATEWAY)
 * Mensimulasikan pemindaian QRIS standar Bank Indonesia.
 * Tidak ada kembalian uang pada transaksi QRIS (kembalian = 0).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class PembayaranQrisSimulasi implements MetodePembayaran {
    private final String kodeNmid;
    private double nominalDibayar;

    public PembayaranQrisSimulasi() {
        this("NMID-SIM-" + System.currentTimeMillis());
    }

    public PembayaranQrisSimulasi(String kodeNmid) {
        this.kodeNmid = kodeNmid;
    }

    @Override
    public String getNamaMetode() {
        return "QRIS (SIMULASI)";
    }

    @Override
    public void bayar(double tagihan, double nominalMasuk) throws PembayaranKurangException {
        if (nominalMasuk < tagihan) {
            throw new PembayaranKurangException("TRX-QRIS", tagihan, nominalMasuk);
        }
        this.nominalDibayar = tagihan; // QRIS selalu pas dengan tagihan
    }

    @Override
    public double getKembalian() {
        return 0.0; // Transaksi digital QRIS tidak memiliki kembalian
    }

    @Override
    public String getInformasiTransaksi() {
        return String.format("QRIS Dinamis [Simulasi]: %s | Dibayar Rp %,.0f (Pas)", kodeNmid, nominalDibayar);
    }

    public String getKodeNmid() {
        return kodeNmid;
    }
}
