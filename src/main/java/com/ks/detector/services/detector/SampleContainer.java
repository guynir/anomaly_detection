package com.ks.detector.services.detector;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Simple POJO containing data points of a device. Used to calculate mean and standard deviation.
 * <p>
 * Initially, such an object is created with a predefined size of an array for data points; however, the array
 * is filled gradually.
 *
 * @author Guy Raz Nir
 * @since 2024/05/25
 */
public class SampleContainer implements Serializable {

    /**
     * Data points accumulated.
     */
    private double[] dataPoints;

    /**
     * Number of data points in the buffer.
     */
    private int dataPointsCount;

    /**
     * Class default constructor.
     * <p>
     * Required for good-old-fashion deserialization. Should not be used directly.
     */
    public SampleContainer() {
    }

    /**
     * Class constructor.
     *
     * @param dataPoints      Initialized data points buffer.
     * @param dataPointsCount Number of actual data points in <i>dataPoints</i>.
     * @throws IllegalArgumentException If either <i>dataPoints</i> is {@code null} or has zero-length or if
     *                                  <i>dataPointsCount</i> has invalid value (negative or greater than the size
     *                                  of <i>dataPoints</i>).
     */
    public SampleContainer(double[] dataPoints, int dataPointsCount) {
        Assert.isTrue(dataPoints != null && dataPoints.length > 0, "Data points array cannot be null or empty.");
        Assert.isTrue(dataPointsCount >= 0 && dataPointsCount <= dataPoints.length, "Invalid data points size.");

        this.dataPoints = dataPoints;
        this.dataPointsCount = dataPointsCount;
    }

    /**
     * Class constructor. Should be typically called to create a new container instance.
     *
     * @param minSampleSize Sample size. Must be greater than 0.
     * @throws IllegalArgumentException If <i>minSampleSize</i> is zero or negative.
     */
    public SampleContainer(int minSampleSize) {
        Assert.isTrue(minSampleSize > 0, "Minimum sample size must be positive number.");

        this.dataPoints = new double[minSampleSize];
        this.dataPointsCount = 0;
    }

    /**
     * Provide indication if this container is full (buffer is fully filled with data points) or not.
     *
     * @return {@code true} if container is full, {@code false} if not.
     */
    public boolean isFull() {
        return dataPointsCount == dataPoints.length;
    }

    /**
     * Add (append) data point to this container. If the container is full, the oldest value is removed first.
     *
     * @param dataPoint Data point to add.
     */
    public void add(double dataPoint) {
        //
        // If our buffer is full, remove the oldest value first.
        //
        if (dataPointsCount == dataPoints.length) {
            for (int i = 0; i < dataPointsCount - 1; i++) {
                dataPoints[i] = dataPoints[i + 1];
            }
            dataPointsCount--;
        }

        // Append the latest value into the buffer.
        dataPoints[dataPointsCount++] = dataPoint;
    }

    /**
     * @return Average of all container's values. If contains is empty, the result is 0.
     */
    public double average() {
        if (dataPointsCount == 0) {
            return 0.0f;
        }

        double sum = 0;
        for (int i = 0; i < dataPointsCount; i++) {
            sum += dataPoints[i];
        }
        return sum / dataPointsCount;
    }

    /**
     * @return The standard deviation of all the values. If the container is empty, the returned value is 0.
     */
    public double standardDeviation() {
        if (dataPointsCount == 0) {
            return 0.0f;
        }

        double mean = average();

        double standardDeviation = 0.0f;
        for (double dataPoint : dataPoints) {
            standardDeviation += Math.pow(dataPoint - mean, 2);
        }

        return Math.sqrt(standardDeviation / dataPointsCount);
    }
}
