package crimesystem.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class CrimeStatisticsPanel extends JPanel {
    
    private static final String DATA_PATH = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    private static final String CRIMES_FILE = "initial_crimes.txt";
    private static final String EXPORT_FILE = "crime_analysis_report.txt";
    
    private JButton btnLoadData, btnRefresh, btnExport;
    private JTextArea txtSummary;
    private SimpleBarChartPanel cityChartPanel;
    private SimpleBarChartPanel crimeTypeChartPanel;
    
    private Map<String, Integer> cityWiseCrimes;
    private Map<String, Integer> crimeTypeCounts;
    private Map<String, Map<String, Integer>> cityWiseCrimeTypes;
    private int totalCrimes = 0;
    
    public CrimeStatisticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 245));
        
        cityWiseCrimes = new LinkedHashMap<>();
        crimeTypeCounts = new LinkedHashMap<>();
        cityWiseCrimeTypes = new LinkedHashMap<>();
        
        // Title Panel
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 110, 250), getWidth(), 0, new Color(237, 100, 166));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        JLabel title = new JLabel("📊 Crime Statistics Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        // Control Panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        // Main content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel cityPanel = createChartPanel("City-wise Crime Statistics", true);
        tabbedPane.addTab("  📍 Cities  ", cityPanel);
        
        JPanel crimeTypePanel = createChartPanel("Crime Type Distribution", false);
        tabbedPane.addTab("  🔍 Crime Types  ", crimeTypePanel);
        
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("  📋 Summary  ", summaryPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        showWelcomeMessage();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        btnLoadData = createModernButton("📥 Load Data", new Color(99, 110, 250));
        btnLoadData.addActionListener(e -> loadCrimeData());
        panel.add(btnLoadData);
        
        btnRefresh = createModernButton("🔄 Refresh", new Color(16, 185, 129));
        btnRefresh.addActionListener(e -> refreshCharts());
        panel.add(btnRefresh);
        
        btnExport = createModernButton("💾 Export", new Color(245, 158, 11));
        btnExport.addActionListener(e -> exportToFile());
        panel.add(btnExport);
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 45));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });
        
        return button;
    }
    
    private JPanel createChartPanel(String title, boolean isCity) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JLabel chartTitle = new JLabel(title, SwingConstants.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        chartTitle.setForeground(new Color(31, 41, 55));
        panel.add(chartTitle, BorderLayout.NORTH);
        
        if (isCity) {
            cityChartPanel = new SimpleBarChartPanel("Cities");
            panel.add(cityChartPanel, BorderLayout.CENTER);
        } else {
            crimeTypeChartPanel = new SimpleBarChartPanel("Crime Types");
            panel.add(crimeTypeChartPanel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JLabel summaryTitle = new JLabel("📊 Detailed Analysis Report", SwingConstants.CENTER);
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        summaryTitle.setForeground(new Color(31, 41, 55));
        panel.add(summaryTitle, BorderLayout.NORTH);
        
        txtSummary = new JTextArea();
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtSummary.setBackground(new Color(249, 250, 251));
        txtSummary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(txtSummary);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showWelcomeMessage() {
        txtSummary.setText("════════════════════════════════════════════════════════════════════════\n");
        txtSummary.append("           CRIME STATISTICS & ANALYTICS DASHBOARD\n");
        txtSummary.append("════════════════════════════════════════════════════════════════════════\n\n");
        txtSummary.append("Welcome! Professional bar chart visualization.\n\n");
        txtSummary.append("Click '📥 Load Data' to generate colorful bar charts!\n");
    }
    
    private void loadCrimeData() {
        try {
            File file = new File(DATA_PATH + CRIMES_FILE);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cityWiseCrimes.clear();
            crimeTypeCounts.clear();
            cityWiseCrimeTypes.clear();
            totalCrimes = 0;
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7 && parts[0].equals("ADD")) {
                        String crimeType = parts[2].trim();
                        String city = parts[3].trim().split(" ")[0];
                        
                        cityWiseCrimes.put(city, cityWiseCrimes.getOrDefault(city, 0) + 1);
                        crimeTypeCounts.put(crimeType, crimeTypeCounts.getOrDefault(crimeType, 0) + 1);
                        
                        cityWiseCrimeTypes.putIfAbsent(city, new HashMap<>());
                        cityWiseCrimeTypes.get(city).put(crimeType, 
                            cityWiseCrimeTypes.get(city).getOrDefault(crimeType, 0) + 1);
                        
                        totalCrimes++;
                    }
                } catch (Exception e) { }
            }
            reader.close();
            
            cityWiseCrimes = sortByValue(cityWiseCrimes);
            crimeTypeCounts = sortByValue(crimeTypeCounts);
            
            cityChartPanel.setData(cityWiseCrimes);
            crimeTypeChartPanel.setData(crimeTypeCounts);
            
            generateSummary();
            
            JOptionPane.showMessageDialog(this, 
                "✅ Loaded " + totalCrimes + " records successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        Map<String, Integer> sorted = new LinkedHashMap<>();
        list.forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }
    
    private void generateSummary() {
        StringBuilder s = new StringBuilder();
        s.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        s.append("║              CRIME DATA ANALYSIS REPORT                                      ║\n");
        s.append("╚══════════════════════════════════════════════════════════════════════════════╝\n\n");
        
        s.append("📊 OVERALL STATISTICS\n");
        s.append("════════════════════════════════════════════════════════════════════════════════\n");
        s.append(String.format("  Total Crimes Recorded: %d\n", totalCrimes));
        s.append(String.format("  Cities Affected: %d\n", cityWiseCrimes.size()));
        s.append(String.format("  Crime Categories: %d\n\n", crimeTypeCounts.size()));
        
        s.append("🔝 TOP 5 HIGH-CRIME CITIES\n");
        s.append("════════════════════════════════════════════════════════════════════════════════\n");
        int rank = 1;
        for (Map.Entry<String, Integer> e : cityWiseCrimes.entrySet()) {
            if (rank > 5) break;
            double pct = (e.getValue() * 100.0) / totalCrimes;
            String bar = "[" + "█".repeat((int)(pct/2)) + " ".repeat(50 - (int)(pct/2)) + "]";
            s.append(String.format("  %d. %-15s: %3d crimes (%.1f%%) %s\n", 
                rank++, e.getKey(), e.getValue(), pct, bar));
        }
        
        s.append("\n🔍 TOP 5 CRIME TYPES\n");
        s.append("════════════════════════════════════════════════════════════════════════════════\n");
        rank = 1;
        for (Map.Entry<String, Integer> e : crimeTypeCounts.entrySet()) {
            if (rank > 5) break;
            double pct = (e.getValue() * 100.0) / totalCrimes;
            String bar = "[" + "█".repeat((int)(pct/2)) + " ".repeat(50 - (int)(pct/2)) + "]";
            s.append(String.format("  %d. %-20s: %3d cases (%.1f%%) %s\n", 
                rank++, e.getKey(), e.getValue(), pct, bar));
        }
        
        s.append("\n📍 CITY-WISE DETAILED BREAKDOWN\n");
        s.append("════════════════════════════════════════════════════════════════════════════════\n");
        
        for (Map.Entry<String, Integer> city : cityWiseCrimes.entrySet()) {
            s.append(String.format("\n  🏙️ %s (%d total crimes)\n", city.getKey(), city.getValue()));
            s.append("  ────────────────────────────────────────────────────────────────────────\n");
            
            Map<String, Integer> types = cityWiseCrimeTypes.get(city.getKey());
            if (types != null) {
                sortByValue(types).forEach((type, count) -> 
                    s.append(String.format("     • %-20s: %2d\n", type, count)));
            }
        }
        
        s.append("\n════════════════════════════════════════════════════════════════════════════════\n");
        s.append("✅ Analysis Complete | " + new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").format(new Date()) + "\n");
        s.append("════════════════════════════════════════════════════════════════════════════════\n");
        
        txtSummary.setText(s.toString());
        txtSummary.setCaretPosition(0);
    }
    
    private void exportToFile() {
        if (cityWiseCrimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            FileWriter writer = new FileWriter(DATA_PATH + EXPORT_FILE);
            writer.write(txtSummary.getText());
            writer.close();
            
            JOptionPane.showMessageDialog(this,
                "✅ Report exported!\n\nFile: " + EXPORT_FILE + "\nLocation: " + DATA_PATH,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshCharts() {
        if (cityWiseCrimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Load data first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cityChartPanel.repaint();
        crimeTypeChartPanel.repaint();
        JOptionPane.showMessageDialog(this, "Charts refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // SIMPLE VERTICAL BAR CHART - EXACTLY LIKE SAMPLE IMAGES
    class SimpleBarChartPanel extends JPanel {
        private Map<String, Integer> data = new LinkedHashMap<>();
        private String chartType;
        
        // Vibrant colors for bars
        private final Color[] BAR_COLORS = {
            new Color(255, 99, 132),   // Pink/Red
            new Color(54, 162, 235),   // Blue
            new Color(255, 206, 86),   // Yellow
            new Color(75, 192, 192),   // Teal
            new Color(153, 102, 255),  // Purple
            new Color(255, 159, 64),   // Orange
            new Color(46, 204, 113),   // Green
            new Color(231, 76, 60),    // Red
            new Color(52, 152, 219),   // Light Blue
            new Color(155, 89, 182)    // Violet
        };
        
        public SimpleBarChartPanel(String type) {
            this.chartType = type;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        }
        
        public void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            if (data.isEmpty()) {
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.setColor(new Color(150, 150, 150));
                String msg = "No data available";
                int w = g2d.getFontMetrics().stringWidth(msg);
                g2d.drawString(msg, (getWidth() - w) / 2, getHeight() / 2);
                return;
            }
            
            int width = getWidth();
            int height = getHeight();
            int padding = 80;
            int chartHeight = height - padding - 80;
            int chartWidth = width - 2 * padding;
            
            // Calculate bar dimensions
            int numBars = data.size();
            int barWidth = Math.min(80, (chartWidth / numBars) - 20);
            int totalBarsWidth = (barWidth * numBars) + (20 * (numBars - 1));
            int startX = padding + (chartWidth - totalBarsWidth) / 2;
            
            int maxValue = Collections.max(data.values());
            
            // Draw horizontal grid lines
            g2d.setColor(new Color(240, 240, 240));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i <= 5; i++) {
                int y = height - padding - (chartHeight * i / 5);
                g2d.drawLine(padding, y, width - padding, y);
                
                // Y-axis values
                g2d.setColor(new Color(120, 120, 120));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String value = String.valueOf(maxValue * i / 5);
                g2d.drawString(value, padding - 35, y + 4);
                g2d.setColor(new Color(240, 240, 240));
            }
            
            // Draw axes
            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
            g2d.drawLine(padding, 40, padding, height - padding); // Y-axis
            
            // Draw Y-axis label
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2d.setColor(new Color(70, 70, 70));
            g2d.drawString("Crime Count", 10, 60);
            
            // Draw bars
            int x = startX;
            int colorIndex = 0;
            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String label = entry.getKey();
                int value = entry.getValue();
                double percentage = (value * 100.0) / total;
                
                // Calculate bar height
                int barHeight = (int) ((value / (double) maxValue) * chartHeight);
                int barY = height - padding - barHeight;
                
                // Get bar color
                Color barColor = BAR_COLORS[colorIndex % BAR_COLORS.length];
                
                // Draw bar with solid color
                g2d.setColor(barColor);
                g2d.fillRect(x, barY, barWidth, barHeight);
                
                // Draw bar border
                g2d.setColor(barColor.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x, barY, barWidth, barHeight);
                
                // Draw value on top of bar
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(new Color(40, 40, 40));
                String valueStr = String.valueOf(value);
                int valueWidth = g2d.getFontMetrics().stringWidth(valueStr);
                g2d.drawString(valueStr, x + (barWidth - valueWidth) / 2, barY - 10);
                
                // Draw percentage below value
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2d.setColor(new Color(100, 100, 100));
                String pctStr = String.format("(%.1f%%)", percentage);
                int pctWidth = g2d.getFontMetrics().stringWidth(pctStr);
                g2d.drawString(pctStr, x + (barWidth - pctWidth) / 2, barY - 25);
                
                // Draw label below X-axis
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2d.setColor(new Color(60, 60, 60));
                String displayLabel = label.length() > 10 ? label.substring(0, 9) + ".." : label;
                int labelWidth = g2d.getFontMetrics().stringWidth(displayLabel);
                g2d.drawString(displayLabel, x + (barWidth - labelWidth) / 2, height - padding + 20);
                
                x += barWidth + 20;
                colorIndex++;
            }
            
            // Draw chart title at bottom
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2d.setColor(new Color(70, 70, 70));
            String title = chartType;
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, height - 15);
        }
    }
}