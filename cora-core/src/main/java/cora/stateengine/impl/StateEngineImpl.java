package cora.stateengine.impl;

import com.alibaba.fastjson.JSON;
import cora.CoraBuilder;
import cora.graph.CoraGraph;
import cora.graph.fsm.Event;
import cora.graph.fsm.State;
import cora.graph.fsm.impl.StateImpl;
import cora.parser.dsl.CoraParser;
import cora.parser.dsl.SDLParser;
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
    public State execute(String query) {
        StateImpl state = (StateImpl) this.getState(query);
        SDLParser sdlParser = new SDLParser();
        if(!sdlParser.isStateModifiedSchema(query)){
            ExecutionResult executeResult = graphQL.execute(query);
            String s = JSON.toJSONString(executeResult.getData());
            state.setExecutionResult(s);
            return state;
        }
        State nextState = this.getNextState("mutationTemplate");
        //getNextState;
        return nextState;
    }

    public State getNextState(String mutation) {
        //todo getId
        String id = "id";
        String nodeType = "nodeType";
        State state = this.getState("queryString");
        Map<Event, State> eventStateMap = CoraGraph.getCoraNode(nodeType).getFsm().nextExecution(state);
        return null;
    }

    public State getState(String query) {

        return null;
    }
}
