package com.logistics.routing;

import com.logistics.graph.Graph;
import com.logistics.graph.Node;
import com.logistics.graph.Edge;

import java.util.*;

public class AStarAlgorithm {
    private final Graph graph;

    public AStarAlgorithm(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the minimal time path from source to target using A* algorithm.
     *
     * @param sourceId        Source node ID.
     * @param targetId        Target node ID.
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadline        Delivery deadline.
     * @return PathResult containing the minimal time path, total distance, total time, and average congestion.
     */
    public RoutePlanner.PathResult findMinimalTimePathWithDetails(String sourceId, String targetId, int vehicleCapacity, double deadline) {
        Node source = new Node(sourceId);
        Node target = new Node(targetId);

        if (!graph.getAdjacencyList().containsKey(source) || !graph.getAdjacencyList().containsKey(target)) {
            throw new IllegalArgumentException("Source or target node does not exist in the graph.");
        }

        // Priority queue to select the node with the smallest estimated total time
        PriorityQueue<NodeTime> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeTime::getEstimatedTotalTime));

        // Map to store the shortest time to each node
        Map<Node, Double> times = new HashMap<>();

        // Map to store the predecessor of each node in the shortest path
        Map<Node, Node> previous = new HashMap<>();

        // Set to track visited nodes
        Set<Node> visited = new HashSet<>();

        // Initialize times: set source time to 0 and all others to infinity
        times.put(source, 0.0);
        pq.add(new NodeTime(source, 0.0, heuristic(source, target)));

        while (!pq.isEmpty()) {
            NodeTime current = pq.poll();
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

                // Calculate the new time to the neighbor
                double newTime = times.getOrDefault(currentNode, Double.MAX_VALUE) + edge.getTime();

                // If a shorter path is found, update the time and predecessor
                if (newTime < times.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    times.put(neighbor, newTime);
                    previous.put(neighbor, currentNode);
                    pq.add(new NodeTime(neighbor, newTime, heuristic(neighbor, target)));
                }
            }
        }

        // Reconstruct the minimal time path
        List<String> path = reconstructPath(previous, source, target);

        // Calculate total distance, time, and average congestion
        double totalDistance = calculateTotalDistance(path);
        double totalTime = times.getOrDefault(target, Double.MAX_VALUE);
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
     * Calculates the total distance of the path.
     *
     * @param path The path as a list of node IDs.
     * @return The total distance in kilometers.
     */
    private double calculateTotalDistance(List<String> path) {
        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = new Node(path.get(i));
            Node to = new Node(path.get(i + 1));
            Edge edge = graph.getNeighbors(from).get(to);
            totalDistance += edge.getDistance();
        }
        return totalDistance;
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
     * Heuristic function for A* algorithm (Euclidean distance).
     *
     * @param node   Current node.
     * @param target Target node.
     * @return Estimated time from the current node to the target node.
     */
    private double heuristic(Node node, Node target) {
        // Placeholder: Replace with actual heuristic calculation (e.g., Euclidean distance)
        return 0.0;
    }

    /**
     * Helper class to store a node, its time, and its estimated total time.
     */
    private static class NodeTime {
        private final Node node;
        private final double time;
        private final double estimatedTotalTime;

        public NodeTime(Node node, double time, double heuristic) {
            this.node = node;
            this.time = time;
            this.estimatedTotalTime = time + heuristic;
        }

        public Node getNode() {
            return node;
        }

        public double getEstimatedTotalTime() {
            return estimatedTotalTime;
        }
    }
}