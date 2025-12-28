package crimesystem.gui;

import crimesystem.integration.CppIntegration;
import crimesystem.integration.CrimeDataLoader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NetworkFinderPanel extends JPanel {
    
    private JTextField txtSearchCriminal, txtNode1, txtNode2;
    private JTextArea txtOutput;
    private JTable tblConnections;
    private DefaultTableModel tableModel;
    private JButton btnLoadData, btnSearchCriminal, btnFindPath, btnShowNetwork, btnClear;
    private Map<String, Set<String>> networkGraph;
    private Map<String, List<String>> crimeData;
    private JList<String> suggestionList;
    private JPopupMenu suggestionPopup;
    
    public NetworkFinderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 245));
        
        networkGraph = new HashMap<>();
        crimeData = new HashMap<>();
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(231, 76, 60));
        JLabel title = new JLabel("🔗 Crime Network Finder");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // Left side: Input controls
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right side: Table view
        JPanel rightPanel = createTablePanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        showWelcomeMessage();
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        
        // Input Panel
        JPanel inputPanel = createInputPanel();
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Output Panel
        JPanel outputPanel = createOutputPanel();
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Load Data Section
        JPanel loadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadPanel.setBackground(Color.WHITE);
        loadPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Load Criminal Records"));
        
        btnLoadData = createButton("📂 Load initial_crimes.txt", new Color(46, 204, 113));
        btnLoadData.setPreferredSize(new Dimension(250, 40));
        btnLoadData.addActionListener(e -> loadCriminalData());
        loadPanel.add(btnLoadData);
        
        panel.add(loadPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Search Criminal Section WITH AUTO-COMPLETE
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Step 2: Search Criminal by ID or Name"));
        
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.setBackground(Color.WHITE);
        searchInputPanel.add(new JLabel("Criminal ID/Name:"));
        
        txtSearchCriminal = new JTextField(20);
        txtSearchCriminal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Setup auto-complete suggestion list
        suggestionList = new JList<>();
        suggestionList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        suggestionPopup = new JPopupMenu();
        JScrollPane suggestionScroll = new JScrollPane(suggestionList);
        suggestionScroll.setPreferredSize(new Dimension(300, 150));
        suggestionPopup.add(suggestionScroll);
        
        // Add key listener for auto-complete
        txtSearchCriminal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchCriminalNetwork();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (suggestionPopup.isVisible() && suggestionList.getModel().getSize() > 0) {
                        suggestionList.requestFocus();
                        suggestionList.setSelectedIndex(0);
                    }
                } else {
                    showSuggestions();
                }
            }
        });
        
        // Add mouse listener to suggestion list
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                        // Extract ID from selection (format: "CR0079 - Salman Hassan")
                        String id = selected.split(" - ")[0].trim();
                        txtSearchCriminal.setText(id);
                        suggestionPopup.setVisible(false);
                        searchCriminalNetwork();
                    }
                }
            }
        });
        
        // Add list selection listener for keyboard navigation
        suggestionList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                        String id = selected.split(" - ")[0].trim();
                        txtSearchCriminal.setText(id);
                        suggestionPopup.setVisible(false);
                        txtSearchCriminal.requestFocus();
                        searchCriminalNetwork();
                    }
                }
            }
        });
        
        searchInputPanel.add(txtSearchCriminal);
        
        btnSearchCriminal = createButton("🔍 Search Network", new Color(52, 152, 219));
        btnSearchCriminal.addActionListener(e -> searchCriminalNetwork());
        searchInputPanel.add(btnSearchCriminal);
        
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        
        // Add help text
        JLabel helpLabel = new JLabel("💡 Type to see suggestions, use arrow keys to navigate");
        helpLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(127, 140, 141));
        helpLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(helpLabel, BorderLayout.SOUTH);
        
        panel.add(searchPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Find Path Section
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.Y_AXIS));
        pathPanel.setBackground(Color.WHITE);
        pathPanel.setBorder(BorderFactory.createTitledBorder("Step 3: Find Connection Between Two Criminals"));
        
        JPanel pathInputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        pathInputPanel.setBackground(Color.WHITE);
        
        pathInputPanel.add(createLabel("Criminal 1 ID:"));
        txtNode1 = new JTextField();
        txtNode1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pathInputPanel.add(txtNode1);
        
        pathInputPanel.add(createLabel("Criminal 2 ID:"));
        txtNode2 = new JTextField();
        txtNode2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pathInputPanel.add(txtNode2);
        
        pathPanel.add(pathInputPanel);
        pathPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel pathButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathButtonPanel.setBackground(Color.WHITE);
        
        btnFindPath = createButton("🔗 Find Connection Path", new Color(155, 89, 182));
        btnFindPath.addActionListener(e -> findConnectionPath());
        pathButtonPanel.add(btnFindPath);
        
        pathPanel.add(pathButtonPanel);
        panel.add(pathPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnShowNetwork = createButton("📊 Show Full Network", new Color(230, 126, 34));
        btnShowNetwork.addActionListener(e -> showFullNetworkTable());
        buttonPanel.add(btnShowNetwork);
        
        btnClear = createButton("🔄 Clear", new Color(149, 165, 166));
        btnClear.addActionListener(e -> clearAll());
        buttonPanel.add(btnClear);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Network Analysis Results",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        txtOutput = new JTextArea(12, 40);
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtOutput.setBackground(new Color(250, 250, 250));
        txtOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Network Connections Table",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table
        String[] columnNames = {"Criminal ID", "Suspect Name", "Crime Type", "Location", "Total Connections"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        tblConnections = new JTable(tableModel);
        tblConnections.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblConnections.setRowHeight(25);
        tblConnections.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblConnections.getTableHeader().setBackground(new Color(52, 152, 219));
        tblConnections.getTableHeader().setForeground(Color.WHITE);
        tblConnections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblConnections.setGridColor(new Color(200, 200, 200));
        
        // Add row sorter for filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tblConnections.setRowSorter(sorter);
        
        // Add double-click listener
        tblConnections.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblConnections.getSelectedRow();
                    if (row >= 0) {
                        String criminalId = (String) tblConnections.getValueAt(row, 0);
                        txtSearchCriminal.setText(criminalId);
                        searchCriminalNetwork();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblConnections);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add info label at bottom
        JLabel infoLabel = new JLabel("💡 Double-click any row to view connections");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showSuggestions() {
        String searchText = txtSearchCriminal.getText().trim().toLowerCase();
        
        if (searchText.isEmpty() || crimeData.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }
        
        // Find matching criminals
        java.util.List<String> matches = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : crimeData.entrySet()) {
            String crimeId = entry.getKey();
            String suspect = entry.getValue().get(4); // Suspect name at index 4
            
            // Match by ID or suspect name
            if (crimeId.toLowerCase().contains(searchText) || 
                suspect.toLowerCase().contains(searchText)) {
                matches.add(crimeId + " - " + suspect);
            }
            
            if (matches.size() >= 10) break; // Limit to 10 suggestions
        }
        
        if (matches.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }
        
        // Update suggestion list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String match : matches) {
            listModel.addElement(match);
        }
        suggestionList.setModel(listModel);
        
        // Show popup below text field
        if (!suggestionPopup.isVisible()) {
            suggestionPopup.show(txtSearchCriminal, 0, txtSearchCriminal.getHeight());
        }
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
        txtOutput.setText("═══════════════════════════════════════════════\n");
        txtOutput.append("   CRIME NETWORK FINDER - TABLE VIEW\n");
        txtOutput.append("═══════════════════════════════════════════════\n\n");
        txtOutput.append("📋 HOW TO USE:\n\n");
        txtOutput.append("1️⃣ Click 'Load initial_crimes.txt'\n");
        txtOutput.append("   → Data will appear in right table\n\n");
        txtOutput.append("2️⃣ Start typing in search box\n");
        txtOutput.append("   → Suggestions will appear automatically\n");
        txtOutput.append("   → Press Enter or click to select\n\n");
        txtOutput.append("3️⃣ Double-click any table row\n");
        txtOutput.append("   → Shows that criminal's connections\n\n");
        txtOutput.append("4️⃣ Find path between two criminals\n");
        txtOutput.append("   → Enter IDs and click Find Connection\n\n");
        txtOutput.append("Ready to start! 🚀\n");
        txtOutput.append("═══════════════════════════════════════════════\n");
    }
    
    private void loadCriminalData() {
        try {
            txtOutput.setText("⏳ Loading criminal records from initial_crimes.txt...\n\n");
            
            // Load data
            crimeData = CrimeDataLoader.loadCriminalRecords();
            
            if (crimeData.isEmpty()) {
                txtOutput.append("❌ ERROR: No data found!\n");
                JOptionPane.showMessageDialog(this,
                    "File not found or empty!\nPath: C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\initial_crimes.txt",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            txtOutput.append("✓ Loaded " + crimeData.size() + " criminal records!\n\n");
            
            // Build network graph
            txtOutput.append("🔨 Building network connections...\n");
            networkGraph = CrimeDataLoader.buildNetworkGraph(crimeData);
            
            txtOutput.append("✓ Network graph constructed!\n\n");
            txtOutput.append("═══════════════════════════════════════════════\n");
            txtOutput.append("NETWORK STATISTICS:\n");
            txtOutput.append("═══════════════════════════════════════════════\n");
            txtOutput.append("Total Criminals: " + crimeData.size() + "\n");
            txtOutput.append("Total Connections: " + countConnections() + "\n\n");
            txtOutput.append("✅ Data loaded! View table on right →\n");
            txtOutput.append("   Start typing to search criminals\n");
            
            // Populate table
            populateTable();
            
            JOptionPane.showMessageDialog(this,
                "Successfully loaded " + crimeData.size() + " records!\n" +
                "Network graph built with " + countConnections() + " connections.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            handleError("Error loading criminal data", ex);
        }
    }
    
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        
        for (Map.Entry<String, List<String>> entry : crimeData.entrySet()) {
            String crimeId = entry.getKey();
            List<String> record = entry.getValue();
            
            String type = record.get(0);
            String location = record.get(1);
            String suspect = record.get(4);
            
            Set<String> connections = networkGraph.get(crimeId);
            int connectionCount = (connections != null) ? connections.size() : 0;
            
            Object[] row = {crimeId, suspect, type, location, connectionCount};
            tableModel.addRow(row);
        }
    }
    
    private void searchCriminalNetwork() {
        try {
            String searchTerm = txtSearchCriminal.getText().trim();
            suggestionPopup.setVisible(false); // Hide suggestions
            
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter Criminal ID or Name!",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (networkGraph.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please load data first!",
                    "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            txtOutput.setText("═══════════════════════════════════════════════\n");
            txtOutput.append("🔍 SEARCHING CRIMINAL NETWORK\n");
            txtOutput.append("═══════════════════════════════════════════════\n\n");
            txtOutput.append("Search Query: " + searchTerm + "\n\n");
            
            // Find criminal ID
            String criminalId = findCriminalId(searchTerm);
            
            if (criminalId == null) {
                txtOutput.append("❌ Criminal not found!\n");
                txtOutput.append("Try searching by:\n");
                txtOutput.append("  • Crime ID (e.g., CR0079)\n");
                txtOutput.append("  • Suspect Name (e.g., Salman Hassan)\n");
                JOptionPane.showMessageDialog(this,
                    "Criminal not found: " + searchTerm,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            List<String> record = crimeData.get(criminalId);
            String suspect = record.get(4);
            String type = record.get(0);
            String location = record.get(1);
            
            txtOutput.append("✓ Criminal Found!\n");
            txtOutput.append("═══════════════════════════════════════════════\n");
            txtOutput.append("Crime ID: " + criminalId + "\n");
            txtOutput.append("Suspect: " + suspect + "\n");
            txtOutput.append("Type: " + type + "\n");
            txtOutput.append("Location: " + location + "\n");
            txtOutput.append("═══════════════════════════════════════════════\n\n");
            
            // Get connections
            Set<String> connections = networkGraph.get(criminalId);
            
            if (connections == null || connections.isEmpty()) {
                txtOutput.append("⚠ No connections found.\n");
                txtOutput.append("This criminal operates independently.\n\n");
                
                // Clear table and show only this record
                tableModel.setRowCount(0);
                Object[] row = {criminalId, suspect, type, location, 0};
                tableModel.addRow(row);
            } else {
                txtOutput.append("📊 CONNECTED CRIMINALS:\n");
                txtOutput.append("Total Connections: " + connections.size() + "\n\n");
                
                // Clear table and populate with connected criminals
                tableModel.setRowCount(0);
                
                // Add searched criminal first (highlighted row)
                Object[] mainRow = {criminalId + " ⭐", suspect, type, location, connections.size()};
                tableModel.addRow(mainRow);
                
                // Add connected criminals
                int i = 1;
                for (String connectedId : connections) {
                    List<String> connRecord = crimeData.get(connectedId);
                    if (connRecord != null) {
                        String connSuspect = connRecord.get(4);
                        String connType = connRecord.get(0);
                        String connLocation = connRecord.get(1);
                        Set<String> connConnections = networkGraph.get(connectedId);
                        int connCount = (connConnections != null) ? connConnections.size() : 0;
                        
                        Object[] row = {connectedId, connSuspect, connType, connLocation, connCount};
                        tableModel.addRow(row);
                        
                        txtOutput.append(String.format("[%d] %s - %s (%s)\n", 
                            i++, connectedId, connSuspect, connType));
                    }
                }
                
                txtOutput.append("\n═══════════════════════════════════════════════\n");
                txtOutput.append("✅ Results displayed in table →\n");
                txtOutput.append("   ⭐ marks the searched criminal\n");
            }
            
        } catch (Exception ex) {
            handleError("Error searching criminal", ex);
        }
    }
    
    private void findConnectionPath() {
        try {
            String criminal1 = txtNode1.getText().trim();
            String criminal2 = txtNode2.getText().trim();
            
            if (criminal1.isEmpty() || criminal2.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter both Criminal IDs!",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (networkGraph.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please load data first!",
                    "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            txtOutput.setText("═══════════════════════════════════════════════\n");
            txtOutput.append("🔗 FINDING CONNECTION PATH\n");
            txtOutput.append("═══════════════════════════════════════════════\n\n");
            txtOutput.append("From: " + criminal1 + "\n");
            txtOutput.append("To: " + criminal2 + "\n\n");
            txtOutput.append("⏳ Running BFS algorithm...\n\n");
            
            // Find path using BFS
            List<String> path = findPathBFS(criminal1, criminal2);
            
            if (path == null || path.isEmpty()) {
                txtOutput.append("═══════════════════════════════════════════════\n");
                txtOutput.append("❌ NO CONNECTION FOUND!\n");
                txtOutput.append("═══════════════════════════════════════════════\n\n");
                txtOutput.append("These criminals are not connected.\n");
                tableModel.setRowCount(0);
            } else {
                txtOutput.append("═══════════════════════════════════════════════\n");
                txtOutput.append("✅ CONNECTION PATH FOUND!\n");
                txtOutput.append("═══════════════════════════════════════════════\n\n");
                txtOutput.append("Path Length: " + (path.size() - 1) + " connections\n");
                txtOutput.append("Total Nodes: " + path.size() + " criminals\n\n");
                txtOutput.append("🔗 CONNECTION CHAIN:\n");
                txtOutput.append("═══════════════════════════════════════════════\n\n");
                
                // Clear table and show path
                tableModel.setRowCount(0);
                
                for (int i = 0; i < path.size(); i++) {
                    String id = path.get(i);
                    List<String> record = crimeData.get(id);
                    
                    if (record != null) {
                        String suspect = record.get(4);
                        String type = record.get(0);
                        String location = record.get(1);
                        
                        String displayId = id;
                        if (i == 0) displayId += " [START]";
                        else if (i == path.size() - 1) displayId += " [END]";
                        else displayId += " [" + i + "]";
                        
                        Object[] row = {displayId, suspect, type, location, "-"};
                        tableModel.addRow(row);
                        
                        txtOutput.append("  [" + (i + 1) + "] " + id + " - " + suspect + "\n");
                        if (i < path.size() - 1) {
                            txtOutput.append("      ↓ (connected)\n");
                        }
                    }
                }
                
                txtOutput.append("\n═══════════════════════════════════════════════\n");
                txtOutput.append("✅ Path displayed in table →\n");
            }
            
        } catch (Exception ex) {
            handleError("Error finding connection path", ex);
        }
    }
    
    private void showFullNetworkTable() {
        if (crimeData.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please load data first!",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        txtOutput.setText("═══════════════════════════════════════════════\n");
        txtOutput.append("📊 FULL NETWORK TABLE VIEW\n");
        txtOutput.append("═══════════════════════════════════════════════\n\n");
        txtOutput.append("Total Criminals: " + crimeData.size() + "\n");
        txtOutput.append("Total Connections: " + countConnections() + "\n\n");
        txtOutput.append("✅ Full network displayed in table →\n");
        txtOutput.append("   Double-click any row to view connections\n");
        
        populateTable();
    }
    
    private String findCriminalId(String searchTerm) {
        // First try exact match with ID
        if (networkGraph.containsKey(searchTerm)) {
            return searchTerm;
        }
        
        // Try to find by partial ID match
        for (String id : networkGraph.keySet()) {
            if (id.toLowerCase().contains(searchTerm.toLowerCase())) {
                return id;
            }
        }
        
        // Try to find by suspect name
        for (Map.Entry<String, List<String>> entry : crimeData.entrySet()) {
            String suspect = entry.getValue().get(4);
            if (suspect.toLowerCase().contains(searchTerm.toLowerCase())) {
                return entry.getKey();
            }
        }
        
        return null;
    }
    
    private List<String> findPathBFS(String start, String end) {
        if (!networkGraph.containsKey(start) || !networkGraph.containsKey(end)) {
            return null;
        }
        
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(end)) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                String node = end;
                while (node != null) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                return path;
            }
            
            Set<String> neighbors = networkGraph.get(current);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }
        
        return null;
    }
    
    private int countConnections() {
        int count = 0;
        for (Set<String> connections : networkGraph.values()) {
            count += connections.size();
        }
        return count / 2;
    }
    
    private void clearAll() {
        txtSearchCriminal.setText("");
        txtNode1.setText("");
        txtNode2.setText("");
        txtOutput.setText("");
        tableModel.setRowCount(0);
        suggestionPopup.setVisible(false);
        showWelcomeMessage();
    }
    
    private void handleError(String message, Exception ex) {
        txtOutput.append("\n═══════════════════════════════════════════════\n");
        txtOutput.append("❌ ERROR OCCURRED\n");
        txtOutput.append("═══════════════════════════════════════════════\n\n");
        txtOutput.append("Error: " + message + "\n");
        txtOutput.append("Details: " + ex.getMessage() + "\n");
        
        JOptionPane.showMessageDialog(this,
            message + "\n\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}