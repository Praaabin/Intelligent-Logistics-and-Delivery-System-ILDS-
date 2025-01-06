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
     * Adds a directed edge between two nodes.
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

        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            throw new IllegalArgumentException("One or both nodes do not exist in the graph.");
        }

        adjacencyList.get(from).put(to, new Edge(to, distance, time, congestion));
    }

    /**
     * Adds a bidirectional edge between two nodes.
     *
     * @param fromId     Source node ID.
     * @param toId       Destination node ID.
     * @param distance   Distance attribute for the edge.
     * @param time       Travel time attribute for the edge.
     * @param congestion Congestion level attribute for the edge.
     */
    public void addBidirectionalEdge(String fromId, String toId, double distance, double time, double congestion) {
        addEdge(fromId, toId, distance, time, congestion);
        addEdge(toId, fromId, distance, time, congestion);
    }

    /**
     * Removes a directed edge between two nodes.
     *
     * @param fromId Source node ID.
     * @param toId   Destination node ID.
     */
    public void removeEdge(String fromId, String toId) {
        Node from = new Node(fromId);
        Node to = new Node(toId);

        Map<Node, Edge> edgesFrom = adjacencyList.get(from);
        if (edgesFrom != null) {
            edgesFrom.remove(to);
        }
    }

    /**
     * Removes a bidirectional edge between two nodes.
     *
     * @param fromId Source node ID.
     * @param toId   Destination node ID.
     */
    public void removeBidirectionalEdge(String fromId, String toId) {
        removeEdge(fromId, toId);
        removeEdge(toId, fromId);
    }

    /**
     * Updates a directed edge's attributes.
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

        Map<Node, Edge> edgesFrom = adjacencyList.get(from);
        if (edgesFrom != null && edgesFrom.containsKey(to)) {
            edgesFrom.put(to, new Edge(to, distance, time, congestion));
            return true;
        }

        System.err.println("Error: Edge from " + fromId + " to " + toId + " does not exist.");
        return false;
    }

    /**
     * Updates a bidirectional edge's attributes.
     *
     * @param fromId     Source node ID.
     * @param toId       Destination node ID.
     * @param distance   New distance.
     * @param time       New time.
     * @param congestion New congestion level.
     * @return True if the edges exist and are updated; otherwise, false.
     */
    public boolean updateBidirectionalEdge(String fromId, String toId, double distance, double time, double congestion) {
        boolean updatedFromTo = updateEdge(fromId, toId, distance, time, congestion);
        boolean updatedToFrom = updateEdge(toId, fromId, distance, time, congestion);

        return updatedFromTo && updatedToFrom;
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
     * Returns the number of nodes in the graph.
     *
     * @return The number of nodes.
     */
    public int getNodeCount() {
        return adjacencyList.size();
    }

    /**
     * Returns the number of edges in the graph.
     *
     * @return The number of edges.
     */
    public int getEdgeCount() {
        int count = 0;
        for (Map<Node, Edge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count;
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
                System.out.printf("  -> %s (Distance: %.2f km, Time: %.2f mins, Congestion: %.2f)%n",
                        edge.getTarget().getId(), edge.getDistance(), edge.getTime(), edge.getCongestion());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== City Network ===\n");

        for (Map.Entry<Node, Map<Node, Edge>> entry : adjacencyList.entrySet()) {
            Node node = entry.getKey();
            sb.append("Node: ").append(node.getId()).append("\n");

            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Edge edge = edgeEntry.getValue();
                sb.append(String.format("  -> %-5s (Distance: %-5.2f km, Time: %-5.2f mins, Congestion: %-4.2f)\n",
                        edge.getTarget().getId(), edge.getDistance(), edge.getTime(), edge.getCongestion()));
            }
        }

        return sb.toString();
    }
}