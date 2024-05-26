package com.ks.detector.jobs;

import com.ks.detector.services.detector.SampleContainer;import com.ks.detector.services.detector.AnomalyDetectionService;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;

/**
 * A Flink process function that calculates mean per device and collect all events that are three deviations
 * from the average.
 *
 * @author Guy Raz Nir
 * @since 2024/05/23
 */
public class AnomalyDetectionJob<E extends Serializable, K> extends KeyedProcessFunction<K, E, AnomalyAlert<E>> {

    /**
     * State that holds the last N data points per device. Required for calculating average and standard deviation.
     */
    private transient ValueState<SampleContainer> containerState;

    /**
     * Actual service that perform anomaly detection.
     */
    @SuppressWarnings("FieldMayBeFinal")
    private AnomalyDetectionService<E> service;

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionJob.class);

    /**
     * Plain class constructor.
     * <p>
     * Required for FLink serialization. Should not be directly used by the application.
     *
     * @see #AnomalyDetectionJob(AnomalyDetectionService).
     */
    public AnomalyDetectionJob() {
        service = null;
    }

    /**
     * Class constructor.
     */
    public AnomalyDetectionJob(AnomalyDetectionService<E> service) throws IllegalArgumentException {
        Assert.notNull(service, "Anomaly detector service cannot be null");
        this.service = service;
    }

    /***
     * Process streaming elements and collect anomalies.
     *
     * @param event The event to evaluate.
     * @param ctx A {@link Context} that allows querying the timestamp of the element and getting a
     *     {@link TimerService} for registering timers and querying the time. The context is only
     *     valid during the invocation of this method, do not store it.
     * @param out The collector for returning result values.
     * @throws IOException If any error occurs while serializing/deserializing state values.
     */
    @Override
    public void processElement(E event,
                               KeyedProcessFunction<K, E, AnomalyAlert<E>>.Context ctx,
                               Collector<AnomalyAlert<E>> out) throws IOException {

        logger.debug("Processing event for '{}'", ctx.getCurrentKey());

        SampleContainer container = getContainer();

        if (service.isAnomaly(container, event)) {
            out.collect(new AnomalyAlert<>(event));
        }

        containerState.update(container);
    }

    @Override
    public void open(OpenContext openContext) {
        var containerStateDescriptor = new ValueStateDescriptor<>("sampleContainer", SampleContainer.class);
        containerState = getRuntimeContext().getState(containerStateDescriptor);

        logger.info("Successfully initialized anomaly detection job.");
    }

    /**
     * Get the sample container from state store. If contains has not yet been initialized -- create a
     * new one (lazy-init).
     *
     * @return Sample container.
     * @throws IOException If I/O error occurred while trying to deserialize container state.
     */
    private SampleContainer getContainer() throws IOException {
        SampleContainer container = containerState.value();
        if (container == null) {
            container = service.createContainer();
            containerState.update(container);
        }

        return container;
    }

}
