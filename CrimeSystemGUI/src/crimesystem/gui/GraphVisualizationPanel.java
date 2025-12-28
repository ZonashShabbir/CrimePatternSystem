/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.gui;

/**
 *
 * @author DELL
 */


import crimesystem.integration.CrimeDataLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * GraphVisualizationPanel - Visual representation of crime network
 * Displays nodes as circles and connections as lines
 */
public class GraphVisualizationPanel extends JPanel {
    
    private Map<String, Set<String>> networkGraph;
    private Map<String, List<String>> crimeData;
    private Map<String, Point> nodePositions;
    private String highlightedNode;
    private List<String> highlightedPath;
    private boolean showFullNetwork;
    
    private static final int NODE_RADIUS = 30;
    private static final Color NODE_COLOR = new Color(52, 152, 219);
    private static final Color HIGHLIGHT_COLOR = new Color(231, 76, 60);
    private static final Color PATH_COLOR = new Color(46, 204, 113);
    private static final Color EDGE_COLOR = new Color(189, 195, 199);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public GraphVisualizationPanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        networkGraph = new HashMap<>();
        crimeData = new HashMap<>();
        nodePositions = new HashMap<>();
        highlightedPath = new ArrayList<>();
        showFullNetwork = false;
    }
    
    public void setNetworkData(Map<String, Set<String>> graph, Map<String, List<String>> data) {
        this.networkGraph = graph;
        this.crimeData = data;
        this.showFullNetwork = true;
        calculateNodePositions();
        repaint();
    }
    
    public void highlightNode(String nodeId) {
        this.highlightedNode = nodeId;
        this.highlightedPath.clear();
        this.showFullNetwork = false;
        repaint();
    }
    
    public void highlightPath(List<String> path) {
        this.highlightedPath = new ArrayList<>(path);
        this.highlightedNode = null;
        this.showFullNetwork = false;
        repaint();
    }
    
    public void showFullNetwork() {
        this.showFullNetwork = true;
        this.highlightedNode = null;
        this.highlightedPath.clear();
        repaint();
    }
    
    public void clear() {
        networkGraph.clear();
        crimeData.clear();
        nodePositions.clear();
        highlightedNode = null;
        highlightedPath.clear();
        showFullNetwork = false;
        repaint();
    }
    
    private void calculateNodePositions() {
        nodePositions.clear();
        
        if (networkGraph.isEmpty()) return;
        
        List<String> nodes = new ArrayList<>(networkGraph.keySet());
        int numNodes = nodes.size();
        
        // Use circular layout for better visualization
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 80;
        
        if (numNodes == 1) {
            nodePositions.put(nodes.get(0), new Point(centerX, centerY));
            return;
        }
        
        double angleStep = 2 * Math.PI / numNodes;
        
        for (int i = 0; i < numNodes; i++) {
            double angle = i * angleStep;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            nodePositions.put(nodes.get(i), new Point(x, y));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (networkGraph.isEmpty()) {
            drawEmptyState(g2d);
            return;
        }
        
        // Recalculate positions if panel size changed
        if (nodePositions.isEmpty()) {
            calculateNodePositions();
        }
        
        // Draw based on current mode
        if (!highlightedPath.isEmpty()) {
            drawPathVisualization(g2d);
        } else if (highlightedNode != null) {
            drawNodeVisualization(g2d);
        } else if (showFullNetwork) {
            drawFullNetwork(g2d);
        } else {
            drawEmptyState(g2d);
        }
    }
    
    private void drawEmptyState(Graphics2D g2d) {
        g2d.setColor(new Color(149, 165, 166));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        String msg1 = "No Network Data";
        String msg2 = "Load data to visualize network";
        
        FontMetrics fm = g2d.getFontMetrics();
        int x1 = (getWidth() - fm.stringWidth(msg1)) / 2;
        int x2 = (getWidth() - fm.stringWidth(msg2)) / 2;
        int y = getHeight() / 2;
        
        g2d.drawString(msg1, x1, y - 10);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        x2 = (getWidth() - fm.stringWidth(msg2)) / 2;
        g2d.drawString(msg2, x2, y + 15);
    }
    
    private void drawFullNetwork(Graphics2D g2d) {
        // Draw title
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("Full Crime Network", 10, 25);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Nodes: " + networkGraph.size() + " | Connections: " + countEdges(), 10, 45);
        
        // Draw all edges first (so they appear behind nodes)
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(EDGE_COLOR);
        
        Set<String> drawnEdges = new HashSet<>();
        
        for (Map.Entry<String, Set<String>> entry : networkGraph.entrySet()) {
            String node1 = entry.getKey();
            Point p1 = nodePositions.get(node1);
            
            if (p1 == null) continue;
            
            for (String node2 : entry.getValue()) {
                String edgeKey = createEdgeKey(node1, node2);
                
                if (!drawnEdges.contains(edgeKey)) {
                    Point p2 = nodePositions.get(node2);
                    if (p2 != null) {
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                        drawnEdges.add(edgeKey);
                    }
                }
            }
        }
        
        // Draw all nodes
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String nodeId = entry.getKey();
            Point pos = entry.getValue();
            
            drawNode(g2d, nodeId, pos.x, pos.y, NODE_COLOR, false);
        }
    }
    
    private void drawNodeVisualization(Graphics2D g2d) {
        // Draw title
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("Criminal Network View", 10, 25);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String suspect = CrimeDataLoader.getSuspectName(highlightedNode, crimeData);
        g2d.drawString("Suspect: " + suspect, 10, 45);
        
        // Get connections for highlighted node
        Set<String> connections = networkGraph.get(highlightedNode);
        
        if (connections == null || connections.isEmpty()) {
            g2d.setColor(new Color(149, 165, 166));
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.drawString("No connections for this criminal", getWidth() / 2 - 100, getHeight() / 2);
            return;
        }
        
        // Calculate positions for star layout (center node + surrounding nodes)
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Point centerPos = new Point(centerX, centerY);
        
        List<String> connectedNodes = new ArrayList<>(connections);
        int numConnections = connectedNodes.size();
        int radius = 150;
        
        // Draw edges
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(EDGE_COLOR);
        
        for (int i = 0; i < numConnections; i++) {
            double angle = (2 * Math.PI * i) / numConnections;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            
            g2d.drawLine(centerX, centerY, x, y);
        }
        
        // Draw center node (highlighted)
        drawNode(g2d, highlightedNode, centerX, centerY, HIGHLIGHT_COLOR, true);
        
        // Draw connected nodes
        for (int i = 0; i < numConnections; i++) {
            double angle = (2 * Math.PI * i) / numConnections;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            
            drawNode(g2d, connectedNodes.get(i), x, y, NODE_COLOR, false);
        }
        
        // Draw legend
        drawLegend(g2d, "Red = Selected Criminal, Blue = Connected Criminals");
    }
    
    private void drawPathVisualization(Graphics2D g2d) {
        // Draw title
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("Connection Path", 10, 25);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Path Length: " + (highlightedPath.size() - 1) + " connections", 10, 45);
        
        if (highlightedPath.size() < 2) return;
        
        // Calculate positions for linear path layout
        int startX = 80;
        int startY = getHeight() / 2;
        int spacing = (getWidth() - 160) / (highlightedPath.size() - 1);
        
        // Draw path edges
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(PATH_COLOR);
        
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            int x1 = startX + (i * spacing);
            int x2 = startX + ((i + 1) * spacing);
            
            g2d.drawLine(x1, startY, x2, startY);
            
            // Draw arrow
            drawArrow(g2d, x1, startY, x2, startY);
        }
        
        // Draw path nodes
        for (int i = 0; i < highlightedPath.size(); i++) {
            int x = startX + (i * spacing);
            Color nodeColor = (i == 0 || i == highlightedPath.size() - 1) ? HIGHLIGHT_COLOR : PATH_COLOR;
            
            drawNode(g2d, highlightedPath.get(i), x, startY, nodeColor, i == 0 || i == highlightedPath.size() - 1);
        }
        
        // Draw legend
        drawLegend(g2d, "Red = Start/End, Green = Intermediate Path");
    }
    
    private void drawNode(Graphics2D g2d, String nodeId, int x, int y, Color color, boolean highlight) {
        // Draw outer circle if highlighted
        if (highlight) {
            g2d.setColor(color.brighter());
            g2d.fillOval(x - NODE_RADIUS - 5, y - NODE_RADIUS - 5, 
                        (NODE_RADIUS + 5) * 2, (NODE_RADIUS + 5) * 2);
        }
        
        // Draw node circle
        g2d.setColor(color);
        g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw node label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
        
        String label = nodeId;
        if (label.length() > 8) {
            label = label.substring(0, 8) + "..";
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int labelHeight = fm.getHeight();
        
        g2d.drawString(label, x - labelWidth / 2, y + labelHeight / 4);
        
        // Draw suspect name below node
        if (crimeData.containsKey(nodeId)) {
            String suspect = CrimeDataLoader.getSuspectName(nodeId, crimeData);
            if (suspect.length() > 12) {
                suspect = suspect.substring(0, 12) + "..";
            }
            
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            fm = g2d.getFontMetrics();
            labelWidth = fm.stringWidth(suspect);
            g2d.drawString(suspect, x - labelWidth / 2, y + NODE_RADIUS + 15);
        }
    }
    
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int arrowSize = 10;
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        
        int[] xPoints = {
            midX,
            midX - (int)(arrowSize * Math.cos(angle - Math.PI / 6)),
            midX - (int)(arrowSize * Math.cos(angle + Math.PI / 6))
        };
        
        int[] yPoints = {
            midY,
            midY - (int)(arrowSize * Math.sin(angle - Math.PI / 6)),
            midY - (int)(arrowSize * Math.sin(angle + Math.PI / 6))
        };
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawLegend(Graphics2D g2d, String text) {
        g2d.setColor(new Color(236, 240, 241));
        g2d.fillRoundRect(10, getHeight() - 40, getWidth() - 20, 30, 10, 10);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2d.drawString(text, 20, getHeight() - 20);
    }
    
    private String createEdgeKey(String node1, String node2) {
        return node1.compareTo(node2) < 0 ? node1 + "-" + node2 : node2 + "-" + node1;
    }
    
    private int countEdges() {
        int count = 0;
        for (Set<String> connections : networkGraph.values()) {
            count += connections.size();
        }
        return count / 2;
    }
}

