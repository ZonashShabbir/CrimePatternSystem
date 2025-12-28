#include "Graph.h"

Graph::Graph(int vertices, vector<string> names) {
    V = vertices;
    criminalNames = names;
    adjMatrix.resize(V, vector<int>(V, INT_MAX));
    for (int i = 0; i < V; i++) {
        adjMatrix[i][i] = 0;
    }
}

void Graph::addEdge(int src, int dest, int weight, string c1, string c2) {
    Edge e;
    e.src = src;
    e.dest = dest;
    e.weight = weight;
    e.criminal1 = c1;
    e.criminal2 = c2;
    edges.push_back(e);

    adjMatrix[src][dest] = weight;
    adjMatrix[dest][src] = weight;
}

int Graph::find(vector<int>& parent, int i) {
    if (parent[i] == i)
        return i;
    return parent[i] = find(parent, parent[i]);
}

void Graph::unionSet(vector<int>& parent, vector<int>& rank, int x, int y) {
    int xroot = find(parent, x);
    int yroot = find(parent, y);

    if (rank[xroot] < rank[yroot])
        parent[xroot] = yroot;
    else if (rank[xroot] > rank[yroot])
        parent[yroot] = xroot;
    else {
        parent[yroot] = xroot;
        rank[xroot]++;
    }
}

void Graph::kruskalMST(ofstream& outFile) {
    vector<Edge> result;
    sort(edges.begin(), edges.end(),
        [](Edge a, Edge b) { return a.weight < b.weight; });

    vector<int> parent(V);
    vector<int> rank(V, 0);

    for (int i = 0; i < V; i++)
        parent[i] = i;

    for (Edge& e : edges) {
        int x = find(parent, e.src);
        int y = find(parent, e.dest);

        if (x != y) {
            result.push_back(e);
            unionSet(parent, rank, x, y);
        }
    }

    outFile << "=== KRUSKAL'S ALGORITHM (Minimum Spanning Tree) ===" << endl;
    outFile << "Critical Criminal Connections:" << endl;
    int totalWeight = 0;
    for (Edge& e : result) {
        outFile << e.criminal1 << " <--> " << e.criminal2
            << " (Strength: " << e.weight << " joint crimes)" << endl;
        totalWeight += e.weight;
    }
    outFile << "Total Network Strength: " << totalWeight << endl;
    outFile << "Key Connections to Disrupt: " << result.size() << endl;
    outFile << endl;
}

