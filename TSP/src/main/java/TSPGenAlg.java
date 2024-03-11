import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class StateTSP implements Comparable<StateTSP> {
    private static double[][] distances; // distances between cities
    private final List<Integer> route; // permutation of cities
    private final double fitness; // value indicating how good the route is (lower values are better)

    public StateTSP(List<Integer> route) {
        this.route = route;
        this.fitness = computeFitness();
    }

    public static void setDistances(double[][] distances) {
        StateTSP.distances = distances;
    }

    // calculates the total distance of the route by summing the distances between consecutive cities
    private double computeFitness() {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int city1 = route.get(i);
            int city2 = route.get(i + 1);
            totalDistance += distances[city1][city2];
        }
        return totalDistance;
    }

    public void swapCities(int firstIndex, int secondIndex) {
        Collections.swap(route, firstIndex, secondIndex);
    }

    public List<Integer> getRoute() {
        return route;
    }

    public int size() {
        return route.size();
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(StateTSP other) {
        return Double.compare(this.fitness, other.fitness);
    }
}

public class TSPGenAlg {
    // generates random coordinates for cities within a specific range
    // generates a pair of random integers between -100 and 100
    // 0-200 and subtract 100
    private static List<List<Integer>> generateCityCoordinates(int citiesCount) {
        Random rand = new Random();
        List<List<Integer>> cityCoordinates = new ArrayList<>();
        for (int i = 0; i < citiesCount; i++) {
            cityCoordinates.add(List.of(rand.nextInt(200) - 100, rand.nextInt(200) - 100));
        }
        return cityCoordinates;
    }

