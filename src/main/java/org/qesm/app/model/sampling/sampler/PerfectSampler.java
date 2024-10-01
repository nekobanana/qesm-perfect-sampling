package org.qesm.app.model.sampling.sampler;

import org.la4j.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PerfectSampler extends Sampler {

    private Map<Integer, StatesSnapshot> sequence = new HashMap<>();
    private int currentTime = 0;
    private final boolean keepSequence;
    private int keepSequenceLength = 2;

    public PerfectSampler(Matrix P, boolean keepSequence) {
        super(P);
        this.keepSequence = keepSequence;
    }

    public PerfectSampler(Matrix P) {
        this(P, false);
    }

    @Override
    public void reset() {
        sequence.clear();
        currentTime = 0;
    }

    public State getState(int stateId, int time) {
        return sequence.get(time).getState(stateId);
    }

    private void initSequence() {
        StatesSnapshot initStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            s.setFlag(i);
            initStatesSnapshot.addState(i, s);
        }
        sequence.put(0, initStatesSnapshot);
    }

    private StatesSnapshot generateNewSnapshot() {
        StatesSnapshot newStatesSnapshot = new StatesSnapshot();
        int randomInt = rand.nextInt(n);
        double randomDouble = rand.nextDouble();
        for (int i = 0; i < n; i++) {
            // se invece di coupling from the past vado in avanti
            // via via che gli stati coalescono non devo più iterare su tutti
            State s = new State();
            s.setId(i);
            State nextState = getState(generateNextStateNumber(i, randomInt, randomDouble), currentTime + 1);
            s.setNext(nextState);
            s.setFlag(nextState.getFlag());
            newStatesSnapshot.addState(i, s);
        }
        return newStatesSnapshot;
    }

    public RunResult runUntilCoalescence() {
        initSequence();
        currentTime = 0;
        boolean coalesced = false;
        while (!coalesced) {
            currentTime--;
            StatesSnapshot newStatesSnapshot = generateNewSnapshot();
            newStatesSnapshot.setTime(currentTime);
            sequence.put(currentTime, newStatesSnapshot);
            coalesced = newStatesSnapshot.haveCoalesced();
            if (!keepSequence) {
                sequence.remove(currentTime + keepSequenceLength);
            }
        }
        StatesSnapshot lastStatesSnapshot = sequence.get(currentTime + 1);
        return new RunResult(lastStatesSnapshot.getState(0).getFlag(), -lastStatesSnapshot.getTime());
    }

    public void writeSequenceToFile(String fileName)
            throws IOException {
        String json = mapper.writeValueAsString(this);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(json);
        writer.close();
    }

    public Integer getKeepSequenceLength() {
        return keepSequenceLength;
    }

    public void setKeepSequenceLength(Integer keepSequenceLength) {
        this.keepSequenceLength = keepSequenceLength;
    }
}
