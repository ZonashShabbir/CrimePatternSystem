// NetworkFinder.cpp - Module 2 Main Program
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include "graph.h"
#include "crime_network.h"

using namespace std;

// Global objects
Graph crimeGraph;
CrimeNetwork networkData;

// Function prototypes
void processCommands();
void writeOutput(const string& message);
string trim(const string& str);

int main() {
    cout << "╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║       NETWORK FINDER - C++ BACKEND                   ║" << endl;
    cout << "║       Module 2: Crime Network Analysis               ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;
    cout << endl;

    cout << "Starting Network Finder..." << endl;
    cout << "Processing commands from input file..." << endl;
    cout << "──────────────────────────────────────────────────────" << endl;

    // Process commands
    processCommands();

    // Display final network
    crimeGraph.displayGraph();
    crimeGraph.displayStatistics();

    // Write output
    writeOutput("Network analysis completed!");

    cout << "──────────────────────────────────────────────────────" << endl;
    cout << "Network Finder completed successfully!" << endl;

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
    ifstream inputFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\network_input.txt");

    if (!inputFile.is_open()) {
        cerr << "Error: Cannot open network_input.txt!" << endl;
        cerr << "Make sure the file exists in the data folder." << endl;
        writeOutput("ERROR: Input file not found!");
        return;
    }

    string line;
    int lineNumber = 0;

    while (getline(inputFile, line)) {
        lineNumber++;
        line = trim(line);

        if (line.empty()) continue;

        cout << "\nProcessing Line " << lineNumber << ": " << line << endl;

        stringstream ss(line);
        string command;
        getline(ss, command, '|');
        command = trim(command);

        if (command == "ADD_NODE") {
            // Format: ADD_NODE|NodeID|Type|Name
            string nodeId, type, name;
            getline(ss, nodeId, '|');
            getline(ss, type, '|');
            getline(ss, name);

            nodeId = trim(nodeId);
            type = trim(type);
            name = trim(name);

            crimeGraph.addNode(nodeId);

            if (type == "CRIME") {
                networkData.addCrime(nodeId, name);
            }
            else if (type == "SUSPECT") {
                networkData.addSuspect(nodeId, name);
            }
            else if (type == "LOCATION") {
                networkData.addLocation(nodeId, name);
            }

        }
        else if (command == "ADD_EDGE") {
            // Format: ADD_EDGE|Node1|Node2
            string node1, node2;
            getline(ss, node1, '|');
            getline(ss, node2);

            node1 = trim(node1);
            node2 = trim(node2);

            crimeGraph.addEdge(node1, node2);

        }
        else if (command == "FIND_PATH_BFS") {
            // Format: FIND_PATH_BFS|Start|End
            string start, end;
            getline(ss, start, '|');
            getline(ss, end);

            start = trim(start);
            end = trim(end);

            vector<string> path = crimeGraph.BFS(start, end);

            // Write to output file
            ofstream outFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\network_input.txt");
            if (path.empty()) {
                outFile << "NO_CONNECTION" << endl;
                cout << "✗ No connection found!" << endl;
            }
            else {
                outFile << "FOUND|";
                for (size_t i = 0; i < path.size(); i++) {
                    outFile << path[i];
                    if (i < path.size() - 1) outFile << "->";
                }
                outFile << endl;

                cout << "✓ Path: ";
                for (size_t i = 0; i < path.size(); i++) {
                    cout << path[i];
                    if (i < path.size() - 1) cout << " → ";
                }
                cout << endl;
            }
            outFile.close();

        }
        else if (command == "FIND_PATH_DFS") {
            // Format: FIND_PATH_DFS|Start|End
            string start, end;
            getline(ss, start, '|');
            getline(ss, end);

            start = trim(start);
            end = trim(end);

            vector<string> path = crimeGraph.DFS(start, end);

           ofstream outFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\network_input.txt", ios::app);
            if (path.empty()) {
                outFile << "NO_CONNECTION" << endl;
            }
            else {
                outFile << "FOUND|";
                for (size_t i = 0; i < path.size(); i++) {
                    outFile << path[i];
                    if (i < path.size() - 1) outFile << "->";
                }
                outFile << endl;
            }
            outFile.close();

        }
        else if (command == "CHECK_CONNECTION") {
            // Format: CHECK_CONNECTION|Node1|Node2
            string node1, node2;
            getline(ss, node1, '|');
            getline(ss, node2);

            node1 = trim(node1);
            node2 = trim(node2);

            if (crimeGraph.areConnected(node1, node2)) {
                cout << "✓ " << node1 << " and " << node2 << " are connected!" << endl;
            }
            else {
                cout << "✗ " << node1 << " and " << node2 << " are NOT connected!" << endl;
            }

        }
        else if (command == "DISPLAY_GRAPH") {
            crimeGraph.displayGraph();

        }
        else if (command == "DISPLAY_STATISTICS") {
            crimeGraph.displayStatistics();

        }
        else if (command == "DISPLAY_NODE") {
            // Format: DISPLAY_NODE|NodeID
            string nodeId;
            getline(ss, nodeId);
            nodeId = trim(nodeId);

            networkData.displayNodeDetails(nodeId);
        }
        else {
            cout << "✗ Unknown command: " << command << endl;
        }
    }

    inputFile.close();
    cout << "\n✓ All commands processed!" << endl;
}

// Write output
void writeOutput(const string& message) {
    ofstream outputFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\network_input.txt", ios::app);

    if (!outputFile.is_open()) {
        cerr << "Error: Cannot write to output file!" << endl;
        return;
    }

    outputFile << "\n╔══════════════════════════════════════════════════════╗" << endl;
    outputFile << "║       NETWORK ANALYSIS - OUTPUT                      ║" << endl;
    outputFile << "╚══════════════════════════════════════════════════════╝" << endl;
    outputFile << endl;

    outputFile << "Total Nodes: " << crimeGraph.getNodeCount() << endl;
    outputFile << "Total Connections: " << crimeGraph.getEdgeCount() << endl;
    outputFile << "Status: SUCCESS" << endl;
    outputFile << "Message: " << message << endl;

    outputFile.close();
    cout << "\n✓ Output written to network_output.txt" << endl;
}
