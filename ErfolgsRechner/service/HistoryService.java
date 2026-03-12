
package Java.ErfolgsRechner.service;

import Java.ErfolgsRechner.model.HistoryEntry;
import Java.ErfolgsRechner.model.LimitFactor;
import Java.ErfolgsRechner.util.CsvUtils;
import Java.ErfolgsRechner.util.ParseUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {

    public Path getHistoryFilePath() {
        return Paths.get(System.getProperty("user.home"), ".erfolgsrechner-history.txt");
    }

    public void appendHistory(
            double w,
            double c,
            double t,
            List<LimitFactor> limits,
            String minName,
            double min,
            double base,
            double successPercent,
            String note
    ) {
        Path historyFile = getHistoryFilePath();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder line = new StringBuilder();
        line.append(LocalDateTime.now().format(formatter)).append(";");
        line.append("w=").append(String.format(java.util.Locale.US, "%.2f", w)).append(";");
        line.append("c=").append(String.format(java.util.Locale.US, "%.2f", c)).append(";");
        line.append("t=").append(String.format(java.util.Locale.US, "%.2f", t)).append(";");

        for (LimitFactor limit : limits) {
            line.append(limit.getName())
                    .append("=")
                    .append(String.format(java.util.Locale.US, "%.2f", limit.getValue()))
                    .append(";");
        }

        line.append("minName=").append(minName).append(";");
        line.append("min=").append(String.format(java.util.Locale.US, "%.2f", min)).append(";");
        line.append("base=").append(String.format(java.util.Locale.US, "%.2f", base)).append(";");
        line.append("success=").append(String.format(java.util.Locale.US, "%.2f", successPercent)).append(";");

        String safeNote = note == null
                ? ""
                : note.replace(";", ",")
                      .replace("\n", " ")
                      .replace("\r", " ")
                      .trim();
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

    public String buildTrendSummary() {
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

            double lastSuccess = ParseUtils.extractValue(last, "success");
            double prevSuccess = ParseUtils.extractValue(previous, "success");
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

    public List<HistoryEntry> readHistoryEntries() {
        Path historyFile = getHistoryFilePath();
        List<HistoryEntry> entries = new ArrayList<>();

        if (!Files.exists(historyFile)) {
            return entries;
        }

        try {
            List<String> lines = Files.readAllLines(historyFile);
            for (String line : lines) {
                String timestamp = ParseUtils.extractTimestamp(line);
                String date = timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
                String minName = ParseUtils.extractTextValue(line, "minName");
                double min = ParseUtils.extractValue(line, "min");
                double success = ParseUtils.extractValue(line, "success");
                String note = ParseUtils.extractTextValue(line, "note");

                entries.add(new HistoryEntry(timestamp, date, minName, min, success, note));
            }
        } catch (IOException ignored) {
        }

        return entries;
    }

    public boolean deleteHistory() {
        Path historyFile = getHistoryFilePath();
        try {
            return Files.deleteIfExists(historyFile);
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean deleteHistoryEntryByTimestamp(String timestampToDelete) {
        Path historyFile = getHistoryFilePath();
        if (!Files.exists(historyFile)) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(historyFile);
            List<String> updated = new ArrayList<>();
            boolean removed = false;

            for (String line : lines) {
                String timestamp = ParseUtils.extractTimestamp(line);
                if (!removed && timestamp.equals(timestampToDelete)) {
                    removed = true;
                    continue;
                }
                updated.add(line);
            }

            if (removed) {
                Files.write(
                        historyFile,
                        updated,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.CREATE
                );
            }

            return removed;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean exportHistoryAsCsv(File targetFile) {
        List<HistoryEntry> entries = readHistoryEntries();
        if (entries.isEmpty()) {
            return false;
        }

        Path targetPath = targetFile.toPath();
        try (BufferedWriter writer = Files.newBufferedWriter(
                targetPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write("timestamp,date,hebel,minWert,erfolgswahrscheinlichkeit,notiz");
            writer.newLine();

            for (HistoryEntry entry : entries) {
                writer.write(CsvUtils.csvEscape(entry.getTimestamp()));
                writer.write(",");
                writer.write(CsvUtils.csvEscape(entry.getDate()));
                writer.write(",");
                writer.write(CsvUtils.csvEscape(entry.getMinName()));
                writer.write(",");
                writer.write(String.format(java.util.Locale.US, "%.2f", entry.getMin() * 100));
                writer.write(",");
                writer.write(String.format(java.util.Locale.US, "%.2f", entry.getSuccess()));
                writer.write(",");
                writer.write(CsvUtils.csvEscape(entry.getNote() == null ? "" : entry.getNote()));
            }

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
