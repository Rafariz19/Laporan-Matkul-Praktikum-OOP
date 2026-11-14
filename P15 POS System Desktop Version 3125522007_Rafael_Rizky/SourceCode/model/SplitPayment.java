package model;

import model.exception.PembayaranKurangException;

/**
 * Concrete Strategy: Split Payment (Pembayaran Kombinasi Tunai + QRIS Simulasi)
 * Mengizinkan konsumen melunasi tagihan menggunakan sebagian uang tunai dan sebagian saldo QRIS.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class SplitPayment implements MetodePembayaran {
    private final double nominalTunai;
    private final double nominalQris;
    private double kembalian;

    public SplitPayment(double nominalTunai, double nominalQris) {
        if (nominalTunai < 0 || nominalQris < 0) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Nominal split payment tidak boleh negatif!");
        }
        this.nominalTunai = nominalTunai;
        this.nominalQris = nominalQris;
    }

    @Override
    public String getNamaMetode() {
        return "SPLIT (TUNAI + QRIS)";
    }

    @Override
    public void bayar(double tagihan, double nominalMasuk) throws PembayaranKurangException {
        double totalKombinasi = nominalTunai + nominalQris;
        if (totalKombinasi < tagihan) {
            throw new PembayaranKurangException("TRX-SPLIT", tagihan, totalKombinasi);
        }
        // Kembalian hanya dihitung dari kelebihan tunai
        double sisaTagihanSetelahQris = Math.max(0, tagihan - nominalQris);
        this.kembalian = Math.max(0, nominalTunai - sisaTagihanSetelahQris);
    }

    @Override
    public double getKembalian() {
        return kembalian;
    }

    @Override
    public String getInformasiTransaksi() {
        return String.format("Split Payment: Tunai Rp %,.0f + QRIS [Simulasi] Rp %,.0f | Kembali Rp %,.0f", 
                nominalTunai, nominalQris, kembalian);
    }

    public double getNominalTunai() { return nominalTunai; }
    public double getNominalQris() { return nominalQris; }
}
