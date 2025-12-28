// dijkstra.h
#ifndef DIJKSTRA_H
#define DIJKSTRA_H

#include <string>
#include <vector>
#include <map>
#include <queue>
#include <limits>
#include <set>
#include <iostream>

using namespace std;

// Edge structure for weighted graph
struct Edge {
    string destination;
    int weight;  // Distance in km or time in minutes

    Edge() : weight(0) {}

    Edge(string dest, int w) : destination(dest), weight(w) {}
};

// Priority Queue element for Dijkstra's algorithm
struct PQElement {
    string node;
    int distance;

    PQElement(string n, int d) : node(n), distance(d) {}

    // For min-heap (priority queue with smallest distance first)
    bool operator>(const PQElement& other) const {
        return distance > other.distance;
    }
};

// Dijkstra's Algorithm Implementation with Min-Heap
class DijkstraGraph {
private:
    // Adjacency List: map of location to list of edges
    map<string, vector<Edge>> adjList;

    // Set of all locations
    set<string> locations;

    // Number of edges
    int edgeCount;

public:
    // Constructor
    DijkstraGraph();

    // Add a location node
    void addLocation(const string& location);

    // Add weighted edge (road between two locations)
    void addEdge(const string& from, const string& to, int weight);

    // Remove a location
    void removeLocation(const string& location);

    // Remove an edge
    void removeEdge(const string& from, const string& to);

    // Check if location exists
    bool locationExists(const string& location) const;

    // Check if edge exists
    bool edgeExists(const string& from, const string& to) const;

    // Get edge weight
    int getEdgeWeight(const string& from, const string& to) const;

    // Dijkstra's Algorithm - Find shortest path
    pair<int, vector<string>> shortestPath(const string& start, const string& end);

    // Find shortest paths from one source to all other nodes
    map<string, int> shortestPathsFromSource(const string& source);

    // Find all paths between two locations (for comparison)
    vector<vector<string>> findAllPaths(const string& start, const string& end, int maxDepth = 10);

    // Display the graph
    void displayGraph() const;

    // Display statistics
    void displayStatistics() const;

    // Get location count
    int getLocationCount() const;

    // Get edge count
    int getEdgeCount() const;

    // Clear graph
    void clear();
};

#endif