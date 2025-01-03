package com.logistics.graph;

public class Edge {
    private final Node target; // The node this edge leads to
    private final double distance;
    private final double time;
    private final double congestion;

    public Edge(Node target, double distance, double time, double congestion) {
        if (distance < 0 || time < 0 || congestion < 0) {
            throw new IllegalArgumentException("Distance, time, and congestion must be non-negative.");
        }
        this.target = target;
        this.distance = distance;
        this.time = time;
        this.congestion = congestion;
    }

    public Node getTarget() {
        return target;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public double getCongestion() {
        return congestion;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "target=" + target +
                ", distance=" + distance +
                ", time=" + time +
                ", congestion=" + congestion +
                '}';
    }
}
