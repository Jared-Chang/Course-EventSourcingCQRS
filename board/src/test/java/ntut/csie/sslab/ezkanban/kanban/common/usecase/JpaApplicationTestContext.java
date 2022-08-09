package ntut.csie.sslab.ezkanban.kanban.common.usecase;

import ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.EzKanbanWebMain;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@ComponentScan(basePackages={"ntut.csie.sslab.ezkanban.kanban",
        "ntut.csie.sslab.ezkanban.kanban.card.adapter.repository.springboot",
        "ntut.csie.sslab.ddd.adapter.gateway"}, excludeFilters= {
        @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value= EzKanbanWebMain.class)})
@EntityScan(basePackages={"ntut.csie.sslab.ezkanban.kanban"})
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public abstract class JpaApplicationTestContext {
}
