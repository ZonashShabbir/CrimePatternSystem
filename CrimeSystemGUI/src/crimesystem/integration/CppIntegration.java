/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.integration;

/**
 *
 * @author DELL
 */
import java.io.*;
public class CppIntegration {
    // Paths - IMPORTANT: Adjust according to your folder structure
    private static final String PROJECT_ROOT = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\";
    private static final String CPP_PATH = PROJECT_ROOT + "cpp\\";
    private static final String DATA_PATH = PROJECT_ROOT + "data\\";
    
    // Module 1: Crime Manager
    public static String executeCrimeManager(String command) {
        try {
            // Write command to input file
            FileWriter writer = new FileWriter(DATA_PATH + "input.txt");
            writer.write(command + "\n");
            writer.close();
            
            System.out.println("Command written to input.txt");
            
            // Execute C++ program
            String exePath = CPP_PATH + "CrimeManager\\Debug\\CrimeManager.exe";
            ProcessBuilder pb = new ProcessBuilder(exePath);
            pb.directory(new File(CPP_PATH + "CrimeManager\\Debug\\"));
            
            Process process = pb.start();
            
            // Capture console output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder consoleOutput = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                consoleOutput.append(line).append("\n");
                System.out.println("C++: " + line);
            }
            
            // Wait for completion
            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);
            
            if (exitCode == 0) {
                // Read output file
                Thread.sleep(500); // Small delay to ensure file is written
                
                BufferedReader fileReader = new BufferedReader(
                    new FileReader(DATA_PATH + "output.txt"));
                StringBuilder result = new StringBuilder();
                
                while ((line = fileReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                fileReader.close();
                
                return result.toString();
            } else {
                return "Error: C++ program failed with exit code " + exitCode + 
                       "\n\nConsole Output:\n" + consoleOutput.toString();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage() + "\n" + 
                   "Stack trace: " + getStackTrace(e);
        }
    }
    
    // Module 2: Network Finder
    public static String executeNetworkFinder(String command) {
        try {
            // Write command to input file
            FileWriter writer = new FileWriter(DATA_PATH + "network_input.txt", true);
            writer.write(command + "\n");
            writer.close();
            
            System.out.println("Command written to network_input.txt");
            
            // Execute C++ program
            String exePath = CPP_PATH + "NetworkFinder\\Debug\\NetworkFinder.exe";
            ProcessBuilder pb = new ProcessBuilder(exePath);
            pb.directory(new File(CPP_PATH + "NetworkFinder\\Debug\\"));
            
            Process process = pb.start();
            
            // Capture output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null) {
                System.out.println("C++: " + line);
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                Thread.sleep(500);
                
                // Read output file
                BufferedReader fileReader = new BufferedReader(
                    new FileReader(DATA_PATH + "network_output.txt"));
                StringBuilder result = new StringBuilder();
                
                while ((line = fileReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                fileReader.close();
                
                return result.toString();
            } else {
                return "Error: Network Finder failed with exit code " + exitCode;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    // Module 3: Route Optimizer
    public static String executeRouteOptimizer(String command) {
        try {
            // Write command to input file
            FileWriter writer = new FileWriter(DATA_PATH + "route_input.txt", true);
            writer.write(command + "\n");
            writer.close();
            
            System.out.println("Command written to route_input.txt");
            
            // Execute C++ program
            String exePath = CPP_PATH + "RouteOptimizer\\Debug\\RouteOptimizer.exe";
            ProcessBuilder pb = new ProcessBuilder(exePath);
            pb.directory(new File(CPP_PATH + "RouteOptimizer\\Debug\\"));
            
            Process process = pb.start();
            
            // Capture output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null) {
                System.out.println("C++: " + line);
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                Thread.sleep(500);
                
                // Read output file
                BufferedReader fileReader = new BufferedReader(
                    new FileReader(DATA_PATH + "route_output.txt"));
                StringBuilder result = new StringBuilder();
                
                while ((line = fileReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                fileReader.close();
                
                return result.toString();
            } else {
                return "Error: Route Optimizer failed with exit code " + exitCode;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    // Helper: Clear input file before starting new session
    public static void clearInputFile(String filename) {
        try {
            FileWriter writer = new FileWriter(DATA_PATH + filename, false);
            writer.write("");
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }
    
    // Helper: Get stack trace as string
    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    // Test connection
    public static boolean testConnection() {
        File cppDir = new File(CPP_PATH);
        File dataDir = new File(DATA_PATH);
        
        System.out.println("Testing paths...");
        System.out.println("CPP Directory exists: " + cppDir.exists());
        System.out.println("Data Directory exists: " + dataDir.exists());
        
        return cppDir.exists() && dataDir.exists();
    }
}