void Graph::primMST(ofstream& outFile) {
    vector<bool> inMST(V, false);
    vector<int> key(V, INT_MAX);
    vector<int> parent(V, -1);

    key[0] = 0;

    for (int count = 0; count < V - 1; count++) {
        int u = -1;
        for (int i = 0; i < V; i++) {
            if (!inMST[i] && (u == -1 || key[i] < key[u]))
                u = i;
        }

        inMST[u] = true;

        for (int v = 0; v < V; v++) {
            if (adjMatrix[u][v] != INT_MAX && !inMST[v]
                && adjMatrix[u][v] < key[v]) {
                key[v] = adjMatrix[u][v];
                parent[v] = u;
            }
        }
    }

    outFile << "=== PRIM'S ALGORITHM (Sequential Network Building) ===" << endl;
    outFile << "Network Expansion from Main Suspect:" << endl;
    int totalWeight = 0;
    for (int i = 1; i < V; i++) {
        outFile << criminalNames[parent[i]] << " --> " << criminalNames[i]
            << " (Strength: " << adjMatrix[i][parent[i]] << ")" << endl;
        totalWeight += adjMatrix[i][parent[i]];
    }
    outFile << "Total Investigation Path Weight: " << totalWeight << endl;
    outFile << endl;
}
void Graph::floydWarshall(ofstream& outFile) {
    vector<vector<int>> dist = adjMatrix;

    // Floyd Warshall Algorithm
    for (int k = 0; k < V; k++) {
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (dist[i][k] != INT_MAX && dist[k][j] != INT_MAX) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    // Write to main output file
    outFile << "=== FLOYD WARSHALL ALGORITHM (All Shortest Paths) ===" << endl;
    outFile << "Shortest Connection Paths Between All Criminals:" << endl;
    outFile << endl;

    // Header
    outFile << "From/To\t";
    for (int i = 0; i < V; i++) {
        outFile << "C" << i << "\t";
    }
    outFile << endl;

    // Matrix
    for (int i = 0; i < V; i++) {
        outFile << "C" << i << "\t";
        for (int j = 0; j < V; j++) {
            if (dist[i][j] == INT_MAX)
                outFile << "INF\t";
            else
                outFile << dist[i][j] << "\t";
        }
        outFile << endl;
    }
    outFile << endl;

    // Analysis
    outFile << "Key Findings:" << endl;
    for (int i = 0; i < V; i++) {
        int maxDist = 0;
        for (int j = 0; j < V; j++) {
            if (i != j && dist[i][j] != INT_MAX && dist[i][j] > maxDist) {
                maxDist = dist[i][j];
            }
        }
        if (maxDist > 0) {
            outFile << criminalNames[i] << " - Max distance to any criminal: "
                << maxDist << endl;
        }
    }
    outFile << endl;

    // ============================================
    // SAVE SEPARATE FILE FOR HEATMAP VISUALIZATION
    // USE FULL PATH TO DATA FOLDER
    // ============================================

    // FULL PATH - VERY IMPORTANT!
    string floydFilePath = "C:\\Users\\DELL\\Desktop\\CrimePatternSystem\\data\\floyd_warshall_results.txt";

    cout << "Attempting to create Floyd Warshall file at: " << floydFilePath << endl;

    ofstream floydFile(floydFilePath);

    if (floydFile.is_open()) {
        cout << "File opened successfully!" << endl;

        floydFile << "=== FLOYD WARSHALL HEATMAP DATA ===" << endl;
        floydFile << "Generated for Criminal Network Intelligence System" << endl;
        floydFile << endl;

        // Header with actual criminal names
        floydFile << "From/To\t";
        for (int i = 0; i < V; i++) {
            floydFile << criminalNames[i] << "\t";
        }
        floydFile << endl;

        // Data rows with criminal names
        for (int i = 0; i < V; i++) {
            floydFile << criminalNames[i] << "\t";
            for (int j = 0; j < V; j++) {
                if (dist[i][j] == INT_MAX)
                    floydFile << "999\t"; // Use 999 for INF (easier to parse)
                else
                    floydFile << dist[i][j] << "\t";
            }
            floydFile << endl;
        }

        floydFile << endl;
        floydFile << "=== END OF DATA ===" << endl;
        floydFile.close();

        cout << "✓ Floyd Warshall heatmap data saved successfully!" << endl;
        cout << "✓ File location: " << floydFilePath << endl;

        outFile << "Floyd Warshall heatmap data saved to: " << floydFilePath << endl;
    }
    else {
        cerr << "✗ ERROR: Could not create floyd_warshall_results.txt" << endl;
        cerr << "✗ Attempted path: " << floydFilePath << endl;

        outFile << "WARNING: Could not create floyd_warshall_results.txt" << endl;
        outFile << "Attempted path: " << floydFilePath << endl;
    }
}

void Graph::findKeyCriminals(ofstream& outFile) {
    outFile << "=== KEY CRIMINAL ANALYSIS ===" << endl;

    vector<int> connections(V, 0);
    for (Edge& e : edges) {
        connections[e.src]++;
        connections[e.dest]++;
    }

    vector<pair<int, int>> criminalConnections;
    for (int i = 0; i < V; i++) {
        criminalConnections.push_back({ connections[i], i });
    }

    sort(criminalConnections.begin(), criminalConnections.end(),
        [](pair<int, int> a, pair<int, int> b) { return a.first > b.first; });

    outFile << "Most Connected Criminals (Priority Targets):" << endl;
    for (int i = 0; i < min(5, V); i++) {
        int idx = criminalConnections[i].second;
        int conn = criminalConnections[i].first;
        outFile << (i + 1) << ". " << criminalNames[idx]
            << " - " << conn << " connections" << endl;
    }
    outFile << endl;

    // Safe check for recommendation
    if (criminalConnections.size() >= 3) {
        outFile << "Recommendation: Arresting top 3 criminals will disrupt "
            << (criminalConnections[0].first + criminalConnections[1].first
                + criminalConnections[2].first)
            << " criminal connections." << endl;
    }
    else {
        outFile << "Note: Network is too small for detailed recommendations." << endl;
    }

    outFile << endl;
}