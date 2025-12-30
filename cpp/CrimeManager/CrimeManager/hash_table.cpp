// hash_table.cpp
#include "hash_table.h"

HashTable::HashTable() : totalRecords(0) {
    table.resize(TABLE_SIZE);
}

// Insert crime record
bool HashTable::insert(const CrimeRecord& crime) {
    int index = hashFunction(crime.crimeID);

    for (auto& record : table[index]) {
        if (record.crimeID == crime.crimeID) {
            // Silent - don't print during bulk load
            return false;
        }
    }

    table[index].push_front(crime);
    totalRecords++;

    // Silent - don't print during bulk load
    return true;
}
// Search for crime by ID
CrimeRecord* HashTable::search(const string& crimeID) {
    int index = hashFunction(crimeID);

    // Search in the linked list at this index
    for (auto& record : table[index]) {
        if (record.crimeID == crimeID) {
            return &record;
        }
    }

    return nullptr;  // Not found
}

// Delete crime record
bool HashTable::remove(const string& crimeID) {
    int index = hashFunction(crimeID);

    // Search and remove from the list
    for (auto it = table[index].begin(); it != table[index].end(); ++it) {
        if (it->crimeID == crimeID) {
            table[index].erase(it);
            totalRecords--;
            cout << "? Crime record '" << crimeID << "' deleted successfully!" << endl;
            return true;
        }
    }

    cout << "? Error: Crime ID '" << crimeID << "' not found!" << endl;
    return false;
}

// Update crime record
bool HashTable::update(const string& crimeID, const CrimeRecord& newData) {
    CrimeRecord* record = search(crimeID);

    if (record != nullptr) {
        // Update all fields except ID
        record->crimeType = newData.crimeType;
        record->location = newData.location;
        record->date = newData.date;
        record->severity = newData.severity;
        record->suspects = newData.suspects;
        record->description = newData.description;

        cout << "? Crime record '" << crimeID << "' updated successfully!" << endl;
        return true;
    }

    cout << "? Error: Crime ID '" << crimeID << "' not found!" << endl;
    return false;
}

// Display all records
void HashTable::displayAll() const {
    if (totalRecords == 0) {
        cout << "\nNo crime records found in database." << endl;
        return;
    }

    cout << "\n??????????????????????????????????????????????????????????" << endl;
    cout << "?           ALL CRIME RECORDS (HASH TABLE)              ?" << endl;
    cout << "??????????????????????????????????????????????????????????" << endl;
    cout << "Total Records: " << totalRecords << endl;
    cout << "??????????????????????????????????????????????????????????" << endl;

    int count = 0;
    for (int i = 0; i < TABLE_SIZE; i++) {
        for (const auto& record : table[i]) {
            cout << "\n[Record #" << ++count << "]" << endl;
            record.display();
            cout << "??????????????????????????????????????????????????????????" << endl;
        }
    }
}

// Get total number of records
int HashTable::getSize() const {
    return totalRecords;
}

// Check if empty
bool HashTable::isEmpty() const {
    return totalRecords == 0;
}

// Get all records
vector<CrimeRecord> HashTable::getAllRecords() const {
    vector<CrimeRecord> records;

    for (int i = 0; i < TABLE_SIZE; i++) {
        for (const auto& record : table[i]) {
            records.push_back(record);
        }
    }

    return records;
}
