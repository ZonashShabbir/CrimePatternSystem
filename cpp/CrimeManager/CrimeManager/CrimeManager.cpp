// CrimeManager.cpp - SIMPLE WORKING VERSION
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include "crime_record.h"
#include "hash_table.h"
#include "avl_tree.h"

using namespace std;

// Global data structures
HashTable crimeDB;
AVLTree crimeBySeverity;

// Function prototypes
void processCommands();
void writeOutput(const string& message);
string trim(const string& str);

int main() {
    cout << "Crime Manager Starting..." << endl;
    
    // Process commands
    processCommands();
    
    // Write output
    writeOutput("Success");
    
    cout << "Crime Manager Completed!" << endl;
    
    return 0;
}

// Trim function
string trim(const string& str) {
    size_t first = str.find_first_not_of(" \t\n\r");
    if (first == string::npos) return "";
    size_t last = str.find_last_not_of(" \t\n\r");
    return str.substr(first, last - first + 1);
}

// Process commands
void processCommands() {
    string dataPath = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    ifstream inputFile(dataPath + "input.txt");
    
    if (!inputFile.is_open()) {
        cout << "No commands to process." << endl;
        return;
    }
    
    string line;
    
    while (getline(inputFile, line)) {
        if (line.empty()) continue;
        
        stringstream ss(line);
        string command;
        getline(ss, command, '|');
        command = trim(command);
        
        if (command == "ADD") {
            string id, type, location, date, suspects, description;
            int severity;
            
            getline(ss, id, '|');
            getline(ss, type, '|');
            getline(ss, location, '|');
            getline(ss, date, '|');
            
            string sevStr;
            getline(ss, sevStr, '|');
            severity = stoi(trim(sevStr));
            
            getline(ss, suspects, '|');
            getline(ss, description);
            
            id = trim(id);
            type = trim(type);
            location = trim(location);
            date = trim(date);
            suspects = trim(suspects);
            description = trim(description);
            
            CrimeRecord crime(id, type, location, date, severity, suspects, description);
            crimeDB.insert(crime);
            crimeBySeverity.insert(crime);
        }
        else if (command == "SEARCH") {
            string id;
            getline(ss, id);
            id = trim(id);
            
            CrimeRecord* crime = crimeDB.search(id);
            if (crime != nullptr) {
                crime->display();
            }
        }
        else if (command == "DELETE") {
            string id;
            getline(ss, id);
            id = trim(id);
            
            crimeDB.remove(id);
        }
        else if (command == "DISPLAY_ALL") {
            crimeDB.displayAll();
        }
        else if (command == "DISPLAY_SORTED") {
            crimeBySeverity.displaySorted();
        }
    }
    
    inputFile.close();
}

// Write output
void writeOutput(const string& message) {
    string dataPath = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\";
    ofstream outputFile(dataPath + "output.txt");
    
    if (!outputFile.is_open()) {
        return;
    }
    
    outputFile << "CRIME MANAGEMENT SYSTEM" << endl;
    outputFile << "Total crimes: " << crimeDB.getSize() << endl;
    outputFile << "Status: " << message << endl;
    outputFile << endl;
    
    if (!crimeDB.isEmpty()) {
        vector<CrimeRecord> allCrimes = crimeDB.getAllRecords();
        for (size_t i = 0; i < allCrimes.size(); i++) {
            outputFile << "[" << (i + 1) << "] ";
            outputFile << "ID: " << allCrimes[i].crimeID << " | ";
            outputFile << "Type: " << allCrimes[i].crimeType << " | ";
            outputFile << "Location: " << allCrimes[i].location << endl;
        }
    }
    
    outputFile.close();
}