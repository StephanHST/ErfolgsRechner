
package Java.ErfolgsRechner.ui;

import Java.ErfolgsRechner.model.LimitFactor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InputPanel extends JPanel {

    public InputPanel(
            List<LimitFactor> limits,
            JSlider wSlider,
            JLabel wValueLabel,
            JSlider cSlider,
            JLabel cValueLabel,
            JSlider tSlider,
            JLabel tValueLabel,
            JTextField noteField,
            JButton calculateButton
    ) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JPanel card = new RoundedCardPanel(
                25,
                Color.WHITE,
                new Color(230, 234, 240)
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 28));
        card.setPreferredSize(new Dimension(560, 940));
        card.setMinimumSize(new Dimension(560, 940));
        card.setMaximumSize(new Dimension(560, 940));

        JLabel title = new JLabel("Bewerte alle Faktoren von 0–100");
        title.setFont(new Font("Noto Sans", Font.BOLD, 21));
        title.setForeground(new Color(34, 39, 46));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(18));

        JLabel limitHeader = new JLabel("Limitierende Faktoren");
        limitHeader.setFont(new Font("Noto Sans", Font.BOLD, 13));
        limitHeader.setForeground(new Color(93, 104, 120));
        limitHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(limitHeader);
        card.add(Box.createVerticalStrut(10));

        for (LimitFactor factor : limits) {
            JPanel row = createSliderRow(
                    factor.getName(),
                    factor.getSlider(),
                    factor.getValueLabel()
            );
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(row);
        }

        card.add(Box.createVerticalStrut(18));

        JLabel mainHeader = new JLabel("Hauptvariablen");
        mainHeader.setFont(new Font("Noto Sans", Font.BOLD, 13));
        mainHeader.setForeground(new Color(93, 104, 120));
        mainHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(mainHeader);
        card.add(Box.createVerticalStrut(10));

        JPanel wRow = createSliderRow("Zielklarheit (w)", wSlider, wValueLabel);
        wRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(wRow);

        JPanel cRow = createSliderRow("Konzentration (c)", cSlider, cValueLabel);
        cRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cRow);

        JPanel tRow = createSliderRow("Zeit (t)", tSlider, tValueLabel);
        tRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tRow);

        card.add(Box.createVerticalStrut(20));

        JLabel noteLabel = new JLabel("Notiz");
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteLabel.setFont(new Font("Noto Sans", Font.BOLD, 16));
        noteLabel.setForeground(new Color(55, 65, 81));
        card.add(noteLabel);
        card.add(Box.createVerticalStrut(5));

        noteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        noteField.setPreferredSize(new Dimension(480, 54));
        noteField.setMinimumSize(new Dimension(240, 54));
        noteField.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteField.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.AbstractBorder() {
                    private final int r = 15;

                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(228, 232, 238));
                        g2.drawRoundRect(x, y, w - 1, h - 1, r, r);
                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(1, 1, 1, 1);
                    }
                },
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        noteField.setBackground(new Color(250, 251, 253));
        noteField.setToolTipText("Optionaler Vermerk...");
        card.add(noteField);

        card.add(Box.createVerticalStrut(20));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.add(calculateButton);
        card.add(buttonRow);

        add(card);
    }

    private JPanel createSliderRow(String labelText, JSlider slider, JLabel valueLabel) {
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
