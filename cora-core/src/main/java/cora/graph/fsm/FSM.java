package cora.graph.fsm;

import java.util.Map;

public interface FSM {

    public Map<Event,State> nextExecution(State state);

}
