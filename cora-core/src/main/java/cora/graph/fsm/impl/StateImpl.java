package cora.graph.fsm.impl;

import cora.graph.fsm.State;

public class StateImpl implements State {
    private String stateDesc;

    public StateImpl(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }
}
