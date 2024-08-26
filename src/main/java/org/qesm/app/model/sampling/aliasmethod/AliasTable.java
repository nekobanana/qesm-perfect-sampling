package org.qesm.app.model.sampling.aliasmethod;

import org.qesm.app.model.sampling.runner.SamplerRunner;
import org.qesm.app.model.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AliasTable {
    private final int[] alias;
    private final double[] probability;
    private final int n;


    public AliasTable(double[] probabilities) {
        this.n = probabilities.length;
        this.alias = new int[n];
        this.probability = new double[n];

        double[] scaledProbabilities = new double[n];
        int[] small = new int[n];
        int[] large = new int[n];
        int smallIndex = 0, largeIndex = 0;

        for (int i = 0; i < n; i++) {
            scaledProbabilities[i] = probabilities[i] * n;
            if (scaledProbabilities[i] < 1.0) {
                small[smallIndex++] = i;
            } else {
                large[largeIndex++] = i;
            }
        }

        while (smallIndex > 0 && largeIndex > 0) {
            int less = small[--smallIndex];
            int more = large[--largeIndex];

            probability[less] = scaledProbabilities[less];
            alias[less] = more;

            scaledProbabilities[more] = (scaledProbabilities[more] + scaledProbabilities[less]) - 1.0;

            if (scaledProbabilities[more] < 1.0) {
                small[smallIndex++] = more;
            } else {
                large[largeIndex++] = more;
            }
        }

        while (largeIndex > 0) {
            probability[large[--largeIndex]] = 1.0;
        }

        while (smallIndex > 0) {
            probability[small[--smallIndex]] = 1.0;
        }
    }

    public int sample(int randomInt, double randomDouble) {
        int column = randomInt;
        boolean coinToss = randomDouble < probability[column];
        return coinToss ? column : alias[column];
    }

//    public static void main(String[] args) {
//        double[] probabilities = {0.1, 0.5, 0.2, 0.2}; // Example probabilities
//        AliasTable aliasTable = new AliasTable(probabilities);
//
//
//        int samplesN = 10000;
//        List<Integer> samples = new ArrayList<>();
//
//        for (int i = 0; i < samplesN; i++) {
//            samples.add(aliasTable.sample());
//        }
//        Map<Integer, Double> pi = SamplerRunner.getDistrFromResults(samples, Function.identity())
//                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (double)e.getValue() / samples.size()));
//
//    }
}
