package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Class LaporanPenjualan - Analisis dan Agregasi Keuangan Harian (Fase 1)
 * Menghitung rekapitulasi penjualan per tanggal:
 * - Jumlah nota LUNAS
 * - Total omzet bersih
 * - Total diskon promosi & member
 * - Total uang tunai diterima
 * - Jumlah nota VOID (dikecualikan dari omzet)
 * - Top 5 produk terlaris berdasarkan kuantitas
 * - Menangani transaksi lama tanpa rincian item (backward compatible)
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class LaporanPenjualan {

    public static class ItemTerlaris {
        private final String kodeProduk;
        private final String namaProduk;
        private final int totalQty;
        private final long totalNominal;

        public ItemTerlaris(String kodeProduk, String namaProduk, int totalQty, long totalNominal) {
            this.kodeProduk = kodeProduk;
            this.namaProduk = namaProduk;
            this.totalQty = totalQty;
            this.totalNominal = totalNominal;
        }

        public String getKodeProduk() { return kodeProduk; }
        public String getNamaProduk() { return namaProduk; }
        public int getTotalQty() { return totalQty; }
        public long getTotalNominal() { return totalNominal; }
    }

    private final String filterTanggal;
    private int jumlahNotaLunas;
    private double totalOmzet;
    private double totalDiskon;
    private double totalTunai;
    private int jumlahNotaVoid;
    private final List<ItemTerlaris> produkTerlarisTop5 = new ArrayList<>();
    private final List<String[]> daftarNotaHarian = new ArrayList<>();
    private boolean adaTransaksiTanpaRincian = false;

    public LaporanPenjualan(String filterTanggal) {
        this.filterTanggal = (filterTanggal != null && !filterTanggal.trim().isEmpty()) ? filterTanggal.trim() : "";
    }

    public static LaporanPenjualan buatLaporan(Path pathTransaksi, Path pathTransaksiItem, String filterTanggal) throws IOException {
        LaporanPenjualan lap = new LaporanPenjualan(filterTanggal);
        lap.prosesData(pathTransaksi, pathTransaksiItem);
        return lap;
    }

    private void prosesData(Path pathTransaksi, Path pathTransaksiItem) throws IOException {
        if (pathTransaksi == null || !Files.exists(pathTransaksi)) {
            return;
        }

        Set<String> idNotaLunasHariIni = new HashSet<>();
        List<String> linesTrx = Files.readAllLines(pathTransaksi, StandardCharsets.UTF_8);

        for (String line : linesTrx) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] p = trimmed.split("\\|");
            if (p.length < 8) continue;

            String idTrx = p[0];
            String tglStr = p[1]; // Format: DD-MM-YYYY HH:mm
            String namaPlg = p[2];
            String tipeMbr = p[3];
            double totalBayar = Double.parseDouble(p[4]);
            double tunai = Double.parseDouble(p[5]);
            double kembali = Double.parseDouble(p[6]);
            String status = p[7].trim().toUpperCase();
            String kasir = (p.length >= 9) ? p[8] : "system";

            // Saring berdasarkan tanggal (jika filterTanggal diisi)
            if (!filterTanggal.isEmpty()) {
                String tglHanya = tglStr.split(" ")[0]; // Ambil DD-MM-YYYY
                if (!tglHanya.equalsIgnoreCase(filterTanggal)) {
                    continue;
                }
            }

            daftarNotaHarian.add(new String[]{idTrx, tglStr, namaPlg, tipeMbr, String.format("%,.0f", totalBayar), status, kasir});

            if ("LUNAS".equals(status)) {
                jumlahNotaLunas++;
                totalOmzet += totalBayar;
                totalTunai += tunai;
                idNotaLunasHariIni.add(idTrx);
            } else if ("VOID".equals(status)) {
                jumlahNotaVoid++;
            }
        }

        // Agregasi item terlaris dari transaksi_item.txt hanya untuk nota LUNAS
        Map<String, int[]> agregatItem = new HashMap<>(); // key -> [qty, nominal]
        Map<String, String> namaItemMap = new HashMap<>();

        if (pathTransaksiItem != null && Files.exists(pathTransaksiItem)) {
            List<String> linesItem = Files.readAllLines(pathTransaksiItem, StandardCharsets.UTF_8);
            Set<String> notaYangPunyaRincian = new HashSet<>();

            for (String line : linesItem) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                String[] p = trimmed.split("\\|");
                if (p.length < 7) continue;

                String idTrx = p[0];
                if (idNotaLunasHariIni.contains(idTrx)) {
                    notaYangPunyaRincian.add(idTrx);
                    String kode = p[1];
                    String nama = p[2];
                    int qty = Integer.parseInt(p[4]);
                    int subtotal = Integer.parseInt(p[6]);

                    namaItemMap.put(kode, nama);
                    int[] data = agregatItem.getOrDefault(kode, new int[]{0, 0});
                    data[0] += qty;
                    data[1] += subtotal;
                    agregatItem.put(kode, data);
                }
            }

            if (notaYangPunyaRincian.size() < idNotaLunasHariIni.size()) {
                adaTransaksiTanpaRincian = true;
            }
        } else {
            if (!idNotaLunasHariIni.isEmpty()) {
                adaTransaksiTanpaRincian = true;
            }
        }

        // Urutkan seluruh produk terlaris berdasarkan total kuantitas terjual
        List<Map.Entry<String, int[]>> listSorted = new ArrayList<>(agregatItem.entrySet());
        listSorted.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));

        for (Map.Entry<String, int[]> e : listSorted) {
            String kode = e.getKey();
            String nama = namaItemMap.getOrDefault(kode, kode);
            produkTerlarisTop5.add(new ItemTerlaris(kode, nama, e.getValue()[0], e.getValue()[1]));
        }
    }

    public String getFilterTanggal() { return filterTanggal; }
    public int getJumlahNotaLunas() { return jumlahNotaLunas; }
    public double getTotalOmzet() { return totalOmzet; }
    public double getTotalDiskon() { return totalDiskon; }
    public double getTotalTunai() { return totalTunai; }
    public int getJumlahNotaVoid() { return jumlahNotaVoid; }
    public List<ItemTerlaris> getDaftarProdukTerlaris() { return Collections.unmodifiableList(produkTerlarisTop5); }
    public List<ItemTerlaris> getProdukTerlarisTop5() { return getDaftarProdukTerlaris(); }
    public List<String[]> getDaftarNotaHarian() { return Collections.unmodifiableList(daftarNotaHarian); }
    public boolean isAdaTransaksiTanpaRincian() { return adaTransaksiTanpaRincian; }
}
