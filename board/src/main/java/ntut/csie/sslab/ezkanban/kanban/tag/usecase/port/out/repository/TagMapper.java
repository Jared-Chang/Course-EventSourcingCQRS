package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxMapper;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;

import java.util.stream.Collectors;

public class TagMapper implements OutboxMapper<Tag, TagData> {
    @Override
    public Tag toDomain(TagData data) {
        Tag tag = new Tag(data.getBoardId(), data.getId(), data.getName(), data.getColor());
        tag.setVersion(data.getVersion());
        tag.clearDomainEvents();
        return tag;
    }

    @Override
    public TagData toData(Tag tag) {
        TagData data = new TagData();
        data.setId(tag.getId());
        data.setBoardId(tag.getBoardId());
        data.setName(tag.getName());
        data.setColor(tag.getColor());
        data.setDomainEventDatas(tag.getDomainEvents().stream().map(DomainEventMapper::toData).collect(Collectors.toList()));
        data.setStreamName(tag.getStreamName());
        data.setVersion(tag.getVersion());
        return data;
    }
}
