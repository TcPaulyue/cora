package cora.graph.fsm.impl;

import cora.graph.fsm.Event;
import cora.graph.fsm.FSM;
import cora.graph.fsm.State;

import java.util.Map;

public class FSMImpl implements FSM {

    Map<State,Map<Event,State>> fsmMap;

    public FSMImpl(Map<State, Map<Event, State>> fsmMap) {
        this.fsmMap = fsmMap;
    }


    public Map<State, Map<Event, State>> getFsmMap() {
        return fsmMap;
    }

    public void setFsmMap(Map<State, Map<Event, State>> fsmMap) {
        this.fsmMap = fsmMap;
    }

    @Override
    public State nextState(State state, Event event) {
        return fsmMap.get(state).get(event);
    }
}
