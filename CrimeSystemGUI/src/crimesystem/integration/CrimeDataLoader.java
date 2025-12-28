/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.integration;

/**
 *
 * @author DELL
 */


import java.io.*;
import java.util.*;

/**
 * CrimeDataLoader - Loads criminal records from initial_crimes.txt
 * and builds network graph automatically
 */
public class CrimeDataLoader {
    
    private static final String DATA_PATH = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    private static final String CRIMES_FILE = "initial_crimes.txt";
    
    /**
     * Load criminal records from initial_crimes.txt
     * Format: ADD|CrimeID|Type|Location|Date|Severity|Suspect|Description
     * @return Map of CrimeID -> [Type, Location, Date, Severity, Suspect, Description]
     */
    public static Map<String, List<String>> loadCriminalRecords() {
        Map<String, List<String>> crimeData = new HashMap<>();
        
        try {
            File file = new File(DATA_PATH + CRIMES_FILE);
            
            if (!file.exists()) {
                System.err.println("ERROR: File not found - " + file.getAbsolutePath());
                return crimeData;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                try {
                    // Parse line: ADD|CR0079|Fraud|Islamabad F-8|29/10/2023|6|Salman Hassan|Description
                    String[] parts = line.split("\\|");
                    
                    if (parts.length >= 7 && parts[0].equals("ADD")) {
                        String crimeId = parts[1].trim();
                        String type = parts[2].trim();
                        String location = parts[3].trim();
                        String date = parts[4].trim();
                        String severity = parts[5].trim();
                        String suspect = parts[6].trim();
                        String description = parts.length > 7 ? parts[7].trim() : "N/A";
                        
                        List<String> record = new ArrayList<>();
                        record.add(type);
                        record.add(location);
                        record.add(date);
                        record.add(severity);
                        record.add(suspect);
                        record.add(description);
                        
                        crimeData.put(crimeId, record);
                        
                        System.out.println("Loaded: " + crimeId + " - " + suspect);
                    } else {
                        System.err.println("Skipping invalid line " + lineNumber + ": " + line);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
            
            reader.close();
            
            System.out.println("\n✓ Successfully loaded " + crimeData.size() + " criminal records");
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return crimeData;
    }
    
    /**
     * Build network graph from crime data
     * Criminals are connected if they:
     * 1. Share same location
     * 2. Same crime type
     * 3. Similar dates (within 7 days)
     * 4. Same suspect appears in multiple crimes
     * 
     * @param crimeData Map of CrimeID -> record details
     * @return Map of CrimeID -> Set of connected CrimeIDs
     */
    public static Map<String, Set<String>> buildNetworkGraph(Map<String, List<String>> crimeData) {
        Map<String, Set<String>> graph = new HashMap<>();
        
        // Initialize graph
        for (String crimeId : crimeData.keySet()) {
            graph.put(crimeId, new HashSet<>());
        }
        
        // Build connections based on multiple criteria
        List<String> crimeIds = new ArrayList<>(crimeData.keySet());
        
        for (int i = 0; i < crimeIds.size(); i++) {
            for (int j = i + 1; j < crimeIds.size(); j++) {
                String id1 = crimeIds.get(i);
                String id2 = crimeIds.get(j);
                
                List<String> record1 = crimeData.get(id1);
                List<String> record2 = crimeData.get(id2);
                
                // Check if crimes are connected
                if (areCrimesConnected(record1, record2)) {
                    graph.get(id1).add(id2);
                    graph.get(id2).add(id1);
                }
            }
        }
        
        System.out.println("\n✓ Network graph built with " + countEdges(graph) + " connections");
        
        return graph;
    }
    
    /**
     * Check if two crimes are connected based on:
     * - Same location
     * - Same crime type
     * - Same suspect name
     * - Similar dates (within 30 days)
     */
    private static boolean areCrimesConnected(List<String> record1, List<String> record2) {
        String type1 = record1.get(0);
        String location1 = record1.get(1);
        String suspect1 = record1.get(4);
        
        String type2 = record2.get(0);
        String location2 = record2.get(1);
        String suspect2 = record2.get(4);
        
        // Connection Rule 1: Same suspect
        if (!suspect1.equalsIgnoreCase("Unknown") && 
            !suspect2.equalsIgnoreCase("Unknown") &&
            suspect1.equalsIgnoreCase(suspect2)) {
            return true;
        }
        
        // Connection Rule 2: Same location AND same crime type
        if (location1.equalsIgnoreCase(location2) && type1.equalsIgnoreCase(type2)) {
            return true;
        }
        
        // Connection Rule 3: Same location (same area indicates possible gang territory)
        String area1 = extractArea(location1);
        String area2 = extractArea(location2);
        if (area1 != null && area1.equalsIgnoreCase(area2)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Extract area name from location
     * e.g., "Islamabad F-8" -> "Islamabad"
     */
    private static String extractArea(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }
        
        String[] parts = location.split(" ");
        return parts.length > 0 ? parts[0] : location;
    }
    
    /**
     * Count total edges in graph (connections / 2 for undirected)
     */
    private static int countEdges(Map<String, Set<String>> graph) {
        int count = 0;
        for (Set<String> connections : graph.values()) {
            count += connections.size();
        }
        return count / 2;
    }
    
    /**
     * Get suspect name for a crime ID
     */
    public static String getSuspectName(String crimeId, Map<String, List<String>> crimeData) {
        List<String> record = crimeData.get(crimeId);
        return record != null ? record.get(4) : "Unknown";
    }
    
    /**
     * Get crime type for a crime ID
     */
    public static String getCrimeType(String crimeId, Map<String, List<String>> crimeData) {
        List<String> record = crimeData.get(crimeId);
        return record != null ? record.get(0) : "Unknown";
    }
    
    /**
     * Get location for a crime ID
     */
    public static String getLocation(String crimeId, Map<String, List<String>> crimeData) {
        List<String> record = crimeData.get(crimeId);
        return record != null ? record.get(1) : "Unknown";
    }
    
    /**
     * Get full details for a crime ID
     */
    public static String getCrimeDetails(String crimeId, Map<String, List<String>> crimeData) {
        List<String> record = crimeData.get(crimeId);
        if (record == null) return "No details available";
        
        StringBuilder details = new StringBuilder();
        details.append("Type: ").append(record.get(0)).append("\n");
        details.append("Location: ").append(record.get(1)).append("\n");
        details.append("Date: ").append(record.get(2)).append("\n");
        details.append("Severity: ").append(record.get(3)).append("/10\n");
        details.append("Suspect: ").append(record.get(4)).append("\n");
        details.append("Description: ").append(record.get(5));
        
        return details.toString();
    }
    /**
     * Generate Criminal Networks from crime data
     * Creates connections between criminals based on:
     * 1. Same suspect name (different crimes)
     * 2. Same location
     * 3. Same crime type
     * 
     * @return List of CriminalNetwork objects
     */
    public static List<crimesystem.models.CriminalNetwork> generateCriminalNetworks() {
        // Load crime data
        Map<String, List<String>> crimeData = loadCriminalRecords();
        List<crimesystem.models.CriminalNetwork> networks = new ArrayList<>();
        
        if (crimeData.isEmpty()) {
            System.err.println("No crime data loaded!");
            return networks;
        }
        
        // Group crimes by suspect
        Map<String, List<String>> criminalCrimes = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : crimeData.entrySet()) {
            String crimeId = entry.getKey();
            String suspect = entry.getValue().get(4); // Suspect is at index 4
            
            if (!suspect.equalsIgnoreCase("Unknown") && !suspect.trim().isEmpty()) {
                criminalCrimes.putIfAbsent(suspect, new ArrayList<>());
                criminalCrimes.get(suspect).add(crimeId);
            }
        }
        
        Set<String> addedConnections = new HashSet<>();
        
        // Rule 1: Connect criminals with SAME NAME (different locations)
        Map<String, List<String>> nameGroups = groupByFirstName(criminalCrimes);
        for (List<String> suspects : nameGroups.values()) {
            if (suspects.size() > 1) {
                for (int i = 0; i < suspects.size(); i++) {
                    for (int j = i + 1; j < suspects.size(); j++) {
                        String s1 = suspects.get(i);
                        String s2 = suspects.get(j);
                        
                        int strength = calculateNameStrength(s1, s2, criminalCrimes, crimeData);
                        addUniqueConnection(networks, addedConnections, s1, s2, strength);
                    }
                }
            }
        }
        
        // Rule 2: Connect criminals in SAME LOCATION
        Map<String, List<String>> locationGroups = groupByLocation(crimeData, criminalCrimes);
        for (List<String> suspects : locationGroups.values()) {
            if (suspects.size() > 1) {
                for (int i = 0; i < suspects.size(); i++) {
                    for (int j = i + 1; j < suspects.size(); j++) {
                        String s1 = suspects.get(i);
                        String s2 = suspects.get(j);
                        
                        int strength = calculateLocationStrength(s1, s2, criminalCrimes, crimeData);
                        addUniqueConnection(networks, addedConnections, s1, s2, strength);
                    }
                }
            }
        }
        
        // Rule 3: Connect criminals with SAME CRIME TYPE
        Map<String, List<String>> crimeTypeGroups = groupByCrimeType(crimeData, criminalCrimes);
        for (List<String> suspects : crimeTypeGroups.values()) {
            if (suspects.size() > 1 && suspects.size() <= 5) {
                for (int i = 0; i < suspects.size(); i++) {
                    for (int j = i + 1; j < suspects.size(); j++) {
                        String s1 = suspects.get(i);
                        String s2 = suspects.get(j);
                        
                        int strength = calculateCrimeTypeStrength(s1, s2, criminalCrimes, crimeData);
                        addUniqueConnection(networks, addedConnections, s1, s2, strength);
                    }
                }
            }
        }
        
        System.out.println("✓ Generated " + networks.size() + " criminal network connections");
        
        return networks;
    }
    
    // Helper: Group criminals by first name
    private static Map<String, List<String>> groupByFirstName(Map<String, List<String>> criminalCrimes) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (String suspect : criminalCrimes.keySet()) {
            String firstName = suspect.split(" ")[0];
            groups.putIfAbsent(firstName, new ArrayList<>());
            groups.get(firstName).add(suspect);
        }
        
        return groups;
    }
    
    // Helper: Group criminals by location
    private static Map<String, List<String>> groupByLocation(
            Map<String, List<String>> crimeData, 
            Map<String, List<String>> criminalCrimes) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : criminalCrimes.entrySet()) {
            String suspect = entry.getKey();
            
            for (String crimeId : entry.getValue()) {
                List<String> record = crimeData.get(crimeId);
                String location = record.get(1); // Location at index 1
                
                groups.putIfAbsent(location, new ArrayList<>());
                if (!groups.get(location).contains(suspect)) {
                    groups.get(location).add(suspect);
                }
            }
        }
        
        return groups;
    }
    
    // Helper: Group criminals by crime type
    private static Map<String, List<String>> groupByCrimeType(
            Map<String, List<String>> crimeData,
            Map<String, List<String>> criminalCrimes) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : criminalCrimes.entrySet()) {
            String suspect = entry.getKey();
            
            for (String crimeId : entry.getValue()) {
                List<String> record = crimeData.get(crimeId);
                String type = record.get(0); // Type at index 0
                
                groups.putIfAbsent(type, new ArrayList<>());
                if (!groups.get(type).contains(suspect)) {
                    groups.get(type).add(suspect);
                }
            }
        }
        
