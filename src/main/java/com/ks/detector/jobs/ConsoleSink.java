package com.ks.detector.jobs;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.Serializable;

/**
 * An FLink sink that simply dumps to console events that were marked as "out of range".
 *
 * @author Guy Raz Nir
 * @since 2024/05/25
 */
public class ConsoleSink<E extends Serializable> implements SinkFunction<AnomalyAlert<E>> {

    /**
     * Dump an event associated with an alert. Each alert represents an event which has deviated out of a given
     * threshold.
     *
     * @param alert   Alert to dump.
     * @param context Provide access to Alert's FLink metadata.
     */
    @Override
    public void invoke(AnomalyAlert<E> alert, Context context) {
        System.out.println(alert.event);
    }
}
