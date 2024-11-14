package org.qesm.app.model.sampling.sampler;

import org.la4j.Matrix;
import org.qesm.app.model.sampling.sampler.random.RandomHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PerfectSampler extends Sampler {

    private Map<Integer, StatesSnapshot> sequence = new HashMap<>();
    private int currentTime = 0;
    private final boolean keepSequence;
    private int keepSequenceLength = 2;
    private final RandomHelper randomHelper;

    public PerfectSampler(Matrix P, Class<? extends RandomHelper> randomHelperClass, boolean keepSequence) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(P);
        this.keepSequence = keepSequence;
        this.randomHelper = randomHelperClass.getConstructor(Random.class, int.class).newInstance(rand, n);
    }

    public PerfectSampler(Matrix P, Class<? extends RandomHelper> randomHelperClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this(P, randomHelperClass, false);
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
        randomHelper.init();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            State nextState = getState(generateNextStateNumber(i, randomHelper.getRandomInt(), randomHelper.getRandomDouble()), currentTime + 1);
//            State nextState = getState(generateNextStateNumber(i, randomHelper.getRandomInt(), randomHelper.getRandomDouble()), currentTime + 1);
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
        class SequenceJson {
            public Map<Integer, StatesSnapshot> sequence;
            public int n;
            public SequenceJson(Map<Integer, StatesSnapshot> sequence, int n) {
                this.sequence = sequence;
                this.n = n;
            }
        }
        String json = mapper.writeValueAsString(new SequenceJson(sequence, n));
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
