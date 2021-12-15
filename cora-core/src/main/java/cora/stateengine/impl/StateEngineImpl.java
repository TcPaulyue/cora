package cora.stateengine.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cora.CoraBuilder;
import cora.graph.CoraGraph;
import cora.graph.fsm.Event;
import cora.graph.fsm.State;
import cora.graph.fsm.impl.StateImpl;
import cora.parser.dsl.CoraParser;
import cora.parser.dsl.SDLParser;
import cora.stateengine.StateEngine;
import cora.util.IngressTemplate;
import cora.util.StringUtil;
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
        SDLParser sdlParser = new SDLParser();
        String id = "id";
        String nodeType = "nodeType";
        //getState
        StateImpl state = (StateImpl) this.getState(nodeType,id);
        //if no event in query
        if(!sdlParser.isStateModifiedSchema(query)){
            ExecutionResult executeResult = graphQL.execute(query);
            String s = JSON.toJSONString(executeResult.getData());
            state.setExecutionResult(s);
            return state;
        }
        //else if event trigger
        StateImpl nextState = (StateImpl) this.getNextState(state,"mutationTemplate");
        //merge string
        String updateState = IngressTemplate.getUpdateStateTemplate(nodeType,id,nextState.getStateDesc());

        String merge = StringUtil.merge(query, updateState);
        ExecutionResult executeResult = graphQL.execute(merge);
        String s = JSON.toJSONString(executeResult.getData());
        nextState.setExecutionResult(s);
        //getNextState;
        return nextState;
    }

    public State getNextState(State state, String event) {
        //todo getId
        String id = "id";
        String nodeType = "nodeType";
        Map<Event, State> eventStateMap = CoraGraph.getCoraNode(nodeType).getFsm().nextExecution(state);
        return null;
    }

    public State getState(String nodeType,String id) {
        String queryState = IngressTemplate.getQueryStateTemplate(nodeType, id);
        ExecutionResult executeResult = graphQL.execute(queryState);
        if(executeResult.getErrors().isEmpty()){
            JSONObject jsonObject = JSON.parseObject(executeResult.getData());
            String state = jsonObject.getString("state");
            return new StateImpl(state);
        }
        return null;
    }
}
