# Anomaly detector based on Apache Flink

## General

This is a sample project for illustrating the usage of Flink to detect anomalies in a data stream.
Anomalies are classified as data points with measurement points, which is beyond a given standard deviation.

For example, given a set of device data points reporting on device temperature, if one of the events
contains temperature which is beyond a standard deviation, the event is reported as an anomaly.

## Hold to build and run

To build the project, simply use _gradlew_ wrapper via the command line:

For Windows environments:
    
> gradlew clean build run

For Linux-based environments
> ./gradlew.sh clean build run

## Further reading

You can refer to the following documents for further reading:

- [Project anatomy and configuration using IoC](docs/configuration)
- [Anomaly detection strategy overview](docs/strategy_overview.md)
- [Main component parts](docs/components.md)
