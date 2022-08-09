package ntut.csie.sslab.ddd.usecase;

import ntut.csie.sslab.ddd.entity.AggregateRoot;

public class AggregateMapper {


    public static AggregateRootData toData(AggregateRoot aggregateRoot) {
        AggregateRootData aggregateRootData = new AggregateRootData();
        aggregateRootData.setStreamName(aggregateRoot.getStreamName());
        aggregateRootData.setVersion(aggregateRoot.getVersion());
        aggregateRootData.setDomainEventDatas(DomainEventMapper.toData(aggregateRoot.getDomainEvents()));

        return aggregateRootData;
    }
}
