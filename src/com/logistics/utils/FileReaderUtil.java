package com.logistics.utils;

import com.logistics.graph.Graph;
import com.logistics.utils.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderUtil {

    /**
     * Loads graph data (nodes and edges) from a file and adds it to the graph.
     * File format:
     * - Nodes: "NODE <nodeId>"
     * - Edges: "EDGE <fromNodeId> <toNodeId> <distance> <time> <congestion>"
     *
     * @param filePath The path to the graph data file.
     * @param graph    The graph to populate.
     * @throws IOException If the file cannot be read.
     */
    public static void loadGraphFromFile(String filePath, Graph graph) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Logger.info("Loading graph from file: " + filePath);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                try {
                    if (parts[0].equalsIgnoreCase("NODE")) {
                        // Add a node to the graph
                        graph.addNode(parts[1]);
                        Logger.info("Added node: " + parts[1]);

                    } else if (parts[0].equalsIgnoreCase("EDGE")) {
                        // Add an edge to the graph
                        String fromNodeId = parts[1];
                        String toNodeId = parts[2];
                        double distance = Double.parseDouble(parts[3]);
                        double time = Double.parseDouble(parts[4]);
                        double congestion = Double.parseDouble(parts[5]);

                        // Validate edge parameters
                        if (distance < 0 || time < 0 || congestion < 0) {
                            Logger.warn("Invalid edge parameters. Skipping line: " + line);
                            continue;
                        }

                        graph.addEdge(fromNodeId, toNodeId, distance, time, congestion);
                        Logger.info(String.format("Added edge from %s to %s with distance=%.2f, time=%.2f, congestion=%.2f",
                                fromNodeId, toNodeId, distance, time, congestion));
                    } else {
                        Logger.warn("Unknown line type. Skipping line: " + line);
                    }
                } catch (Exception e) {
                    Logger.error("Error processing line: " + line + ". Error: " + e.getMessage());
                }
            }

            Logger.info("Graph loading completed successfully.");
        } catch (IOException e) {
            Logger.error("Failed to load graph file: " + e.getMessage());
            throw e;
        }
    }
}
