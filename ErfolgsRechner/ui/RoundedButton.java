

package Java.ErfolgsRechner.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private final int radius;
    private final Color borderColor;

    private final Color normalBg;
    private final Color normalFg;
    private Color hoverBg;
    private Color hoverFg;
    private boolean hoverInstalled = false;

    public RoundedButton(String text, int radius, Color bg, Color fg, Color borderColor) {
        super(text);
        this.radius = radius;
        this.borderColor = borderColor;
        this.normalBg = bg;
        this.normalFg = fg;
        this.hoverBg = bg;
        this.hoverFg = fg;

        setBackground(bg);
        setForeground(fg);

        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setHoverStyle(Color bg, Color fg) {
        setBackground(bg);
        setForeground(fg);
        repaint();
    }

    public void setHoverColors(Color hoverBg, Color hoverFg) {
        this.hoverBg = hoverBg;
        this.hoverFg = hoverFg;

        if (!hoverInstalled) {
            hoverInstalled = true;
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setHoverStyle(RoundedButton.this.hoverBg, RoundedButton.this.hoverFg);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setHoverStyle(normalBg, normalFg);
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
    }
}
