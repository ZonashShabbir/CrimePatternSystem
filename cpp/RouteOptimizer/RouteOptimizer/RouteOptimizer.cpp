// RouteOptimizer.cpp - Module 3 Main Program
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <climits>
#include "dijkstra.h"
#include "route_manager.h"

using namespace std;

// Global objects
DijkstraGraph locationGraph;
RouteManager routeManager;

// Function prototypes
void processCommands();
void writeOutput(const string& message);
string trim(const string& str);

int main() {
    cout << "╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║       ROUTE OPTIMIZER - C++ BACKEND                  ║" << endl;
    cout << "║       Module 3: Patrol Route Optimization            ║" << endl;
    cout << "║       Using Dijkstra's Algorithm & Min-Heap          ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;
    cout << endl;

    cout << "Starting Route Optimizer..." << endl;
    cout << "Processing commands from input file..." << endl;
    cout << "──────────────────────────────────────────────────────" << endl;

    // Process commands
    processCommands();

    // Display final graph
    locationGraph.displayGraph();
    locationGraph.displayStatistics();

    // Write output
    writeOutput("Route optimization completed!");

    cout << "──────────────────────────────────────────────────────" << endl;
    cout << "Route Optimizer completed successfully!" << endl;
    cout << "Check route_output.txt for results." << endl;

    return 0;
}

// Trim whitespace from string
string trim(const string& str) {
    size_t first = str.find_first_not_of(" \t\n\r");
    if (first == string::npos) return "";
    size_t last = str.find_last_not_of(" \t\n\r");
    return str.substr(first, last - first + 1);
}

// Process commands from input file
void processCommands() {
    ifstream inputFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\route_input.txt");


    if (!inputFile.is_open()) {
        cerr << "Error: Cannot open route_input.txt!" << endl;
        cerr << "Make sure the file exists in the data folder." << endl;
        writeOutput("ERROR: Input file not found!");
        return;
    }

    string line;
    int lineNumber = 0;

    while (getline(inputFile, line)) {
        lineNumber++;
        line = trim(line);

        // Skip empty lines
        if (line.empty()) continue;

        cout << "\nProcessing Line " << lineNumber << ": " << line << endl;

        stringstream ss(line);
        string command;
        getline(ss, command, '|');
        command = trim(command);

        if (command == "ADD_LOCATION") {
            // Format: ADD_LOCATION|ID|Name|Type|CrimeCount
            string id, name, type;
            int crimeCount;

            getline(ss, id, '|');
            getline(ss, name, '|');
            getline(ss, type, '|');

            string crimeStr;
            getline(ss, crimeStr);
            crimeCount = crimeStr.empty() ? 0 : stoi(trim(crimeStr));

            id = trim(id);
            name = trim(name);
            type = trim(type);

            locationGraph.addLocation(id);
            routeManager.addLocation(id, name, type, crimeCount);

        }
        else if (command == "ADD_EDGE" || command == "ADD_ROUTE") {
            // Format: ADD_EDGE|From|To|Distance
            string from, to;
            int distance;

            getline(ss, from, '|');
            getline(ss, to, '|');

            string distStr;
            getline(ss, distStr);
            distance = stoi(trim(distStr));

            from = trim(from);
            to = trim(to);

            locationGraph.addEdge(from, to, distance);

        }
        else if (command == "FIND_ROUTE" || command == "SHORTEST_PATH") {
            // Format: FIND_ROUTE|Start|End
            string start, end;
            getline(ss, start, '|');
            getline(ss, end);

            start = trim(start);
            end = trim(end);

            auto result = locationGraph.shortestPath(start, end);
            int distance = result.first;
            vector<string> path = result.second;

            // Write to output file
            ofstream outFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\route_output.txt", ios::app);

            if (distance == INT_MAX || path.empty()) {
                outFile << "NO_ROUTE" << endl;
                cout << "✗ No route found between " << start << " and " << end << "!" << endl;
            }
            else {
                outFile << "DISTANCE|" << distance << endl;
                outFile << "PATH|";
                for (size_t i = 0; i < path.size(); i++) {
                    outFile << path[i];
                    if (i < path.size() - 1) outFile << "->";
                }
                outFile << endl;

                // Calculate estimated time (assuming 40 km/h average speed)
                int estimatedTime = (distance * 60) / 40;  // minutes
                outFile << "TIME|" << estimatedTime << endl;

                cout << "\n═══════════════════════════════════════" << endl;
                cout << "✓ SHORTEST ROUTE FOUND!" << endl;
                cout << "═══════════════════════════════════════" << endl;
                cout << "From: " << start << endl;
                cout << "To: " << end << endl;
                cout << "Distance: " << distance << " km" << endl;
                cout << "Estimated Time: " << estimatedTime << " minutes" << endl;
                cout << "Path: ";
                for (size_t i = 0; i < path.size(); i++) {
                    cout << path[i];
                    if (i < path.size() - 1) cout << " → ";
                }
                cout << endl;
                cout << "═══════════════════════════════════════" << endl;
            }
            outFile.close();

        }
        else if (command == "FIND_ALL_DISTANCES") {
            // Format: FIND_ALL_DISTANCES|Source
            string source;
            getline(ss, source);
            source = trim(source);

            cout << "\n🔍 Finding shortest distances from '" << source << "' to all locations..." << endl;

            map<string, int> distances = locationGraph.shortestPathsFromSource(source);

            cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
            cout << "║     SHORTEST DISTANCES FROM " << source << "                   ║" << endl;
            cout << "╚══════════════════════════════════════════════════════╝" << endl;

            for (const auto& pair : distances) {
                if (pair.first == source) continue;  // Skip source itself

                if (pair.second == INT_MAX) {
                    cout << source << " → " << pair.first << ": ∞ (No route)" << endl;
                }
                else {
                    cout << source << " → " << pair.first << ": " << pair.second << " km" << endl;
                }
            }
            cout << "──────────────────────────────────────────────────────" << endl;

        }
        else if (command == "DISPLAY_HOTSPOTS") {
            // Format: DISPLAY_HOTSPOTS|MinCrimeCount
            string minStr;
            getline(ss, minStr);
            int minCount = minStr.empty() ? 5 : stoi(trim(minStr));

            vector<string> hotspots = routeManager.getHotspots(minCount);

            cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
            cout << "║     CRIME HOTSPOTS (>= " << minCount << " crimes)                 ║" << endl;
            cout << "╚══════════════════════════════════════════════════════╝" << endl;

            if (hotspots.empty()) {
                cout << "⚠ No hotspots found with crime count >= " << minCount << endl;
            }
            else {
                cout << "Found " << hotspots.size() << " hotspot(s):" << endl;
                for (const string& loc : hotspots) {
                    routeManager.displayLocationDetails(loc);
                }
            }
            cout << "──────────────────────────────────────────────────────" << endl;

        }
        else if (command == "UPDATE_CRIME_COUNT") {
            // Format: UPDATE_CRIME_COUNT|LocationID|NewCount
            string id;
            int count;

            getline(ss, id, '|');
            string countStr;
            getline(ss, countStr);

            id = trim(id);
            count = stoi(trim(countStr));

            routeManager.updateCrimeCount(id, count);

        }
        else if (command == "DISPLAY_GRAPH") {
            locationGraph.displayGraph();

        }
        else if (command == "DISPLAY_STATISTICS") {
            locationGraph.displayStatistics();

        }
        else if (command == "DISPLAY_LOCATION") {
            // Format: DISPLAY_LOCATION|ID
            string id;
            getline(ss, id);
            id = trim(id);

            routeManager.displayLocationDetails(id);

        }
        else if (command == "DISPLAY_ALL_LOCATIONS") {
            routeManager.displayAllLocations();

        }
        else if (command == "CHECK_EDGE") {
            // Format: CHECK_EDGE|From|To
            string from, to;
            getline(ss, from, '|');
            getline(ss, to);

            from = trim(from);
            to = trim(to);

            if (locationGraph.edgeExists(from, to)) {
                int weight = locationGraph.getEdgeWeight(from, to);
                cout << "✓ Route exists: " << from << " ↔ " << to << " (" << weight << " km)" << endl;
            }
            else {
                cout << "✗ No route exists between " << from << " and " << to << endl;
            }

        }
        else if (command == "REMOVE_LOCATION") {
            // Format: REMOVE_LOCATION|ID
            string id;
            getline(ss, id);
            id = trim(id);

            locationGraph.removeLocation(id);

        }
        else if (command == "REMOVE_EDGE") {
            // Format: REMOVE_EDGE|From|To
            string from, to;
            getline(ss, from, '|');
            getline(ss, to);

            from = trim(from);
            to = trim(to);

            locationGraph.removeEdge(from, to);

        }
        else if (command == "CLEAR_GRAPH") {
            locationGraph.clear();
            cout << "✓ Graph cleared!" << endl;
        }
        else {
            cout << "✗ Unknown command: " << command << endl;
        }
    }

    inputFile.close();
    cout << "\n✓ All commands processed successfully!" << endl;
}

