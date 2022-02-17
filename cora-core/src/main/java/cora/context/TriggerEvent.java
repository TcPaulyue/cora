package cora.context;

import com.alibaba.fastjson.JSONObject;

public class TriggerEvent {
    private String id;
    private String from;
    private String to;
    private JSONObject executeResult;

    public TriggerEvent(String id, String from, String to, JSONObject executeResult) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.executeResult = executeResult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public JSONObject getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(JSONObject executeResult) {
        this.executeResult = executeResult;
    }

    @Override
    public String toString() {
        return "TriggerEvent{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", executeResult=" + executeResult +
                '}';
    }
}
