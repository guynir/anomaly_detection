package com.ks.detector.services.detector;

/**
 * This is the core business logic that performs the actual extraction of data point and check if data point has
 * exceeded any threshold.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public interface AnomalyDetectionService<E> {

    /**
     * @return A new container with preconfigured buffer size.
     */
    SampleContainer createContainer();

    /**
     * Test if a given event is considered an anomaly or not. All events' measurement points are eventually appended to
     * the container.
     *
     * @param container Container holding historical measurement points.
     * @param event     Event to extract measurement point.
     * @return {@code true} if provided event contains anomal measurement, {@code false} if not.
     */
    boolean isAnomaly(SampleContainer container, E event);

}
