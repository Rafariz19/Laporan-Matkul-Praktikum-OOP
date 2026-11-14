package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pengujian 11 (Fase 1): Pembatalan Nota LUNAS (VOID Nota)
 * Menguji kode produksi:
 * 1. Pembatalan nota berstatus LUNAS: stok kembali utuh ke katalog.
 * 2. Penolakan VOID ganda (StatusTransaksiInvalidException).
 * 3. Penolakan VOID oleh non-admin (AksesDitolakException).
 * 4. Pengecualian nota VOID dari omzet laporan penjualan.
 */
public class TestVoidTransaction {
    public static void run() {
        System.out.println("--- [TEST 11] VOID Nota Penjualan & Pemulihan Inventaris ---");
        try {
            Path fileTrx = Files.createTempFile("trx_void_", ".txt");
            Path fileItem = Files.createTempFile("item_void_", ".txt");
            Path fileUsers = Files.createTempFile("users_void_", ".txt");
            fileTrx.toFile().deleteOnExit();
            fileItem.toFile().deleteOnExit();
            fileUsers.toFile().deleteOnExit();

            AuthService auth = new AuthService(fileUsers);
            User admin = auth.inisialisasiAdminAwal("adminPassword");
            User kasir = auth.register("kasir1", "kasirPassword", Role.KASIR);

            KatalogProduk katalog = new KatalogProduk();
            Produk mouse = new ProdukElektronik("EL01", "Mouse Optik", 100000, 20, 12);
            katalog.tambahProduk(mouse);

            TransaksiFileRepository trxRepo = new TransaksiFileRepository();
            TransaksiItemRepository itemRepo = new TransaksiItemRepository();

            Pelanggan cust = new Pelanggan("CUST-V", "Hendra", "081234567890", "REGULER");
            Transaksi trx = new Transaksi("TRX-VOID-01", "10-11-2026 11:00", cust);
            trx.tambahItem(mouse, 5); // Stok RAM tinggal 15
            trx.prosesTransaksi(500000); // LUNAS

            trxRepo.catatTransaksi(fileTrx, trx, 500000, 0, "kasir1");
            itemRepo.simpanItemTransaksi(fileItem, trx);
            System.out.println("  1. Transaksi TRX-VOID-01 lunas. Stok sisa di katalog: " + mouse.getStok());

            // 1. Uji Jalur Gagal: Kasir mencoba VOID nota -> DITOLAK
            boolean nonAdminDitolak = false;
            try {
                trxRepo.voidTransaksi(fileTrx, fileItem, "TRX-VOID-01", katalog, kasir, itemRepo);
            } catch (AksesDitolakException ex) {
                nonAdminDitolak = true;
                System.out.println("  2. Operasi VOID oleh non-admin DITOLAK: " + ex.getMessage());
            }
            if (!nonAdminDitolak) {
                throw new AssertionError("Operasi VOID oleh non-admin seharusnya ditolak!");
            }

            // 2. Operasi VOID oleh ADMIN -> SUKSES & STOK KEMBALI
            trxRepo.voidTransaksi(fileTrx, fileItem, "TRX-VOID-01", katalog, admin, itemRepo);
            if (mouse.getStok() != 20) {
                throw new AssertionError("Stok tidak kembali setelah VOID! Stok saat ini: " + mouse.getStok());
            }
            System.out.println("  3. Operasi VOID oleh ADMIN berhasil. Stok pulih kembali ke: " + mouse.getStok());

            // 3. Uji Jalur Gagal: VOID ganda -> DITOLAK
            boolean voidGandaDitolak = false;
            try {
                trxRepo.voidTransaksi(fileTrx, fileItem, "TRX-VOID-01", katalog, admin, itemRepo);
            } catch (StatusTransaksiInvalidException ex) {
                voidGandaDitolak = true;
                System.out.println("  4. Percobaan VOID ganda berhasil DITOLAK: " + ex.getMessage());
            }
            if (!voidGandaDitolak) {
                throw new AssertionError("VOID ganda seharusnya ditolak!");
            }

            // 4. Verifikasi Laporan Penjualan: Nota VOID tidak dihitung sebagai omzet
            LaporanPenjualan lap = LaporanPenjualan.buatLaporan(fileTrx, fileItem, "10-11-2026");
            if (lap.getJumlahNotaLunas() != 0 || lap.getTotalOmzet() != 0.0 || lap.getJumlahNotaVoid() != 1) {
                throw new AssertionError("Laporan penjualan salah menghitung nota VOID! Omzet: " + lap.getTotalOmzet() + ", Void: " + lap.getJumlahNotaVoid());
            }
            System.out.println("  5. Laporan penjualan mengecualikan nota VOID dari omzet (Omzet = Rp 0, Nota VOID = 1).");

            System.out.println("✔ [LULUS] Siklus hidup VOID, proteksi peran, dan pemulihan stok terverifikasi penuh.\n");
            Files.deleteIfExists(fileTrx);
            Files.deleteIfExists(fileItem);
            Files.deleteIfExists(fileUsers);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 11] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
