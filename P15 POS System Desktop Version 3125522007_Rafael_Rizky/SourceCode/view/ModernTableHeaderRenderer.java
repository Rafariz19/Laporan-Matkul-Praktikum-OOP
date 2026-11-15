package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * ModernTableHeaderRenderer - Custom Table Header Renderer untuk tema Apple Modern.
 * Memastikan warna header tabel (#1D1D1F) dan teks putih (#FFFFFF) tampil
 * 100% konsisten di semua platform/Look-and-Feel (mengatasi override bawaan Windows L&F).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ModernTableHeaderRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("this-escape")
    public ModernTableHeaderRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(TemaWarna.APPLE_BLACK);
        setForeground(TemaWarna.TEKS_PUTIH);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 1, TemaWarna.BORDER_DIVIDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return this;
    }
}
