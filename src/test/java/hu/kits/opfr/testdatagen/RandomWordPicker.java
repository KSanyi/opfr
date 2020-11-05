package hu.kits.opfr.testdatagen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomWordPicker {

    private final WordDistribution wordDistribution;

    public RandomWordPicker(Path path) {

        List<String> lines;

        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Integer> entries = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split("#");
            String word = parts[0];
            try {
                Integer frequency = Integer.parseInt(parts[1]);
                entries.put(word, frequency);
            } catch (Exception e) {
                entries.put(word, 1);
            }
        }

        wordDistribution = new WordDistribution(entries);
    }

    public String pickRandomWord() {
        return wordDistribution.generate();
    }

}
