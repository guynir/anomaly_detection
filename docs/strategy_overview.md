# Anomaly detection strategy

## Initial requirements

The strategy chosen to detect anomalies relies on the following requirement:

- A device reports N data points (emmit events) per minute with timestamp and temperature.
- If a measurement point (e.g.: temperature) exceeds a 1 minute (i.e., within N data points) three times standard
  deviation, the data point is considered an anomaly.

For example, if a device provides 100 reports per 1 minute with an average temperature of 10.0 degrees Celsius and a
standard deviation of 1.1, if a data point contains a temperature of 25.0 degress Celsius, this event
is considered an anomaly.

## Implementation

The implementation uses a moving window to detect anomalies within a stream. As an example:

Window at time T(x):

    +-------------------------------------------------------------------------------+
    | p34 | p35 | p36 | p37 | p38 | p38 | ...... | p135 | p136 | p137 | p138 | .... | 
    +-------------------------------------------------------------------------------+

           ^----------------- Window T(x) --------------^

Window at time T(x+1):

    +-------------------------------------------------------------------------------+
    | p34 | p35 | p36 | p37 | p38 | p38 | ...... | p135 | p136 | p137 | p138 | .... | 
    +-------------------------------------------------------------------------------+

                 ^----------------- Window T(x+1) -------------^

Window at time T(x+2):

    +-------------------------------------------------------------------------------+
    | p34 | p35 | p36 | p37 | p38 | p38 | ...... | p135 | p136 | p137 | p138 | .... | 
    +-------------------------------------------------------------------------------+

                       ^----------------- Window T(x+2) -------------^

The implementation relies on calculation of the last N data points, assuming the device always
reports the exact number of data points per minute.

## Optional alternatives considered

One of the alternatives considered was to process the data stream in a bounded approach and
determine anomalies using batch processing.

Though this is a more easy and intuitive way for Flink to process batches (using a time watermark based on event's
timestamp), it is too simple for tough guys like us.
