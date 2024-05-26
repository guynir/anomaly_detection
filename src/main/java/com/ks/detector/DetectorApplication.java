package com.ks.detector;

import com.ks.detector.services.detector.AnomalyDetectionService;
import com.ks.detector.services.detector.DefaultAnomalyDetectionService;
import com.ks.detector.services.detector.MeasurementValueExtractor;
import com.ks.detector.services.pipeline.PipelineService;
import com.ks.detector.source.Event;
import com.ks.detector.source.FakeDeviceEventsGenerator;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Application entry point.
 *
 * @author Guy Raz Nir
 * @since 2024/05/25
 */
public class DetectorApplication {

    /**
     * Minimum number of samples required before starting to calculate standard deviation.
     */
    private static final int MIN_SAMPLE_SIZE = 100;

    /**
     * Range of standard deviation which beyond it, a measurement point is considered "out of range".
     */
    private static final int STANDARD_DEVIATION_COUNT = 3;

    public static class EventKeySelector implements KeySelector<Event, String> {

        @Override
        public String getKey(Event event) {
            return event.deviceId;
        }
    }

    /**
     * Application's entry point.
     *
     * @param args Command-line arguments.
     * @throws Exception On any application exception.
     */
    public static void main(String[] args) throws Exception {
        MeasurementValueExtractor<Event> measurementExtractor = e -> e.temperature;

        //
        // Define a source for events.
        //
        DataGeneratorSource<Event> source = new DataGeneratorSource<>(
                new FakeDeviceEventsGenerator(),
                4000,
                TypeInformation.of(Event.class));

        //
        // Anomaly detection service.
        //
        AnomalyDetectionService<Event> detectionService =
                new DefaultAnomalyDetectionService<>(MIN_SAMPLE_SIZE,
                        STANDARD_DEVIATION_COUNT,
                        measurementExtractor);

        //
        // Create a local execution environment.
        // Typically, we would use 'StreamExecutionEnvironment.getExecutionEnvironment()' to generate an execution
        // environment based on external configuration and available connectors.
        // But for the sake of demonstration, we are forcing local execution.
        //
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment()) {
            PipelineService<Event, String> pipeline = new PipelineService<>(env,
                    source,
                    detectionService,
                    new EventKeySelector());

            pipeline.initialize();
            pipeline.execute();
        }
    }

}
