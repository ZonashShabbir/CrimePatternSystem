/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.gui;

/**
 *
 * @author DELL
 * OPTIMIZED VERSION - Large Table, Small Result Area
 */


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import crimesystem.models.CriminalNetwork;
import crimesystem.integration.CppIntelligenceIntegration;
import crimesystem.integration.JavaNetworkAnalysis;
import crimesystem.integration.CrimeDataLoader;
import crimesystem.integration.SampleFloydGenerator;

public class IntelligencePanel extends JPanel {
    
    private JTable networkTable;
    private DefaultTableModel tableModel;
    private JTextArea resultArea;
    private JButton analyzeButton, clearButton, loadButton, statsButton, floydButton;
    private List<CriminalNetwork> networks;
    
    public IntelligencePanel() {
        networks = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel resultPanel = createResultPanel();
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Network Controls"));
        
        // Load Button - BLUE COLOR
        loadButton = new JButton("Load from Crime Records");
        loadButton.setFont(new Font("Arial", Font.BOLD, 13));
        loadButton.setBackground(new Color(33, 150, 243)); // Blue
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.setBorderPainted(false);
        loadButton.setOpaque(true);
        loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButton.setPreferredSize(new Dimension(200, 35));
        loadButton.addActionListener(e -> loadNetworkFromCrimes());
        panel.add(loadButton);
        
        // Statistics Button - PURPLE COLOR
        statsButton = new JButton("Show Statistics");
        statsButton.setFont(new Font("Arial", Font.BOLD, 13));
        statsButton.setBackground(new Color(156, 39, 176)); // Purple
        statsButton.setForeground(Color.WHITE);
        statsButton.setFocusPainted(false);
        statsButton.setBorderPainted(false);
        statsButton.setOpaque(true);
        statsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statsButton.setPreferredSize(new Dimension(180, 35));
        statsButton.addActionListener(e -> showStatistics());
        panel.add(statsButton);
        
        // Analyze Button - GREEN COLOR
        analyzeButton = new JButton("Analyze Network");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 13));
        analyzeButton.setBackground(new Color(76, 175, 80)); // Green
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFocusPainted(false);
        analyzeButton.setBorderPainted(false);
        analyzeButton.setOpaque(true);
        analyzeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        analyzeButton.setPreferredSize(new Dimension(180, 35));
        analyzeButton.addActionListener(e -> analyzeNetwork());
        panel.add(analyzeButton);
        
        // Clear Button - RED COLOR
        clearButton = new JButton("Clear All");
        clearButton.setFont(new Font("Arial", Font.BOLD, 13));
        clearButton.setBackground(new Color(244, 67, 54)); // Red
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setOpaque(true);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(150, 35));
        clearButton.addActionListener(e -> clearNetwork());
        panel.add(clearButton);
        
        // Floyd Warshall Button - ORANGE COLOR
        floydButton = new JButton("📊 Floyd Warshall");
        floydButton.setFont(new Font("Arial", Font.BOLD, 13));
        floydButton.setBackground(new Color(255, 152, 0)); // Orange
        floydButton.setForeground(Color.WHITE);
        floydButton.setFocusPainted(false);
        floydButton.setBorderPainted(false);
        floydButton.setOpaque(true);
        floydButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        floydButton.setPreferredSize(new Dimension(180, 35));
        floydButton.addActionListener(e -> openFloydWarshallResults());
        panel.add(floydButton);
        
        return panel;
    }
    
    private void openFloydWarshallResults() {
        // First check if file exists
        File file = new File("floyd_warshall_results.txt");
        if(!file.exists()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Floyd Warshall results file not found!\n\n" +
                "Would you like to:\n" +
                "1. Check file location (Yes)\n" +
                "2. Continue anyway (No)\n" +
                "3. Cancel (Cancel)",
                "File Not Found",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if(choice == JOptionPane.YES_OPTION) {
                // Show file checker
                FileChecker.checkFloydWarshallFile();
                return;
            } else if(choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        FloydWarshallResultsPanel floydPanel = new FloydWarshallResultsPanel();
        floydPanel.setVisible(true);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Criminal Network Connections"));
        
        // TABLE COLUMNS
        String[] columns = {"Criminal 1", "Criminal 2", "Connection Strength", "Type"};
        tableModel = new DefaultTableModel(columns, 0);
        networkTable = new JTable(tableModel);
        
        // TABLE STYLING - VERY IMPORTANT!
        networkTable.setRowHeight(24);
        networkTable.setFont(new Font("Arial", Font.PLAIN, 12));
        networkTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        networkTable.setFillsViewportHeight(true);
        
        // TABLE HEADER STYLING
        networkTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        networkTable.getTableHeader().setBackground(new Color(70, 130, 180));
        networkTable.getTableHeader().setForeground(Color.WHITE);
        
        // SCROLL PANE - MAXIMUM SIZE FOR TABLE!
        JScrollPane scrollPane = new JScrollPane(networkTable);
        scrollPane.setPreferredSize(new Dimension(1600, 550)); // VERY LARGE!
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Intelligence Analysis Results"));
        
        // RESULT AREA - COMPACT SIZE!
        resultArea = new JTextArea(8, 70); // SMALL ROWS!
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(1600, 180)); // SMALL HEIGHT!
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadNetworkFromCrimes() {
        resultArea.setText("Loading crime records and generating network...\n");
        
        // Agar aapki CrimeDataLoader mein generateCriminalNetworks() method nahi hai
        // toh ye method add karna hoga CrimeDataLoader.java mein
        networks = CrimeDataLoader.generateCriminalNetworks();
        
        if(networks.isEmpty()) {
            resultArea.append("ERROR: No crime records found or no connections detected!\n");
            JOptionPane.showMessageDialog(this, 
                "No criminal connections found in crime records!", 
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        for(CriminalNetwork network : networks) {
            String type = determineConnectionType(network);
            tableModel.addRow(new Object[]{
                network.getCriminal1(),
                network.getCriminal2(),
                network.getConnectionStrength(),
                type
            });
        }
        
        resultArea.append("✓ Loaded " + networks.size() + " connections\n");
        resultArea.append("✓ Network ready for analysis\n");
        
        JOptionPane.showMessageDialog(this, 
            "Network loaded successfully!\nTotal Connections: " + networks.size(), 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String determineConnectionType(CriminalNetwork network) {
        int strength = network.getConnectionStrength();
        if(strength >= 10) return "Strong";
        if(strength >= 6) return "Medium";
        return "Weak";
    }
    
    private void showStatistics() {
        if(networks.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No network data! Load from crime records first.", 
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== NETWORK STATISTICS ===\n");
        stats.append("Total Connections: ").append(networks.size()).append("\n");
        
        java.util.Set<String> criminals = new java.util.HashSet<>();
        for(CriminalNetwork net : networks) {
            criminals.add(net.getCriminal1());
            criminals.add(net.getCriminal2());
        }
        stats.append("Unique Criminals: ").append(criminals.size()).append("\n");
        
        double avgStrength = networks.stream()
            .mapToInt(CriminalNetwork::getConnectionStrength)
            .average().orElse(0.0);
        stats.append("Average Connection Strength: ")
             .append(String.format("%.2f", avgStrength)).append("\n");
        
        resultArea.setText(stats.toString());
    }
    
    private void analyzeNetwork() {
        if(networks.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No network data to analyze! Load from crime records first.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        resultArea.setText("Running Intelligence Analysis...\n");
        resultArea.append("========================================\n\n");
        
        // Check if C++ is available
        File cppExe = new File("crime_intelligence.exe");
        boolean useCpp = cppExe.exists();
        
        String analysisResult = "";
        
        if(useCpp) {
            // Try C++ analysis
            resultArea.append("🔧 Using C++ implementation...\n\n");
            
            boolean saved = CppIntelligenceIntegration.saveNetworkData(networks);
            if(saved) {
                analysisResult = CppIntelligenceIntegration.runIntelligenceAnalysis();
            }
            
            // Check if C++ worked
            if(analysisResult == null || analysisResult.isEmpty() || 
               analysisResult.contains("ERROR") || analysisResult.contains("????????")) {
                useCpp = false;
                resultArea.append("⚠ C++ analysis failed, switching to Java...\n\n");
            }
        }
        
        if(!useCpp) {
            // Use Java implementation
            resultArea.append("☕ Using Java implementation...\n\n");
            analysisResult = JavaNetworkAnalysis.runCompleteAnalysis(networks);
        }
        
        // Display analysis result
        resultArea.append(analysisResult);
        resultArea.append("\n");
        
        // Generate Floyd Warshall
        File floydFile = new File("floyd_warshall_results.txt");
        boolean floydExists = floydFile.exists() && floydFile.length() > 1000;
        
        if(!floydExists) {
            resultArea.append("\n🔄 Generating Floyd Warshall...\n");
            
            boolean generated = SampleFloydGenerator.generateSampleFile(networks);
            
            if(generated) {
                resultArea.append("✅ Floyd Warshall generated successfully!\n");
                resultArea.append("📊 Click '📊 Floyd Warshall' to view heatmap\n");
            } else {
                resultArea.append("❌ ERROR: Could not generate Floyd Warshall!\n");
            }
        } else {
            resultArea.append("\n✓ Floyd Warshall results available\n");
            resultArea.append("📊 Click '📊 Floyd Warshall' to view heatmap\n");
        }
    }
    
    private void clearNetwork() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Clear all network data?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if(confirm == JOptionPane.YES_OPTION) {
            networks.clear();
            tableModel.setRowCount(0);
            resultArea.setText("");
        }
    }
}