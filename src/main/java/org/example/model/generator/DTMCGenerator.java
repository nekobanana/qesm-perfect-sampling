package org.example.model.generator;

import org.example.model.generator.distribution.Distribution;
import org.example.model.utils.RandomUtils;
import org.la4j.Matrix;

import java.util.*;

public class DTMCGenerator {
    final private Random rand = RandomUtils.rand;
    private int N;
    private Distribution edgesNumberDistribution;
    // una volta campionato in numero di archi uscenti uso edgesLocalityDistribution (che potrebbe essere
    // per esempio una gaussiana o una uniforme [-4, +4]) per assegnare per ogni arco verso che nodo va
    private Distribution edgesLocalityDistribution;
    private double selfLoopValue;

    public DTMCGenerator(int n, Distribution edgesNumberDistribution, Distribution edgesLocalityDistribution, double selfLoopProbability) {
        N = n;
        this.edgesNumberDistribution = edgesNumberDistribution;
        this.edgesLocalityDistribution = edgesLocalityDistribution;
        this.selfLoopValue = selfLoopProbability;
    }

    public Matrix getMatrix() {
        Matrix P;
        do {
            P = generateDTMCMatrix();
        } while(!isIrreducible(P));
        forceSelfLoop(P);
        return P;
    }

    private boolean isIrreducible(Matrix P) {
        Tarjan tarjan = new Tarjan(P);
        List<List<Integer>> scc = tarjan.getComponents();
        return scc.size() == 1;
    }

    private void forceSelfLoop(Matrix P) {
        boolean hasSelfLoop = false;
        for (int i = 0; i < N; i++) {
            if (P.get(i, i) != 0) {
                hasSelfLoop = true;
                break;
            }
        }
        if (!hasSelfLoop) {
            P.set(0, 0, rand.nextDouble());
//            Log.logger.info("P.set(0, 0, rand.nextDouble()) = " + P.get(0, 0));
            double sum = P.getRow(0).sum();
            for (int i = 0; i < N; i++) {
                P.set(0, i, P.get(0, i) / sum);
            }
        }
    }

    private Matrix generateDTMCMatrix() {
        Matrix P = Matrix.zero(N, N);
        for (int r = 0; r < N; r++) {
            int nEdges = edgesNumberDistribution.getSample();
//            Log.logger.info("edgesNumberDistribution.getSample() = " + nEdges);
            Map<Integer, Double> sampledEdgesValues = new HashMap<>();
            for (int e = 0; e < nEdges; e++) {
                int destNode;
                do {
                    destNode = (r + edgesLocalityDistribution.getSample() + N) % N;
//                    Log.logger.info("edgesLocalityDistribution.getSample() = " + destNode);
                } while (sampledEdgesValues.containsKey(destNode));
                if (destNode == r) {
                    sampledEdgesValues.put(destNode, selfLoopValue);
                }
                else {
                    sampledEdgesValues.put(destNode, rand.nextDouble());
//                    Log.logger.info("edgesLocalityDistribution.getSample() = " + sampledEdgesValues.get(destNode));
                }
            }
            double selfLoopValueIfPresent = sampledEdgesValues.getOrDefault(r, 0.);
            sampledEdgesValues.remove(r);
            double valuesSum = sampledEdgesValues.values().stream().reduce(0., Double::sum);
            for (Map.Entry<Integer, Double> entry : sampledEdgesValues.entrySet()) {
                P.set(r, entry.getKey(), entry.getValue() / valuesSum * (1 - selfLoopValueIfPresent));
            }
            P.set(r, r, selfLoopValueIfPresent);
        }
        return P;
    }

}
