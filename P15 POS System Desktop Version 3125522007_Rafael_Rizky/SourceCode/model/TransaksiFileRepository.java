package model;

import model.exception.AksesDitolakException;
import model.exception.KasirException;
import model.exception.StatusTransaksiInvalidException;
import model.exception.TransaksiTidakDitemukanException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TransaksiFileRepository - Pencatatan Log Transaksi ke File (Modul 12 / Fase 1)
 * Menyimpan riwayat nota transaksi penjualan yang sudah LUNAS ke dalam file teks transaksi.txt.
 * Mendukung pencatatan username kasir dan operasi pembatalan nota pasca-pelunasan (VOID).
 * 
 * Format Data Baru (9 Kolom):
 * ID_TRX|TANGGAL|NAMA_PELANGGAN|TIPE_MEMBER|TOTAL_BAYAR|TUNAI|KEMBALIAN|STATUS|KASIR
 * Format Data Lama (8 Kolom - Backward Compatible):
 * ID_TRX|TANGGAL|NAMA_PELANGGAN|TIPE_MEMBER|TOTAL_BAYAR|TUNAI|KEMBALIAN|STATUS (KASIR dianggap 'system')
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class TransaksiFileRepository {

    public TransaksiFileRepository() {
    }

    /**
     * Menambahkan catatan transaksi yang telah lunas dengan username kasir.
     */
    public void catatTransaksi(Path lokasi, Transaksi trx, double tunaiDiterima, double kembalian, String kasirUsername) 
            throws IOException, StatusTransaksiInvalidException {
        if (lokasi == null || trx == null) {
            throw new IllegalArgumentException("[ERROR] Lokasi dan transaksi tidak boleh null.");
        }

        if (!"LUNAS".equals(trx.getStatusTransaksi())) {
            throw new StatusTransaksiInvalidException(trx.getIdTransaksi(), trx.getStatusTransaksi(), "catatTransaksi");
        }

        String id = trx.getIdTransaksi();
        String tgl = trx.getTanggal();
        String plg = (trx.getPelanggan() != null) ? trx.getPelanggan().getNama() : "Umum / Non-Member";
        String member = (trx.getPelanggan() != null) ? trx.getPelanggan().getTipeMember() : "-";
        String total = String.format("%.0f", trx.hitungTotalBayar());
        String bayar = String.format("%.0f", tunaiDiterima);
        String kembali = String.format("%.0f", kembalian);
        String status = trx.getStatusTransaksi();
        String kasir = (kasirUsername != null && !kasirUsername.trim().isEmpty()) ? kasirUsername.trim() : "system";

        // Sanitasi pemisah delimiter
        for (String val : new String[]{id, tgl, plg, member, kasir}) {
            if (val.contains("|") || val.contains("\n") || val.contains("\r")) {
                throw new IllegalArgumentException("[ERROR SANITASI] Data transaksi mengandung karakter terlarang '|': " + val);
            }
        }

        String baris = id + "|" + tgl + "|" + plg + "|" + member + "|" + total + "|" + bayar + "|" + kembali + "|" + status + "|" + kasir;

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

    /**
     * Overload catatTransaksi untuk backward-compatibility jika kasir tidak dispesifikasikan.
     */
    public void catatTransaksi(Path lokasi, Transaksi trx, double tunaiDiterima, double kembalian) 
            throws IOException, StatusTransaksiInvalidException {
        catatTransaksi(lokasi, trx, tunaiDiterima, kembalian, "system");
    }

    /**
     * Membatalkan nota yang sudah LUNAS (VOID Nota).
     * Aturan Bisnis & Validasi:
     * 1. Otorisasi: Wajib memiliki hak akses ADMIN (AuthService.enforceAdmin).
     * 2. Status Invariant: Nota wajib berstatus "LUNAS".
     *    - Jika status sudah "VOID", lempar StatusTransaksiInvalidException (tolak VOID ganda).
     *    - Jika status bukan "LUNAS", lempar StatusTransaksiInvalidException.
     * 3. Pemulihan Stok: Mengembalikan kuantitas tiap item di nota ke katalog produk via tambahStok().
     * 4. State File: Mengubah status nota di file transaksi.txt menjadi "VOID".
     * 5. Rollback Memori: Jika penulisan file gagal, stok dikurangkan kembali ke memori.
     */
    public void voidTransaksi(Path pathTransaksi, Path pathTransaksiItem, String idTransaksi, 
                              KatalogProduk katalog, User user, TransaksiItemRepository itemRepo) 
            throws IOException, KasirException {
        if (pathTransaksi == null || idTransaksi == null || katalog == null) {
            throw new IllegalArgumentException("[ERROR] Parameter voidTransaksi tidak boleh null.");
        }

        // 1. Otorisasi Role Admin
        AuthService.enforceAdmin(user, "VOID Nota Transaksi");

        if (!Files.exists(pathTransaksi)) {
            throw new TransaksiTidakDitemukanException("ALL - Berkas belum ada");
        }

        List<String> lines = Files.readAllLines(pathTransaksi, StandardCharsets.UTF_8);
        int targetIndex = -1;
        String[] targetParts = null;

        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i).trim();
            if (l.isEmpty() || l.startsWith("#")) continue;
            String[] p = l.split("\\|");
            if (p.length >= 8 && p[0].equalsIgnoreCase(idTransaksi.trim())) {
                targetIndex = i;
                targetParts = p;
                break;
            }
        }

        if (targetIndex < 0 || targetParts == null) {
            throw new TransaksiTidakDitemukanException(idTransaksi);
        }

        String currentStatus = targetParts[7].trim().toUpperCase();

        // 2. Validasi Status Nota
        if ("VOID".equalsIgnoreCase(currentStatus)) {
            throw new StatusTransaksiInvalidException(idTransaksi, currentStatus, "voidTransaksi (Tolak VOID ganda - nota sudah berstatus VOID)");
        }
        if (!"LUNAS".equalsIgnoreCase(currentStatus)) {
            throw new StatusTransaksiInvalidException(idTransaksi, currentStatus, "voidTransaksi (Hanya nota berstatus LUNAS yang dapat di-VOID)");
        }

        // 3. Baca rincian item untuk pemulihan stok
        List<String[]> items = (itemRepo != null) ? itemRepo.muatItemByTransaksi(pathTransaksiItem, idTransaksi) : new ArrayList<>();
        List<Produk> prodsRestored = new ArrayList<>();
        List<Integer> qtysRestored = new ArrayList<>();

        for (String[] it : items) {
            String kodeProd = it[0];
            int qty = Integer.parseInt(it[3]);
            Produk prod = katalog.cariProduk(kodeProd);
            if (prod != null) {
                prod.tambahStok(qty);
                prodsRestored.add(prod);
                qtysRestored.add(qty);
            }
        }

        // 4. Update status baris di file transaksi
        targetParts[7] = "VOID";
        StringBuilder updatedLine = new StringBuilder();
        for (int i = 0; i < targetParts.length; i++) {
            if (i > 0) updatedLine.append("|");
            updatedLine.append(targetParts[i]);
        }
        lines.set(targetIndex, updatedLine.toString());

        // 5. Tulis pembaruan ke berkas secara crash-safe atomic write dengan rollback memori jika gagal
        try {
            FileIoUtil.atomicWriteLines(pathTransaksi, lines);
        } catch (IOException ex) {
            // Rollback stok di memori jika I/O gagal
            for (int i = 0; i < prodsRestored.size(); i++) {
                try {
                    prodsRestored.get(i).kurangiStok(qtysRestored.get(i));
                } catch (Exception ignored) {}
            }
            throw new IOException("Gagal memperbarui berkas log saat operasi VOID: " + ex.getMessage(), ex);
        }
    }

    /**
     * Membaca seluruh data baris riwayat transaksi (backward compatible 8 atau 9 kolom).
     */
    public List<String[]> muatSemuaTransaksi(Path pathTransaksi) throws IOException {
        List<String[]> list = new ArrayList<>();
        if (pathTransaksi == null || !Files.exists(pathTransaksi)) {
            return list;
        }

        List<String> lines = Files.readAllLines(pathTransaksi, StandardCharsets.UTF_8);
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] p = trimmed.split("\\|");
            if (p.length >= 8) {
                String id = p[0];
                String tgl = p[1];
                String plg = p[2];
                String mbr = p[3];
                String tot = p[4];
                String byr = p[5];
                String kmb = p[6];
                String stt = p[7];
                String ksr = (p.length >= 9) ? p[8] : "system";
                list.add(new String[]{id, tgl, plg, mbr, tot, byr, kmb, stt, ksr});
            }
        }
        return list;
    }
}
