package cora.graph.fsm.impl;

import cora.graph.fsm.Event;

import java.util.Map;
import java.util.Objects;

public class InputEvent implements Event {
    private String id;

    private String nodeType;

    private String eventName;

    private Map<String,Object> data;

    private boolean isDuration;

    private long duration;

    private Integer priority;

    public InputEvent(String eventName) {
        this.eventName = eventName;
    }

    public InputEvent(String id, String nodeType, String eventName, Map<String, Object> data) {
        this.id = id;
        this.nodeType = nodeType;
        this.eventName = eventName;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputEvent that = (InputEvent) o;
        return Objects.equals(eventName, that.eventName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName);
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public boolean isDuration() {
        return isDuration;
    }

    public void setDuration(boolean duration) {
        isDuration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
