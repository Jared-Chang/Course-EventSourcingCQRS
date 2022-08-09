package ntut.csie.sslab.ddd.adapter.repository;

import com.eventstore.dbclient.*;
import ntut.csie.sslab.ddd.usecase.AggregateRootData;
import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ddd.usecase.DomainEventData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class EsdbStoreAdapter implements EventStore {
    private EventStoreDBClient client;

    @Override
    public void save(AggregateRootData aggregateRootData) {
        if (null == aggregateRootData) {
            throw new RuntimeException("AggregateData cannot be null.");
        }
        List<EventData> eventDatas = new ArrayList<>();
        aggregateRootData.getDomainEventDatas().stream().forEach(domainEventData -> {
            eventDatas.add(
                    new EventData(domainEventData.id(),
                    domainEventData.eventType(),
                    domainEventData.contentType(),
                    domainEventData.eventData(),
                    domainEventData.userMetadata()));
        });

        try {
            AppendToStreamOptions options;
            if (-1 == aggregateRootData.getVersion()) {
                options = AppendToStreamOptions.get()
                        .expectedRevision(ExpectedRevision.ANY);
            } else {
                options = AppendToStreamOptions.get()
                        .expectedRevision(new StreamRevision(aggregateRootData.getVersion()));
            }
            WriteResult writeResult = client
                    .appendToStream(aggregateRootData.getStreamName(), options, eventDatas.iterator())
                    .get();

            aggregateRootData.setVersion(writeResult.getNextExpectedRevision().getValueUnsigned());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if(e.getCause() instanceof WrongExpectedVersionException ex){
                throw ex;
            } else {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public Optional<AggregateRootData> load(String aggregateStreamName) {
        if (null == aggregateStreamName) {
            throw new IllegalArgumentException("AggregateStreamName cannot be null.");
        }

        List<ResolvedEvent> resolvedEvents = new ArrayList<>();
        try {
            resolvedEvents = getResolvedEvents(aggregateStreamName);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StreamNotFoundException) {
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (resolvedEvents.isEmpty())
            return Optional.empty();

        AggregateRootData aggregateRootData = new AggregateRootData();
        resolvedEvents.stream().forEach(x -> {
            aggregateRootData.getDomainEventDatas().add(toDomainEventData(x));
        });
        StreamRevision revision = resolvedEvents.get(resolvedEvents.size() - 1).getEvent().getStreamRevision();
        aggregateRootData.setVersion(revision.getValueUnsigned());
        aggregateRootData.setStreamName(aggregateStreamName);

        return Optional.of(aggregateRootData);
    }

    private List<ResolvedEvent> getResolvedEvents(String streamName)
            throws InterruptedException, ExecutionException {
        ReadStreamOptions options = ReadStreamOptions.get()
                .resolveLinkTos()
                .forwards()
                .fromStart();

        return client.
                readStream(streamName, options)
                .get()
                .getEvents();
    }


    @Override
    public List<DomainEventData> getCategoryEvent(String categoryName) {
        List<DomainEventData> domainEventData = new ArrayList<>();
        List<ResolvedEvent> resolvedEvents;
        try {
            resolvedEvents = getResolvedEvents(EVENT_TYPE_PREFIX + categoryName);
            resolvedEvents.forEach(x -> {
                domainEventData.add(toDomainEventData(x));
            });
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StreamNotFoundException) {
                return domainEventData;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return domainEventData;
    }

    @Override
    public List<DomainEventData> getEventFromStream(String streamName, long revision) {
        List<DomainEventData> domainEventData = new ArrayList<>();
        List<ResolvedEvent> resolvedEvents;
        try {
            ReadStreamOptions options = ReadStreamOptions.get()
                    .resolveLinkTos()
                    .forwards()
                    .fromRevision(revision);

            resolvedEvents = client.
                    readStream(streamName, options)
                    .get()
                    .getEvents();

            resolvedEvents.forEach(x -> {
                domainEventData.add(toDomainEventData(x));
            });
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StreamNotFoundException) {
                return domainEventData;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return domainEventData;
    }

    @Override
    public Optional<DomainEventData> getLastEventFromStream(String streamName) {

        try {
            ReadStreamOptions options = ReadStreamOptions.get()
                    .resolveLinkTos()
                    .fromStart();

            var resolvedEvents = client.
                    readStream(streamName, options)
                    .get()
                    .getEvents();

            if (!resolvedEvents.isEmpty()){
                return Optional.of(toDomainEventData(resolvedEvents.get(resolvedEvents.size()-1)));
            }
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StreamNotFoundException) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();


    }

    private DomainEventData toDomainEventData(ResolvedEvent resolvedEvent) {
        return new DomainEventData(
                resolvedEvent.getEvent().getEventId(),
                resolvedEvent.getEvent().getEventType(),
                resolvedEvent.getEvent().getContentType(),
                resolvedEvent.getEvent().getEventData(),
                resolvedEvent.getEvent().getUserMetadata());
    }

    private final String EVENT_TYPE_PREFIX = "$et-";

    public EsdbStoreAdapter(String connectionString) {
        super();
        connect(connectionString);
    }

    private void connect(String connectionString) {
        EventStoreDBClientSettings settings = EventStoreDBConnectionString.parseOrThrow(connectionString);
        this.client = EventStoreDBClient.create(settings);
    }

    @Override
    public void close() {
        try {
            if (null != client)
                client.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
