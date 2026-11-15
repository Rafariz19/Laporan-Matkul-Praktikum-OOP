package view;

import java.awt.Color;

/**
 * TemaWarna - Multi-Preset Professional Color Palette System
 * Memuat 4 Preset Tema Warna Resmi:
 * 1. Apple Modern (Stickdazzle): #F5F5F7, #1D1D1F, #AAAAAA, #007AFF
 * 2. Amazon Prime (Amazon Corporate): #232F3E, #FF9900, #37475A, #EAEDED
 * 3. Mandela Orange (Brand Identity): #1C1C1C, #FF4F00, #FFC400, #FFFFFF
 * 4. Cyber Lime (Cyber Pine / Neon): #013F32, #E7FE25, #161616, #FDFDFD
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public final class TemaWarna {
    private TemaWarna() {}

    // --- PRESET 1: APPLE MODERN ---
    public static final Color APPLE_CANVAS = new Color(0xF5, 0xF5, 0xF7); // #F5F5F7 (Latar Canvas / Surface)
    public static final Color APPLE_BLACK  = new Color(0x1D, 0x1D, 0x1F); // #1D1D1F (Deep Space Black)
    public static final Color APPLE_GRAY   = new Color(0xAA, 0xAA, 0xAA); // #AAAAAA (Silver Gray / Border)
    public static final Color APPLE_BLUE   = new Color(0x00, 0x7A, 0xFF); // #007AFF (Apple Vibrant Blue / CTA)

    // --- PRESET 2: AMAZON PRIME ---
    public static final Color AMAZON_NAVY   = new Color(0x23, 0x2F, 0x3E); // #232F3E (Amazon Dark Squid Ink)
    public static final Color AMAZON_ORANGE = new Color(0xFF, 0x99, 0x00); // #FF9900 (Amazon Vivid Orange)
    public static final Color AMAZON_SLATE  = new Color(0x37, 0x47, 0x5A); // #37475A (Amazon Slate Navy)
    public static final Color AMAZON_CANVAS = new Color(0xEA, 0xED, 0xED); // #EAEDED (Amazon Light Gray Surface)

    // --- PRESET 3: MANDELA ORANGE ---
    public static final Color MANDELA_BLACK  = new Color(0x1C, 0x1C, 0x1C); // #1C1C1C (Deep Black)
    public static final Color MANDELA_ORANGE = new Color(0xFF, 0x4F, 0x00); // #FF4F00 (Bold Orange)
    public static final Color MANDELA_YELLOW = new Color(0xFF, 0xC4, 0x00); // #FFC400 (Golden Yellow)
    public static final Color MANDELA_CANVAS = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA (Clean Off-White)

    // --- PRESET 4: CYBER LIME ---
    public static final Color CYBER_FOREST = new Color(0x01, 0x3F, 0x32); // #013F32 (Deep Forest Pine Green)
    public static final Color CYBER_LIME   = new Color(0xE7, 0xFE, 0x25); // #E7FE25 (Electric Lime)
    public static final Color CYBER_BLACK  = new Color(0x16, 0x16, 0x16); // #161616 (Deep Pitch Black)
    public static final Color CYBER_CANVAS = new Color(0xFD, 0xFD, 0xFD); // #FDFDFD (Pure White Canvas)

    // --- DEFAULT ACTIVE ROLE MAPPINGS ---
    public static final Color HEADER_BG       = APPLE_BLACK;  // #1D1D1F
    public static final Color TOMBOL_UTAMA    = APPLE_BLACK;  // #1D1D1F
    public static final Color TOMBOL_HEADER   = APPLE_BLUE;   // #007AFF
    public static final Color TOMBOL_NETRAL   = APPLE_GRAY;   // #AAAAAA
    public static final Color TOMBOL_BATAL    = APPLE_BLACK;  // #1D1D1F
    public static final Color TOMBOL_CHECKOUT = APPLE_BLUE;   // #007AFF

    // Teks & Pembatas
    public static final Color TEKS_PUTIH         = Color.WHITE;
    public static final Color TEKS_GELAP         = APPLE_BLACK;
    public static final Color TEKS_TOMBOL_NETRAL = APPLE_BLACK; // #1D1D1F (Teks hitam pekat kontras tinggi untuk tombol terang)
    public static final Color TEKS_MUTED         = APPLE_GRAY;
    public static final Color TEKS_SUBTITLE   = new Color(0xD1, 0xD1, 0xD6);
    public static final Color BADGE_KASIR     = APPLE_BLUE;
    public static final Color BORDER_DIVIDER  = APPLE_GRAY;

    /**
     * Menghitung warna teks foreground optimal (Hitam Pekat atau Putih Bersih)
     * berdasarkan kalkulasi standar luminansi relatif WCAG 2.1.
     * Mencegah teks putih di atas background terang (misalnya pada tombol Electric Lime).
     */
    public static Color getKontrasForeground(Color bg) {
        if (bg == null) return Color.WHITE;
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
        return luminance > 0.60 ? APPLE_BLACK : Color.WHITE;
    }
}
