
package Java.ErfolgsRechner.model;

public class HistoryEntry {

    private final String timestamp;
    private final String date;
    private final String minName;
    private final double min;
    private final double success;
    private final String note;

    public HistoryEntry(String timestamp, String date, String minName, double min, double success, String note) {
        this.timestamp = timestamp;
        this.date = date;
        this.minName = minName;
        this.min = min;
        this.success = success;
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getMinName() {
        return minName;
    }

    public double getMin() {
        return min;
    }

    public double getSuccess() {
        return success;
    }

    public String getNote() {
        return note;
    }
}
