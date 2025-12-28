// dijkstra.cpp
#include "dijkstra.h"

// Constructor
DijkstraGraph::DijkstraGraph() : edgeCount(0) {}

// Add location
void DijkstraGraph::addLocation(const string& location) {
    if (!locationExists(location)) {
        adjList[location] = vector<Edge>();
        locations.insert(location);
        cout << "✓ Location '" << location << "' added." << endl;
    }
    else {
        cout << "⚠ Location '" << location << "' already exists." << endl;
    }
}

// Add weighted edge
void DijkstraGraph::addEdge(const string& from, const string& to, int weight) {
    // Add locations if they don't exist
    addLocation(from);
    addLocation(to);

    // Check if edge already exists
    if (edgeExists(from, to)) {
        cout << "⚠ Route between '" << from << "' and '" << to << "' already exists." << endl;
        return;
    }

    // Add edge in both directions (undirected graph)
    adjList[from].push_back(Edge(to, weight));
    adjList[to].push_back(Edge(from, weight));
    edgeCount++;

    cout << "✓ Route added: " << from << " ↔ " << to << " (" << weight << " km)" << endl;
}

// Remove location
void DijkstraGraph::removeLocation(const string& location) {
    if (!locationExists(location)) {
        cout << "✗ Location '" << location << "' does not exist." << endl;
        return;
    }

    // Remove all edges connected to this location
    for (auto& pair : adjList) {
        vector<Edge>& edges = pair.second;
        edges.erase(
            remove_if(edges.begin(), edges.end(),
                [&location](const Edge& e) { return e.destination == location; }),
            edges.end()
        );
    }

    adjList.erase(location);
    locations.erase(location);

    cout << "✓ Location '" << location << "' removed." << endl;
}

// Remove edge
void DijkstraGraph::removeEdge(const string& from, const string& to) {
    if (!edgeExists(from, to)) {
        cout << "✗ Route between '" << from << "' and '" << to << "' does not exist." << endl;
        return;
    }

    // Remove from both directions
    auto& fromEdges = adjList[from];
    fromEdges.erase(
        remove_if(fromEdges.begin(), fromEdges.end(),
            [&to](const Edge& e) { return e.destination == to; }),
        fromEdges.end()
    );

    auto& toEdges = adjList[to];
    toEdges.erase(
        remove_if(toEdges.begin(), toEdges.end(),
            [&from](const Edge& e) { return e.destination == from; }),
        toEdges.end()
    );

    edgeCount--;
    cout << "✓ Route removed: " << from << " ↔ " << to << endl;
}

// Check if location exists
bool DijkstraGraph::locationExists(const string& location) const {
    return locations.find(location) != locations.end();
}

// Check if edge exists
bool DijkstraGraph::edgeExists(const string& from, const string& to) const {
    if (!locationExists(from)) return false;

    const vector<Edge>& edges = adjList.at(from);
    for (const Edge& edge : edges) {
        if (edge.destination == to) return true;
    }
    return false;
}

// Get edge weight
int DijkstraGraph::getEdgeWeight(const string& from, const string& to) const {
    if (!edgeExists(from, to)) return -1;

    const vector<Edge>& edges = adjList.at(from);
    for (const Edge& edge : edges) {
        if (edge.destination == to) return edge.weight;
    }
    return -1;
}

