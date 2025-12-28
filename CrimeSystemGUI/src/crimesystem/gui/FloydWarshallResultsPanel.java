package crimesystem.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Floyd Warshall Results - COMPLETE FIX
 * - Full header names visible
 * - Detailed search with categorized connections
 */
public class FloydWarshallResultsPanel extends JFrame {
    
    private JTable heatmapTable;
    private DefaultTableModel tableModel;
    private JTextArea statsArea;
    private JTextField searchField;
    private int[][] distances;
    private String[] criminals;
    private JLabel statusLabel;
    private Map<String, Integer> keyFindings;
    
    public FloydWarshallResultsPanel() {
        setTitle("Floyd Warshall Algorithm - Shortest Paths Heatmap 🎨");
        setSize(1500, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        keyFindings = new HashMap<>();
        loadFloydWarshallData();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 250));
        
        // Top Panel
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center - Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(950);
        
        JPanel heatmapPanel = createHeatmapPanel();
        splitPane.setLeftComponent(heatmapPanel);
        
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom
        statusLabel = new JLabel("✅ Floyd Warshall Results Loaded: " + criminals.length + " criminals");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 128, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("🔍 Floyd Warshall - Shortest Paths Analysis");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search Criminal:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.addActionListener(e -> searchCriminal());
        searchPanel.add(searchField);
        
