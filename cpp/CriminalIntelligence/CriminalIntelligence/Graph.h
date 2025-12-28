#ifndef GRAPH_H
#define GRAPH_H

#include <iostream>
#include <vector>
#include <climits>
#include <algorithm>
#include <fstream>
using namespace std;

struct Edge {
    int src, dest, weight;
    string criminal1, criminal2;
};

class Graph {
private:
    int V;
    vector<Edge> edges;
    vector<vector<int>> adjMatrix;
    vector<string> criminalNames;

    int find(vector<int>& parent, int i);
    void unionSet(vector<int>& parent, vector<int>& rank, int x, int y);

public:
    Graph(int vertices, vector<string> names);
    void addEdge(int src, int dest, int weight, string c1, string c2);
    void kruskalMST(ofstream& outFile);
    void primMST(ofstream& outFile);
    void floydWarshall(ofstream& outFile);
    void findKeyCriminals(ofstream& outFile);
};

#endif