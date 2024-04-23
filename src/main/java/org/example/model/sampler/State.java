package org.example.model.sampler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
    private int id;
    private int flag;
    @JsonIgnore
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

    @JsonProperty
    public Integer getNextStateId() {
        if (next == null) return null;
        return next.getId();
    }

    public void setNext(State next) {
        this.next = next;
    }
}

