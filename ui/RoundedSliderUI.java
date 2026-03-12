
package Java.ErfolgsRechner.ui;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedSliderUI extends BasicSliderUI {

    public RoundedSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(18, 18);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thumbRect.x;
        int y = thumbRect.y;
        int w = thumbRect.width - 1;
        int h = thumbRect.height - 1;

        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillOval(x + 1, y + 2, w, h);

        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, w, h);

        g2.setColor(new Color(230, 234, 240));
        g2.drawOval(x, y, w, h);

        g2.dispose();
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int trackY = trackRect.y + trackRect.height / 2 - 2;
        int trackH = 4;
        int arc = 4;

        g2.setColor(new Color(230, 234, 240));
        g2.fillRoundRect(trackRect.x, trackY, trackRect.width, trackH, arc, arc);

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int filled = thumbRect.x + thumbRect.width / 2 - trackRect.x;
            filled = Math.max(0, Math.min(trackRect.width, filled));
            g2.setColor(new Color(5, 177, 159));
            g2.fillRoundRect(trackRect.x, trackY, filled, trackH, arc, arc);
        }

        g2.dispose();
    }
}
