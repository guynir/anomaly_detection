# Main components

## General

The implementation contains two core parts (accompanied by additional classes):

## Anomaly detection service

This service is responsible for detecting anomalies based on a sequence of data points. To configure it, during
construction time, all required properties and strategies can be injected:

```java
    final int SAMPLE_POINTS_PER_MINUTE = 100;
    final int STANDARD_DEVIATION_COUNT = 3;

    // Create a new anomaly detection service with injected properties and measurement extraction strategy.
    MeasurementValueExtractor<Event> measurementExtractor = e -> e.temperature;

    var service = new AnomalyDetectionService(SAMPLE_POINTS_PER_MINUTE,
                                              STANDARD_DEVIATION_COUNT,
                                              measurementExtractor);
```

## Pipeline service

This is a wrapper for Flink pipeline implementation. It accepts properties and strategies so that every part of
its behavior can be externally configured:

```java
    // Initialize Flink stream execution environment.
    StreamExecutionEnvironment env = ...
    
    // Create a new data source (e.g.: from Kafka, database query, CSV, .....).
    Source<E, S extends SourceSplit, CT> source = ...

    // Instantiate a new anomaly detection service with all required dependencies.
    AnomalyDetectionService<E> detectionService = ...
    
    // Strategy for extracting a key (e.g.: deviceId) out of a data point.
    KeySelector keySelector = ...
    
    // Create a configured pipeline.
    PipelineService<Event, String> pipeline = new PipelineService<>(env,
        source,
        detectionService,
        keySelector);
```

A pipeline must be firstly initialized before being used:

```java
    pipeline.initialize();
```

To execute a pipeline, simple invoke the _execute_ method:
```java
    pipeline.execute();
```

