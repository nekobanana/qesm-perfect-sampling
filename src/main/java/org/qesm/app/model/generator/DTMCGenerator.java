package org.qesm.app.model.generator;

import org.qesm.app.model.generator.distribution.Distribution;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;

import java.util.*;

public class DTMCGenerator {
    final private Random rand = RandomUtils.rand;
    private int N;
    private Distribution edgesNumberDistribution;
    // una volta campionato in numero di archi uscenti uso edgesLocalityDistribution (che potrebbe essere
    // per esempio una gaussiana o una uniforme [-4, +4]) per assegnare per ogni arco verso che nodo va
    private Distribution edgesLocalityDistribution;
    private Double selfLoopValue;
    boolean connectSCC;


    public Matrix getMatrix() {
        if (!checkNotNullInputs()) {
            throw new RuntimeException("Please set a value for N, edgesNumberDistribution, and edgesLocalityDistribution");
        }
        Matrix P;
        if (connectSCC) {
            P = generateDTMCMatrix();
            forceIrreducible(P);
        }
        else {
            do {
                P = generateDTMCMatrix();
            } while (!isIrreducible(P));
        }
        forceSelfLoop(P);
        return P;
    }

    private boolean isIrreducible(Matrix P) {
        Tarjan tarjan = new Tarjan(P);
        List<List<Integer>> scc = tarjan.getComponents();
        return scc.size() == 1;
    }

    private void forceIrreducible(Matrix P) {
        Tarjan tarjan = new Tarjan(P);
        List<List<Integer>> scc = tarjan.getComponents();
        if (scc.size() > 1) {
            for (int i = 0; i < scc.size(); i++) {
                List<Integer> component = scc.get(i);
                List<Integer> nextComponent = scc.get((i + 1) % scc.size());
                int edgeSource = component.get(rand.nextInt(component.size()));
                int edgeDest = nextComponent.get(rand.nextInt(nextComponent.size()));
                P.set(edgeSource, edgeDest, rand.nextDouble());
                double selfLoopValue = P.get(edgeSource, edgeSource) > 0? this.selfLoopValue : 0;
                double sum = P.getRow(edgeSource).sum() - P.get(edgeSource, edgeSource);
                for (int j = 0; j < P.columns(); j++) {
                    P.set(edgeSource, j, P.get(edgeSource, j) / sum * (1 - selfLoopValue));
                }
                P.set(edgeSource, edgeSource, selfLoopValue);
            }
        }
        if (!isIrreducible(P)) {
            throw new RuntimeException("Generated matrix is not irreducible");
        }
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
        if (!checkDistributionsCompatibility()) {
            throw new RuntimeException("Input distributions should be compatible");
        }
        boolean selfLoopProbabilitySetByUser = selfLoopValue != null;
        if (selfLoopProbabilitySetByUser && !checkSelfLoopCompatibility()) {
            throw new RuntimeException("Self loop value should be less than 1");
        }
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
                if (destNode == r && selfLoopProbabilitySetByUser) {
                    sampledEdgesValues.put(destNode, selfLoopValue);
                }
                else {
                    sampledEdgesValues.put(destNode, rand.nextDouble());
//                    Log.logger.info("edgesLocalityDistribution.getSample() = " + sampledEdgesValues.get(destNode));
                }
            }
            if (selfLoopProbabilitySetByUser) {
                double selfLoopValueIfPresent = sampledEdgesValues.getOrDefault(r, 0.);
                sampledEdgesValues.remove(r);
                double valuesSum = sampledEdgesValues.values().stream().reduce(0., Double::sum);
                for (Map.Entry<Integer, Double> entry : sampledEdgesValues.entrySet()) {
                    P.set(r, entry.getKey(), entry.getValue() / valuesSum * (1 - selfLoopValueIfPresent));
                }
                if (P.getRow(r).sum() > 0)
                    P.set(r, r, selfLoopValueIfPresent);
                else
                    P.set(r, r, 1);
            }
            else {
                double valuesSum = sampledEdgesValues.values().stream().reduce(0., Double::sum);
                for (Map.Entry<Integer, Double> entry : sampledEdgesValues.entrySet()) {
                    P.set(r, entry.getKey(), entry.getValue() / valuesSum);
                }
            }
        }
        return P;
    }

    private boolean checkNotNullInputs() {
        return N > 0 &&
                this.edgesNumberDistribution != null && this.edgesLocalityDistribution != null;
    }


    public void setConnectSCC(boolean connectSCC) {
        this.connectSCC = connectSCC;
    }

    public void setSelfLoopValue(Double selfLoopValue) {
        this.selfLoopValue = selfLoopValue;
    }

    public void setEdgesLocalityDistribution(Distribution edgesLocalityDistribution) {
        this.edgesLocalityDistribution = edgesLocalityDistribution;
    }

    public void setEdgesNumberDistribution(Distribution edgesNumberDistribution) {
        this.edgesNumberDistribution = edgesNumberDistribution;
    }

    public void setN(int n) {
        N = n;
    }

    private boolean checkDistributionsCompatibility() {
        return edgesNumberDistribution.getMin() <= Math.min(edgesLocalityDistribution.getIntervalLength(), N);
        // non tiene conto di cose strane che potrebbero succedere nella ManualDistribution,
        // tipo intervalli che si sovrappongono
    }

    private boolean checkSelfLoopCompatibility() {
        return selfLoopValue >= 0 && selfLoopValue < 1;
    }

}
