package cora.context;

import com.google.common.eventbus.EventBus;
import cora.graph.fsm.Event;
import cora.stateengine.StateEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private String contextId;

    private String contextName;

    private List<String> actorIds;

    private Map<String,Map<Pair<String,String>, ContextEvent>> onEvents = new HashMap<>();

    private Map<String,ActorImpl> actorMap = new ConcurrentHashMap<>();


    public Context(String contextId, List<String> actorIds, String contextName) {
        this.contextId = contextId;
        this.contextName = contextName;
        this.actorIds = actorIds;
    }

    public Context(String contextId, Map<String, ActorImpl> actorMap) {
        this.contextId = contextId;
        this.actorMap = actorMap;
    }

    public void init(StateEngine stateEngine, EventBus eventBus){
        actorIds.forEach(id->{
            actorMap.put(id,new ActorImpl(stateEngine,eventBus));
        });
        this.run();
    }

    public void run(){
        actorMap.keySet().forEach(id->{
            actorMap.get(id).start();
            System.out.println("running");
        });
    }

    public boolean addEvent(String id, Event event){
        if(actorMap.containsKey(id)){
            actorMap.get(id).addEvent(event);
            return true;
        }
        return false;
    }

    public ContextEvent getContextEvent(String id,String from,String to){
        return onEvents.get(id).get(new ImmutablePair<>(from,to));
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public List<String> getActorIds() {
        return actorIds;
    }

    public void setActorIds(List<String> actorIds) {
        this.actorIds = actorIds;
    }

    public Map<String, Map<Pair<String, String>, ContextEvent>> getOnEvents() {
        return onEvents;
    }

    public void setOnEvents(Map<String, Map<Pair<String, String>, ContextEvent>> onEvents) {
        this.onEvents = onEvents;
    }

    public Map<String, ActorImpl> getActorMap() {
        return actorMap;
    }

    public void setActorMap(Map<String, ActorImpl> actorMap) {
        this.actorMap = actorMap;
    }

    public static void main(String[] args) {
//        EventBus eventBus = new EventBus();
//        ContextHandler contextHandler = new ContextHandler(new JsonSchemaParser(),new StateEngineImpl());
//        eventBus.register(contextHandler);
//        Map<String,ActorImpl> actorMap = new ConcurrentHashMap<>();
//        ActorImpl actor = new ActorImpl();
//        actor.setEventBus(eventBus);
//        InputEvent event = new InputEvent("event0");
//        event.setDuration(10000);
//        event.setDuration(true);
//        event.setPriority(1);
//        actor.addEvent(event);
//        InputEvent event1 = new InputEvent("event1");
//        event1.setDuration(5000);
//        event1.setDuration(true);
//        event1.setPriority(2);
//        actor.addEvent(event1);
//        actorMap.put("1",actor);
//        Context context = new Context("001",actorMap);
//        contextHandler.getContextMap().put("001",context);
//        context.run();
    }
}
