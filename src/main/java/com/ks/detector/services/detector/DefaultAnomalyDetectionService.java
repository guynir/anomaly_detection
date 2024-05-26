package com.ks.detector.services.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * A concrete implementation that extracts measurement point out of an event (data point) and check if it is an anomaly.
 *
 * @param <E> Generic type of event.
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class DefaultAnomalyDetectionService<E> implements AnomalyDetectionService<E>, Serializable {

    /**
     * Number of standard deviations which reflect the valid range of measurement points. Events with measurements
     * beyond that are considered anomalies.
     */
    private final int standardDeviationCount;

    /**
     * Minimum number of samples required before starting to calculate standard deviation and detect anomalies.
     */
    private final int minimumSampleCount;

    /**
     * Function that extracts measurement point from an event.
     */
    protected final MeasurementValueExtractor<E> measurementExtractionStrategy;

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultAnomalyDetectionService.class);

    /**
     * Class constructor.
     *
     * @param standardDeviationCount        Number of standard deviations to consider as value range.
     * @param minimumSampleCount            Minimum number of samples required before calculating deviation.
     * @param measurementExtractionStrategy A strategy for extracting the actual measurement point of an event.
     * @throws IllegalArgumentException If either argument is missing or invalid.
     */
    public DefaultAnomalyDetectionService(int standardDeviationCount,
                                          int minimumSampleCount,
                                          MeasurementValueExtractor<E> measurementExtractionStrategy)
            throws IllegalArgumentException {
        Assert.isTrue(standardDeviationCount > 0, "Standard deviation count must be greater than 0");
        Assert.isTrue(minimumSampleCount > 0, "Minimum sample count must be greater than 0");
        Assert.notNull(measurementExtractionStrategy, "Measurement strategy cannot be null");

        this.standardDeviationCount = standardDeviationCount;
        this.minimumSampleCount = minimumSampleCount;
        this.measurementExtractionStrategy = measurementExtractionStrategy;
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public SampleContainer createContainer() {
        return new SampleContainer(minimumSampleCount);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public boolean isAnomaly(SampleContainer container, E event) {
        double measurement = measurementExtractionStrategy.getMeasurement(event);

        //
        // If we have enough data, check the event's data is out of range or not.
        //
        boolean result = false;
        if (container.isFull()) {
            double average = container.average();
            double standardDeviation = container.standardDeviation();
            double deviationThreshold = standardDeviation * standardDeviationCount;

            result = Math.abs(measurement - average) > deviationThreshold;
        }

        // Add latest measurement point to our container.
        container.add(measurement);

        return result;
    }
}
