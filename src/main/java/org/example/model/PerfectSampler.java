package org.example.model;

import org.la4j.Matrix;

import java.util.ArrayList;
import java.util.List;

public class PerfectSampler {
    private final int n;
    private final Matrix P;
    private final List<StatesSnapshot> sequences = new ArrayList<>();

    public PerfectSampler(Matrix P) {
        assert (P.rows() == P.columns());

        this.n = P.rows();
        this.P = P;
    }

    public State getState(int stateId, int time) {
        return sequences.get(-time).getState(stateId);
    }

    public void initSequence() {
        StatesSnapshot initStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            s.setFlag(i);
            initStatesSnapshot.addState(i, s);
        }
        sequences.add(initStatesSnapshot);
    }

    public StatesSnapshot generateNewSnapshot() {
        StatesSnapshot newStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            // se invece di coupling from the past vado in avanti
            // via via che gli stati coalescono non devo più iterare su tutti
            State s = new State();
            s.setId(i);
            State nextState = getState(generateNextStateNumber(i), - (sequences.size()-1));
            s.setNext(nextState);
            s.setFlag(nextState.getFlag());
            newStatesSnapshot.addState(i, s);
        }
        return newStatesSnapshot;
    }

    public RunResult runUntilCoalescence() {
        initSequence();
        assert(P.rows() == P.columns());
        int t = 0;
        boolean coalesced = false;
        while (!coalesced) {
            t--;
            StatesSnapshot newStatesSnapshot = generateNewSnapshot();
            newStatesSnapshot.setTime(t);
            sequences.add(newStatesSnapshot);
            coalesced = newStatesSnapshot.haveCoalesced();
        }
        StatesSnapshot lastStatesSnapshot = sequences.get(sequences.size() - 1);
        return new RunResult(lastStatesSnapshot.getState(0).getFlag(), -lastStatesSnapshot.getTime());
    }

    private int generateNextStateNumber(int i) {
        State s = new State();
        s.setId(i);
        double leftThreshold;
        double rightThreshold = 0;
        double r = Math.random();
        for (int j = 0; j < n; j++) {
            leftThreshold = rightThreshold;
            rightThreshold = P.get(i, j) + leftThreshold;
            if (r < rightThreshold) {
                return j;
            }
        }
        throw new RuntimeException("Error generating next state number");
    }

}
