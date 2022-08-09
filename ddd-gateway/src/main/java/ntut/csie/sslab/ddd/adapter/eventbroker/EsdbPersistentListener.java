package ntut.csie.sslab.ddd.adapter.eventbroker;

import com.eventstore.dbclient.*;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class EsdbPersistentListener implements EsdbListener {

    private EventStoreDBClientSettings settings;
    private EventStoreDBClient eventStoreDBClient;
    private EventStoreDBPersistentSubscriptionsClient client;
    private final String INCLUDE_EZKANBAN_EVENT_REGEX = "(\\w+Events\\$\\w+)";
    private SubscriptionFilter filter;
    private final static String GROUP_NAME = "EZKANBAN_MONO_MAIN";
    private final DomainEventTypeMapper domainEventTypeMapper;
    private final DomainEventBus domainEventBus;
    private PersistentSubscription subscription;
    private final RecentlyReadEvents recentlyReadEvents;
    private final int CAPACITY = 32;
    private final boolean ignoreUnknownEventType;
    public static final boolean IGNORE_UNKNOWN_EVENT_TYPE = true;

    public EsdbPersistentListener(String connectionString,
                                  DomainEventTypeMapper domainEventTypeMapper,
                                  DomainEventBus domainEventBus) {
        this(connectionString, domainEventTypeMapper, domainEventBus, false);
    }

    public EsdbPersistentListener(String connectionString,
                                  DomainEventTypeMapper domainEventTypeMapper,
                                  DomainEventBus domainEventBus,
                                  boolean ignoreUnknownEventType) {
        super();

        requireNotNull("ConnectionString", connectionString);
        requireNotNull("DomainEventTypeMapper", domainEventTypeMapper);
        requireNotNull("DomainEventBus", domainEventBus);

        this.domainEventTypeMapper = domainEventTypeMapper;
        this.domainEventBus = domainEventBus;
        this.ignoreUnknownEventType = ignoreUnknownEventType;
        recentlyReadEvents = new RecentlyReadEvents(CAPACITY);
        connect(connectionString);

        filter = SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression(INCLUDE_EZKANBAN_EVENT_REGEX)
                .build();

        try {
            client.createToAll(GROUP_NAME,
                    PersistentSubscriptionToAllSettings.builder()
                            .filter(filter)
                            .fromEnd()
                            .build()).get();
            System.out.println("group created");
        } catch (ExecutionException e) {
            if (e.getMessage().contains("ALREADY_EXISTS: Subscription group EZKANBAN_MONO_MAIN on stream $all exists.")) {
                // Ignore
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(String connectionString) {
        settings = EventStoreDBConnectionString.parseOrThrow(connectionString);
        eventStoreDBClient = EventStoreDBClient.create(settings);
        client = EventStoreDBPersistentSubscriptionsClient.create(settings);
    }

    public void deletePersistentSubscription() {
        try {
            client.deleteToAll(GROUP_NAME).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Esdb persistent listener starts");

        final CompletableFuture<Integer> result = new CompletableFuture<>();

        SubscribePersistentSubscriptionOptions connectOptions = SubscribePersistentSubscriptionOptions.get()
                .setBufferSize(512);
        recentlyReadEvents.clear();

        try {
            subscription = client.subscribeToAll(GROUP_NAME, connectOptions, new PersistentSubscriptionListener() {
                @Override
                public void onEvent(PersistentSubscription subscription, ResolvedEvent resolvedEvent) {
                    RecordedEvent event = resolvedEvent.getEvent();
                    System.out.println("Persistent onEvent, EventType =====>" + event.getEventType());
                    final Optional<DomainEvent> domainEvent = toDomain(event.getEventType(), event.getEventData());

                    if (domainEvent.isPresent()) {
                        // TODO: Need to guarantee domain event posted successfully before ack.
                        recentlyReadEvents.add(domainEvent.get());
                        domainEventBus.post(domainEvent.get());
                    }
                    subscription.ack(resolvedEvent);
                }

                @Override
                public void onError(PersistentSubscription subscription, Throwable throwable) {
                    System.out.println("onError");
                    result.completeExceptionally(throwable);
                }

                @Override
                public void onCancelled(PersistentSubscription subscription) {
                    System.out.println("onCancelled");
                }
            }).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class RecentlyReadEvents {
        private final int capacity;
        private Queue<DomainEvent> recentlyReadEvents;

        public RecentlyReadEvents(int capacity) {
            this.recentlyReadEvents = new LinkedList<>();
            this.capacity = capacity;
        }

        public int size() {
            return recentlyReadEvents.size();
        }

        public DomainEvent take() {
            return recentlyReadEvents.poll();
        }

        public void add(DomainEvent domainEvent) {
            if (recentlyReadEvents.size() == capacity) {
                recentlyReadEvents.poll();
            }
            recentlyReadEvents.add(domainEvent);
        }

        public void clear() {
            recentlyReadEvents.clear();
        }

        public DomainEvent[] getRecentlyReadEvents() {
            var x = recentlyReadEvents.toArray(new DomainEvent[recentlyReadEvents.size()]);
            return recentlyReadEvents.toArray(new DomainEvent[recentlyReadEvents.size()]);
        }
    }

    public void updatePersistentSubscription() {

        PersistentSubscriptionToAllSettings updatedSettings = PersistentSubscriptionToAllSettings.builder()
                .filter(filter)
//                .startFrom(0, 0)
                .fromEnd()
                .build();

        UpdatePersistentSubscriptionToAllOptions options = UpdatePersistentSubscriptionToAllOptions.get()
                .settings(updatedSettings);

        try {
            client.updateToAll(GROUP_NAME, options).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (null != subscription) {
            subscription.stop();
        }
    }

    @Override
    public void shutdown() {
        if (null != subscription) {
            subscription.stop();
        }
    }

    private Optional<DomainEvent> toDomain(String eventType, byte[] eventData) {
        try {
            Class<?> cls = domainEventTypeMapper.toClass(eventType);
            Object domainEvent = Json.readAs(
                    eventData, cls);
            return Optional.of((DomainEvent) domainEvent);
        } catch (Exception e) {
            if (ignoreUnknownEventType) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }
    }

    public DomainEvent[] getRecentlyReadEvents() {
        return recentlyReadEvents.getRecentlyReadEvents();
    }
}


