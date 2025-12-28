package crimesystem.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Utility to check Floyd Warshall file
 */
public class FileChecker {
    
    public static void checkFloydWarshallFile() {
        StringBuilder report = new StringBuilder();
        report.append("=== FLOYD WARSHALL FILE CHECK ===\n\n");
        
        // Check current directory
        String currentDir = System.getProperty("user.dir");
        report.append("Current Directory:\n  ").append(currentDir).append("\n\n");
        
        // Check if file exists
        File file = new File("floyd_warshall_results.txt");
        report.append("File: floyd_warshall_results.txt\n");
        report.append("Exists: ").append(file.exists()).append("\n");
        
        if(file.exists()) {
            report.append("Size: ").append(file.length()).append(" bytes\n");
            report.append("Absolute Path:\n  ").append(file.getAbsolutePath()).append("\n\n");
            
            // Try to read first few lines
            report.append("--- First 10 Lines ---\n");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for(int i = 0; i < 10; i++) {
                    String line = reader.readLine();
                    if(line == null) break;
                    report.append(i+1).append(": ").append(line).append("\n");
                }
                reader.close();
            } catch(IOException e) {
                report.append("ERROR reading file: ").append(e.getMessage()).append("\n");
            }
        } else {
            report.append("❌ FILE NOT FOUND!\n\n");
            report.append("Expected location:\n  ").append(file.getAbsolutePath()).append("\n\n");
            report.append("Solutions:\n");
            report.append("1. Run 'Analyze Network' first\n");
            report.append("2. Check if C++ program generated the file\n");
            report.append("3. Check project root directory\n");
        }
        
        // Show in dialog
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(null, scrollPane, 
            "Floyd Warshall File Check", JOptionPane.INFORMATION_MESSAGE);
        
        // Print to console
        System.out.println(report.toString());
    }
}