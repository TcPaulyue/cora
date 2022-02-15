package cora.context;

import cora.graph.fsm.Event;

public interface Actor {
    public boolean addEvent(Event event);
}
