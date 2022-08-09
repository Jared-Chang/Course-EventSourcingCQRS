package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.adapter.eventbroker.EventDataBuilderJava8;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventMetadata;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class DomainEventMapper {
    public static final String RAW_TYPE = "rawType";

    private static DomainEventTypeMapper mapper = DomainEvent.mapper();

    public static final DomainEventTypeMapper getMapper() {
        return mapper;
    }

    public static void setMapper(DomainEventTypeMapper newMapper) {
        newMapper.getMap().forEach( (key, value) -> {
            mapper.put(key, value);
        });
    }

    public static DomainEventData toData(DomainEvent event) {
        requireNotNull("DomainEvent", event);

        return EventDataBuilderJava8.json(
                        mapper.toMappingType(event.getClass()),
                        event)
                .eventId(event.id())
                .buildNewDomainEventData();
    }


    public static List<DomainEventData> toData(List<DomainEvent> events) {
        requireNotNull("DomainEvent", events);

        return events.stream().map(DomainEventMapper::toData).collect(Collectors.toList());
    }

    public static <T extends DomainEvent> T toDomain(DomainEventData data) {
        requireNotNull("DomainEventData", data);
        requireNotNull("Please call setMapper to config public class DomainEventMapper first", mapper);

        T domainEvent = null;
        try {
            domainEvent = (T) Json.readAs(data.eventData(), mapper.toClass(data.eventType()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return domainEvent;
    }

    public static <T extends DomainEvent> List<T> toDomain(List<DomainEventData> datas) {
        requireNotNull("DomainEventData list", datas);

        List<T> result = new ArrayList<>();
        datas.forEach( x -> result.add(toDomain(x)));
        return result;
    }

    private static String getEventRawTypeMetadata(DomainEvent event) {
        DomainEventMetadata metadata = new DomainEventMetadata();
        metadata.append(RAW_TYPE, event.getClass().getName());

        return metadata.asJsonString();
    }

}
