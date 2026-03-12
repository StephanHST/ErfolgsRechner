
package Java.ErfolgsRechner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TopicService {

    public Path getTopicFilePath() {
        return Paths.get(System.getProperty("user.home"), ".erfolgsrechner-topic.txt");
    }

    public String loadCurrentTopic() {
        Path topicFile = getTopicFilePath();

        if (!Files.exists(topicFile)) {
            return "";
        }

        try {
            return Files.readString(topicFile).trim();
        } catch (IOException ignored) {
            return "";
        }
    }

    public void saveCurrentTopic(String topic) {
        Path topicFile = getTopicFilePath();

        try {
            Files.writeString(
                    topicFile,
                    topic == null ? "" : topic.trim(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException ignored) {
        }
    }
}
