package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * ModernTableHeaderRenderer - Custom Table Header Renderer untuk tema POS Desktop.
 * Memastikan warna header tabel dinamis mengikuti tema dan teks kontras
 * 100% konsisten di semua platform/Look-and-Feel (mengatasi override bawaan Windows L&F).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ModernTableHeaderRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
    private Color headerBg = TemaWarna.APPLE_BLACK;
    private Color headerFg = Color.WHITE;

    @SuppressWarnings("this-escape")
    public ModernTableHeaderRenderer() {
        this(TemaWarna.APPLE_BLACK, Color.WHITE);
    }

    @SuppressWarnings("this-escape")
    public ModernTableHeaderRenderer(Color bg, Color fg) {
        this.headerBg = bg;
        this.headerFg = fg;
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public void setColors(Color bg, Color fg) {
        this.headerBg = bg;
        this.headerFg = fg;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(headerBg);
        setForeground(headerFg);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 1, TemaWarna.BORDER_DIVIDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return this;
    }
}
