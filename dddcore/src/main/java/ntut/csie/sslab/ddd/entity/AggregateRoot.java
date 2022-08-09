package ntut.csie.sslab.ddd.entity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static ntut.csie.sslab.ddd.entity.common.Contract.ensure;
import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public abstract class AggregateRoot<ID, E extends DomainEvent> implements Entity<ID> {

    protected ID id;
    private final List<DomainEvent> domainEvents;
    protected boolean isDeleted;
    protected AtomicLong version;

    public AggregateRoot(ID id) {
        super();
        this.id = id;
        this.domainEvents = new CopyOnWriteArrayList<>();
        this.version = new AtomicLong(-1);
    }

    public AggregateRoot(List<? extends DomainEvent> events) {
        this();
        requireNotNull( "Domain events", events);
        this.version = new AtomicLong(0);
        events.forEach(x-> apply(x));
        clearDomainEvents();

        ensure("Domain event size is 0", ()-> getDomainEvents().size() == 0);
    }

    protected AggregateRoot() {
        super();
        this.domainEvents = new CopyOnWriteArrayList<>();
        this.version = new AtomicLong(-1);
    }

    @Override
    public ID getId(){
        return id;
    }
    public void addDomainEvent(DomainEvent domainEvent) {
        domainEvents.add(domainEvent);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public int getDomainEventSize(){
        return domainEvents.size();
    }

    public void clearDomainEvents(){
        domainEvents.clear();
    }

    public abstract void markAsDeleted(String userId);

    public boolean isDeleted() {
        return isDeleted;
    }

    public long getVersion() {
        return version.get();
    }

    public void setVersion(long version) {
        this.version.set(version);
    }

    public final void apply(DomainEvent event) {
        ensureInvariant();
        when(event);
        ensureInvariant();
        addDomainEvent(event);
    }
    protected void ensureInvariant(){}
    protected abstract void when(DomainEvent domainEvent);




    abstract public String getCategory();

    public final String getStreamName(){
        return getCategory() + "-" + getId().toString();
    }

    public static String getStreamName(String category, String id) {
        return category + "-" + id;
    }
}
