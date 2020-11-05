package hu.kits.opfr.testdatagen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

class WordDistribution {

    private final LinkedHashMap<Integer, String> wordDistributionFunction;

    private final int sum;

    private Random random = new Random();

    public WordDistribution(Map<String, Integer> wordFrequencies) {
        wordDistributionFunction = new LinkedHashMap<>();
        int sum = 0;
        for (Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            sum = sum + entry.getValue();
            wordDistributionFunction.put(sum, entry.getKey());
        }
        this.sum = sum;
    }

    public String generate() {
        double r = random.nextInt(sum);
        for (Entry<Integer, String> entry : wordDistributionFunction.entrySet()) {
            if (r <= entry.getKey()) {
                return entry.getValue();
            }
        }
        throw new AssertionError();
    }

}
