package Java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErfolgsRechner {

    static class LimitFactor {
        String name;
        JSlider slider;
        JLabel valueLabel;

        LimitFactor(String name) {
            this.name = name;
            this.slider = new JSlider(0, 100, 50);
            this.slider.setMajorTickSpacing(20);
            this.slider.setMinorTickSpacing(5);
            this.slider.setPaintTicks(true);
            this.slider.setPaintLabels(true);
            this.valueLabel = new JLabel("50 %");
            this.valueLabel.setPreferredSize(new Dimension(60, 25));
            this.slider.addChangeListener(e -> this.valueLabel.setText(this.slider.getValue() + " %"));
        }

        double value() {
            return slider.getValue() / 100.0;
        }
    }

    static class HistoryEntry {
        String timestamp;
        String date;
        String minName;
        double min;
        double success;
        String note;

        HistoryEntry(String timestamp, String date, String minName, double min, double success, String note) {
            this.timestamp = timestamp;
            this.date = date;
            this.minName = minName;
            this.min = min;
            this.success = success;
            this.note = note;
        }
    }

    private static JSlider createSlider() {
        JSlider s = new JSlider(0, 100, 50);
        s.setMajorTickSpacing(20);
        s.setMinorTickSpacing(5);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        return s;
    }

    private static JPanel createSliderRow(String labelText, JSlider slider, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(190, 25));

        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(label, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    private static JLabel createValueLabel(JSlider slider) {
        JLabel valueLabel = new JLabel(slider.getValue() + " %");
        valueLabel.setPreferredSize(new Dimension(60, 25));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        slider.addChangeListener(e -> valueLabel.setText(slider.getValue() + " %"));
        return valueLabel;
    }

    private static Path getHistoryFilePath() {
        return Paths.get(System.getProperty("user.home"), ".erfolgsrechner-history.txt");
    }

    private static void appendHistory(double w, double c, double t, List<LimitFactor> limits, String minName, double min, double base, double successPercent, String note) {
        Path historyFile = getHistoryFilePath();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder line = new StringBuilder();
        line.append(LocalDateTime.now().format(formatter)).append(";");
        line.append("w=").append(String.format(java.util.Locale.US, "%.2f", w)).append(";");
        line.append("c=").append(String.format(java.util.Locale.US, "%.2f", c)).append(";");
        line.append("t=").append(String.format(java.util.Locale.US, "%.2f", t)).append(";");

        for (LimitFactor l : limits) {
            line.append(l.name).append("=").append(String.format(java.util.Locale.US, "%.2f", l.value())).append(";");
        }

        line.append("minName=").append(minName).append(";");
        line.append("min=").append(String.format(java.util.Locale.US, "%.2f", min)).append(";");
        line.append("base=").append(String.format(java.util.Locale.US, "%.2f", base)).append(";");
        line.append("success=").append(String.format(java.util.Locale.US, "%.2f", successPercent)).append(";");
        String safeNote = note == null ? "" : note.replace(";", ",").replace("\n", " ").replace("\r", " ").trim();
        line.append("note=").append(safeNote);

        try (BufferedWriter writer = Files.newBufferedWriter(
                historyFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(line.toString());
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String buildTrendSummary() {
        Path historyFile = getHistoryFilePath();

        if (!Files.exists(historyFile)) {
            return "<p><b>Verlauf:</b> Noch keine Verlaufsdaten vorhanden.</p>";
        }

        try {
            List<String> lines = Files.readAllLines(historyFile);
            if (lines.size() < 2) {
                return "<p><b>Verlauf:</b> Erste Messung gespeichert.</p>";
            }

            String last = lines.get(lines.size() - 1);
            String previous = lines.get(lines.size() - 2);

            double lastSuccess = extractValue(last, "success");
            double prevSuccess = extractValue(previous, "success");
            double delta = lastSuccess - prevSuccess;

            String direction;
            if (delta > 3) {
                direction = "steigend ↑";
            } else if (delta < -3) {
                direction = "fallend ↓";
            } else {
                direction = "stabil →";
            }

            return "<p><b>Verlauf:</b> Erfolgswahrscheinlichkeit im Vergleich zur letzten Messung: <b>"
                    + direction
                    + "</b> ("
                    + (delta >= 0 ? "+" : "")
                    + new DecimalFormat("0.00").format(delta)
                    + "%).</p>";
        } catch (IOException ex) {
            return "<p><b>Verlauf:</b> Konnte nicht gelesen werden.</p>";
        }
    }

    private static java.util.List<HistoryEntry> readHistoryEntries() {
        Path historyFile = getHistoryFilePath();
        java.util.List<HistoryEntry> entries = new java.util.ArrayList<>();

        if (!Files.exists(historyFile)) {
            return entries;
        }

        try {
            java.util.List<String> lines = Files.readAllLines(historyFile);
            for (String line : lines) {
                String timestamp = extractTimestamp(line);
                String date = timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
                String minName = extractTextValue(line, "minName");
                double min = extractValue(line, "min");
                double success = extractValue(line, "success");
                String note = extractTextValue(line, "note");
                entries.add(new HistoryEntry(timestamp, date, minName, min, success, note));
            }
        } catch (IOException ignored) {
        }

        return entries;
    }

    private static JPanel createHistoryChartPanel(java.util.List<HistoryEntry> allEntries) {
        java.util.List<HistoryEntry> entries = new java.util.ArrayList<>();
        int start = Math.max(0, allEntries.size() - 14);
        for (int i = start; i < allEntries.size(); i++) {
            entries.add(allEntries.get(i));
        }

        Map<String, String> bottleneckByDate = new LinkedHashMap<>();
        for (HistoryEntry entry : entries) {
            bottleneckByDate.put(entry.date, entry.minName + " (" + new DecimalFormat("0.00").format(entry.min * 100) + "%)");
        }
        Map<String, java.util.List<String>> notesByDate = new LinkedHashMap<>();
        for (HistoryEntry entry : entries) {
            if (entry.note != null && !entry.note.trim().isEmpty()) {
                notesByDate.computeIfAbsent(entry.date, k -> new ArrayList<>()).add(entry.note.trim());
            }
        }

        final int noteCount;
        int tmpNoteCount = 0;
        for (HistoryEntry entry : entries) {
            if (entry.note != null && !entry.note.trim().isEmpty()) {
                tmpNoteCount++;
            }
        }
        noteCount = tmpNoteCount;

        return new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                int preferredHeight = 760 + Math.max(0, noteCount - 2) * 70;
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
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("Verlauf der Erfolgswahrscheinlichkeit", left, 32);

                int chartWidth = Math.max(1, width - left - right);
                int chartHeight = Math.max(1, height - top - bottom);

                int y100 = top;
                int y60 = top + chartHeight - (int) Math.round((60 / 100.0) * chartHeight);
                int y35 = top + chartHeight - (int) Math.round((35 / 100.0) * chartHeight);
                int y20 = top + chartHeight - (int) Math.round((20 / 100.0) * chartHeight);
                int y0 = top + chartHeight;

                // Hintergrundzonen: unten rot (schwach), Mitte gelb, oben grün (stark)
                g2.setColor(new Color(248, 222, 222));
                g2.fillRect(left, y20, chartWidth, y0 - y20);

                g2.setColor(new Color(250, 242, 210));
                g2.fillRect(left, y35, chartWidth, y20 - y35);

                g2.setColor(new Color(245, 236, 200));
                g2.fillRect(left, y60, chartWidth, y35 - y60);

                g2.setColor(new Color(223, 242, 223));
                g2.fillRect(left, y100, chartWidth, y60 - y100);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
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
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
                    ys[i] = top + chartHeight - (int) Math.round((entry.success / 100.0) * chartHeight);
                }

                g2.setColor(new Color(90, 130, 220));
                for (int i = 0; i < count - 1; i++) {
                    g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }

                for (int i = 0; i < count; i++) {
                    HistoryEntry entry = entries.get(i);
                    g2.setColor(new Color(90, 130, 220));
                    Shape point = new Ellipse2D.Double(xs[i] - 5, ys[i] - 5, 10, 10);
                    g2.fill(point);

                    g2.setColor(new Color(60, 60, 60));
                    g2.drawString(new DecimalFormat("0.0").format(entry.success) + "%", xs[i] - 14, ys[i] - 10);

                    String shortDate;
                    try {
                        shortDate = java.time.LocalDate.parse(entry.date)
                                .format(DateTimeFormatter.ofPattern("d.M."));
                    } catch (Exception ex) {
                        shortDate = entry.date;
                    }

                    g2.drawString(shortDate, xs[i] - 16, top + chartHeight + 20);
                }

                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(110, 110, 110, 170));

                FontMetrics fm = g2.getFontMetrics();
                int labelRight = left + chartWidth - 12;

                String phaseHigh = "Flow / Hochleistung";
                String phaseProd = "Produktive Phase";
                String phaseBuild = "Aufbauphase";
                String phaseLow = "Engpassmodus";

                int highY = y60 - ((y60 - y100) / 2) + (fm.getAscent() / 2);
                int prodY = y35 - ((y35 - y60) / 2) + (fm.getAscent() / 2);
                int buildY = y20 - ((y20 - y35) / 2) + (fm.getAscent() / 2);
                int lowY = y0 - ((y0 - y20) / 2) + (fm.getAscent() / 2);

                g2.drawString(phaseHigh, labelRight - fm.stringWidth(phaseHigh), highY);
                g2.drawString(phaseProd, labelRight - fm.stringWidth(phaseProd), prodY);
                g2.drawString(phaseBuild, labelRight - fm.stringWidth(phaseBuild), buildY);
                g2.drawString(phaseLow, labelRight - fm.stringWidth(phaseLow), lowY);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(60, 60, 60));
                int legendY = top + chartHeight + 45;
                g2.drawString("Engpässe pro Datum:", left, legendY);

                int legendLine = 1;
                for (Map.Entry<String, String> entry : bottleneckByDate.entrySet()) {
                    String shortDate;
                    try {
                        shortDate = java.time.LocalDate.parse(entry.getKey())
                                .format(DateTimeFormatter.ofPattern("d.M."));
                    } catch (Exception ex) {
                        shortDate = entry.getKey();
                    }

                    int currentY = legendY + (legendLine * 16);
                    g2.drawString(shortDate + ": " + entry.getValue(), left, currentY);
                    legendLine++;
                }

                // Notizboxen zuletzt zeichnen (damit sie immer vorne liegen)
                for (int i = 0; i < count; i++) {
                    HistoryEntry entry = entries.get(i);

                    if (entry.note != null && !entry.note.trim().isEmpty()) {
                        boolean placeAbove = (i % 2 == 0);

                        drawNoteBox(
                                g2,
                                entry.note,
                                xs[i],
                                ys[i],
                                placeAbove,
                                left,
                                left + chartWidth,
                                top,
                                top + chartHeight
                        );
                    }
                }
                g2.dispose();
            }
        };
    }

    private static void showHistoryChart(JFrame frame) {
        java.util.List<HistoryEntry> entries = readHistoryEntries();
        JPanel chartPanel = createHistoryChartPanel(entries);

        JScrollPane scrollPane = new JScrollPane(
                chartPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setPreferredSize(new Dimension(1220, 760));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        JOptionPane.showMessageDialog(
                frame,
                scrollPane,
                "ErfolgsRechner – Verlauf",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private static boolean deleteHistory() {
        Path historyFile = getHistoryFilePath();
        try {
            return Files.deleteIfExists(historyFile);
        } catch (IOException ex) {
            return false;
        }
    }

    private static double extractValue(String line, String key) {
        String[] parts = line.split(";");
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                try {
                    return Double.parseDouble(part.substring((key + "=").length()));
                } catch (NumberFormatException ignored) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static String extractTextValue(String line, String key) {
        String[] parts = line.split(";");
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                return part.substring((key + "=").length());
            }
        }
        return "-";
    }

    private static String extractTimestamp(String line) {
        String[] parts = line.split(";");
        return parts.length > 0 ? parts[0] : "-";
    }

        private static String getSuccessPhase(double successPercent) {
        if (successPercent < 20) {
            return "Engpassmodus";
        } else if (successPercent < 35) {
            return "Aufbauphase";
        } else if (successPercent < 60) {
            return "Produktive Phase";
        } else {
            return "Flow / Hochleistung";
        }
    }

        private static java.util.List<String> wrapNoteText(String text, int maxCharsPerLine) {
        java.util.List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }

        String[] words = text.trim().split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.length() == 0) {
                current.append(word);
            } else if (current.length() + 1 + word.length() <= maxCharsPerLine) {
                current.append(" ").append(word);
            } else {
                lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }

        return lines;
    }

    private static void drawNoteBox(
        Graphics2D g2,
        String note,
        int anchorX,
        int anchorY,
        boolean placeAbove,
        int chartLeft,
        int chartRight,
        int chartTop,
        int chartBottom
                                    ) {
        java.util.List<String> lines = wrapNoteText(note, 18);
        if (lines.isEmpty()) {
            return;
        }

        Font originalFont = g2.getFont();
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        int paddingX = 10;
        int paddingY = 8;
        int lineHeight = fm.getHeight();
        int boxWidth = 160;

        // Höhe dynamisch an Text anpassen (mit kleinem Mindestwert)
        int contentHeight = lines.size() * lineHeight;
        int boxHeight = Math.max(40, paddingY * 2 + contentHeight);

        int boxX = anchorX + 12;
        int boxY = placeAbove ? anchorY - boxHeight - 14 : anchorY + 14;
        boolean boxOnLeft = false;

        // horizontal innerhalb des Diagramms halten
        if (boxX + boxWidth > chartRight - 10) {
            boxX = anchorX - boxWidth - 12;
            boxOnLeft = true;
        }
        if (boxX < chartLeft + 10) {
            boxX = chartLeft + 10;
            boxOnLeft = false;
        }

        // vertikal innerhalb des Diagramms halten
        if (boxY < chartTop + 10) {
            boxY = chartTop + 10;
        }
        if (boxY + boxHeight > chartBottom - 10) {
            boxY = chartBottom - boxHeight - 10;
        }

        // Schatten
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(boxX + 3, boxY + 3, boxWidth, boxHeight, 16, 16);

        // Box
        g2.setColor(new Color(255, 255, 255, 235));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 16, 16);

        g2.setColor(new Color(170, 170, 170));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 16, 16);

        // Verbindungslinie korrekt andocken
        g2.setColor(new Color(120, 120, 120));
        int lineStartX = anchorX;
        int lineStartY = anchorY;

        int lineEndX;
        int lineEndY;

        if (boxOnLeft) {
            lineEndX = boxX + boxWidth;
            lineEndY = placeAbove ? boxY + boxHeight - 14 : boxY + 14;
        } else {
            lineEndX = boxX;
            lineEndY = placeAbove ? boxY + boxHeight - 14 : boxY + 14;
        }

        g2.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);

        g2.setColor(new Color(55, 55, 55));
        int textY = boxY + paddingY + fm.getAscent();
        for (String line : lines) {
            g2.drawString(line, boxX + paddingX, textY);
            textY += lineHeight;
        }

        g2.setFont(originalFont);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("ErfolgsRechner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(920, 520);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("Bewerte alle Faktoren von 0–100");
        info.setFont(new Font("SansSerif", Font.BOLD, 20));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(info);
        panel.add(Box.createVerticalStrut(15));

        JLabel limitHeader = new JLabel("Limitierende Faktoren");
        limitHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        limitHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(limitHeader);
        panel.add(Box.createVerticalStrut(8));

        // limitierende Faktoren
        List<LimitFactor> limits = new ArrayList<>();
        limits.add(new LimitFactor("Energie"));
        limits.add(new LimitFactor("Emotionale Stabilität"));
        limits.add(new LimitFactor("Ressourcen"));
        limits.add(new LimitFactor("Wissenstand"));

        for (LimitFactor l : limits) {
            JPanel row = createSliderRow(l.name, l.slider, l.valueLabel);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(row);
        }

        panel.add(Box.createVerticalStrut(12));

        JLabel mainHeader = new JLabel("Hauptvariablen");
        mainHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        mainHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mainHeader);
        panel.add(Box.createVerticalStrut(8));

        JSlider wSlider = createSlider();
        JLabel wValueLabel = createValueLabel(wSlider);
        JPanel wRow = createSliderRow("Zielklarheit (w)", wSlider, wValueLabel);
        wRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(wRow);

        JSlider cSlider = createSlider();
        JLabel cValueLabel = createValueLabel(cSlider);
        JPanel cRow = createSliderRow("Konzentration (c)", cSlider, cValueLabel);
        cRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cRow);

        JSlider tSlider = createSlider();
        JLabel tValueLabel = createValueLabel(tSlider);
        JPanel tRow = createSliderRow("Zeit (t)", tSlider, tValueLabel);
        tRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tRow);

        panel.add(Box.createVerticalStrut(18));

        JLabel noteLabel = new JLabel("Notiz zur Messung (max. 130 Zeichen)");
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(noteLabel);
        panel.add(Box.createVerticalStrut(4));

        JTextField noteField = new JTextField();
        noteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        noteField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(noteField);

        panel.add(Box.createVerticalStrut(12));

        JButton calculate = new JButton("Berechnen");
        JButton showHistory = new JButton("Verlauf anzeigen");
        JButton deleteHistoryButton = new JButton("Verlauf löschen");
        calculate.setFocusPainted(false);
        calculate.setAlignmentX(Component.LEFT_ALIGNMENT);
        showHistory.setFocusPainted(false);
        showHistory.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteHistoryButton.setFocusPainted(false);
        deleteHistoryButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        showHistory.addActionListener((ActionEvent e) -> showHistoryChart(frame));

        deleteHistoryButton.addActionListener((ActionEvent e) -> {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Willst du den gespeicherten Verlauf wirklich löschen?",
                    "Verlauf löschen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean deleted = deleteHistory();
                JOptionPane.showMessageDialog(
                        frame,
                        deleted
                                ? "Der Verlauf wurde gelöscht."
                                : "Es war kein Verlauf vorhanden oder er konnte nicht gelöscht werden.",
                        "ErfolgsRechner – Verlauf",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        calculate.addActionListener((ActionEvent e) -> {

            double w = wSlider.getValue() / 100.0;
            double c = cSlider.getValue() / 100.0;
            double t = tSlider.getValue() / 100.0;
            String note = noteField.getText() == null ? "" : noteField.getText().trim();
            if (note.length() > 130) {
                note = note.substring(0, 130);
            }

            double min = 1.0;
            String minName = "";

            for (LimitFactor l : limits) {
                if (l.value() < min) {
                    min = l.value();
                    minName = l.name;
                }
            }

            double base = (w * c + t);


            double r = base * min;
            double successPercent = r * 100;
            if (successPercent > 100) successPercent = 100;
            if (successPercent < 0) successPercent = 0;
            appendHistory(w, c, t, limits, minName, min, base, successPercent, note);

            String trendSummary = buildTrendSummary();

            String recommendationTitle = "Empfehlung für heute";
            StringBuilder recommendationText = new StringBuilder();

            switch (minName) {
                case "Energie":
                    recommendationText.append("Stabilisiere zuerst deine Energie: weniger Druck, klare Pausen, Bewegung, Schlaf und nur die wichtigsten Aufgaben.");
                    break;
                case "Emotionale Stabilität":
                    recommendationText.append("Reduziere zuerst innere Reibung: offene Schleifen schließen, Erwartungen senken, Reize reduzieren und für mehr Ruhe im System sorgen.");
                    break;
                case "Ressourcen":
                    recommendationText.append("Prüfe zuerst Hebel bei Zeit, Geld oder Unterstützung: priorisieren, delegieren, vereinfachen oder verschieben.");
                    break;
                case "Wissenstand":
                    recommendationText.append("Der größte Hebel liegt gerade im Wissen: kurz recherchieren, nachfragen oder eine Wissenslücke gezielt schließen.");
                    break;
                default:
                    recommendationText.append("Schütze den aktuell schwächsten Faktor zuerst, bevor du auf maximalen Output gehst.");
                    break;
            }

            List<String> followUps = new ArrayList<>();

            if (w < 0.4) {
                followUps.add("danach Zielklarheit erhöhen und den nächsten Schritt sauber definieren");
            }

            if (c < 0.4) {
                followUps.add("anschließend Ablenkung reduzieren und Konzentration schützen");
            }

            if (t < 0.4) {
                followUps.add("außerdem bewusst ein größeres Zeitfenster freischaufeln");
            }

            if (!followUps.isEmpty()) {
                recommendationText.append(" ");
                recommendationText.append("Danach ");
                recommendationText.append(String.join(", ", followUps));
                recommendationText.append(".");
            }

            DecimalFormat df = new DecimalFormat("0.00");
            String successPhase = getSuccessPhase(successPercent);

            String message = "<html>"
                    + "<div style='font-family:sans-serif; padding:10px; width:420px;'>"
                    + "<h2 style='margin-top:0;'>Ergebnisanalyse | " + successPhase + "</h2>"
                    + "<p><b>Aktueller Engpass:</b> " + minName + "</p>"
                    + "<p><b>Stärke dieses Faktors:</b> " + df.format(min * 100) + "%</p>"
                    + "<p><b>Der Engpass bremst dein Gesamtergebnis aktuell um " + df.format((1 - min) * 100) + "%.</b></p>"
                    + "<hr>"
                    + "<p><b>Basisleistung (w × c + t):</b> " + df.format(base) + "</p>"
                    + "<p><b>Erwartete Erfolgswahrscheinlichkeit:</b> " + df.format(successPercent) + "%</p>"
                    + (!note.isEmpty() ? "<p><b>Notiz:</b> " + note + "</p>" : "")
                    + "<hr>"
                    + trendSummary
                    + "<hr>"
                    + "<p><b>" + recommendationTitle + ":</b><br>" + recommendationText.toString() + "</p>"
                    + "</div></html>";

            noteField.setText("");
            JOptionPane.showMessageDialog(
                    frame,
                    message,
                    "ErfolgsRechner – Ergebnis",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.add(calculate);
        buttonRow.add(showHistory);
        buttonRow.add(deleteHistoryButton);

        panel.add(buttonRow);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
