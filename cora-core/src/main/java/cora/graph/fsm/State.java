package cora.graph.fsm;

public interface State {
    public Boolean getFailed() ;

    public String getPreState();

    public String getCurState();

    public String getNodeInstanceId();

    public String getExecutionResult();
}
