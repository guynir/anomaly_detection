package com.ks.detector.source;

import org.apache.flink.connector.datagen.source.GeneratorFunction;

import java.util.Random;

/**
 * A generator for fake device data.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class FakeDeviceEventsGenerator implements GeneratorFunction<Long, Event> {

    /**
     * Randomizer for generating minor values deviations.
     */
    private static final Random rand = new Random();

    /**
     * Generator will produce anomaly every 20 values.
     */
    public static final int ANOMALY_STEP_FACTOR = 20;

    /**
     * The generator will produce temperature with variation of +/-NOISE_RANGE.
     */
    public static final float NOISE_RANGE = 1.5f;

    /**
     * Class constructor.
     */
    public FakeDeviceEventsGenerator() {
    }

    /**
     * Produce a fake event.
     *
     * @param value Index value to produce event by.
     * @return New event.
     */
    @Override
    public Event map(Long value) {
        String deviceId = "device-" + Thread.currentThread().getId();
        long eventTime = System.currentTimeMillis();

        // Generate anomaly every 20 samples.
        float baseTemperature = value % ANOMALY_STEP_FACTOR != 0 ? 10.0f : 32.0f;

        // Add some noise to each temperature value (+/-1.5 degrees).
        float adjustedTemperature = baseTemperature + rand.nextFloat(2 * NOISE_RANGE) - NOISE_RANGE;

        return new Event(deviceId, eventTime, adjustedTemperature);
    }

}
