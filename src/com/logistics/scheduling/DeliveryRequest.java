package com.logistics.scheduling;

import java.util.Objects;

public class DeliveryRequest {
    private final String source;
    private final String destination;
    private final int packageCount;
    private final int urgency; // Higher value = higher urgency
    private final double deadline; // Latest time for delivery in hours
    private boolean delivered; // Tracks whether the delivery is completed
    private String vehicleId; // Tracks the assigned vehicle ID (if any)

    // Constructor with deadline
    public DeliveryRequest(String source, String destination, int packageCount, int urgency, double deadline) {
        if (source == null || source.isEmpty() || destination == null || destination.isEmpty()) {
            throw new IllegalArgumentException("Source and destination must not be null or empty.");
        }
        if (packageCount < 0) {
            throw new IllegalArgumentException("Package count must be non-negative.");
        }
        if (urgency < 1 || urgency > 5) {
            throw new IllegalArgumentException("Urgency must be between 1 and 5.");
        }
        if (deadline < 0) {
            throw new IllegalArgumentException("Deadline must be non-negative.");
        }
        this.source = source;
        this.destination = destination;
        this.packageCount = packageCount;
        this.urgency = urgency;
        this.deadline = deadline;
        this.delivered = false; // Default to not delivered
        this.vehicleId = null; // No vehicle assigned initially
    }

    // Constructor without deadline (default value)
    public DeliveryRequest(String source, String destination, int packageCount, int urgency) {
        this(source, destination, packageCount, urgency, Double.MAX_VALUE); // Default deadline as infinity
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public int getUrgency() {
        return urgency;
    }

    public double getDeadline() {
        return deadline;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void markAsDelivered() {
        this.delivered = true;
    }

    /**
     * Provides a detailed delivery status for real-time tracking.
     *
     * @return A descriptive string for the delivery status.
     */
    public String getStatus() {
        if (delivered) {
            return "Delivered";
        }
        return vehicleId == null ? "Pending Assignment" : "In Transit";
    }

    @Override
    public String toString() {
        return String.format(
                "DeliveryRequest{source='%s', destination='%s', packageCount=%d, urgency=%d, deadline=%.2f, status='%s', vehicleId='%s'}",
                source, destination, packageCount, urgency, deadline, getStatus(), vehicleId != null ? vehicleId : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryRequest that = (DeliveryRequest) o;
        return packageCount == that.packageCount &&
                urgency == that.urgency &&
                Double.compare(that.deadline, deadline) == 0 &&
                delivered == that.delivered &&
                Objects.equals(source, that.source) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(vehicleId, that.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, packageCount, urgency, deadline, delivered, vehicleId);
    }
}