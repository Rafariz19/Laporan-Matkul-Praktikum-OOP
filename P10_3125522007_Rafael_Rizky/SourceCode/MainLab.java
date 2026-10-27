/**
 * Program Praktikum Dasar Modul 10: Exception Handling dan Validasi State Objek
 * Sesuai Langkah Praktikum Langkah 1 - 3 pada Modul 10.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek
 * Mahasiswa   : Rafael Rizky Luga Tanjung
 * NRP         : 3125522007
 * Kampus      : PENS PSDKU Sumenep (2026)
 * 
 * Pengujian berurutan pada objek yang sama:
 * Input: "2", "0", "9", "abc", "-1", "3"
 */

class StokTidakCukupExceptionLab extends Exception {
    public StokTidakCukupExceptionLab(String pesan) {
        super(pesan);
    }
}

class ProdukLab {
    private int stok;

    public ProdukLab(int stokAwal) {
        if (stokAwal < 0) {
            throw new IllegalArgumentException("Stok awal tidak boleh negatif.");
        }
        this.stok = stokAwal;
    }

    public void beli(int jumlah) throws StokTidakCukupExceptionLab {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari nol.");
        }
        if (jumlah > stok) {
            throw new StokTidakCukupExceptionLab("Stok tidak cukup. Tersedia: " + stok);
        }
        // State berubah setelah seluruh validasi berhasil
        this.stok -= jumlah;
    }

    public int getStok() {
        return stok;
    }
}

public class MainLab {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("PRAKTIKUM DASAR MODUL 10 - EXCEPTION HANDLING & VALIDASI STATE OBJEK");
        System.out.println("Nama    : Rafael Rizky Luga Tanjung | NRP: 3125522007");
        System.out.println("Kampus  : PENS PSDKU Sumenep (2026)");
        System.out.println("======================================================================\n");

        ProdukLab produk = new ProdukLab(5);
        System.out.println("[INISIALISASI] Objek Produk dibuat dengan stok awal = " + produk.getStok() + " unit.\n");

        // Daftar input pengujian sesuai Modul 10: "2", "0", "9", "abc", ditambah "-1" dan "3"
        String[] daftarInput = {"2", "0", "9", "abc", "-1", "3"};

        int langkah = 1;
        for (String input : daftarInput) {
            System.out.println("--- Pengujian #" + (langkah++) + " ---");
            System.out.println("Input pembelian : \"" + input + "\"");
            try {
                int jumlah = Integer.parseInt(input);
                produk.beli(jumlah);
                System.out.println("Hasil          : Pembelian berhasil (" + jumlah + " unit dibeli).");
            } catch (NumberFormatException e) {
                System.out.println("Ditolak        : Input harus bilangan bulat.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ditolak        : " + e.getMessage());
            } catch (StokTidakCukupExceptionLab e) {
                System.out.println("Ditolak        : " + e.getMessage());
            } finally {
                System.out.println("Stok akhir     : " + produk.getStok() + " unit");
                System.out.println();
            }
        }

        System.out.println("======================================================================");
        System.out.println("KESIMPULAN PENGUJIAN PRAKTIKUM DASAR:");
        System.out.println("1. Seluruh operasi yang ditolak terbukti TIDAK MENGUBAH stok produk.");
        System.out.println("2. Pembelian boundary '3' (tepat sejumlah sisa stok) berhasil diproses,");
        System.out.println("   menghasilkan stok akhir 0 unit tanpa pelanggaran bisnis.");
        System.out.println("======================================================================");
    }
}
