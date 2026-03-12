
package Java.ErfolgsRechner.ui;

import javax.swing.*;
import java.awt.*;

public class DialogFactory {

    public void installPopupTheme() {
        Font popupFont = new Font("Noto Sans", Font.PLAIN, 14);
        Font popupButtonFont = new Font("Noto Sans", Font.PLAIN, 14);

        Color popupBg = new Color(244, 246, 249);
        Color popupButtonBg = Color.WHITE;
        Color popupButtonFg = new Color(55, 65, 81);
        Color popupButtonBorder = new Color(210, 214, 220);

        UIManager.put("OptionPane.messageFont", popupFont);
        UIManager.put("OptionPane.buttonFont", popupButtonFont);
        UIManager.put("OptionPane.background", popupBg);
        UIManager.put("Panel.background", popupBg);
        UIManager.put("OptionPane.messageForeground", new Color(34, 39, 46));

        UIManager.put("Button.background", popupButtonBg);
        UIManager.put("Button.foreground", popupButtonFg);
        UIManager.put("Button.select", new Color(245, 247, 250));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(popupButtonBorder, 1, true),
                BorderFactory.createEmptyBorder(10, 22, 10, 22)
        ));
        UIManager.put("Button.margin", new Insets(10, 22, 10, 22));
        UIManager.put("Button.arc", 15);
    }

    public JButton createPopupDialogButton(String text, boolean primary) {
        JButton button = new RoundedButton(
                text,
                10,
                primary ? new Color(5, 177, 159) : Color.WHITE,
                primary ? Color.WHITE : new Color(55, 65, 81),
                primary ? new Color(5, 177, 159) : new Color(210, 214, 220)
        );

        button.setFocusPainted(false);
        button.setFont(new Font("Noto Sans", primary ? Font.BOLD : Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 46));
        button.setMaximumSize(new Dimension(150, 46));

        if (primary) {
            installButtonHoverEffect(
                    button,
                    new Color(5, 177, 159),
                    Color.WHITE,
                    new Color(4, 160, 143),
                    Color.WHITE
            );
        } else {
            installButtonHoverEffect(
                    button,
                    Color.WHITE,
                    new Color(55, 65, 81),
                    new Color(245, 247, 250),
                    new Color(34, 39, 46)
            );
        }

        return button;
    }

    public int showStyledConfirmDialog(Component parent, String message, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(244, 246, 249));

        JLabel text = new JLabel("<html><div style='font-family:Noto Sans; width:360px;'>" + message + "</div></html>");
        text.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        text.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        text.setForeground(new Color(34, 39, 46));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttonRow.setOpaque(false);

        JButton noButton = createPopupDialogButton("Nein", false);
        JButton yesButton = createPopupDialogButton("Ja", true);

        final int[] result = {JOptionPane.CLOSED_OPTION};

        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        buttonRow.add(noButton);
        buttonRow.add(yesButton);

        dialog.add(text, BorderLayout.CENTER);
        dialog.add(buttonRow, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return result[0];
    }

    public void showStyledMessageDialog(Component parent, Object message, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(244, 246, 249));

        JComponent messageComponent;
        if (message instanceof JComponent) {
            messageComponent = (JComponent) message;
        } else {
            JLabel text = new JLabel("<html><div style='font-family:Noto Sans; width:360px;'>"
                    + String.valueOf(message)
                    + "</div></html>");
            text.setFont(new Font("Noto Sans", Font.PLAIN, 14));
            text.setForeground(new Color(34, 39, 46));
            messageComponent = text;
        }

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        center.add(messageComponent, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        buttonRow.setOpaque(false);

        JButton okButton = createPopupDialogButton("OK", false);
        okButton.addActionListener(e -> dialog.dispose());
        buttonRow.add(okButton);

        dialog.add(center, BorderLayout.CENTER);
        dialog.add(buttonRow, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public void showStyledResultDialog(Component parent, Object message, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(244, 246, 249));

        JComponent messageComponent;
        if (message instanceof JComponent) {
            messageComponent = (JComponent) message;
        } else {
            JLabel text = new JLabel(String.valueOf(message));
            text.setFont(new Font("Noto Sans", Font.PLAIN, 15));
            text.setForeground(new Color(34, 39, 46));
            text.setVerticalAlignment(SwingConstants.TOP);
            messageComponent = text;
        }

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(true);
        outer.setBackground(new Color(244, 246, 249));
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

        JPanel card = new RoundedCardPanel(
                20,
                Color.WHITE,
                new Color(230, 234, 240)
        );
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(26, 22, 22, 22));

        JScrollPane scrollPane = new JScrollPane(
                messageComponent,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);
        outer.add(card, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JButton okButton = createPopupDialogButton("OK", true);
        okButton.addActionListener(e -> dialog.dispose());
        buttonRow.add(okButton);

        outer.add(buttonRow, BorderLayout.SOUTH);

        dialog.setContentPane(outer);
        dialog.setSize(1120, 920);
        dialog.setMinimumSize(new Dimension(980, 760));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void installButtonHoverEffect(
            JButton button,
            Color normalBg,
            Color normalFg,
            Color hoverBg,
            Color hoverFg
    ) {
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button instanceof RoundedButton) {
                    ((RoundedButton) button).setHoverStyle(hoverBg, hoverFg);
                } else {
                    button.setBackground(hoverBg);
                    button.setForeground(hoverFg);
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button instanceof RoundedButton) {
                    ((RoundedButton) button).setHoverStyle(normalBg, normalFg);
                } else {
                    button.setBackground(normalBg);
                    button.setForeground(normalFg);
                    button.repaint();
                }
            }
        });
    }
}
