package cora.parser.dsl;

import cora.context.Context;
import cora.graph.fsm.Event;
import cora.graph.fsm.FSM;
import graphql.language.Definition;

import java.util.List;

public interface CoraParser {
    List<Definition> parseSchema(String schema);

    FSM parseFSM(String fsm);

    Context parseContext(String context);

    Event parseEvent(String event);

    boolean isValid(String schema);
}
