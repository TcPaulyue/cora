package cora.graph.fsm;

public interface FSM {

    public State nextState(State state,Event event);

    public State initState();

}