        return groups;
    }
    
    // Calculate strength for name match
    private static int calculateNameStrength(String s1, String s2,
            Map<String, List<String>> criminalCrimes,
            Map<String, List<String>> crimeData) {
        int strength = 5; // Base for same name
        
        List<String> crimes1 = criminalCrimes.get(s1);
        List<String> crimes2 = criminalCrimes.get(s2);
        
        // Add strength based on number of crimes
        strength += (crimes1.size() + crimes2.size()) / 2;
        
        // Check for same crime types
        Set<String> types1 = new HashSet<>();
        Set<String> types2 = new HashSet<>();
        
        for (String crimeId : crimes1) {
            types1.add(crimeData.get(crimeId).get(0));
        }
        for (String crimeId : crimes2) {
            types2.add(crimeData.get(crimeId).get(0));
        }
        
        types1.retainAll(types2); // Intersection
        strength += types1.size() * 2;
        
        return Math.min(strength, 15); // Max 15
    }
    
    // Calculate strength for location match
    private static int calculateLocationStrength(String s1, String s2,
            Map<String, List<String>> criminalCrimes,
            Map<String, List<String>> crimeData) {
        int strength = 3; // Base for same location
        
        List<String> crimes1 = criminalCrimes.get(s1);
        List<String> crimes2 = criminalCrimes.get(s2);
        
        // Check for same dates
        Set<String> dates1 = new HashSet<>();
        Set<String> dates2 = new HashSet<>();
        
        for (String crimeId : crimes1) {
            dates1.add(crimeData.get(crimeId).get(2)); // Date at index 2
        }
        for (String crimeId : crimes2) {
            dates2.add(crimeData.get(crimeId).get(2));
        }
        
        dates1.retainAll(dates2);
        strength += dates1.size() * 3;
        
        // Add severity average
        int totalSeverity = 0;
        int count = 0;
        
        for (String crimeId : crimes1) {
            totalSeverity += Integer.parseInt(crimeData.get(crimeId).get(3));
            count++;
        }
        for (String crimeId : crimes2) {
            totalSeverity += Integer.parseInt(crimeData.get(crimeId).get(3));
            count++;
        }
        
        strength += (totalSeverity / count) / 2;
        
        return Math.min(strength, 12); // Max 12
    }
    
    // Calculate strength for crime type match
    private static int calculateCrimeTypeStrength(String s1, String s2,
            Map<String, List<String>> criminalCrimes,
            Map<String, List<String>> crimeData) {
        int strength = 2; // Base for same crime type
        
        List<String> crimes1 = criminalCrimes.get(s1);
        List<String> crimes2 = criminalCrimes.get(s2);
        
        // Count common crime types
        Map<String, Integer> types1 = new HashMap<>();
        Map<String, Integer> types2 = new HashMap<>();
        
        for (String crimeId : crimes1) {
            String type = crimeData.get(crimeId).get(0);
            types1.put(type, types1.getOrDefault(type, 0) + 1);
        }
        for (String crimeId : crimes2) {
            String type = crimeData.get(crimeId).get(0);
            types2.put(type, types2.getOrDefault(type, 0) + 1);
        }
        
        for (String type : types1.keySet()) {
            if (types2.containsKey(type)) {
                strength += Math.min(types1.get(type), types2.get(type));
            }
        }
        
        return Math.min(strength, 10); // Max 10
    }
    
    // Add connection if not already added
    private static void addUniqueConnection(
            List<crimesystem.models.CriminalNetwork> networks,
            Set<String> addedConnections,
            String s1, String s2, int strength) {
        
        String key1 = s1 + "-" + s2;
        String key2 = s2 + "-" + s1;
        
        if (!addedConnections.contains(key1) && !addedConnections.contains(key2)) {
            networks.add(new crimesystem.models.CriminalNetwork(s1, s2, strength));
            addedConnections.add(key1);
            addedConnections.add(key2);
        }
    }
}
