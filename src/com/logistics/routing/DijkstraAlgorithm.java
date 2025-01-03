package com.logistics.routing;

import com.logistics.graph.Edge;
import com.logistics.graph.Graph;
import com.logistics.graph.Node;

import java.util.*;

public class DijkstraAlgorithm {
    private final Graph graph;

    public DijkstraAlgorithm(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path with details considering constraints like vehicle capacity and deadlines.
     *
     * @param sourceId        Starting node ID.
     * @param targetId        Destination node ID.
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadlineInHours Deadline constraint in hours.
     * @return PathResult object containing path details.
     */
    public RoutePlanner.PathResult findShortestPathWithDetails(String sourceId, String targetId, int vehicleCapacity, double deadlineInHours) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);

        if (!graph.hasNode(source.getId()) || !graph.hasNode(target.getId())) {
            throw new IllegalArgumentException("One or both nodes do not exist in the graph.");
        }

        // Initialize data structures
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::getDistance));
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Double> times = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        distances.put(source, 0.0);
        times.put(source, 0.0);
        pq.add(new NodeDistance(source, 0.0));

        double deadlineInMinutes = deadlineInHours * 60; // Convert hours to minutes

        // Process the priority queue
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node currentNode = current.getNode();

            // Skip if already visited
            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);

            // Stop if the target is reached
            if (currentNode.equals(target)) break;

            // Explore neighbors
            for (Map.Entry<Node, Edge> neighborEntry : graph.getNeighbors(currentNode).entrySet()) {
                Node neighbor = neighborEntry.getKey();
                Edge edge = neighborEntry.getValue();

                // Validate constraints
                if (edge.getCongestion() > vehicleCapacity) continue; // Skip if capacity constraint is violated

                double newTime = times.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getTime();
                if (newTime > deadlineInMinutes) continue; // Skip if deadline is exceeded

                double newDistance = distances.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getDistance();

                // Update shortest path if a better path is found
                if (newDistance < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    times.put(neighbor, newTime);
                    previous.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newDistance));
                }
            }
        }

        // Reconstruct path
        List<String> path = reconstructPath(previous, source, target);
        double totalDistance = distances.getOrDefault(target, Double.MAX_VALUE);
        double totalTime = times.getOrDefault(target, Double.MAX_VALUE);
        double averageCongestion = calculateAverageCongestion(path);

        // Check if no valid path was found
        if (path.isEmpty() || totalDistance == Double.MAX_VALUE || totalTime == Double.MAX_VALUE) {
            System.err.printf("Error: No valid path found between %s and %s%n", sourceId, targetId);
            return new RoutePlanner.PathResult(Collections.emptyList(), Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        }

        return new RoutePlanner.PathResult(path, totalDistance, totalTime, averageCongestion);
    }

    /**
     * Reconstructs the path from the previous node map.
     *
     * @param previous Map of nodes to their predecessors.
     * @param source   The source node.
     * @param target   The target node.
     * @return A list of node IDs representing the path.
     */
    private List<String> reconstructPath(Map<Node, Node> previous, Node source, Node target) {
        List<String> path = new ArrayList<>();
        for (Node at = target; at != null; at = previous.get(at)) {
            path.add(at.getId());
        }
        Collections.reverse(path);

        // Return an empty path if the path doesn't start from the source
        if (!path.isEmpty() && !path.get(0).equals(source.getId())) {
            return Collections.emptyList();
        }
        return path;
    }

    /**
     * Calculates the average congestion along the path.
     *
     * @param path The list of node IDs in the path.
     * @return The average congestion value.
     */
    private double calculateAverageCongestion(List<String> path) {
        double totalCongestion = 0;
        int edgeCount = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            Node from = new Node(path.get(i));
            Node to = new Node(path.get(i + 1));
            Edge edge = graph.getNeighbors(from).get(to);

            if (edge != null) {
                totalCongestion += edge.getCongestion();
                edgeCount++;
            }
        }

        return edgeCount > 0 ? totalCongestion / edgeCount : 0.0;
    }

    /**
     * Helper class to store node distances in the priority queue.
     */
    private static class NodeDistance {
        private final Node node;
        private final double distance;

        public NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        public Node getNode() {
            return node;
        }

        public double getDistance() {
            return distance;
        }
    }
}
