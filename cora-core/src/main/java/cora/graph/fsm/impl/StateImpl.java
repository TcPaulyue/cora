package cora.graph.fsm.impl;

import cora.graph.fsm.State;

import java.util.Objects;

public class StateImpl implements State {
    private String stateDesc;

    private String executionResult;

    public StateImpl(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateImpl state = (StateImpl) o;
        return Objects.equals(stateDesc, state.stateDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateDesc);
    }
}
