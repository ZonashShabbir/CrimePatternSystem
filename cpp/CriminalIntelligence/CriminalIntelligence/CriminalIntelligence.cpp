#include "Graph.h"
#include <sstream>
#include <map>
#include <iostream>
#include <fstream>
using namespace std;

int main() {
    // Try to open files
    ifstream inFile("criminal_network.txt");
    ofstream outFile("intelligence_output.txt");

    if (!inFile.is_open()) {
        // Create error file for debugging
        ofstream errorLog("error_log.txt");
        errorLog << "ERROR: Cannot open criminal_network.txt" << endl;
        errorLog << "Make sure the file is in the same folder as the .exe" << endl;
        errorLog.close();

        cerr << "ERROR: Cannot open criminal_network.txt" << endl;
        return 1;
    }

    if (!outFile.is_open()) {
        cerr << "ERROR: Cannot create intelligence_output.txt" << endl;
        return 1;
    }

    map<string, int> criminalMap;
    vector<string> criminalNames;
    vector<tuple<string, string, int>> connections;

    string line;
    int idCounter = 0;

    // Read data
    while (getline(inFile, line)) {
        if (line.empty()) continue;

        stringstream ss(line);
        string c1, c2, weightStr;

        getline(ss, c1, '|');
        getline(ss, c2, '|');
        getline(ss, weightStr);

        try {
            int weight = stoi(weightStr);

            if (criminalMap.find(c1) == criminalMap.end()) {
                criminalMap[c1] = idCounter++;
                criminalNames.push_back(c1);
            }
            if (criminalMap.find(c2) == criminalMap.end()) {
                criminalMap[c2] = idCounter++;
                criminalNames.push_back(c2);
            }

            connections.push_back(make_tuple(c1, c2, weight));
        }
        catch (const exception& e) {
            // Skip invalid lines
            continue;
        }
    }
    inFile.close();

    // Check if we have data
    if (criminalNames.empty() || connections.empty()) {
        outFile << "ERROR: No valid criminal data found!" << endl;
        outFile << "Please check the input file format." << endl;
        outFile.close();
        return 1;
    }

    // Create graph
    Graph graph(criminalNames.size(), criminalNames);

    for (auto& conn : connections) {
        string c1 = get<0>(conn);
        string c2 = get<1>(conn);
        int weight = get<2>(conn);

        int id1 = criminalMap[c1];
        int id2 = criminalMap[c2];

        graph.addEdge(id1, id2, weight, c1, c2);
    }

    // Analysis
    outFile << "========================================" << endl;
    outFile << "  CRIMINAL NETWORK INTELLIGENCE REPORT  " << endl;
    outFile << "========================================" << endl;
    outFile << endl;

    outFile << "Total Criminals Identified: " << criminalNames.size() << endl;
    outFile << "Total Connections Found: " << connections.size() << endl;
    outFile << endl;

    graph.kruskalMST(outFile);
    graph.primMST(outFile);
    graph.floydWarshall(outFile);
    graph.findKeyCriminals(outFile);

    outFile << "========================================" << endl;
    outFile << "           END OF REPORT                " << endl;
    outFile << "========================================" << endl;

    outFile.close();

    cout << "SUCCESS: Analysis complete. Check intelligence_output.txt" << endl;

    return 0;
}
