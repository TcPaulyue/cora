package cora.stateengine.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cora.CoraBuilder;
import cora.graph.CoraGraph;
import cora.graph.fsm.Event;
import cora.graph.fsm.State;
import cora.graph.fsm.impl.InputEvent;
import cora.graph.fsm.impl.StateImpl;
import cora.parser.dsl.CoraParser;
import cora.parser.dsl.JsonSchemaParser;
import cora.stateengine.StateEngine;
import cora.util.IngressTemplate;
import cora.util.StringUtil;
import graphql.ExecutionResult;
import graphql.GraphQL;

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
    public State execute(String input) {
        JsonSchemaParser jsonSchemaParser = new JsonSchemaParser();
        InputEvent event = (InputEvent) jsonSchemaParser.parseEvent(input);

        //if no event in query
        if(event == null){
            ExecutionResult executeResult = graphQL.execute(input);
            String s = JSON.toJSONString(executeResult.getData());
            StateImpl state = new StateImpl(null);
            state.setExecutionResult(s);
            return state;
        }

        //getState
        StateImpl state = (StateImpl) this.getState(event.getNodeType(), event.getId());

        //if event trigger
        StateImpl nextState = (StateImpl) this.getNextState(state,event);

        //merge string
        String updateState = IngressTemplate.getUpdateStateTemplate(event.getNodeType(),event.getId(),nextState.getStateDesc());
        String merge = StringUtil.merge(input, updateState);

        ExecutionResult executeResult = graphQL.execute(merge);
        String s = JSON.toJSONString(executeResult.getData());
        nextState.setExecutionResult(s);
        //getNextState;
        return nextState;
    }

    public State getNextState(State state, Event event) {
        return CoraGraph.getCoraNode(((InputEvent)event).getNodeType()).getFsm().nextState(state, event);
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
