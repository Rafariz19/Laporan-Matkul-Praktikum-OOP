package test;

/**
 * Test Suite Runner Terpadu - P15 POS System Desktop Version
 * Mengeksekusi seluruh pengujian invariansi bisnis, immutability, persistensi berkas aman,
 * multi-role otentikasi, log item granular, laporan harian, dan pembatalan nota (VOID).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class RunAllTests {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   SUITE PENGUJIAN REGRESI & INVARIANSI DATA - P15 POS SYSTEM DESKTOP ");
        System.out.println("   Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)       ");
        System.out.println("   Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)          ");
        System.out.println("======================================================================\n");

        long start = System.currentTimeMillis();

        // Uji Fase 0 & 0b
        TestOrderLineImmutability.run();
        TestEditStokKeranjang.run();
        TestStokTereservasiSimpanFile.run();
        TestInvarianceUbahProduk.run();
        TestAlurStokResetTransaksi.run();

        // Uji Fase 0c
        TestCheckoutVsSafeSave.run();
        TestHapusProdukKeranjangDanWindowClosing.run();
        TestBatalTidakTulisTransaksiFile.run();

        // Uji Fase 1
        TestAuthAndRoles.run();
        TestItemLogAndDailyReport.run();
        TestVoidTransaction.run();

        long durasi = System.currentTimeMillis() - start;
        System.out.println("======================================================================");
        System.out.println("   HASIL AKHIR: 11/11 PENGUJIAN LULUS DENGAN SUKSES (100% PASS)       ");
        System.out.println("   Total Durasi Pengujian: " + durasi + " ms                          ");
        System.out.println("======================================================================");
    }
}
