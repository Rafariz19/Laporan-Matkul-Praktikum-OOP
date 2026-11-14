package model;

/**
 * Class User
 * Merepresentasikan akun pengguna terotentikasi dalam POS Kasir.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class User {
    private final String username;
    private final String saltHex;
    private final String hashHex;
    private final Role role;

    public User(String username, String saltHex, String hashHex, Role role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Username tidak boleh kosong.");
        }
        if (saltHex == null || saltHex.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Salt tidak boleh kosong.");
        }
        if (hashHex == null || hashHex.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Hash tidak boleh kosong.");
        }
        if (role == null) {
            throw new IllegalArgumentException("[ERROR VALIDASI] Role tidak boleh null.");
        }
        this.username = username.trim();
        this.saltHex = saltHex.trim();
        this.hashHex = hashHex.trim();
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getSaltHex() {
        return saltHex;
    }

    public String getHashHex() {
        return hashHex;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
