package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TransaksiItemRepository - Persistensi Rincian Item Transaksi (Modul 12 / Fase 1)
 * Menyimpan rincian belanjaan tiap nota secara granular menggunakan nilai snapshot:
 * NO_TRX|KODE_PRODUK|NAMA_PRODUK|HARGA_SATUAN|QTY|DISKON_ITEM|SUBTOTAL
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class TransaksiItemRepository {

    public TransaksiItemRepository() {
    }

    /**
     * Menyimpan seluruh baris item belanjaan dari transaksi yang telah lunas ke transaksi_item.txt.
     */
    public void simpanItemTransaksi(Path lokasi, Transaksi trx) throws IOException {
        if (lokasi == null || trx == null) {
            throw new IllegalArgumentException("[ERROR] Lokasi dan transaksi tidak boleh null.");
        }

        List<String> lines = new ArrayList<>();
        String idTrx = trx.getIdTransaksi();

        for (ItemTransaksi item : trx.getDaftarItem()) {
            String kode = item.getProduk().getKode();
            String nama = item.getNamaProduk();
            int harga = item.getHargaSatuan();
            int qty = item.getJumlahBeli();
            double disc = item.getDiskonItem();
            int subtotal = item.hitungSubtotal();

            // Sanitasi delimiter
            for (String val : new String[]{idTrx, kode, nama}) {
                if (val.contains("|") || val.contains("\n") || val.contains("\r")) {
                    throw new IllegalArgumentException("[ERROR SANITASI] Data rincian transaksi mengandung '|': " + val);
                }
            }

            lines.add(idTrx + "|" + kode + "|" + nama + "|" + harga + "|" + qty + "|" + String.format("%.2f", disc) + "|" + subtotal);
        }

        if (lokasi.getParent() != null) {
            Files.createDirectories(lokasi.getParent());
        }

        if (Files.exists(lokasi)) {
            Files.write(lokasi, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } else {
            Files.write(lokasi, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        }
    }

    /**
     * Membaca seluruh item belanja berdasarkan ID transaksi tertentu.
     * Mengembalikan list data rincian [kode, nama, hargaSatuan, qty, diskon, subtotal].
     */
    public List<String[]> muatItemByTransaksi(Path lokasi, String idTransaksi) throws IOException {
        List<String[]> hasil = new ArrayList<>();
        if (lokasi == null || !Files.exists(lokasi) || idTransaksi == null) {
            return hasil;
        }

        List<String> allLines = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        for (String line : allLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] parts = trimmed.split("\\|");
            if (parts.length >= 7 && parts[0].equalsIgnoreCase(idTransaksi.trim())) {
                hasil.add(new String[]{parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]});
            }
        }
        return hasil;
    }
}
