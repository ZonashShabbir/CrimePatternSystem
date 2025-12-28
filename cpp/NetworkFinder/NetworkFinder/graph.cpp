// graph.cpp
#include "graph.h"

// Constructor
Graph::Graph() : edgeCount(0) {}

// Add node
void Graph::addNode(const string& node) {
    if (!nodeExists(node)) {
        adjList[node] = vector<string>();
        nodes.insert(node);
        cout << "✓ Node '" << node << "' added to network." << endl;
    }
    else {
        cout << "⚠ Node '" << node << "' already exists." << endl;
    }
}

// Add edge (undirected)
void Graph::addEdge(const string& node1, const string& node2) {
    // Add nodes if they don't exist
    addNode(node1);
    addNode(node2);

    // Check if edge already exists
    if (edgeExists(node1, node2)) {
        cout << "⚠ Connection between '" << node1 << "' and '" << node2 << "' already exists." << endl;
        return;
    }

    // Add edge in both directions (undirected graph)
    adjList[node1].push_back(node2);
    adjList[node2].push_back(node1);
    edgeCount++;

    cout << "✓ Connection added: " << node1 << " ↔ " << node2 << endl;
}

// Remove node
void Graph::removeNode(const string& node) {
    if (!nodeExists(node)) {
        cout << "✗ Node '" << node << "' does not exist." << endl;
        return;
    }

    // Remove all edges connected to this node
    for (const string& neighbor : adjList[node]) {
        auto& neighborList = adjList[neighbor];
        neighborList.erase(remove(neighborList.begin(), neighborList.end(), node), neighborList.end());
        edgeCount--;
    }

    // Remove the node itself
    adjList.erase(node);
    nodes.erase(node);

    cout << "✓ Node '" << node << "' removed from network." << endl;
}

// Remove edge
void Graph::removeEdge(const string& node1, const string& node2) {
    if (!edgeExists(node1, node2)) {
        cout << "✗ Connection between '" << node1 << "' and '" << node2 << "' does not exist." << endl;
        return;
    }

    // Remove from both adjacency lists
    auto& list1 = adjList[node1];
    auto& list2 = adjList[node2];

    list1.erase(remove(list1.begin(), list1.end(), node2), list1.end());
    list2.erase(remove(list2.begin(), list2.end(), node1), list2.end());
    edgeCount--;

    cout << "✓ Connection removed: " << node1 << " ↔ " << node2 << endl;
}

// Check if node exists
bool Graph::nodeExists(const string& node) const {
    return nodes.find(node) != nodes.end();
}

// Check if edge exists
bool Graph::edgeExists(const string& node1, const string& node2) const {
    if (!nodeExists(node1) || !nodeExists(node2)) return false;

    const auto& neighbors = adjList.at(node1);
    return find(neighbors.begin(), neighbors.end(), node2) != neighbors.end();
}

// Get neighbors
vector<string> Graph::getNeighbors(const string& node) const {
    if (!nodeExists(node)) return vector<string>();
    return adjList.at(node);
}

// BFS - Breadth First Search
vector<string> Graph::BFS(const string& start, const string& end) {
    if (!nodeExists(start) || !nodeExists(end)) {
        cout << "✗ Start or end node does not exist!" << endl;
        return vector<string>();
    }

    cout << "\nRunning BFS from '" << start << "' to '" << end << "'..." << endl;

    queue<string> q;
    set<string> visited;
    map<string, string> parent;

    q.push(start);
    visited.insert(start);
    parent[start] = "";

    bool found = false;

    while (!q.empty() && !found) {
        string current = q.front();
        q.pop();

        cout << "Visiting: " << current << endl;

        if (current == end) {
            found = true;
            break;
        }

        // Explore neighbors
        for (const string& neighbor : adjList.at(current)) {
            if (visited.find(neighbor) == visited.end()) {
                visited.insert(neighbor);
                parent[neighbor] = current;
                q.push(neighbor);
            }
        }
    }

    // Reconstruct path
    vector<string> path;
    if (found) {
        string current = end;
        while (!current.empty()) {
            path.insert(path.begin(), current);
            current = parent[current];
        }
        cout << "✓ Path found!" << endl;
    }
    else {
        cout << "✗ No path found!" << endl;
    }

    return path;
}

