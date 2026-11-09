import java.io.*;
import java.util.*;

/**
 * Class Pengelola Koleksi Buku dan Repository I/O
 * Mengelola List<Buku> dengan proteksi invariansi data dan safe loading atomik.
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 */
public class Perpustakaan {
    private final List<Buku> daftarBuku;

    public Perpustakaan() {
        this.daftarBuku = new ArrayList<>();
    }

    public boolean tambahBuku(Buku buku) {
        if (buku == null) {
            System.err.println("Gagal: Objek buku bernilai null.");
            return false;
        }
        if (isKodeTerdaftar(buku.getKode())) {
            System.err.println("Gagal: Kode buku '" + buku.getKode() + "' sudah terdaftar dalam koleksi.");
            return false;
        }
        daftarBuku.add(buku);
        return true;
    }

    public boolean isKodeTerdaftar(String kode) {
        if (kode == null) return false;
        for (Buku b : daftarBuku) {
            if (b.getKode().equalsIgnoreCase(kode.trim())) {
                return true;
            }
        }
        return false;
    }

    public Buku cariBuku(String kode) {
        if (kode == null) return null;
        for (Buku b : daftarBuku) {
            if (b.getKode().equalsIgnoreCase(kode.trim())) {
                return b;
            }
        }
        return null;
    }

    public boolean hapusBuku(String kode) {
        Buku sasaran = cariBuku(kode);
        if (sasaran != null) {
            daftarBuku.remove(sasaran);
            return true;
        }
        return false;
    }

    public List<Buku> getDaftarBuku() {
        return Collections.unmodifiableList(daftarBuku);
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }

    public void kosongkan() {
        daftarBuku.clear();
    }

    // Operasi File dengan Atomic Temporary Collection
    public void simpanKeFile(String pathFile) throws IOException {
        File file = new File(pathFile);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Buku b : daftarBuku) {
                writer.println(b.toFileFormat());
            }
        }
    }

    public void muatDariFile(String pathFile) throws IOException {
        File file = new File(pathFile);
        if (!file.exists()) {
            throw new FileNotFoundException("Berkas tidak ditemukan: " + pathFile);
        }

        List<Buku> tempList = new ArrayList<>();
        Set<String> tempKodes = new HashSet<>();
        int barisKe = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                barisKe++;
                baris = baris.trim();
                if (baris.isEmpty()) continue;

                String[] parts = baris.split("\\|", -1);
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                        String.format("Format baris %d tidak valid: '%s' (harus KODE|JUDUL)", barisKe, baris)
                    );
                }

                String kode = parts[0].trim();
                String judul = parts[1].trim();

                if (kode.isEmpty() || judul.isEmpty()) {
                    throw new IllegalArgumentException(
                        String.format("Data pada baris %d kosong (KODE='%s', JUDUL='%s')", barisKe, kode, judul)
                    );
                }

                if (!tempKodes.add(kode.toUpperCase())) {
                    throw new IllegalArgumentException(
                        String.format("Kode duplikat '%s' terdeteksi pada baris %d", kode, barisKe)
                    );
                }

                tempList.add(new Buku(kode, judul));
            }
        }

        // Transaksi atomik: timpa koleksi hanya bila semua baris sukses
        daftarBuku.clear();
        daftarBuku.addAll(tempList);
    }
}

