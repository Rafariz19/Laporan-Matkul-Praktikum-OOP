package view;

import java.awt.Color;

/**
 * TemaWarna - Definisi Palet Warna Satu Rasi (Cohesive Harmonious Palette)
 * Mengeliminasi tombol warna-warni yang campur aduk (ungu, oranye, cyan, dll)
 * dan menggantikannya dengan skema warna terpadu berbasis Navy Slate / Corporate Indigo.
 * 
 * Filosofi Desain:
 * 1. Primary Unified Tone : Slate Navy (#243B5A) untuk seluruh tombol fungsional utama & header.
 * 2. Secondary Neutral Tone: Muted Slate Gray (#5A6B7C) untuk aksi netral/reset/cari.
 * 3. Accent Action Tone   : Corporate Teal / Forest Accent (#1E5F74 / #166534) untuk checkout.
 * 4. Controlled Muted Tone: Deep Rose Slate (#8B2635) untuk aksi pembatalan/hapus (tetap serasi).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public final class TemaWarna {
    private TemaWarna() {}

    // Warna Utama Satu Rasi (Harmonious Slate Navy)
    public static final Color HEADER_BG = new Color(27, 42, 65);          // Deep Corporate Navy (#1B2A41)
    public static final Color TOMBOL_UTAMA = new Color(36, 60, 93);       // Slate Navy (#243C5D)
    public static final Color TOMBOL_HEADER = new Color(42, 68, 104);     // Elegant Header Slate Navy (#2A4468)
    public static final Color TOMBOL_NETRAL = new Color(85, 100, 118);    // Cool Muted Slate (#556476)
    public static final Color TOMBOL_BATAL = new Color(139, 44, 55);      // Deep Slate Crimson (#8B2C37)
    public static final Color TOMBOL_CHECKOUT = new Color(30, 81, 123);   // Accent Royal Navy / Teal Navy (#1E517B)
    
    // Teks & Border
    public static final Color TEKS_PUTIH = Color.WHITE;
    public static final Color TEKS_SUBTITLE = new Color(205, 220, 240);
    public static final Color BADGE_KASIR = new Color(245, 215, 110);
}
