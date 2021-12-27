package cora.stateengine.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    private final GraphQL graphQL;

    public StateEngineImpl(CoraParser coraParser, GraphQL graphQL) {
        this.coraParser = coraParser;
        this.graphQL = graphQL;
    }

    @Override
    public State execute(String input) {
        //todo
        JsonSchemaParser jsonSchemaParser = new JsonSchemaParser();
        InputEvent event = (InputEvent) jsonSchemaParser.parseEvent(input);

        //if no event in query
        if(event == null){
            ExecutionResult executeResult = graphQL.execute(input);
            String s;
            if(executeResult.getErrors().isEmpty())
                s = JSON.toJSONString(executeResult.getData());
            else s = JSON.toJSONString(executeResult.getErrors());

            StateImpl state = new StateImpl(null);
            state.setExecutionResult(s);
            return state;
        }

        //getState
        StateImpl currentState = (StateImpl) this.getState(event.getNodeType(), event.getId());

        //if event trigger
        StateImpl nextState = (StateImpl) this.getNextState(currentState,event);
        //merge string
        String updateStateMutation = IngressTemplate.getUpdateStateTemplate(event.getNodeType(),event.getId(),nextState.getStateDesc());
        String updateMutation = StringUtil.merge( updateStateMutation,event.getData());

        ExecutionResult executeResult = graphQL.execute(updateMutation);
        String s;
        //getNextState;
        if(executeResult.getErrors().isEmpty()){
            s = JSON.toJSONString(executeResult.getData());
            nextState.setExecutionResult(s);
        }
        //failed
        else{
            s = JSON.toJSONString(executeResult.getErrors());
            nextState.setStateDesc(currentState.getStateDesc());
            nextState.setExecutionResult(s);
        }
        return nextState;
    }

    public State getNextState(State state, Event event) {
        return CoraGraph.getCoraNode(((InputEvent)event).getNodeType()).getFsm().nextState(state, event);
    }

    public State getState(String nodeType,String id) {
        String queryState = IngressTemplate.getQueryStateTemplate(nodeType, id);
        ExecutionResult executeResult = graphQL.execute(queryState);
        if(executeResult.getErrors().isEmpty()){
            JSONObject jsonObject = new JSONObject(executeResult.getData());
            String state = null;
            for(String key:jsonObject.keySet()){
                state = jsonObject.getJSONObject(key).getString("state");
                break;
            }
            return new StateImpl(state);
        }
        return null;
    }
}
