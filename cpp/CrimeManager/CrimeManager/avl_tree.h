// avl_tree.h
#ifndef AVL_TREE_H
#define AVL_TREE_H

#include "crime_record.h"
#include <algorithm>
#include <iostream>

// AVL Tree Node
struct AVLNode {
    CrimeRecord data;
    AVLNode* left;
    AVLNode* right;
    int height;

    AVLNode(const CrimeRecord& crime)
        : data(crime), left(nullptr), right(nullptr), height(1) {
    }
};

class AVLTree {
private:
    AVLNode* root;

    // Get height of node
    int getHeight(AVLNode* node);

    // Get balance factor
    int getBalance(AVLNode* node);

    // Update height
    void updateHeight(AVLNode* node);

    // Right rotation
    AVLNode* rotateRight(AVLNode* y);

    // Left rotation
    AVLNode* rotateLeft(AVLNode* x);

    // Insert node (recursive)
    AVLNode* insertNode(AVLNode* node, const CrimeRecord& crime);

    // Inorder traversal (recursive)
    void inorderTraversal(AVLNode* node) const;

    // Search by severity range
    void searchBySeverityRange(AVLNode* node, int minSev, int maxSev) const;

    // Search by date
    void searchByDate(AVLNode* node, const string& date) const;

    // Delete tree (cleanup)
    void deleteTree(AVLNode* node);

public:
    // Constructor
    AVLTree();

    // Destructor
    ~AVLTree();

    // Insert crime
    void insert(const CrimeRecord& crime);

    // Display sorted by severity
    void displaySorted() const;

    // Display by severity range
    void displayBySeverityRange(int minSeverity, int maxSeverity) const;

    // Display by date
    void displayByDate(const string& date) const;

    // Check if empty
    bool isEmpty() const;
};

#endif#pragma once
