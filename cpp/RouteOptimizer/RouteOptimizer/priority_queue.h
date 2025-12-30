
#ifndef PRIORITY_QUEUE_H
#define PRIORITY_QUEUE_H

#include <vector>
#include <string>
#include <stdexcept>
#include <iostream>

using namespace std;

// Min-Heap Priority Queue for Dijkstra's Algorithm
template<typename T>
class MinHeap {
private:
    vector<T> heap;

    // Get parent index
    int parent(int i) { return (i - 1) / 2; }

    // Get left child index
    int leftChild(int i) { return 2 * i + 1; }

    // Get right child index
    int rightChild(int i) { return 2 * i + 2; }

    // Swap two elements
    void swap(T& a, T& b) {
        T temp = a;
        a = b;
        b = temp;
    }

    // Heapify up (bubble up)
    void heapifyUp(int index) {
        while (index > 0 && heap[parent(index)] > heap[index]) {
            swap(heap[parent(index)], heap[index]);
            index = parent(index);
        }
    }

    // Heapify down (bubble down)
    void heapifyDown(int index) {
        int smallest = index;
        int left = leftChild(index);
        int right = rightChild(index);

        if (left < heap.size() && heap[left] < heap[smallest]) {
            smallest = left;
        }

        if (right < heap.size() && heap[right] < heap[smallest]) {
            smallest = right;
        }

        if (smallest != index) {
            swap(heap[index], heap[smallest]);
            heapifyDown(smallest);
        }
    }

public:
    // Constructor
    MinHeap() {}

    // Insert element
    void push(const T& value) {
        heap.push_back(value);
        heapifyUp(heap.size() - 1);
    }

    // Extract minimum element
    T pop() {
        if (heap.empty()) {
            throw runtime_error("Heap is empty!");
        }

        T minValue = heap[0];
        heap[0] = heap.back();
        heap.pop_back();

        if (!heap.empty()) {
            heapifyDown(0);
        }

        return minValue;
    }

    // Get minimum element without removing
    T top() const {
        if (heap.empty()) {
            throw runtime_error("Heap is empty!");
        }
        return heap[0];
    }

    // Check if empty
    bool empty() const {
        return heap.size() == 0;
    }

    // Get size
    int size() const {
        return heap.size();
    }

    // Clear heap
    void clear() {
        heap.clear();
    }

    // Display heap
    void display() const {
        cout << "Heap: ";
        for (const T& val : heap) {
            cout << val << " ";
        }
        cout << endl;
    }
};

#endif
