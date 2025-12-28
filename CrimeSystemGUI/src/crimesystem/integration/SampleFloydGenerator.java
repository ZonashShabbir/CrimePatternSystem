package crimesystem.integration;

import java.io.*;
import java.util.*;
import crimesystem.models.CriminalNetwork;

/**
 * Generates a sample Floyd Warshall results file
 * Use this when C++ program is not available
 */
public class SampleFloydGenerator {
    
    public static boolean generateSampleFile(List<CriminalNetwork> networks) {
        if(networks == null || networks.isEmpty()) {
            System.err.println("No network data to generate Floyd Warshall results!");
            return false;
        }
        
        try {
            // Extract unique criminals
            Set<String> criminalSet = new LinkedHashSet<>();
            for(CriminalNetwork net : networks) {
                criminalSet.add(net.getCriminal1());
                criminalSet.add(net.getCriminal2());
            }
            
            String[] criminals = criminalSet.toArray(new String[0]);
            int n = criminals.length;
            
            System.out.println("Generating Floyd Warshall for " + n + " criminals...");
            
            // Create adjacency matrix
            int[][] dist = new int[n][n];
            
            // Initialize with infinity
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(i == j) {
                        dist[i][j] = 0;
                    } else {
                        dist[i][j] = 9999; // Use large number instead of MAX_VALUE
                    }
                }
            }
            
            // Map criminal names to indices
            Map<String, Integer> criminalIndex = new HashMap<>();
            for(int i = 0; i < criminals.length; i++) {
                criminalIndex.put(criminals[i], i);
            }
            
            // Fill in direct connections
            for(CriminalNetwork net : networks) {
                int i = criminalIndex.get(net.getCriminal1());
                int j = criminalIndex.get(net.getCriminal2());
                int weight = net.getConnectionStrength();
                
                dist[i][j] = weight;
                dist[j][i] = weight; // Undirected graph
            }
            
            // Floyd Warshall Algorithm
            for(int k = 0; k < n; k++) {
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        if(dist[i][k] != 9999 && dist[k][j] != 9999) {
                            if(dist[i][j] > dist[i][k] + dist[k][j]) {
                                dist[i][j] = dist[i][k] + dist[k][j];
                            }
                        }
                    }
                }
            }
            
            // Write to file
            PrintWriter writer = new PrintWriter(new FileWriter("floyd_warshall_results.txt"));
            
            writer.println("FLOYD WARSHALL ALGORITHM (All Shortest Paths)");
            writer.println("Shortest Connection Paths Between All Criminals:");
            writer.println();
            
            // Header row
            writer.print("From/To");
            for(int i = 0; i < n; i++) {
                writer.print("\t" + criminals[i]);
            }
            writer.println();
            
            // Data rows
            for(int i = 0; i < n; i++) {
                writer.print(criminals[i]);
                for(int j = 0; j < n; j++) {
                    if(dist[i][j] >= 9999) {
                        writer.print("\t∞");
                    } else {
                        writer.print("\t" + dist[i][j]);
                    }
                }
                writer.println();
            }
            
            writer.println();
            writer.println("Key Findings:");
            
            // Calculate max distance for each criminal
            for(int i = 0; i < n; i++) {
                int maxDist = 0;
                for(int j = 0; j < n; j++) {
                    if(dist[i][j] < 9999 && dist[i][j] > maxDist) {
                        maxDist = dist[i][j];
                    }
                }
                writer.println(criminals[i] + " - Max distance to any criminal: " + maxDist);
            }
            
            writer.close();
            
            System.out.println("✓ Floyd Warshall results saved to: floyd_warshall_results.txt");
            System.out.println("✓ Total criminals: " + n);
            
            return true;
            
        } catch(IOException e) {
            System.err.println("ERROR: Could not write Floyd Warshall results!");
            e.printStackTrace();
            return false;
        }
    }
}