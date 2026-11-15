package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernButton - Komponen Tombol Kustom Swing dengan Sudut Membulat (Border Radius)
 * dan Skema Warna Profesional Modern.
 * 
 * Fitur:
 * 1. Sudut melengkung halus (Anti-Aliased Rounded Corners, default radius 12-16px).
 * 2. Visual state responsif: Normal, Hover (terang/gelap halus), Pressed, dan Disabled.
 * 3. Bayangan tepi halus (subtle border) dengan kontras tinggi sehingga tidak pucat.
 * 
 * Mahasiswa: Rafael Rizky Luga Tanjung (NRP: 3125522007) - PENS PSDKU Sumenep (2026)
 */
public class ModernButton extends JButton {
    private static final long serialVersionUID = 1L;

    private int cornerRadius = 14;
    private Color normalBg;
    private Color hoverBg;
    private Color pressedBg;
    private Color borderColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, new Color(52, 73, 94), Color.WHITE); // Default Slate Navy
    }

    @SuppressWarnings("this-escape")
    public ModernButton(String text, Color bg, Color fg) {
        super(text);
        this.normalBg = bg;
        this.hoverBg = hitungWarnaHover(bg);
        this.pressedBg = hitungWarnaPressed(bg);
        this.borderColor = hitungWarnaBorder(bg);

        setForeground(fg);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(6, 14, 6, 14));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCustomColors(Color bg, Color fg) {
        this.normalBg = bg;
        this.hoverBg = hitungWarnaHover(bg);
        this.pressedBg = hitungWarnaPressed(bg);
        this.borderColor = hitungWarnaBorder(bg);
        setForeground(fg);
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    private Color hitungWarnaHover(Color c) {
        int r = Math.min(255, (int) (c.getRed() * 1.15) + 15);
        int g = Math.min(255, (int) (c.getGreen() * 1.15) + 15);
        int b = Math.min(255, (int) (c.getBlue() * 1.15) + 15);
        return new Color(r, g, b);
    }

    private Color hitungWarnaPressed(Color c) {
        int r = Math.max(0, (int) (c.getRed() * 0.85));
        int g = Math.max(0, (int) (c.getGreen() * 0.85));
        int b = Math.max(0, (int) (c.getBlue() * 0.85));
        return new Color(r, g, b);
    }

    private Color hitungWarnaBorder(Color c) {
        int r = Math.max(0, (int) (c.getRed() * 0.75));
        int g = Math.max(0, (int) (c.getGreen() * 0.75));
        int b = Math.max(0, (int) (c.getBlue() * 0.75));
        return new Color(r, g, b, 180);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color drawBg;
        if (!isEnabled()) {
            drawBg = new Color(189, 195, 199); // Soft light gray disabled
        } else if (isPressed) {
            drawBg = pressedBg;
        } else if (isHovered) {
            drawBg = hoverBg;
        } else {
            drawBg = normalBg;
        }

        // Gambar Background Berlekuk (Rounded)
        g2.setColor(drawBg);
        g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));

        // Gambar Border Halus
        if (isEnabled()) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
