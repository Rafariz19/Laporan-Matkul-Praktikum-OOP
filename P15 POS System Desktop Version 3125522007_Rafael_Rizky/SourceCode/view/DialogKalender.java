package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * DialogKalender - Modal dialog pemilih tanggal interaktif berbasis kalender visual (Pure Java Swing).
 * Menyediakan:
 * 1. Navigasi Cepat: Dropdown Bulan (Januari - Desember) dan Spinner Tahun (2000 - 2099).
 * 2. Tombol Navigasi Cepat Bulan: Mundur (◀) dan Maju (▶).
 * 3. Grid Kalender 7x7 (Minggu s/d Sabtu) dengan penanda tanggal hari ini dan tanggal terpilih.
 * 4. Tombol Shortcut: "Hari Ini" dan "Batal".
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class DialogKalender extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final String[] NAMA_BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    private static final String[] HARI_HEADER = {
        "Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"
    };

    private int tahunAktif;
    private int bulanAktif; // 1 - 12
    private LocalDate tanggalTerpilih;
    private boolean terkonfirmasi = false;

    private JComboBox<String> cmbBulan;
    private JSpinner spinTahun;
    private JPanel pnlGridHari;
    private boolean isUpdatingNav = false;

    @SuppressWarnings("this-escape")
    public DialogKalender(Window owner, LocalDate initialDate) {
        super(owner, "📅 Pilih Tanggal Kalender", ModalityType.APPLICATION_MODAL);
        LocalDate init = (initialDate != null) ? initialDate : LocalDate.now();
        this.tanggalTerpilih = init;
        this.tahunAktif = init.getYear();
        this.bulanAktif = init.getMonthValue();

        initUI();
    }

    private void initUI() {
        setSize(360, 340);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(TemaWarna.APPLE_CANVAS);

        // 1. TOP NAV PANEL: Bulan, Tahun, Prev, Next
        JPanel pnlTop = new JPanel(new BorderLayout(5, 5));
        pnlTop.setBackground(TemaWarna.APPLE_CANVAS);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(8, 10, 5, 10));

        ModernButton btnPrev = new ModernButton("<", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnPrev.setPreferredSize(new Dimension(38, 28));
        btnPrev.setMargin(new Insets(0, 0, 0, 0));
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrev.addActionListener(e -> {
            bulanAktif--;
            if (bulanAktif < 1) {
                bulanAktif = 12;
                tahunAktif--;
            }
            sinkronkanNavigasi();
            renderGridHari();
        });

        ModernButton btnNext = new ModernButton(">", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnNext.setPreferredSize(new Dimension(38, 28));
        btnNext.setMargin(new Insets(0, 0, 0, 0));
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.addActionListener(e -> {
            bulanAktif++;
            if (bulanAktif > 12) {
                bulanAktif = 1;
                tahunAktif++;
            }
            sinkronkanNavigasi();
            renderGridHari();
        });

        JPanel pnlCenterNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        pnlCenterNav.setOpaque(false);

        cmbBulan = new JComboBox<>(NAMA_BULAN);
        cmbBulan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbBulan.setSelectedIndex(bulanAktif - 1);
        cmbBulan.addActionListener(e -> {
            if (!isUpdatingNav) {
                bulanAktif = cmbBulan.getSelectedIndex() + 1;
                renderGridHari();
            }
        });

        SpinnerNumberModel modelTahun = new SpinnerNumberModel(tahunAktif, 2000, 2099, 1);
        spinTahun = new JSpinner(modelTahun);
        spinTahun.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JSpinner.NumberEditor editorTahun = new JSpinner.NumberEditor(spinTahun, "#");
        spinTahun.setEditor(editorTahun);
        spinTahun.addChangeListener(e -> {
            if (!isUpdatingNav) {
                tahunAktif = (Integer) spinTahun.getValue();
                renderGridHari();
            }
        });

        pnlCenterNav.add(cmbBulan);
        pnlCenterNav.add(spinTahun);

        pnlTop.add(btnPrev, BorderLayout.WEST);
        pnlTop.add(pnlCenterNav, BorderLayout.CENTER);
        pnlTop.add(btnNext, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // 2. CENTER PANEL: Grid Hari (Header + Angka Tanggal)
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10),
                BorderFactory.createLineBorder(TemaWarna.BORDER_DIVIDER)
        ));

        pnlGridHari = new JPanel(new GridLayout(7, 7, 2, 2));
        pnlGridHari.setBackground(Color.WHITE);
        pnlGridHari.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        renderGridHari();
        pnlCenter.add(pnlGridHari, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Shortcut Hari Ini & Batal
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlBottom.setBackground(TemaWarna.APPLE_CANVAS);

        ModernButton btnHariIni = new ModernButton("Hari Ini", TemaWarna.APPLE_BLUE, Color.WHITE);
        btnHariIni.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHariIni.addActionListener(e -> {
            tanggalTerpilih = LocalDate.now();
            terkonfirmasi = true;
            dispose();
        });

        ModernButton btnBatal = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBatal.addActionListener(e -> {
            terkonfirmasi = false;
            dispose();
        });

        pnlBottom.add(btnHariIni);
        pnlBottom.add(btnBatal);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void sinkronkanNavigasi() {
        isUpdatingNav = true;
        cmbBulan.setSelectedIndex(bulanAktif - 1);
        spinTahun.setValue(tahunAktif);
        isUpdatingNav = false;
    }

    private void renderGridHari() {
        pnlGridHari.removeAll();

        // Baris 1: Header Nama Hari (Min, Sen, Sel, Rab, Kam, Jum, Sab)
        for (int i = 0; i < 7; i++) {
            JLabel lblHdr = new JLabel(HARI_HEADER[i], SwingConstants.CENTER);
            lblHdr.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if (i == 0) {
                lblHdr.setForeground(new Color(0xD3, 0x2F, 0x2F)); // Minggu merah
            } else {
                lblHdr.setForeground(TemaWarna.APPLE_BLACK);
            }
            lblHdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TemaWarna.BORDER_DIVIDER));
            pnlGridHari.add(lblHdr);
        }

        YearMonth ym = YearMonth.of(tahunAktif, bulanAktif);
        LocalDate hariPertama = ym.atDay(1);
        int offsetHari = hariPertama.getDayOfWeek().getValue() % 7; // Sunday = 0
        int jumlahHari = ym.lengthOfMonth();

        LocalDate hariIni = LocalDate.now();

        // Kotak kosong sebelum tanggal 1
        for (int i = 0; i < offsetHari; i++) {
            JLabel lblKosong = new JLabel("", SwingConstants.CENTER);
            pnlGridHari.add(lblKosong);
        }

        // Tanggal 1 s/d jumlahHari
        for (int tgl = 1; tgl <= jumlahHari; tgl++) {
            LocalDate tglObj = LocalDate.of(tahunAktif, bulanAktif, tgl);
            boolean isHariIni = tglObj.equals(hariIni);
            boolean isTerpilih = tglObj.equals(tanggalTerpilih);

            JButton btnTgl = new JButton(String.valueOf(tgl));
            btnTgl.setFont(new Font("Segoe UI", isTerpilih || isHariIni ? Font.BOLD : Font.PLAIN, 11));
            btnTgl.setFocusPainted(false);
            btnTgl.setMargin(new Insets(1, 1, 1, 1));
            btnTgl.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (isTerpilih) {
                btnTgl.setBackground(TemaWarna.APPLE_BLUE);
                btnTgl.setForeground(Color.WHITE);
                btnTgl.setBorder(BorderFactory.createLineBorder(TemaWarna.APPLE_BLUE));
            } else if (isHariIni) {
                btnTgl.setBackground(new Color(0xE3, 0xF2, 0xFD));
                btnTgl.setForeground(TemaWarna.APPLE_BLUE);
                btnTgl.setBorder(BorderFactory.createLineBorder(TemaWarna.APPLE_BLUE));
            } else {
                btnTgl.setBackground(Color.WHITE);
                int dayOfWeek = tglObj.getDayOfWeek().getValue() % 7;
                if (dayOfWeek == 0) {
                    btnTgl.setForeground(new Color(0xD3, 0x2F, 0x2F)); // Hari Minggu
                } else {
                    btnTgl.setForeground(TemaWarna.APPLE_BLACK);
                }
                btnTgl.setBorder(BorderFactory.createLineBorder(new Color(0xEE, 0xEE, 0xEE)));
            }

            final LocalDate clickedDate = tglObj;
            btnTgl.addActionListener(e -> {
                tanggalTerpilih = clickedDate;
                terkonfirmasi = true;
                dispose();
            });

            pnlGridHari.add(btnTgl);
        }

        // Kotak kosong penutup grid agar pas 6 baris x 7 kolom (total 42 sel)
        int sisaSel = 42 - (offsetHari + jumlahHari);
        for (int i = 0; i < sisaSel; i++) {
            JLabel lblKosong = new JLabel("", SwingConstants.CENTER);
            pnlGridHari.add(lblKosong);
        }

        pnlGridHari.revalidate();
        pnlGridHari.repaint();
    }

    public LocalDate getTanggalTerpilih() {
        return tanggalTerpilih;
    }

    public boolean isTerkonfirmasi() {
        return terkonfirmasi;
    }
}
