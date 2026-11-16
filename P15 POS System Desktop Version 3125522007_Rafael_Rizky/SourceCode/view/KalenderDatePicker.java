package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * KalenderDatePicker - Komponen visual Swing untuk pemilihan tanggal dengan kalender interaktif.
 * Menggantikan metode input pengetikan manual (keyboard) dengan dialog kalender visual modal.
 * 
 * Fitur:
 * 1. Tampilan field tanggal terformat yang tidak dapat diketik manual (non-editable).
 * 2. Tombol pemilih kalender visual (📅) yang memunculkan DialogKalender.
 * 3. Klik pada kotak teks maupun tombol ikon akan langsung membuka dialog kalender.
 * 4. Mendukung format tanggal kustom (misal 'yyyy-MM-dd' untuk katalog, 'dd-MM-yyyy' untuk laporan).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class KalenderDatePicker extends JPanel {
    private static final long serialVersionUID = 1L;

    private LocalDate selectedDate;
    private final String pattern;
    private final transient DateTimeFormatter formatter;
    private final Window parentWindow;

    private final JTextField txtDate;
    private final ModernButton btnCalendar;

    public KalenderDatePicker(Window parent) {
        this(parent, "yyyy-MM-dd", LocalDate.now());
    }

    public KalenderDatePicker(Window parent, String pattern) {
        this(parent, pattern, LocalDate.now());
    }

    @SuppressWarnings("this-escape")
    public KalenderDatePicker(Window parent, String pattern, LocalDate initialDate) {
        this.parentWindow = parent;
        this.pattern = (pattern != null && !pattern.trim().isEmpty()) ? pattern : "yyyy-MM-dd";
        this.formatter = DateTimeFormatter.ofPattern(this.pattern);
        this.selectedDate = (initialDate != null) ? initialDate : LocalDate.now();

        setLayout(new BorderLayout(4, 0));
        setOpaque(false);

        txtDate = new JTextField(this.selectedDate.format(formatter));
        txtDate.setEditable(false);
        txtDate.setBackground(Color.WHITE);
        txtDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtDate.setToolTipText("Klik untuk memilih tanggal dari kalender");
        txtDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    bukaDialogKalender();
                }
            }
        });

        btnCalendar = new ModernButton("📅", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnCalendar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCalendar.setToolTipText("Buka Kalender Interaktif");
        btnCalendar.setPreferredSize(new Dimension(38, 26));
        btnCalendar.addActionListener(e -> bukaDialogKalender());

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    public void bukaDialogKalender() {
        DialogKalender dlg = new DialogKalender(parentWindow, selectedDate);
        dlg.setVisible(true);
        if (dlg.isTerkonfirmasi()) {
            setDate(dlg.getTanggalTerpilih());
        }
    }

    public LocalDate getDate() {
        return selectedDate;
    }

    public void setDate(LocalDate date) {
        if (date != null) {
            this.selectedDate = date;
            this.txtDate.setText(this.selectedDate.format(formatter));
        }
    }

    public String getText() {
        return txtDate.getText().trim();
    }

    public void setText(String text) {
        if (text == null || text.trim().isEmpty()) return;
        try {
            LocalDate d = LocalDate.parse(text.trim(), formatter);
            setDate(d);
        } catch (DateTimeParseException ex) {
            // Coba fallback ke yyyy-MM-dd jika format utama berbeda
            try {
                LocalDate d = LocalDate.parse(text.trim());
                setDate(d);
            } catch (Exception ignored) {
                txtDate.setText(text.trim());
            }
        }
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtDate.setEnabled(enabled);
        btnCalendar.setEnabled(enabled);
        if (enabled) {
            txtDate.setBackground(Color.WHITE);
        } else {
            txtDate.setBackground(new Color(240, 240, 240));
        }
    }

    public JTextField getTextField() {
        return txtDate;
    }
}
