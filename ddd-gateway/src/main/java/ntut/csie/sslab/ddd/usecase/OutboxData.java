package ntut.csie.sslab.ddd.usecase;

import org.springframework.data.annotation.Transient;

import java.util.List;

public interface OutboxData {

    long getVersion();

    void setVersion(long version);

    @Transient
    String getId();

    @Transient
    void setId(String id);

    @Transient
    List<DomainEventData> getDomainEventDatas();

    @Transient
    void setDomainEventDatas(List<DomainEventData> domainEventDatas);

    @Transient
    String getStreamName();

    @Transient
    void setStreamName(String streamName);
}
