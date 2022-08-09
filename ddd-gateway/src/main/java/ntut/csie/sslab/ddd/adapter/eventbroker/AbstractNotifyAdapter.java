package ntut.csie.sslab.ddd.adapter.eventbroker;

import ntut.csie.sslab.ddd.entity.DomainEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractNotifyAdapter {
    public static final int DEFAULT_QUEUE_SIZE = 1024;
    private final BlockingQueue<DomainEvent> eventQueue;

    public AbstractNotifyAdapter(int capacity) {
        this.eventQueue = new ArrayBlockingQueue<>(capacity);
    }

    protected void putEvent(DomainEvent event) {
        try {
            eventQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected DomainEvent takeEvent() throws InterruptedException {
        return eventQueue.take();
    }
}
