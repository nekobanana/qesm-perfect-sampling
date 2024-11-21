package org.qesm.app.model.utils;

import java.util.Map;

public class Metrics {
    public static double distanceL2(Map<Integer, Double> v1, Map<Integer, Double> v2) {
        int n = Math.max(v1.keySet().stream().max(Integer::compareTo).get(),
                    v2.keySet().stream().max(Integer::compareTo).get());
        return distanceL2(v1, v2, n);
    }

    public static double distanceL2(Map<Integer, Double> v1, Map<Integer, Double> v2, int n) {
        double s = 0;
        for (int i = 0; i < n; i++) {
            s += Math.pow((v1.getOrDefault(i, 0.)) - (v2.getOrDefault(i, 0.)), 2);
        }
        return Math.sqrt(s);
    }

    public static double distanceL2DividedByN(Map<Integer, Double> v1, Map<Integer, Double> v2, int n) {
        return distanceL2(v1, v2, n) / n;
    }

    public static double totalVariationDistance(Map<Integer, Double> v1, Map<Integer, Double> v2, int n) {
        double s = 0;
        for (int i = 0; i < n; i++) {
            s += 0.5 * Math.abs((v1.getOrDefault(i, 0.)) - (v2.getOrDefault(i, 0.)));
        }
        return s;
    }
}
