/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.gui;

/**
 *
 * @author DELL
 */


import crimesystem.integration.CppIntegration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private CrimeRecordPanel crimeRecordPanel;
    private NetworkFinderPanel networkFinderPanel;
    private RouteOptimizerPanel routeOptimizerPanel;
    
    public MainFrame() {
        // Window settings
        setTitle("Crime Pattern Record and Network Finder System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon (optional)
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, skip
        }
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        
        // Create panels
        crimeRecordPanel = new CrimeRecordPanel();
        networkFinderPanel = new NetworkFinderPanel();
        routeOptimizerPanel = new RouteOptimizerPanel();
        
        
        // Add tabs
        tabbedPane.addTab("  📋 Crime Records  ", crimeRecordPanel);
        tabbedPane.addTab("  🔗 Network Finder  ", networkFinderPanel);
        tabbedPane.addTab("  🗺️ Route Optimizer  ", routeOptimizerPanel);
        tabbedPane.addTab("Intelligence", new IntelligencePanel());
        tabbedPane.addTab("Crime Statistics", new CrimeStatisticsPanel());
         
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Create menu bar
        createMenuBar();
        
        // Check C++ connection on startup
        checkCppConnection();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(getWidth(), 80));
        panel.setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Crime Pattern Record and Network Finder");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Integrated Java GUI + C++ Data Structures & Algorithms");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(44, 62, 80));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(52, 73, 94));
        panel.setPreferredSize(new Dimension(getWidth(), 40));
        
        JLabel footerLabel = new JLabel("© 2024 Crime Management System | DSA Project | Version 1.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(189, 195, 199));
        
        panel.add(footerLabel);
        
        return panel;
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(exitItem);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem testConnectionItem = new JMenuItem("Test C++ Connection");
        testConnectionItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        testConnectionItem.addActionListener(e -> checkCppConnection());
        
        JMenuItem clearAllDataItem = new JMenuItem("Clear All Data Files");
        clearAllDataItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clearAllDataItem.addActionListener(e -> clearAllDataFiles());
        
        toolsMenu.add(testConnectionItem);
        toolsMenu.addSeparator();
        toolsMenu.add(clearAllDataItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem crimeRecordsItem = new JMenuItem("Crime Records");
        crimeRecordsItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        crimeRecordsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
        crimeRecordsItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem networkFinderItem = new JMenuItem("Network Finder");
        networkFinderItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        networkFinderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
        networkFinderItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JMenuItem routeOptimizerItem = new JMenuItem("Route Optimizer");
        routeOptimizerItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        routeOptimizerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK));
        routeOptimizerItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        viewMenu.add(crimeRecordsItem);
        viewMenu.add(networkFinderItem);
        viewMenu.add(routeOptimizerItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        aboutItem.addActionListener(e -> showAbout());
        
        JMenuItem userGuideItem = new JMenuItem("User Guide");
        userGuideItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userGuideItem.addActionListener(e -> showUserGuide());
        
        helpMenu.add(userGuideItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void checkCppConnection() {
        boolean connected = CppIntegration.testConnection();
        
        if (connected) {
            JOptionPane.showMessageDialog(this,
                "✓ C++ Backend Connection Successful!\n\n" +
                "All modules are ready to use:\n" +
                "• Crime Manager (Hash Table + AVL Tree)\n" +
                "• Network Finder (Graph + BFS/DFS)\n" +
                "• Route Optimizer (Dijkstra + Min-Heap)",
                "Connection Test",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "✗ C++ Backend Connection Failed!\n\n" +
                "Please check:\n" +
                "1. C++ programs are compiled (Debug folder exists)\n" +
                "2. Paths in CppIntegration.java are correct\n" +
                "3. Data folder exists with required .txt files",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearAllDataFiles() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will clear all data files:\n" +
            "• input.txt\n" +
            "• output.txt\n" +
            "• network_input.txt\n" +
            "• network_output.txt\n" +
            "• route_input.txt\n" +
            "• route_output.txt\n\n" +
            "Are you sure?",
            "Clear All Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            CppIntegration.clearInputFile("input.txt");
            CppIntegration.clearInputFile("output.txt");
            CppIntegration.clearInputFile("network_input.txt");
            CppIntegration.clearInputFile("network_output.txt");
            CppIntegration.clearInputFile("route_input.txt");
            CppIntegration.clearInputFile("route_output.txt");
            
            JOptionPane.showMessageDialog(this,
                "All data files cleared successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showAbout() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Crime Pattern Record and Network Finder");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea infoArea = new JTextArea(
            "\nA unified system combining:\n\n" +
            "Frontend (Java):\n" +
            "• Swing GUI with 3 modules\n" +
            "• File-based integration layer\n" +
            "• Real-time data visualization\n\n" +
            "Backend (C++):\n" +
            "• Hash Table (Crime storage)\n" +
            "• AVL Tree (Sorted indexing)\n" +
            "• Graph with BFS/DFS (Network analysis)\n" +
            "• Dijkstra's Algorithm with Min-Heap (Route optimization)\n\n" +
            "Technologies:\n" +
            "• Java Swing (NetBeans)\n" +
            "• C++ STL (Visual Studio)\n" +
            "• File I/O integration\n\n" +
            "Developed by: [Your Group Names]\n" +
            "Course: Data Structures & Algorithms\n" +
            "Institution: [Your University]"
        );
        infoArea.setEditable(false);
        infoArea.setBackground(panel.getBackground());
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(versionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(infoArea);
        
        JOptionPane.showMessageDialog(this,
            panel,
            "About",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void showUserGuide() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("User Guide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea guideArea = new JTextArea(
            "\nModule 1: Crime Records\n" +
            "• Add new crime records with details\n" +
            "• Search by Crime ID\n" +
            "• Update existing records\n" +
            "• Delete records\n" +
            "• View all records (Hash Table)\n" +
            "• View sorted by severity (AVL Tree)\n\n" +
            
            "Module 2: Network Finder\n" +
            "• Add nodes (crimes, suspects, locations)\n" +
            "• Connect nodes to build network\n" +
            "• Find shortest connection path (BFS)\n" +
            "• Visualize network structure\n\n" +
            
            "Module 3: Route Optimizer\n" +
            "• Add locations with crime counts\n" +
            "• Define routes with distances\n" +
            "• Find shortest patrol route (Dijkstra)\n" +
            "• View crime hotspots\n\n" +
            
            "Keyboard Shortcuts:\n" +
            "• Ctrl+1: Crime Records\n" +
            "• Ctrl+2: Network Finder\n" +
            "• Ctrl+3: Route Optimizer\n" +
            "• Ctrl+Q: Exit\n" +
            "• F1: About"
        );
        guideArea.setEditable(false);
        guideArea.setBackground(panel.getBackground());
        guideArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        guideArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(guideArea);
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "User Guide",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Application closed by user.");
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
    




}

