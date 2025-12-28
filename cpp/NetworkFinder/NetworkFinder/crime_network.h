// crime_network.h
#ifndef CRIME_NETWORK_H
#define CRIME_NETWORK_H

#include <string>
#include <map>
#include <vector>
using namespace std;

// Structure to store crime/suspect information
struct NetworkNode {
    string id;           // Crime ID or Suspect ID
    string type;         // "CRIME" or "SUSPECT" or "LOCATION"
    string name;         // Name/Description
    vector<string> connections;  // Connected node IDs

    NetworkNode() {}

    NetworkNode(string nodeId, string nodeType, string nodeName)
        : id(nodeId), type(nodeType), name(nodeName) {
    }
};

// Crime Network Manager - combines Graph with node metadata
class CrimeNetwork {
private:
    map<string, NetworkNode> nodeData;  // Store node information

public:
    // Add a crime node
    void addCrime(const string& crimeId, const string& description);

    // Add a suspect node
    void addSuspect(const string& suspectId, const string& name);

    // Add a location node
    void addLocation(const string& locationId, const string& name);

    // Get node information
    NetworkNode* getNode(const string& nodeId);

    // Display node details
    void displayNodeDetails(const string& nodeId) const;

    // Display all nodes
    void displayAllNodes() const;

    // Check if node exists
    bool hasNode(const string& nodeId) const;
};

#endif