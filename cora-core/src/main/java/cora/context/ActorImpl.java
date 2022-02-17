package cora.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.EventBus;
import cora.graph.fsm.Event;
import cora.graph.fsm.impl.InputEvent;
import cora.stateengine.StateEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ActorImpl extends Thread implements Actor {

    private String actorId;

    private static Logger logger = LogManager.getLogger(ActorImpl.class);

    private static volatile Boolean stopped = false;

    private StateEngine stateEngine;

    private EventBus eventBus;

    public ActorImpl(String actorId, StateEngine stateEngine, EventBus eventBus) {
        this.actorId = actorId;
        this.stateEngine = stateEngine;
        this.eventBus = eventBus;
    }

    public ActorImpl() {
    }

    private static final Queue<InputEvent> queue = new ConcurrentLinkedDeque<>();

    @Override
    public boolean addEvent(Event event) {
        return queue.add((InputEvent) event);
    }

    @Override
    public void run() {
        while(!stopped){
            InputEvent event = queue.poll();
            if(event!=null){
                logger.info("actor-"+this.actorId + ": "+"execute event start "+event.getId());
                if(event.isDuration()){
                    try {
                        Thread.sleep(event.getDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                cora.graph.fsm.State state = stateEngine.execute(event);
                if(state.getFailed()){
                    logger.error("get state from stateEngine failed "+state.getExecutionResult());
                    return;
                }
                logger.info("event bus publish event "+state.getNodeInstanceId()+" "+state.getPreState()
                        +" "+state.getCurState());
                String key = JSON.parseObject(state.getExecutionResult()).keySet().stream().findFirst().get();
                JSONObject executeResult = JSON.parseObject(state.getExecutionResult()).getJSONObject(key);
                eventBus.post(new TriggerEvent(state.getNodeInstanceId()
                        ,state.getPreState(),state.getCurState(), executeResult));
                logger.info("actor-"+this.actorId +": "+"execute event end "+event.getId());
            }
        }
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

}
