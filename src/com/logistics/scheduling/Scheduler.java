package com.logistics.scheduling;

import com.logistics.routing.RoutePlanner;
import com.logistics.routing.RoutePlanner.PathResult;

import java.util.List;

public class Scheduler {
    private final List<Vehicle> vehicles;
    private final RoutePlanner routePlanner;

    public Scheduler(List<Vehicle> vehicles, RoutePlanner routePlanner) {
        this.vehicles = vehicles;
        this.routePlanner = routePlanner;
    }

    /**
     * Schedules deliveries, considering vehicle capacity, location, and delivery deadlines.
     *
     * @param deliveries List of delivery requests to schedule.
     */
    public void scheduleDeliveries(List<DeliveryRequest> deliveries) {
        for (DeliveryRequest delivery : deliveries) {
            if (delivery.isDelivered()) {
                System.out.printf("Delivery from %s to %s is already completed.%n",
                        delivery.getSource(), delivery.getDestination());
                continue;
            }

            // Find an available vehicle for the delivery
            Vehicle assignedVehicle = findAvailableVehicle(delivery.getPackageCount(), delivery.getSource());

            if (assignedVehicle != null) {
                // Find the best path for the delivery
                PathResult pathResult = routePlanner.findBestPath(
                        delivery.getSource(),
                        delivery.getDestination(),
                        "minimal_time", // Optimize for minimal delivery time
                        assignedVehicle.getCapacity(),
                        delivery.getDeadline()
                );

                // Check if a valid path exists and the deadline is met
                if (!pathResult.getPath().isEmpty() && pathResult.getTotalTime() <= delivery.getDeadline() * 60) {
                    // Assign the delivery to the vehicle
                    assignedVehicle.addDelivery(delivery);
                    delivery.setVehicleId(assignedVehicle.getId());
                    delivery.markAsDelivered(); // Mark delivery as completed

                    // Print delivery details
                    printDeliveryDetails(assignedVehicle, delivery, pathResult);
                } else {
                    System.err.printf("No valid path found or deadline exceeded for delivery from %s to %s%n",
                            delivery.getSource(), delivery.getDestination());
                }
            } else {
                System.err.printf("No available vehicle for delivery from %s to %s%n",
                        delivery.getSource(), delivery.getDestination());
            }
        }
    }

    /**
     * Finds an available vehicle that meets the capacity and location requirements.
     *
     * @param packageCount Number of packages for the delivery.
     * @param source       Source location of the delivery.
     * @return The available vehicle, or null if none is found.
     */
    private Vehicle findAvailableVehicle(int packageCount, String source) {
        for (Vehicle vehicle : vehicles) {
            // Calculate the available capacity of the vehicle
            int availableCapacity = vehicle.getCapacity() - vehicle.getDeliveries().stream()
                    .mapToInt(DeliveryRequest::getPackageCount)
                    .sum();

            // Check if the vehicle can accommodate the delivery and is at the source location
            if (availableCapacity >= packageCount && vehicle.getCurrentLocation().equals(source)) {
                return vehicle;
            }
        }
        return null;
    }

    /**
     * Prints the details of a scheduled delivery.
     *
     * @param vehicle     The vehicle assigned to the delivery.
     * @param delivery    The delivery request.
     * @param pathResult  The result of the pathfinding operation.
     */
    private void printDeliveryDetails(Vehicle vehicle, DeliveryRequest delivery, PathResult pathResult) {
        System.out.printf("Delivery scheduled:%n");
        System.out.printf("Vehicle: %s%n", vehicle.getId());
        System.out.printf("Path: %s%n", pathResult.getPath());
        System.out.printf("Total Distance: %.2f km%n", pathResult.getTotalDistance());
        System.out.printf("Total Time: %.2f mins%n", pathResult.getTotalTime());
        System.out.printf("Average Congestion: %s%n", pathResult.getAverageCongestionLevel());
        System.out.printf("Remaining Capacity: %d%n", vehicle.getAvailableCapacity());
        System.out.printf("Delivery Status: %s%n", delivery.getStatus());
        System.out.println("----------------------------------------");
    }
}