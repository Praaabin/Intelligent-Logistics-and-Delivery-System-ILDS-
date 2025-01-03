package com.logistics.routing;

import com.logistics.graph.Edge;
import com.logistics.graph.Graph;
import com.logistics.graph.Node;

import java.util.*;

public class AStarAlgorithm {
    private final Graph graph;

    public AStarAlgorithm(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the minimal time path with constraints using the A* algorithm.
     *
     * @param sourceId        The starting node ID.
     * @param targetId        The destination node ID.
     * @param vehicleCapacity The capacity of the vehicle (constraint).
     * @param deadline        The deadline for the delivery in hours.
     * @return The optimal path as a RoutePlanner.PathResult object.
     */
    public RoutePlanner.PathResult findMinimalTimePathWithDetails(String sourceId, String targetId, int vehicleCapacity, double deadline) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);

        if (!graph.hasNode(source.getId()) || !graph.hasNode(target.getId())) {
            throw new IllegalArgumentException("One or both nodes do not exist in the graph.");
        }

        // Initialize data structures
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::getPriority));
        Map<Node, Double> times = new HashMap<>();
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        times.put(source, 0.0);
        distances.put(source, 0.0);
        pq.add(new NodeDistance(source, heuristic(source, target)));

        double deadlineInMinutes = deadline * 60; // Convert hours to minutes

        // Process the priority queue
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node currentNode = current.getNode();

            // Skip already visited nodes
            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);

            // Stop if the target is reached
            if (currentNode.equals(target)) break;

            // Explore neighbors
            for (Map.Entry<Node, Edge> neighborEntry : graph.getNeighbors(currentNode).entrySet()) {
                Node neighbor = neighborEntry.getKey();
                Edge edge = neighborEntry.getValue();

                double newTime = times.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getTime();
                double newDistance = distances.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getDistance();

                // Validate constraints: deadline and vehicle capacity
                if (newTime > deadlineInMinutes || edge.getCongestion() > vehicleCapacity) continue;

                // Update if a better path is found
                if (newTime < times.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    times.put(neighbor, newTime);
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newTime + heuristic(neighbor, target)));
                }
            }
        }

        // Reconstruct the path
        List<String> path = reconstructPath(previous, source, target);
        if (path.isEmpty()) {
            System.err.printf("Error: No valid path found between %s and %s%n", sourceId, targetId);
        }
        double totalDistance = distances.getOrDefault(target, Double.MAX_VALUE);
        double totalTime = times.getOrDefault(target, Double.MAX_VALUE);
        double averageCongestion = calculateAverageCongestion(path);

        return new RoutePlanner.PathResult(path, totalDistance, totalTime, averageCongestion);
    }

    /**
     * Heuristic function for A* algorithm.
     *
     * @param current The current node.
     * @param target  The target node.
     * @return Estimated time to reach the target from the current node.
     */
    private double heuristic(Node current, Node target) {
        // Heuristic uses the straight-line distance approximation (or any admissible heuristic).
        double averageSpeed = 60.0; // Assumed average speed in km/h
        return graph.getNeighbors(current).values()
                .stream()
                .mapToDouble(Edge::getDistance)
                .min()
                .orElse(0.0) / averageSpeed; // Estimated time in hours
    }

    /**
     * Reconstructs the path using the 'previous' map.
     *
     * @param previous The map of nodes to their predecessors.
     * @param source   The source node.
     * @param target   The target node.
     * @return The reconstructed path as a list of node IDs.
     */
    private List<String> reconstructPath(Map<Node, Node> previous, Node source, Node target) {
        List<String> path = new ArrayList<>();
        for (Node at = target; at != null; at = previous.get(at)) {
            path.add(at.getId());
        }
        Collections.reverse(path);

        // Return empty if the path doesn't start from the source
        return path.isEmpty() || !path.get(0).equals(source.getId()) ? Collections.emptyList() : path;
    }

    /**
     * Calculates the average congestion for a given path.
     *
     * @param path The path as a list of node IDs.
     * @return The average congestion across the path.
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
     * Helper class to store node distances and priorities for the priority queue.
     */
    private static class NodeDistance {
        private final Node node;
        private final double priority;

        public NodeDistance(Node node, double priority) {
            this.node = node;
            this.priority = priority;
        }

        public Node getNode() {
            return node;
        }

        public double getPriority() {
            return priority;
        }
    }
}
