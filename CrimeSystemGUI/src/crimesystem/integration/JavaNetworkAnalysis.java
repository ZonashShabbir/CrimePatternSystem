package crimesystem.integration;

import java.util.*;
import crimesystem.models.CriminalNetwork;

/**
 * Complete Network Analysis in Java
 * Implements Kruskal's MST and other algorithms
 */
public class JavaNetworkAnalysis {
    
    // Edge class for MST
    static class Edge implements Comparable<Edge> {
        String criminal1;
        String criminal2;
        int weight;
        
        Edge(String c1, String c2, int w) {
            criminal1 = c1;
            criminal2 = c2;
            weight = w;
        }
        
        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }
    
    // Union-Find for Kruskal's
    static class UnionFind {
        Map<String, String> parent;
        Map<String, Integer> rank;
        
        UnionFind(Set<String> criminals) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for(String c : criminals) {
                parent.put(c, c);
                rank.put(c, 0);
            }
        }
        
        String find(String x) {
            if(!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }
        
        boolean union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);
            
            if(rootX.equals(rootY)) return false;
            
            if(rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if(rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
            return true;
        }
    }
    
    public static String runCompleteAnalysis(List<CriminalNetwork> networks) {
        if(networks == null || networks.isEmpty()) {
            return "ERROR: No network data available!";
        }
        
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("═══════════════════════════════════════════════════\n");
        report.append("  CRIMINAL NETWORK INTELLIGENCE REPORT\n");
        report.append("═══════════════════════════════════════════════════\n\n");
        
        // Get unique criminals
        Set<String> criminalsSet = new LinkedHashSet<>();
        for(CriminalNetwork net : networks) {
            criminalsSet.add(net.getCriminal1());
            criminalsSet.add(net.getCriminal2());
        }
        
        report.append("Total Criminals Identified: ").append(criminalsSet.size()).append("\n");
        report.append("Total Connections Found: ").append(networks.size()).append("\n\n");
        
        // Kruskal's Algorithm
        report.append("=== KRUSKAL'S ALGORITHM (Minimum Spanning Tree) ===\n");
        report.append("Critical Criminal Connections:\n\n");
        
        List<Edge> edges = new ArrayList<>();
        for(CriminalNetwork net : networks) {
            edges.add(new Edge(net.getCriminal1(), net.getCriminal2(), 
                             net.getConnectionStrength()));
        }
        
        // Sort by weight (descending for strongest connections)
        edges.sort((a, b) -> Integer.compare(b.weight, a.weight));
        
        UnionFind uf = new UnionFind(criminalsSet);
        List<Edge> mst = new ArrayList<>();
        
        for(Edge edge : edges) {
            if(uf.union(edge.criminal1, edge.criminal2)) {
                mst.add(edge);
            }
        }
        
        // Show ALL MST connections
        report.append(String.format("Displaying all %d critical connections:\n\n", mst.size()));
        
        for(Edge edge : mst) {
            report.append(String.format("%-20s <--> %-20s (Strength: %2d joint crimes)\n",
                edge.criminal1, edge.criminal2, edge.weight));
        }
        
        report.append("\nTotal MST Edges: ").append(mst.size()).append("\n\n");
        
        // Network Statistics
        report.append("=== NETWORK STATISTICS ===\n");
        
        // Calculate degree centrality
        Map<String, Integer> degree = new HashMap<>();
        for(CriminalNetwork net : networks) {
            degree.put(net.getCriminal1(), 
                      degree.getOrDefault(net.getCriminal1(), 0) + 1);
            degree.put(net.getCriminal2(), 
                      degree.getOrDefault(net.getCriminal2(), 0) + 1);
        }
        
        // Most connected criminals
        report.append("\nMost Connected Criminals (Hubs):\n");
        List<Map.Entry<String, Integer>> sortedDegree = new ArrayList<>(degree.entrySet());
        sortedDegree.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        for(int i = 0; i < Math.min(10, sortedDegree.size()); i++) {
            Map.Entry<String, Integer> entry = sortedDegree.get(i);
            report.append(String.format("%-20s : %3d connections\n", 
                entry.getKey(), entry.getValue()));
        }
        
        // Connection strength analysis
        report.append("\n=== CONNECTION STRENGTH ANALYSIS ===\n");
        int strong = 0, medium = 0, weak = 0;
        
        for(CriminalNetwork net : networks) {
            int strength = net.getConnectionStrength();
            if(strength >= 10) strong++;
            else if(strength >= 6) medium++;
            else weak++;
        }
        
        report.append(String.format("Strong Connections (≥10):   %4d (%.1f%%)\n", 
            strong, 100.0 * strong / networks.size()));
        report.append(String.format("Medium Connections (6-9):   %4d (%.1f%%)\n", 
            medium, 100.0 * medium / networks.size()));
        report.append(String.format("Weak Connections (<6):      %4d (%.1f%%)\n", 
            weak, 100.0 * weak / networks.size()));
        
        // Average connection strength
        double avgStrength = networks.stream()
            .mapToInt(CriminalNetwork::getConnectionStrength)
            .average().orElse(0.0);
        report.append(String.format("\nAverage Connection Strength: %.2f\n", avgStrength));
        
        // Network density
        int maxPossible = criminalsSet.size() * (criminalsSet.size() - 1) / 2;
        double density = 100.0 * networks.size() / maxPossible;
        report.append(String.format("Network Density: %.2f%%\n", density));
        
        // Strongest connections
        report.append("\n=== TOP 10 STRONGEST CONNECTIONS ===\n");
        List<CriminalNetwork> sortedNetworks = new ArrayList<>(networks);
        sortedNetworks.sort((a, b) -> 
            Integer.compare(b.getConnectionStrength(), a.getConnectionStrength()));
        
        for(int i = 0; i < Math.min(10, sortedNetworks.size()); i++) {
            CriminalNetwork net = sortedNetworks.get(i);
            report.append(String.format("%2d. %-18s <--> %-18s : %2d crimes\n",
                i+1, net.getCriminal1(), net.getCriminal2(), 
                net.getConnectionStrength()));
        }
        
        // Key findings
        report.append("\n=== KEY FINDINGS ===\n");
        report.append("✓ Network Analysis Complete\n");
        report.append("✓ MST computed using Kruskal's Algorithm\n");
        report.append("✓ Hub criminals identified\n");
        report.append("✓ Connection patterns analyzed\n");
        
        report.append("\n═══════════════════════════════════════════════════\n");
        report.append("  Analysis generated using Java implementation\n");
        report.append("═══════════════════════════════════════════════════\n");
        
        return report.toString();
    }
}