    // computes the distances matrix based on the generated city coordinates
    // distance from city A to city B is the same as the distance between B and A
    private static double[][] createDistanceMatrix(List<List<Integer>> cityList) {
        int size = cityList.size();
        double[][] distances = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double distance = computeDistance(cityList.get(i), cityList.get(j));
                distances[i][j] = distances[j][i] = distance;
            }
        }
        return distances;
    }

    // compute the Euclidean distance between two cities
    private static double computeDistance(List<Integer> city1, List<Integer> city2) {
        int x1 = city1.get(0);
        int y1 = city1.get(1);
        int x2 = city2.get(0);
        int y2 = city2.get(1);
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // creates random permutation of cities
    private static List<Integer> generateRoute(int size) {
        List<Integer> route = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            route.add(i);
        }
        Collections.shuffle(route);
        return route;
    }

    // creates an initial population of states instances for the Generic Algorithm, where
    // each state represents potential solution to the TSP
    // populationSize = the desired size of the population, indicating the number of
    // potential solutions(route) to be generated
    // for each iteration, a random route is generated
    // add a new state to the population = states representing potential solutions
    private static List<StateTSP> initializePopulation(int populationSize, int cityCount) {
        List<StateTSP> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Integer> route = generateRoute(cityCount);
            population.add(new StateTSP(route));
        }
        return population;
    }

    // sorts the population and selects the top individuals based on fitness
    // performs selection in the Generic Algorithm, choosing the best individuals(states)
    // from the population based on their fitness values
    // 1/5th of the population size
    private static List<StateTSP> select(List<StateTSP> population) {
        Collections.sort(population);
        int selectionSize = population.size() / 5;
        return population.subList(0, selectionSize);
    }

    // randomly selects two parents from the top individuals in the populations
    // selecting two distinct parents from a given list of states (individuals)
    // for the crossover operation in the Generic Algorithm
    private static StateTSP[] chooseParents(List<StateTSP> selection) {
        Random random = new Random();
        int firstParentIndex = random.nextInt(selection.size());
        int secondParentIndex = random.nextInt(selection.size());
        while (firstParentIndex == secondParentIndex) {
            secondParentIndex = random.nextInt(selection.size());
        }
        StateTSP firstParent = selection.get(firstParentIndex);
        StateTSP secondParent = selection.get(secondParentIndex);
        return new StateTSP[] { firstParent, secondParent };
    }

    // generates random start and end genes for the crossover operation in the Generic Algorithm
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

    // performs crossover between two parents to create a child
    // a list representing the child's route, initially filled with -1 values
    // generates random start and end indices for the crossover
    // copies the genes from the first parent's route to the child's route within the specific segment
    // creates a list of remaining cities not copied from the first parent
    // copies genes from the second parent after the endGene index
    // appends genes from the second parent before the endGene index
    // initializes a position to start adding genes in the child route
    // iterates through genes from the second parent
    // checks if the city is not already in the child route
    // adds the city to the child route and increments the position
    // combines genes from two parents to create a new child route while
    // preserving the order of genes in a certain segment defined by the start and end indices
    // the remaining genes are filled from the second parent, avoiding duplicates
    // this operation introduces diversity and exploration in the population
    // during the generic algorithm optimization process
    private static StateTSP orderOneCrossover(StateTSP firstParent, StateTSP secondParent) {
        List<Integer> childRoute = new ArrayList<>(Collections.nCopies(firstParent.getRoute().size(), -1));
        int[] genes = generateStartAndEndGenes(firstParent.getRoute().size());
        int startGene = genes[0];
        int endGene = genes[1];

        for (int i = startGene; i <= endGene; i++) {
            childRoute.set(i, firstParent.getRoute().get(i));
        }

        List<Integer> remainingCities = new ArrayList<>(firstParent.getRoute());
        remainingCities.removeAll(childRoute);

        List<Integer> secondParentRoute = new ArrayList<>(secondParent.getRoute().subList(endGene + 1, secondParent.getRoute().size()));
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
        return new StateTSP(childRoute);
    }

    // performs the mutation operation in the Generic Algorithm
    // randomly swapping two cities in the route of given child
    // helps explore different solutions and avoid getting stuck in local optima
    // during the optimization process of the Generic Algorithm
    private static StateTSP mutate(StateTSP child) {
        Random random = new Random();
        int firstIndex = random.nextInt(child.getRoute().size());
        int secondIndex = random.nextInt(child.getRoute().size());
        while (firstIndex == secondIndex) {
            secondIndex = random.nextInt(child.getRoute().size());
        }
        child.swapCities(firstIndex, secondIndex);
        return child;
    }

    // top k individuals from the population based on their fitness values
    // sorts the population and creates a new list containing the best individuals
    private static List<StateTSP> getKBest(List<StateTSP> population, int k) {
        Collections.sort(population);
        return new ArrayList<>(population.subList(0, k));
    }

    // 1. Selection: it selects the top individuals from the population based on their fitness values
    // 2. Crossover and Mutation: for each individual in the population, it chooses two parents,
    // performs crossover to create two children, and applies mutation to each child with a
    // probability determined by the mutation percentage
    // 3. Population update: the new individuals are added to the population
    // 4. Selection of the Best Individuals: the population is updated by selecting
    // the top individuals based on their fitness values
    // 5. Print epoch info: the fitness of the best individual in the population is printed for each epoch
    // the breeding loop follows the generic algorithm steps and evolves the population
    // over multiple generations to improve the solutions for the TSP
    // performs evolution of the population over a specific number of epochs by selecting parents,
    // performing crossover and mutation, and updating the population with new individuals
    private static void breed(List<StateTSP> population, int epochs, int mutationPercentage) {
        Random random = new Random();
        int k = population.size();
        for (int epoch = 0; epoch < epochs; epoch++) {
            List<StateTSP> selection = select(population);
            List<StateTSP> newIndividuals = new ArrayList<>();

            for (int i = 0; i < population.size(); i++) {

                StateTSP[] parents = chooseParents(selection);
                StateTSP firstParent = parents[0];
                StateTSP secondParent = parents[1];

                StateTSP firstChild = orderOneCrossover(firstParent, secondParent);
                StateTSP secondChild = orderOneCrossover(secondParent, firstParent);

                if (random.nextInt(100) < mutationPercentage) {
                    firstChild = mutate(firstChild);
                }
                if (random.nextInt(100) < mutationPercentage) {
                    secondChild = mutate(secondChild);
                }
                newIndividuals.add(firstChild);
                newIndividuals.add(secondChild);
            }
            population.addAll(newIndividuals);
            population = getKBest(population, k);
            System.out.println("Epoch " + epoch + ": " + population.get(0).getFitness());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of cities (N): ");
        int N = scanner.nextInt();

        List<List<Integer>> cityList = generateCityCoordinates(N);
        StateTSP.setDistances(createDistanceMatrix(cityList));

        int K = 150;
        int epochs = 10;
        int mutationPercentage = 20;

        List<StateTSP> population = initializePopulation(K, N);
        breed(population, epochs, mutationPercentage);
    }
}