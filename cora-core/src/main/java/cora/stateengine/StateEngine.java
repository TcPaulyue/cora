package cora.stateengine;

import cora.graph.fsm.Event;
import cora.graph.fsm.State;

public interface StateEngine {
    public State execute(String query);

    public State execute(Event event);
//    State getNextState(String mutation);
//
//    State getState(String query);
}
