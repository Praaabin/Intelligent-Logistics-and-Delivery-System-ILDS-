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
     * Simulates traffic conditions and updates edge weights in the graph.
     */
    public void simulateTraffic() {
        for (Map.Entry<Node, Map<Node, Edge>> entry : graph.getAdjacencyList().entrySet()) {
            Node from = entry.getKey();
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Node to = edgeEntry.getKey();
                Edge edge = edgeEntry.getValue();

                // Simulate congestion by applying a random factor to travel time
                double congestionFactor = generateCongestionFactor(edge);
                double newTime = edge.getTime() * congestionFactor;

                // Update the graph with simulated traffic data
                graph.updateEdge(from.getId(), to.getId(), edge.getDistance(), newTime, congestionFactor);
            }
        }
    }

    /**
     * Generates a congestion factor based on randomness and edge properties.
     *
     * @param edge The edge for which congestion is simulated.
     * @return A congestion factor between 1.0 and 2.0.
     */
    private double generateCongestionFactor(Edge edge) {
        double baseCongestion = edge.getCongestion();
        return baseCongestion + random.nextDouble() * 0.5; // Add a random factor for dynamic traffic
    }
}
