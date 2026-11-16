package view;

import model.AuthService;
import model.KatalogProduk;
import model.PengaturanSistem;
import model.ProfilPengaturan;
import model.User;
import model.exception.AksesDitolakException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DialogPengaturan - Modal Dialog CRUD Pengaturan Sistem & Template Settingan (Fase 4 & 5)
 * Memfasilitasi:
 * 1. Multi-Template / Profil Pengaturan (Simpan Template Baru, Switch Template, Hapus Template).
 * 2. CRUD Judul Aplikasi & Subtitle Identitas.
 * 3. Pemilihan Skema Warna Tema (Header, Aksen, & 4 Preset Resmi).
 * 4. CRUD Diskon Membership (Tambah, Edit, Hapus tipe member dan nilai diskonnya).
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class DialogPengaturan extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient PengaturanSistem config;
    private final transient User currentUser;
    private final transient KasirDesktopFrame parentFrame;

    // Selector Template / Profil Settingan
    private JComboBox<String> cmbProfil;
    private boolean isSwitchingProfile = false;

    // Komponen Form Tampilan
    private JTextField txtTitle;
    private JTextField txtSubtitle;
    private JPanel pnlHeaderPreview;
    private JPanel pnlAccentPreview;
    private Color selectedHeaderColor;
    private Color selectedAccentColor;

    // Komponen Form Diskon (Lokal per form)
    private DefaultTableModel modelDiskon;
    private JTable tabelDiskon;
    private final transient Map<String, Double> localDiskonMap;

    // Komponen Form Manajemen Kategori
    private DefaultTableModel modelTabelKategori;
    private JTable tabelKategoriAdmin;
    private final transient List<String> localKategoriList;

    private ModernButton btnSimpanSemua;

    @SuppressWarnings("this-escape")
    public DialogPengaturan(KasirDesktopFrame parent, PengaturanSistem config, User currentUser) {
        super(parent, "⚙️ Pengaturan Tampilan & Template Sistem Kasir", true);
        this.parentFrame = parent;
        this.config = config;
        this.currentUser = currentUser;

        this.selectedHeaderColor = config.getWarnaHeader();
        this.selectedAccentColor = config.getWarnaAksen();
        this.localDiskonMap = new LinkedHashMap<>(config.getDaftarDiskonMember());
        this.localKategoriList = new ArrayList<>(config.getDaftarKategori());

        initUI();
    }

    private void initUI() {
        setSize(700, 680);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));

        // 1. TOP PANEL: BILAH TEMPLATE / PROFIL SETTINGAN
        JPanel pnlProfileBar = new JPanel(new BorderLayout(10, 5));
        pnlProfileBar.setBackground(TemaWarna.APPLE_CANVAS);
        pnlProfileBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, TemaWarna.BORDER_DIVIDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel pnlProfileLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        pnlProfileLeft.setOpaque(false);
        JLabel lblProfile = new JLabel("📁 Template / Settingan Aktif:");
        lblProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProfile.setForeground(TemaWarna.APPLE_BLACK);

        cmbProfil = new JComboBox<>();
        cmbProfil.setFont(new Font("Segoe UI", Font.BOLD, 12));
        muatComboProfil();
        cmbProfil.addActionListener(e -> {
            if (!isSwitchingProfile) {
                String terpilih = (String) cmbProfil.getSelectedItem();
                if (terpilih != null) {
                    aksiSwitchProfil(terpilih);
                }
            }
        });

        pnlProfileLeft.add(lblProfile);
        pnlProfileLeft.add(cmbProfil);

        JPanel pnlProfileRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        pnlProfileRight.setOpaque(false);

        ModernButton btnTambahTemplate = new ModernButton("+ Template Baru", TemaWarna.APPLE_BLUE, Color.WHITE);
        btnTambahTemplate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTambahTemplate.addActionListener(e -> aksiTambahTemplateBaru());

        ModernButton btnHapusTemplate = new ModernButton("Hapus Template", TemaWarna.TOMBOL_BATAL, Color.WHITE);
        btnHapusTemplate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHapusTemplate.addActionListener(e -> aksiHapusTemplateTerpilih());

        pnlProfileRight.add(btnTambahTemplate);
        pnlProfileRight.add(btnHapusTemplate);

        pnlProfileBar.add(pnlProfileLeft, BorderLayout.WEST);
        pnlProfileBar.add(pnlProfileRight, BorderLayout.EAST);
        add(pnlProfileBar, BorderLayout.NORTH);

        // 2. CENTER PANEL: TABBED PANE (Tampilan & Diskon)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // TAB 1: IDENTITAS & WARNA TAMPILAN
        JPanel pnlTampilan = new JPanel(new BorderLayout(10, 10));
        pnlTampilan.setBackground(TemaWarna.APPLE_CANVAS);
        pnlTampilan.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formTampilan = new JPanel(new GridLayout(6, 1, 8, 8));
        formTampilan.setOpaque(false);

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
        pnlWarnaContainer.setOpaque(false);

        JPanel pnlCol1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlCol1.setOpaque(false);
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
        pnlCol2.setOpaque(false);
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
                if (btnSimpanSemua != null) {
                    btnSimpanSemua.setCustomColors(c, TemaWarna.getKontrasForeground(c));
                }
            }
        });
        pnlCol2.add(pnlAccentPreview);
        pnlCol2.add(btnPilihAksen);

        pnlWarnaContainer.add(pnlCol1);
        pnlWarnaContainer.add(pnlCol2);
        formTampilan.add(new JLabel("Kustomisasi Warna Tema:"));
        formTampilan.add(pnlWarnaContainer);

        pnlTampilan.add(formTampilan, BorderLayout.NORTH);

        // Presets Color Themes (4 Tema Warna Pilihan)
        JPanel pnlPreset = new JPanel(new GridLayout(2, 2, 10, 8));
        pnlPreset.setBackground(TemaWarna.APPLE_CANVAS);
        pnlPreset.setBorder(BorderFactory.createTitledBorder("Pilihan Tema Warna Profesional Cepat"));

        ModernButton btnThemeApple = new ModernButton("🍎 Apple Modern", TemaWarna.APPLE_BLACK, TemaWarna.APPLE_BLUE);
        btnThemeApple.addActionListener(e -> terapkanPreset(TemaWarna.APPLE_BLACK, TemaWarna.APPLE_BLUE));

        ModernButton btnThemeAmazon = new ModernButton("📦 Amazon Prime", TemaWarna.AMAZON_NAVY, TemaWarna.AMAZON_ORANGE);
        btnThemeAmazon.addActionListener(e -> terapkanPreset(TemaWarna.AMAZON_NAVY, TemaWarna.AMAZON_ORANGE));

        ModernButton btnThemeMandela = new ModernButton("🔥 Mandela Orange", TemaWarna.MANDELA_BLACK, TemaWarna.MANDELA_ORANGE);
        btnThemeMandela.addActionListener(e -> terapkanPreset(TemaWarna.MANDELA_BLACK, TemaWarna.MANDELA_ORANGE));

        ModernButton btnThemeCyber = new ModernButton("⚡ Cyber Lime", TemaWarna.CYBER_FOREST, TemaWarna.CYBER_LIME);
        btnThemeCyber.addActionListener(e -> terapkanPreset(TemaWarna.CYBER_FOREST, TemaWarna.CYBER_LIME));

        pnlPreset.add(btnThemeApple);
        pnlPreset.add(btnThemeAmazon);
        pnlPreset.add(btnThemeMandela);
        pnlPreset.add(btnThemeCyber);
        pnlTampilan.add(pnlPreset, BorderLayout.CENTER);

        tabbedPane.addTab("🎨 Tampilan & Teks Header", pnlTampilan);

        // TAB 2: CRUD SKEMA DISKON MEMBERSHIP
        JPanel pnlDiskon = new JPanel(new BorderLayout(8, 8));
        pnlDiskon.setBackground(TemaWarna.APPLE_CANVAS);
        pnlDiskon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Tipe Keanggotaan (Member)", "Persentase Diskon (%)", "Nilai Desimal (0.0 - 1.0)"};
        modelDiskon = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelDiskon = new JTable(modelDiskon);
        tabelDiskon.setRowHeight(25);
        tabelDiskon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelDiskon.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelDiskon.getTableHeader().setDefaultRenderer(new ModernTableHeaderRenderer());
        tabelDiskon.setGridColor(TemaWarna.BORDER_DIVIDER);
        muatDataTabelDiskon();

        pnlDiskon.add(new JScrollPane(tabelDiskon), BorderLayout.CENTER);

        JPanel pnlActionDiskon = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlActionDiskon.setBackground(TemaWarna.APPLE_CANVAS);
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

        tabbedPane.addTab("🏷️ Skema Diskon Member", pnlDiskon);

        // TAB 3: CRUD MANAJEMEN KATEGORI PRODUK
        JPanel pnlKategori = new JPanel(new BorderLayout(8, 8));
        pnlKategori.setBackground(TemaWarna.APPLE_CANVAS);
        pnlKategori.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] colKategori = {"No.", "Nama Kategori", "Jumlah Produk Terdaftar", "Tipe Kategori"};
        modelTabelKategori = new DefaultTableModel(colKategori, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelKategoriAdmin = new JTable(modelTabelKategori);
        tabelKategoriAdmin.setRowHeight(25);
        tabelKategoriAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelKategoriAdmin.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelKategoriAdmin.getTableHeader().setDefaultRenderer(new ModernTableHeaderRenderer());
        tabelKategoriAdmin.setGridColor(TemaWarna.BORDER_DIVIDER);
        tabelKategoriAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelKategoriAdmin.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabelKategoriAdmin.getColumnModel().getColumn(0).setMaxWidth(60);
        tabelKategoriAdmin.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabelKategoriAdmin.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabelKategoriAdmin.getColumnModel().getColumn(3).setPreferredWidth(130);

        muatDataTabelKategori();

        pnlKategori.add(new JScrollPane(tabelKategoriAdmin), BorderLayout.CENTER);

        JPanel pnlActionKategori = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlActionKategori.setBackground(TemaWarna.APPLE_CANVAS);
        ModernButton btnTambahKat = new ModernButton("+ Tambah Kategori", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnEditKat = new ModernButton("Edit Kategori", TemaWarna.TOMBOL_UTAMA, TemaWarna.TEKS_PUTIH);
        ModernButton btnHapusKat = new ModernButton("Hapus Kategori", TemaWarna.TOMBOL_BATAL, TemaWarna.TEKS_PUTIH);

        btnTambahKat.addActionListener(e -> aksiTambahKategori());
        btnEditKat.addActionListener(e -> aksiEditKategori());
        btnHapusKat.addActionListener(e -> aksiHapusKategori());

        pnlActionKategori.add(btnTambahKat);
        pnlActionKategori.add(btnEditKat);
        pnlActionKategori.add(btnHapusKat);
        pnlKategori.add(pnlActionKategori, BorderLayout.SOUTH);

        tabbedPane.addTab("📁 Manajemen Kategori", pnlKategori);

        add(tabbedPane, BorderLayout.CENTER);

        // 3. BOTTOM ACTION BUTTONS
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBottom.setBackground(TemaWarna.APPLE_CANVAS);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TemaWarna.BORDER_DIVIDER));

        ModernButton btnBatal = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnBatal.addActionListener(e -> dispose());

        btnSimpanSemua = new ModernButton("💾 Simpan Pengaturan", selectedAccentColor, TemaWarna.getKontrasForeground(selectedAccentColor));
        btnSimpanSemua.addActionListener(e -> aksiSimpanSemua());

        pnlBottom.add(btnBatal);
        pnlBottom.add(btnSimpanSemua);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void muatComboProfil() {
        isSwitchingProfile = true;
        cmbProfil.removeAllItems();
        for (String nama : config.getSemuaProfil().keySet()) {
            cmbProfil.addItem(nama);
        }
        cmbProfil.setSelectedItem(config.getNamaProfilAktif());
        isSwitchingProfile = false;
    }

    private void aksiSwitchProfil(String namaProfil) {
        ProfilPengaturan p = config.getProfil(namaProfil);
        if (p == null) return;

        txtTitle.setText(p.getJudulAplikasi());
        txtSubtitle.setText(p.getTeksHeaderSub());
        this.selectedHeaderColor = p.getWarnaHeader();
        this.selectedAccentColor = p.getWarnaAksen();
        pnlHeaderPreview.setBackground(selectedHeaderColor);
        pnlAccentPreview.setBackground(selectedAccentColor);

        if (btnSimpanSemua != null) {
            btnSimpanSemua.setCustomColors(selectedAccentColor, TemaWarna.getKontrasForeground(selectedAccentColor));
        }

        localDiskonMap.clear();
        localDiskonMap.putAll(p.getDaftarDiskonMember());
        muatDataTabelDiskon();

        localKategoriList.clear();
        localKategoriList.addAll(p.getDaftarKategori());
        muatDataTabelKategori();
    }

    private void aksiTambahTemplateBaru() {
        try {
            AuthService.enforceAdmin(currentUser, "Buat Template Pengaturan Baru");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaBaru = JOptionPane.showInputDialog(this, 
                "Masukkan Nama Template / Settingan Baru:\n(Contoh: 'Settingan 2', 'Promo Akhir Tahun', 'Toko Cabang 2')", 
                "Simpan Sebagai Template Baru", JOptionPane.QUESTION_MESSAGE);
        
        if (namaBaru == null || namaBaru.trim().isEmpty()) return;

        try {
            config.buatProfilBaruDariAktif(namaBaru.trim());
            ProfilPengaturan baru = new ProfilPengaturan(namaBaru.trim(), 
                txtTitle.getText().trim(), txtSubtitle.getText().trim(),
                selectedHeaderColor, selectedAccentColor, localDiskonMap, localKategoriList);
            config.simpanProfil(baru);
            muatComboProfil();
            cmbProfil.setSelectedItem(namaBaru.trim());
            JOptionPane.showMessageDialog(this, "Template '" + namaBaru.trim() + "' berhasil dibuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiHapusTemplateTerpilih() {
        try {
            AuthService.enforceAdmin(currentUser, "Hapus Template Pengaturan");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String terpilih = (String) cmbProfil.getSelectedItem();
        if (terpilih == null) return;

        int konf = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus template settingan '" + terpilih + "'?", 
                "Konfirmasi Hapus Template", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (konf != JOptionPane.YES_OPTION) return;

        try {
            if (config.hapusProfil(terpilih)) {
                muatComboProfil();
                String aktifBaru = (String) cmbProfil.getSelectedItem();
                if (aktifBaru != null) {
                    aksiSwitchProfil(aktifBaru);
                }
                JOptionPane.showMessageDialog(this, "Template '" + terpilih + "' berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ditolak", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void terapkanPreset(Color header, Color accent) {
        this.selectedHeaderColor = header;
        this.selectedAccentColor = accent;
        pnlHeaderPreview.setBackground(header);
        pnlAccentPreview.setBackground(accent);
        if (btnSimpanSemua != null) {
            btnSimpanSemua.setCustomColors(accent, TemaWarna.getKontrasForeground(accent));
        }
    }

    private void muatDataTabelDiskon() {
        modelDiskon.setRowCount(0);
        for (Map.Entry<String, Double> entry : localDiskonMap.entrySet()) {
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

        JTextField txtTipe = new JTextField();
        JTextField txtDiskon = new JTextField();
        JPanel pnl = new JPanel(new GridLayout(2, 2, 8, 8));
        pnl.add(new JLabel("Nama Tipe Member (Unik):"));
        pnl.add(txtTipe);
        pnl.add(new JLabel("Persentase Diskon (0 s/d 100):"));
        pnl.add(txtDiskon);

        int opt = JOptionPane.showConfirmDialog(this, pnl, "Tambah Skema Diskon Member", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String tipe = txtTipe.getText().trim().toUpperCase();
            String strD = txtDiskon.getText().trim();
            if (tipe.isEmpty() || strD.isEmpty()) return;

            try {
                double pct = Double.parseDouble(strD.replace("%", "").trim());
                if (pct < 0 || pct > 100) {
                    JOptionPane.showMessageDialog(this, "Persentase diskon harus 0 s/d 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double desimal = pct / 100.0;
                localDiskonMap.put(tipe, desimal);
                muatDataTabelDiskon();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Format angka diskon tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Pilih baris tipe member yang ingin diedit nilainya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipe = (String) modelDiskon.getValueAt(row, 0);
        double diskonLama = localDiskonMap.getOrDefault(tipe, 0.0);

        String input = JOptionPane.showInputDialog(this, 
            "Masukkan nilai diskon baru untuk member '" + tipe + "' (dalam %):\n(0 s/d 100)", 
            String.format("%.0f", diskonLama * 100));

        if (input == null || input.trim().isEmpty()) return;

        try {
            double pct = Double.parseDouble(input.replace("%", "").trim());
            if (pct < 0 || pct > 100) {
                JOptionPane.showMessageDialog(this, "Persentase diskon harus 0 s/d 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double desimal = pct / 100.0;
            localDiskonMap.put(tipe, desimal);
            muatDataTabelDiskon();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka diskon tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Pilih baris tipe member yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
            localDiskonMap.remove(tipe);
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

        String profileName = (String) cmbProfil.getSelectedItem();
        if (profileName == null || profileName.trim().isEmpty()) {
            profileName = "Settingan 1";
        }

        try {
            String title = txtTitle.getText().trim();
            String sub = txtSubtitle.getText().trim();

            ProfilPengaturan target = new ProfilPengaturan(profileName, title, sub,
                    selectedHeaderColor, selectedAccentColor, localDiskonMap, localKategoriList);

            config.simpanProfil(target);
            config.aktifkanProfil(profileName);
            config.simpanKeFile();

            JOptionPane.showMessageDialog(this, 
                "Pengaturan template '" + profileName + "' berhasil disimpan dan diterapkan!", 
                "Pengaturan Tersimpan", JOptionPane.INFORMATION_MESSAGE);

            parentFrame.terapkanPengaturan();
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan berkas konfigurasi: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- LOGIKA AKSI CRUD KATEGORI PRODUK ---

    private void muatDataTabelKategori() {
        if (modelTabelKategori == null) return;
        modelTabelKategori.setRowCount(0);
        KatalogProduk kat = (parentFrame != null) ? parentFrame.getKatalog() : null;

        int no = 1;
        for (String k : localKategoriList) {
            int jumlahProduk = (kat != null) ? kat.hitungProdukByKategori(k) : 0;
            boolean isDasar = "Makanan".equalsIgnoreCase(k) || "Minuman".equalsIgnoreCase(k) || "Elektronik".equalsIgnoreCase(k);
            String tipe = isDasar ? "Kategori Bawaan" : "Kategori Kustom";
            modelTabelKategori.addRow(new Object[]{
                no++, k, jumlahProduk + " produk", tipe
            });
        }
    }

    private void aksiTambahKategori() {
        try {
            AuthService.enforceAdmin(currentUser, "Tambah Kategori Baru");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
            "Masukkan Nama Kategori Baru:\n(Contoh: 'Snack', 'Perawatan', 'Alat Tulis')",
            "Tambah Kategori Produk", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        String katBaru = input.trim();

        for (String k : localKategoriList) {
            if (k.equalsIgnoreCase(katBaru)) {
                JOptionPane.showMessageDialog(this, "Kategori '" + katBaru + "' sudah ada di template ini!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        localKategoriList.add(katBaru);
        muatDataTabelKategori();
        JOptionPane.showMessageDialog(this, "Kategori '" + katBaru + "' berhasil ditambahkan!\nKlik 'Simpan Pengaturan' untuk menerapkan perubahan secara permanen.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aksiEditKategori() {
        try {
            AuthService.enforceAdmin(currentUser, "Edit Kategori Produk");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabelKategoriAdmin.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kategori pada tabel yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String katLama = (String) modelTabelKategori.getValueAt(row, 1);
        if ("Makanan".equalsIgnoreCase(katLama) || "Minuman".equalsIgnoreCase(katLama) || "Elektronik".equalsIgnoreCase(katLama)) {
            JOptionPane.showMessageDialog(this, "Kategori bawaan '" + katLama + "' tidak boleh diedit atau diubah namanya!", "Ditolak Aturan Sistem", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String inputBaru = JOptionPane.showInputDialog(this,
            "Masukkan nama baru untuk kategori '" + katLama + "':",
            katLama);
        if (inputBaru == null || inputBaru.trim().isEmpty()) return;
        String katBaru = inputBaru.trim();
        if (katBaru.equalsIgnoreCase(katLama)) return;

        for (String k : localKategoriList) {
            if (k.equalsIgnoreCase(katBaru)) {
                JOptionPane.showMessageDialog(this, "Kategori '" + katBaru + "' sudah ada di template ini!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int idx = -1;
        for (int i = 0; i < localKategoriList.size(); i++) {
            if (localKategoriList.get(i).equalsIgnoreCase(katLama)) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) {
            localKategoriList.set(idx, katBaru);
        }

        int diubah = 0;
        if (parentFrame != null && parentFrame.getKatalog() != null) {
            diubah = parentFrame.getKatalog().ubahNamaKategoriMassal(katLama, katBaru);
            try {
                parentFrame.simpanKatalogKeDisk();
            } catch (Exception ignored) {
            }
        }

        muatDataTabelKategori();
        JOptionPane.showMessageDialog(this, 
            "Kategori '" + katLama + "' berhasil diubah menjadi '" + katBaru + "'!\n" +
            (diubah > 0 ? diubah + " produk di katalog otomatis disesuaikan.\n" : "") +
            "Klik 'Simpan Pengaturan' untuk menerapkan template secara permanen.",
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aksiHapusKategori() {
        try {
            AuthService.enforceAdmin(currentUser, "Hapus Kategori Produk");
        } catch (AksesDitolakException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tabelKategoriAdmin.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kategori pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kat = (String) modelTabelKategori.getValueAt(row, 1);
        if ("Makanan".equalsIgnoreCase(kat) || "Minuman".equalsIgnoreCase(kat) || "Elektronik".equalsIgnoreCase(kat)) {
            JOptionPane.showMessageDialog(this, "Kategori bawaan '" + kat + "' tidak boleh dihapus!", "Ditolak Aturan Sistem", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KatalogProduk katObj = (parentFrame != null) ? parentFrame.getKatalog() : null;
        int jmlProduk = (katObj != null) ? katObj.hitungProdukByKategori(kat) : 0;

        if (jmlProduk > 0) {
            int konf = JOptionPane.showConfirmDialog(this,
                "Terdapat " + jmlProduk + " produk yang saat ini menggunakan kategori '" + kat + "'.\n" +
                "Jika kategori ini dihapus, produk-produk tersebut akan dialihkan ke kategori bawaan 'Makanan'.\n\n" +
                "Apakah Anda yakin ingin melanjutkan?",
                "Konfirmasi Hapus Kategori", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (konf != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            int konf = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori '" + kat + "'?",
                "Konfirmasi Hapus Kategori", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (konf != JOptionPane.YES_OPTION) {
                return;
            }
        }

        localKategoriList.removeIf(k -> k.equalsIgnoreCase(kat));

        if (jmlProduk > 0 && katObj != null) {
            katObj.ubahNamaKategoriMassal(kat, "Makanan");
            try {
                parentFrame.simpanKatalogKeDisk();
            } catch (Exception ignored) {
            }
        }

        muatDataTabelKategori();
        JOptionPane.showMessageDialog(this, "Kategori '" + kat + "' berhasil dihapus dari template ini!\nKlik 'Simpan Pengaturan' untuk menerapkan perubahan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }
}
