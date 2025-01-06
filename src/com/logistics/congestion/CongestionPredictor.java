package com.logistics.congestion;

import com.logistics.graph.Edge;
import com.logistics.graph.Graph;
import com.logistics.graph.Node;
import com.logistics.routing.DijkstraAlgorithm;
import com.logistics.routing.AStarAlgorithm;
import com.logistics.routing.RoutePlanner;

import java.util.*;

public class CongestionPredictor {
    private final Graph graph;
    private final Map<String, Double> historicalCongestionData; // Historical congestion data for edges

    public CongestionPredictor(Graph graph) {
        this.graph = graph;
        this.historicalCongestionData = new HashMap<>();
    }

    /**
     * Predicts congestion and updates the graph's edge attributes to reflect traffic changes.
     */
    public void predictAndAdapt() {
        for (Map.Entry<Node, Map<Node, Edge>> entry : graph.getAdjacencyList().entrySet()) {
            Node from = entry.getKey();
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Node to = edgeEntry.getKey();
                Edge edge = edgeEntry.getValue();

                // Predict congestion based on historical data or simulation
                double updatedCongestion = predictCongestion(from.getId(), to.getId(), edge.getCongestion());

                // Simulate updated travel time based on congestion
                double newTime = edge.getTime() * (1 + updatedCongestion); // Increase time proportionally

                // Update edge with new traffic-adjusted attributes
                boolean updated = graph.updateEdge(from.getId(), to.getId(), edge.getDistance(), newTime, updatedCongestion);
                if (!updated) {
                    System.err.printf("Failed to update edge from %s to %s%n", from.getId(), to.getId());
                }
            }
        }
        System.out.println("Traffic simulation completed. Routes updated based on congestion.");
    }

    /**
     * Predicts congestion for an edge using historical data or simulation.
     *
     * @param fromId       Source node ID.
     * @param toId         Target node ID.
     * @param currentCongestion Current congestion level of the edge.
     * @return The predicted congestion level (0 to 1).
     */
    private double predictCongestion(String fromId, String toId, double currentCongestion) {
        String edgeKey = fromId + "-" + toId;

        // Use historical data if available, otherwise simulate congestion
        if (historicalCongestionData.containsKey(edgeKey)) {
            return historicalCongestionData.get(edgeKey);
        } else {
            return simulateCongestion(currentCongestion);
        }
    }

    /**
     * Simulates congestion dynamically based on current edge attributes.
     *
     * @param currentCongestion The current congestion level of the edge.
     * @return The new congestion value.
     */
    private double simulateCongestion(double currentCongestion) {
        // Example logic: simulate congestion as a random factor
        double randomFactor = 0.8 + (Math.random() * 0.4); // Random factor between 0.8 and 1.2
        double newCongestion = currentCongestion * randomFactor;

        // Ensure congestion stays within valid bounds (0 to 1)
        return Math.max(0, Math.min(1, newCongestion));
    }

    /**
     * Suggests alternative routes to avoid congestion.
     *
     * @param sourceId       Starting node ID.
     * @param targetId       Destination node ID.
     * @param vehicleCapacity The capacity constraint of the vehicle.
     * @param deadline       The delivery deadline in minutes.
     * @return A list of alternative paths.
     */
    public List<RoutePlanner.PathResult> suggestAlternativeRoutes(String sourceId, String targetId, int vehicleCapacity, double deadline) {
        List<RoutePlanner.PathResult> alternatives = new ArrayList<>();

        try {
            // Use Dijkstra's Algorithm to find alternative paths
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
            RoutePlanner.PathResult dijkstraPath = dijkstra.findShortestPathWithDetails(
                    sourceId, targetId, vehicleCapacity, deadline, false
            );

            if (!dijkstraPath.getPath().isEmpty()) {
                alternatives.add(dijkstraPath);
            }

            // Use A* Algorithm to find alternative paths
            AStarAlgorithm aStar = new AStarAlgorithm(graph);
            RoutePlanner.PathResult aStarPath = aStar.findMinimalTimePathWithDetails(
                    sourceId, targetId, vehicleCapacity, deadline
            );

            if (!aStarPath.getPath().isEmpty()) {
                alternatives.add(aStarPath);
            }
        } catch (Exception e) {
            System.err.printf("Error finding alternative routes: %s%n", e.getMessage());
        }

        return alternatives;
    }

    /**
     * Adds historical congestion data for an edge.
     *
     * @param fromId      Source node ID.
     * @param toId        Target node ID.
     * @param congestion  The historical congestion level (0 to 1).
     */
    public void addHistoricalData(String fromId, String toId, double congestion) {
        if (congestion < 0 || congestion > 1) {
            throw new IllegalArgumentException("Congestion must be between 0 and 1.");
        }
        String edgeKey = fromId + "-" + toId;
        historicalCongestionData.put(edgeKey, congestion);
    }
}