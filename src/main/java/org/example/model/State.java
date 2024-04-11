package org.example.model;

public class State {
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

