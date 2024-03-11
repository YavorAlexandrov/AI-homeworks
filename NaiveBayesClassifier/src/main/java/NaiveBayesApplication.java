import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NaiveBayesApplication {
    public static void main(String[] args) {
        URL fileURL = NaiveBayesApplication.class.getClassLoader().getResource("house-votes-84.data");
        if (fileURL != null) {
            String filePath = new File(fileURL.getFile()).getAbsolutePath();
            List<List<String>> dataset = loadDatasetFromFile(filePath);
            NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
            executeKFoldCrossValidation(naiveBayesClassifier, dataset, 10);
        }
    }

    private static List<List<String>> loadDatasetFromFile(String filepath) {
        List<List<String>> dataset = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] values = line.split(",");
                List<String> row = new ArrayList<>();
                Collections.addAll(row, values);
                dataset.add(row);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return dataset;
    }

    private static void executeKFoldCrossValidation(NaiveBayesClassifier naiveBayesClassifier, List<List<String>> dataset, int k) {
        List<List<List<String>>> splitDataset = splitDataset(k, dataset);
        List<Double> accuracies = new ArrayList<>();

        for (List<List<String>> split : splitDataset) {
            List<List<String>> trainingData = new ArrayList<>(dataset);
            for (List<String> row : dataset) {
                if (!split.contains(row)) {
                    trainingData.add(row);
                }
            }
            naiveBayesClassifier.fitModel(trainingData);
            double currentSplitAccuracy = naiveBayesClassifier.evaluateAccuracy(split);
            accuracies.add(currentSplitAccuracy);
        }

        double averageAccuracy = accuracies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.println("Average accuracy: " + averageAccuracy);
    }

    private static List<List<List<String>>> splitDataset(int k, List<List<String>> dataset) {
        List<List<List<String>>> splitDataset = new ArrayList<>();
        int splitSize = dataset.size() / k;

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            indices.add(i);
        }

        Random random = new Random();

        for (int i = 0; i < k - 1; i++) {
            Collections.shuffle(indices, random);
            List<Integer> currentSplitIndices = indices.subList(0, splitSize);
            List<List<String>> currentSplitData = new ArrayList<>();
            for (int index : currentSplitIndices) {
                currentSplitData.add(new ArrayList<>(dataset.get(index)));
            }
            splitDataset.add(currentSplitData);
            indices.removeAll(currentSplitIndices);
        }

        splitDataset.add(new ArrayList<>());
        for (int index : indices) {
            splitDataset.get(splitDataset.size() - 1).add(new ArrayList<>(dataset.get(index)));
        }

        return splitDataset;
    }
}