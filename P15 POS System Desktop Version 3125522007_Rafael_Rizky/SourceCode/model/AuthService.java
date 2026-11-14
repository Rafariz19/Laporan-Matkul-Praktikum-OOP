package model;

import model.exception.AksesDitolakException;
import model.exception.KasirException;
import model.exception.OtentikasiException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Class AuthService
 * Layanan Otentikasi dan Manajemen Pengguna Lokal (Zero External Dependency).
 * Menggunakan salt acak via SecureRandom dan hashing SHA-256 via MessageDigest.
 * Tidak ada password plain-text yang disimpan ke media penyimpanan.
 * 
 * Format data/users.txt:
 * USERNAME|SALT_HEX|HASH_HEX|ROLE
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class AuthService {
    private final Path userFilePath;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(Path userFilePath) {
        if (userFilePath == null) {
            throw new IllegalArgumentException("[ERROR] Path user file tidak boleh null.");
        }
        this.userFilePath = userFilePath;
    }

    /**
     * Memeriksa apakah berkas users.txt belum dibuat atau masih kosong tanpa akun.
     */
    public boolean isUsersFileEmpty() {
        if (!Files.exists(userFilePath)) {
            return true;
        }
        try {
            List<String> lines = Files.readAllLines(userFilePath, StandardCharsets.UTF_8);
            for (String l : lines) {
                if (!l.trim().isEmpty() && !l.trim().startsWith("#")) {
                    return false;
                }
            }
        } catch (IOException e) {
            return true;
        }
        return true;
    }

    /**
     * Inisialisasi akun Administrator utama saat aplikasi pertama kali dijalankan.
     */
    public User inisialisasiAdminAwal(String password) throws KasirException, IOException {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Password admin pertama tidak boleh kosong!");
        }
        return register("admin", password.trim(), Role.ADMIN);
    }

    /**
     * Mendaftarkan pengguna baru dengan salt acak dan hash SHA-256.
     */
    public User register(String username, String password, Role role) throws KasirException, IOException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Username tidak boleh kosong.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Password tidak boleh kosong.");
        }
        if (role == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Role tidak boleh null.");
        }

        String userClean = username.trim().toLowerCase();
        if (userClean.contains("|") || userClean.contains("\n") || userClean.contains("\r")) {
            throw new IllegalArgumentException("[ERROR SANITASI] Username mengandung karakter terlarang '|'.");
        }

        // Cek duplikasi username
        if (cariUser(userClean) != null) {
            throw new OtentikasiException("ERR-AUTH-DUPLICATE", "[GAGAL REGISTRASI] Username '" + userClean + "' sudah terdaftar!");
        }

        byte[] saltBytes = new byte[16];
        secureRandom.nextBytes(saltBytes);
        String saltHex = bytesToHex(saltBytes);
        String hashHex = hitungHashSha256(password.trim(), saltHex);

        User newUser = new User(userClean, saltHex, hashHex, role);

        String baris = userClean + "|" + saltHex + "|" + hashHex + "|" + role.name();
        FileIoUtil.appendLine(userFilePath, baris);

        return newUser;
    }

    /**
     * Proses otentikasi login pengguna berdasarkan kredensial yang dimasukkan.
     */
    public User login(String username, String password) throws KasirException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new OtentikasiException("ERR-AUTH-EMPTY", "[GAGAL LOGIN] Username dan password wajib diisi!");
        }

        User u = cariUser(username.trim().toLowerCase());
        if (u == null) {
            throw new OtentikasiException("ERR-AUTH-INVALID", "[GAGAL LOGIN] Username atau password salah!");
        }

        String inputHash = hitungHashSha256(password.trim(), u.getSaltHex());
        if (!inputHash.equalsIgnoreCase(u.getHashHex())) {
            throw new OtentikasiException("ERR-AUTH-INVALID", "[GAGAL LOGIN] Username atau password salah!");
        }

        return u;
    }

    /**
     * Mencari profil user terdaftar di berkas users.txt.
     */
    public User cariUser(String username) {
        if (!Files.exists(userFilePath)) {
            return null;
        }
        try {
            List<String> lines = Files.readAllLines(userFilePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                String[] parts = trimmed.split("\\|");
                if (parts.length >= 4) {
                    if (parts[0].equalsIgnoreCase(username.trim())) {
                        Role r = Role.valueOf(parts[3].trim().toUpperCase());
                        return new User(parts[0], parts[1], parts[2], r);
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Penegakan otorisasi di lapisan model: memastikan pengguna berstatus ADMIN.
     */
    public static void enforceAdmin(User user, String namaOperasi) throws AksesDitolakException {
        if (user == null || !user.isAdmin()) {
            String uname = (user != null) ? user.getUsername() : "ANONYMOUS";
            throw new AksesDitolakException(uname, namaOperasi);
        }
    }

    // --- HELPER CRYPTOGRAPHY (JDK ONLY) ---

    private String hitungHashSha256(String password, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltHex.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritma SHA-256 tidak tersedia di JDK!", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
