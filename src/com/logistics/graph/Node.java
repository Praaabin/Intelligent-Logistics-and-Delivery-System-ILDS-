package com.logistics.graph;

import java.util.Objects;

/**
 * Represents a node (hub or location) in the logistics network.
 */
public class Node {
    private final String id; // Unique identifier for a hub or location

    /**
     * Constructs a new Node with the specified ID.
     *
     * @param id The unique identifier for the node.
     * @throws IllegalArgumentException If the ID is null or empty.
     */
    public Node(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty.");
        }
        this.id = id;
    }

    /**
     * Returns the ID of the node.
     *
     * @return The node's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Checks if this node is equal to another object.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    /**
     * Returns the hash code for this node.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the node.
     *
     * @return A string in the format "Node{id='<id>'}".
     */
    @Override
    public String toString() {
        return "Node{" + "id='" + id + '\'' + '}';
    }
}