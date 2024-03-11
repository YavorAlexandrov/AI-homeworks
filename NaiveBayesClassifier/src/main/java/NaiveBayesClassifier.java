import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NaiveBayesClassifier {
    private Map<String, Double> classProbabilities;
    private Map<String, List<Map<String, Double>>> attributeProbabilitiesModel;
    private Set<String> uniqueClasses;

    public NaiveBayesClassifier() {
        classProbabilities = new HashMap<>();
        attributeProbabilitiesModel = new HashMap<>();
    }

    public void fitModel(List<List<String>> trainingData) {
        classProbabilities = new HashMap<>();
        attributeProbabilitiesModel = new HashMap<>();
        uniqueClasses = findUniqueClass(trainingData);
        Set<String> uniqueAttributeValues = findUniqueAttributeValues(trainingData);

        for (String className : uniqueClasses) {
            classProbabilities.put(className, (double) countClassOccurrences(trainingData, className) / trainingData.size());
            attributeProbabilitiesModel.put(className, new ArrayList<>());

            for (int attributeIndex = 1; attributeIndex < trainingData.get(0).size(); attributeIndex++) {
                Map<String, Double> attributeProbabilities = new HashMap<>();

                for (String attributeValue : uniqueAttributeValues) {
                    double numerator = countAttributeOccurrencesForClass(trainingData, className, attributeIndex, attributeValue);
                    double denominator = countAttributeOccurrences(trainingData, attributeIndex, attributeValue);

                    attributeProbabilities.put(attributeValue, numerator / denominator);
                }

                attributeProbabilitiesModel.get(className).add(attributeProbabilities);
            }
        }
    }

    public double evaluateAccuracy(List<List<String>> testData) {
        int total = testData.size();
        int correct = 0;

        for (List<String> row : testData) {
            String predictedClass = predictClass(row.subList(1, row.size()));
            if (predictedClass.equals(row.get(0))) {
                correct++;
            }
        }

        double accuracy = (double) correct / total;
        System.out.println("Accuracy: " + accuracy);
        return accuracy;
    }

    private String predictClass(List<String> testInstance) {
        Map<String, Double> probabilities = new HashMap<>();

        for (String className : uniqueClasses) {
            double currentClassProbability = classProbabilities.get(className);

            for (int attributeIndex = 0; attributeIndex < testInstance.size(); attributeIndex++) {
                String attributeValue = testInstance.get(attributeIndex);

                if (attributeValue.equals("?")) {
                    continue;
                }

                currentClassProbability *= attributeProbabilitiesModel.get(className).get(attributeIndex).get(attributeValue);
            }

            probabilities.put(className, currentClassProbability);
        }

        return Collections.max(probabilities.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private Set<String> findUniqueClass(List<List<String>> data) {
        Set<String> classes = new HashSet<>();

        for (List<String> row : data) {
            String className = row.get(0);
            classes.add(className);
        }

        return classes;
    }

    private Set<String> findUniqueAttributeValues(List<List<String>> dataset) {
        Set<String> attributeValues = new HashSet<>();
        for (List<String> row : dataset) {
            for (int i = 1; i < row.size(); i++) {
                attributeValues.add(row.get(i));
            }
        }
        return attributeValues;
    }

    private int countClassOccurrences(List<List<String>> dataset, String className) {
        int count = 0;
        for (List<String> row : dataset) {
            if (row.get(0).equals(className)) {
                count++;
            }
        }
        return count;
    }

    private int countAttributeOccurrences(List<List<String>> dataset, int attributeIndex, String attributeValue) {
        int count = 0;
        for (List<String> row : dataset) {
            if (row.get(attributeIndex).equals(attributeValue)) {
                count++;
            }
        }
        return count;
    }

    private int countAttributeOccurrencesForClass(List<List<String>> dataset, String className, int attributeIndex, String attributeValue) {
        int count = 0;
        for (List<String> row : dataset) {
            if (row.get(0).equals(className) && row.get(attributeIndex).equals(attributeValue)) {
                count++;
            }
        }
        return count;
    }
}
