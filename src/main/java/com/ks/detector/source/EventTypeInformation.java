package com.ks.detector.source;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.PojoField;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;

import java.util.Arrays;
import java.util.List;

/**
 * A helper class holding metadata required to serialize {@link Event} instance by Flink.
 *
 * @author Guy Raz Nir
 * @since 2025/05/25
 */
public class EventTypeInformation {

    /**
     * Holds Flink serializer data.
     */
    public static final TypeInformation<Event> EVENT_TYPE_INFORMATION = createEventTypeInformation();

    /**
     * @return Flink serializer type information describing {@link Event}.
     */
    private static TypeInformation<Event> createEventTypeInformation() {
        List<PojoField> eventFields;

        try {
            eventFields =
                    Arrays.asList(
                            new PojoField(Event.class.getField("deviceId"), BasicTypeInfo.STRING_TYPE_INFO),
                            new PojoField(Event.class.getField("time"), BasicTypeInfo.LONG_TYPE_INFO),
                            new PojoField(Event.class.getField("temperature"), BasicTypeInfo.FLOAT_TYPE_INFO));
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Missing required 'Event' object field.", ex);
        }

        return new PojoTypeInfo<>(Event.class, eventFields);
    }
}
