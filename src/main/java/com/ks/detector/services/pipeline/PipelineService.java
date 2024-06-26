package com.ks.detector.services.pipeline;

import com.ks.detector.jobs.AnomalyAlert;import com.ks.detector.jobs.AnomalyDetectionJob;import com.ks.detector.services.detector.AnomalyDetectionService;
import com.ks.detector.jobs.ConsoleSink;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * A pipeline service that processes data stream of events and generate alerts for anomalies.
 *
 * @param <E> Generic type of events to process.
 * @param <K> Generic type of event's key (such as a device id).
 */
public class PipelineService<E extends Serializable, K extends Serializable> {

    /**
     * Flink execution environment.
     */
    private final StreamExecutionEnvironment environment;

    /**
     * Data source to that provides events.
     */
    private final Source<E, ?, ?> source;

    /**
     * Service for detecting events' anomalies.
     */
    private final AnomalyDetectionService<E> service;

    /**
     * A function that extracts an event key (such as device identifier).
     */
    private final KeySelector<E, K> keyExtractor;

    /**
     * Generated pipeline sink.
     */
    private DataStreamSink<AnomalyAlert<E>> sink = null;


    /**
     * Name of the data source.
     */
    private static final String DATA_SOURCE_NAME = "randomEventsDataSource";

    /**
     * Name of the anomaly detecting data stream.
     */
    private static final String ANOMALY_DETECTOR_NAME = "anomalyDetector";

    /**
     * Name of sink.
     */
    private static final String ANOMALY_SINK_NAME = "anomalySink";

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PipelineService.class);

    /**
     * Class constructor.
     */
    public PipelineService(StreamExecutionEnvironment environment,
                           Source<E, ?, ?> source,
                           AnomalyDetectionService<E> service,
                           KeySelector<E, K> keyExtractor) {
        this.environment = environment;
        this.source = source;
        this.service = service;
        this.keyExtractor = keyExtractor;
    }

    /**
     * Initialize the pipeline. Must be called before {@link #execute()}.
     */
    public void initialize() {
        logger.info("Initializing pipeline service.");

        //
        // Register pipeline data source.
        //
        DataStream<E> events = environment
                .fromSource(source, WatermarkStrategy.forMonotonousTimestamps(), DATA_SOURCE_NAME);

        //
        // Generate data stream of alerts from anomaly events.
        //
        DataStream<AnomalyAlert<E>> alerts = events
                .keyBy(keyExtractor)
                .process(new AnomalyDetectionJob<>(service))
                .name(ANOMALY_DETECTOR_NAME);

        //
        // Add sink that dumps alerts to console.
        //
        sink = alerts
                .addSink(new ConsoleSink<>())
                .name(ANOMALY_SINK_NAME);

        logger.info("Successfully initialized pipeline service.");
    }

    /**
     * Execute pipeline.
     *
     * @throws IllegalStateException If the pipeline was not previously initialized.
     * @throws Exception             On any error generated by the pipeline's execution.
     */
    public void execute() throws IllegalStateException, Exception {
        Assert.state(sink != null, "Pipeline not initialized (did you forget to call 'initialize()'? ).");
        try {
            logger.info("Launching pipeline execution.");
            JobExecutionResult results = environment.execute();
            logger.info("Pipeline execution complete.");
        } catch (Exception ex) {
            logger.error("Pipeline execution failed.", ex);
        } finally {
            sink = null;
        }
    }
}
