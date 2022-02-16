package cora.stateengine.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cora.context.ActorImpl;
import cora.graph.CoraGraph;
import cora.graph.fsm.Event;
import cora.graph.fsm.State;
import cora.graph.fsm.impl.StateImpl;
import cora.parser.dsl.CoraParser;
import cora.parser.dsl.JsonSchemaParser;
import cora.stateengine.StateEngine;
import cora.util.IngressTemplate;
import cora.util.StringUtil;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StateEngineImpl implements StateEngine {

    private static Logger logger = LogManager.getLogger(StateEngineImpl.class);

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
        Event event = jsonSchemaParser.parseEvent(input);

        //if no event in query
        if(event == null){
            logger.info("[stateEngine]: no event in query");
            ExecutionResult executeResult = graphQL.execute(input);
            String s;
            if(executeResult.getErrors().isEmpty())
                s = JSON.toJSONString(executeResult.getData());
            else{
                s = JSON.toJSONString(executeResult.getErrors());
                logger.error("[stateEngine]: graphql execute query error "+s);
            }
            StateImpl state = new StateImpl(null);
            state.setExecutionResult(s);
            return state;
        }
        return this.execute(event);
    }

    @Override
    public State execute(Event event) {
        //getState
        StateImpl currentState = (StateImpl) this.getState(event.getNodeType(), event.getId());
        StateImpl nextState;
        if(currentState == null){
            logger.error("[stateEngine]: get current state failed.");
            nextState = new StateImpl("get current state failed");
            nextState.setFailed(true);
            return nextState;
        }
        //if event trigger
        nextState = (StateImpl) this.getNextState(currentState,event);

        if(nextState == null){
            logger.error("[stateEngine]: get next state failed.");
            nextState = new StateImpl("get next state failed");
            nextState.setFailed(true);
            return nextState;
        }
        //get nodeInstance Id
        nextState.setNodeInstanceId(event.getId());
        //merge string
        String updateStateMutation = IngressTemplate.getUpdateStateTemplate(event.getNodeType(),event.getId(),nextState.getCurState());
        String updateMutation = StringUtil.merge( updateStateMutation,event.getData());

        ExecutionResult executeResult = graphQL.execute(updateMutation);
        String s;

        //getNextState;
        if(executeResult.getErrors().isEmpty()){
            s = JSON.toJSONString(executeResult.getData());
            nextState.setFailed(false);
            nextState.setPreState(currentState.getCurState());
        }
        //failed
        else{
            s = JSON.toJSONString(executeResult.getErrors());
            logger.error("[stateEngine]: graphql execute update query failed "+ s);
            nextState.setCurState(currentState.getCurState());
            nextState.setFailed(true);
        }
        nextState.setExecutionResult(s);
        return nextState;
    }

    public State getNextState(State state, Event event) {
        return CoraGraph.getCoraNode((event).getNodeType()).getFsm().nextState(state, event);
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
        logger.error("[stateEngine(getState)]: graphql execute getState failed "+ executeResult.getErrors());
        return null;
    }
}
