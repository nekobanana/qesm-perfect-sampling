package org.example.model.generator;

import org.la4j.Matrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTMCGenerator {
    long seed;
    int N;
    Distribution edgesNumberDistribution;
    // una volta campionato in numero di archi uscenti uso edgesLocalityDistribution (che potrebbe essere
    // per esempio una gaussiana o una uniforme [-4, +4]) per assegnare per ogni arco verso che nodo va
    Distribution edgesLocalityDistribution;
    // probabilità di self loop, da tenere in considerazione per edgesNumberDistribution o è a sé? (semprerebbe a sé)
    double selfLoopProbability;

    // manca una distribuzione per i valori degli archi?


    // forse da rifare col builder?
    public DTMCGenerator(long seed, int n, Distribution edgesNumberDistribution, Distribution edgesLocalityDistribution, double selfLoopProbability) {
        this.seed = seed;
        N = n;
        this.edgesNumberDistribution = edgesNumberDistribution;
        this.edgesLocalityDistribution = edgesLocalityDistribution;
        this.selfLoopProbability = selfLoopProbability;
        edgesNumberDistribution.setSeed(seed);
        edgesLocalityDistribution.setSeed(seed);
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
            P.set(0, 0, Math.random());
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
            Map<Integer, Double> sampledEdgesValues = new HashMap<>();
            for (int e = 0; e < nEdges; e++) {
                int destNode;
                do {
                    destNode = (r + edgesLocalityDistribution.getSample() + N) % N;
                } while (destNode == r || sampledEdgesValues.containsKey(destNode));
                sampledEdgesValues.put(destNode, Math.random());
            }
            boolean selfLoop = Math.random() < selfLoopProbability;
            if (selfLoop) {
                sampledEdgesValues.put(r, Math.random());
            }
            double valuesSum = sampledEdgesValues.values().stream().reduce(0., Double::sum);
            for (Map.Entry<Integer, Double> entry : sampledEdgesValues.entrySet()) {
                P.set(r, entry.getKey(), entry.getValue() / valuesSum);
            }
        }
        return P;
    }

}
