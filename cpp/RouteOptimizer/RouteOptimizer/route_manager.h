// route_manager.h
#ifndef ROUTE_MANAGER_H
#define ROUTE_MANAGER_H

#include <string>
#include <vector>
#include <map>
using namespace std;

// Structure to store location information
struct LocationInfo {
    string id;
    string name;
    string type;  // "CRIME_HOTSPOT", "POLICE_STATION", "CHECKPOINT", "NORMAL"
    int crimeCount;  // Number of crimes at this location

    LocationInfo() : crimeCount(0) {}

    LocationInfo(string locId, string locName, string locType, int crimes = 0)
        : id(locId), name(locName), type(locType), crimeCount(crimes) {
    }
};

// Route Manager - manages location metadata
class RouteManager {
private:
    map<string, LocationInfo> locationData;

public:
    // Add location
    void addLocation(const string& id, const string& name, const string& type, int crimeCount = 0);

    // Get location info
    LocationInfo* getLocation(const string& id);

    // Update crime count
    void updateCrimeCount(const string& id, int count);

    // Display location details
    void displayLocationDetails(const string& id) const;

    // Display all locations
    void displayAllLocations() const;

    // Get hotspots (locations with high crime)
    vector<string> getHotspots(int minCrimeCount = 5) const;

    // Check if location exists
    bool hasLocation(const string& id) const;
};

#endif