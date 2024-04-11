package org.example;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.ejml.data.DMatrixRMaj;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.util.Map;
import java.util.stream.Collectors;

public class PerfectSampling {
    public static Map<Integer, Double> getSteadyStateDistributionLinearSystem(double[][] P) {
        MutableValueGraph<Object, Double> graph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        checkValidSquareMat(P);
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < P.length; j++) {
                graph.putEdgeValue(i, j, P[i][j]);
            }
        }
        return DTMCStationary.builder().build().apply(graph)
                .entrySet().stream()
                .collect(Collectors.toMap(e -> (int)e.getKey(), Map.Entry::getValue));
    }

    private static void checkValidSquareMat(double[][] P) {
        for (int i = 0; i < P.length; i++) {assert(P.length == P[i].length);}
    }

    public static double[] linearizeMatrix(double[][] P) {
        int n = P.length;
        double[] linP = new double[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                linP[n * i + j] = P[i][j];
            }
        }
        return linP;
    }
}
