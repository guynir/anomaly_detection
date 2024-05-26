package com.ks.detector.services.detector;

import java.io.Serializable;

/**
 * A functional definition to extracting a measurement point out of an event.
 *
 * @param <E> Generic type of event.
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
@FunctionalInterface
public interface MeasurementValueExtractor<E> extends Serializable {

    /**
     * Extract measurement from event.
     *
     * @param event Event to extract from.
     * @return Event's measurement point.
     */
    double getMeasurement(E event);

}
