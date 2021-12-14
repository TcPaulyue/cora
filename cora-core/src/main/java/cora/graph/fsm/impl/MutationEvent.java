package cora.graph.fsm.impl;

import cora.graph.fsm.Event;

public class MutationEvent implements Event {
    private String mutation;

    public MutationEvent(String mutation) {
        this.mutation = mutation;
    }

    public String getMutation() {
        return mutation;
    }

    public void setMutation(String mutation) {
        this.mutation = mutation;
    }
}
