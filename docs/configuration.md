# Configuration overview

## Main components

For main parts in this implementation, please refer to [component documentation](components.md)

## Injection of resources

The entire implementation relies on IoC concept: all required dependencies are injected, rather
than tightly-coupled. As an example, the _AnomalyDetectionService_, which is responsible for performing the
actual anomaly detection, requires the following properties and dependencies:

- Minimum number of data points to start evaluating anomalies. This should be equal to the number of data points a
  device emitted in 1 minute.
- Number of standard deviations that should be considered as an anomaly.
- A function that extracts a measurement point from a data point. This externalizes the extraction of a measurement
  point from a data point, allowing the service to handle events of different types without knowing their internal
  structure.

```java
    final int SAMPLE_POINTS_PER_MINUTE = 100;
    final int STANDARD_DEVIATION_COUNT = 3;

    // Create a new anomaly detection service with injected properties and measurement extraction strategy.
    MeasurementValueExtractor<Event> measurementExtractor = e -> e.temperature;

    var service = new AnomalyDetectionService(SAMPLE_POINTS_PER_MINUTE,
                                              STANDARD_DEVIATION_COUNT,
                                              measurementExtractor);
```

## Generic approach to data processing

The entire implementation, both the anomaly detection service and the pipeline service (which wraps Flink) are all
based on generic type.
This allows a future extension of the implementation to support any type of data point with any type of **single** 
measurement point (e.g.: scalar such as _temperature_).

For example, it allows measuring of noise or electrical charges with any configuration of window size or deviation
count.
