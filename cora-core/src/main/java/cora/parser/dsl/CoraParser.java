package cora.parser.dsl;

import cora.graph.fsm.FSM;
import graphql.language.Definition;

import java.util.List;

public interface CoraParser {
    List<Definition> parseSchema(String schema);

    FSM parseFSM(String fsm);
    boolean isValid(String schema);
}
