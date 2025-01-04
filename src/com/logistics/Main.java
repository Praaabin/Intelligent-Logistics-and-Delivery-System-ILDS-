package com.logistics;

import com.logistics.graph.Graph;
import com.logistics.routing.RoutePlanner;
import com.logistics.scheduling.DeliveryRequest;
import com.logistics.scheduling.Scheduler;
import com.logistics.scheduling.Vehicle;
import com.logistics.congestion.CongestionPredictor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph();
        List<Vehicle> vehicles = new ArrayList<>();

        System.out.println("=== Intelligent Logistics and Delivery System ===");

        // Main program loop
        while (true) {
            printMenu();
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1 -> initializeGraph(scanner, graph);
                case 2 -> initializeVehicles(scanner, vehicles);
                case 3 -> simulateTraffic(graph);
                case 4 -> scheduleDeliveries(scanner, graph, vehicles);
                case 5 -> displayCityNetwork(graph);
                case 6 -> findBestPath(scanner, graph);
                case 7 -> manageNodes(scanner, graph);
                case 8 -> manageEdges(scanner, graph);
                case 9 -> System.exit(0);
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Input City Network");
        System.out.println("2. Initialize Vehicles");
        System.out.println("3. Simulate Traffic");
        System.out.println("4. Schedule Deliveries");
        System.out.println("5. View City Network");
        System.out.println("6. Find Best Path");
        System.out.println("7. Manage Nodes (Add/Remove)");
        System.out.println("8. Manage Edges (Add/Remove/Update)");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void initializeGraph(Scanner scanner, Graph graph) {
        System.out.println("Enter the number of nodes (hubs/locations): ");
        int nodeCount = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < nodeCount; i++) {
            System.out.print("Enter node ID: ");
            String nodeId = scanner.nextLine();
            graph.addNode(nodeId);
        }

        System.out.println("Enter the number of edges (roads): ");
        int edgeCount = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < edgeCount; i++) {
            addEdge(scanner, graph);
        }

        System.out.println("City network initialized successfully.");
    }

    private static void addEdge(Scanner scanner, Graph graph) {
        System.out.print("Enter source node ID: ");
        String fromNode = scanner.nextLine();
        System.out.print("Enter destination node ID: ");
        String toNode = scanner.nextLine();

        if (!graph.hasNode(fromNode) || !graph.hasNode(toNode)) {
            System.out.println("Error: One or both nodes do not exist.");
            return;
        }

        System.out.print("Enter distance (km): ");
        double distance = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter time (minutes): ");
        double time = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter congestion level: ");
        double congestion = Double.parseDouble(scanner.nextLine());

        graph.addEdge(fromNode, toNode, distance, time, congestion);
    }

    private static void initializeVehicles(Scanner scanner, List<Vehicle> vehicles) {
        System.out.println("Enter the number of vehicles: ");
        int vehicleCount = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < vehicleCount; i++) {
            System.out.print("Enter vehicle ID: ");
            String vehicleId = scanner.nextLine();
            System.out.print("Enter vehicle capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter initial location: ");
            String initialLocation = scanner.nextLine();

            vehicles.add(new Vehicle(vehicleId, capacity, initialLocation));
        }

        System.out.println("Vehicles initialized successfully.");
    }

    private static void simulateTraffic(Graph graph) {
        CongestionPredictor congestionPredictor = new CongestionPredictor(graph);
        congestionPredictor.predictAndAdapt();
        System.out.println("Traffic simulation completed. Routes updated based on congestion.");
    }

    private static void scheduleDeliveries(Scanner scanner, Graph graph, List<Vehicle> vehicles) {
        System.out.println("Enter the number of deliveries: ");
        int deliveryCount = Integer.parseInt(scanner.nextLine());
        List<DeliveryRequest> deliveries = new ArrayList<>();

        for (int i = 0; i < deliveryCount; i++) {
            System.out.print("Enter source location: ");
            String source = scanner.nextLine();
            System.out.print("Enter destination location: ");
            String destination = scanner.nextLine();
            System.out.print("Enter package count: ");
            int packageCount = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter urgency (1-5): ");
            int urgency = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter delivery deadline (hours): ");
            double deadline = Double.parseDouble(scanner.nextLine());

            deliveries.add(new DeliveryRequest(source, destination, packageCount, urgency, deadline));
        }

        Scheduler scheduler = new Scheduler(vehicles, new RoutePlanner(graph));
        scheduler.scheduleDeliveries(deliveries);
    }

    private static void displayCityNetwork(Graph graph) {
        System.out.println("City Network:");
        graph.getAdjacencyList().forEach((node, edges) -> {
            System.out.println("* " + node.getId());
            edges.forEach((neighbor, edge) -> {
                System.out.printf("  └── Connected to: %s (%.2f km, Time: %.2f minutes, Congestion: %.2f)%n",
                        neighbor.getId(), edge.getDistance(), edge.getTime(), edge.getCongestion());
            });
        });
    }

    private static void findBestPath(Scanner scanner, Graph graph) {
        System.out.print("Enter Start Hub: ");
        String start = scanner.nextLine();
        System.out.print("Enter Destination Hub: ");
        String destination = scanner.nextLine();
        System.out.print("Enter Routing Preference (shortest/minimal_time): ");
        String preference = scanner.nextLine();

        RoutePlanner routePlanner = new RoutePlanner(graph);
        RoutePlanner.PathResult pathResult = routePlanner.findBestPath(start, destination, preference, Integer.MAX_VALUE, Double.MAX_VALUE);

        if (pathResult.getPath().isEmpty()) {
            System.out.println("No path found between the specified hubs.");
        } else {
            System.out.println("Optimal Route: " + pathResult.getPath());
            System.out.printf("Total Distance: %.2f km%n", pathResult.getTotalDistance());
            System.out.printf("Total Time: %.2f minutes%n", pathResult.getTotalTime());
            System.out.printf("Average Congestion: %.2f%n", pathResult.getAverageCongestion());
        }
    }

    private static void manageNodes(Scanner scanner, Graph graph) {
        System.out.println("\n=== Manage Nodes ===");
        System.out.println("1. Add Node");
        System.out.println("2. Remove Node");
        System.out.print("Enter your choice: ");
        int choice = getUserChoice(scanner);

        switch (choice) {
            case 1 -> {
                System.out.print("Enter Node ID to add: ");
                String nodeId = scanner.nextLine();
                graph.addNode(nodeId);
                System.out.println("Node added successfully.");
            }
            case 2 -> {
                System.out.print("Enter Node ID to remove: ");
                String nodeId = scanner.nextLine();
                graph.removeNode(nodeId);
                System.out.println("Node removed successfully.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void manageEdges(Scanner scanner, Graph graph) {
        System.out.println("\n=== Manage Edges ===");
        System.out.println("1. Add Edge");
        System.out.println("2. Remove Edge");
        System.out.println("3. Update Edge Attributes");
        System.out.print("Enter your choice: ");
        int choice = getUserChoice(scanner);

        switch (choice) {
            case 1 -> addEdge(scanner, graph);
            case 2 -> {
                System.out.print("Enter Source Node ID: ");
                String fromId = scanner.nextLine();
                System.out.print("Enter Destination Node ID: ");
                String toId = scanner.nextLine();

                graph.removeEdge(fromId, toId);
                System.out.println("Edge removed successfully.");
            }
            case 3 -> {
                System.out.print("Enter Source Node ID: ");
                String fromId = scanner.nextLine();
                System.out.print("Enter Destination Node ID: ");
                String toId = scanner.nextLine();

                System.out.print("Enter New Distance (km): ");
                double distance = Double.parseDouble(scanner.nextLine());
                System.out.print("Enter New Time (minutes): ");
                double time = Double.parseDouble(scanner.nextLine());
                System.out.print("Enter New Congestion Level: ");
                double congestion = Double.parseDouble(scanner.nextLine());

                if (graph.updateEdge(fromId, toId, distance, time, congestion)) {
                    System.out.println("Edge updated successfully.");
                } else {
                    System.out.println("Edge does not exist.");
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }
}
