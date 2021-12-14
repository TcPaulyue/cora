package cora.stateengine;

import cora.graph.fsm.State;

public interface StateEngine {
    State getNextState(String mutation);

    State getState(String query);
}
