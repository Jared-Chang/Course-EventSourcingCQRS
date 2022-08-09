package ntut.csie.sslab.ddd.entity;

import ntut.csie.sslab.ddd.entity.common.Json;

import java.util.Date;
import java.util.UUID;

public class RemoteDomainEvent implements DomainEvent {

	private final UUID id;
	private final Date occurredOn;
	private final String jsonEvent;
	private final String eventType;
	private final String eventSimpleName;
	private final String tag;

	public RemoteDomainEvent(DomainEvent event, String tag, Date occurredOn)  {
		this(event, UUID.randomUUID(), tag, occurredOn);
	}

	public RemoteDomainEvent(DomainEvent event, UUID id, String tag, Date occurredOn){
		this.id = id;
		this.occurredOn = occurredOn;
		this.eventType = event.getClass().getTypeName();
		this.eventSimpleName = event.getClass().getSimpleName();
		this.jsonEvent = Json.asString(event);
		this.tag = tag;
	}

	public RemoteDomainEvent(UUID id, Date occurredOn, String jsonEvent, String eventType, String eventSimpleName, String tag) {
		this.id = id;
		this.occurredOn = occurredOn;
		this.jsonEvent = jsonEvent;
		this.eventType = eventType;
		this.eventSimpleName = eventSimpleName;
		this.tag = tag;
	}

	@Override
	public String aggregateId() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getJsonEvent(){
		return jsonEvent;
	}

	public String getEventType(){
		return eventType;
	}

	public String getEventSimpleName() {
		return eventSimpleName;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public UUID id() {
		return id;
	}

	@Override
	public Date occurredOn() {
		return occurredOn;
	}

}
