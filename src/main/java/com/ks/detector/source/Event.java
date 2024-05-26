package com.ks.detector.source;

import java.io.Serializable;

/**
 * Simple value object describing an event.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class Event implements Serializable {

    /**
     * Device identifier.
     */
    public String deviceId;

    /**
     * Timestamp (milliseconds since UTC) of the event.
     */
    public long time;

    /**
     * Event temperature measure point.
     */
    public float temperature;

    /**
     * Class constructor. Intended for use by Flink serializer. Should not be used directly.
     */
    public Event() {
    }

    /**
     * Class constructor.
     *
     * @param deviceId    Device identifier.
     * @param time        Event timestamp.
     * @param temperature Device temperature report.
     */
    public Event(String deviceId, long time, float temperature) {
        this.deviceId = deviceId;
        this.time = time;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Device %s, measurement %.2f C, time %d\n".formatted(deviceId, temperature, time);
    }
}
