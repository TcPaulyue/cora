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

    @Override
    public Map<Event, State> nextExecution(State state) {
        return fsmMap.get(state);
    }


    public Map<State, Map<Event, State>> getFsmMap() {
        return fsmMap;
    }

    public void setFsmMap(Map<State, Map<Event, State>> fsmMap) {
        this.fsmMap = fsmMap;
    }
}
