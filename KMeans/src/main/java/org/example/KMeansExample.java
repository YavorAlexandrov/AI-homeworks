package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansExample {
    public static void main(String[] args) {
        String filename = "normal.txt"; // Replace with your input file name
        int clustersNumber = 3; // Replace with the desired number of clusters
        List<double[]> points = loadPoints(filename);

        KMeans kmeans = new KMeans(clustersNumber, points);
        kmeans.clusterize();
        //kmeans.plot();
    }

    private static List<double[]> loadPoints(String filepath) {
        List<double[]> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] coordinates = line.split("\\s+");
                double x = Double.parseDouble(coordinates[0]);
                double y = Double.parseDouble(coordinates[1]);
                points.add(new double[] { x, y });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }
}
