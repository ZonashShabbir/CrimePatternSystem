// hash_table.h
#ifndef HASH_TABLE_H
#define HASH_TABLE_H

#include "crime_record.h"
#include <vector>
#include <list>
#include <iostream>

class HashTable {
private:
    static const int TABLE_SIZE = 100;
    vector<list<CrimeRecord>> table;
    int totalRecords;

    // Hash function - converts crime ID to index
    int hashFunction(const string& key) {
        int hash = 0;
        for (char c : key) {
            hash = (hash * 31 + c) % TABLE_SIZE;
        }
        return abs(hash);
    }

public:
    // Constructor
    HashTable();

    // Insert new crime record
    bool insert(const CrimeRecord& crime);

    // Search for crime by ID
    CrimeRecord* search(const string& crimeID);

    // Delete crime by ID
    bool remove(const string& crimeID);

    // Update existing crime
    bool update(const string& crimeID, const CrimeRecord& newData);

    // Display all records
    void displayAll() const;

    // Get total number of records
    int getSize() const;

    // Check if table is empty
    bool isEmpty() const;

    // Get all records as vector
    vector<CrimeRecord> getAllRecords() const;
};

#endif#pragma once
