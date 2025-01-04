package com.logistics.scheduling;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private final String id;       // Unique vehicle ID
    private final int capacity;    // Maximum number of packages the vehicle can hold
    private int currentLoad;       // Current number of packages assigned to the vehicle
    private String currentLocation; // Current location of the vehicle
    private double totalDistanceTraveled; // Total distance covered by the vehicle
    private final List<DeliveryRequest> deliveries; // Assigned deliveries

    public Vehicle(String id, int capacity, String initialLocation) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1.");
        }
        if (initialLocation == null || initialLocation.isEmpty()) {
            throw new IllegalArgumentException("Initial location must be specified.");
        }
        this.id = id;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.currentLocation = initialLocation;
        this.totalDistanceTraveled = 0;
        this.deliveries = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    public List<DeliveryRequest> getDeliveries() {
        return deliveries;
    }

    public void setCurrentLocation(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty.");
        }
        this.currentLocation = location;
    }

    public void addDistance(double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative.");
        }
        this.totalDistanceTraveled += distance;
    }

    public boolean canAccommodate(int packageCount) {
        return (currentLoad + packageCount) <= capacity;
    }

    public void loadPackages(int packageCount) {
        if (canAccommodate(packageCount)) {
            this.currentLoad += packageCount;
        } else {
            throw new IllegalArgumentException("Cannot load packages. Capacity exceeded.");
        }
    }

    public void unloadAllPackages() {
        this.currentLoad = 0;
        this.deliveries.clear();
    }

    public void assignDelivery(DeliveryRequest deliveryRequest) {
        if (canAccommodate(deliveryRequest.getPackageCount())) {
            this.deliveries.add(deliveryRequest);
            loadPackages(deliveryRequest.getPackageCount());
        } else {
            throw new IllegalArgumentException("Cannot assign delivery. Capacity exceeded.");
        }
    }

    /**
     * Resets the vehicle's state (e.g., for a new scheduling cycle).
     */
    public void reset() {
        this.currentLoad = 0;
        this.totalDistanceTraveled = 0;
        this.deliveries.clear();
    }

    public int getAvailableCapacity() {
        return capacity - currentLoad;
    }
}
