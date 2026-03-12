package Java.ErfolgsRechner;

import Java.ErfolgsRechner.model.HistoryEntry;
import Java.ErfolgsRechner.model.LimitFactor;
import Java.ErfolgsRechner.service.AssetService;
import Java.ErfolgsRechner.service.AnalysisResultService;
import Java.ErfolgsRechner.service.FontService;
import Java.ErfolgsRechner.service.HistoryService;
import Java.ErfolgsRechner.service.OverviewService;
import Java.ErfolgsRechner.service.RecommendationService;
import Java.ErfolgsRechner.service.StateService;
import Java.ErfolgsRechner.service.SuccessCalculator;
import Java.ErfolgsRechner.service.TopicService;
import Java.ErfolgsRechner.ui.HistoryChartPanel;
import Java.ErfolgsRechner.ui.HistoryPreviewChartPanel;
import Java.ErfolgsRechner.ui.MainFrame;
import Java.ErfolgsRechner.ui.DashboardPanel;
import Java.ErfolgsRechner.ui.InputPanel;
import Java.ErfolgsRechner.ui.RoundedButton;
import Java.ErfolgsRechner.ui.UIFactory;
import Java.ErfolgsRechner.ui.DialogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ErfolgsRechner {

    private static final FontService FONT_SERVICE = new FontService();
    private static final AssetService ASSET_SERVICE = new AssetService();
    private static final AnalysisResultService ANALYSIS_RESULT_SERVICE = new AnalysisResultService();
    private static final HistoryService HISTORY_SERVICE = new HistoryService();
    private static final StateService STATE_SERVICE = new StateService();
    private static final TopicService TOPIC_SERVICE = new TopicService();
    private static final SuccessCalculator SUCCESS_CALCULATOR = new SuccessCalculator();
    private static final RecommendationService RECOMMENDATION_SERVICE = new RecommendationService();
    private static final DialogFactory DIALOG_FACTORY = new DialogFactory();
    private static final OverviewService OVERVIEW_SERVICE = new OverviewService(
            HISTORY_SERVICE,
            RECOMMENDATION_SERVICE,
            SUCCESS_CALCULATOR
    );

    private static void loadNotoSansFont() {
        FONT_SERVICE.loadNotoSansFont();
    }

    private static JSlider createSlider() {
        return UIFactory.createSlider();
    }

    private static JLabel createValueLabel(JSlider slider) {
        return UIFactory.createValueLabel(slider);
    }

    private static String loadCurrentTopic() {
        return TOPIC_SERVICE.loadCurrentTopic();
    }

    private static void saveCurrentTopic(String topic) {
        TOPIC_SERVICE.saveCurrentTopic(topic);
    }

    private static void saveUiState(JSlider wSlider, JSlider cSlider, JSlider tSlider, List<LimitFactor> limits, JTextField noteField) {
        STATE_SERVICE.saveUiState(wSlider, cSlider, tSlider, limits, noteField);
    }

    private static void loadUiState(JSlider wSlider, JSlider cSlider, JSlider tSlider, List<LimitFactor> limits, JTextField noteField) {
        STATE_SERVICE.loadUiState(wSlider, cSlider, tSlider, limits, noteField);
    }


    private static void appendHistory(double w, double c, double t, List<LimitFactor> limits, String minName, double min, double base, double successPercent, String note) {
        HISTORY_SERVICE.appendHistory(w, c, t, limits, minName, min, base, successPercent, note);
    }

    private static String buildTrendSummary() {
        return HISTORY_SERVICE.buildTrendSummary();
    }



    private static void showHistoryChart(Component parent) {
        java.util.List<HistoryEntry> entries = HISTORY_SERVICE.readHistoryEntries();

        HistoryChartPanel chartPanel = new HistoryChartPanel(
            entries,
            HISTORY_SERVICE,
            (dialogParent, entry) -> {
                String shortDate;
                try {
                    shortDate = java.time.LocalDate.parse(entry.getDate())
                            .format(DateTimeFormatter.ofPattern("d.M."));
                } catch (Exception ex) {
                    shortDate = entry.getDate();
                }

                int choice = DIALOG_FACTORY.showStyledConfirmDialog(
                        dialogParent,
                        "Diesen Eintrag vom " + shortDate + " löschen?",
                        "Eintrag löschen"
                );

                return choice == JOptionPane.YES_OPTION;
            },
            (dialogParent) -> DIALOG_FACTORY.showStyledMessageDialog(
                    dialogParent,
                    "Der Eintrag konnte nicht gelöscht werden.",
                    "ErfolgsRechner – Verlauf"
            )
        );

        JScrollPane scrollPane = new JScrollPane(
                chartPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setPreferredSize(new Dimension(1220, 760));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        DIALOG_FACTORY.showStyledMessageDialog(
                parent,
                scrollPane,
                "ErfolgsRechner – Verlauf"
        );
    }


    private static boolean deleteHistory() {
        return HISTORY_SERVICE.deleteHistory();
    }

    private static boolean deleteUiState() {
        return STATE_SERVICE.deleteUiState();
    }

    private static boolean exportHistoryAsCsv(File targetFile) {
        return HISTORY_SERVICE.exportHistoryAsCsv(targetFile);
    }

    private static double calculateSuccessPercent(double w, double c, double t, List<LimitFactor> limits) {
        return SUCCESS_CALCULATOR.calculateSuccessPercent(w, c, t, limits);
    }

    private static double calculateSuccessPercentWithValues(double w, double c, double t, List<Double> limitValues) {
        return SUCCESS_CALCULATOR.calculateSuccessPercentWithValues(w, c, t, limitValues);
    }

    private static String getSuccessPhase(double successPercent) {
        return SUCCESS_CALCULATOR.getSuccessPhase(successPercent);
    }

    private static String pickRandomThought() {
        return ASSET_SERVICE.pickRandomThought();
    }

    private static ImageIcon loadArrowIcon(String filename, int width, int height) {
        return ASSET_SERVICE.loadArrowIcon(filename, width, height);
    }

    private static String buildRecommendationForLever(String leverName) {
        return RECOMMENDATION_SERVICE.buildRecommendationForLever(leverName);
    }

    private static String buildSecondLeverHint(String leverName) {
        return RECOMMENDATION_SERVICE.buildSecondLeverHint(leverName);
    }

    public static void startApp() {

        loadNotoSansFont();
        DIALOG_FACTORY.installPopupTheme();

        final String randomThought = pickRandomThought();
        final ImageIcon arrowUpIcon = loadArrowIcon("arrowUp.png", 22, 22);
        final ImageIcon arrowRightIcon = loadArrowIcon("arrowRight.png", 22, 22);
        final ImageIcon arrowDownIcon = loadArrowIcon("arrowDown.png", 22, 22);
        final String[] currentTopic = new String[]{loadCurrentTopic()};

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int desiredWidth = 1060;
        int desiredHeight = 1040;
        int frameWidth = Math.min(desiredWidth, screenSize.width - 40);
        int frameHeight = Math.min(desiredHeight, screenSize.height - 40);

        MainFrame frame = new MainFrame("ErfolgsRechner", frameWidth, frameHeight);

        JMenuBar menuBar = new JMenuBar();

        JMenu dateiMenu = new JMenu("Datei");
        JMenu hilfeMenu = new JMenu("Hilfe");

        JMenuItem verlaufLoeschenItem = new JMenuItem("Verlauf löschen");
        JMenuItem statusLoeschenItem = new JMenuItem("Statusdatei löschen");
        JMenuItem csvExportItem = new JMenuItem("CSV exportieren");
        JMenuItem beendenItem = new JMenuItem("Beenden");
        JMenuItem ueberItem = new JMenuItem("Über ErfolgsRechner");

        dateiMenu.add(verlaufLoeschenItem);
        dateiMenu.add(statusLoeschenItem);
        dateiMenu.add(csvExportItem);
        dateiMenu.addSeparator();
        dateiMenu.add(beendenItem);

        hilfeMenu.add(ueberItem);

        menuBar.add(dateiMenu);
        menuBar.add(hilfeMenu);
        frame.setJMenuBar(menuBar);

        JPanel panel = frame.getRootPanel();

        final int leftCardHeight = 940;
        final int rightColumnGap = 24;
        // Give the preview a bit more height and keep the overview more compact.
        final int previewCardHeight = 310;
        final int dashboardCardHeight = leftCardHeight - rightColumnGap - previewCardHeight;

        // limitierende Faktoren
        List<LimitFactor> limits = new ArrayList<>();
        limits.add(new LimitFactor("Energie", createSlider(), null));
        limits.add(new LimitFactor("Emotionale Stabilität", createSlider(), null));
        limits.add(new LimitFactor("Ressourcen", createSlider(), null));
        limits.add(new LimitFactor("Wissenstand", createSlider(), null));

        for (int i = 0; i < limits.size(); i++) {
            LimitFactor factor = limits.get(i);
            JLabel label = createValueLabel(factor.getSlider());
            limits.set(i, new LimitFactor(factor.getName(), factor.getSlider(), label));
        }

        // Main variables
        JSlider wSlider = createSlider();
        JLabel wValueLabel = createValueLabel(wSlider);

        JSlider cSlider = createSlider();
        JLabel cValueLabel = createValueLabel(cSlider);

        JSlider tSlider = createSlider();
        JLabel tValueLabel = createValueLabel(tSlider);

        JTextField noteField = new JTextField();
        loadUiState(wSlider, cSlider, tSlider, limits, noteField);

        JButton calculate = new RoundedButton(
                "Berechnen",
                15,
                new Color(5, 177, 159),
                Color.WHITE,
                new Color(5, 177, 159)
        );
        JButton showHistory = new RoundedButton(
                "Verlauf anzeigen",
                15,
                Color.WHITE,
                new Color(55, 65, 81),
                new Color(210, 214, 220)
        );
        JButton deleteHistoryButton = new RoundedButton(
                "Verlauf löschen",
                15,
                Color.WHITE,
                new Color(55, 65, 81),
                new Color(210, 214, 220)
        );
        calculate.setFocusPainted(false);
        calculate.setAlignmentX(Component.LEFT_ALIGNMENT);
        showHistory.setFocusPainted(false);
        showHistory.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteHistoryButton.setFocusPainted(false);
        deleteHistoryButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        calculate.setFont(new Font("Noto Sans", Font.BOLD, 18));
        calculate.setPreferredSize(new Dimension(260, 52));
        calculate.setMaximumSize(new Dimension(260, 52));

        showHistory.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        showHistory.setPreferredSize(new Dimension(180, 42));
        showHistory.setMaximumSize(new Dimension(180, 42));

        deleteHistoryButton.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        deleteHistoryButton.setPreferredSize(new Dimension(180, 42));
        deleteHistoryButton.setMaximumSize(new Dimension(180, 42));

        showHistory.setBackground(Color.WHITE);
        showHistory.setForeground(new Color(55, 65, 81));
        showHistory.setOpaque(true);
        showHistory.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        showHistory.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        deleteHistoryButton.setBackground(Color.WHITE);
        deleteHistoryButton.setForeground(new Color(55, 65, 81));
        deleteHistoryButton.setOpaque(true);
        deleteHistoryButton.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        deleteHistoryButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        calculate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showHistory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteHistoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (calculate instanceof RoundedButton) {
            ((RoundedButton) calculate).setHoverColors(
                    new Color(4, 160, 143),
                    Color.WHITE
            );
        }

        if (showHistory instanceof RoundedButton) {
            ((RoundedButton) showHistory).setHoverColors(
                    new Color(245, 247, 250),
                    new Color(34, 39, 46)
            );
        }

        if (deleteHistoryButton instanceof RoundedButton) {
            ((RoundedButton) deleteHistoryButton).setHoverColors(
                    new Color(245, 247, 250),
                    new Color(34, 39, 46)
            );
        }

        showHistory.addActionListener((ActionEvent e) -> showHistoryChart(frame));

        Runnable deleteHistoryAction = () -> {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Willst du den gespeicherten Verlauf wirklich löschen?",
                    "Verlauf löschen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean deleted = deleteHistory();
                DIALOG_FACTORY.showStyledMessageDialog(
                        frame,
                        deleted
                                ? "Der Verlauf wurde gelöscht."
                                : "Es war kein Verlauf vorhanden oder er konnte nicht gelöscht werden.",
                        "ErfolgsRechner – Verlauf"
                );
                panel.revalidate();
                panel.repaint();
            }
        };

        deleteHistoryButton.addActionListener((ActionEvent e) -> deleteHistoryAction.run());
        verlaufLoeschenItem.addActionListener((ActionEvent e) -> deleteHistoryAction.run());

        statusLoeschenItem.addActionListener((ActionEvent e) -> {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Willst du die gespeicherten Slider-Stände wirklich löschen?",
                    "Statusdatei löschen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean deleted = deleteUiState();
                DIALOG_FACTORY.showStyledMessageDialog(
                        frame,
                        deleted
                                ? "Die gespeicherten UI-Werte wurden gelöscht."
                                : "Es war keine Statusdatei vorhanden oder sie konnte nicht gelöscht werden.",
                        "ErfolgsRechner – Status"
                );
            }
        });

        beendenItem.addActionListener((ActionEvent e) -> {
            saveUiState(wSlider, cSlider, tSlider, limits, noteField);
            frame.dispose();
        });

        ueberItem.addActionListener((ActionEvent e) -> DIALOG_FACTORY.showStyledMessageDialog(
                frame,
                "<html><div style='font-family:sans-serif; width:320px;'>"
                        + "<h2 style='margin-top:0;'>ErfolgsRechner</h2>"
                        + "<p>Ein kleines Tool zur Analyse von Hebeln bei der Zielerreichung.</p>"
                        + "<p><b>Version:</b> 1.2</p>"
                        + "<p>Es zeigt, welcher Faktor aktuell den größten Einfluss auf dein Ergebnis hat und visualisiert den Verlauf über Zeit.</p>"
                        + "</div></html>",
                "Über ErfolgsRechner"
        ));

        final HistoryPreviewChartPanel previewChart = new HistoryPreviewChartPanel(HISTORY_SERVICE);
        final JLabel topicValue = new JLabel("-");
        final JLabel timestampValue = new JLabel("-");
        final JLabel phaseValue = new JLabel("-");
        final JLabel trendValue = new JLabel("neutral");
        final JLabel trendIconLabel = new JLabel();
        final JTextArea recommendationValue = new JTextArea();
        final JLabel leverValue = new JLabel("-");
        final JLabel thoughtValue = new JLabel();
        topicValue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topicValue.setToolTipText("Klicken, um ein Thema zu hinterlegen");

        calculate.addActionListener((ActionEvent e) -> {
            double w = wSlider.getValue() / 100.0;
            double c = cSlider.getValue() / 100.0;
            double t = tSlider.getValue() / 100.0;
            String note = noteField.getText() == null ? "" : noteField.getText().trim();
            if (note.length() > 130) {
                note = note.substring(0, 130);
            }

            double min = 1.0;
            for (LimitFactor l : limits) {
                if (l.getValue() < min) {
                    min = l.getValue();
                }
            }

            double base = (w * c + t);

            double r = base * min;
            double successPercent = r * 100;
            if (successPercent > 100) successPercent = 100;
            if (successPercent < 0) successPercent = 0;

            String bestLeverName = "";
            double bestLeverCurrentValue = 0;
            double bestLeverImprovedValue = 0;
            double bestLeverResult = successPercent;
            double bestLeverDelta = Double.NEGATIVE_INFINITY;

            String secondLeverName = "";
            double secondLeverDelta = Double.NEGATIVE_INFINITY;

            double candidateW = Math.min(1.0, w + 0.10);
            double candidateWResult = calculateSuccessPercent(candidateW, c, t, limits);
            double candidateWDelta = candidateWResult - successPercent;
            if (candidateWDelta > bestLeverDelta) {
                secondLeverDelta = bestLeverDelta;
                secondLeverName = bestLeverName;
                bestLeverDelta = candidateWDelta;
                bestLeverName = "Zielklarheit";
                bestLeverCurrentValue = w * 100;
                bestLeverImprovedValue = candidateW * 100;
                bestLeverResult = candidateWResult;
            } else if (candidateWDelta > secondLeverDelta) {
                secondLeverDelta = candidateWDelta;
                secondLeverName = "Zielklarheit";
            }

            double candidateC = Math.min(1.0, c + 0.10);
            double candidateCResult = calculateSuccessPercent(w, candidateC, t, limits);
            double candidateCDelta = candidateCResult - successPercent;
            if (candidateCDelta > bestLeverDelta) {
                secondLeverDelta = bestLeverDelta;
                secondLeverName = bestLeverName;
                bestLeverDelta = candidateCDelta;
                bestLeverName = "Konzentration";
                bestLeverCurrentValue = c * 100;
                bestLeverImprovedValue = candidateC * 100;
                bestLeverResult = candidateCResult;
            } else if (candidateCDelta > secondLeverDelta) {
                secondLeverDelta = candidateCDelta;
                secondLeverName = "Konzentration";
            }

            double candidateT = Math.min(1.0, t + 0.10);
            double candidateTResult = calculateSuccessPercent(w, c, candidateT, limits);
            double candidateTDelta = candidateTResult - successPercent;
            if (candidateTDelta > bestLeverDelta) {
                secondLeverDelta = bestLeverDelta;
                secondLeverName = bestLeverName;
                bestLeverDelta = candidateTDelta;
                bestLeverName = "Zeit";
                bestLeverCurrentValue = t * 100;
                bestLeverImprovedValue = candidateT * 100;
                bestLeverResult = candidateTResult;
            } else if (candidateTDelta > secondLeverDelta) {
                secondLeverDelta = candidateTDelta;
                secondLeverName = "Zeit";
            }

            for (int i = 0; i < limits.size(); i++) {
                LimitFactor factor = limits.get(i);
                List<Double> candidateLimitValues = new ArrayList<>();
                for (int j = 0; j < limits.size(); j++) {
                    double value = limits.get(j).getValue();
                    if (i == j) {
                        value = Math.min(1.0, value + 0.10);
                    }
                    candidateLimitValues.add(value);
                }

                double candidateResult = calculateSuccessPercentWithValues(w, c, t, candidateLimitValues);
                double candidateDelta = candidateResult - successPercent;
                if (candidateDelta > bestLeverDelta) {
                    secondLeverDelta = bestLeverDelta;
                    secondLeverName = bestLeverName;
                    bestLeverDelta = candidateDelta;
                    bestLeverName = factor.getName();
                    bestLeverCurrentValue = factor.getValue() * 100;
                    bestLeverImprovedValue = Math.min(1.0, factor.getValue() + 0.10) * 100;
                    bestLeverResult = candidateResult;
                } else if (candidateDelta > secondLeverDelta) {
                    secondLeverDelta = candidateDelta;
                    secondLeverName = factor.getName();
                }
            }

            appendHistory(w, c, t, limits, bestLeverName, min, base, successPercent, note);
            saveUiState(wSlider, cSlider, tSlider, limits, noteField);

            String trendSummary = buildTrendSummary();

            StringBuilder recommendationText = new StringBuilder(
                    buildRecommendationForLever(bestLeverName)
            );

            if (!secondLeverName.isEmpty() && !secondLeverName.equals(bestLeverName)) {
                recommendationText.append(buildSecondLeverHint(secondLeverName));
            }

            String successPhase = getSuccessPhase(successPercent);

            String leverText = ANALYSIS_RESULT_SERVICE.buildLeverText(
                    bestLeverName,
                    bestLeverCurrentValue,
                    bestLeverImprovedValue,
                    successPercent,
                    bestLeverResult,
                    bestLeverDelta
            );

            String message = ANALYSIS_RESULT_SERVICE.buildResultHtml(
                    successPhase,
                    bestLeverName,
                    bestLeverCurrentValue,
                    bestLeverDelta,
                    base,
                    successPercent,
                    note,
                    leverText,
                    trendSummary,
                    recommendationText.toString()
            );

            noteField.setText("");
            previewChart.repaint();
                    OVERVIEW_SERVICE.updateOverviewCard(
                            topicValue,
                            timestampValue,
                            phaseValue,
                            trendValue,
                            trendIconLabel,
                            leverValue,
                            recommendationValue,
                            thoughtValue,
                            randomThought,
                            arrowUpIcon,
                            arrowRightIcon,
                            arrowDownIcon,
                            currentTopic[0]
                    );
            DIALOG_FACTORY.showStyledMessageDialog(
                    frame,
                    message,
                    "ErfolgsRechner – Ergebnis"
            );
        });
        InputPanel inputPanel = new InputPanel(
                limits,
                wSlider,
                wValueLabel,
                cSlider,
                cValueLabel,
                tSlider,
                tValueLabel,
                noteField,
                calculate
        );
        inputPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel dashboardCard = DashboardPanel.createDashboardCard(
                dashboardCardHeight,
                topicValue,
                timestampValue,
                phaseValue,
                trendValue,
                trendIconLabel,
                leverValue,
                recommendationValue,
                thoughtValue
        );

        JPanel previewCard = DashboardPanel.createPreviewCard(
                previewChart,
                showHistory,
                previewCardHeight
        );

        DashboardPanel dashboardPanel = new DashboardPanel(
                dashboardCard,
                previewCard,
                rightColumnGap,
                leftCardHeight
        );

        topicValue.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String input = JOptionPane.showInputDialog(
                        frame,
                        "Aktuelles Thema eingeben:",
                        currentTopic[0] == null ? "" : currentTopic[0]
                );
                if (input != null) {
                    currentTopic[0] = input.trim();
                    saveCurrentTopic(currentTopic[0]);
                    OVERVIEW_SERVICE.updateOverviewCard(
                        topicValue,
                        timestampValue,
                        phaseValue,
                        trendValue,
                        trendIconLabel,
                        leverValue,
                        recommendationValue,
                        thoughtValue,
                        randomThought,
                        arrowUpIcon,
                        arrowRightIcon,
                        arrowDownIcon,
                        currentTopic[0]
                );
                }
            }
        });

        OVERVIEW_SERVICE.updateOverviewCard(
                topicValue,
                timestampValue,
                phaseValue,
                trendValue,
                trendIconLabel,
                leverValue,
                recommendationValue,
                thoughtValue,
                randomThought,
                arrowUpIcon,
                arrowRightIcon,
                arrowDownIcon,
                currentTopic[0]
        );
        panel.add(inputPanel, BorderLayout.WEST);
        panel.add(dashboardPanel, BorderLayout.CENTER);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveUiState(wSlider, cSlider, tSlider, limits, noteField);
            }
        });
        csvExportItem.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Verlauf als CSV exportieren");
            chooser.setSelectedFile(new File("erfolgsrechner-verlauf.csv"));

            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                boolean exported = exportHistoryAsCsv(selectedFile);

                DIALOG_FACTORY.showStyledMessageDialog(
                        frame,
                        exported
                                ? "Der Verlauf wurde als CSV exportiert."
                                : "Es sind keine Verlaufsdaten vorhanden oder der Export ist fehlgeschlagen.",
                        "ErfolgsRechner – CSV Export"
                );
            }
        });
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        startApp();
    }
}