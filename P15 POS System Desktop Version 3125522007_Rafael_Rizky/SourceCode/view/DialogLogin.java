package view;

import model.AuthService;
import model.Role;
import model.User;
import model.exception.KasirException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * DialogLogin - Modal Dialog Otentikasi & Registrasi Pengguna Baru Sistem POS.
 * 
 * Fitur:
 * 1. Otentikasi login aman menggunakan SHA-256 + Salt acak lokal via AuthService.
 * 2. Fitur langsung menambahkan/mendaftarkan pengguna baru (Kasir atau Admin).
 * 3. Tampilan modern dan selaras dengan tema POS Desktop.
 * 
 * Mata Kuliah : Praktikum Pemrograman Berorientasi Obyek (PBO)
 * Mahasiswa   : Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Kampus      : PENS PSDKU Sumenep (2026)
 */
public class DialogLogin extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient AuthService authService;
    private transient User authenticatedUser = null;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    @SuppressWarnings("this-escape")
    public DialogLogin(Frame parent, AuthService authService) {
        super(parent, "🔐 Login Sistem Kasir POS", true);
        this.authService = authService;

        initUI();
    }

    private void initUI() {
        setSize(440, 360);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(0, 0));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (authenticatedUser == null) {
                    System.exit(0);
                }
            }
        });

        // 1. Header Panel
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlHeader.setBackground(TemaWarna.APPLE_BLACK);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("SISTEM KASIR POINT OF SALE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Otentikasi Petugas & Manajemen Pengguna Multi-Role");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TemaWarna.TEKS_SUBTITLE);

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Form Body Panel
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setBackground(TemaWarna.APPLE_CANVAS);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtUsername = new JTextField("admin", 20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    aksiLogin();
                }
            }
        });

        pnlBody.add(lblUser);
        pnlBody.add(Box.createVerticalStrut(4));
        pnlBody.add(txtUsername);
        pnlBody.add(Box.createVerticalStrut(12));
        pnlBody.add(lblPass);
        pnlBody.add(Box.createVerticalStrut(4));
        pnlBody.add(txtPassword);

        add(pnlBody, BorderLayout.CENTER);

        // 3. Action Buttons Panel
        JPanel pnlFooter = new JPanel(new GridLayout(2, 1, 8, 8));
        pnlFooter.setBackground(TemaWarna.APPLE_CANVAS);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        ModernButton btnMasuk = new ModernButton("🔑 Masuk / Login", TemaWarna.APPLE_BLUE, Color.WHITE);
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMasuk.addActionListener(e -> aksiLogin());

        JPanel pnlSubButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlSubButtons.setOpaque(false);

        ModernButton btnDaftarBaru = new ModernButton("👤 + Tambah User", TemaWarna.APPLE_BLACK, Color.WHITE);
        btnDaftarBaru.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDaftarBaru.addActionListener(e -> aksiBukaDialogRegistrasi());

        ModernButton btnKeluar = new ModernButton("Tutup / Keluar", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnKeluar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnKeluar.addActionListener(e -> System.exit(0));

        pnlSubButtons.add(btnDaftarBaru);
        pnlSubButtons.add(btnKeluar);

        pnlFooter.add(btnMasuk);
        pnlFooter.add(pnlSubButtons);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void aksiLogin() {
        String uname = txtUsername.getText().trim();
        String pwd = new String(txtPassword.getPassword()).trim();

        if (uname.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan username dan password!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            this.authenticatedUser = authService.login(uname, pwd);
            dispose();
        } catch (KasirException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Otentikasi", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void aksiBukaDialogRegistrasi() {
        JDialog dlgReg = new JDialog(this, "👤 Tambah Pengguna Baru (Registrasi)", true);
        dlgReg.setSize(420, 360);
        dlgReg.setLocationRelativeTo(this);
        dlgReg.setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlForm.setBackground(TemaWarna.APPLE_CANVAS);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JTextField txtNewUser = new JTextField();
        JPasswordField txtNewPass = new JPasswordField();
        JPasswordField txtConfirmPass = new JPasswordField();
        JComboBox<Role> cmbRole = new JComboBox<>(new Role[]{Role.KASIR, Role.ADMIN});

        pnlForm.add(new JLabel("Username Baru:"));
        pnlForm.add(txtNewUser);
        pnlForm.add(new JLabel("Password:"));
        pnlForm.add(txtNewPass);
        pnlForm.add(new JLabel("Konfirmasi Password:"));
        pnlForm.add(txtConfirmPass);
        pnlForm.add(new JLabel("Hak Akses (Role):"));
        pnlForm.add(cmbRole);

        dlgReg.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pnlBtn.setBackground(TemaWarna.APPLE_CANVAS);
        pnlBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TemaWarna.BORDER_DIVIDER));

        ModernButton btnBatalReg = new ModernButton("Batal", TemaWarna.TOMBOL_NETRAL, TemaWarna.TEKS_TOMBOL_NETRAL);
        btnBatalReg.addActionListener(e -> dlgReg.dispose());

        ModernButton btnSimpanUser = new ModernButton("Daftarkan Akun", TemaWarna.APPLE_BLUE, Color.WHITE);
        btnSimpanUser.addActionListener(e -> {
            String u = txtNewUser.getText().trim();
            String p1 = new String(txtNewPass.getPassword()).trim();
            String p2 = new String(txtConfirmPass.getPassword()).trim();
            Role r = (Role) cmbRole.getSelectedItem();

            if (u.isEmpty()) {
                JOptionPane.showMessageDialog(dlgReg, "Username tidak boleh kosong!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p1.isEmpty()) {
                JOptionPane.showMessageDialog(dlgReg, "Password tidak boleh kosong!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p1.length() < 4) {
                JOptionPane.showMessageDialog(dlgReg, "Password minimal 4 karakter!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(dlgReg, "Konfirmasi password tidak cocok!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                authService.register(u, p1, r);
                JOptionPane.showMessageDialog(dlgReg, 
                    "Pengguna baru '" + u + "' dengan role [" + r + "] berhasil didaftarkan!\n" +
                    "Akun langsung aktif dan tersimpan ke berkas sistem.", 
                    "Registrasi Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                dlgReg.dispose();
                txtUsername.setText(u);
                txtPassword.setText("");
                txtPassword.requestFocus();
            } catch (KasirException ex) {
                JOptionPane.showMessageDialog(dlgReg, ex.getMessage(), "Registrasi Ditolak", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dlgReg, "Gagal menulis ke berkas pengguna: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlBtn.add(btnBatalReg);
        pnlBtn.add(btnSimpanUser);
        dlgReg.add(pnlBtn, BorderLayout.SOUTH);

        dlgReg.setVisible(true);
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
