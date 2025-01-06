package com.logistics.congestion;

import com.logistics.graph.Edge;
import com.logistics.graph.Graph;
import com.logistics.graph.Node;

import java.util.Map;
import java.util.Random;

public class TrafficSimulator {
    private final Graph graph;
    private final Random random = new Random();

    public TrafficSimulator(Graph graph) {
        this.graph = graph;
    }

    /**
     * Simulates traffic conditions and updates edge attributes in the graph.
     */
    public void simulateTraffic() {
        for (Map.Entry<Node, Map<Node, Edge>> entry : graph.getAdjacencyList().entrySet()) {
            Node from = entry.getKey();
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Node to = edgeEntry.getKey();
                Edge edge = edgeEntry.getValue();

                // Simulate congestion and travel time
                double congestionFactor = generateCongestionFactor(edge);
                double newCongestion = calculateNewCongestion(edge.getCongestion(), congestionFactor);
                double newTime = calculateNewTravelTime(edge.getTime(), newCongestion);

                // Update the graph with simulated traffic data
                boolean updated = graph.updateEdge(from.getId(), to.getId(), edge.getDistance(), newTime, newCongestion);
                if (!updated) {
                    System.err.printf("Failed to update edge from %s to %s%n", from.getId(), to.getId());
                }
            }
        }
        System.out.println("Traffic simulation completed. Edge attributes updated.");
    }

    /**
     * Generates a congestion factor based on randomness and edge properties.
     *
     * @param edge The edge for which congestion is simulated.
     * @return A congestion factor between 0.5 and 1.5.
     */
    private double generateCongestionFactor(Edge edge) {
        // Base congestion factor based on the edge's current congestion
        double baseCongestion = edge.getCongestion();

        // Add a random factor to simulate dynamic traffic conditions
        double randomFactor = 0.5 + random.nextDouble(); // Random factor between 0.5 and 1.5
        return baseCongestion * randomFactor;
    }

    /**
     * Calculates the new congestion level based on the congestion factor.
     *
     * @param currentCongestion The current congestion level of the edge.
     * @param congestionFactor  The congestion factor generated for the edge.
     * @return The new congestion level (0 to 1).
     */
    private double calculateNewCongestion(double currentCongestion, double congestionFactor) {
        double newCongestion = currentCongestion * congestionFactor;

        // Ensure congestion stays within valid bounds (0 to 1)
        return Math.max(0, Math.min(1, newCongestion));
    }

    /**
     * Calculates the new travel time based on the updated congestion level.
     *
     * @param currentTime      The current travel time of the edge.
     * @param newCongestion    The new congestion level of the edge.
     * @return The new travel time.
     */
    private double calculateNewTravelTime(double currentTime, double newCongestion) {
        // Travel time increases proportionally with congestion
        return currentTime * (1 + newCongestion);
    }
}