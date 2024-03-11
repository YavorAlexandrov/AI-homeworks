import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DecisionTreeClassifier {
    private final int minExamplesInDataset;
    private TreeNode root;

    public DecisionTreeClassifier(int minExamplesInDataset) {
        this.minExamplesInDataset = minExamplesInDataset;
    }

    public void buildDecisionTree(List<List<String>> dataset) {
        this.root = constructTree(dataset, getFeatures(dataset), null);
    }

    public double computeAccuracy(List<List<String>> testData) {
        int total = testData.size();
        int correct = 0;
        for (List<String> row : testData) {
            String predictedClass = predict(row.subList(1, row.size()));
            if (predictedClass.equals(row.get(0))) {
                correct++;
            }
        }
        return (double) correct / total;
    }

    private List<Integer> getFeatures(List<List<String>> dataset) {
        List<Integer> features = new ArrayList<>();
        for (int i = 1; i < dataset.get(0).size(); i++) {
            features.add(i);
        }
        return features;
    }

    private String predict(List<String> testExample) {
        TreeNode currentTreeNode = root;
        while (!currentTreeNode.getIsLeafNode()) {
            int currentFeature = Integer.parseInt(currentTreeNode.getFeature());
            String currentFeatureValue = testExample.get(currentFeature);
            if (!currentTreeNode.getChildren().containsKey(currentFeatureValue)) {
                break;
            }
            currentTreeNode = currentTreeNode.getChildren().get(currentFeatureValue);
        }
        return currentTreeNode.getMostFrequentClass();
    }

    private TreeNode constructTree(List<List<String>> dataset, List<Integer> features, TreeNode parent) {
        if (features.isEmpty()) {
            return new TreeNode(null, true, findMostFrequentClass(dataset));
        }
        if (dataset.stream().map(row -> row.get(0)).distinct().count() == 1) {
            return new TreeNode(null, true, dataset.get(0).get(0));
        }
        if (parent != null && dataset.size() < minExamplesInDataset) {
            return new TreeNode(null, true, parent.getMostFrequentClass());
        }

        TreeNode currentNode = new TreeNode(null, false, findMostFrequentClass(dataset));
        double datasetEntropy = computeEntropy(dataset);
        Map<Integer, Double> featuresInfoGain = new HashMap<>();
        for (int feature : features) {
            featuresInfoGain.put(feature, computeInfoGain(dataset, feature, datasetEntropy));
        }

        int bestFeature = featuresInfoGain.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();

        currentNode.setFeature(Integer.toString(bestFeature));
        features.remove(Integer.valueOf(bestFeature));
        for (String featureValue : getFeatureValues(dataset, bestFeature)) {
            List<List<String>> subset = new ArrayList<>();
            for (List<String> row : dataset) {
                if (row.get(bestFeature).equals(featureValue)) {
                    subset.add(row);
                }
            }
            currentNode.addChildNode(featureValue, constructTree(subset, features, currentNode));
        }

        return currentNode;
    }

    private String findMostFrequentClass(List<List<String>> dataset) {
        Set<String> classes = new HashSet<>();
        for (List<String> row : dataset) {
            classes.add(row.get(0));
        }

        Map<String, Double> classProbabilities = new HashMap<>();
        for (String className : classes) {
            long count = dataset.stream().filter(row -> row.get(0).equals(className)).count();
            classProbabilities.put(className, (double) count / dataset.size());
        }

        return Collections.max(classProbabilities.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private double computeEntropy(List<List<String>> dataset) {
        Set<String> classes = dataset.stream().map(row -> row.get(0)).collect(Collectors.toSet());

        Map<String, Double> classProbabilities = new HashMap<>();
        for (String className : classes) {
            long count = dataset.stream().filter(row -> row.get(0).equals(className)).count();
            classProbabilities.put(className, (double) count / dataset.size());
        }

        return -classProbabilities.values().stream()
                .mapToDouble(classProbability -> classProbability * Math.log(classProbability) / Math.log(2))
                .sum();
    }

    private Double computeInfoGain(List<List<String>> dataset, int feature, double datasetEntropy) {
        Set<String> featureValues = new HashSet<>();
        for (List<String> row : dataset) {
            featureValues.add(row.get(feature));
        }

        Map<String, Double> featureValuesProbabilities = new HashMap<>();
        for (String featureValue : featureValues) {
            long count = dataset.stream().filter(row -> row.get(feature).equals(featureValue)).count();
            featureValuesProbabilities.put(featureValue, (double) count / dataset.size());
        }

        double infoGain = 0.0;
        for (String featureValue : featureValuesProbabilities.keySet()) {
            List<List<String>> subset = dataset.stream()
                    .filter(row -> row.get(feature).equals(featureValue))
                    .toList();
            infoGain += featureValuesProbabilities.get(featureValue) * computeEntropy(subset);
        }

        return datasetEntropy - infoGain;
    }

    private Set<String> getFeatureValues(List<List<String>> dataset, int bestFeature) {
        Set<String> featureValues = new HashSet<>();
        for (List<String> row : dataset) {
            featureValues.add(row.get(bestFeature));
        }
        return featureValues;
    }
}
