import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class State implements Comparable<State> {
    private static double[][] distances;
    private final List<Integer> route;
    private final double fitness;

    public State(List<Integer> route) {
        this.route = route;
        this.fitness = calculateFitness();
    }

    private double calculateFitness() {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int city1 = route.get(i);
            int city2 = route.get(i + 1);
            totalDistance += distances[city1][city2];
        }
        return totalDistance;
    }

    public static void setDistances(double[][] distances) {
        State.distances = distances;
    }

    public void swapCities(int firstIndex, int secondIndex) {
        Collections.swap(route, firstIndex, secondIndex);
    }

    public List<Integer> getRoute() {
        return route;
    }

    public int routeSize() {
        return route.size();
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(State other) {
        return Double.compare(this.fitness, other.fitness);
    }
}

public class TSP {
    private static List<State> performSelection(List<State> population) {
        Collections.sort(population);
        int selectionSize = population.size() / 5;
        return population.subList(0, selectionSize);
    }

    private static State[] selectParents(List<State> selection) {
        Random random = new Random();
        int firstParentIndex = random.nextInt(selection.size());
        int secondParentIndex = random.nextInt(selection.size());
        while (firstParentIndex == secondParentIndex) {
            secondParentIndex = random.nextInt(selection.size());
        }
        State firstParent = selection.get(firstParentIndex);
        State secondParent = selection.get(secondParentIndex);
        return new State[] { firstParent, secondParent };
    }

    private static int[] generateStartAndEndGenes(int parentSize) {
        Random rand = new Random();
        int startGene = rand.nextInt(parentSize);
        int endGene = rand.nextInt(parentSize);
        if (startGene > endGene) {
            int temp = startGene;
            startGene = endGene;
            endGene = temp;
        }
        return new int[] { startGene, endGene };
    }

    private static State performOrderOneCrossover(State firstParent, State secondParent) {
        List<Integer> childRoute = new ArrayList<>(Collections.nCopies(firstParent.routeSize(), -1));
        int[] genes = generateStartAndEndGenes(firstParent.routeSize());
        int startGene = genes[0];
        int endGene = genes[1];

        for (int i = startGene; i <= endGene; i++) {
            childRoute.set(i, firstParent.getRoute().get(i));
        }

        List<Integer> remainingCities = new ArrayList<>(firstParent.getRoute());
        remainingCities.removeAll(childRoute);

        List<Integer> secondParentRoute = new ArrayList<>(secondParent.getRoute().subList(endGene + 1, secondParent.routeSize()));
        secondParentRoute.addAll(secondParent.getRoute().subList(0, endGene + 1));

        int position = endGene + 1;
        for (int city : secondParentRoute) {
            if (remainingCities.contains(city)) {
                if (position > childRoute.size() - 1) {
                    position = 0;
                }
                childRoute.set(position, city);
                position++;
            }
        }
        return new State(childRoute);
    }

    private static State applyMutation(State child) {
        Random random = new Random();
        int firstIndex = random.nextInt(child.routeSize());
        int secondIndex = random.nextInt(child.routeSize());
        while (firstIndex == secondIndex) {
            secondIndex = random.nextInt(child.routeSize());
        }
        child.swapCities(firstIndex, secondIndex);
        return child;
    }

    private static List<State> retrieveTopKIndividuals(List<State> population, int k) {
        Collections.sort(population);
        return new ArrayList<>(population.subList(0, k));
    }

    private static void breed(List<State> population, int epochs, int mutationPercentage) {
        Random random = new Random();
        int k = population.size();
        for (int epoch = 0; epoch < epochs; epoch++) {
            List<State> selection = performSelection(population);
            List<State> newIndividuals = new ArrayList<>();

            for (int i = 0; i < population.size(); i++) {

                State[] parents = selectParents(selection);
                State firstParent = parents[0];
                State secondParent = parents[1];

                State firstChild = performOrderOneCrossover(firstParent, secondParent);
                State secondChild = performOrderOneCrossover(secondParent, firstParent);

                if (random.nextInt(100) < mutationPercentage) {
                    firstChild = applyMutation(firstChild);
                }
                if (random.nextInt(100) < mutationPercentage) {
                    secondChild = applyMutation(secondChild);
                }
                newIndividuals.add(firstChild);
                newIndividuals.add(secondChild);
            }
            population.addAll(newIndividuals);
            population = retrieveTopKIndividuals(population, k);
            System.out.println("Epoch " + epoch + ": " + population.get(0).getFitness());
        }
    }

    private static List<List<Integer>> createRandomCityCoordinates(int citiesCount) {
        Random rand = new Random();
        List<List<Integer>> cityCoordinates = new ArrayList<>();
        for (int i = 0; i < citiesCount; i++) {
            cityCoordinates.add(List.of(rand.nextInt(200) - 100, rand.nextInt(200) - 100));
        }
        return cityCoordinates;
    }

    private static double calculateDistance(List<Integer> city1, List<Integer> city2) {
        int x1 = city1.get(0);
        int y1 = city1.get(1);
        int x2 = city2.get(0);
        int y2 = city2.get(1);
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private static double[][] createDistanceMatrix(List<List<Integer>> cityList) {
        int size = cityList.size();
        double[][] distances = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double distance = calculateDistance(cityList.get(i), cityList.get(j));
                distances[i][j] = distances[j][i] = distance;
            }
        }
        return distances;
    }

    private static List<Integer> createRandomRoute(int size) {
        List<Integer> route = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            route.add(i);
        }
        Collections.shuffle(route);
        return route;
    }

    private static List<State> createInitialPopulation(int populationSize, int cityCount) {
        List<State> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Integer> route = createRandomRoute(cityCount);
            population.add(new State(route));
        }
        return population;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of cities (N): ");
        int N = scanner.nextInt();

        List<List<Integer>> cityList = createRandomCityCoordinates(N);
        State.setDistances(createDistanceMatrix(cityList));

        int K = 150;
        int epochs = 100;
        int mutationPercentage = 20;

        List<State> population = createInitialPopulation(K, N);
        breed(population, epochs, mutationPercentage);
    }
}
