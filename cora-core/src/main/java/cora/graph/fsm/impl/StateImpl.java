package cora.graph.fsm.impl;

import cora.graph.fsm.State;

import java.util.Objects;

public class StateImpl implements State {
    private String nodeInstanceId;

    private String preState;

    private String curState;

    private String executionResult;

    private Boolean isFailed;

    public StateImpl(String curState) {
        this.curState = curState;
    }

    @Override
    public String getCurState() {
        return curState;
    }

    public void setCurState(String curState) {
        this.curState = curState;
    }

    @Override
    public String getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }

    @Override
    public Boolean getFailed() {
        return isFailed;
    }

    public void setFailed(Boolean failed) {
        isFailed = failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateImpl state = (StateImpl) o;
        return Objects.equals(curState, state.curState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curState);
    }

    @Override
    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    @Override
    public String getPreState() {
        return preState;
    }

    public void setPreState(String preState) {
        this.preState = preState;
    }
}
