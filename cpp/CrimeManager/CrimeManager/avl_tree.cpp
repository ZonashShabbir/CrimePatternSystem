// avl_tree.cpp
#include "avl_tree.h"

// Constructor
AVLTree::AVLTree() : root(nullptr) {}

// Destructor
AVLTree::~AVLTree() {
    deleteTree(root);
}

// Delete tree (cleanup)
void AVLTree::deleteTree(AVLNode* node) {
    if (node != nullptr) {
        deleteTree(node->left);
        deleteTree(node->right);
        delete node;
    }
}

// Get height
int AVLTree::getHeight(AVLNode* node) {
    return (node == nullptr) ? 0 : node->height;
}

// Get balance factor
int AVLTree::getBalance(AVLNode* node) {
    return (node == nullptr) ? 0 : getHeight(node->left) - getHeight(node->right);
}

// Update height
void AVLTree::updateHeight(AVLNode* node) {
    if (node != nullptr) {
        node->height = 1 + max(getHeight(node->left), getHeight(node->right));
    }
}

// Right rotation
AVLNode* AVLTree::rotateRight(AVLNode* y) {
    AVLNode* x = y->left;
    AVLNode* T2 = x->right;

    // Perform rotation
    x->right = y;
    y->left = T2;

    // Update heights
    updateHeight(y);
    updateHeight(x);

    return x;
}

// Left rotation
AVLNode* AVLTree::rotateLeft(AVLNode* x) {
    AVLNode* y = x->right;
    AVLNode* T2 = y->left;

    // Perform rotation
    y->left = x;
    x->right = T2;

    // Update heights
    updateHeight(x);
    updateHeight(y);

    return y;
}

// Insert node (recursive)
AVLNode* AVLTree::insertNode(AVLNode* node, const CrimeRecord& crime) {
    // Normal BST insertion
    if (node == nullptr) {
        return new AVLNode(crime);
    }

    // FIXED: Better comparison for duplicates
    if (crime.severity < node->data.severity) {
        node->left = insertNode(node->left, crime);
    }
    else if (crime.severity > node->data.severity) {
        node->right = insertNode(node->right, crime);
    }
    else {
        // FIXED: Same severity - compare by Crime ID to avoid issues
        if (crime.crimeID < node->data.crimeID) {
            node->left = insertNode(node->left, crime);
        }
        else {
            node->right = insertNode(node->right, crime);
        }
    }

    // Update height
    node->height = 1 + max(getHeight(node->left), getHeight(node->right));

    // Get balance factor
    int balance = getBalance(node);

    // Left Left Case
    if (balance > 1 && crime.severity < node->left->data.severity) {
        return rotateRight(node);
    }

    // Right Right Case
    if (balance < -1 && crime.severity > node->right->data.severity) {
        return rotateLeft(node);
    }

    // Left Right Case
    if (balance > 1 && crime.severity >= node->left->data.severity) {
        node->left = rotateLeft(node->left);
        return rotateRight(node);
    }

    // Right Left Case
    if (balance < -1 && crime.severity <= node->right->data.severity) {
        node->right = rotateRight(node->right);
        return rotateLeft(node);
    }

    return node;
}
// Insert crime
void AVLTree::insert(const CrimeRecord& crime) {
    root = insertNode(root, crime);
    // Silent insert - no console output
}
// Inorder traversal
void AVLTree::inorderTraversal(AVLNode* node) const {
    if (node != nullptr) {
        inorderTraversal(node->left);

        cout << "\n???????????????????????????????????????" << endl;
        cout << "? Crime ID: " << node->data.crimeID << endl;
        cout << "? Severity: " << node->data.severity << "/10" << endl;
        cout << "? Type: " << node->data.crimeType << endl;
        cout << "? Location: " << node->data.location << endl;
        cout << "? Date: " << node->data.date << endl;
        cout << "???????????????????????????????????????" << endl;

        inorderTraversal(node->right);
    }
}

// Display sorted
void AVLTree::displaySorted() const {
    if (root == nullptr) {
        cout << "\nNo records in AVL Tree." << endl;
        return;
    }

    cout << "\n?????????????????????????????????????????????????????????" << endl;
    cout << "?     CRIMES SORTED BY SEVERITY (AVL TREE)             ?" << endl;
    cout << "?     (Low to High Severity)                           ?" << endl;
    cout << "?????????????????????????????????????????????????????????" << endl;

    inorderTraversal(root);
}

// Search by severity range
void AVLTree::searchBySeverityRange(AVLNode* node, int minSev, int maxSev) const {
    if (node == nullptr) return;

    // Search left subtree if range includes values smaller than node
    if (minSev < node->data.severity) {
        searchBySeverityRange(node->left, minSev, maxSev);
    }

    // If current node is in range, display it
    if (node->data.severity >= minSev && node->data.severity <= maxSev) {
        cout << "\n???????????????????????????????????????" << endl;
        node->data.display();
        cout << "???????????????????????????????????????" << endl;
    }

    // Search right subtree if range includes values larger than node
    if (maxSev > node->data.severity) {
        searchBySeverityRange(node->right, minSev, maxSev);
    }
}

// Display by severity range
void AVLTree::displayBySeverityRange(int minSeverity, int maxSeverity) const {
    if (root == nullptr) {
        cout << "\nNo records in AVL Tree." << endl;
        return;
    }

    cout << "\n?????????????????????????????????????????????????????????" << endl;
    cout << "?  CRIMES WITH SEVERITY BETWEEN " << minSeverity << " AND " << maxSeverity << "          ?" << endl;
    cout << "?????????????????????????????????????????????????????????" << endl;

    searchBySeverityRange(root, minSeverity, maxSeverity);
}

// Search by date
void AVLTree::searchByDate(AVLNode* node, const string& date) const {
    if (node != nullptr) {
        searchByDate(node->left, date);

        if (node->data.date == date) {
            cout << "\n???????????????????????????????????????" << endl;
            node->data.display();
            cout << "???????????????????????????????????????" << endl;
        }

        searchByDate(node->right, date);
    }
}

// Display by date
void AVLTree::displayByDate(const string& date) const {
    if (root == nullptr) {
        cout << "\nNo records in AVL Tree." << endl;
        return;
    }

    cout << "\n?????????????????????????????????????????????????????????" << endl;
    cout << "?  CRIMES ON DATE: " << date << "                     ?" << endl;
    cout << "?????????????????????????????????????????????????????????" << endl;

    searchByDate(root, date);
}

// Check if empty
bool AVLTree::isEmpty() const {
    return root == nullptr;
}