// Dijkstra's Algorithm - Shortest Path
pair<int, vector<string>> DijkstraGraph::shortestPath(const string& start, const string& end) {
    if (!locationExists(start) || !locationExists(end)) {
        cout << "✗ Start or end location does not exist!" << endl;
        return { INT_MAX, vector<string>() };
    }

    cout << "\n🔍 Running Dijkstra's Algorithm..." << endl;
    cout << "From: " << start << " → To: " << end << endl;
    cout << "──────────────────────────────────────────────────────" << endl;

    // Distance map: location -> shortest distance from start
    map<string, int> distance;

    // Parent map: location -> previous location in shortest path
    map<string, string> parent;

    // Priority queue: min-heap of (distance, location)
    priority_queue<PQElement, vector<PQElement>, greater<PQElement>> pq;

    // Visited set
    set<string> visited;

    // Initialize distances
    for (const string& loc : locations) {
        distance[loc] = INT_MAX;
    }
    distance[start] = 0;

    // Push start location to priority queue
    pq.push(PQElement(start, 0));

    // Main loop
    while (!pq.empty()) {
        PQElement current = pq.top();
        pq.pop();

        string currentLoc = current.node;
        int currentDist = current.distance;

        // Skip if already visited
        if (visited.find(currentLoc) != visited.end()) {
            continue;
        }

        visited.insert(currentLoc);

        cout << "Visiting: " << currentLoc << " (distance: " << currentDist << " km)" << endl;

        // If we reached the destination
        if (currentLoc == end) {
            break;
        }

        // Explore neighbors
        for (const Edge& edge : adjList[currentLoc]) {
            string neighbor = edge.destination;
            int edgeWeight = edge.weight;
            int newDist = currentDist + edgeWeight;

            // If found shorter path
            if (newDist < distance[neighbor]) {
                distance[neighbor] = newDist;
                parent[neighbor] = currentLoc;
                pq.push(PQElement(neighbor, newDist));
            }
        }
    }

    // Reconstruct path
    vector<string> path;
    int totalDistance = distance[end];

    if (totalDistance == INT_MAX) {
        cout << "✗ No route found!" << endl;
        return { INT_MAX, vector<string>() };
    }

    string current = end;
    while (!current.empty()) {
        path.insert(path.begin(), current);
        current = parent[current];
    }

    cout << "✓ Shortest path found!" << endl;
    cout << "Total Distance: " << totalDistance << " km" << endl;

    return { totalDistance, path };
}

// Shortest paths from source to all locations
map<string, int> DijkstraGraph::shortestPathsFromSource(const string& source) {
    map<string, int> distance;
    priority_queue<PQElement, vector<PQElement>, greater<PQElement>> pq;
    set<string> visited;

    // Initialize
    for (const string& loc : locations) {
        distance[loc] = INT_MAX;
    }
    distance[source] = 0;
    pq.push(PQElement(source, 0));

    // Dijkstra's algorithm
    while (!pq.empty()) {
        PQElement current = pq.top();
        pq.pop();

        string currentLoc = current.node;

        if (visited.find(currentLoc) != visited.end()) continue;
        visited.insert(currentLoc);

        for (const Edge& edge : adjList[currentLoc]) {
            string neighbor = edge.destination;
            int newDist = distance[currentLoc] + edge.weight;

            if (newDist < distance[neighbor]) {
                distance[neighbor] = newDist;
                pq.push(PQElement(neighbor, newDist));
            }
        }
    }

    return distance;
}

// Display graph
void DijkstraGraph::displayGraph() const {
    if (locations.empty()) {
        cout << "\n⚠ Graph is empty." << endl;
        return;
    }

    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           LOCATION GRAPH (WEIGHTED)                  ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;

    for (const string& loc : locations) {
        cout << "\n[" << loc << "]";

        if (adjList.at(loc).empty()) {
            cout << " → (No connections)" << endl;
        }
        else {
            cout << " →" << endl;
            for (const Edge& edge : adjList.at(loc)) {
                cout << "    • " << edge.destination << " (" << edge.weight << " km)" << endl;
            }
        }
    }
}

// Display statistics
void DijkstraGraph::displayStatistics() const {
    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           GRAPH STATISTICS                           ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;
    cout << "Total Locations: " << locations.size() << endl;
    cout << "Total Routes: " << edgeCount << endl;

    // Calculate average connections per location
    int totalConnections = 0;
    for (const auto& pair : adjList) {
        totalConnections += pair.second.size();
    }
    double avgConnections = locations.empty() ? 0 : (double)totalConnections / locations.size();
    cout << "Average Connections per Location: " << avgConnections << endl;
}

// Get location count
int DijkstraGraph::getLocationCount() const {
    return locations.size();
}

// Get edge count
int DijkstraGraph::getEdgeCount() const {
    return edgeCount;
}

// Clear graph
void DijkstraGraph::clear() {
    adjList.clear();
    locations.clear();
    edgeCount = 0;
    cout << "✓ Graph cleared." << endl;
}