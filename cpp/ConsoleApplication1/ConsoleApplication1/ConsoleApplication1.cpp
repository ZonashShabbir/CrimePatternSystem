#include <iostream>
#include <iomanip>
using namespace std;

int main() {
    // Student Info
    cout << "Name: Zonash Shabbir\n";
    long long regNo = 242681;

    int digits[5];
    int sumDigits = 0;

    long long temp = regNo;
    for (int i = 0; i < 5; i++) {
        digits[i] = temp % 10;
        sumDigits += digits[i];
        temp /= 10;
    }

    double cdf = 0;
    cout << "Outcome\tProbability\tCDF\n";

    for (int i = 0; i < 5; i++) {
        double prob = (double)digits[i] / sumDigits;
        cdf += prob;

        cout << (i + 1) << "\t"
            << fixed << setprecision(4) << prob << "\t\t"
            << cdf << endl;
    }

    return 0;
}
