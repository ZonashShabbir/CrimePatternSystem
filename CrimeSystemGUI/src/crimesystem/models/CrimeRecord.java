/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.models;

/**
 *
 * @author DELL
 */
public class CrimeRecord {
    
    private String crimeID;
    private String crimeType;
    private String location;
    private String date;
    private int severity;
    private String suspects;
    private String description;
    
    // Default constructor
    public CrimeRecord() {
        this.severity = 0;
    }
    
    // Parameterized constructor
    public CrimeRecord(String id, String type, String loc, String date, 
                       int sev, String susp, String desc) {
        this.crimeID = id;
        this.crimeType = type;
        this.location = loc;
        this.date = date;
        this.severity = sev;
        this.suspects = susp;
        this.description = desc;
    }
    
    // Getters
    public String getCrimeID() { return crimeID; }
    public String getCrimeType() { return crimeType; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public int getSeverity() { return severity; }
    public String getSuspects() { return suspects; }
    public String getDescription() { return description; }
    
    // Setters
    public void setCrimeID(String crimeID) { this.crimeID = crimeID; }
    public void setCrimeType(String crimeType) { this.crimeType = crimeType; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setSeverity(int severity) { this.severity = severity; }
    public void setSuspects(String suspects) { this.suspects = suspects; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "Crime ID: " + crimeID + ", Type: " + crimeType + 
               ", Location: " + location + ", Severity: " + severity;
    }
}

