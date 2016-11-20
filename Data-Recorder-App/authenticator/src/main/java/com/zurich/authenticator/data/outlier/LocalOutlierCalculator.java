package com.zurich.authenticator.data.outlier;

import com.zurich.authenticator.data.calculator.DataCalculationException;
import com.zurich.authenticator.data.classification.ClassificationData;
import com.zurich.authenticator.data.feature.FeatureData;
import com.zurich.authenticator.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalOutlierCalculator extends OutlierCalculator {

    private static final String TAG = LocalOutlierCalculator.class.getSimpleName();

    public static final float OUTLIER_THRESHOLD_NONE = 0;
    public static final float OUTLIER_THRESHOLD_SMALL = 1f;
    public static final float OUTLIER_THRESHOLD_NORMAL = 1.5f;
    public static final float OUTLIER_THRESHOLD_LARGE = 5f;

    public static final int DEFAULT_K = 3;

    private List<List<Float>> existingValues;
    private List<Map<Float, NearestNeighborsDistances>> nearestNeighborsDistances;
    private List<Map<Float, Float>> localReachabilityDensities;

    private int k = DEFAULT_K;
    private int dimensions;

    /**
     * Helper class that holds the k nearest neighbors of a given value
     * and its distances.
     */
    public static class NearestNeighborsDistances {

        Float newValue;
        List<KnnCalculator.Neighbor> nearestNeighbors;

        public NearestNeighborsDistances(Float newValue, List<Float> existingValues, int k) {
            this.newValue = newValue;
            this.nearestNeighbors = KnnCalculator.getNearestNeighbors(newValue, existingValues, k);
        }

        public float getDistance(int k) {
            return nearestNeighbors.get(k).distance;
        }

    }

    public void initialize() {
        if (trainedData.size() > 0) {
            dimensions = trainedData.get(0).getValues().length;
        }
        existingValues = new ArrayList<>(dimensions);
        nearestNeighborsDistances = new ArrayList<>(dimensions);
        localReachabilityDensities = new ArrayList<>(dimensions);
    }

    @Override
    public ClassificationData calculate() throws DataCalculationException {
        if (dimensions == 0 || existingValues == null) {
            initialize();
        }
        return super.calculate();
    }

    @Override
    public List<FeatureData> detectOutliers() throws DataCalculationException {
        Set<FeatureData> detectedOutliers = new HashSet<>();
        for (int dimension = 0; dimension < dimensions; dimension++) {
            detectedOutliers.addAll(detectOutliers(dimension));
        }
        return new ArrayList<>(detectedOutliers);
    }

    public List<FeatureData> detectOutliers(int dimension) throws DataCalculationException {
        List<FeatureData> detectedOutliers = new ArrayList<>();

        for (int i = 0; i < currentData.size(); i++) {
            try {
                float localOutlierFactor = calculateLocalOutlierFeature(currentData.get(i), dimension);
                if (isOutlier(localOutlierFactor, OUTLIER_THRESHOLD_SMALL)) {
                    detectedOutliers.add(currentData.get(i));
                }
            } catch (Exception ex) {
                Logger.w(TAG, ex.getMessage());
            }
        }

        return detectedOutliers;
    }

    public List<Float> getExistingValues(int dimension) {
        if (existingValues.get(dimension) == null) {
            List<Float> values = new ArrayList<>(trainedData.size());
            for (int i = 0; i < trainedData.size(); i++) {
                values.add(trainedData.get(i).getValues()[dimension]);
            }
            existingValues.set(dimension, values);
        }
        return existingValues.get(dimension);
    }

    public Map<Float, NearestNeighborsDistances> getNearestNeighborsDistances(int dimension) {
        if (nearestNeighborsDistances.get(dimension) == null) {
            nearestNeighborsDistances.set(dimension, calculateNearestNeighborsDistances(getExistingValues(dimension), k));
        }
        return nearestNeighborsDistances.get(dimension);
    }

    public Map<Float, Float> getLocalReachabilityDensities(int dimension) {
        if (localReachabilityDensities.get(dimension) == null) {
            localReachabilityDensities.set(dimension, calculateLocalReachabilityDensities(getNearestNeighborsDistances(dimension), k));
        }
        return localReachabilityDensities.get(dimension);
    }

    public float calculateLocalOutlierFeature(FeatureData featureData, int dimension) {
        return calculateLocalOutlierFeature(featureData.getValues()[dimension], dimension);
    }

    public float calculateLocalOutlierFeature(Float newValue, int dimension) {
        return calculateLocalOutlierFeature(newValue, getExistingValues(dimension), getNearestNeighborsDistances(dimension), getLocalReachabilityDensities(dimension), k);
    }

    /**
     * Local outlier factor (LOF) is an algorithm for finding anomalous data points by measuring the
     * local deviation of a given data point with respect to its neighbours.
     * https://en.wikipedia.org/wiki/Local_outlier_factor
     */
    public static float calculateLocalOutlierFeature(Float newValue, List<Float> existingValues, Map<Float, NearestNeighborsDistances> nearestNeighborsDistances, Map<Float, Float> localReachabilityDensities, int k) {
        // add the nearest neighbors distances of the new value
        if (!nearestNeighborsDistances.containsKey(newValue)) {
            NearestNeighborsDistances newNearestNeighborsDistances = new NearestNeighborsDistances(newValue, existingValues, k);
            nearestNeighborsDistances.put(newValue, newNearestNeighborsDistances);
        }

        // add the local reachability density of the new value
        if (!localReachabilityDensities.containsKey(newValue)) {
            float newLocalReachabilityDensity = calculateLocalReachabilityDensity(newValue, nearestNeighborsDistances, k);
            localReachabilityDensities.put(newValue, newLocalReachabilityDensity);
        }

        // feed densities & distances into outlier factor calculation
        return calculateLocalOutlierFactor(newValue, localReachabilityDensities, nearestNeighborsDistances);
    }

    public static float calculateLocalOutlierFeature(Float newValue, List<Float> existingValues) {
        return calculateLocalOutlierFeature(newValue, existingValues, DEFAULT_K);
    }

    public static float calculateLocalOutlierFeature(Float newValue, List<Float> existingValues, int k) {
        Map<Float, NearestNeighborsDistances> nearestNeighborsDistances = calculateNearestNeighborsDistances(existingValues, k);
        Map<Float, Float> localReachabilityDensities = calculateLocalReachabilityDensities(nearestNeighborsDistances, k);
        return calculateLocalOutlierFeature(newValue, existingValues, nearestNeighborsDistances, localReachabilityDensities, k);
    }

    public static Map<Float, NearestNeighborsDistances> calculateNearestNeighborsDistances(List<Float> existingValues, int k) {
        Map<Float, NearestNeighborsDistances> nearestNeighborsDistances = new HashMap<>();
        // add the nearest neighbors distances of the existing values
        for (Float existingValue : existingValues) {
            // avoid duplicate calculations for same values
            if (nearestNeighborsDistances.containsKey(existingValue)) {
                continue;
            }

            // remove the current value
            List<Float> modifiedExistingValues = new LinkedList<>(existingValues);
            modifiedExistingValues.remove(existingValue);

            // add nearest neighbors distances
            NearestNeighborsDistances existingNearestNeighborsDistances = new NearestNeighborsDistances(existingValue, modifiedExistingValues, k);
            nearestNeighborsDistances.put(existingValue, existingNearestNeighborsDistances);
        }
        return nearestNeighborsDistances;
    }

    public static Map<Float, Float> calculateLocalReachabilityDensities(Map<Float, NearestNeighborsDistances> nearestNeighborsDistances, int k) {
        Map<Float, Float> localReachabilityDensities = new HashMap<>();
        for (Map.Entry<Float, NearestNeighborsDistances> nearestNeighborsDistancesEntry : nearestNeighborsDistances.entrySet()) {
            float value = nearestNeighborsDistancesEntry.getKey();
            float localReachabilityDensity = calculateLocalReachabilityDensity(value, nearestNeighborsDistances, k);
            localReachabilityDensities.put(value, localReachabilityDensity);
        }
        return localReachabilityDensities;
    }

    /**
     * The local reachability density (LRD) is  defined as  the inverse of the average reachability distance of the k-nearest neighbours of A (Nk(A)) to A itself.
     *
     * @param newValue                  value A for which LRD is calculated
     * @param nearestNeighborsDistances nearest neighbor distances for all existing values
     * @param k                         range indicator for the nearest neighbors
     * @return local reachability distance
     */
    public static float calculateLocalReachabilityDensity(Float newValue, Map<Float, NearestNeighborsDistances> nearestNeighborsDistances, int k) {
        List<KnnCalculator.Neighbor> nearestNeighbors = nearestNeighborsDistances.get(newValue).nearestNeighbors;

        float reachabilityDistanceSum = 0;
        for (KnnCalculator.Neighbor neighbor : nearestNeighbors) {
            float reachabilityDistance = calculateReachabilityDistance(newValue, neighbor.value, nearestNeighborsDistances, k);
            reachabilityDistanceSum += reachabilityDistance;
        }

        float averageReachabilityDistance = reachabilityDistanceSum / nearestNeighbors.size();
        return 1 / averageReachabilityDistance;
    }

    /**
     * The reachability distance is defined as the distance between the two requested values A and B but at least the k-th distance of the existing value.
     * In the k-nearest neighbors the distances of its members are considered to be equally distance. Therefore we choose the largest (k-th) distance as a representative (k-th distance).
     * That way if the distance between A and B (dist(A,B)) is larger then the distance of the k-nearest neighbors, this dist(A,B) is returned. Else
     * if this distance (dist(A,B)) is smaller than A is a new member of the nearest Neighbors and therefore we must return the representative (k-th distance).
     *
     * @param newValue                     value A for which the distance is calculated
     * @param existingValue                value B to which the distance is calculated
     * @param nearestNeighborsDistancesMap nearest neighbor distances for all existing values (even the new value)
     * @param k                            range indicator for the nearest neighbors
     * @return reachability distance between newValue and existingValue
     */
    public static float calculateReachabilityDistance(Float newValue, Float existingValue, Map<Float, NearestNeighborsDistances> nearestNeighborsDistancesMap, int k) {
        float absoluteDistance = KnnCalculator.calculateDistance(newValue, existingValue);
        NearestNeighborsDistances nearestNeighborsDistances = nearestNeighborsDistancesMap.get(existingValue);
        if (nearestNeighborsDistances.nearestNeighbors.size() > 0) {
            int neighborIndex = Math.min(k, nearestNeighborsDistances.nearestNeighbors.size() - 1);
            float nearestNeighborDistance = nearestNeighborsDistances.getDistance(neighborIndex);
            return Math.max(nearestNeighborDistance, absoluteDistance);
        } else {
            return absoluteDistance;
        }
    }

    /**
     * LOF is defined as the average local reachability density of the neighbors divided by the object's own local reachability density. A value of approximately 1
     * indicates that the object is comparable to its neighbors (and thus not an outlier). A value below 1 indicates a denser region (which would be an inlier),
     * while values significantly larger than 1 indicate outliers.
     *
     * @param newValue                   value A for which the LOF is calculated
     * @param localReachabilityDensities LRD's for all k nearest neighbors of A
     * @param nearestNeighborsDistances  nearest neighbor distances for all existing values (even the new value)
     * @return local outlier factor for A
     */
    public static float calculateLocalOutlierFactor(Float newValue, Map<Float, Float> localReachabilityDensities, Map<Float, NearestNeighborsDistances> nearestNeighborsDistances) {
        float localReachabilityDensitySum = 0;
        List<KnnCalculator.Neighbor> nearestNeighbors = nearestNeighborsDistances.get(newValue).nearestNeighbors;
        for (KnnCalculator.Neighbor nearestNeighbor : nearestNeighbors) {
            float value = nearestNeighbor.value;
            float localReachabilityDensity = localReachabilityDensities.get(value);
            localReachabilityDensitySum += localReachabilityDensity;
        }

        float averageLocalReachabilityDensity = localReachabilityDensitySum / nearestNeighbors.size();
        float newLocalReachabilityDensity = localReachabilityDensities.get(newValue);

        return averageLocalReachabilityDensity / newLocalReachabilityDensity;
    }

    public static List<Integer> findOutlierIndices(List<Float> existingValues, int k) {
        List<Integer> outlierIndices = new ArrayList<>();
        try {
            // pre-calculate nearest neighbors distances for existing values
            Map<Float, NearestNeighborsDistances> nearestNeighborsDistances;
            nearestNeighborsDistances = calculateNearestNeighborsDistances(existingValues, k);

            // pre-calculate local reachability densities for existing values
            Map<Float, Float> localReachabilityDensities;
            localReachabilityDensities = calculateLocalReachabilityDensities(nearestNeighborsDistances, k);

            // iterate over all values
            for (int valueIndex = 0; valueIndex < existingValues.size(); valueIndex++) {
                Float value = existingValues.get(valueIndex);
                float localOutlierFactor = calculateLocalOutlierFeature(value, existingValues, nearestNeighborsDistances, localReachabilityDensities, k);
                if (isOutlier(localOutlierFactor)) {
                    outlierIndices.add(valueIndex);
                }
            }
        } catch (Exception ex) {
            Logger.w(TAG, "Unable to find outliers: " + ex.getMessage());
        }
        return outlierIndices;
    }

    public static List<Integer> findOutlierIndices(List<Float> existingValues) {
        return findOutlierIndices(existingValues, DEFAULT_K);
    }

    public static List<Float> removeOutliersFromFloats(List<Float> existingValues, int k) {
        List<Float> clearedValues = new ArrayList<>(existingValues);
        List<Integer> outlierIndices = findOutlierIndices(clearedValues, k);
        int indexOffset = 0;
        for (Integer outlierIndex : outlierIndices) {
            clearedValues.remove(outlierIndex - indexOffset);
            indexOffset += 1;
        }
        return clearedValues;
    }

    public static List<Float> removeOutliersFromFloats(List<Float> existingValues) {
        return removeOutliersFromFloats(existingValues, DEFAULT_K);
    }

    /**
     * Returns true if the calculated LOF indicates that the value
     * is an outlier
     */
    public static boolean isOutlier(float localOutlierFactor, float threshold) {
        return localOutlierFactor > 1 + threshold;
    }

    public static boolean isOutlier(float localOutlierFactor) {
        return isOutlier(localOutlierFactor, OUTLIER_THRESHOLD_NORMAL);
    }

    public static float convertToInlierProbability(float localOutlierFactor) {
        return Math.min(1, 1 / localOutlierFactor);
    }

}
