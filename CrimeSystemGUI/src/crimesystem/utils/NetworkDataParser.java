/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.utils;

/**
 *
 * @author DELL
 */


import java.util.*;

/**
 * NetworkDataParser - Utility class for parsing and analyzing network data
 */
public class NetworkDataParser {
    
    /**
     * Parse crime record line from initial_crimes.txt
     * Format: ADD|CrimeID|Type|Location|Date|Severity|Suspect|Description
     */
    public static Map<String, String> parseCrimeRecord(String line) {
        Map<String, String> record = new HashMap<>();
        
        if (line == null || line.trim().isEmpty()) {
            return record;
        }
        
        String[] parts = line.split("\\|");
        
        if (parts.length >= 7 && parts[0].equals("ADD")) {
            record.put("command", parts[0].trim());
            record.put("crimeId", parts[1].trim());
            record.put("type", parts[2].trim());
            record.put("location", parts[3].trim());
            record.put("date", parts[4].trim());
            record.put("severity", parts[5].trim());
            record.put("suspect", parts[6].trim());
            record.put("description", parts.length > 7 ? parts[7].trim() : "");
        }
        
        return record;
    }
    
    /**
     * Convert crime record to network node format
     */
    public static String convertToNetworkNode(Map<String, String> record, String nodeType) {
        StringBuilder sb = new StringBuilder();
        
        String crimeId = record.get("crimeId");
        String suspect = record.get("suspect");
        String type = record.get("type");
        
        // Create node for crime
        sb.append("ADD_NODE|").append(crimeId).append("|CRIME|")
          .append(type).append("\n");
        
        // Create node for suspect if not "Unknown"
        if (suspect != null && !suspect.equalsIgnoreCase("Unknown")) {
            String suspectId = "SUSP_" + suspect.replaceAll("\\s+", "_");
            sb.append("ADD_NODE|").append(suspectId).append("|SUSPECT|")
              .append(suspect).append("\n");
            
            // Create edge between crime and suspect
            sb.append("ADD_EDGE|").append(crimeId).append("|")
              .append(suspectId).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Extract connections from network output
     */
    public static Map<String, Set<String>> parseNetworkOutput(String output) {
        Map<String, Set<String>> connections = new HashMap<>();
        
        if (output == null || output.trim().isEmpty()) {
            return connections;
        }
        
        String[] lines = output.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // Parse format: [NodeID] → Node1, Node2, Node3
            if (line.startsWith("[") && line.contains("→")) {
                String[] parts = line.split("→");
                
                if (parts.length == 2) {
                    String nodeId = parts[0].trim()
                                           .replace("[", "")
                                           .replace("]", "");
                    
                    String[] connectedNodes = parts[1].split(",");
                    Set<String> nodeConnections = new HashSet<>();
                    
                    for (String node : connectedNodes) {
                        String trimmed = node.trim();
                        if (!trimmed.isEmpty() && !trimmed.equals("(No connections)")) {
                            nodeConnections.add(trimmed);
                        }
                    }
                    
                    if (!nodeConnections.isEmpty()) {
                        connections.put(nodeId, nodeConnections);
                    }
                }
            }
        }
        
        return connections;
    }
    
    /**
     * Extract path from BFS result
     */
    public static List<String> extractPath(String result) {
        List<String> path = new ArrayList<>();
        
        if (result == null || result.trim().isEmpty()) {
            return path;
        }
        
        // Look for FOUND| format
        if (result.contains("FOUND|")) {
            String[] lines = result.split("\n");
            for (String line : lines) {
                if (line.startsWith("FOUND|")) {
                    String pathStr = line.substring(6).trim();
                    String[] nodes = pathStr.split("->");
                    
                    for (String node : nodes) {
                        path.add(node.trim());
                    }
                    break;
                }
            }
        }
        // Look for -> format in any line
        else if (result.contains("->")) {
            String[] lines = result.split("\n");
            for (String line : lines) {
                if (line.contains("->") && !line.contains("No")) {
                    String[] nodes = line.split("->");
                    
                    for (String node : nodes) {
                        String cleaned = node.trim()
                                            .replaceAll("[^a-zA-Z0-9_]", "");
                        if (!cleaned.isEmpty()) {
                            path.add(cleaned);
                        }
                    }
                    
                    if (!path.isEmpty()) break;
                }
            }
        }
        
        return path;
    }
    
    /**
     * Calculate network metrics
     */
    public static NetworkMetrics calculateMetrics(Map<String, Set<String>> graph) {
        NetworkMetrics metrics = new NetworkMetrics();
        
        metrics.totalNodes = graph.size();
        
        int totalEdges = 0;
        int maxConnections = 0;
        int minConnections = Integer.MAX_VALUE;
        int totalConnections = 0;
        
        for (Set<String> connections : graph.values()) {
            int connCount = connections.size();
            totalConnections += connCount;
            totalEdges += connCount;
            
            if (connCount > maxConnections) {
                maxConnections = connCount;
            }
            
            if (connCount < minConnections && connCount > 0) {
                minConnections = connCount;
            }
        }
        
        metrics.totalEdges = totalEdges / 2; // Undirected graph
        metrics.maxConnections = maxConnections;
        metrics.minConnections = (minConnections == Integer.MAX_VALUE) ? 0 : minConnections;
        metrics.avgConnections = metrics.totalNodes > 0 ? 
            (double) totalConnections / metrics.totalNodes : 0.0;
        
        // Calculate density: actual edges / possible edges
        if (metrics.totalNodes > 1) {
            int possibleEdges = (metrics.totalNodes * (metrics.totalNodes - 1)) / 2;
            metrics.density = (double) metrics.totalEdges / possibleEdges;
        }
        
        return metrics;
    }
    
    /**
     * Find highly connected nodes (hubs)
     */
    public static List<String> findHubs(Map<String, Set<String>> graph, int minConnections) {
        List<String> hubs = new ArrayList<>();
        
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            if (entry.getValue().size() >= minConnections) {
                hubs.add(entry.getKey());
            }
        }
        
        // Sort by connection count (descending)
        hubs.sort((a, b) -> 
            Integer.compare(graph.get(b).size(), graph.get(a).size()));
        
        return hubs;
    }
    
    /**
     * Find isolated nodes (no connections)
     */
    public static List<String> findIsolatedNodes(Map<String, Set<String>> graph) {
        List<String> isolated = new ArrayList<>();
        
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            if (entry.getValue().isEmpty()) {
                isolated.add(entry.getKey());
            }
        }
        
        return isolated;
    }
    
    /**
     * Network metrics data class
     */
    public static class NetworkMetrics {
        public int totalNodes;
        public int totalEdges;
        public int maxConnections;
        public int minConnections;
        public double avgConnections;
        public double density;
        
        @Override
        public String toString() {
            return String.format(
                "Nodes: %d | Edges: %d | Density: %.2f%% | Avg Connections: %.2f",
                totalNodes, totalEdges, density * 100, avgConnections
            );
        }
    }
}
