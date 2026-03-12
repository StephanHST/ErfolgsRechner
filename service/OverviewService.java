package Java.ErfolgsRechner.service;

import Java.ErfolgsRechner.model.HistoryEntry;
import Java.ErfolgsRechner.util.FormatUtils;
import Java.ErfolgsRechner.util.ParseUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OverviewService {

    private final HistoryService historyService;
    private final RecommendationService recommendationService;
    private final SuccessCalculator successCalculator;

    public OverviewService(
            HistoryService historyService,
            RecommendationService recommendationService,
            SuccessCalculator successCalculator
    ) {
        this.historyService = historyService;
        this.recommendationService = recommendationService;
        this.successCalculator = successCalculator;
    }

    public void updateOverviewCard(
            JLabel topicValue,
            JLabel timestampValue,
            JLabel phaseValue,
            JLabel trendValue,
            JLabel trendIconLabel,
            JLabel leverValue,
            JTextArea recommendationValue,
            JLabel thoughtValue,
            String randomThought,
            ImageIcon arrowUpIcon,
            ImageIcon arrowRightIcon,
            ImageIcon arrowDownIcon,
            String manualTopic
    ) {
        List<HistoryEntry> entries = historyService.readHistoryEntries();
        DecimalFormat df = new DecimalFormat("0");

        thoughtValue.setText(
                "<html><div style='text-align:center; width:260px;'>\""
                        + randomThought
                        + "\"</div></html>"
        );
        thoughtValue.setHorizontalAlignment(SwingConstants.CENTER);
        thoughtValue.setVerticalAlignment(SwingConstants.CENTER);

        if (entries.isEmpty()) {
            topicValue.setText((manualTopic != null && !manualTopic.trim().isEmpty()) ? manualTopic.trim() : "Noch kein Thema hinterlegt");
            timestampValue.setText("-");
            phaseValue.setText("-");
            trendValue.setText("neutral");
            trendIconLabel.setIcon(arrowRightIcon);
            leverValue.setText("-");
            recommendationValue.setText("Bitte zuerst eine Messung durchführen.");
            return;
        }

        HistoryEntry latest = entries.get(entries.size() - 1);
        double delta = 0;
        if (entries.size() >= 2) {
            delta = latest.getSuccess() - entries.get(entries.size() - 2).getSuccess();
        }

        if (manualTopic != null && !manualTopic.trim().isEmpty()) {
            topicValue.setText(manualTopic.trim());
        } else {
            topicValue.setText((latest.getNote() != null && !latest.getNote().trim().isEmpty()) ? latest.getNote() : "-");
        }

        timestampValue.setText(FormatUtils.formatOverviewTimestamp(latest.getTimestamp()));
        phaseValue.setText(successCalculator.shortenPhaseName(latest.getSuccess()) + " | " + df.format(latest.getSuccess()) + "%");

        if (delta > 3) {
            trendValue.setText("steigend");
            trendIconLabel.setIcon(arrowUpIcon);
            trendValue.setForeground(new Color(102, 187, 106));
        } else if (delta < -3) {
            trendValue.setText("fallend");
            trendIconLabel.setIcon(arrowDownIcon);
            trendValue.setForeground(new Color(220, 90, 90));
        } else {
            trendValue.setText("stabil");
            trendIconLabel.setIcon(arrowRightIcon);
            trendValue.setForeground(new Color(120, 128, 140));
        }

        leverValue.setText(latest.getMinName() == null || latest.getMinName().trim().isEmpty() ? "-" : latest.getMinName());

        String recommendation = recommendationService.buildRecommendationForLever(latest.getMinName());

        String secondLeverName = "";
        double secondLeverDelta = Double.NEGATIVE_INFINITY;
        String bestLeverName = latest.getMinName() == null ? "" : latest.getMinName();
        double successPercent = latest.getSuccess();

        if (!entries.isEmpty()) {
            String lastLine = null;
            Path historyFile = historyService.getHistoryFilePath();

            try {
                List<String> lines = Files.readAllLines(historyFile);
                if (!lines.isEmpty()) {
                    lastLine = lines.get(lines.size() - 1);
                }
            } catch (Exception ignored) {
            }

            if (lastLine != null) {
                double w = ParseUtils.extractValue(lastLine, "w");
                double c = ParseUtils.extractValue(lastLine, "c");
                double t = ParseUtils.extractValue(lastLine, "t");

                LinkedHashMap<String, Double> limitValues = new LinkedHashMap<>();
                limitValues.put("Energie", ParseUtils.extractValue(lastLine, "Energie"));
                limitValues.put("Emotionale Stabilität", ParseUtils.extractValue(lastLine, "Emotionale Stabilität"));
                limitValues.put("Ressourcen", ParseUtils.extractValue(lastLine, "Ressourcen"));
                limitValues.put("Wissenstand", ParseUtils.extractValue(lastLine, "Wissenstand"));

                double bestLeverDelta = Double.NEGATIVE_INFINITY;

                double candidateW = Math.min(1.0, w + 0.10);
                double candidateWResult = successCalculator.calculateSuccessPercentWithValues(candidateW, c, t, new ArrayList<>(limitValues.values()));
                double candidateWDelta = candidateWResult - successPercent;
                if (candidateWDelta > bestLeverDelta) {
                    secondLeverDelta = bestLeverDelta;
                    secondLeverName = bestLeverName;
                    bestLeverDelta = candidateWDelta;
                    bestLeverName = "Zielklarheit";
                } else if (candidateWDelta > secondLeverDelta) {
                    secondLeverDelta = candidateWDelta;
                    secondLeverName = "Zielklarheit";
                }

                double candidateC = Math.min(1.0, c + 0.10);
                double candidateCResult = successCalculator.calculateSuccessPercentWithValues(w, candidateC, t, new ArrayList<>(limitValues.values()));
                double candidateCDelta = candidateCResult - successPercent;
                if (candidateCDelta > bestLeverDelta) {
                    secondLeverDelta = bestLeverDelta;
                    secondLeverName = bestLeverName;
                    bestLeverDelta = candidateCDelta;
                    bestLeverName = "Konzentration";
                } else if (candidateCDelta > secondLeverDelta) {
                    secondLeverDelta = candidateCDelta;
                    secondLeverName = "Konzentration";
                }

                double candidateT = Math.min(1.0, t + 0.10);
                double candidateTResult = successCalculator.calculateSuccessPercentWithValues(w, c, candidateT, new ArrayList<>(limitValues.values()));
                double candidateTDelta = candidateTResult - successPercent;
                if (candidateTDelta > bestLeverDelta) {
                    secondLeverDelta = bestLeverDelta;
                    secondLeverName = bestLeverName;
                    bestLeverDelta = candidateTDelta;
                    bestLeverName = "Zeit";
                } else if (candidateTDelta > secondLeverDelta) {
                    secondLeverDelta = candidateTDelta;
                    secondLeverName = "Zeit";
                }

                for (Map.Entry<String, Double> entry : limitValues.entrySet()) {
                    LinkedHashMap<String, Double> candidate = new LinkedHashMap<>(limitValues);
                    candidate.put(entry.getKey(), Math.min(1.0, entry.getValue() + 0.10));
                    double candidateResult = successCalculator.calculateSuccessPercentWithValues(w, c, t, new ArrayList<>(candidate.values()));
                    double candidateDelta = candidateResult - successPercent;
                    if (candidateDelta > bestLeverDelta) {
                        secondLeverDelta = bestLeverDelta;
                        secondLeverName = bestLeverName;
                        bestLeverDelta = candidateDelta;
                        bestLeverName = entry.getKey();
                    } else if (candidateDelta > secondLeverDelta) {
                        secondLeverDelta = candidateDelta;
                        secondLeverName = entry.getKey();
                    }
                }
            }
        }

        if (!secondLeverName.isEmpty() && !secondLeverName.equals(latest.getMinName())) {
            recommendation += recommendationService.buildSecondLeverHint(secondLeverName);
        }

        recommendationValue.setText(recommendation);
    }
}