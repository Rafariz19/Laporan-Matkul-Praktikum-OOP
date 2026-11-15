package view;

import model.AuthService;
import model.PengaturanSistem;
import model.User;
import model.exception.AksesDitolakException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

/**
 * DialogPengaturan - Modal Dialog CRUD Pengaturan Sistem Kasir (Fase 4)
 * Memfasilitasi:
 * 1. CRUD Judul Aplikasi & Subtitle (PBO 2026 | Rafael Rizky...).
 * 2. Pemilihan Skema Warna Tema (Header Background & Aksen).
 * 3. CRUD Diskon Membership (Tambah, Edit, Hapus tipe member dan nilai diskonnya).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class DialogPengaturan extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient PengaturanSistem config;
    private final transient User currentUser;
    private final transient KasirDesktopFrame parentFrame;

    private JTextField txtTitle;
    private JTextField txtSubtitle;
    private JPanel pnlHeaderPreview;
    private JPanel pnlAccentPreview;
    private Color selectedHeaderColor;
    private Color selectedAccentColor;

    private DefaultTableModel modelDiskon;
    private JTable tabelDiskon;

    @SuppressWarnings("this-escape")
    public DialogPengaturan(KasirDesktopFrame parent, PengaturanSistem config, User currentUser) {
        super(parent, "⚙️ Pengaturan Tampilan & Skema Diskon Kasir", true);
        this.parentFrame = parent;
        this.config = config;
        this.currentUser = currentUser;

        this.selectedHeaderColor = config.getWarnaHeader();
        this.selectedAccentColor = config.getWarnaAksen();

        initUI();
    }

    private void initUI() {
        setSize(650, 620);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // TAB 1: IDENTITAS & WARNA TAMPILAN
        JPanel pnlTampilan = new JPanel(new BorderLayout(10, 10));
        pnlTampilan.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formTampilan = new JPanel(new GridLayout(6, 1, 8, 8));

        formTampilan.add(new JLabel("Judul Aplikasi Sistem Kasir:"));
        txtTitle = new JTextField(config.getJudulAplikasi());
        txtTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTampilan.add(txtTitle);

        formTampilan.add(new JLabel("Teks Subtitle / Identitas (NRP, Mahasiswa, Kampus):"));
        txtSubtitle = new JTextField(config.getTeksHeaderSub());
        txtSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formTampilan.add(txtSubtitle);

        // Color Picker Row: Header & Accent
        JPanel pnlWarnaContainer = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel pnlCol1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlCol1.add(new JLabel("Warna Header:"));
        pnlHeaderPreview = new JPanel();
        pnlHeaderPreview.setPreferredSize(new Dimension(35, 24));
        pnlHeaderPreview.setBackground(selectedHeaderColor);
        pnlHeaderPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        ModernButton btnPilihHeader = new ModernButton("Pilih", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnPilihHeader.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Pilih Warna Header", selectedHeaderColor);
            if (c != null) {
                selectedHeaderColor = c;
                pnlHeaderPreview.setBackground(c);
            }
        });
        pnlCol1.add(pnlHeaderPreview);
        pnlCol1.add(btnPilihHeader);

        JPanel pnlCol2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlCol2.add(new JLabel("Warna Aksen:"));
        pnlAccentPreview = new JPanel();
        pnlAccentPreview.setPreferredSize(new Dimension(35, 24));
        pnlAccentPreview.setBackground(selectedAccentColor);
        pnlAccentPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        ModernButton btnPilihAksen = new ModernButton("Pilih", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnPilihAksen.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Pilih Warna Aksen", selectedAccentColor);
            if (c != null) {
                selectedAccentColor = c;
                pnlAccentPreview.setBackground(c);
            }
        });
        pnlCol2.add(pnlAccentPreview);
        pnlCol2.add(btnPilihAksen);

        pnlWarnaContainer.add(pnlCol1);
        pnlWarnaContainer.add(pnlCol2);
        formTampilan.add(new JLabel("Kustomisasi Warna Tema:"));
        formTampilan.add(pnlWarnaContainer);

        pnlTampilan.add(formTampilan, BorderLayout.NORTH);

        // Presets Color Themes
        JPanel pnlPreset = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlPreset.setBorder(BorderFactory.createTitledBorder("Pilihan Tema Warna Profesional Cepat"));

        ModernButton btnThemeNavy = new ModernButton("Navy Sapphire", new Color(27, 54, 93), Color.WHITE);
        btnThemeNavy.addActionListener(e -> terapkanPreset(new Color(27, 54, 93), new Color(0, 150, 136)));

        ModernButton btnThemeEmerald = new ModernButton("Emerald Forest", new Color(20, 90, 50), Color.WHITE);
        btnThemeEmerald.addActionListener(e -> terapkanPreset(new Color(20, 90, 50), new Color(46, 204, 113)));

        ModernButton btnThemeCharcoal = new ModernButton("Modern Charcoal", new Color(44, 62, 80), Color.WHITE);
        btnThemeCharcoal.addActionListener(e -> terapkanPreset(new Color(44, 62, 80), new Color(52, 152, 219)));

        ModernButton btnThemeCrimson = new ModernButton("Crimson Wine", new Color(120, 20, 30), Color.WHITE);
        btnThemeCrimson.addActionListener(e -> terapkanPreset(new Color(120, 20, 30), new Color(230, 126, 34)));

        pnlPreset.add(btnThemeNavy);
        pnlPreset.add(btnThemeEmerald);
        pnlPreset.add(btnThemeCharcoal);
        pnlPreset.add(btnThemeCrimson);
        pnlTampilan.add(pnlPreset, BorderLayout.CENTER);

        tabbedPane.addTab("🎨 Tampilan & Teks Header", pnlTampilan);

        // TAB 2: CRUD SKEMA DISKON MEMBERSHIP
        JPanel pnlDiskon = new JPanel(new BorderLayout(8, 8));
        pnlDiskon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Tipe Keanggotaan (Member)", "Persentase Diskon (%)", "Nilai Desimal (0.0 - 1.0)"};
        modelDiskon = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelDiskon = new JTable(modelDiskon);
        tabelDiskon.setRowHeight(24);
        tabelDiskon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelDiskon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        muatDataTabelDiskon();

        pnlDiskon.add(new JScrollPane(tabelDiskon), BorderLayout.CENTER);

        JPanel pnlActionDiskon = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        ModernButton btnTambahDiskon = new ModernButton("+ Tambah Diskon", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnEditDiskon = new ModernButton("Edit Nilai Diskon", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnHapusDiskon = new ModernButton("Hapus Diskon", TemaWarna.TOMBOL_BATAL, TemaWarna.TEKS_PUTIH);

        btnTambahDiskon.addActionListener(e -> aksiTambahDiskon());
        btnEditDiskon.addActionListener(e -> aksiEditDiskon());
        btnHapusDiskon.addActionListener(e -> aksiHapusDiskon());

        pnlActionDiskon.add(btnTambahDiskon);
        pnlActionDiskon.add(btnEditDiskon);
        pnlActionDiskon.add(btnHapusDiskon);
        pnlDiskon.add(pnlActionDiskon, BorderLayout.SOUTH);

        tabbedPane.addTab("🏷️ CRUD Skema Diskon Member", pnlDiskon);

        add(tabbedPane, BorderLayout.CENTER);

        // BOTTOM ACTION BUTTONS
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        ModernButton btnBatal = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_PUTIH);
        btnBatal.addActionListener(e -> dispose());

        ModernButton btnSimpanSemua = new ModernButton("💾 Simpan Pengaturan", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        btnSimpanSemua.addActionListener(e -> aksiSimpanSemua());

        pnlBottom.add(btnBatal);
        pnlBottom.add(btnSimpanSemua);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void terapkanPreset(Color header, Color accent) {
        this.selectedHeaderColor = header;
        this.selectedAccentColor = accent;
        pnlHeaderPreview.setBackground(header);
        pnlAccentPreview.setBackground(accent);
    }

    private void muatDataTabelDiskon() {
        modelDiskon.setRowCount(0);
        for (Map.Entry<String, Double> entry : config.getDaftarDiskonMember().entrySet()) {
            modelDiskon.addRow(new Object[]{
                entry.getKey(),
                String.format("%.1f%%", entry.getValue() * 100),
                String.format("%.2f", entry.getValue())
            });
        }
    }

    private void aksiTambahDiskon() {
        try {
            AuthService.enforceAdmin(currentUser, "Tambah Skema Diskon");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtNama = new JTextField();
        JTextField txtPersen = new JTextField();
        JPanel pnl = new JPanel(new GridLayout(2, 2, 8, 8));
        pnl.add(new JLabel("Nama Tipe Member (misal PLATINUM):"));
        pnl.add(txtNama);
        pnl.add(new JLabel("Persentase Diskon (0 s/d 100 %):"));
        pnl.add(txtPersen);

        int opt = JOptionPane.showConfirmDialog(this, pnl, "Tambah Skema Diskon Baru", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String nama = txtNama.getText().trim().toUpperCase();
                double p = Double.parseDouble(txtPersen.getText().trim());
                if (p < 0 || p > 100) {
                    JOptionPane.showMessageDialog(this, "Persentase harus di antara 0 dan 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                config.simpanDiskonMember(nama, p / 100.0);
                muatDataTabelDiskon();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage(), "Error Format", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aksiEditDiskon() {
        try {
            AuthService.enforceAdmin(currentUser, "Edit Skema Diskon");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabelDiskon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu baris diskon yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipe = (String) modelDiskon.getValueAt(row, 0);
        double diskonLama = config.getDiskon(tipe);

        String input = JOptionPane.showInputDialog(this, 
            "Masukkan persentase diskon baru untuk '" + tipe + "' (0 s/d 100 %):", 
            String.format("%.0f", diskonLama * 100));

        if (input != null && !input.trim().isEmpty()) {
            try {
                double val = Double.parseDouble(input.trim());
                if (val < 0 || val > 100) {
                    JOptionPane.showMessageDialog(this, "Persentase diskon harus 0 s/d 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                config.simpanDiskonMember(tipe, val / 100.0);
                muatDataTabelDiskon();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nilai persentase harus berupa angka numerik!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aksiHapusDiskon() {
        try {
            AuthService.enforceAdmin(currentUser, "Hapus Skema Diskon");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabelDiskon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu baris diskon yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipe = (String) modelDiskon.getValueAt(row, 0);
        if ("REGULER".equalsIgnoreCase(tipe)) {
            JOptionPane.showMessageDialog(this, "Tipe member REGULER tidak boleh dihapus!", "Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konf = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus skema diskon untuk member '" + tipe + "'?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konf == JOptionPane.YES_OPTION) {
            config.hapusDiskonMember(tipe);
            muatDataTabelDiskon();
        }
    }

    private void aksiSimpanSemua() {
        try {
            AuthService.enforceAdmin(currentUser, "Simpan Konfigurasi Sistem");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            config.setJudulAplikasi(txtTitle.getText());
            config.setTeksHeaderSub(txtSubtitle.getText());
            config.setWarnaHeader(selectedHeaderColor);
            config.setWarnaAksen(selectedAccentColor);

            config.simpanKeFile();

            JOptionPane.showMessageDialog(this, 
                "Pengaturan tampilan, judul/subtitle, dan skema diskon berhasil disimpan!", 
                "Pengaturan Tersimpan", JOptionPane.INFORMATION_MESSAGE);

            parentFrame.terapkanPengaturan();
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan berkas konfigurasi: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}
