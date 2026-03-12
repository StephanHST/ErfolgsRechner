
package Java.ErfolgsRechner.ui;

import javax.swing.*;
import java.awt.*;

public final class UIFactory {

    private UIFactory() {
    }

    public static JPanel createCardPanel() {
        JPanel card = new RoundedCardPanel(
                25,
                Color.WHITE,
                new Color(230, 234, 240)
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 28));
        return card;
    }

    public static JLabel createDashboardCaption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Noto Sans", Font.PLAIN, 13));
        label.setForeground(new Color(120, 128, 140));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JSlider createSlider() {
        JSlider slider = new JSlider(0, 100, 50);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setFocusable(false);
        slider.setOpaque(false);
        slider.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        slider.setUI(new RoundedSliderUI(slider));
        return slider;
    }

    public static JLabel createValueLabel(JSlider slider) {
        JLabel valueLabel = new JLabel(slider.getValue() + " %");
        valueLabel.setPreferredSize(new Dimension(72, 40));
        valueLabel.setMinimumSize(new Dimension(72, 40));
        valueLabel.setMaximumSize(new Dimension(72, 40));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Noto Sans", Font.BOLD, 13));
        valueLabel.setForeground(new Color(71, 85, 105));
        valueLabel.setOpaque(true);
        valueLabel.setBackground(new Color(250, 251, 253));
        valueLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 238), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        slider.addChangeListener(e -> valueLabel.setText(slider.getValue() + " %"));
        return valueLabel;
    }

    public static JPanel createSliderRow(String labelText, JSlider slider, JLabel valueLabel) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Noto Sans", Font.BOLD, 13));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel sliderLine = new JPanel(new BorderLayout(12, 0));
        sliderLine.setOpaque(false);
        sliderLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        sliderLine.add(slider, BorderLayout.CENTER);
        sliderLine.add(valueLabel, BorderLayout.EAST);

        row.add(label);
        row.add(Box.createVerticalStrut(8));
        row.add(sliderLine);

        return row;
    }
}