        JButton searchBtn = new JButton("🔍");
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setForeground(new Color(70, 130, 180));
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchCriminal());
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createHeatmapPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel header = new JLabel("📊 Heatmap Matrix - Connection Distances");
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(header, BorderLayout.NORTH);
        
        int displaySize = Math.min(50, criminals.length);
        
        Vector<String> columns = new Vector<>();
        columns.add("Criminal");
        for(int i = 0; i < displaySize; i++) {
            columns.add(criminals[i]);
        }
        
        Vector<Vector<Object>> data = new Vector<>();
        for(int i = 0; i < displaySize; i++) {
            Vector<Object> row = new Vector<>();
            row.add(criminals[i]);
            for(int j = 0; j < displaySize; j++) {
                row.add(distances[i][j] == Integer.MAX_VALUE ? "∞" : distances[i][j]);
            }
            data.add(row);
        }
        
        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        heatmapTable = new JTable(tableModel);
        heatmapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        heatmapTable.setRowHeight(24);
        heatmapTable.setFont(new Font("Arial", Font.BOLD, 10));
        heatmapTable.setGridColor(new Color(220, 220, 220));
        
        // Column widths
        heatmapTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for(int i = 1; i < heatmapTable.getColumnCount(); i++) {
            heatmapTable.getColumnModel().getColumn(i).setPreferredWidth(40);
        }
        
        // Heatmap colors
        heatmapTable.setDefaultRenderer(Object.class, new HeatmapCellRenderer());
        
        // FIXED HEADER - Taller with wrapped text
        JTableHeader headerTable = heatmapTable.getTableHeader();
        headerTable.setPreferredSize(new Dimension(headerTable.getPreferredSize().width, 60)); // TALLER!
        headerTable.setBackground(Color.WHITE);
        headerTable.setForeground(Color.BLACK);
        headerTable.setFont(new Font("Arial", Font.BOLD, 8));
        
        // Custom header renderer for wrapped text
        headerTable.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JTextArea textArea = new JTextArea(value != null ? value.toString() : "");
                textArea.setFont(new Font("Arial", Font.BOLD, 8));
                textArea.setForeground(Color.BLACK);
                textArea.setBackground(Color.WHITE);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(180, 180, 180)));
                
                return textArea;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(heatmapTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel note = new JLabel("📌 Showing " + displaySize + "x" + displaySize + " criminals. Total: " + 
            criminals.length + ". Use search for details.");
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        note.setForeground(new Color(100, 100, 100));
        note.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(note, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(500, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        tabbedPane.addTab("🎨 Legend", createLegendPanel());
        tabbedPane.addTab("📊 Statistics", createStatsPanel());
        tabbedPane.addTab("🔑 Key Findings", createKeyFindingsPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Color Code Meaning:");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        
        addLegendItem(panel, "Self Connection (0)", new Color(255, 255, 255), "Same criminal");
        addLegendItem(panel, "Very Strong (1-3)", new Color(139, 0, 0), "Direct connection");
        addLegendItem(panel, "Strong (4-6)", new Color(220, 20, 60), "Close network");
        addLegendItem(panel, "Medium (7-9)", new Color(255, 140, 0), "2-3 steps away");
        addLegendItem(panel, "Medium-Weak (10-12)", new Color(255, 215, 0), "Moderate distance");
        addLegendItem(panel, "Weak (13-15)", new Color(173, 255, 47), "Far connection");
        addLegendItem(panel, "Very Weak (16-20)", new Color(144, 238, 144), "Very far");
        addLegendItem(panel, "Extremely Weak (21+)", new Color(240, 255, 240), "Distant");
        
        panel.add(Box.createVerticalStrut(20));
        
        JTextArea explanation = new JTextArea();
        explanation.setText("💡 How to Read:\n\n" +
            "• Lower numbers (RED) = Stronger connections\n" +
            "• Higher numbers (GREEN) = Weaker connections\n" +
            "• Number shows minimum steps between criminals\n" +
            "• 0 means same person\n" +
            "• ∞ means no connection possible");
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setFont(new Font("Arial", Font.PLAIN, 11));
        explanation.setBackground(new Color(255, 255, 220));
        explanation.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 100)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        explanation.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(explanation);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addLegendItem(JPanel panel, String text, Color color, String description) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(40, 25));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel desc = new JLabel(description);
        desc.setFont(new Font("Arial", Font.ITALIC, 10));
        desc.setForeground(Color.GRAY);
        
        textPanel.add(label);
        textPanel.add(desc);
        
        item.add(colorBox, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        
        panel.add(item);
        panel.add(Box.createVerticalStrut(8));
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("📈 Network Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        statsArea.setBackground(new Color(250, 250, 250));
        statsArea.setLineWrap(false);
        
        calculateStatistics();
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createKeyFindingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("🔑 Important Criminals");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        JTextArea findingsArea = new JTextArea();
        findingsArea.setEditable(false);
        findingsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        findingsArea.setBackground(new Color(255, 250, 240));
        findingsArea.setLineWrap(true);
        findingsArea.setWrapStyleWord(true);
        
        StringBuilder findings = new StringBuilder();
        findings.append("=== TOP 10 MOST CONNECTED ===\n\n");
        findings.append("(Lower total distance = More central)\n\n");
        
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(keyFindings.entrySet());
        sorted.sort(Map.Entry.comparingByValue());
        
        int rank = 1;
        for(int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            findings.append(String.format("%d. %s\n", rank++, entry.getKey()));
            findings.append(String.format("   Total Distance: %d\n", entry.getValue()));
            findings.append(String.format("   Status: %s\n\n", 
                entry.getValue() < 800 ? "⭐ HIGHLY CENTRAL" : 
                entry.getValue() < 1000 ? "✓ CENTRAL" : "○ MODERATE"));
        }
        
        findings.append("\n=== ANALYSIS ===\n\n");
        findings.append("These criminals have shortest paths\n");
        findings.append("to all other criminals in network.\n\n");
        findings.append("They are likely:\n");
        findings.append("• Key connectors\n");
        findings.append("• Hub nodes\n");
        findings.append("• Important figures\n");
        
        findingsArea.setText(findings.toString());
        
        JScrollPane scrollPane = new JScrollPane(findingsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void calculateStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("═══════════════════════════════\n");
        stats.append("  FLOYD WARSHALL STATISTICS\n");
        stats.append("═══════════════════════════════\n\n");
        
        stats.append("Total Criminals: ").append(criminals.length).append("\n");
        stats.append("Total Possible Paths: ")
             .append(criminals.length * criminals.length).append("\n\n");
        
        long totalDistance = 0;
        int pathCount = 0;
        int veryStrong = 0, strong = 0, medium = 0, weak = 0, veryWeak = 0;
        
        int maxDistance = 0, minDistance = Integer.MAX_VALUE;
        String maxPair = "", minPair = "";
        
        for(int i = 0; i < distances.length; i++) {
            for(int j = 0; j < distances[i].length; j++) {
                if(i != j && distances[i][j] != Integer.MAX_VALUE) {
                    int dist = distances[i][j];
                    totalDistance += dist;
                    pathCount++;
                    
                    if(dist <= 3) veryStrong++;
                    else if(dist <= 6) strong++;
                    else if(dist <= 12) medium++;
                    else if(dist <= 20) weak++;
                    else veryWeak++;
                    
                    if(dist > maxDistance) {
                        maxDistance = dist;
                        maxPair = criminals[i] + " ↔ " + criminals[j];
                    }
                    if(dist < minDistance && dist > 0) {
                        minDistance = dist;
                        minPair = criminals[i] + " ↔ " + criminals[j];
                    }
                }
            }
        }
        
        double avgDistance = pathCount > 0 ? (double)totalDistance / pathCount : 0;
        
        stats.append("─── Connection Strength ───\n");
        stats.append(String.format("Very Strong (1-3):  %5d\n", veryStrong));
        stats.append(String.format("Strong (4-6):       %5d\n", strong));
        stats.append(String.format("Medium (7-12):      %5d\n", medium));
        stats.append(String.format("Weak (13-20):       %5d\n", weak));
        stats.append(String.format("Very Weak (21+):    %5d\n\n", veryWeak));
        
        stats.append("─── Distance Stats ───\n");
        stats.append(String.format("Average Distance: %.2f\n", avgDistance));
        stats.append(String.format("Shortest Path: %d\n", minDistance));
        stats.append(String.format("Longest Path: %d\n\n", maxDistance));
        
        stats.append("─── Extreme Cases ───\n");
        stats.append("Closest Pair:\n  " + minPair + "\n  Distance: " + minDistance + "\n\n");
        stats.append("Farthest Pair:\n  " + maxPair + "\n  Distance: " + maxDistance + "\n\n");
        
        stats.append("─── Network Properties ───\n");
        stats.append("Network Diameter: " + maxDistance + "\n");
        stats.append("(Maximum shortest path)\n\n");
        
        stats.append("Average Connections:\n");
        stats.append(String.format("  %.1f steps to reach\n", avgDistance));
        stats.append("  any criminal\n");
        
        statsArea.setText(stats.toString());
    }
    
    private void loadFloydWarshallData() {
        try {
            File file = new File("floyd_warshall_results.txt");
            if(!file.exists()) {
                JOptionPane.showMessageDialog(this, 
                    "Floyd Warshall results file not found!\n" +
                    "Please run the analysis first from Intelligence Panel.", 
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
                criminals = new String[]{"C0"};
                distances = new int[][]{{0}};
                return;
            }
            
            List<String> criminalsList = new ArrayList<>();
            List<List<Integer>> distancesList = new ArrayList<>();
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean foundHeader = false;
            boolean inMatrix = false;
            
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty()) continue;
                
                if(line.startsWith("From/To")) {
                    foundHeader = true;
                    inMatrix = true;
                    
                    String[] headers = line.split("\t");
                    for(int i = 1; i < headers.length; i++) {
                        String name = headers[i].trim();
                        if(!name.isEmpty()) {
                            criminalsList.add(name);
                        }
                    }
                    continue;
                }
                
                if(foundHeader && inMatrix) {
                    if(line.startsWith("Key Findings:") || line.contains("Max distance") || !line.contains("\t")) {
                        inMatrix = false;
                        break;
                    }
                    
                    String[] parts = line.split("\t");
                    if(parts.length > 1) {
                        List<Integer> rowDistances = new ArrayList<>();
                        
                        for(int i = 1; i < parts.length && rowDistances.size() < criminalsList.size(); i++) {
                            String value = parts[i].trim();
                            try {
                                if(value.equals("INF") || value.equals("∞") || value.isEmpty()) {
                                    rowDistances.add(Integer.MAX_VALUE);
                                } else {
                                    rowDistances.add(Integer.parseInt(value));
                                }
                            } catch(NumberFormatException e) {
                                rowDistances.add(Integer.MAX_VALUE);
                            }
                        }
                        
                        if(rowDistances.size() == criminalsList.size()) {
                            distancesList.add(rowDistances);
                        }
                    }
                }
            }
            
            reader.close();
            
            if(criminalsList.isEmpty() || distancesList.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Could not parse Floyd Warshall results!",
                    "Parse Error", JOptionPane.ERROR_MESSAGE);
                criminals = new String[]{"C0"};
                distances = new int[][]{{0}};
                return;
            }
            
            criminals = criminalsList.toArray(new String[0]);
            distances = new int[distancesList.size()][criminalsList.size()];
            
            for(int i = 0; i < distancesList.size(); i++) {
                List<Integer> row = distancesList.get(i);
                for(int j = 0; j < row.size(); j++) {
                    distances[i][j] = row.get(j);
                }
            }
            
            for(int i = 0; i < criminals.length && i < distances.length; i++) {
                int totalDist = 0;
                for(int j = 0; j < distances[i].length; j++) {
                    if(distances[i][j] != Integer.MAX_VALUE) {
                        totalDist += distances[i][j];
                    }
                }
                keyFindings.put(criminals[i], totalDist);
            }
            
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error reading Floyd Warshall results:\n" + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            criminals = new String[]{"C0"};
            distances = new int[][]{{0}};
        }
    }
    
    private void searchCriminal() {
        String searchText = searchField.getText().trim();
        if(searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a criminal name!", 
                "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int foundIndex = -1;
        String foundName = "";
        
        for(int i = 0; i < criminals.length; i++) {
            if(criminals[i].toLowerCase().contains(searchText.toLowerCase())) {
                foundIndex = i;
                foundName = criminals[i];
                break;
            }
        }
        
        if(foundIndex == -1) {
            statusLabel.setText("❌ Not found: " + searchText);
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Criminal not found: " + searchText, 
                "Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // DETAILED REPORT - SHOW ALL CONNECTIONS
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════\n");
        report.append("           CONNECTION REPORT\n");
        report.append("═══════════════════════════════════════════════════════\n\n");
        
        report.append("Criminal: ").append(foundName).append("\n");
        report.append("Position: ").append(foundIndex + 1).append(" / ").append(criminals.length).append("\n");
        report.append("─────────────────────────────────────────────────────\n\n");
        
        // Categorize by strength
        List<String> veryStrong = new ArrayList<>();
        List<String> strong = new ArrayList<>();
        List<String> medium = new ArrayList<>();
        List<String> weak = new ArrayList<>();
        
        for(int i = 0; i < distances[foundIndex].length; i++) {
            if(foundIndex != i && distances[foundIndex][i] != Integer.MAX_VALUE) {
                int dist = distances[foundIndex][i];
                String conn = String.format("  %-25s : %2d steps", criminals[i], dist);
                
                if(dist <= 3) veryStrong.add(conn);
                else if(dist <= 6) strong.add(conn);
                else if(dist <= 12) medium.add(conn);
                else weak.add(conn);
            }
        }
        
        // Display ALL connections in each category
        if(!veryStrong.isEmpty()) {
            report.append("🔴 VERY STRONG (1-3 steps): ").append(veryStrong.size()).append(" connections\n");
            report.append("─────────────────────────────────────────────────────\n");
            for(String conn : veryStrong) {
                report.append(conn).append("\n");
            }
            report.append("\n");
        }
        
        if(!strong.isEmpty()) {
            report.append("🟠 STRONG (4-6 steps): ").append(strong.size()).append(" connections\n");
            report.append("─────────────────────────────────────────────────────\n");
            for(String conn : strong) {
                report.append(conn).append("\n");
            }
            report.append("\n");
        }
        
        if(!medium.isEmpty()) {
            report.append("🟡 MEDIUM (7-12 steps): ").append(medium.size()).append(" connections\n");
            report.append("─────────────────────────────────────────────────────\n");
            for(String conn : medium) {
                report.append(conn).append("\n");
            }
            report.append("\n");
        }
        
        if(!weak.isEmpty()) {
            report.append("🟢 WEAK (13+ steps): ").append(weak.size()).append(" connections\n");
            report.append("─────────────────────────────────────────────────────\n");
            for(String conn : weak) {
                report.append(conn).append("\n");
            }
            report.append("\n");
        }
        
        // SUMMARY
        int total = veryStrong.size() + strong.size() + medium.size() + weak.size();
        report.append("═══════════════════════════════════════════════════════\n");
        report.append("                    SUMMARY\n");
        report.append("═══════════════════════════════════════════════════════\n\n");
        report.append(String.format("Total Connections:        %3d\n", total));
        report.append(String.format("  🔴 Very Strong (1-3):   %3d (%.1f%%)\n", 
            veryStrong.size(), 100.0 * veryStrong.size() / total));
        report.append(String.format("  🟠 Strong (4-6):        %3d (%.1f%%)\n", 
            strong.size(), 100.0 * strong.size() / total));
        report.append(String.format("  🟡 Medium (7-12):       %3d (%.1f%%)\n", 
            medium.size(), 100.0 * medium.size() / total));
        report.append(String.format("  🟢 Weak (13+):          %3d (%.1f%%)\n", 
            weak.size(), 100.0 * weak.size() / total));
        report.append("\n");
        report.append("═══════════════════════════════════════════════════════\n");
        report.append("         Complete connection list displayed\n");
        report.append("═══════════════════════════════════════════════════════\n");
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        textArea.setBackground(new Color(250, 250, 250));
        textArea.setLineWrap(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 800)); // MUCH LARGER!
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Custom dialog with better sizing
        JDialog dialog = new JDialog(this, "🔍 Criminal: " + foundName, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton copyBtn = new JButton("📋 Copy Report");
        copyBtn.setFont(new Font("Arial", Font.BOLD, 12));
        copyBtn.addActionListener(e -> {
            textArea.selectAll();
            textArea.copy();
            JOptionPane.showMessageDialog(dialog, "Report copied to clipboard!");
        });
        buttonPanel.add(copyBtn);
        
        JButton closeBtn = new JButton("✓ Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(700, 850);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        statusLabel.setText("✓ " + foundName + " - " + total + " connections");
        statusLabel.setForeground(new Color(0, 128, 0));
        
        // Scroll to criminal (works for ALL criminals now)
        if(foundIndex < heatmapTable.getRowCount()) {
            heatmapTable.setRowSelectionInterval(foundIndex, foundIndex);
            heatmapTable.scrollRectToVisible(heatmapTable.getCellRect(foundIndex, 0, true));
        }
    }
    
    class HeatmapCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            if(column == 0) {
                c.setBackground(new Color(240, 248, 255));
                c.setForeground(new Color(0, 0, 128));
                setFont(new Font("Arial", Font.BOLD, 11));
            } else {
                int distance = 0;
                if(value instanceof Integer) {
                    distance = (Integer) value;
                } else if(value instanceof String) {
                    String str = (String) value;
                    if(str.equals("∞")) {
                        c.setBackground(Color.LIGHT_GRAY);
                        c.setForeground(Color.DARK_GRAY);
                        setHorizontalAlignment(CENTER);
                        return c;
                    }
                    try {
                        distance = Integer.parseInt(str);
                    } catch(NumberFormatException e) {
                        distance = 0;
                    }
                }
                
                Color bgColor = getHeatmapColor(distance);
                c.setBackground(bgColor);
                
                if(distance == 0) {
                    c.setForeground(Color.LIGHT_GRAY);
                    setFont(new Font("Arial", Font.PLAIN, 10));
                } else if(distance <= 6) {
                    c.setForeground(Color.WHITE);
                    setFont(new Font("Arial", Font.BOLD, 11));
                } else {
                    c.setForeground(Color.BLACK);
                    setFont(new Font("Arial", Font.PLAIN, 11));
                }
            }
            
            setHorizontalAlignment(CENTER);
            return c;
        }
        
        private Color getHeatmapColor(int distance) {
            if(distance == 0) return new Color(255, 255, 255);
            else if(distance <= 3) return new Color(139, 0, 0);
            else if(distance <= 6) return new Color(220, 20, 60);
            else if(distance <= 9) return new Color(255, 140, 0);
            else if(distance <= 12) return new Color(255, 215, 0);
            else if(distance <= 15) return new Color(173, 255, 47);
            else if(distance <= 20) return new Color(144, 238, 144);
            else return new Color(240, 255, 240);
        }
    }
}