// Write output to file
void writeOutput(const string& message) {
    ofstream outputFile("C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\route_output.txt", ios::app);

    if (!outputFile.is_open()) {
        cerr << "Error: Cannot write to output file!" << endl;
        return;
    }

    outputFile << "\n" << endl;
    outputFile << "╔══════════════════════════════════════════════════════╗" << endl;
    outputFile << "║       ROUTE OPTIMIZATION SYSTEM - SUMMARY            ║" << endl;
    outputFile << "╚══════════════════════════════════════════════════════╝" << endl;
    outputFile << endl;

    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "SYSTEM INFORMATION" << endl;
    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "Module: Route Optimizer (Module 3)" << endl;
    outputFile << "Algorithm: Dijkstra's Shortest Path Algorithm" << endl;
    outputFile << "Data Structure: Min-Heap Priority Queue" << endl;
    outputFile << endl;

    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "GRAPH STATISTICS" << endl;
    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "Total Locations: " << locationGraph.getLocationCount() << endl;
    outputFile << "Total Routes (Edges): " << locationGraph.getEdgeCount() << endl;
    outputFile << endl;

    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "EXECUTION STATUS" << endl;
    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "Status: SUCCESS ✓" << endl;
    outputFile << "Message: " << message << endl;
    outputFile << endl;

    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "DSA IMPLEMENTATION DETAILS" << endl;
    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "Level-1 DSA: Priority Queue (Min-Heap)" << endl;
    outputFile << "  - Purpose: Efficiently extract minimum distance node" << endl;
    outputFile << "  - Operations: push(), pop(), top()" << endl;
    outputFile << "  - Time Complexity: O(log n)" << endl;
    outputFile << endl;
    outputFile << "Level-2 DSA: Dijkstra's Algorithm" << endl;
    outputFile << "  - Purpose: Find shortest path between locations" << endl;
    outputFile << "  - Graph Representation: Adjacency List (weighted)" << endl;
    outputFile << "  - Time Complexity: O((V + E) log V)" << endl;
    outputFile << "  - Space Complexity: O(V)" << endl;
    outputFile << endl;

    outputFile << "═══════════════════════════════════════════════════════" << endl;
    outputFile << "END OF REPORT" << endl;
    outputFile << "═══════════════════════════════════════════════════════" << endl;

    outputFile.close();
    cout << "\n✓ Detailed output written to route_output.txt" << endl;
}