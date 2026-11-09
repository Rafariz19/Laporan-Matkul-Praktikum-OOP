package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TransaksiFileRepository - Pencatatan Log Transaksi ke File (Modul 12)
 * Menyimpan riwayat nota transaksi penjualan yang sudah lunas ke dalam file teks transaksi.txt.
 * 
 * Format: ID_TRX|TANGGAL|NAMA_PELANGGAN|TIPE_MEMBER|TOTAL_BAYAR|STATUS
 */
public class TransaksiFileRepository {

    public TransaksiFileRepository() {
    }

    /**
     * Menambahkan (append) catatan transaksi yang telah lunas ke dalam file transaksi.txt.
     */
    public void catatTransaksi(Path lokasi, Transaksi trx, double tunaiDiterima, double kembalian) throws IOException {
        if (lokasi == null || trx == null) {
            throw new IllegalArgumentException("[ERROR] Lokasi dan transaksi tidak boleh null.");
        }

        String id = trx.getIdTransaksi();
        String tgl = trx.getTanggal();
        String plg = (trx.getPelanggan() != null) ? trx.getPelanggan().getNama() : "Umum / Non-Member";
        String member = (trx.getPelanggan() != null) ? trx.getPelanggan().getTipeMember() : "-";
        String total = String.format("%.0f", trx.hitungTotalBayar());
        String bayar = String.format("%.0f", tunaiDiterima);
        String kembali = String.format("%.0f", kembalian);
        String status = trx.getStatusTransaksi();

        String baris = id + "|" + tgl + "|" + plg + "|" + member + "|" + total + "|" + bayar + "|" + kembali + "|" + status;

        if (lokasi.getParent() != null) {
            Files.createDirectories(lokasi.getParent());
        }

        List<String> list = List.of(baris);
        if (Files.exists(lokasi)) {
            Files.write(lokasi, list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } else {
            Files.write(lokasi, list, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }
    }
}

