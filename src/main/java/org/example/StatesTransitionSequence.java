package org.example;

import org.la4j.Matrix;
import org.la4j.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatesTransitionSequence {
    private int n;
    private Matrix P;
    private List<States> sequences = new ArrayList<States>();

    public StatesTransitionSequence(Matrix P) {
        assert (P.rows() == P.columns());
        this.n = P.rows();
        this.P = P;
    }

    public States getStates(int time) {
        return sequences.get(-time);
    }

    public State getState(int stateId, int time) {
        return sequences.get(-time).states.get(stateId);
    }

    public void initSequence() {
        States initStates = new States();
        for (int i = 0; i < n; i++) {
            State s = new State();
            s.setId(i);
            s.setFlag(i);
            initStates.states.put(i, s);
        }
        sequences.add(initStates);
    }

    public States generateNewSnapshot() {
        States newStates = new States();
        for (int i = 0; i < n; i++) {
            // se invece di coupling from the past vado in avanti
            // via via che gli stati coalescono non devo più iterare su tutti
            State s = new State();
            s.setId(i);
            State nextState = getState(generateNextStateNumber(i), - (sequences.size()-1));
            s.setNext(nextState);
            s.setFlag(nextState.getFlag());
            newStates.states.put(i, s);
        }
        return newStates;
    }

    public RunResult runUntilCoalescence() {
        initSequence();
        assert(P.rows() == P.columns());
        int t = 0;
        boolean coalesced = false;
        while (!coalesced) {
            t--;
            States newStates = generateNewSnapshot();
            newStates.setTime(t);
            sequences.add(newStates);
            coalesced = newStates.haveCoalesced();
        }
        States lastStates = sequences.get(sequences.size() - 1);
        return new RunResult(lastStates.states.get(0).getFlag(), -lastStates.time);
    }

    private int generateNextStateNumber(int i) {
        State s = new State();
        s.id = i;
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
        return n;
    }

    static public class States {
        private Map<Integer, State> states = new HashMap<>();
        private int time;

        public boolean haveCoalesced() {
            return states.values().stream()
                    .map(State::getFlag)
                    .distinct()
                    .limit(2)
                    .count() <= 1;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }

    static public class State {
        private int id;
        private int flag;
        private State next;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public State getNext() {
            return next;
        }

        public void setNext(State next) {
            this.next = next;
        }
    }

    static public class RunResult {
        int sampledState;
        int steps;

        public RunResult(int sampledState, int steps) {
            this.sampledState = sampledState;
            this.steps = steps;
        }
    }
}
