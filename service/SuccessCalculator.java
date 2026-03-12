
package Java.ErfolgsRechner.service;

import Java.ErfolgsRechner.model.CalculationResult;
import Java.ErfolgsRechner.model.LimitFactor;

import java.util.List;

public class SuccessCalculator {

    public CalculationResult calculate(double w, double c, double t, List<LimitFactor> limits) {
        String minName = "-";
        double minValue = 1.0;

        for (LimitFactor limit : limits) {
            if (limit.getValue() < minValue) {
                minValue = limit.getValue();
                minName = limit.getName();
            }
        }

        double base = (w * c + t);
        double successPercent = clamp((base * minValue) * 100.0, 0.0, 100.0);

        return new CalculationResult(
                w,
                c,
                t,
                minName,
                minValue,
                base,
                successPercent
        );
    }

    public double calculateSuccessPercent(double w, double c, double t, List<LimitFactor> limits) {
        return calculate(w, c, t, limits).getSuccessPercent();
    }

    public double calculateSuccessPercentWithValues(double w, double c, double t, List<Double> limitValues) {
        double minValue = 1.0;

        for (Double value : limitValues) {
            if (value != null && value < minValue) {
                minValue = value;
            }
        }

        double base = (w * c + t);
        return clamp((base * minValue) * 100.0, 0.0, 100.0);
    }

    public String getSuccessPhase(double successPercent) {
        if (successPercent < 20) {
            return "Engpassmodus";
        } else if (successPercent < 35) {
            return "Aufbauphase";
        } else if (successPercent < 60) {
            return "Produktive Phase";
        }
        return "Flow / Hochleistung";
    }

    public String shortenPhaseName(double successPercent) {
        if (successPercent < 20) {
            return "Engpass";
        }
        if (successPercent < 35) {
            return "Aufbau";
        }
        if (successPercent < 60) {
            return "Produktiv";
        }
        return "Flow";
    }

    public String formatFactorDisplayName(String key) {
        switch (key) {
            case "w":
                return "Zielklarheit";
            case "c":
                return "Konzentration";
            case "t":
                return "Zeit";
            default:
                return key;
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
