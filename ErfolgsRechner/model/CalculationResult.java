package Java.ErfolgsRechner.model;

public class CalculationResult {

    private final double w;
    private final double c;
    private final double t;

    private final String minName;
    private final double minValue;

    private final double base;
    private final double successPercent;

    public CalculationResult(
            double w,
            double c,
            double t,
            String minName,
            double minValue,
            double base,
            double successPercent
    ) {
        this.w = w;
        this.c = c;
        this.t = t;
        this.minName = minName;
        this.minValue = minValue;
        this.base = base;
        this.successPercent = successPercent;
    }

    public double getW() {
        return w;
    }

    public double getC() {
        return c;
    }

    public double getT() {
        return t;
    }

    public String getMinName() {
        return minName;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getBase() {
        return base;
    }

    public double getSuccessPercent() {
        return successPercent;
    }
}
