package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import ntut.csie.sslab.ddd.adapter.eventbroker.GoogleEventBusAdapter;
import ntut.csie.sslab.ddd.adapter.repository.EsdbStoreAdapter;
import ntut.csie.sslab.ddd.adapter.repository.EzOutboxStoreAdapter;
import ntut.csie.sslab.ddd.adapter.repository.IdempotentRepositoryPeer;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.framework.EzOutboxStore;
import ntut.csie.sslab.ddd.framework.ezes.PgMessageDbClient;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.BoardOrmClient;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentStateRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentStateRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardData;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.card.adapter.out.repository.springboot.CardOrmClient;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardData;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.adapter.out.repository.springboot.TagOrmClient;
import ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot.UserInBoardRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot.UserRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.adapter.out.repository.springboot.WorkflowOrmClient;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowData;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:/application.properties")
@Configuration("KanbanRepositoryInjection")
@EnableConfigurationProperties(value=DataSourceConfig.class)
@AutoConfigureAfter({KanbanDataSourceConfiguration.class, BootstrapConfiguration.class})
public class RepositoryInjection {

  @Value("${esdb.url}")
  private String ESDB_URL;
  private DomainEventTypeMapper domainEventTypeMapper;

  private UserInBoardRepositoryPeer userInBoardRepositoryPeer;
  private DataSourceConfig dataSourceConfig;

  private BoardOrmClient boardOrmStoreClient;
  private WorkflowOrmClient workflowOrmStoreClient;
  private CardOrmClient cardOrmStoreClient;

  private TagOrmClient tagOrmStoreClient;

  private PgMessageDbClient pgMessageDbClient;

  private BoardContentReadModelRepositoryPeer boardContentReadModelRepositoryPeer;

  private IdempotentRepositoryPeer idempotentRepositoryPeer;

  @Autowired
  public RepositoryInjection(
          DataSourceConfig dataSourceConfig,
          UserInBoardRepositoryPeer userInBoardRepositoryPeer,
          @Qualifier("domainEventTypeMapperInBoard") DomainEventTypeMapper domainEventTypeMapper,
          BoardOrmClient boardOrmStoreClient,
          WorkflowOrmClient workflowOrmStoreClient,
          CardOrmClient cardOrmStoreClient,
          PgMessageDbClient pgMessageDbClient,
          TagOrmClient tagOrmStoreClient,
          BoardContentReadModelRepositoryPeer boardContentReadModelRepositoryPeer,
          IdempotentRepositoryPeer idempotentRepositoryPeer){

    this.dataSourceConfig = dataSourceConfig;
    this.userInBoardRepositoryPeer = userInBoardRepositoryPeer;
    this.domainEventTypeMapper = domainEventTypeMapper;
    this.boardOrmStoreClient = boardOrmStoreClient;
    this.workflowOrmStoreClient = workflowOrmStoreClient;
    this.cardOrmStoreClient = cardOrmStoreClient;
    this.tagOrmStoreClient = tagOrmStoreClient;
    this.pgMessageDbClient = pgMessageDbClient;

    this.boardContentReadModelRepositoryPeer = boardContentReadModelRepositoryPeer;
    this.idempotentRepositoryPeer = idempotentRepositoryPeer;
  }

  @Bean(name="boardRepository")
  public BoardRepository boardRepository() {
    if(dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
      return new BoardEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
    }
    return new BoardOutboxRepository(new EzOutboxStoreAdapter<BoardData, String>(boardPostgresOutboxStoreClient()));
  }

  @Bean(name="workflowRepository")
  public WorkflowRepository workflowRepository() {
    if(dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
      return new WorkflowEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
    }
    return new WorkflowOutboxRepository(new EzOutboxStoreAdapter<WorkflowData, String>(workflowPostgresOutboxStoreClient()));
  }

  @Bean(name="cardRepository")
  public CardRepository cardRepository() {
    if(dataSourceConfig.getDataSource().equalsIgnoreCase("ESDB")) {
      return new CardEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
    }
    return new CardOutboxRepository(new EzOutboxStoreAdapter<CardData, String>(cardPostgresOutboxStoreClient()));
  }

  @Bean(name="userRepositoryInBoard")
  public UserRepository userRepository() {
    return new UserRepositoryImpl(userInBoardRepositoryPeer);
  }

  @Bean(name="kanbanEventBus")
  public DomainEventBus eventBus() {
    return new GoogleEventBusAdapter();
  }

  @Bean(name="boardContentStateRepository")
  public BoardContentStateRepository boardContentStateRepository() {
    return new BoardContentStateRepositoryImpl(boardContentReadModelRepositoryPeer, idempotentRepositoryPeer);
  }

  @Bean(name="CardPostgresOutboxStoreClient")
  public EzOutboxStore cardPostgresOutboxStoreClient() {
    return new EzOutboxStore(cardOrmStoreClient, pgMessageDbClient);
  }

  @Bean(name="BoardPostgresOutboxStoreClient")
  public EzOutboxStore boardPostgresOutboxStoreClient() {
    return new EzOutboxStore(boardOrmStoreClient, pgMessageDbClient);
  }

  @Bean(name="WorkflowPostgresOutboxStoreClient")
  public EzOutboxStore workflowPostgresOutboxStoreClient() {
    return new EzOutboxStore(workflowOrmStoreClient, pgMessageDbClient);
  }

  @Bean(name="TagPostgresOutboxStoreClient")
  public EzOutboxStore tagPostgresOutboxStoreClient() {
    return new EzOutboxStore(tagOrmStoreClient, pgMessageDbClient);
  }
}
