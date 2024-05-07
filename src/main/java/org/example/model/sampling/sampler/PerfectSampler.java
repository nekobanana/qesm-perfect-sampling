package org.example.model.sampling.sampler;

import org.la4j.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerfectSampler extends Sampler {

    private final List<StatesSnapshot> sequence = new ArrayList<>();

    public PerfectSampler(Matrix P, long seed) {
        super(P);
    }

    @Override
    public void reset() {
        sequence.clear();
    }

    public State getState(int stateId, int time) {
        return sequence.get(-time).getState(stateId);
    }

    private void initSequence() {
        StatesSnapshot initStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            s.setFlag(i);
            initStatesSnapshot.addState(i, s);
        }
        sequence.add(initStatesSnapshot);
    }

    private StatesSnapshot generateNewSnapshot() {
        StatesSnapshot newStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            // se invece di coupling from the past vado in avanti
            // via via che gli stati coalescono non devo più iterare su tutti
            State s = new State();
            s.setId(i);
            State nextState = getState(generateNextStateNumber(i), - (sequence.size()-1));
            s.setNext(nextState);
            s.setFlag(nextState.getFlag());
            newStatesSnapshot.addState(i, s);
        }
        return newStatesSnapshot;
    }

    public RunResult runUntilCoalescence() {
        initSequence();
        int t = 0;
        boolean coalesced = false;
        while (!coalesced) {
            t--;
            StatesSnapshot newStatesSnapshot = generateNewSnapshot();
            newStatesSnapshot.setTime(t);
            sequence.add(newStatesSnapshot);
            coalesced = newStatesSnapshot.haveCoalesced();
        }
        StatesSnapshot lastStatesSnapshot = sequence.get(sequence.size() - 1);
        return new RunResult(lastStatesSnapshot.getState(0).getFlag(), -lastStatesSnapshot.getTime());
    }

    public void writeSequenceToFile(String fileName)
            throws IOException {
        String json = mapper.writeValueAsString(this);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(json);
        writer.close();
    }
}
