package cora.stateengine;

import cora.graph.fsm.State;

public interface StateEngine {
    public State execute(String query);
//
//    State getNextState(String mutation);
//
//    State getState(String query);
}
