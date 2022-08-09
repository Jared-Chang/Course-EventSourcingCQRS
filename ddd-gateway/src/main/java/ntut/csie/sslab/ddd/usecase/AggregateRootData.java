package ntut.csie.sslab.ddd.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AggregateRootData {
    private String streamName;
    private long version;
    private List<DomainEventData> domainEventDatas;

    public AggregateRootData() {
        domainEventDatas = new ArrayList<>();
    }

    public AggregateRootData(String streamName, long version, List<DomainEventData> domainEventDatas) {
        this.streamName = streamName;
        this.version = version;
        this.domainEventDatas = domainEventDatas;
    }

    public List<DomainEventData> getDomainEventDatas() {
        return domainEventDatas;
    }

    public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
        this.domainEventDatas = domainEventDatas;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    @Override
    public boolean equals(Object that) {
        if(that instanceof AggregateRootData target) {
            return version == target.getVersion() &&
                   streamName.equals(target.getStreamName()) &&
                    Objects.equals(domainEventDatas, target.getDomainEventDatas());

//                   domainEventDatas.equals(target.getDomainEventDatas());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.version);
        hash = 79 * hash + Objects.hashCode(this.streamName);
        hash = 79 * hash + Objects.hashCode(this.domainEventDatas);
        return hash;
    }
}
