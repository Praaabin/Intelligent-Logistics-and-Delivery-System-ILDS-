package com.logistics.routing;

import com.logistics.graph.Graph;
import com.logistics.scheduling.Vehicle;
import com.logistics.scheduling.DeliveryRequest;

import java.util.*;

public class RoutePlanner {
    private final Graph graph;

    public RoutePlanner(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the best path based on user preference.
     *
     * @param sourceId        Starting node ID.
     * @param targetId        Destination node ID.
     * @param preference      User preference (e.g., "shortest" or "minimal_time").
     * @param vehicleCapacity Vehicle capacity constraint.
     * @param deadline        Delivery deadline.
     * @return The best path result including path, distance, time, and congestion.
     */
    public PathResult findBestPath(String sourceId, String targetId, String preference, int vehicleCapacity, double deadline) {
        PathResult pathResult = null;

        try {
            switch (preference.toLowerCase()) {
                case "shortest" -> pathResult = new DijkstraAlgorithm(graph).findShortestPathWithDetails(
                        sourceId, targetId, vehicleCapacity, deadline, false);
                case "minimal_time" -> pathResult = new AStarAlgorithm(graph).findMinimalTimePathWithDetails(
                        sourceId, targetId, vehicleCapacity, deadline);
                default -> throw new IllegalArgumentException("Invalid preference. Use 'shortest' or 'minimal_time'.");
            }
        } catch (Exception e) {
            System.err.printf("Error finding path: %s%n", e.getMessage());
        }

        // Fallback if no valid path is found
        if (pathResult == null || pathResult.getPath().isEmpty()) {
            System.err.printf("Error: No valid path found between %s and %s%n", sourceId, targetId);
            return new PathResult(Collections.emptyList(), Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        }

        return pathResult;
    }

    /**
     * Assigns deliveries to vehicles based on constraints like capacity and deadlines.
     *
     * @param vehicles         List of available vehicles.
     * @param deliveryRequests List of delivery requests.
     */
    public void assignDeliveries(List<Vehicle> vehicles, List<DeliveryRequest> deliveryRequests) {
        for (DeliveryRequest request : deliveryRequests) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.canAccommodate(request.getPackageCount())) {
                    PathResult pathResult = findBestPath(
                            vehicle.getCurrentLocation(),
                            request.getDestination(),
                            "minimal_time",
                            vehicle.getCapacity(),
                            request.getDeadline()
                    );

                    if (!pathResult.getPath().isEmpty() && pathResult.getTotalTime() <= request.getDeadline()) {
                        vehicle.addDelivery(request);
                        request.setVehicleId(vehicle.getId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * PathResult class to encapsulate path-related metrics.
     */
    public static class PathResult {
        private final List<String> path;
        private final double totalDistance;
        private final double totalTime;
        private final double averageCongestion;

        public PathResult(List<String> path, double totalDistance, double totalTime, double averageCongestion) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.averageCongestion = averageCongestion;
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

        /**
         * Converts the numeric average congestion value into a descriptive level.
         *
         * @return Congestion level as a String ("Light", "Moderate", "Heavy").
         */
        public String getAverageCongestionLevel() {
            if (averageCongestion < 0.3) {
                return "Light";
            } else if (averageCongestion < 0.7) {
                return "Moderate";
            } else {
                return "Heavy";
            }
        }

        @Override
        public String toString() {
            if (path == null || path.isEmpty()) {
                return "No valid route found.";
            }
            return String.format(
                    "Optimal Route: %s%nTotal Distance: %.2f km%nTotal Time: %.2f mins%nAverage Congestion: %s",
                    path, totalDistance, totalTime, getAverageCongestionLevel()
            );
        }
    }
}