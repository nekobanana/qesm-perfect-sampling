package org.example.model.sampling.runner;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface SamplerRunner {
    static <T> Map<Integer, Long> getDistrFromResults(List<T> results, Function<? super T, Integer> function) {
        return results.stream().collect(Collectors.groupingBy(function, Collectors.counting()));
    }
    void run(int runs);
    Map<Integer, Double> getStatesDistribution();
    Map<Integer, Double> getStatesDistribution(boolean print);
}
