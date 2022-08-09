package ntut.csie.sslab.ddd.adapter.eventbroker;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.Json;

public class EventSerializer {

	private static EventSerializer eventSerializer;

    public static synchronized EventSerializer instance() {
        if (EventSerializer.eventSerializer == null) {
            EventSerializer.eventSerializer = new EventSerializer();
        }

        return EventSerializer.eventSerializer;
    }
    
    public static String serialize(DomainEvent domainEvent) {
//        String eventBody = new Gson().toJson(domainEvent);

        String eventBody = Json.asString(domainEvent);
        return eventBody;
    }

    public static <T extends DomainEvent> T deserialize(String eventBody, final Class<T> eventType) {
//        T domainEvent = new Gson().fromJson(eventBody, eventType);

        T domainEvent = Json.readValue(eventBody, eventType);
        return domainEvent;
    }

}
