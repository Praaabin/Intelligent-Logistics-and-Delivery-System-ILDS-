package com.logistics.routing;

import com.logistics.graph.Graph;

import java.util.Collections;
import java.util.List;

public class RoutePlanner {
    private final Graph graph;

    public RoutePlanner(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the best path based on user preference.
     *
     * @param sourceId       Starting node ID.
     * @param targetId       Destination node ID.
     * @param preference     User preference (e.g., "shortest" or "minimal_time").
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadline       Delivery deadline.
     * @return The best path result including path, distance, time, and congestion.
     */
    public PathResult findBestPath(String sourceId, String targetId, String preference, int vehicleCapacity, double deadline) {
        PathResult pathResult = null;

        try {
            switch (preference.toLowerCase()) {
                case "shortest":
                    pathResult = new DijkstraAlgorithm(graph).findShortestPathWithDetails(sourceId, targetId, vehicleCapacity, deadline);
                    break;
                case "minimal_time":
                    pathResult = new AStarAlgorithm(graph).findMinimalTimePathWithDetails(sourceId, targetId, vehicleCapacity, deadline);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid preference. Use 'shortest' or 'minimal_time'.");
            }
        } catch (Exception e) {
            System.err.println("Error finding path: " + e.getMessage());
        }

        // Fallback if no valid path is found
        if (pathResult == null || pathResult.getPath().isEmpty()) {
            System.err.println("Error: No valid path found between " + sourceId + " and " + targetId);
            return new PathResult(Collections.emptyList(), Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        }

        return pathResult;
    }

    /**
     * Calculates the shortest distance between two nodes using Dijkstra's Algorithm.
     *
     * @param sourceId       The starting node.
     * @param targetId       The destination node.
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadline       Delivery deadline.
     * @return The total distance between source and target, or Double.MAX_VALUE if no valid path exists.
     */
    public double calculateDistance(String sourceId, String targetId, int vehicleCapacity, double deadline) {
        PathResult pathResult = findBestPath(sourceId, targetId, "shortest", vehicleCapacity, deadline);

        if (pathResult.getPath().isEmpty()) {
            return Double.MAX_VALUE; // Indicates no valid path
        }

        return pathResult.getTotalDistance();
    }

    /**
     * PathResult class to encapsulate path-related metrics.
     */
    public static class PathResult {
        private final List<String> path;
        private final double totalDistance;
        private final double totalTime;
        private final double averageCongestion;

        public PathResult(List<String> path, double totalDistance, double totalTime, double averageCongestion) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.averageCongestion = averageCongestion;
        }

        public List<String> getPath() {
            return path;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public double getTotalTime() {
            return totalTime;
        }

        public double getAverageCongestion() {
            return averageCongestion;
        }

        @Override
        public String toString() {
            if (path == null || path.isEmpty()) {
                return "No valid route found.";
            }
            return String.format(
                    "Optimal Route: %s%nTotal Distance: %.2f km%nTotal Time: %.2f mins%nAverage Congestion: %.2f",
                    path, totalDistance, totalTime, averageCongestion
            );
        }
    }
}
