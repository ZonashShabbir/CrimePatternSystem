package crimesystem.gui;

import crimesystem.integration.CppIntegration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.util.*;
import java.util.List;

public class CrimeRecordPanel extends JPanel {
    
    private static final String DATA_PATH = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    private static final String CRIMES_FILE = "initial_crimes.txt";
    
    private JTextField txtCrimeID, txtType, txtLocation, txtDate, txtSuspects;
    private JTextArea txtDescription, txtOutput;
    private JButton btnAdd, btnSearch, btnDelete, btnUpdate, btnDisplayAll, btnDisplaySorted, btnLoadData, btnClear;
    private JComboBox<String> cmbSeverity;
    private Set<String> loadedCrimeIds;
    private Map<String, List<String>> cachedCrimes; // Changed to Map like Network Finder
    
    public CrimeRecordPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 245));
        
        loadedCrimeIds = new HashSet<>();
        cachedCrimes = new HashMap<>();
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel title = new JLabel("Crime Record Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.5);
        
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(240, 240, 245));
        
        JPanel inputPanel = createInputPanel();
        leftPanel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        
        JPanel outputPanel = createOutputPanel();
        splitPane.setRightComponent(outputPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        showWelcomeMessage();
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);
        
        fieldsPanel.add(createLabel("Crime ID:"));
        txtCrimeID = new JTextField();
        txtCrimeID.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtCrimeID);
        
        fieldsPanel.add(createLabel("Crime Type:"));
        txtType = new JTextField();
        txtType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtType);
        
        fieldsPanel.add(createLabel("Location:"));
        txtLocation = new JTextField();
        txtLocation.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtLocation);
        
        fieldsPanel.add(createLabel("Date (DD/MM/YYYY):"));
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(Color.WHITE);
        txtDate = new JTextField();
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnToday = new JButton("Today");
        btnToday.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnToday.addActionListener(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtDate.setText(sdf.format(new Date()));
        });
        datePanel.add(txtDate, BorderLayout.CENTER);
        datePanel.add(btnToday, BorderLayout.EAST);
        fieldsPanel.add(datePanel);
        
        fieldsPanel.add(createLabel("Severity (1-10):"));
        String[] severityLevels = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        cmbSeverity = new JComboBox<>(severityLevels);
        cmbSeverity.setSelectedIndex(6);
        cmbSeverity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(cmbSeverity);
        
        fieldsPanel.add(createLabel("Suspects:"));
        txtSuspects = new JTextField();
        txtSuspects.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtSuspects);
        
        fieldsPanel.add(createLabel("Description:"));
        txtDescription = new JTextArea(2, 20);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane descScroll = new JScrollPane(txtDescription);
        fieldsPanel.add(descScroll);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setPreferredSize(new Dimension(700, 100));
        
        btnAdd = createButton("Add", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> addCrime());
        panel.add(btnAdd);
        
        btnSearch = createButton("Search", new Color(52, 152, 219));
        btnSearch.addActionListener(e -> searchCrime());
        panel.add(btnSearch);
        
        btnUpdate = createButton("Update", new Color(241, 196, 15));
        btnUpdate.addActionListener(e -> updateCrime());
        panel.add(btnUpdate);
        
        btnDelete = createButton("Delete", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> deleteCrime());
        panel.add(btnDelete);
        
        btnDisplayAll = createButton("Display All", new Color(155, 89, 182));
        btnDisplayAll.addActionListener(e -> displayAll());
        panel.add(btnDisplayAll);
        
        btnDisplaySorted = createButton("Display Sorted", new Color(230, 126, 34));
        btnDisplaySorted.addActionListener(e -> displaySorted());
        panel.add(btnDisplaySorted);
        
        btnLoadData = createButton("Load initial_crimes.txt", new Color(41, 128, 185));
        btnLoadData.addActionListener(e -> loadCriminalData());
        panel.add(btnLoadData);
        
        btnClear = createButton("Clear", new Color(149, 165, 166));
        btnClear.addActionListener(e -> clearFields());
        panel.add(btnClear);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Output / Results",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtOutput.setBackground(new Color(250, 250, 250));
        txtOutput.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        txtOutput.setLineWrap(false);
        txtOutput.setWrapStyleWord(false);
        
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
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
        txtOutput.append("                   CRIME RECORD MANAGEMENT SYSTEM\n");
        txtOutput.append("===============================================================================\n\n");
        txtOutput.append("INSTRUCTIONS:\n\n");
        txtOutput.append("1. Click 'Load initial_crimes.txt' to load existing crimes\n\n");
        txtOutput.append("2. Fill in the form and click 'Add' to add new crimes\n\n");
        txtOutput.append("3. Use Search, Update, Delete for management\n\n");
        txtOutput.append("4. View all records or sorted by severity\n\n");
        txtOutput.append("Ready to start!\n");
        txtOutput.append("===============================================================================\n");
    }
    
    private String formatAsTable(List<String[]> crimes) {
        StringBuilder table = new StringBuilder();
        
        table.append("+----------+------------------+------------------+----------------------------+----------+\n");
        table.append(String.format("| %-8s | %-16s | %-16s | %-26s | %-8s |\n", 
            "Crime ID", "Suspect", "Crime Type", "Location", "Severity"));
        table.append("+----------+------------------+------------------+----------------------------+----------+\n");
        
        for (String[] crime : crimes) {
            String crimeId = truncate(crime[0], 8);
            String suspect = truncate(crime[1], 16);
            String type = truncate(crime[2], 16);
            String location = truncate(crime[3], 26);
            String severity = truncate(crime[4], 8);
            
            table.append(String.format("| %-8s | %-16s | %-16s | %-26s | %-8s |\n",
                crimeId, suspect, type, location, severity));
        }
        
        table.append("+----------+------------------+------------------+----------------------------+----------+\n");
        
        return table.toString();
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 2) + "..";
    }
    
    // ========== NETWORK FINDER STYLE FAST LOADING ==========
    private void loadCriminalData() {
        try {
            txtOutput.setText("Loading criminal records from initial_crimes.txt...\n\n");
            
            File file = new File(DATA_PATH + CRIMES_FILE);
            
            if (!file.exists()) {
                txtOutput.append("ERROR: File not found!\n");
                txtOutput.append("Path: " + file.getAbsolutePath() + "\n\n");
                txtOutput.append("Please create the file or check the path.\n");
                JOptionPane.showMessageDialog(this,
                    "File not found!\n" + file.getAbsolutePath(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Clear previous data
            loadedCrimeIds.clear();
            cachedCrimes.clear();
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    // Parse: ADD|CrimeID|Type|Location|Date|Severity|Suspect|Description
                    String[] parts = line.split("\\|");
                    
                    if (parts.length >= 7 && parts[0].equals("ADD")) {
                        String crimeId = parts[1].trim();
                        loadedCrimeIds.add(crimeId);
                        
                        // Store complete record in cache
                        List<String> record = new ArrayList<>();
                        record.add(parts[2].trim()); // Type [0]
                        record.add(parts[3].trim()); // Location [1]
                        record.add(parts[4].trim()); // Date [2]
                        record.add(parts[5].trim()); // Severity [3]
                        record.add(parts[6].trim()); // Suspect [4]
                        record.add(parts.length >= 8 ? parts[7].trim() : ""); // Description [5]
                        
                        cachedCrimes.put(crimeId, record);
                        count++;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
            
            reader.close();
            
            if (count == 0) {
                txtOutput.append("ERROR: No data found!\n");
                JOptionPane.showMessageDialog(this,
                    "File is empty or no valid records found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            txtOutput.append("Loaded " + count + " criminal records!\n\n");
            txtOutput.append("===============================================================================\n");
            txtOutput.append("LOADING COMPLETE!\n");
            txtOutput.append("===============================================================================\n");
            txtOutput.append("Total Records: " + count + "\n\n");
            txtOutput.append("Data loaded successfully!\n");
            txtOutput.append("Use 'Display All' to view all records in table format.\n");
            
            JOptionPane.showMessageDialog(this,
                "Successfully loaded " + count + " records!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Automatically display all
            displayAll();
            
        } catch (IOException e) {
            txtOutput.setText("ERROR: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                "Error loading file!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addCrime() {
        if (txtCrimeID.getText().trim().isEmpty() || 
            txtType.getText().trim().isEmpty() ||
            txtLocation.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill Crime ID, Type, and Location!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String id = txtCrimeID.getText().trim();
            String type = txtType.getText().trim();
            String location = txtLocation.getText().trim();
            String date = txtDate.getText().trim();
            
            if (date.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                date = sdf.format(new Date());
            }
            
            int severity = Integer.parseInt((String) cmbSeverity.getSelectedItem());
            String suspects = txtSuspects.getText().trim();
            if (suspects.isEmpty()) suspects = "Unknown";
            
            String description = txtDescription.getText().trim();
            if (description.isEmpty()) description = "No description";
            
            if (loadedCrimeIds.contains(id)) {
                JOptionPane.showMessageDialog(this,
                    "Crime ID '" + id + "' already exists!",
                    "Duplicate Crime ID",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String command = "ADD|" + id + "|" + type + "|" + location + "|" + 
                           date + "|" + severity + "|" + suspects + "|" + description;
            
            txtOutput.setText("Adding crime record...\n\n");
            txtOutput.append("Crime ID: " + id + "\n");
            txtOutput.append("Type: " + type + "\n");
            txtOutput.append("Location: " + location + "\n");
            txtOutput.append("Date: " + date + "\n");
            txtOutput.append("Severity: " + severity + "/10\n");
            txtOutput.append("Suspects: " + suspects + "\n\n");
            
            // Send to C++ backend
            CppIntegration.clearInputFile("input.txt");
            String result = CppIntegration.executeCrimeManager(command);
            txtOutput.append("Backend Response:\n");
            txtOutput.append(result + "\n");
            
            // Save to file
            boolean savedToFile = saveToFile(command);
            
            if (savedToFile) {
                txtOutput.append("\n===============================================================================\n");
                txtOutput.append("SUCCESS!\n");
                txtOutput.append("===============================================================================\n");
                txtOutput.append("Crime added to database and saved to file\n");
                
                loadedCrimeIds.add(id);
                
                // Add to cache
                List<String> record = new ArrayList<>();
                record.add(type);
                record.add(location);
                record.add(date);
                record.add(String.valueOf(severity));
                record.add(suspects);
                record.add(description);
                cachedCrimes.put(id, record);
                
                clearFields();
                
                JOptionPane.showMessageDialog(this,
                    "Crime added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                txtOutput.append("\nWarning: File save failed!\n");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid input!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean saveToFile(String command) {
        try {
            File file = new File(DATA_PATH + CRIMES_FILE);
            
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            FileWriter writer = new FileWriter(file, true);
            writer.write(command + "\n");
            writer.close();
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void searchCrime() {
        String id = txtCrimeID.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter Crime ID!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cachedCrimes.containsKey(id)) {
            List<String> record = cachedCrimes.get(id);
            txtOutput.setText("SEARCH RESULT\n\n");
            txtOutput.append("Crime ID: " + id + "\n");
            txtOutput.append("Type: " + record.get(0) + "\n");
            txtOutput.append("Location: " + record.get(1) + "\n");
            txtOutput.append("Date: " + record.get(2) + "\n");
            txtOutput.append("Severity: " + record.get(3) + "\n");
            txtOutput.append("Suspect: " + record.get(4) + "\n");
            txtOutput.append("Description: " + record.get(5) + "\n");
        } else {
            txtOutput.setText("Crime ID '" + id + "' not found.\n");
            txtOutput.append("\nPlease load data first or check the Crime ID.\n");
        }
    }
    
    private void updateCrime() {
        if (txtCrimeID.getText().trim().isEmpty() || 
            txtType.getText().trim().isEmpty() ||
            txtLocation.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String id = txtCrimeID.getText().trim();
            String type = txtType.getText().trim();
            String location = txtLocation.getText().trim();
            String date = txtDate.getText().trim();
            int severity = Integer.parseInt((String) cmbSeverity.getSelectedItem());
            String suspects = txtSuspects.getText().trim();
            String description = txtDescription.getText().trim();
            
            String command = "UPDATE|" + id + "|" + type + "|" + location + "|" + 
                           date + "|" + severity + "|" + suspects + "|" + description;
            
            CppIntegration.clearInputFile("input.txt");
            
            txtOutput.setText("Updating crime: " + id + "\n\n");
            String result = CppIntegration.executeCrimeManager(command);
            txtOutput.append(result);
            
            if (result.contains("SUCCESS") || result.contains("updated")) {
                updateInFile(id, command.replace("UPDATE", "ADD"));
                
                // Update in cache
                List<String> record = new ArrayList<>();
                record.add(type);
                record.add(location);
                record.add(date);
                record.add(String.valueOf(severity));
                record.add(suspects);
                record.add(description);
                cachedCrimes.put(id, record);
                
                txtOutput.append("\nUpdated successfully!\n");
                JOptionPane.showMessageDialog(this,
                    "Updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateInFile(String crimeId, String newCommand) {
        try {
            File file = new File(DATA_PATH + CRIMES_FILE);
            if (!file.exists()) return;
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("|" + crimeId + "|")) {
                    content.append(newCommand).append("\n");
                } else {
                    content.append(line).append("\n");
                }
            }
            reader.close();
            
            FileWriter writer = new FileWriter(file);
            writer.write(content.toString());
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void deleteCrime() {
        String id = txtCrimeID.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Crime ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete crime '" + id + "'?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String command = "DELETE|" + id;
            CppIntegration.clearInputFile("input.txt");
            
            txtOutput.setText("Deleting crime: " + id + "\n\n");
            String result = CppIntegration.executeCrimeManager(command);
            txtOutput.append(result);
            
            if (result.contains("SUCCESS") || result.contains("deleted")) {
                deleteFromFile(id);
                loadedCrimeIds.remove(id);
                cachedCrimes.remove(id);
                
                txtOutput.append("\nDeleted successfully!\n");
                clearFields();
                JOptionPane.showMessageDialog(this, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void deleteFromFile(String crimeId) {
        try {
            File file = new File(DATA_PATH + CRIMES_FILE);
            if (!file.exists()) return;
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.contains("|" + crimeId + "|")) {
                    content.append(line).append("\n");
                }
            }
            reader.close();
            
            FileWriter writer = new FileWriter(file);
            writer.write(content.toString());
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void displayAll() {
        txtOutput.setText("ALL CRIME RECORDS\n\n");
        
        if (cachedCrimes.isEmpty()) {
            txtOutput.append("No crimes found.\n");
            txtOutput.append("Click 'Load initial_crimes.txt' first.\n");
        } else {
            List<String[]> crimeList = new ArrayList<>();
            
            for (Map.Entry<String, List<String>> entry : cachedCrimes.entrySet()) {
                String crimeId = entry.getKey();
                List<String> record = entry.getValue();
                
                String[] crimeData = {
                    crimeId,           // Crime ID
                    record.get(4),     // Suspect
                    record.get(0),     // Type
                    record.get(1),     // Location
                    record.get(3)      // Severity
                };
                crimeList.add(crimeData);
            }
            
            txtOutput.append(formatAsTable(crimeList));
            txtOutput.append("\nTotal Records: " + cachedCrimes.size() + "\n");
        }
    }
    
    private void displaySorted() {
        txtOutput.setText("CRIMES SORTED BY SEVERITY (High to Low)\n\n");
        
        if (cachedCrimes.isEmpty()) {
            txtOutput.append("No crimes found.\n");
            txtOutput.append("Click 'Load initial_crimes.txt' first.\n");
        } else {
            List<String[]> crimeList = new ArrayList<>();
            
            for (Map.Entry<String, List<String>> entry : cachedCrimes.entrySet()) {
                String crimeId = entry.getKey();
                List<String> record = entry.getValue();
                
                String[] crimeData = {
                    crimeId,           // Crime ID
                    record.get(4),     // Suspect
                    record.get(0),     // Type
                    record.get(1),     // Location
                    record.get(3)      // Severity
                };
                crimeList.add(crimeData);
            }
            
            // Sort by severity (descending)
            crimeList.sort((a, b) -> {
                try {
                    int severityA = Integer.parseInt(a[4]);
                    int severityB = Integer.parseInt(b[4]);
                    return Integer.compare(severityB, severityA);
                } catch (NumberFormatException e) {
                    return 0;
                }
            });
            
            txtOutput.append(formatAsTable(crimeList));
            txtOutput.append("\nTotal Records: " + cachedCrimes.size() + "\n");
        }
    }
    
    private void clearFields() {
        txtCrimeID.setText("");
        txtType.setText("");
        txtLocation.setText("");
        txtDate.setText("");
        cmbSeverity.setSelectedIndex(6);
        txtSuspects.setText("");
        txtDescription.setText("");
    }
}