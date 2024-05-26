package com.ks.detector.jobs;

import java.io.Serializable;

/**
 * An anomaly alert represents an alert related to an event which was found as "ouf of bounds" (exceeded allowed
 * thresholds).
 *
 * @author Guy Raz Nir
 * @since 2024/05/25
 */
public class AnomalyAlert<E extends Serializable> implements Serializable {

    /**
     * The event which triggered the alert.
     */
    public E event;

    /**
     * Default class constructor.
     * <p>
     * Typically used by FLink deserializer.
     */
    public AnomalyAlert() {
    }

    /**
     * Class constructor.
     *
     * @param event Event which triggered the alert.
     */
    public AnomalyAlert(E event) {
        this.event = event;
    }
}
