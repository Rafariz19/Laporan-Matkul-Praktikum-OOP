package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FileIoUtil - Utility Operasi Berkas I/O Andal & Crash-Safe (Fase 3)
 * Menerapkan:
 * 1. Safe Atomic Write: Tulis ke berkas temporer .tmp di direktori yang sama,
 *    lalu pindahkan secara atomik via Files.move(ATOMIC_MOVE, REPLACE_EXISTING).
 *    Fallback ke REPLACE_EXISTING jika filesystem tidak mendukung ATOMIC_MOVE.
 * 2. Auto-Backup Berstempel Waktu dengan Rotasi Otomatis (Maksimal N = 5 file cadangan).
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public final class FileIoUtil {
    public static final int MAX_BACKUP_ROTASI = 5;
    private static final DateTimeFormatter BACKUP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    private FileIoUtil() {
        // Private constructor for utility class
    }

    /**
     * Menulis daftar baris teks secara atomik dan aman dari risiko crash/kerusakan berkas.
     * 
     * @param targetFile path berkas tujuan
     * @param lines      koleksi baris teks yang akan ditulis
     * @throws IOException jika penulisan atau atomic move gagal
     */
    public static void atomicWriteLines(Path targetFile, List<String> lines) throws IOException {
        if (targetFile == null || lines == null) {
            throw new IllegalArgumentException("[ERROR] targetFile dan lines tidak boleh null!");
        }

        Path parent = targetFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // 1. Buat backup otomatis jika targetFile sudah ada sebelumnya
        if (Files.exists(targetFile)) {
            buatBackupDanRotasi(targetFile, MAX_BACKUP_ROTASI);
        }

        // 2. Tulis data ke temporary file di direktori yang sama (agar pada filesystem/partisi sama)
        Path tempFile = (parent != null)
                ? Files.createTempFile(parent, targetFile.getFileName().toString() + "_tmp_", ".tmp")
                : Files.createTempFile(targetFile.getFileName().toString() + "_tmp_", ".tmp");

        try {
            Files.write(tempFile, lines, StandardCharsets.UTF_8);

            // 3. Pindahkan berkas temp ke targetFile secara ATOMIC_MOVE
            try {
                Files.move(tempFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback jika volume/filesystem lokal tidak mendukung atomic move
                Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            // Bersihkan tempFile jika masih tertinggal akibat pengecualian
            try {
                if (Files.exists(tempFile)) {
                    Files.deleteIfExists(tempFile);
                }
            } catch (IOException ignored) {}
        }
    }

    /**
     * Menambahkan satu baris secara aman ke berkas log (append mode).
     * Jika berkas belum ada, akan dibuat otomatis bersama direktori induknya.
     */
    public static synchronized void appendLine(Path targetFile, String line) throws IOException {
        if (targetFile == null || line == null) {
            throw new IllegalArgumentException("[ERROR] targetFile dan line tidak boleh null!");
        }
        Path parent = targetFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> s = new ArrayList<>();
        s.add(line);
        Files.write(targetFile, s, StandardCharsets.UTF_8, 
                    java.nio.file.StandardOpenOption.CREATE, 
                    java.nio.file.StandardOpenOption.WRITE, 
                    java.nio.file.StandardOpenOption.APPEND);
    }

    /**
     * Membuat berkas backup dengan stempel waktu di subfolder backup/ dan membatasi jumlah maksimal N berkas.
     * Contoh: data/produk.txt -> data/backup/produk_20261113_220500123.txt
     * 
     * @param targetFile berkas asli
     * @param maxRotasi  jumlah file backup maksimal yang dipertahankan
     */
    public static void buatBackupDanRotasi(Path targetFile, int maxRotasi) {
        if (targetFile == null || !Files.exists(targetFile)) {
            return;
        }

        try {
            Path parent = targetFile.getParent();
            Path backupDir = (parent != null) ? parent.resolve("backup") : Path.of("backup");
            Files.createDirectories(backupDir);

            String fileName = targetFile.getFileName().toString();
            String prefix = fileName;
            String suffix = "";
            int dotIdx = fileName.lastIndexOf('.');
            if (dotIdx > 0) {
                prefix = fileName.substring(0, dotIdx);
                suffix = fileName.substring(dotIdx);
            }

            String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
            Path destBackup = backupDir.resolve(prefix + "_" + timestamp + suffix);

            // Salin berkas asli ke backup
            Files.copy(targetFile, destBackup, StandardCopyOption.REPLACE_EXISTING);

            // Rotasi: hapus backup tertua jika jumlah berkas melebihi maxRotasi
            final String filterPrefix = prefix + "_";
            final String filterSuffix = suffix;
            try (Stream<Path> stream = Files.list(backupDir)) {
                List<Path> matchingBackups = stream
                        .filter(p -> {
                            String f = p.getFileName().toString();
                            return f.startsWith(filterPrefix) && f.endsWith(filterSuffix);
                        })
                        .sorted(Comparator.comparingLong(p -> {
                            try {
                                return Files.getLastModifiedTime(p).toMillis();
                            } catch (IOException e) {
                                return 0L;
                            }
                        }))
                        .collect(Collectors.toList());

                while (matchingBackups.size() > maxRotasi) {
                    Path tertua = matchingBackups.remove(0);
                    Files.deleteIfExists(tertua);
                }
            }
        } catch (Exception ex) {
            System.err.println("[WARN AUTO-BACKUP] Gagal membuat cadangan untuk " + targetFile + ": " + ex.getMessage());
        }
    }
}
