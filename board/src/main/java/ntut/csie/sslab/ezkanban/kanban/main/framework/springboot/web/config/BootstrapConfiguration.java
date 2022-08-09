package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.usecase.MessageDataMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("KanbanBootstrapConfiguration")
public class BootstrapConfiguration {

    @Bean(name="domainEventTypeMapperInBoard")
    public DomainEventTypeMapper domainEventTypeMapper() {

        DomainEventTypeMapper domainEventTypeMapper = new DomainEventTypeMapper.DomainEventTypeMapperImpl();

        BoardEvents.mapper().getMap().forEach( (key, value) -> {
            domainEventTypeMapper.put(key, value);
        });
        WorkflowEvents.mapper().getMap().forEach( (key, value) -> {
            domainEventTypeMapper.put(key, value);
        });
        CardEvents.mapper().getMap().forEach( (key, value) -> {
            domainEventTypeMapper.put(key, value);
        });

        TagEvents.mapper().getMap().forEach( (key, value) -> {
            domainEventTypeMapper.put(key, value);
        });

        MessageDataMapper.setMapper(domainEventTypeMapper);

        return domainEventTypeMapper;
    }

}
