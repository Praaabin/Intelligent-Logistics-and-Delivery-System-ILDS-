package com.logistics.graph;

import java.util.Objects;

public class Node {
    private final String id; // Unique identifier for a hub or location

    public Node(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty.");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" + "id='" + id + '\'' + '}';
    }
}
