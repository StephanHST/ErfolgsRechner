
package Java.ErfolgsRechner.ui;

import Java.ErfolgsRechner.model.HistoryEntry;
import Java.ErfolgsRechner.service.HistoryService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryPreviewChartPanel extends JPanel {

    private final HistoryService historyService;

    public HistoryPreviewChartPanel(HistoryService historyService) {
        this.historyService = historyService;
        setOpaque(false);
        setPreferredSize(new Dimension(340, 210));
        setMinimumSize(new Dimension(340, 210));
        setMaximumSize(new Dimension(340, 210));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int left = 10;
        int right = 10;
        int top = 10;
        int bottom = 15;
        int chartWidth = Math.max(1, width - left - right);
        int chartHeight = Math.max(1, height - top - bottom);

        int y100 = top;
        int y60 = top + chartHeight - (int) Math.round((60 / 100.0) * chartHeight);
        int y35 = top + chartHeight - (int) Math.round((35 / 100.0) * chartHeight);
        int y20 = top + chartHeight - (int) Math.round((20 / 100.0) * chartHeight);
        int y0 = top + chartHeight;

        Shape oldClip = g2.getClip();
        Shape roundChart = new java.awt.geom.RoundRectangle2D.Double(left, top, chartWidth, chartHeight, 18, 18);
        g2.setClip(roundChart);

        g2.setColor(new Color(223, 242, 223));
        g2.fillRect(left, y100, chartWidth, y60 - y100);

        g2.setColor(new Color(245, 236, 200));
        g2.fillRect(left, y60, chartWidth, y35 - y60);

        g2.setColor(new Color(250, 242, 210));
        g2.fillRect(left, y35, chartWidth, y20 - y35);

        g2.setColor(new Color(248, 222, 222));
        g2.fillRect(left, y20, chartWidth, y0 - y20);

        List<HistoryEntry> allEntries = historyService.readHistoryEntries();
        List<HistoryEntry> entries = new ArrayList<>();
        int start = Math.max(0, allEntries.size() - 10);
        for (int i = start; i < allEntries.size(); i++) {
            entries.add(allEntries.get(i));
        }

        if (entries.isEmpty()) {
            g2.setClip(oldClip);
            g2.setColor(new Color(120, 128, 140));
            g2.setFont(new Font("Noto Sans", Font.PLAIN, 13));
            FontMetrics fm = g2.getFontMetrics();
            String emptyText = "Noch keine Daten vorhanden";
            int textX = left + (chartWidth - fm.stringWidth(emptyText)) / 2;
            int textY = top + (chartHeight + fm.getAscent()) / 2;
            g2.drawString(emptyText, textX, textY);
            g2.dispose();
            return;
        }

        int count = entries.size();
        int[] xs = new int[count];
        int[] ys = new int[count];

        for (int i = 0; i < count; i++) {
            HistoryEntry entry = entries.get(i);
            double xRatio = count == 1 ? 0.5 : i / (double) (count - 1);
            xs[i] = left + (int) Math.round(xRatio * chartWidth);
            ys[i] = top + chartHeight - (int) Math.round((entry.getSuccess() / 100.0) * chartHeight);
        }

        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(5, 177, 159));
        for (int i = 0; i < count - 1; i++) {
            g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }

        if (count == 1) {
            g2.fillOval(xs[0] - 4, ys[0] - 4, 8, 8);
        }

        g2.setClip(oldClip);
        g2.dispose();
    }
}
