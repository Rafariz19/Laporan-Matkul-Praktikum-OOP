package view;

import java.awt.Color;

/**
 * TemaWarna - Apple Modern Color Palette System
 * Berdasarkan palet resmi Apple Modern (Stickdazzle):
 * 1. #F5F5F7 : Apple Off-White / Canvas Surface
 * 2. #1D1D1F : Apple Space Black / Deep Charcoal
 * 3. #AAAAAA : Apple Silver Gray / Divider & Secondary Neutral
 * 4. #007AFF : Apple Vibrant Blue / Primary Call-to-Action & Focus Accent
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public final class TemaWarna {
    private TemaWarna() {}

    // --- EMPAT WARNA POKOK APPLE MODERN ---
    public static final Color APPLE_CANVAS = new Color(0xF5, 0xF5, 0xF7); // #F5F5F7 (Latar Canvas / Surface)
    public static final Color APPLE_BLACK  = new Color(0x1D, 0x1D, 0x1F); // #1D1D1F (Deep Space Black)
    public static final Color APPLE_GRAY   = new Color(0xAA, 0xAA, 0xAA); // #AAAAAA (Silver Gray / Border / Neutral)
    public static final Color APPLE_BLUE   = new Color(0x00, 0x7A, 0xFF); // #007AFF (Apple Vibrant Blue / CTA)

    // --- PEMETAAN PERAN KOMPONEN (ROLE MAPPING) ---
    public static final Color HEADER_BG       = APPLE_BLACK;  // #1D1D1F
    public static final Color TOMBOL_UTAMA    = APPLE_BLACK;  // #1D1D1F (Tombol aksi fungsional)
    public static final Color TOMBOL_HEADER   = APPLE_BLUE;   // #007AFF (Navigasi / fitur atas)
    public static final Color TOMBOL_NETRAL   = APPLE_GRAY;   // #AAAAAA (Reset / Batal)
    public static final Color TOMBOL_BATAL    = APPLE_BLACK;  // #1D1D1F (Tombol pembatalan/hapus elegan)
    public static final Color TOMBOL_CHECKOUT = APPLE_BLUE;   // #007AFF (Primary Call-to-Action)

    // Teks & Aksen
    public static final Color TEKS_PUTIH      = Color.WHITE;
    public static final Color TEKS_GELAP      = APPLE_BLACK;
    public static final Color TEKS_MUTED      = APPLE_GRAY;
    public static final Color TEKS_SUBTITLE   = new Color(0xD1, 0xD1, 0xD6); // Apple Light Silver
    public static final Color BADGE_KASIR     = APPLE_BLUE;
    public static final Color BORDER_DIVIDER  = APPLE_GRAY;
}
