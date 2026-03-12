package Java.ErfolgsRechner.service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetService {

    public File resolveAssetFile(String filename) {
        File file = new File("assets/" + filename);
        if (file.exists()) return file;

        file = new File("../assets/" + filename);
        if (file.exists()) return file;

        file = new File("/mnt/data/" + filename);
        if (file.exists()) return file;

        return null;
    }

    public List<String> loadThoughts() {
        List<String> thoughts = new ArrayList<>();
        try {
            java.net.URL resource = getClass().getResource("/Java/ErfolgsRechner/assets/thoughts.json");
            if (resource != null) {
                String json = Files.readString(java.nio.file.Paths.get(resource.toURI()));
                Matcher matcher = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"").matcher(json);

                while (matcher.find()) {
                    String thought = matcher.group(1)
                            .replace("\\\"", "\"")
                            .replace("\\n", " ")
                            .replace("\\r", " ")
                            .trim();

                    if (!thought.isEmpty()) {
                        thoughts.add(thought);
                    }
                }

                return thoughts;
            }
        } catch (Exception ignored) {
        }

        File file = resolveAssetFile("thoughts.json");

        if (file == null || !file.exists()) {
            return thoughts;
        }

        try {
            String json = Files.readString(file.toPath());
            Matcher matcher = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"").matcher(json);

            while (matcher.find()) {
                String thought = matcher.group(1)
                        .replace("\\\"", "\"")
                        .replace("\\n", " ")
                        .replace("\\r", " ")
                        .trim();

                if (!thought.isEmpty()) {
                    thoughts.add(thought);
                }
            }
        } catch (IOException ignored) {
        }

        return thoughts;
    }

    public String pickRandomThought() {
        List<String> thoughts = loadThoughts();

        if (thoughts.isEmpty()) {
            return "Ein ruhiger Geist sieht mehr Möglichkeiten.";
        }

        return thoughts.get(new Random().nextInt(thoughts.size()));
    }

    public ImageIcon loadArrowIcon(String filename, int width, int height) {
        try {
            java.net.URL resource = getClass().getResource("/Java/ErfolgsRechner/assets/" + filename);
            if (resource != null) {
                ImageIcon raw = new ImageIcon(resource);
                Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {
        }

        File file = resolveAssetFile(filename);
        if (file == null || !file.exists()) {
            return null;
        }

        ImageIcon raw = new ImageIcon(file.getAbsolutePath());
        Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
