## Test Task

### Developer: Andrei Pronsky

### Overview

This project demonstrates how to read a JSON file containing flight data, calculate specific metrics for flights between specific airports, interact with a database.

### Metrics Calculated:

* Difference between median and average price
* Minimum flight time for each carrier

### Target Airports:

* Origin: VVO (Vladivostok International Airport)
* Destination: TLV (Ben Gurion International Airport)

### Project Structure

#### flight-service: 
Contains the logic for reading the JSON file, calculating metrics, and interacting with a database.


### Prerequisites:

* Docker installed and running

### Steps to deploy:

* Clone this repository.
* Build the project: ``` .\gradlew clean build ```
* Start the service and DB: ```docker compose up ```
