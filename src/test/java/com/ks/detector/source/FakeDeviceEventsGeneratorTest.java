package com.ks.detector.source;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.ks.detector.source.FakeDeviceEventsGenerator.NOISE_RANGE;

/**
 * Test suite for {@link FakeDeviceEventsGenerator}.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class FakeDeviceEventsGeneratorTest {

    /**
     * Generator to test.
     */
    private final FakeDeviceEventsGenerator generator = new FakeDeviceEventsGenerator();

    /**
     * Test that all standard values are between a defined range (10.0f +/- {@link FakeDeviceEventsGenerator#NOISE_RANGE})
     * and that values have variants (the sum of all values will not yield a perfect "10.0f" value).
     */
    @Test
    @DisplayName("Standard temperatures are variant")
    public void testStandardTemperaturesAreVariant() {
        List<Float> measurements = generateMeasurements(1, 1000, generator);

        // Make sure that all values are within +/-1.5 range.
        List<Float> outOfBoundValues = findOutOfBoundsValues(measurements, 10 - NOISE_RANGE, 10 + NOISE_RANGE);
        Assertions.assertThat(outOfBoundValues).isEmpty();

        double sum = measurements.stream().mapToDouble(f -> f).sum();
        Assertions.assertThat(sum).isNotEqualTo(10.0f);
    }

    /**
     * Test that all anomaly values are between a defined range (32.0f +/- {@link FakeDeviceEventsGenerator#NOISE_RANGE})
     * and that values have variants (the sum of all values will not yield a perfect "32.0f" value).
     */
    @Test
    @DisplayName("Anomaly temperatures are variant")
    public void testAnomalyTemperaturesAreVariant() {
        List<Float> measurements = generateMeasurements(0, 2000, generator);

        // Make sure that all values are within +/-1.5 range.
        List<Float> outOfBoundValues = findOutOfBoundsValues(measurements, 32 - NOISE_RANGE, 32 + NOISE_RANGE);
        Assertions.assertThat(outOfBoundValues).isEmpty();

        double sum = measurements.stream().mapToDouble(f -> f).sum();
        Assertions.assertThat(sum).isNotEqualTo(10.0f);
    }

    /**
     * Generate a series of temperatures based on a given key.
     *
     * @param key             A key to generated temperatures by.
     * @param numberOfSamples Number of samples to generate.
     * @param generator       Generator to use.
     * @return List of temperatures.
     */
    private List<Float> generateMeasurements(long key, int numberOfSamples, FakeDeviceEventsGenerator generator) {
        return IntStream.range(0, numberOfSamples).mapToObj(i -> generator.map(key)).map(e -> e.temperature).toList();
    }

    /**
     * Generate a list of all values that are out of predefined range.
     *
     * @param measurements List of values.
     * @param min          Lower bound, exclusive.
     * @param max          High bound, exclusive.
     * @return List of values which are out of bound.
     */
    private List<Float> findOutOfBoundsValues(List<Float> measurements, float min, float max) {
        return measurements.stream().filter(t -> t < min || t > max).toList();
    }
}
