package cora.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cora.graph.fsm.Event;
import cora.graph.fsm.impl.InputEvent;
import cora.groovy.GroovyShellService;
import cora.parser.dsl.CoraParser;
import cora.stateengine.StateEngine;
import cora.util.IngressTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextHandler {

    private static Logger logger = LogManager.getLogger(ContextHandler.class);

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
        if(context == null){
            logger.error("[ContextHandler]: coraParser parse Context schema failed");
            return false;
        }
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

    public boolean deliverEvent(String event){
        logger.info("[ContextHandler]: deliver event start "+event);
        JSONObject jsonObject = JSON.parseObject(event);
        String contextId = jsonObject.getString("contextId");
        InputEvent parsedEvent = (InputEvent) coraParser.parseEvent(jsonObject.getString("input_event"));
        if(parsedEvent == null)
            return false;
        contextMap.get(contextId).addEvent(parsedEvent.getId(),parsedEvent);
        logger.info("[ContextHandler]: deliver event end "+event);
        return true;
    }

    @Subscribe
    public void handleContextEvent(TriggerEvent triggerEvent){
        logger.info("[ContextHandler]: handle trigger event from eventbus start "+triggerEvent.toString());
        contextMap.keySet().forEach(id->{
            ContextEvent contextEvent = contextMap.get(id).getContextEvent(triggerEvent.getId()
                    ,triggerEvent.getFrom(),triggerEvent.getTo());
            if(contextEvent == null)
                return;
            //todo
            //get context event
            Map<String,Object> triggerItems = new HashMap<>();
            contextEvent.getTriggerItems().forEach(triggerItem->{
                triggerItems.put(triggerItem,triggerEvent.getExecuteResult().getString(triggerItem));
            });
            //get trigger situation
            String trigger = contextEvent.getTrigger();

            boolean execute = GroovyShellService.execute(triggerItems, trigger);

            //get actor state

            //judge trigger situation

            //publish event
            Event action = contextEvent.getAction();

            logger.info("[ContextHandler]: deliver action event "+triggerEvent.getId());
            contextMap.get(id).addEvent(action.getId(),action);

        });
        logger.info("[ContextHandler]: handle trigger event from eventbus end "+triggerEvent.toString());

    }
}
