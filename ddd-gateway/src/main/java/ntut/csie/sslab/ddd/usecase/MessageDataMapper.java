package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventMetadata;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class MessageDataMapper {
    public static final String RAW_TYPE = "rawType";
    private static DomainEventTypeMapper mapper = null;
    public static DomainEventTypeMapper getMapper() {
        return mapper;
    }
    public static void setMapper(DomainEventTypeMapper newMapper) {
        mapper = newMapper;
    }

    public static MessageData toData(DomainEvent event) {
        requireNotNull("DomainEvent", event);
        requireNotNull("Please call setMapper to config public class DomainEventMapper first", mapper);

        return new MessageData(
                event.id(),
                Json.asString(event),
                mapper.toMappingType(event),
                "",
                event.occurredOn(),
                getEventRawTypeMetadata(event)
        );
    }

    public static <T extends DomainEvent> T toDomain(MessageData data) {
        requireNotNull("DomainEventData", data);
        requireNotNull("Please call setMapper to config public class DomainEventMapper first", mapper);

        T domainEvent = null;
        try {
            domainEvent = (T) Json.readValue(data.getEventBody(), mapper.toClass(data.getEventType()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return domainEvent;
    }


    public static List<MessageData> toData(List<DomainEvent> events) {
        requireNotNull("DomainEvent", events);
        requireNotNull("Please call setMapper to config public class DomainEventMapper first", mapper);

        return events.stream().map(MessageDataMapper::toData).collect(Collectors.toList());
    }



    public static <T extends DomainEvent> List<T> toDomain(List<MessageData> datas) {
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
