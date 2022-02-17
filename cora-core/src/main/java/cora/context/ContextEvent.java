package cora.context;


import cora.graph.fsm.Event;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ContextEvent {
    private Pair<String,String> hook;
    private List<String> triggerItems;
    private String trigger;
    private Event action;

    public ContextEvent(Pair<String, String> hook, List<String> triggerItems, String trigger, Event action) {
        this.hook = hook;
        this.triggerItems = triggerItems;
        this.trigger = trigger;
        this.action = action;
    }


    public ContextEvent() {
    }

    public Pair<String, String> getHook() {
        return hook;
    }

    public void setHook(Pair<String, String> hook) {
        this.hook = hook;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Event getAction() {
        return action;
    }

    public void setAction(Event action) {
        this.action = action;
    }

    public List<String> getTriggerItems() {
        return triggerItems;
    }

    public void setTriggerItems(List<String> triggerItems) {
        this.triggerItems = triggerItems;
    }

    @Override
    public String toString() {
        return "ContextEvent{" +
                "hook=" + hook +
                ", triggerItems=" + triggerItems +
                ", trigger='" + trigger + '\'' +
                ", action=" + action +
                '}';
    }
}
