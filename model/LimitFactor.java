
package Java.ErfolgsRechner.model;

import javax.swing.JLabel;
import javax.swing.JSlider;

public class LimitFactor {

    private final String name;
    private final JSlider slider;
    private final JLabel valueLabel;

    public LimitFactor(String name, JSlider slider, JLabel valueLabel) {
        this.name = name;
        this.slider = slider;
        this.valueLabel = valueLabel;
    }

    public String getName() {
        return name;
    }

    public JSlider getSlider() {
        return slider;
    }

    public JLabel getValueLabel() {
        return valueLabel;
    }

    public double getValue() {
        return slider.getValue() / 100.0;
    }
}
