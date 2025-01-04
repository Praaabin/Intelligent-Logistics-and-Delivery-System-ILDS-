package com.logistics.scheduling;

public class DeliveryRequest {
    private final String source;
    private final String destination;
    private final int packageCount;
    private final int urgency; // Higher value = higher urgency
    private final double deadline; // Optional: latest time for delivery

    // Constructor with deadline
    public DeliveryRequest(String source, String destination, int packageCount, int urgency, double deadline) {
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
}
