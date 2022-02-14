package cora.graph.fsm;

import java.util.Map;

public interface Event {

    public String getNodeType();

    public String getId();

    public Map<String, Object> getData();
}
