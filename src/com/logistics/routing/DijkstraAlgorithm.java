package com.logistics.routing;

import com.logistics.graph.Graph;
import com.logistics.graph.Node;
import com.logistics.graph.Edge;

import java.util.*;

public class DijkstraAlgorithm {
    private final Graph graph;

    public DijkstraAlgorithm(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path from source to target based on distance.
     *
     * @param sourceId        Source node ID.
     * @param targetId        Target node ID.
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadline        Delivery deadline.
     * @param considerTime    Whether to consider time as a constraint.
     * @return PathResult containing the shortest path, total distance, total time, and average congestion.
     */
    public RoutePlanner.PathResult findShortestPathWithDetails(String sourceId, String targetId, int vehicleCapacity, double deadline, boolean considerTime) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);

        if (!graph.getAdjacencyList().containsKey(source) || !graph.getAdjacencyList().containsKey(target)) {
            throw new IllegalArgumentException("Source or target node does not exist in the graph.");
        }

        // Priority queue to select the node with the smallest distance
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::getDistance));

        // Map to store the shortest distance to each node
        Map<Node, Double> distances = new HashMap<>();

        // Map to store the predecessor of each node in the shortest path
        Map<Node, Node> previous = new HashMap<>();

        // Set to track visited nodes
        Set<Node> visited = new HashSet<>();

        // Initialize distances: set source distance to 0 and all others to infinity
        distances.put(source, 0.0);
        pq.add(new NodeDistance(source, 0.0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node currentNode = current.getNode();

            // If we reach the target node, stop the algorithm
            if (currentNode.equals(target)) {
                break;
            }

            // Skip already visited nodes
            if (visited.contains(currentNode)) {
                continue;
            }
            visited.add(currentNode);

            // Traverse all neighbors of the current node
            for (Map.Entry<Node, Edge> neighborEntry : graph.getNeighbors(currentNode).entrySet()) {
                Node neighbor = neighborEntry.getKey();
                Edge edge = neighborEntry.getValue();

                // Calculate the new distance to the neighbor
                double newDistance = distances.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getDistance();

                // If a shorter path is found, update the distance and predecessor
                if (newDistance < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentNode);
                    pq.add(new NodeDistance(neighbor, newDistance));
                }
            }
        }

        // Reconstruct the shortest path
        List<String> path = reconstructPath(previous, source, target);

        // Calculate total distance, time, and average congestion
        double totalDistance = distances.getOrDefault(target, Double.MAX_VALUE);
        double totalTime = calculateTotalTime(path);
        double averageCongestion = calculateAverageCongestion(path);

        return new RoutePlanner.PathResult(path, totalDistance, totalTime, averageCongestion);
    }

    /**
     * Reconstructs the path from the target node to the source node.
     *
     * @param previous Map of nodes to their predecessors.
     * @param source   Source node.
     * @param target   Target node.
     * @return A list of node IDs representing the path.
     */
    private List<String> reconstructPath(Map<Node, Node> previous, Node source, Node target) {
        List<String> path = new ArrayList<>();
        for (Node at = target; at != null; at = previous.get(at)) {
            path.add(at.getId());
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0).equals(source.getId())) {
            return path;
        } else {
            return Collections.emptyList(); // No valid path
        }
    }

    /**
     * Calculates the total time required to traverse the path.
     *
     * @param path The path as a list of node IDs.
     * @return The total time in minutes.
     */
    private double calculateTotalTime(List<String> path) {
        double totalTime = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = new Node(path.get(i));
            Node to = new Node(path.get(i + 1));
            Edge edge = graph.getNeighbors(from).get(to);
            totalTime += edge.getTime();
        }
        return totalTime;
    }

    /**
     * Calculates the average congestion level along the path.
     *
     * @param path The path as a list of node IDs.
     * @return The average congestion level (0 to 1).
     */
    private double calculateAverageCongestion(List<String> path) {
        double totalCongestion = 0.0;
        int edgeCount = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = new Node(path.get(i));
            Node to = new Node(path.get(i + 1));
            Edge edge = graph.getNeighbors(from).get(to);
            totalCongestion += edge.getCongestion();
            edgeCount++;
        }
        return edgeCount == 0 ? 0.0 : totalCongestion / edgeCount;
    }

    /**
     * Helper class to store a node and its distance.
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