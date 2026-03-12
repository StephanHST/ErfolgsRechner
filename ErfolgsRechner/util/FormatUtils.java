
package Java.ErfolgsRechner.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatUtils {

    private FormatUtils() {
    }

    public static String formatOverviewTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty() || "-".equals(timestamp)) {
            return "-";
        }

        try {
            LocalDateTime dt = LocalDateTime.parse(
                    timestamp,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm 'Uhr'"));
        } catch (Exception ex) {
            return timestamp;
        }
    }
}
