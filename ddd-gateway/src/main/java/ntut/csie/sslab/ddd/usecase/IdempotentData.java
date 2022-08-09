package ntut.csie.sslab.ddd.usecase;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="idempotent", uniqueConstraints=
@UniqueConstraint(columnNames = {"handler_id", "event_id"}))
public class IdempotentData {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="handler_id")
    private String handlerId;
    @Column(name="event_id")
    private String eventId;
    @Column(name="handled_on")
    private Date handledOn;
    public IdempotentData(){}
    public IdempotentData(String handlerId, String eventId, Date handledOn) {
        this.handlerId = handlerId;
        this.eventId = eventId;
        this.handledOn = handledOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getHandledOn() {
        return handledOn;
    }

    public void setHandledOn(Date handledOn) {
        this.handledOn = handledOn;
    }
}
