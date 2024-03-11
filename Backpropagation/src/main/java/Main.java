import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Training data for boolean functions: AND, OR, XOR
        List<List<Double>> trainingInputs = new ArrayList<>();
        trainingInputs.add(List.of(0.0, 0.0));
        trainingInputs.add(List.of(0.0, 1.0));
        trainingInputs.add(List.of(1.0, 0.0));
        trainingInputs.add(List.of(1.0, 1.0));

        // Output for AND
        List<List<Double>> trainingOutputsForAndOperation = new ArrayList<>();
        trainingOutputsForAndOperation.add(List.of(0.0));
        trainingOutputsForAndOperation.add(List.of(0.0));
        trainingOutputsForAndOperation.add(List.of(0.0));
        trainingOutputsForAndOperation.add(List.of(1.0));

        // Output for OR
        List<List<Double>> trainingOutputsForOrOperation = new ArrayList<>();
        trainingOutputsForOrOperation.add(List.of(0.0));
        trainingOutputsForOrOperation.add(List.of(1.0));
        trainingOutputsForOrOperation.add(List.of(1.0));
        trainingOutputsForOrOperation.add(List.of(1.0));

        // Output for XOR
        List<List<Double>> trainingOutputsForXorOperation = new ArrayList<>();
        trainingOutputsForXorOperation.add(List.of(0.0));
        trainingOutputsForXorOperation.add(List.of(1.0));
        trainingOutputsForXorOperation.add(List.of(1.0));
        trainingOutputsForXorOperation.add(List.of(0.0));

        // Create neural network objects
        SingleLayerNeuralNetwork neuralNetworkAnd = new SingleLayerNeuralNetwork(2, 4, 1);
        SingleLayerNeuralNetwork neuralNetworkOr = new SingleLayerNeuralNetwork(2, 4, 1);
        MultiLayerNeuralNetwork neuralNetworkXor = new MultiLayerNeuralNetwork();

        // Train the neural networks for XOR
        neuralNetworkAnd.train(trainingInputs, trainingOutputsForAndOperation, 10000);
        neuralNetworkOr.train(trainingInputs, trainingOutputsForOrOperation, 10000);
        neuralNetworkXor.train(trainingInputs, trainingOutputsForXorOperation, 10000);

        // Test the neural networks for AND + OR
        System.out.println("AND Predictions:");
        for (List<Double> input : trainingInputs) {
            List<List<Double>> in = new ArrayList<>();
            in.add(input);
            List<List<Double>> output = neuralNetworkAnd.predict(in);
            System.out.println("Input: [" + input.get(0) + ", " + input.get(1) + "], Predicted Output: " + output.get(0).get(0));
        }

        System.out.println("OR Predictions:");
        for (List<Double> training_input : trainingInputs) {
            List<List<Double>> in = new ArrayList<>();
            in.add(training_input);
            List<List<Double>> output = neuralNetworkOr.predict(in);
            System.out.println(
                    "Input: [" + training_input.get(0) + ", " + training_input.get(1) + "], Predicted Output: " + output.get(0).get(0));
        }

        System.out.println("XOR Predictions:");
        for (List<Double> trainingInput : trainingInputs) {
            List<List<Double>> in = new ArrayList<>();
            in.add(trainingInput);
            List<List<Double>> output = neuralNetworkXor.think(in);
            System.out.println(
                    "Input: [" + trainingInput.get(0) + ", " + trainingInput.get(1) + "], Predicted Output: " + output.get(0).get(0));
        }
    }
}
