package ntut.csie.sslab.ddd.adapter.eventbroker;

import com.eventstore.dbclient.*;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class EsdbVolatileListener implements EsdbListener {

    private EventStoreDBClient client;
    private final DomainEventTypeMapper domainEventTypeMapper;
    private final DomainEventBus domainEventBus;
    private Subscription subscription;
    private final CountDownLatch cancellation = new CountDownLatch(1);

    public EsdbVolatileListener(String connectionString,
                                DomainEventTypeMapper domainEventTypeMapper,
                                DomainEventBus domainEventBus) {
        super();

        requireNotNull("ConnectionString", connectionString);
        requireNotNull("DomainEventTypeMapper", domainEventTypeMapper);
        requireNotNull("DomainEventBus", domainEventBus);

        this.domainEventTypeMapper = domainEventTypeMapper;
        this.domainEventBus = domainEventBus;
        connect(connectionString);
    }

    private void connect(String connectionString) {
        EventStoreDBClientSettings settings = EventStoreDBConnectionString.parseOrThrow(connectionString);
        this.client = EventStoreDBClient.create(settings);
    }

    @Override
    public void run() {
        System.out.println("ESDB listener starts");

        SubscriptionListener listener = new SubscriptionListener(){
            @Override
            public void onEvent(Subscription subscription, ResolvedEvent event) {
                try{
                    RecordedEvent record = event.getEvent();
                    if (record.getEventType().startsWith("$")){
                        System.out.println("Got system event " + record.getEventType() + " ==========>");
                        return;
                    }

                    final DomainEvent domainEvent = toDomain(event.getEvent().getEventType(), event.getEvent().getEventData());
                    System.out.println("onEvent, EventType =====>" + record.getEventType() + ", data = " + domainEvent.toString());

                    domainEventBus.post(domainEvent);
                }
                catch (Throwable e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(Subscription subscription) {
                cancellation.countDown();
                System.out.println("onCancelled  =====>" + subscription);
            }

            @Override
            public void onError(Subscription subscription, Throwable throwable) {
                System.out.println("onError  =====>" + subscription);
                throwable.printStackTrace();
            }
        };

        String excludeSystemEventsRegex = "^[^\\$]";
        SubscriptionFilter filter = SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression(excludeSystemEventsRegex)
                .build();

        SubscribeToAllOptions options = SubscribeToAllOptions.get()
                .filter(filter)
                .resolveLinkTos()
                .fromEnd();

        try {
            subscription = client
                    .subscribeToAll(listener, options)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        //Doing nothing
    }

    @Override
    public void shutdown(){
        if(null != subscription) {
            subscription.stop();
            try {
                cancellation.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private DomainEvent toDomain(String eventType, byte[] eventData) {
        try {
            Class<?> cls = domainEventTypeMapper.toClass(eventType);
            Object domainEvent = Json.readAs(
                    eventData, cls);
            return (DomainEvent) domainEvent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}



