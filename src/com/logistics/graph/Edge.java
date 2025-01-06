package com.logistics.graph;

import java.util.Objects;

public class Edge {
    private final Node target; // The node this edge leads to
    private final double distance;
    private final double time;
    private final double congestion;

    /**
     * Constructs an Edge with specified target node, distance, time, and congestion.
     *
     * @param target     The target node this edge connects to.
     * @param distance   The distance of the edge (must be non-negative).
     * @param time       The time required to traverse the edge (must be non-negative).
     * @param congestion The congestion level of the edge (must be between 0 and 1).
     * @throws IllegalArgumentException if distance, time, or congestion is invalid.
     */
    public Edge(Node target, double distance, double time, double congestion) {
        if (distance < 0 || time < 0) {
            throw new IllegalArgumentException("Distance and time must be non-negative.");
        }
        if (congestion < 0 || congestion > 1) {
            throw new IllegalArgumentException("Congestion must be between 0 and 1.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target node cannot be null.");
        }
        this.target = target;
        this.distance = distance;
        this.time = time;
        this.congestion = congestion;
    }

    /**
     * Returns the target node of the edge.
     *
     * @return The target node.
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the distance of the edge.
     *
     * @return The distance in kilometers.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the time required to traverse the edge.
     *
     * @return The time in minutes.
     */
    public double getTime() {
        return time;
    }

    /**
     * Returns the congestion level of the edge.
     *
     * @return The congestion level (0 to 1).
     */
    public double getCongestion() {
        return congestion;
    }

    /**
     * Returns congestion level as a descriptive string.
     *
     * @return Descriptive congestion level ("Light", "Moderate", "Heavy").
     */
    public String getCongestionLevel() {
        if (congestion < 0.3) {
            return "Light";
        } else if (congestion < 0.7) {
            return "Moderate";
        } else {
            return "Heavy";
        }
    }

    /**
     * Checks if two edges are equivalent based on target node and attributes.
     *
     * @param o The object to compare with.
     * @return True if edges are equivalent, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.distance, distance) == 0 &&
                Double.compare(edge.time, time) == 0 &&
                Double.compare(edge.congestion, congestion) == 0 &&
                Objects.equals(target, edge.target);
    }

    /**
     * Generates a hash code for the Edge.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(target, distance, time, congestion);
    }

    /**
     * Provides a detailed string representation of the Edge.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return String.format("Edge{target=%s, distance=%.2f km, time=%.2f mins, congestion=%s}",
                target.getId(), distance, time, getCongestionLevel());
    }
}