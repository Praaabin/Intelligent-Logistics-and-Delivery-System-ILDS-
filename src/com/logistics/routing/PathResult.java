package com.logistics.routing;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PathResult {
    private final List<String> path;
    private final double totalDistance;
    private final double totalTime;
    private final double averageCongestion;

    /**
     * Constructs a PathResult object with path-related metrics.
     *
     * @param path              The list of nodes in the path.
     * @param totalDistance     The total distance of the path in kilometers.
     * @param totalTime         The total time of the path in minutes.
     * @param averageCongestion The average congestion along the path.
     */
    public PathResult(List<String> path, double totalDistance, double totalTime, double averageCongestion) {
        this.path = (path != null) ? Collections.unmodifiableList(path) : Collections.emptyList();
        this.totalDistance = totalDistance >= 0 ? totalDistance : Double.MAX_VALUE;
        this.totalTime = totalTime >= 0 ? totalTime : Double.MAX_VALUE;
        this.averageCongestion = averageCongestion >= 0 ? averageCongestion : 0.0;
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getAverageCongestion() {
        return averageCongestion;
    }

    @Override
    public String toString() {
        if (path.isEmpty()) {
            return "No valid route found.";
        }
        return String.format(
                "Optimal Route: %s%nTotal Distance: %.2f km%nTotal Time: %.2f mins%nAverage Congestion: %.2f",
                path, totalDistance, totalTime, averageCongestion
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathResult)) return false;
        PathResult that = (PathResult) o;
        return Double.compare(that.totalDistance, totalDistance) == 0 &&
                Double.compare(that.totalTime, totalTime) == 0 &&
                Double.compare(that.averageCongestion, averageCongestion) == 0 &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, totalDistance, totalTime, averageCongestion);
    }
}
