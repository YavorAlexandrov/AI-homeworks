import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SingleLayerNeuralNetwork {
    private final List<List<Double>> weightsHiddenInput;
    private final List<List<Double>> weightsHiddenOutput;
    private final List<Double> biasHidden;
    private final List<Double> biasOutput;

    public SingleLayerNeuralNetwork(Integer inputSize, Integer hiddenSize, Integer outputSize) {
        weightsHiddenInput = new ArrayList<>();
        weightsHiddenOutput = new ArrayList<>();
        biasHidden = new ArrayList<>();
        biasOutput = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < inputSize; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < hiddenSize; j++) {
                row.add(rand.nextDouble());
            }
            weightsHiddenInput.add(row);
        }

        for (int i = 0; i < hiddenSize; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < outputSize; j++) {
                row.add(rand.nextDouble());
            }
            weightsHiddenOutput.add(row);
        }

        for (int i = 0; i < hiddenSize; i++) {
            biasHidden.add(rand.nextDouble());
        }

        for (int i = 0; i < outputSize; i++) {
            biasOutput.add(rand.nextDouble());
        }
    }

    public void train(List<List<Double>> inputs, List<List<Double>> labels, Integer epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {

            // Forward propagation
            List<List<Double>> hiddenLayerInput = new ArrayList<>();
            List<List<Double>> hiddenLayerOutput = new ArrayList<>();
            List<List<Double>> outputLayerInput = new ArrayList<>();
            List<List<Double>> output = new ArrayList<>();

            for (int i = 0; i < inputs.size(); i++) {
                hiddenLayerInput.add(new ArrayList<>());
                hiddenLayerOutput.add(new ArrayList<>());
                for (int j = 0; j < weightsHiddenInput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < inputs.get(0).size(); k++) {
                        sum += inputs.get(i).get(k) * weightsHiddenInput.get(k).get(j);
                    }
                    hiddenLayerInput.get(i).add(sum + biasHidden.get(j));
                    hiddenLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(hiddenLayerInput.get(i).get(j)));
                }
            }

            for (int i = 0; i < hiddenLayerOutput.size(); i++) {
                outputLayerInput.add(new ArrayList<>());
                output.add(new ArrayList<>());
                for (int j = 0; j < weightsHiddenOutput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < hiddenLayerOutput.get(0).size(); k++) {
                        sum += hiddenLayerOutput.get(i).get(k) * weightsHiddenOutput.get(k).get(j);
                    }
                    outputLayerInput.get(i).add(sum + biasOutput.get(j));
                    output.get(i).add(NeuralNetworkActivations.sigmoid(outputLayerInput.get(i).get(j)));
                }
            }

            // Backpropagation
            List<List<Double>> outputLayerError = new ArrayList<>();
            List<List<Double>> outputLayerDelta = new ArrayList<>();
            List<List<Double>> hiddenLayerError = new ArrayList<>();
            List<List<Double>> hiddenLayerDelta = new ArrayList<>();

            for (int i = 0; i < labels.size(); i++) {
                outputLayerError.add(new ArrayList<>());
                outputLayerDelta.add(new ArrayList<>());
                for (int j = 0; j < labels.get(0).size(); j++) {
                    outputLayerError.get(i).add(labels.get(i).get(j) - output.get(i).get(j));
                    outputLayerDelta.get(i).add(outputLayerError.get(i).get(j) * NeuralNetworkActivations.sigmoidDerivative(output.get(i).get(j)));
                }
            }

            for (int i = 0; i < hiddenLayerOutput.size(); i++) {
                hiddenLayerError.add(new ArrayList<>());
                hiddenLayerDelta.add(new ArrayList<>());
                for (int j = 0; j < hiddenLayerOutput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < labels.get(0).size(); k++) {
                        sum += outputLayerDelta.get(i).get(k) * weightsHiddenOutput.get(j).get(k);
                    }
                    hiddenLayerError.get(i).add(sum);
                    hiddenLayerDelta.get(i).add(hiddenLayerError.get(i).get(j) * NeuralNetworkActivations.sigmoidDerivative(hiddenLayerOutput.get(i).get(j)));
                }
            }

            // Update weights and biases
            for (int i = 0; i < weightsHiddenOutput.size(); i++) {
                for (int j = 0; j < weightsHiddenOutput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < hiddenLayerOutput.size(); k++) {
                        sum += hiddenLayerOutput.get(k).get(i) * outputLayerDelta.get(k).get(j);
                    }
                    weightsHiddenOutput.get(i).set(j, weightsHiddenOutput.get(i).get(j) + sum);
                }
            }

            for (int i = 0; i < weightsHiddenInput.size(); i++) {
                for (int j = 0; j < weightsHiddenInput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < inputs.size(); k++) {
                        sum += inputs.get(k).get(i) * hiddenLayerDelta.get(k).get(j);
                    }
                    weightsHiddenInput.get(i).set(j, weightsHiddenInput.get(i).get(j) + sum);
                }
            }

            for (int i = 0; i < biasOutput.size(); i++) {
                double sum = 0.0;
                for (List<Double> doubles : outputLayerDelta) {
                    sum += doubles.get(i);
                }
                biasOutput.set(i, biasOutput.get(i) + sum);
            }

            for (int i = 0; i < biasHidden.size(); i++) {
                double sum = 0.0;
                for (List<Double> doubles : hiddenLayerDelta) {
                    sum += doubles.get(i);
                }
                biasHidden.set(i, biasHidden.get(i) + sum);
            }
        }
    }

    public List<List<Double>> predict(List<List<Double>> inputs) {
        List<List<Double>> hiddenLayerInput = new ArrayList<>();
        List<List<Double>> hiddenLayerOutput = new ArrayList<>();
        List<List<Double>> outputLayerInput = new ArrayList<>();
        List<List<Double>> outputLayerOutput = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            hiddenLayerInput.add(new ArrayList<>());
            hiddenLayerOutput.add(new ArrayList<>());
            for (int j = 0; j < weightsHiddenInput.get(0).size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < inputs.get(0).size(); k++) {
                    sum += inputs.get(i).get(k) * weightsHiddenInput.get(k).get(j);
                }
                hiddenLayerInput.get(i).add(sum + biasHidden.get(j));
                hiddenLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(hiddenLayerInput.get(i).get(j)));
            }
        }

        for (int i = 0; i < hiddenLayerOutput.size(); i++) {
            outputLayerInput.add(new ArrayList<>());
            outputLayerOutput.add(new ArrayList<>());
            for (int j = 0; j < weightsHiddenOutput.get(0).size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < hiddenLayerOutput.get(0).size(); k++) {
                    sum += hiddenLayerOutput.get(i).get(k) * weightsHiddenOutput.get(k).get(j);
                }
                outputLayerInput.get(i).add(sum + biasOutput.get(j));
                outputLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(outputLayerInput.get(i).get(j)));
            }
        }

        return outputLayerOutput;
    }
}
