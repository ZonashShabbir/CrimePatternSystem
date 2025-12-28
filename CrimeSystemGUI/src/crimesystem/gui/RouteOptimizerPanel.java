package crimesystem.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class RouteOptimizerPanel extends JPanel {
    
    private static final String DATA_PATH = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    private static final String LOCATIONS_FILE = "patrol_locations.txt";
    
    private JTextField txtLocationID, txtLocationName, txtCrimeCount;
    private JTextField txtLoc1, txtLoc2, txtDistance;
    private JTextField txtStartLocation, txtEndLocation;
    private JComboBox<String> cmbType;
    private JTextArea txtOutput;
    private JButton btnAddLocation, btnAddRoute, btnFindPath, btnShowAll, btnLoadData, btnClear;
    
    private Map<String, Location> locations;
    private Map<String, Map<String, Double>> routes;
    
    class Location {
        String id;
        String name;
        String type;
        int crimeCount;
        
        Location(String id, String name, String type, int crimeCount) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.crimeCount = crimeCount;
        }
    }
    
    public RouteOptimizerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 245));
        
        locations = new HashMap<>();
        routes = new HashMap<>();
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(46, 204, 113));
        JLabel title = new JLabel("Patrol Route Optimizer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        
        // Left side: Input controls WITH SCROLL
        JPanel leftPanel = createLeftPanel();
        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        splitPane.setLeftComponent(leftScroll);
        
        // Right side: Output display
        JPanel rightPanel = createOutputPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // BOTTOM TOOLBAR with main action buttons
        JPanel bottomToolbar = createBottomToolbar();
        add(bottomToolbar, BorderLayout.SOUTH);
        
        showWelcomeMessage();
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 245));
        
        // Load Data Section
        JPanel loadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadPanel.setBackground(Color.WHITE);
        loadPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Load Patrol Data"));
        
        btnLoadData = createButton("Load patrol_locations.txt", new Color(52, 152, 219));
        btnLoadData.setPreferredSize(new Dimension(250, 40));
        btnLoadData.addActionListener(e -> loadPatrolData());
        loadPanel.add(btnLoadData);
        
        panel.add(loadPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add Location Section
        JPanel locationPanel = createLocationPanel();
        panel.add(locationPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add Route Section
        JPanel routePanel = createRoutePanel();
        panel.add(routePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Find Shortest Path Section
        JPanel pathPanel = createPathPanel();
        panel.add(pathPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return panel;
    }
    
    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        toolbar.setBackground(new Color(52, 73, 94));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnShowAll = createButton("Show All Locations & Routes", new Color(155, 89, 182));
        btnShowAll.setPreferredSize(new Dimension(250, 40));
        btnShowAll.addActionListener(e -> showAllData());
        toolbar.add(btnShowAll);
        
        btnClear = createButton("Clear All", new Color(149, 165, 166));
        btnClear.setPreferredSize(new Dimension(150, 40));
        btnClear.addActionListener(e -> clearAll());
        toolbar.add(btnClear);
        
        return toolbar;
    }
    
    private JPanel createLocationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Step 2: Add Location"));
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(createLabel("Location ID:"));
        txtLocationID = new JTextField();
        txtLocationID.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtLocationID);
        
        inputPanel.add(createLabel("Location Name:"));
        txtLocationName = new JTextField();
        txtLocationName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtLocationName);
        
        inputPanel.add(createLabel("Type:"));
        String[] types = {"POLICE_STATION", "CRIME_HOTSPOT", "CHECKPOINT", "PATROL_POINT"};
        cmbType = new JComboBox<>(types);
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(cmbType);
        
        inputPanel.add(createLabel("Crime Count:"));
        txtCrimeCount = new JTextField("0");
        txtCrimeCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtCrimeCount);
        
        panel.add(inputPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAddLocation = createButton("Add Location", new Color(46, 204, 113));
        btnAddLocation.addActionListener(e -> addLocation());
        buttonPanel.add(btnAddLocation);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createRoutePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Step 3: Add Route Between Locations"));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(createLabel("Location 1 ID:"));
        txtLoc1 = new JTextField();
        txtLoc1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtLoc1);
        
        inputPanel.add(createLabel("Location 2 ID:"));
        txtLoc2 = new JTextField();
        txtLoc2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtLoc2);
        
        inputPanel.add(createLabel("Distance (km):"));
        txtDistance = new JTextField();
        txtDistance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtDistance);
        
        panel.add(inputPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAddRoute = createButton("Add Route", new Color(52, 152, 219));
        btnAddRoute.addActionListener(e -> addRoute());
        buttonPanel.add(btnAddRoute);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createPathPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Step 4: Find Shortest Path"));
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(createLabel("Start Location ID:"));
        txtStartLocation = new JTextField();
        txtStartLocation.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtStartLocation);
        
        inputPanel.add(createLabel("End Location ID:"));
        txtEndLocation = new JTextField();
        txtEndLocation.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtEndLocation);
        
        panel.add(inputPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        btnFindPath = createButton("Find Shortest Path (Dijkstra)", new Color(230, 126, 34));
        btnFindPath.addActionListener(e -> findShortestPath());
        buttonPanel.add(btnFindPath);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Route Analysis Results",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtOutput.setBackground(new Color(250, 250, 250));
        txtOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void showWelcomeMessage() {
        txtOutput.setText("===============================================================================\n");
        txtOutput.append("                   PATROL ROUTE OPTIMIZER\n");
        txtOutput.append("===============================================================================\n\n");
        txtOutput.append("HOW TO USE:\n\n");
        txtOutput.append("STEP 1: Load Data\n");
        txtOutput.append("  - Click 'Load patrol_locations.txt' to load existing locations\n");
        txtOutput.append("  - File loaded: 66 locations, 71 routes\n\n");
        txtOutput.append("STEP 2: View All Data\n");
        txtOutput.append("  - Click 'Show All Locations & Routes' button at BOTTOM\n");
        txtOutput.append("  - This will display all 66 locations and 71 routes\n\n");
        txtOutput.append("STEP 3: Find Shortest Path\n");
        txtOutput.append("  - Example: Start: K001, End: K011 (Karachi)\n");
        txtOutput.append("  - Example: Start: I001, End: R001 (Islamabad to Rawalpindi)\n");
        txtOutput.append("  - Click 'Find Shortest Path (Dijkstra)'\n\n");
        txtOutput.append("STEP 4: Add New Data (Optional)\n");
        txtOutput.append("  - Add new locations and routes manually\n\n");
        txtOutput.append("Ready! Click 'Show All' at bottom to view loaded data.\n");
        txtOutput.append("===============================================================================\n");
    }
    
    private void loadPatrolData() {
        try {
            txtOutput.setText("Loading patrol locations from file...\n\n");
            
            File file = new File(DATA_PATH + LOCATIONS_FILE);
            
            if (!file.exists()) {
                txtOutput.append("File not found! Creating sample data...\n\n");
                createSampleFile();
                file = new File(DATA_PATH + LOCATIONS_FILE);
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int locationCount = 0;
            int routeCount = 0;
            
            locations.clear();
            routes.clear();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                
                if (parts[0].equals("LOCATION") && parts.length >= 5) {
                    String id = parts[1].trim();
                    String name = parts[2].trim();
                    String type = parts[3].trim();
                    int crimeCount = Integer.parseInt(parts[4].trim());
                    
                    locations.put(id, new Location(id, name, type, crimeCount));
                    locationCount++;
                    
                } else if (parts[0].equals("ROUTE") && parts.length >= 4) {
                    String loc1 = parts[1].trim();
                    String loc2 = parts[2].trim();
                    double distance = Double.parseDouble(parts[3].trim());
                    
                    addRouteToGraph(loc1, loc2, distance);
                    routeCount++;
                }
            }
            
            reader.close();
            
            txtOutput.append("===============================================================================\n");
            txtOutput.append("LOADING COMPLETE!\n");
            txtOutput.append("===============================================================================\n");
            txtOutput.append("Locations Loaded: " + locationCount + "\n");
            txtOutput.append("Routes Loaded: " + routeCount + "\n\n");
            txtOutput.append("Data loaded successfully!\n\n");
            txtOutput.append("NEXT STEPS:\n");
            txtOutput.append("1. Click 'Show All' button at BOTTOM to view all data\n");
            txtOutput.append("2. Or enter location IDs to find shortest path\n\n");
            txtOutput.append("Example shortest paths:\n");
            txtOutput.append("  - K001 to K011 (Karachi routes)\n");
            txtOutput.append("  - I001 to R001 (Islamabad to Rawalpindi)\n");
            txtOutput.append("  - L001 to L010 (Lahore routes)\n");
            
            JOptionPane.showMessageDialog(this,
                "Successfully loaded:\n" + locationCount + " locations\n" + routeCount + " routes\n\n" +
                "Click 'Show All' button at bottom to view data!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            txtOutput.append("\nERROR: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                "Error loading file!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createSampleFile() throws IOException {
        File file = new File(DATA_PATH + LOCATIONS_FILE);
        file.getParentFile().mkdirs();
        
        FileWriter writer = new FileWriter(file);
        writer.write("# Patrol Locations - Sample Data\n");
        writer.write("LOCATION|K001|Karachi Saddar Station|POLICE_STATION|52\n");
        writer.write("LOCATION|K002|Karachi Defence Checkpoint|CHECKPOINT|35\n");
        writer.write("LOCATION|I001|Islamabad F-6 Station|POLICE_STATION|35\n");
        writer.write("LOCATION|R001|Rawalpindi Saddar Station|POLICE_STATION|58\n\n");
        writer.write("ROUTE|K001|K002|8.5\n");
        writer.write("ROUTE|I001|R001|18.5\n");
        writer.close();
        
        txtOutput.append("Sample file created successfully!\n");
    }
    
    private void addLocation() {
        try {
            String id = txtLocationID.getText().trim();
            String name = txtLocationName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            int crimeCount = Integer.parseInt(txtCrimeCount.getText().trim());
            
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (locations.containsKey(id)) {
                JOptionPane.showMessageDialog(this,
                    "Location ID already exists!",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            locations.put(id, new Location(id, name, type, crimeCount));
            saveLocationToFile(id, name, type, crimeCount);
            
            txtOutput.setText("LOCATION ADDED SUCCESSFULLY!\n\n");
            txtOutput.append("ID: " + id + "\n");
            txtOutput.append("Name: " + name + "\n");
            txtOutput.append("Type: " + type + "\n");
            txtOutput.append("Crime Count: " + crimeCount + "\n\n");
            txtOutput.append("Total Locations: " + locations.size() + "\n");
            
            txtLocationID.setText("");
            txtLocationName.setText("");
            txtCrimeCount.setText("0");
            
            JOptionPane.showMessageDialog(this,
                "Location added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid crime count!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addRoute() {
        try {
            String loc1 = txtLoc1.getText().trim();
            String loc2 = txtLoc2.getText().trim();
            double distance = Double.parseDouble(txtDistance.getText().trim());
            
            if (loc1.isEmpty() || loc2.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!locations.containsKey(loc1) || !locations.containsKey(loc2)) {
                JOptionPane.showMessageDialog(this,
                    "One or both locations not found!\nPlease add locations first or load data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            addRouteToGraph(loc1, loc2, distance);
            saveRouteToFile(loc1, loc2, distance);
            
            txtOutput.setText("ROUTE ADDED SUCCESSFULLY!\n\n");
            txtOutput.append("From: " + loc1 + " (" + locations.get(loc1).name + ")\n");
            txtOutput.append("To: " + loc2 + " (" + locations.get(loc2).name + ")\n");
            txtOutput.append("Distance: " + distance + " km\n\n");
            txtOutput.append("Total Routes: " + countRoutes() + "\n");
            
            txtLoc1.setText("");
            txtLoc2.setText("");
            txtDistance.setText("");
            
            JOptionPane.showMessageDialog(this,
                "Route added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid distance!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addRouteToGraph(String loc1, String loc2, double distance) {
        routes.putIfAbsent(loc1, new HashMap<>());
        routes.putIfAbsent(loc2, new HashMap<>());
        routes.get(loc1).put(loc2, distance);
        routes.get(loc2).put(loc1, distance);
    }
    
    private void findShortestPath() {
        String start = txtStartLocation.getText().trim();
        String end = txtEndLocation.getText().trim();
        
        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both start and end locations!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!locations.containsKey(start) || !locations.containsKey(end)) {
            JOptionPane.showMessageDialog(this,
                "One or both locations not found!\nPlease load data first.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        txtOutput.setText("===============================================================================\n");
        txtOutput.append("          FINDING SHORTEST PATH (DIJKSTRA ALGORITHM)\n");
        txtOutput.append("===============================================================================\n\n");
        txtOutput.append("Start: " + start + " - " + locations.get(start).name + "\n");
        txtOutput.append("End: " + end + " - " + locations.get(end).name + "\n\n");
        txtOutput.append("Running Dijkstra's algorithm...\n\n");
        
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        Set<String> visited = new HashSet<>();
        
        for (String loc : locations.keySet()) {
            distances.put(loc, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.offer(new Node(start, 0.0));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (visited.contains(current.id)) continue;
            visited.add(current.id);
            
            if (current.id.equals(end)) break;
            
            Map<String, Double> neighbors = routes.get(current.id);
            if (neighbors != null) {
                for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
                    String neighborId = neighbor.getKey();
                    double weight = neighbor.getValue();
                    double newDist = distances.get(current.id) + weight;
                    
                    if (newDist < distances.get(neighborId)) {
                        distances.put(neighborId, newDist);
                        previous.put(neighborId, current.id);
                        pq.offer(new Node(neighborId, newDist));
                    }
                }
            }
        }
        
        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        
        if (path.isEmpty() || !path.get(0).equals(start)) {
            txtOutput.append("===============================================================================\n");
            txtOutput.append("NO PATH FOUND!\n");
            txtOutput.append("===============================================================================\n\n");
            txtOutput.append("There is no route connecting these locations.\n");
            return;
        }
        
        double totalDistance = distances.get(end);
        
        txtOutput.append("===============================================================================\n");
        txtOutput.append("SHORTEST PATH FOUND!\n");
        txtOutput.append("===============================================================================\n\n");
        txtOutput.append("Total Distance: " + String.format("%.2f", totalDistance) + " km\n");
        txtOutput.append("Number of Stops: " + path.size() + "\n\n");
        txtOutput.append("OPTIMAL ROUTE:\n");
        txtOutput.append("---------------------------------------\n\n");
        
        for (int i = 0; i < path.size(); i++) {
            String locId = path.get(i);
            Location loc = locations.get(locId);
            
            txtOutput.append((i + 1) + ". " + locId + " - " + loc.name + "\n");
            txtOutput.append("   Type: " + loc.type + "\n");
            txtOutput.append("   Crime Count: " + loc.crimeCount + "\n");
            
            if (i < path.size() - 1) {
                String nextId = path.get(i + 1);
                double segmentDist = routes.get(locId).get(nextId);
                txtOutput.append("   --> " + String.format("%.2f", segmentDist) + " km to next location\n");
            }
            
            txtOutput.append("\n");
        }
        
        txtOutput.append("===============================================================================\n");
        txtOutput.append("ROUTE OPTIMIZATION COMPLETE!\n");
        txtOutput.append("===============================================================================\n");
    }
    
    class Node {
        String id;
        double distance;
        
        Node(String id, double distance) {
            this.id = id;
            this.distance = distance;
        }
    }
    
    private void showAllData() {
        if (locations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No data loaded!\nPlease click 'Load patrol_locations.txt' first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        txtOutput.setText("===============================================================================\n");
        txtOutput.append("            ALL PATROL LOCATIONS AND ROUTES\n");
        txtOutput.append("===============================================================================\n\n");
        
        txtOutput.append("LOCATIONS (" + locations.size() + " total):\n");
        txtOutput.append("---------------------------------------\n\n");
        
        for (Location loc : locations.values()) {
            txtOutput.append(loc.id + " - " + loc.name + "\n");
            txtOutput.append("  Type: " + loc.type + "\n");
            txtOutput.append("  Crime Count: " + loc.crimeCount + "\n\n");
        }
        
        txtOutput.append("\nROUTES (" + countRoutes() + " total):\n");
        txtOutput.append("---------------------------------------\n\n");
        
        Set<String> printedRoutes = new HashSet<>();
        
        for (Map.Entry<String, Map<String, Double>> entry : routes.entrySet()) {
            String loc1 = entry.getKey();
            for (Map.Entry<String, Double> route : entry.getValue().entrySet()) {
                String loc2 = route.getKey();
                double distance = route.getValue();
                
                String routeKey = loc1.compareTo(loc2) < 0 ? loc1 + "-" + loc2 : loc2 + "-" + loc1;
                
                if (!printedRoutes.contains(routeKey)) {
                    txtOutput.append(loc1 + " <--> " + loc2 + ": " + distance + " km\n");
                    printedRoutes.add(routeKey);
                }
            }
        }
        
        txtOutput.append("\n===============================================================================\n");
        txtOutput.append("DATA DISPLAY COMPLETE!\n");
        txtOutput.append("===============================================================================\n");
    }
    
    private int countRoutes() {
        int count = 0;
        for (Map<String, Double> neighbors : routes.values()) {
            count += neighbors.size();
        }
        return count / 2;
    }
    
    private void saveLocationToFile(String id, String name, String type, int crimeCount) {
        try {
            File file = new File(DATA_PATH + LOCATIONS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            FileWriter writer = new FileWriter(file, true);
            writer.write("LOCATION|" + id + "|" + name + "|" + type + "|" + crimeCount + "\n");
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveRouteToFile(String loc1, String loc2, double distance) {
        try {
            File file = new File(DATA_PATH + LOCATIONS_FILE);
            FileWriter writer = new FileWriter(file, true);
            writer.write("ROUTE|" + loc1 + "|" + loc2 + "|" + distance + "\n");
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void clearAll() {
        txtLocationID.setText("");
        txtLocationName.setText("");
        txtCrimeCount.setText("0");
        txtLoc1.setText("");
        txtLoc2.setText("");
        txtDistance.setText("");
        txtStartLocation.setText("");
        txtEndLocation.setText("");
        txtOutput.setText("");
        showWelcomeMessage();
    }
}