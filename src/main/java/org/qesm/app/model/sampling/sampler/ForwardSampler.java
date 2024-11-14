package org.qesm.app.model.sampling.sampler;

import org.la4j.Matrix;
import org.qesm.app.model.sampling.sampler.random.RandomHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ForwardSampler extends Sampler {

    private final List<StatesSnapshot> sequence = new ArrayList<>();
    private final RandomHelper randomHelper;

    public ForwardSampler(Matrix P, Class<? extends RandomHelper> randomHelperClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(P);
        this.randomHelper = randomHelperClass.getConstructor(Random.class, int.class).newInstance(rand, n);
    }

    @Override
    public void reset() {
        sequence.clear();
    }

    public State getState(int stateId, int time) {
        return sequence.get(time).getState(stateId);
    }

    private void initSequence() {
        StatesSnapshot initStatesSnapshot = new StatesSnapshot();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            initStatesSnapshot.addState(i, s);
        }
        sequence.add(initStatesSnapshot);
    }

    private StatesSnapshot generateNewSnapshot() {
        StatesSnapshot newStatesSnapshot = new StatesSnapshot();
        randomHelper.init();
        for (Map.Entry<Integer, State> entry : sequence.get(sequence.size() - 1).getStates().entrySet()) {
            State s;
            int currentStateId = generateNextStateNumber(entry.getKey(), randomHelper.getRandomInt(), randomHelper.getRandomDouble());
            if (!newStatesSnapshot.getStates().containsKey(currentStateId)) {
                s = new State();
                s.setId(currentStateId);
                newStatesSnapshot.addState(currentStateId, s);
            }
            s = newStatesSnapshot.getState(currentStateId);
            entry.getValue().setNext(s);
            newStatesSnapshot.addState(currentStateId, s);
        }
        return newStatesSnapshot;
    }

    public RunResult runUntilCoalescence() {
        initSequence();
        int t = 0;
        boolean coalesced = false;
        while (!coalesced) {
            t++;
            StatesSnapshot newStatesSnapshot = generateNewSnapshot();
            newStatesSnapshot.setTime(t);
            sequence.add(newStatesSnapshot);
            coalesced = newStatesSnapshot.getStates().size() == 1;
        }
        StatesSnapshot lastStatesSnapshot = sequence.get(sequence.size() - 1);
        return new RunResult(lastStatesSnapshot.getStates().keySet().stream().findAny().get(), lastStatesSnapshot.getTime());
    }

}
