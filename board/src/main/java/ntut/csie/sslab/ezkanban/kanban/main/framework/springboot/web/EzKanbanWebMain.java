package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google.NotifyBoardAdapter;
import ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config.UseCaseInjection;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.BoardOrmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"ntut.csie.sslab.ezkanban.kanban"})
@EntityScan(basePackages={"ntut.csie.sslab.ezkanban.kanban"})

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@AutoConfigureAfter({UseCaseInjection.class})
public class EzKanbanWebMain extends SpringBootServletInitializer implements CommandLineRunner {

    private DomainEventBus domainEventBus;
    private NotifyBoardAdapter notifyBoardAdapter;
    private BoardOrmClient boardOrmStoreClient;
//    private NotifyBoardSessionBroadcaster notifyBoardSessionBroadcaster;

    @Autowired
    public EzKanbanWebMain(
            NotifyBoardAdapter notifyBoardAdapter,
            BoardOrmClient boardOrmStoreClient,
            DomainEventBus domainEventBus){
        this.notifyBoardAdapter = notifyBoardAdapter;
        this.boardOrmStoreClient = boardOrmStoreClient;
//        this.notifyBoardSessionBroadcaster = notifyBoardSessionBroadcaster;
        this.domainEventBus = domainEventBus;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EzKanbanWebMain.class);
    }

    public static void main(String[] args){
        SpringApplication.run(EzKanbanWebMain.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        System.out.println("EzKanbanWebMain runs");

        boardOrmStoreClient.clearBoardSession();
        domainEventBus.register(notifyBoardAdapter);
    }
}
