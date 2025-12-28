package crimesystem.integration;

import java.io.*;
import java.util.List;
import crimesystem.models.CriminalNetwork;

public class CppIntelligenceIntegration {
    
    private static final String BASE_PATH = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\";
    
    // C++ .exe path
    private static final String CPP_EXE_PATH = 
        BASE_PATH + "cpp\\CriminalIntelligence\\Debug\\CriminalIntelligence.exe";
    
    // Files DATA FOLDER mein save karo (jahan initial_crimes.txt hai)
    private static final String NETWORK_FILE = 
        BASE_PATH + "data\\criminal_network.txt";
    
    private static final String OUTPUT_FILE = 
        BASE_PATH + "data\\intelligence_output.txt";
    
    public static boolean saveNetworkData(List<CriminalNetwork> networks) {
        try {
            File file = new File(NETWORK_FILE);
            
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            for(CriminalNetwork network : networks) {
                writer.println(network.getCriminal1() + "|" + 
                             network.getCriminal2() + "|" + 
                             network.getConnectionStrength());
            }
            writer.close();
            
            System.out.println("✓ Saved to: " + NETWORK_FILE);
            return true;
            
        } catch(IOException e) {
            System.err.println("Error saving: " + e.getMessage());
            return false;
        }
    }
    
    public static String runIntelligenceAnalysis() {
        try {
            File exeFile = new File(CPP_EXE_PATH);
            
            if (!exeFile.exists()) {
                return "ERROR: C++ executable not found at:\n" + CPP_EXE_PATH;
            }
            
            // Run C++ with data folder as working directory
            ProcessBuilder pb = new ProcessBuilder(CPP_EXE_PATH);
            pb.directory(new File(BASE_PATH + "data\\"));
            
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            
            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            StringBuilder errors = new StringBuilder();
            while((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            
            if(exitCode == 0) {
                return readOutputFile();
            } else {
                return "ERROR: C++ failed (Exit Code: " + exitCode + ")\n\n" +
                       "Console Output:\n" + output.toString() + "\n" +
                       "Errors:\n" + errors.toString() + "\n\n" +
                       "Files Location:\n" +
                       "- Input: " + NETWORK_FILE + "\n" +
                       "- Output: " + OUTPUT_FILE;
            }
            
        } catch(Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private static String readOutputFile() {
        File outputFile = new File(OUTPUT_FILE);
        
        if (!outputFile.exists()) {
            return "ERROR: Output file not created at:\n" + OUTPUT_FILE;
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(outputFile))) {
            String line;
            while((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch(IOException e) {
            return "Error reading output: " + e.getMessage();
        }
    }
}
