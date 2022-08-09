package ntut.csie.sslab.ddd.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public interface DomainEvent extends Serializable {
	UUID id();
	Date occurredOn();
	String aggregateId();

	record Snapshotted(
			String aggregateId,
			String category,
			String snapshot,
			long version,
			UUID id,
			Date occurredOn
	) implements DomainEvent {}

	class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
		public static final String MAPPING_TYPE_PREFIX = "DomainEvent$";
		public static final String SNAPSHOTTED = MAPPING_TYPE_PREFIX + "Snapshotted";

		private static final DomainEventTypeMapper mapper;

		static {
			mapper = new DomainEventTypeMapperImpl();
			mapper.put(SNAPSHOTTED, DomainEvent.Snapshotted.class);
		}
		public static DomainEventTypeMapper getInstance(){
			return mapper;
		}

	}

	static DomainEventTypeMapper mapper(){
		return DomainEvent.TypeMapper.getInstance();
	}
}