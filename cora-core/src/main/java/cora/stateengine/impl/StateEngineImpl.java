package cora.stateengine.impl;

import cora.CoraBuilder;
import cora.graph.CoraGraph;
import cora.graph.fsm.Event;
import cora.graph.fsm.State;
import cora.parser.dsl.CoraParser;
import cora.stateengine.StateEngine;
import graphql.ExecutionResult;
import graphql.GraphQL;

import java.util.Map;

public class StateEngineImpl implements StateEngine {
    private CoraParser coraParser;

    private GraphQL graphQL;

    private final CoraBuilder coraBuilder;

    public StateEngineImpl(CoraParser coraParser, CoraBuilder coraBuilder) {
        this.coraParser = coraParser;
        this.coraBuilder = coraBuilder;
        this.graphQL = coraBuilder.createGraphQL();
    }

    @Override
    public State getNextState(String mutation) {
        //todo getId
        String id = "id";
        String nodeType = "nodeType";
        State state = this.getState("queryString");
        Map<Event, State> eventStateMap = CoraGraph.getCoraNode(nodeType).getFsm().nextExecution(state);
        return null;
    }

    @Override
    public State getState(String query) {
        ExecutionResult executeResult = graphQL.execute(query);
        return null;
    }
}
