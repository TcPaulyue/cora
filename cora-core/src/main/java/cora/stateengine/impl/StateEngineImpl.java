package cora.stateengine.impl;

import cora.graph.fsm.State;
import cora.parser.dsl.CoraParser;
import cora.stateengine.StateEngine;

public class StateEngineImpl implements StateEngine {
    private CoraParser coraParser;

    public StateEngineImpl(CoraParser coraParser) {
        this.coraParser = coraParser;
    }

    @Override
    public State getNextState(String mutation) {
        return null;
    }

    @Override
    public State getState(String query) {
        return null;
    }
}
