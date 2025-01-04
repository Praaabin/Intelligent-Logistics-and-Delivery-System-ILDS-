package com.logistics.scheduling;

import com.logistics.routing.RoutePlanner;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Scheduler {
    private final List<Vehicle> vehicles; // List of all available vehicles
    private final RoutePlanner routePlanner; // Route planner for finding efficient routes

    public Scheduler(List<Vehicle> vehicles, RoutePlanner routePlanner) {
        this.vehicles = vehicles;
        this.routePlanner = routePlanner;
    }

    /**
     * Schedule deliveries based on priority, vehicle capacity, and deadlines.
     *
     * @param deliveries List of deliveries (each delivery has source, destination, package count, and urgency).
     */
    public void scheduleDeliveries(List<DeliveryRequest> deliveries) {
        // Sort deliveries by urgency (high to low priority) and deadline (earliest first)
        deliveries.sort((d1, d2) -> {
            int urgencyComparison = Integer.compare(d2.getUrgency(), d1.getUrgency());
            if (urgencyComparison == 0) {
                return Double.compare(d1.getDeadline(), d2.getDeadline());
            }
            return urgencyComparison;
        });

        for (DeliveryRequest delivery : deliveries) {
            Vehicle assignedVehicle = findAvailableVehicle(delivery);

            if (assignedVehicle != null) {
                // Assign delivery to the vehicle
                assignedVehicle.assignDelivery(delivery);

                // Find the best route
                RoutePlanner.PathResult pathResult = routePlanner.findBestPath(
                        assignedVehicle.getCurrentLocation(),
                        delivery.getDestination(),
                        "shortest", // Use "shortest" as the routing preference
                        assignedVehicle.getAvailableCapacity(),
                        delivery.getDeadline()
                );

                // Update vehicle details
                assignedVehicle.addDistance(pathResult.getTotalDistance());
                assignedVehicle.setCurrentLocation(delivery.getDestination());

                // Print delivery details
                System.out.println("Delivery scheduled:");
                System.out.println("Vehicle: " + assignedVehicle.getId());
                System.out.println("Path: " + pathResult.getPath());
                System.out.printf("Total Distance: %.2f km%n", pathResult.getTotalDistance());
                System.out.printf("Total Time: %.2f mins%n", pathResult.getTotalTime());
                System.out.printf("Average Congestion: %.2f%n", pathResult.getAverageCongestion());
            } else {
                System.out.println("No available vehicle for delivery to " + delivery.getDestination());
            }
        }

        // Print summary
        System.out.println("\n=== Delivery Summary ===");
        for (Vehicle vehicle : vehicles) {
            System.out.println("Vehicle " + vehicle.getId() + " Summary:");
            System.out.println("  Total Distance Traveled: " + vehicle.getTotalDistanceTraveled() + " km");
            System.out.println("  Deliveries: " +
                    vehicle.getDeliveries().stream()
                            .map(d -> d.getSource() + " -> " + d.getDestination())
                            .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Finds an available vehicle that can accommodate the delivery and is closest to the source.
     *
     * @param delivery The delivery request.
     * @return The assigned vehicle or null if no suitable vehicle is found.
     */
    private Vehicle findAvailableVehicle(DeliveryRequest delivery) {
        Vehicle bestVehicle = null;
        double minDistance = Double.MAX_VALUE;

        for (Vehicle vehicle : vehicles) {
            if (vehicle.canAccommodate(delivery.getPackageCount())) {
                // Calculate distance to the delivery source
                double distanceToSource = routePlanner.calculateDistance(
                        vehicle.getCurrentLocation(),
                        delivery.getSource(),
                        vehicle.getAvailableCapacity(),
                        delivery.getDeadline()
                );

                if (distanceToSource < minDistance) {
                    minDistance = distanceToSource;
                    bestVehicle = vehicle;
                }
            }
        }
        return bestVehicle;
    }

    /**
     * Resets all vehicles for a new scheduling cycle.
     */
    public void resetVehicles() {
        for (Vehicle vehicle : vehicles) {
            vehicle.reset();
        }
    }
}
