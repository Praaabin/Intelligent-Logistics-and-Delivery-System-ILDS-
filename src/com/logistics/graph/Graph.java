package com.logistics.graph;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    private final Map<Node, Map<Node, Edge>> adjacencyList = new HashMap<>();

    /**
     * Adds a new node to the graph.
     *
     * @param nodeId Unique identifier for the node.
     */
    public void addNode(String nodeId) {
        adjacencyList.putIfAbsent(new Node(nodeId), new HashMap<>());
    }

    /**
     * Checks if a node exists in the graph.
     *
     * @param nodeId Node ID to check.
     * @return True if the node exists, otherwise false.
     */
    public boolean hasNode(String nodeId) {
        return adjacencyList.containsKey(new Node(nodeId));
    }

    /**
     * Removes a node and all associated edges.
     *
     * @param nodeId Node to remove.
     */
    public void removeNode(String nodeId) {
        Node nodeToRemove = new Node(nodeId);
        adjacencyList.values().forEach(edges -> edges.remove(nodeToRemove));
        adjacencyList.remove(nodeToRemove);
    }

    /**
     * Adds a bidirectional edge between two nodes.
     *
     * @param fromId     Source node ID.
     * @param toId       Destination node ID.
     * @param distance   Distance attribute.
     * @param time       Travel time attribute.
     * @param congestion Congestion level attribute.
     */
    public void addEdge(String fromId, String toId, double distance, double time, double congestion) {
        Node from = new Node(fromId);
        Node to = new Node(toId);

        // Add edge from source to destination
        adjacencyList.computeIfAbsent(from, k -> new HashMap<>())
                .put(to, new Edge(to, distance, time, congestion));

        // Add reverse edge from destination to source
        adjacencyList.computeIfAbsent(to, k -> new HashMap<>())
                .put(from, new Edge(from, distance, time, congestion));
    }

    /**
     * Removes a bidirectional edge between two nodes.
     *
     * @param fromId Source node ID.
     * @param toId   Destination node ID.
     */
    public void removeEdge(String fromId, String toId) {
        Node from = new Node(fromId);
        Node to = new Node(toId);

        // Remove edge from source to destination
        Map<Node, Edge> edgesFrom = adjacencyList.get(from);
        if (edgesFrom != null) {
            edgesFrom.remove(to);
        }

        // Remove reverse edge from destination to source
        Map<Node, Edge> edgesTo = adjacencyList.get(to);
        if (edgesTo != null) {
            edgesTo.remove(from);
        }
    }

    /**
     * Updates a bidirectional edge's attributes.
     *
     * @param fromId     Source node ID.
     * @param toId       Destination node ID.
     * @param distance   New distance.
     * @param time       New time.
     * @param congestion New congestion level.
     * @return True if the edge exists and is updated; otherwise, false.
     */
    public boolean updateEdge(String fromId, String toId, double distance, double time, double congestion) {
        Node from = new Node(fromId);
        Node to = new Node(toId);

        boolean updated = false;

        // Update edge from source to destination
        if (adjacencyList.containsKey(from) && adjacencyList.get(from).containsKey(to)) {
            adjacencyList.get(from).put(to, new Edge(to, distance, time, congestion));
            updated = true;
        }

        // Update reverse edge from destination to source
        if (adjacencyList.containsKey(to) && adjacencyList.get(to).containsKey(from)) {
            adjacencyList.get(to).put(from, new Edge(from, distance, time, congestion));
            updated = true;
        }

        if (!updated) {
            System.out.println("Edge between " + fromId + " and " + toId + " does not exist.");
        }

        return updated;
    }

    /**
     * Retrieves the adjacency list of the graph.
     *
     * @return The adjacency list as a Map.
     */
    public Map<Node, Map<Node, Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Retrieves all neighbors of a given node.
     *
     * @param node Node whose neighbors are to be retrieved.
     * @return Map of neighbors and their corresponding edges.
     */
    public Map<Node, Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, new HashMap<>());
    }

    /**
     * Prints the graph structure for debugging.
     */
    public void printGraph() {
        System.out.println("Graph Structure:");
        for (Map.Entry<Node, Map<Node, Edge>> entry : adjacencyList.entrySet()) {
            Node node = entry.getKey();
            System.out.println("Node: " + node.getId());
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Edge edge = edgeEntry.getValue();
                System.out.printf("  -> %s (Distance: %.2f, Time: %.2f, Congestion: %.2f)%n",
                        edge.getTarget().getId(), edge.getDistance(), edge.getTime(), edge.getCongestion());
            }
        }
    }
}
