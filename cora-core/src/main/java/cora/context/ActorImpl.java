package cora.context;

import com.google.common.eventbus.EventBus;
import cora.graph.fsm.Event;
import cora.graph.fsm.impl.InputEvent;
import cora.stateengine.StateEngine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ActorImpl extends Thread implements Actor {
    private static volatile Boolean stopped = false;

    private StateEngine stateEngine;

    private EventBus eventBus;


    public ActorImpl(StateEngine stateEngine,EventBus eventBus) {
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
                if(event.isDuration()){
                    try {
                        Thread.sleep(event.getDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("execute event");
                //todo: get from to
                stateEngine.execute(event);
                eventBus.post(new TriggerEvent(event.getId(),"off","on"));
                System.out.println("post event");
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
