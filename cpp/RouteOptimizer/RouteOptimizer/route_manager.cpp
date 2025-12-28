// route_manager.cpp
#include "route_manager.h"
#include <iostream>

// Add location
void RouteManager::addLocation(const string& id, const string& name, const string& type, int crimeCount) {
    if (hasLocation(id)) {
        cout << "⚠ Location '" << id << "' already exists." << endl;
        return;
    }

    locationData[id] = LocationInfo(id, name, type, crimeCount);
    cout << "✓ Location info added: " << id << " (" << type << ")" << endl;
}

// Get location
LocationInfo* RouteManager::getLocation(const string& id) {
    if (!hasLocation(id)) return nullptr;
    return &locationData[id];
}

// Update crime count
void RouteManager::updateCrimeCount(const string& id, int count) {
    if (!hasLocation(id)) {
        cout << "✗ Location '" << id << "' not found." << endl;
        return;
    }

    locationData[id].crimeCount = count;
    cout << "✓ Crime count updated for '" << id << "': " << count << endl;
}

// Display location details
void RouteManager::displayLocationDetails(const string& id) const {
    if (!hasLocation(id)) {
        cout << "✗ Location '" << id << "' not found." << endl;
        return;
    }

    const LocationInfo& loc = locationData.at(id);

    cout << "\n┌─────────────────────────────────────┐" << endl;
    cout << "│ ID: " << loc.id << endl;
    cout << "│ Name: " << loc.name << endl;
    cout << "│ Type: " << loc.type << endl;
    cout << "│ Crime Count: " << loc.crimeCount << endl;
    cout << "└─────────────────────────────────────┘" << endl;
}

// Display all locations
void RouteManager::displayAllLocations() const {
    if (locationData.empty()) {
        cout << "\n⚠ No locations found." << endl;
        return;
    }

    cout << "\n╔══════════════════════════════════════════════════════╗" << endl;
    cout << "║           ALL LOCATIONS                              ║" << endl;
    cout << "╚══════════════════════════════════════════════════════╝" << endl;

    for (const auto& pair : locationData) {
        displayLocationDetails(pair.first);
    }
}

// Get hotspots
vector<string> RouteManager::getHotspots(int minCrimeCount) const {
    vector<string> hotspots;

    for (const auto& pair : locationData) {
        if (pair.second.crimeCount >= minCrimeCount) {
            hotspots.push_back(pair.first);
        }
    }

    return hotspots;
}

// Check if location exists
bool RouteManager::hasLocation(const string& id) const {
    return locationData.find(id) != locationData.end();
}