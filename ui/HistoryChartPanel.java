package Java.ErfolgsRechner.ui;

import Java.ErfolgsRechner.model.HistoryEntry;
import Java.ErfolgsRechner.service.HistoryService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryChartPanel extends JPanel {

    public interface ConfirmDeleteHandler {
        boolean confirmDelete(Component parent, HistoryEntry entry);
    }

    public interface DeleteFailedHandler {
        void showDeleteFailed(Component parent);
    }

    private final List<HistoryEntry> entries = new ArrayList<>();
    private final HistoryService historyService;
    private final ConfirmDeleteHandler confirmDeleteHandler;
    private final DeleteFailedHandler deleteFailedHandler;

    private int[] lastXs = new int[0];
    private int[] lastYs = new int[0];
    private List<HistoryEntry> lastEntries = new ArrayList<>();
    private int hoveredIndex = -1;
    private final Cursor defaultCursor = Cursor.getDefaultCursor();
    private final Cursor hoverCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public HistoryChartPanel(
            List<HistoryEntry> allEntries,
            HistoryService historyService,
            ConfirmDeleteHandler confirmDeleteHandler,
            DeleteFailedHandler deleteFailedHandler
    ) {
        this.historyService = historyService;
        this.confirmDeleteHandler = confirmDeleteHandler;
        this.deleteFailedHandler = deleteFailedHandler;

        setOpaque(true);
        setBackground(Color.WHITE);

        int start = Math.max(0, allEntries.size() - 14);
        for (int i = start; i < allEntries.size(); i++) {
            this.entries.add(allEntries.get(i));
        }

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleMouseClick(e);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredIndex = -1;
                setCursor(defaultCursor);
                repaint();
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                handleMouseMove(e);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        int noteCount = 0;
        for (HistoryEntry entry : entries) {
            if (entry.getNote() != null && !entry.getNote().trim().isEmpty()) {
                noteCount++;
            }
        }
        int preferredHeight = 720 + Math.max(0, noteCount - 2) * 70;
        return new Dimension(1220, preferredHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int left = 75;
        int right = 75;
        int top = 85;
        int bottom = 150;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        g2.setColor(new Color(60, 60, 60));
        g2.setFont(new Font("Noto Sans", Font.BOLD, 18));
        g2.drawString("Erfolgsentwicklung", left, 30);
        g2.setFont(new Font("Noto Sans", Font.PLAIN, 12));
        g2.setColor(new Color(120, 128, 140));
        g2.drawString("Letzte Messungen · Klick auf einen Punkt zum Löschen", left, 48);

        int chartWidth = Math.max(1, width - left - right);
        int chartHeight = Math.max(1, height - top - bottom);

        int y100 = top;
        int y60 = top + chartHeight - (int) Math.round((60 / 100.0) * chartHeight);
        int y35 = top + chartHeight - (int) Math.round((35 / 100.0) * chartHeight);
        int y20 = top + chartHeight - (int) Math.round((20 / 100.0) * chartHeight);
        int y0 = top + chartHeight;

        g2.setColor(new Color(248, 222, 222));
        g2.fillRect(left, y20, chartWidth, y0 - y20);

        g2.setColor(new Color(250, 242, 210));
        g2.fillRect(left, y35, chartWidth, y20 - y35);

        g2.setColor(new Color(245, 236, 200));
        g2.fillRect(left, y60, chartWidth, y35 - y60);

        g2.setColor(new Color(223, 242, 223));
        g2.fillRect(left, y100, chartWidth, y60 - y100);

        g2.setFont(new Font("Noto Sans", Font.PLAIN, 11));
        g2.setColor(new Color(210, 210, 210));
        for (int y = 0; y <= 100; y += 20) {
            int py = top + chartHeight - (int) Math.round((y / 100.0) * chartHeight);
            g2.drawLine(left, py, left + chartWidth, py);
            g2.setColor(new Color(90, 90, 90));
            g2.drawString(y + "%", 25, py + 4);
            g2.setColor(new Color(210, 210, 210));
        }

        g2.setColor(new Color(90, 90, 90));
        g2.drawLine(left, top, left, top + chartHeight);
        g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight);

        if (entries.isEmpty()) {
            g2.setFont(new Font("Noto Sans", Font.PLAIN, 14));
            g2.drawString("Noch keine Verlaufsdaten vorhanden.", left, top + 30);
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

        lastXs = xs.clone();
        lastYs = ys.clone();
        lastEntries = new ArrayList<>(entries);

        g2.setColor(new Color(5, 177, 159));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < count - 1; i++) {
            g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }

        for (int i = 0; i < count; i++) {
            HistoryEntry entry = entries.get(i);
            Color pointColor = (i == hoveredIndex)
                    ? new Color(220, 90, 90)
                    : new Color(5, 177, 159);

            if (i == hoveredIndex) {
                g2.setColor(new Color(220, 90, 90, 55));
                g2.fill(new Ellipse2D.Double(xs[i] - 10, ys[i] - 10, 20, 20));
            }

            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(xs[i] - 6, ys[i] - 6, 12, 12));
            g2.setColor(pointColor);
            g2.setStroke(new BasicStroke(2f));
            Shape point = new Ellipse2D.Double(xs[i] - 6, ys[i] - 6, 12, 12);
            g2.draw(point);
            g2.fill(new Ellipse2D.Double(xs[i] - 3, ys[i] - 3, 6, 6));

            g2.setColor(new Color(60, 60, 60));
            g2.drawString(new DecimalFormat("0.0").format(entry.getSuccess()) + "%", xs[i] - 16, ys[i] - 14);
            g2.drawString(formatShortDate(entry.getDate()), xs[i] - 16, top + chartHeight + 20);
        }

        g2.setFont(new Font("Noto Sans", Font.PLAIN, 11));
        g2.setColor(new Color(110, 110, 110, 170));

        FontMetrics fm = g2.getFontMetrics();
        int labelRight = left + chartWidth - 12;

        drawPhaseLabel(g2, fm, labelRight, y60 - ((y60 - y100) / 2) + (fm.getAscent() / 2), "Flow / Hochleistung");
        drawPhaseLabel(g2, fm, labelRight, y35 - ((y35 - y60) / 2) + (fm.getAscent() / 2), "Produktive Phase");
        drawPhaseLabel(g2, fm, labelRight, y20 - ((y20 - y35) / 2) + (fm.getAscent() / 2), "Aufbauphase");
        drawPhaseLabel(g2, fm, labelRight, y0 - ((y0 - y20) / 2) + (fm.getAscent() / 2), "Engpassmodus");

        if (hoveredIndex >= 0 && hoveredIndex < entries.size()) {
            paintTooltip(g2, entries.get(hoveredIndex), xs[hoveredIndex], ys[hoveredIndex], left, chartWidth, top);
        }

        g2.dispose();
    }

    private void handleMouseClick(java.awt.event.MouseEvent e) {
        int clickRadius = 12;
        for (int i = 0; i < lastXs.length; i++) {
            int dx = e.getX() - lastXs[i];
            int dy = e.getY() - lastYs[i];
            if ((dx * dx) + (dy * dy) <= clickRadius * clickRadius && i < lastEntries.size()) {
                HistoryEntry selected = lastEntries.get(i);
                boolean confirmed = confirmDeleteHandler != null && confirmDeleteHandler.confirmDelete(this, selected);
                if (confirmed) {
                    boolean deleted = historyService.deleteHistoryEntryByTimestamp(selected.getTimestamp());
                    if (deleted) {
                        entries.remove(selected);
                        revalidate();
                        repaint();
                    } else if (deleteFailedHandler != null) {
                        deleteFailedHandler.showDeleteFailed(this);
                    }
                }
                break;
            }
        }
    }

    private void handleMouseMove(java.awt.event.MouseEvent e) {
        int hoverRadius = 12;
        int foundIndex = -1;

        for (int i = 0; i < lastXs.length; i++) {
            int dx = e.getX() - lastXs[i];
            int dy = e.getY() - lastYs[i];
            if ((dx * dx) + (dy * dy) <= hoverRadius * hoverRadius) {
                foundIndex = i;
                break;
            }
        }

        hoveredIndex = foundIndex;
        setCursor(foundIndex >= 0 ? hoverCursor : defaultCursor);
        repaint();
    }

    private void drawPhaseLabel(Graphics2D g2, FontMetrics fm, int labelRight, int y, String text) {
        g2.drawString(text, labelRight - fm.stringWidth(text), y);
    }

    private void paintTooltip(Graphics2D g2, HistoryEntry hovered, int pointX, int pointY, int left, int chartWidth, int top) {
        String noteText = (hovered.getNote() == null || hovered.getNote().trim().isEmpty())
                ? "Keine Notiz"
                : hovered.getNote().trim();

        int boxW = 210;
        int boxH = 60;
        int boxX = pointX + 14;
        int boxY = pointY - boxH - 12;

        if (boxX + boxW > left + chartWidth - 8) {
            boxX = pointX - boxW - 14;
        }
        if (boxX < left + 8) {
            boxX = left + 8;
        }
        if (boxY < top + 8) {
            boxY = pointY + 16;
        }

        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(boxX + 3, boxY + 3, boxW, boxH, 14, 14);

        g2.setColor(new Color(255, 255, 255, 245));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 14, 14);
        g2.setColor(new Color(210, 214, 220));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 14, 14);

        g2.setColor(new Color(55, 65, 81));
        g2.setFont(new Font("Noto Sans", Font.BOLD, 12));
        g2.drawString(formatShortDate(hovered.getDate()) + " · " + new DecimalFormat("0.0").format(hovered.getSuccess()) + "%", boxX + 12, boxY + 20);

        g2.setFont(new Font("Noto Sans", Font.PLAIN, 12));
        g2.drawString("Hebel: " + hovered.getMinName(), boxX + 12, boxY + 38);

        String trimmedNote = noteText.length() > 24 ? noteText.substring(0, 24) + "…" : noteText;
        g2.drawString("Notiz: " + trimmedNote, boxX + 12, boxY + 54);
    }

    private String formatShortDate(String date) {
        try {
            return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("d.M."));
        } catch (Exception ex) {
            return date;
        }
    }
}
