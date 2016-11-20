package com.zurich.authenticator.data.outlier;

import com.zurich.authenticator.data.DummyData.DummyDataBatch;
import com.zurich.authenticator.data.DummyData.DummyDataBuilder;
import com.zurich.authenticator.data.DummyData.DummyDataException;
import com.zurich.authenticator.data.DummyData.DummyDataManipulator;
import com.zurich.authenticator.data.batch.DataBatch;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.data.feature.manager.FeatureManager;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class LocalOutlierCalculatorTest {

    private int k = LocalOutlierCalculator.DEFAULT_K;
    private final float[] clusters = new float[]{-5, 5};
    private final float clusterVariance = 0.25f;
    private List<Float> existingValues;

    public static DataBatch createDummyDataBatch(final float[] clusters, final float clusterVariance) throws DummyDataException {
        DummyDataBatch dummyDataBatch = DummyDataBuilder.forFeatureData()
                .withItemCount(500)
                .withDummyDataManipulator(new DummyDataManipulator<FeatureData>() {
                    @Override
                    public FeatureData manipulateData(int index, FeatureData data) {
                        float value = clusters[index % clusters.length];
                        float variance = (float) (Math.random() * clusterVariance * 2) - clusterVariance;
                        value += variance;
                        data.setValues(new float[]{value, value, value});
                        return data;
                    }
                })
                .build();
        return dummyDataBatch.getDataBatch();
    }

    public static List<Float> createDummyValues(final float[] clusters, final float clusterVariance) throws DummyDataException {
        DataBatch dataBatch = createDummyDataBatch(clusters, clusterVariance);
        int dimension = 0;
        float[] valuesInDimension = FeatureManager.getValuesInDimension(dataBatch.getDataList(), dimension);
        List<Float> existingValues = new ArrayList<>(valuesInDimension.length);
        for (float value : valuesInDimension) {
            existingValues.add(value);
        }
        return existingValues;
    }

    public static float calculateLocalOutlierFactor(float newValue, List<Float> existingValues, int k) {
        Map<Float, LocalOutlierCalculator.NearestNeighborsDistances> nearestNeighborsDistances = LocalOutlierCalculator.calculateNearestNeighborsDistances(existingValues, k);
        Map<Float, Float> localReachabilityDensities = LocalOutlierCalculator.calculateLocalReachabilityDensities(nearestNeighborsDistances, k);
        return LocalOutlierCalculator.calculateLocalOutlierFeature(newValue, existingValues, nearestNeighborsDistances, localReachabilityDensities, k);
    }

    @Before
    public void initialize() throws Exception {
        existingValues = createDummyValues(clusters, clusterVariance);
    }

    @Test
    public void calculateLocalOutlierFeature_inlier_isNoOutlier() throws Exception {
        for (float clusterCenter : clusters) {
            // the center of each cluster should definitely be an inlier
            float lof = calculateLocalOutlierFactor(clusterCenter, existingValues, k);
            assertFalse(LocalOutlierCalculator.isOutlier(lof));
        }
    }

    @Test
    public void calculateLocalOutlierFeature_outlier_isOutlier() throws Exception {
        for (float clusterCenter : clusters) {
            // create a value that is not part of this cluster
            // caution: make sure that it's not part of a different cluster
            float outlier = clusterCenter + (2 * clusterVariance);
            float lof = calculateLocalOutlierFactor(outlier, existingValues, k);
            assertTrue(LocalOutlierCalculator.isOutlier(lof));
        }
    }

}