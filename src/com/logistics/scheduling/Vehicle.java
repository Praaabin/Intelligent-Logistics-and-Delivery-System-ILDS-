package com.logistics.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle {
    private final String id;
    private final int capacity; // Maximum capacity of the vehicle
    private int usedCapacity; // Tracks the current capacity utilization
    private String currentLocation; // Current location of the vehicle
    private final List<DeliveryRequest> deliveries; // List of assigned deliveries

    public Vehicle(String id, int capacity, String currentLocation) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative.");
        }
        this.id = id;
        this.capacity = capacity;
        this.usedCapacity = 0; // Initial capacity utilization
        this.currentLocation = currentLocation;
        this.deliveries = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableCapacity() {
        return capacity - usedCapacity;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Updates the vehicle's current location.
     *
     * @param newLocation The new location of the vehicle.
     */
    public void updateLocation(String newLocation) {
        this.currentLocation = newLocation;
    }

    /**
     * Returns an unmodifiable list of deliveries.
     *
     * @return List of assigned deliveries.
     */
    public List<DeliveryRequest> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }

    /**
     * Checks if the vehicle has enough capacity for a given package.
     *
     * @param packageSize The size of the package to check.
     * @return True if the vehicle has enough available capacity, false otherwise.
     */
    public boolean canAccommodate(int packageSize) {
        return getAvailableCapacity() >= packageSize;
    }

    /**
     * Adds a delivery to the vehicle's delivery list and updates its location.
     *
     * @param delivery The delivery request to add.
     * @throws IllegalStateException if the vehicle does not have enough capacity.
     */
    public void addDelivery(DeliveryRequest delivery) {
        if (!canAccommodate(delivery.getPackageCount())) {
            throw new IllegalStateException("Not enough capacity to accommodate the delivery.");
        }
        deliveries.add(delivery);
        usedCapacity += delivery.getPackageCount();
        currentLocation = delivery.getDestination(); // Update current location to delivery destination
    }

    /**
     * Updates the vehicle's status after completing a delivery.
     *
     * @param delivery The completed delivery.
     */
    public void completeDelivery(DeliveryRequest delivery) {
        if (deliveries.remove(delivery)) {
            usedCapacity -= delivery.getPackageCount();
            if (usedCapacity < 0) {
                usedCapacity = 0; // Ensure used capacity doesn't go negative
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Vehicle{id='%s', capacity=%d, usedCapacity=%d, currentLocation='%s', deliveries=%d}",
                id, capacity, usedCapacity, currentLocation, deliveries.size()
        );
    }
}