package ntut.csie.sslab.ddd.adapter.gateway;

import com.google.common.eventbus.EventBus;
import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GoogleEventBusAdapter extends EventBus implements DomainEventBus, Runnable {

    private final BlockingQueue<DomainEvent> eventQueue;

    public GoogleEventBusAdapter() {
        super();
        eventQueue = new ArrayBlockingQueue<>(1024);
    }

    @Override
    public synchronized void post(DomainEvent domainEvent){
        try {
            eventQueue.put(domainEvent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void postAll(AggregateRoot aggregateRoot) {
        List<DomainEvent> domainEvents = new ArrayList<>(aggregateRoot.getDomainEvents());

        domainEvents.forEach(this::post);
        domainEvents.clear();
        aggregateRoot.clearDomainEvents();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DomainEvent domainEvent = eventQueue.take();
                super.post(domainEvent);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

