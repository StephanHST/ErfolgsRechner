
package Java.ErfolgsRechner.util;

public final class ParseUtils {

    private ParseUtils() {
    }

    public static double extractValue(String line, String key) {
        if (line == null || key == null) {
            return 0;
        }

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

    public static String extractTextValue(String line, String key) {
        if (line == null || key == null) {
            return "-";
        }

        String[] parts = line.split(";");
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                return part.substring((key + "=").length());
            }
        }
        return "-";
    }

    public static String extractTimestamp(String line) {
        if (line == null) {
            return "-";
        }

        String[] parts = line.split(";");
        return parts.length > 0 ? parts[0] : "-";
    }
}
