package com.zurich.authenticator.data.outlier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KnnCalculator {

    public static class Neighbor {
        float value;
        float distance;

        public Neighbor(float value, float distance) {
            this.value = value;
            this.distance = distance;
        }

        public static Comparator distanceComparator = new Comparator<Neighbor>() {
            public int compare(Neighbor neighbor1, Neighbor neighbor2) {
                if (neighbor1.distance == neighbor2.distance) {
                    return 0;
                }
                return neighbor1.distance < neighbor2.distance ? -1 : 1;
            }
        };

        public static Comparator valueComparator = new Comparator<Neighbor>() {
            public int compare(Neighbor neighbor1, Neighbor neighbor2) {
                if (neighbor1.value == neighbor2.value) {
                    return 0;
                }
                return neighbor1.value < neighbor2.value ? -1 : 1;
            }
        };

    }

    /**
     * Converts a given list of values into neighbor objects and calculates
     * the distance to the given value
     */
    public static List<Neighbor> getNeighbors(Float newValue, List<Float> existingValues) {
        List<Neighbor> neighbors = new ArrayList<>();

        // calculate all distances and add them as neighbors
        for (Float existingValue : existingValues) {
            Float distance = calculateDistance(newValue, existingValue);
            neighbors.add(new Neighbor(existingValue, distance));
        }
        return neighbors;
    }

    /**
     * Returns the k neighbors that have the lowest distance to the
     * given value
     */
    public static List<Neighbor> getNearestNeighbors(Float newValue, List<Float> existingValues, int k) {
        List<Neighbor> neighbors = getNeighbors(newValue, existingValues);

        // sort the neighbors based on distance
        Collections.sort(neighbors, Neighbor.distanceComparator);

        // return the first k neighbors
        if (neighbors.size() > k) {
            return new ArrayList<>(neighbors.subList(0, k));
        } else {
            return neighbors;
        }
    }

    /**
     * Returns the values of the k neighbors that have the lowest distance
     * to the given value
     */
    public static List<Float> getNearestNeighborValues(Float newValue, List<Float> existingValues, int k) {
        List<Neighbor> nearestNeighbors = getNearestNeighbors(newValue, existingValues, k);
        List<Float> nearestNeighborValues = new ArrayList<>(nearestNeighbors.size());
        for (Neighbor nearestNeighbor : nearestNeighbors) {
            nearestNeighborValues.add(nearestNeighbor.value);
        }
        return nearestNeighborValues;
    }

    /**
     * Calculates the absolute delta of 2 values
     */
    public static Float calculateDistance(Float newValue, Float existingValue) {
        return Math.abs(newValue - existingValue);
    }

}
