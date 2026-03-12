package Java.ErfolgsRechner.util;

public final class CsvUtils {

    private CsvUtils() {
    }

    public static String csvEscape(String value) {
        String safe = value == null ? "" : value;
        safe = safe.replace("\r", " ").replace("\n", " ");

        if (safe.contains(",") || safe.contains("\"") || safe.contains(";")) {
            safe = safe.replace("\"", "\"\"");
            return "\"" + safe + "\"";
        }

        return safe;
    }
}
