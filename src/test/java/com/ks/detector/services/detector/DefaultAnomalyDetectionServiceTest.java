package com.ks.detector.services.detector;

import com.ks.detector.source.Event;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test suite for {@link DefaultAnomalyDetectionService}.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class DefaultAnomalyDetectionServiceTest {

    /**
     * Service under test.
     */
    private DefaultAnomalyDetectionService<Event> service;

    /**
     * Minimum number of samples required before starting classifying anomalies.
     */
    private static final int MIN_SAMPLE_COUNT = 100;

    /**
     * Test fixture -- create a new anomaly detection service for each test.
     * It is assumed the service may be stateful; therefore, we need a new instance for each new test.
     */
    @BeforeEach
    public void setUp() {
        service = new DefaultAnomalyDetectionService<>(
                1,
                MIN_SAMPLE_COUNT,
                e -> e.temperature);
    }

    /**
     * Test that the anomaly service will report no anomaly for value which is within the standard deviation.
     */
    @Test
    @DisplayName("Test should not detect anomaly")
    public void testShouldNotDetectAnyAnomalies() {
        SampleContainer container = initContainer(MIN_SAMPLE_COUNT, 40.0f);
        Event samleEvent = createEvent(40.0f);

        Assertions.assertThat(service.isAnomaly(container, samleEvent)).isFalse();
    }

    /**
     * Test that our service report an anomaly for a values which is beyond standard deviation.
     */
    @Test
    @DisplayName("Test should detect anomaly")
    public void testShouldDetectAnomaly() {
        SampleContainer container = initContainer(MIN_SAMPLE_COUNT, 40.0f);
        Event samleEvent = createEvent(42.0f);

        Assertions.assertThat(service.isAnomaly(container, samleEvent)).isTrue();
    }

    /**
     * Test that our anomaly service will not classify an out-ouf-bounds event if the minimum number of samples is
     * not fulfilled.
     */
    @Test
    @DisplayName("Test should not detect anomaly when container is not full")
    public void testShouldNotDetectAnomalyWhenContainerIsNotFull() {
        SampleContainer container = initContainer(MIN_SAMPLE_COUNT - 1, 32.0f);

        Event samleEvent = createEvent(42.0f);

        // The value of '42.0' is beyond 1 standard deviation from a collection of '40.0' values.
        // However, since the container is not full of minimum number of measurements, the faulty value
        // will not be measured as an anomaly.
        Assertions.assertThat(service.isAnomaly(container, samleEvent)).isFalse();
    }

    /**
     * Create and fill container with values.
     *
     * @param count Number of values to add to container.
     * @param value Value to set.
     * @return New and initialized container.
     */
    private SampleContainer initContainer(int count, float value) {
        SampleContainer container = service.createContainer();
        for (int c = 0; c < count; c++) {
            container.add(value);
        }

        return container;
    }

    private Event createEvent(float temperature) {
        return new Event("event", System.currentTimeMillis(), temperature);
    }

}
