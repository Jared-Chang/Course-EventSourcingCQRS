package ntut.csie.sslab.ddd.usecase;


import ntut.csie.sslab.ddd.adapter.eventbroker.EventSerializer;
import ntut.csie.sslab.ddd.entity.DomainEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="messages")
public class MessageData {

	@Id
	private UUID id;

	@Column(name = "data")
	private String eventBody;

	@Column(name = "type")
	private String eventType;

	@Column(name = "stream_name")
	private String streamName;

	@Column(name = "time")
	private Date writingTime;

	@Column(name = "metadata")
	private String metadata;

	@Column(name = "global_position")
	private long globalPosition;

	@Column(name = "position")
	private long position;

  	public MessageData(){}

	public MessageData(UUID id,
					   String eventBody,
					   String eventType,
					   String streamName,
					   Date writingTime,
					   String metadata) {
		this.id = id;
		this.eventBody = eventBody;
		this.eventType = eventType;
		this.streamName = streamName;
		this.writingTime = writingTime;
		this.metadata = metadata;
	}

	public UUID getId() {
		return id;
	}

	public String getEventBody() {
		return eventBody;
	}

	public String getEventType() {
		return eventType;
	}

	public String getStreamName() {
		return streamName;
	}

	public Date getWritingTime() {
		return writingTime;
	}

	public String getMetadata() {
		return metadata;
	}

	public long getGlobalPosition() {
		return globalPosition;
	}

	public long getPosition() {
		return position;
	}

	public String getAggregateId() {
		return streamName.split("-", 2)[1];
	}

	@SuppressWarnings("unchecked")
	public <T extends DomainEvent> T toDomainEvent() {
        Class<T> domainEventClass = null;

        try {
            domainEventClass = (Class<T>) Class.forName(this.getEventType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        T domainEvent =
            EventSerializer
                .instance()
                .deserialize(this.getEventBody(), domainEventClass);

        return domainEvent;
    }

	@Override
	public boolean equals(Object that) {
		if(that instanceof MessageData target) {
			return  this.id.equals(target.getId())&&
					this.eventBody.equals(target.getEventBody()) &&
					this.eventType.equals(target.getEventType()) &&
					this.metadata.equals(target.getMetadata());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.id);
		hash = 79 * hash + Objects.hashCode(this.eventBody);
		hash = 79 * hash + Objects.hashCode(this.eventType);
		hash = 79 * hash + Objects.hashCode(this.metadata);
		return hash;
	}
}
