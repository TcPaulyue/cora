package cora.context;


import cora.graph.fsm.Event;
import org.apache.commons.lang3.tuple.Pair;

public class ContextEvent {
    private Pair<String,String> hook;
    private String trigger;
    private Event action;

    public ContextEvent(Pair<String, String> hook, String trigger, Event action) {
        this.trigger = trigger;
        this.hook = hook;
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

    @Override
    public String toString() {
        return "ContextEvent{" +
                "hook=" + hook +
                ", trigger='" + trigger + '\'' +
                ", action=" + action +
                '}';
    }
}
