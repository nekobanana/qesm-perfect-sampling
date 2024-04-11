package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class StatesSnapshot {
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

    void setTime(int time) {
        this.time = time;
    }

    void addState(int stateId, State s) {
        states.put(stateId, s);
    }

    State getState(int stateId) {
        return states.get(stateId);
    }
}
