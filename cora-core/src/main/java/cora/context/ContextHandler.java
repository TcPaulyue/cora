package cora.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cora.graph.fsm.impl.InputEvent;
import cora.parser.dsl.CoraParser;
import cora.stateengine.StateEngine;

import java.util.HashMap;
import java.util.Map;

public class ContextHandler {

    private static Map<String,Context> contextMap = new HashMap<>();

    private final CoraParser coraParser;

    private final StateEngine stateEngine;

    public ContextHandler(CoraParser coraParser, StateEngine stateEngine) {
        this.coraParser = coraParser;
        this.stateEngine = stateEngine;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, Context> contextMap) {
        this.contextMap = contextMap;
    }

    public boolean addContext(String schema){
        Context context = coraParser.parseContext(schema);
        if(context == null)
            return false;
        EventBus eventBus = new EventBus();
        eventBus.register(this);
        context.init(stateEngine,eventBus);
        contextMap.put(context.getContextId(),context);
        return true;
    }

    public boolean deleteContext(String id){
        contextMap.remove(id);
        return true;
    }

    public void deliverEvent(String event){
        JSONObject jsonObject = JSON.parseObject(event);
        String contextId = jsonObject.getString("contextId");
        InputEvent parsedEvent = (InputEvent) coraParser.parseEvent(jsonObject.getString("input_event"));
        contextMap.get(contextId).addEvent(parsedEvent.getId(),parsedEvent);
    }


    @Subscribe
    public void handleContextEvent(TriggerEvent triggerEvent){
        System.out.println(triggerEvent.getId());
        contextMap.keySet().forEach(id->{
            ContextEvent contextEvent = contextMap.get(id).getContextEvent(triggerEvent.getId()
                    ,triggerEvent.getFrom(),triggerEvent.getTo());
            if(contextEvent == null)
                return;
            //get context event

            //get trigger situation
            String trigger = contextEvent.getTrigger();
            //get actor state

            //judge trigger situation

            //publish event
            InputEvent event1 = new InputEvent("event2");
            event1.setDuration(6000);
            event1.setDuration(true);
            event1.setPriority(2);
            contextMap.get(id).addEvent("1",event1);
        });
    }
}
