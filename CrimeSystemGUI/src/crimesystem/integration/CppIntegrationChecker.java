package crimesystem.integration;

import java.io.*;

/**
 * Check C++ Integration Issues
 */
public class CppIntegrationChecker {
    
    public static String checkCppIntegration() {
        StringBuilder report = new StringBuilder();
        report.append("=== C++ INTEGRATION CHECK ===\n\n");
        
        // Check 1: C++ executable
        File cppExe = new File("crime_intelligence.exe");
        report.append("1. C++ Executable:\n");
        report.append("   File: crime_intelligence.exe\n");
        report.append("   Exists: ").append(cppExe.exists()).append("\n");
        if(cppExe.exists()) {
            report.append("   Size: ").append(cppExe.length()).append(" bytes\n");
            report.append("   Path: ").append(cppExe.getAbsolutePath()).append("\n");
        }
        report.append("\n");
        
        // Check 2: Input file
        File inputFile = new File("network_data.txt");
        report.append("2. Input File:\n");
        report.append("   File: network_data.txt\n");
        report.append("   Exists: ").append(inputFile.exists()).append("\n");
        if(inputFile.exists()) {
            report.append("   Size: ").append(inputFile.length()).append(" bytes\n");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                int lines = 0;
                while(reader.readLine() != null && lines < 5) {
                    lines++;
                }
                reader.close();
                report.append("   Lines: ").append(lines).append("+ (showing first 5)\n");
            } catch(IOException e) {
                report.append("   Error reading: ").append(e.getMessage()).append("\n");
            }
        }
        report.append("\n");
        
        // Check 3: Try running C++
        report.append("3. Try Running C++:\n");
        try {
            Process process = Runtime.getRuntime().exec("crime_intelligence.exe");
            
            // Wait for completion (max 10 seconds)
            boolean completed = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            
            if(completed) {
                int exitCode = process.exitValue();
                report.append("   Status: Completed\n");
                report.append("   Exit Code: ").append(exitCode).append("\n");
                
                // Read output
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                String line;
                int outputLines = 0;
                report.append("   Output (first 5 lines):\n");
                while((line = reader.readLine()) != null && outputLines < 5) {
                    report.append("     ").append(line).append("\n");
                    outputLines++;
                }
                
                if(outputLines == 0) {
                    report.append("     (No output)\n");
                }
            } else {
                report.append("   Status: Timeout (took more than 10 seconds)\n");
                process.destroy();
            }
        } catch(IOException e) {
            report.append("   Status: Failed\n");
            report.append("   Error: ").append(e.getMessage()).append("\n");
            report.append("   Reason: C++ executable not found or cannot run\n");
        } catch(InterruptedException e) {
            report.append("   Status: Interrupted\n");
        }
        report.append("\n");
        
        // Check 4: Output files
        report.append("4. Output Files:\n");
        String[] outputFiles = {
            "intelligence_report.txt",
            "floyd_warshall_results.txt",
            "kruskal_results.txt"
        };
        
        for(String filename : outputFiles) {
            File file = new File(filename);
            report.append("   - ").append(filename).append(": ");
            if(file.exists()) {
                report.append("✓ (").append(file.length()).append(" bytes)\n");
            } else {
                report.append("✗ Not found\n");
            }
        }
        report.append("\n");
        
        // Recommendations
        report.append("=== RECOMMENDATIONS ===\n");
        if(!cppExe.exists()) {
            report.append("❌ C++ executable not found!\n");
            report.append("   Solution: Use Java implementation instead\n");
        } else {
            report.append("✓ C++ executable exists\n");
            report.append("   Try running manually to check for errors\n");
        }
        
        return report.toString();
    }
}