// graph.h
#ifndef GRAPH_H
#define GRAPH_H

#include <string>
#include <vector>
#include <map>
#include <queue>
#include <set>
#include <iostream>

using namespace std;

// Graph class using Adjacency List (Level-1 DSA: List/Map)
class Graph {
private:
    // Adjacency List: map of node to list of connected nodes
    map<string, vector<string>> adjList;

    // Keep track of all nodes
    set<string> nodes;

    // Number of edges
    int edgeCount;

public:
    // Constructor
    Graph();

    // Add a node to the graph
    void addNode(const string& node);

    // Add an edge between two nodes (undirected)
    void addEdge(const string& node1, const string& node2);

    // Remove a node and all its connections
    void removeNode(const string& node);

    // Remove an edge between two nodes
    void removeEdge(const string& node1, const string& node2);

    // Check if node exists
    bool nodeExists(const string& node) const;

    // Check if edge exists
    bool edgeExists(const string& node1, const string& node2) const;

    // Get all neighbors of a node
    vector<string> getNeighbors(const string& node) const;

    // BFS - Breadth First Search (Level-2 DSA)
    vector<string> BFS(const string& start, const string& end);

    // DFS - Depth First Search (Level-2 DSA)
    vector<string> DFS(const string& start, const string& end);

    // Find all connected components
    vector<vector<string>> findConnectedComponents();

    // Check if two nodes are connected (in same component)
    bool areConnected(const string& node1, const string& node2);

    // Display the entire graph
    void displayGraph() const;

    // Get graph statistics
    void displayStatistics() const;

    // Get total number of nodes
    int getNodeCount() const;

    // Get total number of edges
    int getEdgeCount() const;

    // Clear the graph
    void clear();
};

#endif