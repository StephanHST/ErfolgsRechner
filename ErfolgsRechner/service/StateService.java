
package Java.ErfolgsRechner.service;

import Java.ErfolgsRechner.model.LimitFactor;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

public class StateService {

    public Path getStateFilePath() {
        return Paths.get(System.getProperty("user.home"), ".erfolgsrechner-state.properties");
    }

    public void saveUiState(JSlider wSlider, JSlider cSlider, JSlider tSlider, List<LimitFactor> limits, JTextField noteField) {
        Properties props = new Properties();

        props.setProperty("w", String.valueOf(wSlider.getValue()));
        props.setProperty("c", String.valueOf(cSlider.getValue()));
        props.setProperty("t", String.valueOf(tSlider.getValue()));
        props.setProperty("note", noteField.getText() == null ? "" : noteField.getText());

        for (LimitFactor l : limits) {
            props.setProperty("limit." + l.getName(), String.valueOf(l.getSlider().getValue()));
        }

        Path stateFile = getStateFilePath();

        try (OutputStream out = Files.newOutputStream(
                stateFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            props.store(out, "ErfolgsRechner UI State");

        } catch (IOException ignored) {
        }
    }

    public void loadUiState(JSlider wSlider, JSlider cSlider, JSlider tSlider, List<LimitFactor> limits, JTextField noteField) {

        Path stateFile = getStateFilePath();

        if (!Files.exists(stateFile)) {
            return;
        }

        Properties props = new Properties();

        try (InputStream in = Files.newInputStream(stateFile, StandardOpenOption.READ)) {

            props.load(in);

            wSlider.setValue(parseSliderValue(props.getProperty("w"), wSlider.getValue()));
            cSlider.setValue(parseSliderValue(props.getProperty("c"), cSlider.getValue()));
            tSlider.setValue(parseSliderValue(props.getProperty("t"), tSlider.getValue()));

            noteField.setText(props.getProperty("note", ""));

            for (LimitFactor l : limits) {
                l.getSlider().setValue(
                        parseSliderValue(
                                props.getProperty("limit." + l.getName()),
                                l.getSlider().getValue()
                        )
                );
            }

        } catch (IOException ignored) {
        }
    }

    public boolean deleteUiState() {

        Path stateFile = getStateFilePath();

        try {
            return Files.deleteIfExists(stateFile);
        } catch (IOException ex) {
            return false;
        }
    }

    private int parseSliderValue(String value, int fallback) {

        if (value == null) {
            return fallback;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return Math.max(0, Math.min(100, parsed));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
