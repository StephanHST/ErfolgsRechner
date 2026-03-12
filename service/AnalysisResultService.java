
package Java.ErfolgsRechner.service;

import java.text.DecimalFormat;

public class AnalysisResultService {

    public String buildLeverText(
            String bestLeverName,
            double bestLeverCurrentValue,
            double bestLeverImprovedValue,
            double successPercent,
            double bestLeverResult,
            double bestLeverDelta
    ) {
        DecimalFormat df = new DecimalFormat("0.00");

        return "Der aktuell größte Hebel ist " + bestLeverName
                + ". Wenn du diesen Wert um 10 Prozentpunkte verbesserst ("
                + df.format(bestLeverCurrentValue) + "% → "
                + df.format(bestLeverImprovedValue) + "%), steigt deine Erfolgswahrscheinlichkeit von "
                + df.format(successPercent) + "% auf "
                + df.format(bestLeverResult) + "% ("
                + (bestLeverDelta >= 0 ? "+" : "")
                + df.format(bestLeverDelta) + " Prozentpunkte).";
    }

    public String buildResultHtml(
            String successPhase,
            String bestLeverName,
            double bestLeverCurrentValue,
            double bestLeverDelta,
            double base,
            double successPercent,
            String note,
            String leverText,
            String trendSummary,
            String recommendationText
    ) {
        DecimalFormat df = new DecimalFormat("0.00");
        String safeNote = note == null ? "" : note;
        String safeTrendSummary = trendSummary == null ? "" : trendSummary.replace("<p>", "").replace("</p>", "");
        String safeRecommendationText = recommendationText == null ? "" : recommendationText;

        return "<html>"
                + "<div style='font-family:sans-serif; padding:14px 16px; width:500px; color:#222;'>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Analyse deines aktuellen Zustands</div>"
                + "<h2 style='margin:0 0 12px 0;'>Ergebnisanalyse | " + successPhase + "</h2>"

                + "<div style='margin-bottom:10px; padding:10px 12px; border:1px solid #d8d8d8; background:#f7f7f7;'>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Stärkster aktueller Hebel</div>"
                + "<div style='font-size:18px; font-weight:bold; margin-bottom:6px;'>" + bestLeverName + "</div>"
                + "<div><b>Aktueller Wert:</b> " + df.format(bestLeverCurrentValue) + "%</div>"
                + "<div><b>Potenzial bei +10 Punkten:</b> " + (bestLeverDelta >= 0 ? "+" : "") + df.format(bestLeverDelta) + " Prozentpunkte</div>"
                + "</div>"

                + "<div style='margin-bottom:10px;'>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Leistungsbild</div>"
                + "<div><b>Basisleistung (w × c + t):</b> " + df.format(base) + "</div>"
                + "<div><b>Erwartete Erfolgswahrscheinlichkeit:</b> " + df.format(successPercent) + "%</div>"
                + (!safeNote.isEmpty() ? "<div><b>Notiz:</b> " + safeNote + "</div>" : "")
                + "</div>"

                + "<hr style='border:none; border-top:1px solid #dcdcdc; margin:10px 0;'>"
                + "<div style='margin-bottom:10px;'>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Wirkung einer kleinen Verbesserung</div>"
                + "<div>" + leverText + "</div>"
                + "</div>"

                + "<hr style='border:none; border-top:1px solid #dcdcdc; margin:10px 0;'>"
                + "<div style='margin-bottom:10px;'>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Verlauf</div>"
                + "<div>" + safeTrendSummary + "</div>"
                + "</div>"

                + "<hr style='border:none; border-top:1px solid #dcdcdc; margin:10px 0;'>"
                + "<div>"
                + "<div style='font-size:13px; color:#666; margin-bottom:4px;'>Empfehlung für heute</div>"
                + "<div>" + safeRecommendationText + "</div>"
                + "</div>"
                + "</div></html>";
    }
}
