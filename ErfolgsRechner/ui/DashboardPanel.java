package Java.ErfolgsRechner.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(
            JPanel dashboardCard,
            JPanel previewCard,
            int rightColumnGap,
            int leftCardHeight
    ) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        setPreferredSize(new Dimension(460, leftCardHeight));
        setMinimumSize(new Dimension(460, leftCardHeight));
        setMaximumSize(new Dimension(460, leftCardHeight));
        setAlignmentY(Component.TOP_ALIGNMENT);

        add(dashboardCard);
        add(Box.createVerticalStrut(rightColumnGap));
        add(previewCard);
    }

    public static JPanel createDashboardCard(
            int dashboardCardHeight,
            JLabel topicValue,
            JLabel timestampValue,
            JLabel phaseValue,
            JLabel trendValue,
            JLabel trendIconLabel,
            JLabel leverValue,
            JTextArea recommendationValue,
            JLabel thoughtValue
    ) {
        JPanel dashboardCard = new RoundedCardPanel(
                25,
                Color.WHITE,
                new Color(230, 234, 240)
        );
        dashboardCard.setLayout(new BorderLayout(0, 0));
        dashboardCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardCard.setPreferredSize(new Dimension(430, dashboardCardHeight));
        dashboardCard.setMinimumSize(new Dimension(430, dashboardCardHeight));
        dashboardCard.setMaximumSize(new Dimension(430, dashboardCardHeight));

        JLabel dashboardTitle = new JLabel("Überblick");
        dashboardTitle.setFont(new Font("Noto Sans", Font.BOLD, 18));
        dashboardTitle.setForeground(new Color(55, 65, 81));
        dashboardTitle.setHorizontalAlignment(SwingConstants.LEFT);
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel dashboardTop = new JPanel(new BorderLayout());
        dashboardTop.setOpaque(false);
        dashboardTop.add(dashboardTitle, BorderLayout.WEST);

        JPanel dashboardValues = new JPanel();
        dashboardValues.setOpaque(false);
        dashboardValues.setLayout(new BoxLayout(dashboardValues, BoxLayout.Y_AXIS));
        dashboardValues.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel dashboardContent = new JPanel();
        dashboardContent.setOpaque(false);
        dashboardContent.setLayout(new BoxLayout(dashboardContent, BoxLayout.Y_AXIS));
        dashboardContent.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        dashboardCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardCard.add(dashboardTop, BorderLayout.NORTH);
        dashboardCard.add(dashboardContent, BorderLayout.CENTER);

        Color dashboardLabelColor = new Color(120, 128, 140);
        Color dashboardValueColor = new Color(5, 177, 159);
        Color dashboardTextColor = new Color(55, 65, 81);

        JLabel topicLabel = new JLabel("aktuelles Thema");
        topicLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        topicLabel.setForeground(dashboardLabelColor);
        topicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardValues.add(topicLabel);

        topicValue.setText("-");
        topicValue.setFont(new Font("Noto Sans", Font.PLAIN, 18));
        topicValue.setForeground(dashboardValueColor);
        topicValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        topicValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        topicValue.setPreferredSize(new Dimension(360, 28));
        dashboardValues.add(topicValue);
        dashboardValues.add(Box.createVerticalStrut(10));

        JLabel timestampLabel = new JLabel("letzter Check in");
        timestampLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        timestampLabel.setForeground(dashboardLabelColor);
        timestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardValues.add(timestampLabel);

        timestampValue.setText("-");
        timestampValue.setFont(new Font("Noto Sans", Font.BOLD, 20));
        timestampValue.setForeground(new Color(34, 39, 46));
        timestampValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        timestampValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dashboardValues.add(timestampValue);
        dashboardValues.add(Box.createVerticalStrut(18));

        JLabel phaseLabel = new JLabel("aktuelle Phase");
        phaseLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        phaseLabel.setForeground(dashboardLabelColor);
        phaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardValues.add(phaseLabel);

        JPanel phaseRow = new JPanel(new BorderLayout(12, 0));
        phaseRow.setOpaque(false);
        phaseRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        phaseRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        phaseValue.setFont(new Font("Noto Sans", Font.PLAIN, 18));
        phaseValue.setForeground(dashboardValueColor);

        JPanel trendRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        trendRow.setOpaque(false);
        trendValue.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        trendRow.add(trendValue);
        trendRow.add(trendIconLabel);

        phaseRow.add(phaseValue, BorderLayout.WEST);
        phaseRow.add(trendRow, BorderLayout.EAST);

        dashboardValues.add(phaseRow);
        dashboardValues.add(Box.createVerticalStrut(14));

        JLabel leverLabel = new JLabel("aktueller Hebel");
        leverLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        leverLabel.setForeground(dashboardLabelColor);
        leverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardValues.add(leverLabel);

        leverValue.setFont(new Font("Noto Sans", Font.PLAIN, 18));
        leverValue.setForeground(dashboardValueColor);
        leverValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        leverValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dashboardValues.add(leverValue);

        dashboardValues.add(Box.createVerticalStrut(24));

        JLabel recLabel = new JLabel("Empfehlung");
        recLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        recLabel.setForeground(dashboardLabelColor);
        recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardContent.add(dashboardValues);
        dashboardContent.add(recLabel);
        dashboardContent.add(Box.createVerticalStrut(10));

        recommendationValue.setEditable(false);
        recommendationValue.setLineWrap(true);
        recommendationValue.setWrapStyleWord(true);
        recommendationValue.setOpaque(false);
        recommendationValue.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        recommendationValue.setForeground(dashboardTextColor);
        recommendationValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        recommendationValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        recommendationValue.setText("Bitte zuerst eine Messung durchführen.");
        recommendationValue.setRows(5);
        recommendationValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        dashboardContent.add(recommendationValue);
        dashboardContent.add(Box.createVerticalStrut(12));

        JLabel thoughtLabel = new JLabel("Gedanke");
        thoughtLabel.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        thoughtLabel.setForeground(dashboardLabelColor);
        thoughtLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardContent.add(thoughtLabel);
        dashboardContent.add(Box.createVerticalStrut(8));

        JPanel thoughtCard = new RoundedCardPanel(
                18,
                new Color(250, 251, 253),
                new Color(230, 234, 240)
        );
        thoughtCard.setLayout(new BorderLayout());
        thoughtCard.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        // allow the thought to grow vertically if it wraps to multiple lines
        thoughtCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        thoughtCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        thoughtValue.setText("-");
        thoughtValue.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        thoughtValue.setForeground(new Color(93, 104, 120));
        thoughtValue.setHorizontalAlignment(SwingConstants.CENTER);
        thoughtValue.setVerticalAlignment(SwingConstants.CENTER);
        thoughtCard.add(thoughtValue, BorderLayout.CENTER);

        dashboardContent.add(thoughtCard);

        return dashboardCard;
    }

    public static JPanel createPreviewCard(JComponent previewChart, JButton showHistoryButton, int previewCardHeight) {
        JPanel previewCard = new RoundedCardPanel(
                25,
                Color.WHITE,
                new Color(230, 234, 240)
        );
        previewCard.setLayout(new BorderLayout(0, 10));
        previewCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        previewCard.setPreferredSize(new Dimension(430, previewCardHeight));
        previewCard.setMinimumSize(new Dimension(430, previewCardHeight));
        previewCard.setMaximumSize(new Dimension(430, previewCardHeight));
        previewCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel previewTitle = new JLabel("Verlaufsvorschau");
        previewTitle.setFont(new Font("Noto Sans", Font.BOLD, 16));
        previewTitle.setForeground(new Color(55, 65, 81));
        previewCard.add(previewTitle, BorderLayout.NORTH);

        JPanel previewCenter = new JPanel(new BorderLayout(0, 10));
        previewCenter.setOpaque(false);
        previewCenter.add(previewChart, BorderLayout.CENTER);

        JPanel previewButtonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        previewButtonRow.setOpaque(false);
        previewButtonRow.add(showHistoryButton);
        previewCenter.add(previewButtonRow, BorderLayout.SOUTH);

        previewCard.add(previewCenter, BorderLayout.CENTER);

        return previewCard;
    }

}
