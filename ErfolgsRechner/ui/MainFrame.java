
package Java.ErfolgsRechner.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final JPanel rootPanel;
    private final JScrollPane scrollPane;

    public MainFrame(String title, int width, int height) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(244, 246, 249));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(rootPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(244, 246, 249));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