// DFS - Depth First Search (Helper function)
bool DFSHelper(const string& current, const string& end,
    const map<string, vector<string>>& adjList,
    set<string>& visited, vector<string>& path) {

    visited.insert(current);
    path.push_back(current);

    if (current == end) {
        return true;
    }

    for (const string& neighbor : adjList.at(current)) {
        if (visited.find(neighbor) == visited.end()) {
            if (DFSHelper(neighbor, end, adjList, visited, path)) {
                return true;
            }
        }
    }

    path.pop_back();
    return false;
}

// DFS - Depth First Search
vector<string> Graph::DFS(const string& start, const string& end) {
    if (!nodeExists(start) || !nodeExists(end)) {
        cout << "✗ Start or end node does not exist!" << endl;
        return vector<string>();
    }

    cout << "\nRunning DFS from '" << start << "' to '" << end << "'..." << endl;

    set<string> visited;
    vector<string> path;

    if (DFSHelper(start, end, adjList, visited, path)) {
        cout << "✓ Path found!" << endl;
        return path;
    }
    else {
        cout << "✗ No path found!" << endl;
        return vector<string>();
    }
}

// Find connected components
vector<vector<string>> Graph::findConnectedComponents() {
    vector<vector<string>> components;
    set<string> visited;

    for (const string& node : nodes) {
        if (visited.find(node) == visited.end()) {
            vector<string> component;
            queue<string> q;

            q.push(node);
            visited.insert(node);

            while (!q.empty()) {
                string current = q.front();
                q.pop();
                component.push_back(current);

                for (const string& neighbor : adjList.at(current)) {
                    if (visited.find(neighbor) == visited.end()) {
                        visited.insert(neighbor);
                        q.push(neighbor);
                    }
                }
            }

            components.push_back(component);
        }
    }

    return components;
}

// Check if connected
bool Graph::areConnected(const string& node1, const string& node2) {
    if (!nodeExists(node1) || !nodeExists(node2)) return false;

    set<string> visited;
    queue<string> q;

    q.push(node1);
    visited.insert(node1);

    while (!q.empty()) {
        string current = q.front();
        q.pop();

        if (current == node2) return true;

        for (const string& neighbor : adjList.at(current)) {
            if (visited.find(neighbor) == visited.end()) {
                visited.insert(neighbor);
                q.push(neighbor);
            }
        }
    }

    return false;
}

// Display graph
void Graph::displayGraph() const {
    if (nodes.empty()) {
        cout << "\n⚠ Network is empty." << endl;
        return;
    }

    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           CRIME NETWORK GRAPH                        ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;

    for (const string& node : nodes) {
        cout << "\n[" << node << "]";

        if (adjList.at(node).empty()) {
            cout << " → (No connections)" << endl;
        }
        else {
            cout << " → ";
            for (size_t i = 0; i < adjList.at(node).size(); i++) {
                cout << adjList.at(node)[i];
                if (i < adjList.at(node).size() - 1) cout << ", ";
            }
            cout << endl;
        }
    }
}

// Display statistics
void Graph::displayStatistics() const {
    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           NETWORK STATISTICS                         ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;
    cout << "Total Nodes: " << nodes.size() << endl;
    cout << "Total Connections: " << edgeCount << endl;

    vector<vector<string>> components = const_cast<Graph*>(this)->findConnectedComponents();
    cout << "Connected Components: " << components.size() << endl;

    for (size_t i = 0; i < components.size(); i++) {
        cout << "  Component " << (i + 1) << ": " << components[i].size() << " nodes" << endl;
    }
}

// Get node count
int Graph::getNodeCount() const {
    return nodes.size();
}

// Get edge count
int Graph::getEdgeCount() const {
    return edgeCount;
}

// Clear graph
void Graph::clear() {
    adjList.clear();
    nodes.clear();
    edgeCount = 0;
    cout << "✓ Network cleared." << endl;
}