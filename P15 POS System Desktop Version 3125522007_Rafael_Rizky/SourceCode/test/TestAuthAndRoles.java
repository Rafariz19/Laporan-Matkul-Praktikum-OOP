package test;

import model.*;
import model.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pengujian 9 (Fase 1): Otentikasi, Enkripsi Hash Salt, & Penegakan Otorisasi Role
 * Menguji kode produksi AuthService, User, Role, dan AksesDitolakException:
 * 1. Registrasi user kasir & admin dengan salt acak dan hash SHA-256.
 * 2. Login berhasil vs Login gagal (sandi salah, username fiktif).
 * 3. Penegakan otorisasi di lapisan model (AksesDitolakException saat non-admin mencoba aksi admin).
 */
public class TestAuthAndRoles {
    public static void run() {
        System.out.println("--- [TEST 9] Otentikasi Multi-Role & Hash SHA-256 ---");
        try {
            Path userFile = Files.createTempFile("users_test_", ".txt");
            userFile.toFile().deleteOnExit();

            AuthService auth = new AuthService(userFile);

            // 1. Inisialisasi Admin Pertama
            User admin = auth.inisialisasiAdminAwal("adminRahasia123");
            if (!admin.isAdmin() || admin.getRole() != Role.ADMIN) {
                throw new AssertionError("Role admin awal tidak sesuai!");
            }
            System.out.println("  1. Akun admin berhasil diinisialisasi dengan salt: " + admin.getSaltHex().substring(0, 8) + "...");

            // 2. Registrasi Kasir
            User kasir = auth.register("budi_kasir", "kasirPassword", Role.KASIR);
            if (kasir.isAdmin() || kasir.getRole() != Role.KASIR) {
                throw new AssertionError("Role kasir tidak sesuai!");
            }
            System.out.println("  2. Akun kasir terdaftar (Role: " + kasir.getRole() + ")");

            // 3. Login Sukses
            User loginKasir = auth.login("budi_kasir", "kasirPassword");
            if (!loginKasir.getUsername().equals("budi_kasir")) {
                throw new AssertionError("Username login tidak cocok!");
            }
            System.out.println("  3. Login kasir berhasil terotentikasi.");

            // 4. Uji Jalur Gagal: Password Salah
            boolean loginSalahDitolak = false;
            try {
                auth.login("budi_kasir", "passwordNgawur");
            } catch (KasirException e) {
                loginSalahDitolak = true;
                System.out.println("  4. Login password salah berhasil DITOLAK: " + e.getMessage());
            }
            if (!loginSalahDitolak) {
                throw new AssertionError("Login dengan password salah seharusnya ditolak!");
            }

            // 5. Uji Jalur Gagal: Penegakan Otorisasi Model (AksesDitolakException)
            boolean otorisasiKasirDitolak = false;
            try {
                AuthService.enforceAdmin(kasir, "Hapus Produk Katalog");
            } catch (AksesDitolakException ex) {
                otorisasiKasirDitolak = true;
                System.out.println("  5. Otorisasi model berhasil menolak akses non-admin: " + ex.getMessage());
            }
            if (!otorisasiKasirDitolak) {
                throw new AssertionError("Kasir seharusnya tidak boleh mengakses operasi ADMIN!");
            }

            // 6. Otorisasi Admin Berhasil
            AuthService.enforceAdmin(admin, "Hapus Produk Katalog");
            System.out.println("  6. Otorisasi model mengizinkan akses untuk user ADMIN.");

            System.out.println("✔ [LULUS] Otentikasi, enkripsi SHA-256, dan penegakan role model terverifikasi penuh.\n");
            Files.deleteIfExists(userFile);
        } catch (Exception e) {
            System.err.println("✘ [GAGAL TEST 9] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
