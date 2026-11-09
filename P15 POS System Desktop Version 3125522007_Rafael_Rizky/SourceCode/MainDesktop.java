import view.KasirDesktopFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * MainDesktop - Entry Point Aplikasi GUI Sistem Kasir Sederhana
 * Menjalankan antarmuka grafis Java Swing modern.
 * 
 * @author Rafael Rizky Luga Tanjung (NRP: 3125522007)
 * Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep 2026)
 */
public class MainDesktop {
    public static void main(String[] args) {
        // Terapkan System Look and Feel agar menyatu dengan OS Windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            KasirDesktopFrame app = new KasirDesktopFrame();
            app.setVisible(true);
        });
    }
}

