// crime_record.h
#ifndef CRIME_RECORD_H
#define CRIME_RECORD_H

#include <string>
#include<iostream>
using namespace std;

// Crime record structure
struct CrimeRecord {
    string crimeID;
    string crimeType;
    string location;
    string date;
    int severity;
    string suspects;
    string description;

    // Default constructor
    CrimeRecord() : severity(0) {}

    // Parameterized constructor
    CrimeRecord(string id, string type, string loc, string dt, int sev, string susp, string desc = "") {
        crimeID = id;
        crimeType = type;
        location = loc;
        date = dt;
        severity = sev;
        suspects = susp;
        description = desc;
    }

    // Display function
    void display() const {
        cout << "Crime ID: " << crimeID << endl;
        cout << "Type: " << crimeType << endl;
        cout << "Location: " << location << endl;
        cout << "Date: " << date << endl;
        cout << "Severity: " << severity << "/10" << endl;
        cout << "Suspects: " << suspects << endl;
        if (!description.empty()) {
            cout << "Description: " << description << endl;
        }
    }
};

#endif#pragma once
