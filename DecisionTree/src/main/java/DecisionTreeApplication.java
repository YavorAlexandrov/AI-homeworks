import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DecisionTreeApplication {
    public static void main(String[] args) {
        URL fileURL = DecisionTreeApplication.class.getClassLoader().getResource("breast-cancer.data");
        if (fileURL != null) {
            String filePath = new File(fileURL.getFile()).getAbsolutePath();
            List<List<String>> dataset = loadDataset(filePath);
            int k = 10;
            DecisionTreeClassifier decisionTreeClassifier = new DecisionTreeClassifier(k);
            performKFoldCrossValidation(decisionTreeClassifier, dataset, k);
        }
    }

    private static List<List<String>> loadDataset(String filePath) {
        List<List<String>> dataset = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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

    private static void performKFoldCrossValidation(DecisionTreeClassifier decisionTreeClassifier, List<List<String>> dataset, int k) {
        List<List<List<String>>> splitDataset = splitDataset(k, dataset);
        double[] accuracies = new double[k];

        for (int i = 0; i < k; i++) {
            List<List<String>> testSplit = splitDataset.get(i);
            List<List<String>> trainData = generateTrainData(splitDataset, i);

            decisionTreeClassifier.buildDecisionTree(trainData);
            double currentSplitAccuracy = decisionTreeClassifier.computeAccuracy(testSplit);
            accuracies[i] = currentSplitAccuracy;
        }

        double averageAccuracy = computeAverage(accuracies);
        System.out.println("Average accuracy: " + averageAccuracy);
    }

    private static List<List<String>> generateTrainData(List<List<List<String>>> splitDataset, int excludeIndex) {
        List<List<String>> trainData = new ArrayList<>();
        for (int i = 0; i < splitDataset.size(); i++) {
            if (i != excludeIndex) {
                trainData.addAll(splitDataset.get(i));
            }
        }
        return trainData;
    }

    private static double computeAverage(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
}
