package com.logistics.congestion;

import com.logistics.graph.Edge;
import com.logistics.graph.Graph;
import com.logistics.graph.Node;
import com.logistics.routing.DijkstraAlgorithm;
import com.logistics.routing.RoutePlanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CongestionPredictor {
    private final Graph graph;

    public CongestionPredictor(Graph graph) {
        this.graph = graph;
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

                // Simulate updated traffic conditions
                double updatedCongestion = simulateCongestion(edge);
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
            RoutePlanner.PathResult alternativePath = dijkstra.findShortestPathWithDetails(sourceId, targetId, vehicleCapacity, deadline);

            if (!alternativePath.getPath().isEmpty()) {
                alternatives.add(alternativePath);
            }
        } catch (Exception e) {
            System.err.printf("Error finding alternative routes: %s%n", e.getMessage());
        }

        return alternatives;
    }

    /**
     * Simulates congestion dynamically based on current edge attributes.
     *
     * @param edge The edge to simulate congestion for.
     * @return The new congestion value.
     */
    private double simulateCongestion(Edge edge) {
        // Example logic: simulate congestion as a random factor
        double randomFactor = 0.8 + (Math.random() * 0.4); // Random factor between 0.8 and 1.2
        return edge.getCongestion() * randomFactor;
    }
}
