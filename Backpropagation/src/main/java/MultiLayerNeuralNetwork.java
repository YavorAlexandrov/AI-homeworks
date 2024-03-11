import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiLayerNeuralNetwork {
    private final List<List<Double>> weightsHiddenInput;
    private final List<List<Double>> weightsHiddenOutput;

    public MultiLayerNeuralNetwork() {
        weightsHiddenInput = new ArrayList<>();
        weightsHiddenOutput = new ArrayList<>();

        int inputSize = 2;
        int hiddenSize = 4;
        int outputSize = 1;

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
    }

    public void train(List<List<Double>> trainingInputs, List<List<Double>> trainingOutputs, Integer iterations) {
        for (int iteration = 0; iteration < iterations; iteration++) {
            List<List<Double>> hiddenLayerOutput = new ArrayList<>();
            List<List<Double>> outputLayerOutput = new ArrayList<>();

            for (int i = 0; i < trainingInputs.size(); i++) {
                hiddenLayerOutput.add(new ArrayList<>());
                for (int j = 0; j < weightsHiddenInput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < trainingInputs.get(0).size(); k++) {
                        sum += trainingInputs.get(i).get(k) * weightsHiddenInput.get(k).get(j);
                    }
                    hiddenLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(sum));
                }
            }

            for (int i = 0; i < hiddenLayerOutput.size(); i++) {
                outputLayerOutput.add(new ArrayList<>());
                for (int j = 0; j < weightsHiddenOutput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < hiddenLayerOutput.get(0).size(); k++) {
                        sum += hiddenLayerOutput.get(i).get(k) * weightsHiddenOutput.get(k).get(j);
                    }
                    outputLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(sum));
                }
            }

            List<List<Double>> outputLayerError = new ArrayList<>();
            List<List<Double>> outputLayerDelta = new ArrayList<>();
            List<List<Double>> hiddenLayerError = new ArrayList<>();
            List<List<Double>> hiddenLayerDelta = new ArrayList<>();

            for (int i = 0; i < trainingOutputs.size(); i++) {
                outputLayerError.add(new ArrayList<>());
                outputLayerDelta.add(new ArrayList<>());
                for (int j = 0; j < trainingOutputs.get(0).size(); j++) {
                    outputLayerError.get(i).add(trainingOutputs.get(i).get(j) - outputLayerOutput.get(i).get(j));
                    outputLayerDelta.get(i).add(outputLayerError.get(i).get(j) * NeuralNetworkActivations.sigmoidDerivative(outputLayerOutput.get(i).get(j)));
                }
            }

            for (int i = 0; i < hiddenLayerOutput.size(); i++) {
                hiddenLayerError.add(new ArrayList<>());
                hiddenLayerDelta.add(new ArrayList<>());
                for (int j = 0; j < hiddenLayerOutput.get(0).size(); j++) {
                    double sum = 0.0;
                    for (int k = 0; k < trainingOutputs.get(0).size(); k++) {
                        sum += outputLayerDelta.get(i).get(k) * weightsHiddenOutput.get(j).get(k);
                    }
                    hiddenLayerError.get(i).add(sum);
                    hiddenLayerDelta.get(i).add(hiddenLayerError.get(i).get(j) * NeuralNetworkActivations.sigmoidDerivative(hiddenLayerOutput.get(i).get(j)));
                }
            }


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
                    for (int k = 0; k < trainingInputs.size(); k++) {
                        sum += trainingInputs.get(k).get(i) * hiddenLayerDelta.get(k).get(j);
                    }
                    weightsHiddenInput.get(i).set(j, weightsHiddenInput.get(i).get(j) + sum);
                }
            }
        }
    }

    public List<List<Double>> think(List<List<Double>> inputs) {
        List<List<Double>> hiddenLayerOutput = new ArrayList<>();
        List<List<Double>> outputLayerOutput = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            hiddenLayerOutput.add(new ArrayList<>());
            for (int j = 0; j < weightsHiddenInput.get(0).size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < inputs.get(0).size(); k++) {
                    sum += inputs.get(i).get(k) * weightsHiddenInput.get(k).get(j);
                }
                hiddenLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(sum));
            }
        }

        for (int i = 0; i < hiddenLayerOutput.size(); i++) {
            outputLayerOutput.add(new ArrayList<>());
            for (int j = 0; j < weightsHiddenOutput.get(0).size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < hiddenLayerOutput.get(0).size(); k++) {
                    sum += hiddenLayerOutput.get(i).get(k) * weightsHiddenOutput.get(k).get(j);
                }
                outputLayerOutput.get(i).add(NeuralNetworkActivations.sigmoid(sum));
            }
        }
        return outputLayerOutput;
    }
}
