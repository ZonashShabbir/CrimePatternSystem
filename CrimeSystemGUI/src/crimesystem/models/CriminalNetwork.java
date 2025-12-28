/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crimesystem.models;

/**
 *
 * @author DELL
 */
public class CriminalNetwork {
   
    private String criminal1;
    private String criminal2;
    private int connectionStrength;
    
    public CriminalNetwork(String c1, String c2, int strength) {
        this.criminal1 = c1;
        this.criminal2 = c2;
        this.connectionStrength = strength;
    }
    
    public String getCriminal1() { 
        return criminal1; 
    }
    
    public String getCriminal2() { 
        return criminal2; 
    }
    
    public int getConnectionStrength() { 
        return connectionStrength; 
    }
    
    public void setCriminal1(String criminal1) { 
        this.criminal1 = criminal1; 
    }
    
    public void setCriminal2(String criminal2) { 
        this.criminal2 = criminal2; 
    }
    
    public void setConnectionStrength(int strength) { 
        this.connectionStrength = strength; 
    }
    
    @Override
    public String toString() {
        return criminal1 + " <--> " + criminal2 + 
               " (Strength: " + connectionStrength + ")";
    }
}

