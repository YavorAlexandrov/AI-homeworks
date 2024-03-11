package org.example;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class KMeans {
    private final int K;
    private final List<double[]> points;
    private List<double[]> centroids;
    private List<List<double[]>> clusters;
    private List<List<double[]>> bestClusters;
    private double bestClustersMeasure;
    private List<double[]> bestCentroids;

    public KMeans(int K, List<double[]> points) {
        this.K = K;
        this.points = points;
        this.generateCentroidsOptimized();
        this.clusters = new ArrayList<>();
        this.bestClusters = new ArrayList<>();
        this.bestClustersMeasure = Double.POSITIVE_INFINITY;
        this.bestCentroids = new ArrayList<>();
    }

    private void generateCentroids() {
        centroids = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            centroids.add(points.get((int) (Math.random() * points.size())));
        }
    }

    private void generateCentroidsOptimized() {
        centroids = new ArrayList<>();
        centroids.add(points.get((int) (Math.random() * points.size())));
        for (int i = 1; i < K; i++) {
            double[] nextCentroid = points.stream()
                    .max((p1, p2) -> Double.compare(
                            computeDistanceToCentroid(p1, centroids),
                            computeDistanceToCentroid(p2, centroids)))
                    .orElseThrow();
            centroids.add(nextCentroid);
        }
    }

    private double computeDistanceToCentroid(double[] point, List<double[]> centroids) {
        return centroids.stream()
                .mapToDouble(centroid -> Math.pow(centroid[0] - point[0], 2) + Math.pow(centroid[1] - point[1], 2))
                .min()
                .orElseThrow();
    }

    private void putPointIntoACluster(double[] point) {
        int minimalDistanceIndex = 0;
        double minimalDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < K; i++) {
            double distance = computeDistanceToCentroid(point, List.of(centroids.get(i)));
            if (distance < minimalDistance) {
                minimalDistance = distance;
                minimalDistanceIndex = i;
            }
        }
        clusters.get(minimalDistanceIndex).add(point);
    }

    private void determineNewCentroids() {
        for (int i = 0; i < K; i++) {
            double xAverage = clusters.get(i).stream().mapToDouble(p -> p[0]).average().orElse(0);
            double yAverage = clusters.get(i).stream().mapToDouble(p -> p[1]).average().orElse(0);
            centroids.set(i, new double[]{xAverage, yAverage});
        }
    }

    private double measureCluster(int clusterIndex) {
        if (clusters.get(clusterIndex).isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        double sum = clusters.get(clusterIndex).stream()
                .mapToDouble(point -> computeDistanceToCentroid(point, List.of(centroids.get(clusterIndex))))
                .sum();
        return sum / clusters.get(clusterIndex).size();
    }

    public void clusterize() {
        for (int iteration = 0; iteration < 50; iteration++) {
            generateCentroidsOptimized();
            while (true) {
                clusters = new ArrayList<>(K);
                for (int i = 0; i < K; i++) {
                    clusters.add(new ArrayList<>());
                }

                for (double[] point : points) {
                    putPointIntoACluster(point);
                }

                List<double[]> oldCentroids = new ArrayList<>(centroids);
                determineNewCentroids();

                double difference = 0;
                for (int i = 0; i < K; i++) {
                    difference += Math.abs(centroids.get(i)[0] - oldCentroids.get(i)[0])
                            + Math.abs(centroids.get(i)[1] - oldCentroids.get(i)[1]);
                }

                if (difference < 1e-7) {
                    double currentClustersMeasure = 0;
                    for (int i = 0; i < K; i++) {
                        currentClustersMeasure += measureCluster(i);
                    }

                    if (currentClustersMeasure < bestClustersMeasure) {
                        bestClustersMeasure = currentClustersMeasure;
                        bestClusters.clear();
                        for (List<double[]> cluster : clusters) {
                            bestClusters.add(new ArrayList<>(cluster));
                        }
                        bestCentroids.clear();
                        bestCentroids.addAll(centroids);
                    }
                    break;
                }
            }
        }
    }

//    public void plot() {
//        for (int i = 0; i < K; i++) {
//            List<double[]> cluster = bestClusters.get(i);
//            XYSeries series = new XYSeries("Cluster " + (i + 1));
//            cluster.forEach(point -> series.add(point[0], point[1]));
//            plotDataset(series, Color.getHSBColor((float) Math.random(), 1, 1));
//        }
//
//        XYSeries centroidSeries = new XYSeries("Centroids");
//        bestCentroids.forEach(centroid -> centroidSeries.add(centroid[0], centroid[1]));
//        plotDataset(centroidSeries, Color.BLACK);
//
//        saveChart("output.png");
//    }
//
//    private void plotDataset(XYSeries series, Color color) {
//        XYSeriesCollection dataset = new XYSeriesCollection(series);
//        JFreeChart chart = ChartFactory.createScatterPlot("Cluster Plot", "X", "Y", dataset, PlotOrientation.VERTICAL, true, true, false);
//        XYPlot plot = chart.getXYPlot();
//        plot.getRenderer().setSeriesPaint(0, color);
//
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new Dimension(560, 370));
//        setContentPane(chartPanel);
//    }
//
//    private void saveChart(String fileName) {
//        try {
//            Thread.sleep(1000); // Added to ensure proper rendering before saving
//            ChartPanel chartPanel = (ChartPanel) getContentPane().getComponent(0);
//            chartPanel.doSaveAs();
//            Thread.sleep(500); // Added to ensure proper saving before exiting
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        }
//    }
}

