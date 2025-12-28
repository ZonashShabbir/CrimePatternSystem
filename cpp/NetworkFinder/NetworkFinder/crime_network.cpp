// crime_network.cpp
#include "crime_network.h"
#include <iostream>

// Add crime node
void CrimeNetwork::addCrime(const string& crimeId, const string& description) {
    if (hasNode(crimeId)) {
        cout << "⚠ Crime '" << crimeId << "' already exists." << endl;
        return;
    }

    nodeData[crimeId] = NetworkNode(crimeId, "CRIME", description);
    cout << "✓ Crime node added: " << crimeId << endl;
}

// Add suspect node
void CrimeNetwork::addSuspect(const string& suspectId, const string& name) {
    if (hasNode(suspectId)) {
        cout << "⚠ Suspect '" << suspectId << "' already exists." << endl;
        return;
    }

    nodeData[suspectId] = NetworkNode(suspectId, "SUSPECT", name);
    cout << "✓ Suspect node added: " << suspectId << endl;
}

// Add location node
void CrimeNetwork::addLocation(const string& locationId, const string& name) {
    if (hasNode(locationId)) {
        cout << "⚠ Location '" << locationId << "' already exists." << endl;
        return;
    }

    nodeData[locationId] = NetworkNode(locationId, "LOCATION", name);
    cout << "✓ Location node added: " << locationId << endl;
}

// Get node
NetworkNode* CrimeNetwork::getNode(const string& nodeId) {
    if (!hasNode(nodeId)) return nullptr;
    return &nodeData[nodeId];
}

// Display node details
void CrimeNetwork::displayNodeDetails(const string& nodeId) const {
    if (!hasNode(nodeId)) {
        cout << "✗ Node '" << nodeId << "' not found." << endl;
        return;
    }

    const NetworkNode& node = nodeData.at(nodeId);

    cout << "\n┌─────────────────────────────────────┐" << endl;
    cout << "│ ID: " << node.id << endl;
    cout << "│ Type: " << node.type << endl;
    cout << "│ Name: " << node.name << endl;
    cout << "└─────────────────────────────────────┘" << endl;
}

// Display all nodes
void CrimeNetwork::displayAllNodes() const {
    if (nodeData.empty()) {
        cout << "\n⚠ No nodes in network." << endl;
        return;
    }

    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           ALL NETWORK NODES                          ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;

    for (const auto& pair : nodeData) {
        displayNodeDetails(pair.first);
    }
}

// Check if node exists
bool CrimeNetwork::hasNode(const string& nodeId) const {
    return nodeData.find(nodeId) != nodeData.end();
}