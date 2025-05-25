package org.example.model;

import java.util.Collections;
import java.util.List;

public class StatisticsUtil {
    public static double mean(List<Double> data) {
        double sum = 0;
        for (double v : data) sum += v;
        return data.size() > 0 ? sum / data.size() : 0;
    }
    public static double median(List<Double> data) {
        if (data.isEmpty()) return 0;
        List<Double> sorted = new java.util.ArrayList<>(data);
        Collections.sort(sorted);
        int n = sorted.size();
        return n % 2 == 0 ? (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0 : sorted.get(n/2);
    }
    public static double stddev(List<Double> data) {
        if (data.isEmpty()) return 0;
        double m = mean(data), sum = 0;
        for (double v : data) sum += Math.pow(v - m, 2);
        return Math.sqrt(sum / data.size());
    }
}
