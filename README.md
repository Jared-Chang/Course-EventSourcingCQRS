# Development Environments

### Step 1: Install Java: JDK 17, with preview enabled (pattern matching for switch) 

### Step 2: Install Maven

### Step 3: Install Docker Desktop (https://www.docker.com/products/docker-desktop) or Docker

### Step 4: Install docker images

### 4a: EventStoreDB
#### docker

for MacOS
```shell
docker run --name esdb-node -it -p 2113:2113 -p 1113:1113 -d \
eventstore/eventstore:latest --insecure --run-projections=All \
--enable-external-tcp --enable-atom-pub-over-http \
--start-standard-projections
```

for Windows
```shell
docker run --name esdb-node -it -p 2113:2113 -p 1113:1113 -d eventstore/eventstore:latest --insecure --run-projections=All --enable-external-tcp --enable-atom-pub-over-http --start-standard-projections
```

#### EventStoreDB client
url: http://localhost:2113/

### 4b: Postgres for ezKanban
#### docker

```shell
docker run --name postgres_test -e POSTGRES_PASSWORD=root -p 6000:5432 -d ezkanban/postgres_message_db:1.0
```
### 4c. Postgres client pgadmin4
for MacOS
```shell
docker run -p 5050:80 -e "pgadmin_default_email=teddy.chen.tw@gmail.com" -e "PGADMIN_DEFAULT_PASSWORD=root" -d dpage/pgadmin4
```
for Windows
```shell
docker run -p 5050:80 -e PGADMIN_DEFAULT_EMAIL=teddy.chen.tw@gmail.com -e PGADMIN_DEFAULT_PASSWORD=root -d dpage/pgadmin4
```
#### pgadmin4 client
url: http://127.0.0.1:5050/login


# Event Sourcing Exercises

### Exercise 1a: CreateMyTagUseCase

```java    
    public class CreateMyTagUseCaseTest {
        
        private SimpleAsyncTaskExecutor executor;
        private GoogleEventBusAdapter eventBus;
        private MyTagRepository repository;
    
        private AllEventsListener allEventsListener;
    
        @BeforeEach
        public void setUp(){
    
            executor = new SimpleAsyncTaskExecutor();
            eventBus = new GoogleEventBusAdapter();
            repository = new InMemoryMyTagRepository(eventBus);
            allEventsListener = Mockito.mock(AllEventsListener.class);
            eventBus.register(allEventsListener);
            executor.execute(eventBus);
        }
    
        @Test
        public void create_mytag_usecase_test(){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId("tag id");
            input.setTagName("Issue");
            input.setBoardId("board id");
    
            CqrsOutput output = createMyTagUseCase.execute(input);
    
            assertNotNull(output.getId());
            assertTrue(repository.findById(output.getId()).isPresent());
            await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
        }
    }
    
    public class MyTag extends AggregateRoot<String, DomainEvent> {
    
        public final static String CATEGORY = "MyTag";
    
        private String tagId;
        private String name;
        private String boardId;
    
        public MyTag(List<DomainEvent> domainEvents) {
            super();
            domainEvents.forEach(x -> apply(x));
            this.clearDomainEvents();
        }
    
        public MyTag(String tagId, String name, String boardId) {
            super();
            apply(new MyTagEvents.TagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
        }
    
        public String getTagId() {
            return tagId;
        }
    
        public String getName() {
            return name;
        }
    
        public String getBoardId() {
            return boardId;
        }
    
        @Override
        public void markAsDeleted(String userId) {
            apply(new MyTagEvents.TagDeleted(boardId, tagId, UUID.randomUUID(), DateProvider.now()));
        }
    
        @Override
        protected void when(DomainEvent domainEvent) {
            switch (domainEvent){
                case MyTagEvents.TagCreated event -> {
                    this.id = event.tagId();
                    this.tagId = event.tagId();
                    this.name = event.name();
                    this.boardId = event.boardId();
                }
                case MyTagEvents.TagDeleted myTagDeleted -> isDeleted = true;
                default -> {}
            }
        }
    
        @Override
        public String getCategory() {
            return CATEGORY;
        }
    }
```

### Exercise 1b: DeleteMyTagUseCase

```java 
    public class DeleteMyTagUseCaseTest {
    
        private SimpleAsyncTaskExecutor executor;
        private MyTagRepository repository;
        private GoogleEventBusAdapter eventBus;
    
        private AllEventsListener allEventsListener;
    
        @BeforeEach
        public void setUp(){
            executor = new SimpleAsyncTaskExecutor();
            eventBus = new GoogleEventBusAdapter();
            repository = new InMemoryMyTagRepository(eventBus);
            allEventsListener = Mockito.mock(AllEventsListener.class);
            eventBus.register(allEventsListener);
            executor.execute(eventBus);
        }
    
        @Test
        public void delete_mytag_usecase_test(){
            String tagId = createMyTagUseCase(UUID.randomUUID().toString());
            DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
            DeleteMyTagInput input = new DeleteMyTagInput();
            input.setTagId(tagId);
    
            CqrsOutput output = deleteMyTagUseCase.execute(input);
    
            assertNotNull(output.getId());
            assertTrue(repository.findById(output.getId()).isEmpty());
            await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagDeleted.class)));
        }
    
        public String createMyTagUseCase(String boardId){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId(UUID.randomUUID().toString());
            input.setTagName("Issue");
            input.setBoardId(boardId);
    
            return createMyTagUseCase.execute(input).getId();
        }
    
    }
    
    public class InMemoryMyTagRepository implements MyTagRepository {
    
        private final List<MyTag> store = new ArrayList<>();
        private final DomainEventBus domainEventBus;
    
        public InMemoryMyTagRepository(DomainEventBus domainEventBus) {
            this.domainEventBus = domainEventBus;
        }
    
        @Override
        public Optional<MyTag> findById(String tagId) {
            var myTag = store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
            if (myTag.isPresent() && myTag.get().isDeleted()){
                    return Optional.empty();
            }
    
            return myTag;
        }
    
        @Override
        public void save(MyTag myTag) {
            store.add(myTag);
            domainEventBus.postAll(myTag);
        }
    }
    
    
    ### Exercise 1c: Refactoring, use record to implement domain events
    
    public class MyTag extends AggregateRoot<String, DomainEvent> {
    private String tagId;
    private String name;
    private String boardId;
    
        public MyTag(String tagId, String name, String boardId) {
            super();
            apply(new MyTagEvents.TagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
        }
    
        public String getTagId() {
            return tagId;
        }
    
        public String getName() {
            return name;
        }
    
        public String getBoardId() {
            return boardId;
        }
    
        @Override
        public void markAsDeleted(String userId) {
            apply(new MyTagEvents.TagDeleted(boardId, tagId, UUID.randomUUID(), DateProvider.now()));
        }
    
        @Override
        protected void when(DomainEvent domainEvent) {
            switch (domainEvent){
                case MyTagEvents.TagCreated event -> {
                    this.id = event.aggregateId();
                    this.tagId = event.tagId();
                    this.name = event.name();
                    this.boardId = event.boardId();
                }
                case MyTagEvents.TagDeleted myTagDeleted -> isDeleted = true;
                default -> {}
            }
        }
    
        @Override
        public String getCategory() {
            return "MyTag";
        }
    }
    
    
    public interface MyTagEvents extends DomainEvent {
    
        String boardId();
        String tagId();
    
        default String aggregateId(){
            return tagId();
        }
     
        ///////////////////////////////////////////////////////////////
        record TagCreated(
                String boardId,
                String tagId,
                String name,
                UUID id,
                Date occurredOn
        ) implements MyTagEvents {}
    
        record TagRenamed(
                String boardId,
                String tagId,
                String name,
                UUID id,
                Date occurredOn
        ) implements MyTagEvents {}
    
        record TagDeleted(
                String boardId,
                String tagId,
                UUID id,
                Date occurredOn
        ) implements MyTagEvents {}
    
        class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
            public static final String MAPPING_TYPE_PREFIX = "TagEvents$";
            public static final String TAG_CREATED = MAPPING_TYPE_PREFIX + "TagCreated";
            public static final String TAG_RENAMED = MAPPING_TYPE_PREFIX + "TagRenamed";
            public static final String TAG_COLOR_CHANGED = MAPPING_TYPE_PREFIX + "TagColorChanged";
            public static final String TAG_DELETED = MAPPING_TYPE_PREFIX + "TagDeleted";
            private static final DomainEventTypeMapper mapper;
            static {
                mapper = new DomainEventTypeMapperImpl();
                mapper.put(TAG_CREATED, MyTagEvents.TagCreated.class);
                mapper.put(TAG_RENAMED, MyTagEvents.TagRenamed.class);
                mapper.put(TAG_DELETED, MyTagEvents.TagDeleted.class);
            }
            public static DomainEventTypeMapper getInstance(){
                return mapper;
            }
        }
    
        static DomainEventTypeMapper mapper(){
            return MyTagEvents.TypeMapper.getInstance();
        }
    
    }
```




### Exercise 2a: Use EventStoreDB 

```java
    public class MyTagEventSourcingRepository implements MyTagRepository {
    
        private final GenericEventSourcingRepository<MyTag> eventSourcingRepository;
        private final EventStore eventStore;
    
        public MyTagEventSourcingRepository(EventStore eventStore) {
            eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, MyTag.class, MyTag.CATEGORY);
            this.eventStore = eventStore;
        }
    
    
        @Override
        public Optional<MyTag> findById(String tagId) {
            return eventSourcingRepository.findById(tagId);
    
        }
    
        @Override
        public void save(MyTag tag) {
            eventSourcingRepository.save(tag);
        }
    
        @Override
        public void delete(MyTag myTag) {
            eventSourcingRepository.delete(myTag);
        }
    }
    
    public class CreateMyTagUseCaseTest {
    
        private SimpleAsyncTaskExecutor executor;
        private GoogleEventBusAdapter eventBus;
        private MyTagRepository repository;
    
        private AllEventsListener allEventsListener;
    
        private final static String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";
    
        private EsdbListener esdbListener;
    
        @BeforeEach
        public void setUp(){
    
            DomainEventMapper.setMapper(MyTagEvents.mapper());
            executor = new SimpleAsyncTaskExecutor();
            eventBus = new GoogleEventBusAdapter();
    
            // In Memory database
    //        repository = new InMemoryMyTagRepository(eventBus);
    
            // EventStoreDb
            repository = new MyTagEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
            esdbListener = new EsdbPersistentListener(ESDB_URL, DomainEventMapper.getMapper(), eventBus);
    
            allEventsListener = Mockito.mock(AllEventsListener.class);
            eventBus.register(allEventsListener);
            executor.execute(eventBus);
            executor.execute(esdbListener);
        }
    
        @Test
        public void create_mytag_usecase_test(){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId(UUID.randomUUID().toString());
            input.setTagName("Issue");
            input.setBoardId("board id");
    
            CqrsOutput output = createMyTagUseCase.execute(input);
    
            assertNotNull(output.getId());
            assertTrue(repository.findById(output.getId()).isPresent());
            await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
        }
    }
```

### Exercise 2b: Use EzOutboxStore

#### step a:

```java
    @Entity
    @Table(name="mytag")
    public class MyTagData implements OutboxData {
        @Transient
        private String steamName;
        @Transient
        private List<DomainEventData> domainEventDatas;
        @Id
        @Column(name="id")
        private String id;
        @Column(name = "board_id", nullable = false)
        private String boardId;
        @Column(name = "tag_name")
        private String name;
        @Version
        @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
        private long version;
    
        public MyTagData(){
            this(0l);
        }
    
        public MyTagData(long version){
            this.version = version;
        }
    
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        @Override
        public long getVersion() {
            return version;
        }
    
        @Override
        public void setVersion(long version) {
            this.version = version;
        }
    
        @Override
        public String getId() {
            return id;
        }
    
        @Override
        public void setId(String id) {
            this.id = id;
        }
    
        @Override
        public List<DomainEventData> getDomainEventDatas() {
            return domainEventDatas;
        }
    
        @Override
        public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
            this. domainEventDatas = domainEventDatas;
        }
    
        @Override
        public String getStreamName() {
            return steamName;
        }
    
        @Override
        public void setStreamName(String streamName) {
            this.steamName = streamName;
        }
    
        public String getBoardId() {
            return boardId;
        }
    
        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }
    }
```

#### step b:

```java
    public interface MyTagOrmClient extends OrmClient<MyTagData, String> {
    }
```
        
#### step c:

```java
    public class MyTagOutboxRepository implements MyTagRepository {
    private final GenericOutboxRepository<MyTag, MyTagData, String> outboxRepository;
    private final OutboxStore<MyTagData, String> store;
    
        public MyTagOutboxRepository(OutboxStore<MyTagData, String> store) {
            this.outboxRepository = new GenericOutboxRepository<>(store, new MyTagMapper());
            this.store = store;
        }
    
        @Override
        public Optional<MyTag> findById(String tagId) {
            return outboxRepository.findById(tagId);
        }
    
        @Override
        public void save(MyTag myTag) {
            outboxRepository.save(myTag);
        }
    
        @Override
        public void delete(MyTag myTag) {
            outboxRepository.delete(myTag);
        }
    }

    public class MyTagMapper implements OutboxMapper<MyTag, MyTagData> {
    
        @Override
        public MyTag toDomain(MyTagData data) {
        MyTag tag = new MyTag(data.getBoardId(), data.getId(), data.getName());
        tag.setVersion(data.getVersion());
        tag.clearDomainEvents();
        return tag;
        }
    
        @Override
        public MyTagData toData(MyTag tag) {
            MyTagData data = new MyTagData();
            data.setId(tag.getId());
            data.setBoardId(tag.getBoardId());
            data.setName(tag.getName());
            data.setDomainEventDatas(tag.getDomainEvents().stream().map(DomainEventMapper::toData).collect(Collectors.toList()));
            data.setStreamName(tag.getStreamName());
            data.setVersion(tag.getVersion());
            return data;
        }
    }
```
 
#### step d: add bean in RepositoryInjection file

```java
    private MyTagOrmClient myTagOrmStoreClient;

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
    MyTagOrmClient myTagOrmStoreClient,
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
        this.myTagOrmStoreClient = myTagOrmStoreClient;
    
        this.boardContentReadModelRepositoryPeer = boardContentReadModelRepositoryPeer;
        this.idempotentRepositoryPeer = idempotentRepositoryPeer;
    }


    @Bean(name="MyTagPostgresOutboxStoreClient")
    public EzOutboxStore myTagPostgresOutboxStoreClient() {
        return new EzOutboxStore(myTagOrmStoreClient, pgMessageDbClient);
    }
```

#### step e: 

```java
        @Rollback(false)
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes= JpaApplicationTestContext.class)
        @TestPropertySource(locations = "classpath:board-test.properties")
        @AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
        @AutoConfigureAfter({UseCaseInjection.class})
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
        public class DeleteMyTagUseCaseTest {
    
        private SimpleAsyncTaskExecutor executor;
        private MyTagRepository repository;
        private GoogleEventBusAdapter eventBus;
    
        private AllEventsListener allEventsListener;
    
        @Value("${jdbc.test.url}")
        private String JDBC_TEST_URL;
    
        private PostgresDomainEventListener postgresDomainEventListener;
    
        @Autowired
        private MyTagOrmClient myTagOrmStoreClient;
    
        @Autowired
        private PgMessageDbClient pgMessageDbClient;
    
        @BeforeEach
        public void setUp(){
    
            DomainEventMapper.setMapper(MyTagEvents.mapper());
            executor = new SimpleAsyncTaskExecutor();
            eventBus = new GoogleEventBusAdapter();
    
        //        repository = new InMemoryMyTagRepository(eventBus);
            repository = new MyTagOutboxRepository(new EzOutboxStoreAdapter(
            new EzOutboxStore(myTagOrmStoreClient, pgMessageDbClient)));
            try {
            postgresDomainEventListener = new PostgresDomainEventListener(
            JDBC_TEST_URL,
            "postgres",
            "root", 20,
            DomainEventMapper.getMapper(),
            eventBus);
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    
            allEventsListener = Mockito.mock(AllEventsListener.class);
            eventBus.register(allEventsListener);
            executor.execute(eventBus);
            executor.execute(postgresDomainEventListener);
        }
    
        @Test
        public void delete_mytag_usecase_test(){
            String tagId = createMyTagUseCase(UUID.randomUUID().toString());
            DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
            DeleteMyTagInput input = new DeleteMyTagInput();
            input.setTagId(tagId);
    
            CqrsOutput output = deleteMyTagUseCase.execute(input);
    
            assertNotNull(output.getId());
            assertTrue(repository.findById(output.getId()).isEmpty());
            await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagDeleted.class)));
        }
    
        public String createMyTagUseCase(String boardId){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId(UUID.randomUUID().toString());
            input.setTagName("Issue");
            input.setBoardId(boardId);
    
            return createMyTagUseCase.execute(input).getId();
        }
    }
```

### Exercise 3: Event Type Mapping
    
    See MyTagEvents

```java
    class TypeMapper extends DomainEventTypeMapper.DomainEventTypeMapperImpl {
        public static final String MAPPING_TYPE_PREFIX = "MyTagEvents$";
        public static final String TAG_CREATED = MAPPING_TYPE_PREFIX + "TagCreated";
        public static final String TAG_RENAMED = MAPPING_TYPE_PREFIX + "TagRenamed";
        public static final String TAG_DELETED = MAPPING_TYPE_PREFIX + "TagDeleted";
        private static final DomainEventTypeMapper mapper;
        static {
            mapper = new DomainEventTypeMapperImpl();
            mapper.put(TAG_CREATED, MyTagEvents.TagCreated.class);
            mapper.put(TAG_RENAMED, MyTagEvents.TagRenamed.class);
            mapper.put(TAG_DELETED, MyTagEvents.TagDeleted.class);
        }
        public static DomainEventTypeMapper getInstance(){
            return mapper;
        }
    }

    static DomainEventTypeMapper mapper(){
        return MyTagEvents.TypeMapper.getInstance();
    }
```

### Exercise 4: Find all tags in a board

```java
    public interface MyTagRepository {
            Optional<MyTag> findById(String tagId);
            void save(MyTag myTag);
        
            void delete(MyTag myTag);
        
            List<MyTag> getMyTagsByBoardId(String boardId);
    }



    public class InMemoryMyTagRepository implements MyTagRepository {
    
        private List<MyTag> store = new ArrayList<>();
        private DomainEventBus domainEventBus;
    
        public InMemoryMyTagRepository(DomainEventBus domainEventBus) {
            this.domainEventBus = domainEventBus;
        }
    
        @Override
        public Optional<MyTag> findById(String tagId) {
            var myTag = store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
            if (myTag.isPresent() && myTag.get().isDeleted()){
                    return Optional.empty();
            }
            return myTag;
        }
    
        @Override
        public void save(MyTag myTag) {
            store.add(myTag);
            domainEventBus.postAll(myTag);
        }
    
        @Override
        public void delete(MyTag myTag) {
            if (store.removeIf(x -> x.getTagId().equals(myTag.getTagId()))){
                domainEventBus.postAll(myTag);
            }
        }
    
        @Override
        public List<MyTag> getMyTagsByBoardId(String boardId) {
            return store.stream().filter(x -> x.getBoardId().equals(boardId)).filter( x -> false == x.isDeleted()).toList();
        }
    }



    public class CreateMyTagUseCaseTest {
        private SimpleAsyncTaskExecutor executor;
    
        private GoogleEventBusAdapter eventBus;
        private MyTagRepository repository;
    
        private AllEventsListener allEventsListener;
    
        private final static String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";
    
        private EsdbListener esdbListener;
    
        @BeforeEach
        public void setUp(){
    
            DomainEventMapper.setMapper(MyTagEvents.mapper());
            executor = new SimpleAsyncTaskExecutor();
            eventBus = new GoogleEventBusAdapter();
    
            // In Memory database
            repository = new InMemoryMyTagRepository(eventBus);
    
            // EventStoreDb
    //        repository = new MyTagEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
    //        esdbListener = new EsdbPersistentListener(ESDB_URL, DomainEventMapper.getMapper(), eventBus);
    
            allEventsListener = Mockito.mock(AllEventsListener.class);
            eventBus.register(allEventsListener);
            executor.execute(eventBus);
    //        executor.execute(esdbListener);
    }
    
        @Test
        public void create_mytag_usecase_test(){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId(UUID.randomUUID().toString());
            input.setTagName("Issue");
            input.setBoardId("board id");
    
            CqrsOutput output = createMyTagUseCase.execute(input);
    
            assertNotNull(output.getId());
            assertTrue(repository.findById(output.getId()).isPresent());
            await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
        }
    
    
        @Test
        public void get_mytags_in_a_board(){
    
            String board1 = UUID.randomUUID().toString();
            createMyTagUseCase(board1);
            createMyTagUseCase(board1);
            createMyTagUseCase(board1);
    
            createMyTagUseCase(UUID.randomUUID().toString());
            createMyTagUseCase(UUID.randomUUID().toString());
    
            assertEquals(3, repository.getMyTagsByBoardId(board1).size());
        }
    
    
        public String createMyTagUseCase(String boardId){
    
            CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
            CreateMyTagInput input = new CreateMyTagInput();
            input.setTagId(UUID.randomUUID().toString());
            input.setTagName("Issue");
            input.setBoardId(boardId);
    
            return createMyTagUseCase.execute(input).getId();
        }
    
    }

    public class MyTagEventSourcingRepository implements MyTagRepository {
    
        private final GenericEventSourcingRepository<MyTag> eventSourcingRepository;
        private final EventStore eventStore;
    
        public MyTagEventSourcingRepository(EventStore eventStore) {
            eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, MyTag.class, MyTag.CATEGORY);
            this.eventStore = eventStore;
        }
    
    
        @Override
        public Optional<MyTag> findById(String tagId) {
            return eventSourcingRepository.findById(tagId);
    
        }
    
        @Override
        public void save(MyTag tag) {
            eventSourcingRepository.save(tag);
        }
    
        @Override
        public void delete(MyTag myTag) {
            eventSourcingRepository.delete(myTag);
        }
    
        @Override
        public List<MyTag> getMyTagsByBoardId(String boardId) {
    
            List<MyTagEvents.TagCreated> tagCreateds = eventStore.getCategoryEvent(MyTagEvents.TypeMapper.TAG_CREATED)
                    .stream().map(x -> (MyTagEvents.TagCreated) DomainEventMapper.toDomain(x)).
                    filter( x-> x.boardId().equals(boardId)).toList();
    
            List<MyTag> result = new ArrayList<>();
            for(var event : tagCreateds){
                Optional<MyTag> myTag = eventSourcingRepository.findById(event.tagId());
                if (myTag.isPresent()){
                    result.add(myTag.get());
                }
            }
            return result;
        }
    }


    public class MyTagOutboxRepository implements MyTagRepository {
    
    private final GenericOutboxRepository<MyTag, MyTagData, String> outboxRepository;
    private final OutboxStore<MyTagData, String> store;

    public MyTagOutboxRepository(OutboxStore<MyTagData, String> store) {
        this.outboxRepository = new GenericOutboxRepository<>(store, new MyTagMapper());
        this.store = store;
    }

    @Override
    public Optional<MyTag> findById(String tagId) {
        return outboxRepository.findById(tagId);
    }

    @Override
    public void save(MyTag myTag) {
        outboxRepository.save(myTag);
    }

    @Override
    public void delete(MyTag myTag) {
        outboxRepository.delete(myTag);
    }

    @Override
    public List<MyTag> getMyTagsByBoardId(String boardId) {
        List<MyTagEvents.TagCreated> tagCreateds = store.getCategoryEvent(MyTagEvents.TypeMapper.TAG_CREATED)
                .stream().map(x -> (MyTagEvents.TagCreated) DomainEventMapper.toDomain(x)).
                filter( x-> x.boardId().equals(boardId)).toList();

        List<MyTag> result = new ArrayList<>();
        for(var event : tagCreateds){
            Optional<MyTag> myTag = outboxRepository.findById(event.tagId());
            if (myTag.isPresent()){
                result.add(myTag.get());
            }
        }
        return result;
    }
}
```

### Exercise 5: Optimistic Locking

#### Add optimistic_locking_failure() test method

```java
    public class CreateMyTagUseCaseTest {
    
    private SimpleAsyncTaskExecutor executor;

    private GoogleEventBusAdapter eventBus;
    private MyTagRepository repository;

    private AllEventsListener allEventsListener;

    private final static String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";

    private EsdbListener esdbListener;

    @BeforeEach
    public void setUp(){

        DomainEventMapper.setMapper(MyTagEvents.mapper());
        executor = new SimpleAsyncTaskExecutor();
        eventBus = new GoogleEventBusAdapter();

        allEventsListener = Mockito.mock(AllEventsListener.class);
        eventBus.register(allEventsListener);
        executor.execute(eventBus);

        // In Memory database
        // repository = new InMemoryMyTagRepository(eventBus);

        // EventStoreDb
        repository = new MyTagEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
        esdbListener = new EsdbPersistentListener(ESDB_URL, DomainEventMapper.getMapper(), eventBus);
        executor.execute(esdbListener);

    }

    @AfterEach
    public void teardown(){
        if (null != esdbListener){
            esdbListener.shutdown();
        }
    }

    @Test
    public void create_mytag_usecase_test(){

        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setTagName("Issue");
        input.setBoardId("board id");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
    }


    @Test
    public void get_mytags_in_a_board(){

        String board1 = UUID.randomUUID().toString();
        createMyTagUseCase(board1);
        createMyTagUseCase(board1);
        createMyTagUseCase(board1);

        createMyTagUseCase(UUID.randomUUID().toString());
        createMyTagUseCase(UUID.randomUUID().toString());

        assertEquals(3, repository.getMyTagsByBoardId(board1).size());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(5)).when(isA(MyTagEvents.TagCreated.class)));
    }

    @Test
    public void optimistic_locking_failure(){
        String myTag1Id = createMyTagUseCase(UUID.randomUUID().toString());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
        MyTag myTagtV1 = repository.findById(myTag1Id).get();
        MyTag myTagtV2 = repository.findById(myTag1Id).get();
        myTagtV1.rename("story");
        repository.save(myTagtV1);
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagRenamed.class)));

        try{
            repository.save(myTagtV2);
            fail("Infeasible path");
        }
        catch (RepositorySaveException e){
            assertEquals("Optimistic locking failure", e.getMessage());
        }
    }


    public String createMyTagUseCase(String boardId){

        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setTagName("Issue");
        input.setBoardId(boardId);

        return createMyTagUseCase.execute(input).getId();
    }

}
```

#### Implement rename(String newName) in MyTag

```java
    public void rename(String newName){
        if (name.equals(newName))
            return;

        apply(new MyTagEvents.TagRenamed(boardId, tagId, newName, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    protected void when(DomainEvent domainEvent) {
        switch (domainEvent){
            case MyTagEvents.TagCreated event -> {
                this.id = event.tagId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.boardId = event.boardId();
            }
            case MyTagEvents.TagDeleted event -> isDeleted = true;
            case MyTagEvents.TagRenamed event -> this.name = event.name();
            default -> {}
        }
    }
```

#### Implement concrete repository

```java
public class InMemoryMyTagRepository implements MyTagRepository {

    private List<MyTag> store = new ArrayList<>();
    private DomainEventBus domainEventBus;

    public InMemoryMyTagRepository(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    @Override
    public Optional<MyTag> findById(String tagId) {
        var myTag = store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
        if (myTag.isEmpty() || (myTag.isPresent() && myTag.get().isDeleted())){
                return Optional.empty();
        }

        var found = new MyTag(
                myTag.get().getBoardId(),
                myTag.get().getId(),
                myTag.get().getName());

        found.setVersion(myTag.get().getVersion());
        return Optional.of(found);
    }


    @Override
    public void save(MyTag myTag) {
        var old = store.stream().filter(x -> x.getId().equals(myTag.getId())).findAny();
        if (old.isPresent() && old.get().getVersion() != myTag.getVersion()) {
            throw new RepositorySaveException(RepositorySaveException.OPTIMISTIC_LOCKING_FAILURE);
        }
        if (old.isPresent()) {
            store.removeIf(x -> x.getId().equals(myTag.getId()));
        }

        myTag.setVersion(myTag.getVersion() + 1);
        store.add(myTag);
        domainEventBus.postAll(myTag);
    }


    @Override
    public void delete(MyTag myTag) {
        if (store.removeIf(x -> x.getTagId().equals(myTag.getTagId()))){
            domainEventBus.postAll(myTag);
        }
    }

    @Override
    public List<MyTag> getMyTagsByBoardId(String boardId) {
        return store.stream().filter(x -> x.getBoardId().equals(boardId)).filter( x -> false == x.isDeleted()).toList();
    }
}
```

```java
    @Rollback(false)
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes= JpaApplicationTestContext.class)
    @TestPropertySource(locations = "classpath:board-test.properties")
    @AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
    @AutoConfigureAfter({UseCaseInjection.class})
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    public class DeleteMyTagUseCaseTest {

    private SimpleAsyncTaskExecutor executor;
    private MyTagRepository repository;
    private GoogleEventBusAdapter eventBus;

    private AllEventsListener allEventsListener;

    @Value("${jdbc.test.url}")
    private String JDBC_TEST_URL;

    private PostgresDomainEventListener postgresDomainEventListener;

    @Autowired
    private MyTagOrmClient myTagOrmStoreClient;

    @Autowired
    private PgMessageDbClient pgMessageDbClient;

    @BeforeEach
    public void setUp(){

        DomainEventMapper.setMapper(MyTagEvents.mapper());
        executor = new SimpleAsyncTaskExecutor();
        eventBus = new GoogleEventBusAdapter();

    //        repository = new InMemoryMyTagRepository(eventBus);
        repository = new MyTagOutboxRepository(new EzOutboxStoreAdapter(
        new EzOutboxStore(myTagOrmStoreClient, pgMessageDbClient)));
        try {
        postgresDomainEventListener = new PostgresDomainEventListener(
        JDBC_TEST_URL,
        "postgres",
        "root", 20,
        DomainEventMapper.getMapper(),
        eventBus);
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }

        allEventsListener = Mockito.mock(AllEventsListener.class);
        eventBus.register(allEventsListener);
        executor.execute(eventBus);
        executor.execute(postgresDomainEventListener);
    }

    @AfterEach
    public void teardown(){
        if (null != postgresDomainEventListener){
            postgresDomainEventListener.shutdown();
        }
    }

    @Test
    public void delete_mytag_usecase_test(){
        String tagId = createMyTagUseCase(UUID.randomUUID().toString());
        DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
        DeleteMyTagInput input = new DeleteMyTagInput();
        input.setTagId(tagId);

        CqrsOutput output = deleteMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isEmpty());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagDeleted.class)));
    }


    @Test
    public void get_mytags_in_a_board(){

        String board1 = UUID.randomUUID().toString();
        createMyTagUseCase(board1);
        createMyTagUseCase(board1);
        createMyTagUseCase(board1);

        createMyTagUseCase(UUID.randomUUID().toString());
        createMyTagUseCase(UUID.randomUUID().toString());

        assertEquals(3, repository.getMyTagsByBoardId(board1).size());
    }


    @Test
    public void optimistic_locking_failure(){
        String myTag1Id = createMyTagUseCase(UUID.randomUUID().toString());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagCreated.class)));
        MyTag myTagtV1 = repository.findById(myTag1Id).get();
        MyTag myTagtV2 = repository.findById(myTag1Id).get();
        myTagtV1.rename("story");
        repository.save(myTagtV1);
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(MyTagEvents.TagRenamed.class)));

        try{
            repository.save(myTagtV2);
            fail("Infeasible path");
        }
        catch (RepositorySaveException e){
            assertEquals("Optimistic locking failure", e.getMessage());
        }
    }


    public String createMyTagUseCase(String boardId){

        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagSeervice(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setTagName("Issue");
        input.setBoardId(boardId);

        return createMyTagUseCase.execute(input).getId();
    }

}
```


## Exercise 6: Snapshot

```java
    public class CreateMyTagUseCaseTest {
        @Test
        public void read_from_snapshot() {
    
    //        EsdbStoreAdapter esdbStoreAdapter = new EsdbStoreAdapter(ESDB_URL);
            MyTagRepository myTagRepository = new SnapshottedMyTagEventSourcingRepository(
            (MyTagEventSourcingRepository) this.repository, esdbStoreAdapter);
    
            String tagId = "5000";
            StopWatch sw = new StopWatch(UUID.randomUUID().toString());
    
            int loop = 5000;
            createMyTagAndGenerateNTagRenamedEventsIfItDoesNotExist(myTagRepository, tagId, loop);
    
    //        sw.start();
    //        var myDirectTag = this.repository.findById(tagId).get();
    //        sw.stop();
    //        long directStreamTotalTimeMillis  = sw.getTotalTimeMillis();
    //        System.out.println("Read form stream for " + loop + " events: " + directStreamTotalTimeMillis + " ms");
    
            sw.start();
            var mySnapshotTag = myTagRepository.findById(tagId).get();
            sw.stop();
            long snapshotTotalTimeMillis  = sw.getTotalTimeMillis();
            System.out.println("Read form snapshot for " + loop + " events: " + snapshotTotalTimeMillis + " ms");
    
    //        assertTrue(directStreamTotalTimeMillis > snapshotTotalTimeMillis);
    //        assertEquals(mySnapshotTag.getBoardId(), myDirectTag.getBoardId());
    //        assertEquals(mySnapshotTag.getName(), myDirectTag.getName());
    }
    
        private void createMyTagAndGenerateNTagRenamedEventsIfItDoesNotExist(MyTagRepository repository, String tagId, int loop){
            if (repository.findById(tagId).isPresent()){
                return;
            }
    
            MyTag myTag = new MyTag(UUID.randomUUID().toString(), tagId, "issue");
            for (int i = 0; i < loop; i++){
                myTag.rename("name_" + UUID.randomUUID().toString());
                repository.save(myTag);
            }
        }
    }

    public class SnapshottedMyTagEventSourcingRepository implements MyTagRepository {
    
        private final EventStore eventStore;
        private final MyTagEventSourcingRepository myTagEventSourcingRepository;
    
        private int snapshotIncrement = 1000;
    
        public SnapshottedMyTagEventSourcingRepository(MyTagEventSourcingRepository myTagEventSourcingRepository, EventStore eventStore) {
            requireNotNull("MyTagEventSourcingRepository", myTagEventSourcingRepository);
            requireNotNull("EventSourcingStore", eventStore);
    
            this.myTagEventSourcingRepository = myTagEventSourcingRepository;
            this.eventStore = eventStore;
        }
    
        @Override
        public Optional<MyTag> findById(String tagId) {
    
            Optional<DomainEventData> domainEventData =
                    eventStore.getLastEventFromStream(getSnapshottedStreamName(tagId));
    
            if (domainEventData.isEmpty()){
                return myTagEventSourcingRepository.findById(tagId);
            }
    
            DomainEvent.Snapshotted snapshotted = DomainEventMapper.toDomain(domainEventData.get());
            MyTag tag = MyTag.fromSnapshot(Json.readAs(snapshotted.snapshot().getBytes(), MyTag.MyTagSnapshot.class));
            var events = DomainEventMapper.toDomain(
                    eventStore.getEventFromStream(tag.getStreamName(), tag.getVersion()+1));
            events.forEach( x -> tag.apply(x));
            return Optional.of(tag);
        }
    
    
        @Override
        public void save(MyTag tag) {
    
            myTagEventSourcingRepository.save(tag);
    
            Optional<DomainEventData> snapshotEventData =
                    eventStore.getLastEventFromStream(getSnapshottedStreamName(tag.getId()));
            if (snapshotEventData.isPresent()){
                DomainEvent.Snapshotted snapshotted = DomainEventMapper.toDomain(snapshotEventData.get());
                if (tag.getVersion() - snapshotted.version() >= snapshotIncrement){
                    saveSnapshot(tag);
                }
            }
            else if (tag.getVersion() >= snapshotIncrement){
                saveSnapshot(tag);
            }
        }
    
        private void saveSnapshot (MyTag tag){
            MyTag.MyTagSnapshot snapshot = tag.getSnapshot();
            AggregateRootData data = new AggregateRootData();
            data.setVersion(-1);
            data.setStreamName(getSnapshottedStreamName(tag.getId()));
            var snapshotted = new DomainEvent.Snapshotted(tag.getId(), tag.getCategory(), Json.asString(snapshot), tag.getVersion(), UUID.randomUUID(), DateProvider.now());
            data.setDomainEventDatas(Arrays.asList(DomainEventMapper.toData(snapshotted)));
            eventStore.save(data);
            return;
        }
    
        private String getSnapshottedStreamName(String tagId){
            return "Snapshot-MyTag-" + tagId;
        }
    
        @Override
        public void delete(MyTag tag) {
            myTagEventSourcingRepository.delete(tag);
        }
    
        @Override
        public List<MyTag> getMyTagsByBoardId(String boardId){
            return myTagEventSourcingRepository.getMyTagsByBoardId(boardId);
        }
    }
    
    
    public class MyTag extends AggregateRoot<String, DomainEvent> implements AggregateSnapshot<MyTag.MyTagSnapshot> {
    
        public final static String CATEGORY = "MyTag";
    
        private String boardId;
        private String tagId;
        private String name;
    
        public record MyTagSnapshot(String boardId, String tagId, String name, AtomicLong version){}
    
        @Override
        public MyTagSnapshot getSnapshot() {
            return new MyTag.MyTagSnapshot(boardId, tagId, name, version);
        }
    
        @Override
        public void setSnapshot(MyTagSnapshot snapshot) {
            this.boardId = snapshot.boardId;
            this.id = snapshot.tagId;
            this.name = snapshot.name;
            this.version = snapshot.version;
        }
    
        public static MyTag fromSnapshot(MyTagSnapshot snapshot){
            MyTag tag = new MyTag();
            tag.setSnapshot(snapshot);
            return tag;
        }
    
        private MyTag(){}
    }
```


## Exercise 7: $all stream (master stream)

See EzesPersistentConsumerTest


# CQRS

### Exercise 8: Use NotifyBoardContent & PostgreSQL (Projection)

See GetBoardContentUseCaseTest

#### example 1: NotifyBoardContent projects BoardContentViewModel in board_content table of the Postgres database 

```java
    @Test
    public void get_board_content_with_a_valid_board_id() {
        createThreeUsersInBoardBoundedContext();
        createBoardUseCase(teamId, boardId, boardName, userId);
        inviteThreeUsers();
        createOneWorkflowAndThreeStages();
        createFourCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(4)).when(isA(CardEvents.CardCreated.class)));
        moveTwoCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(2)).when(isA(CardEvents.CardMoved.class)));

        DateProvider.setDate(DateProvider.parse("2021-04-07 00:00:00"));
        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);

        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(boardId.id(), boardContentViewModel.getBoardId());
        assertEquals(boardName, boardContentViewModel.getBoardName());

        assertEquals(1, boardContentViewModel.getWorkflows().size());
        assertEquals(3 , boardContentViewModel.getBoardMembers().size());
        WorkflowDto firstWorkflowDto = boardContentViewModel.getWorkflows().get(0);
        assertEquals(firstWorkflowId, firstWorkflowDto.getWorkflowId());
        assertEquals(1, firstWorkflowDto.getLanes().size());
        LaneDto firstStageDto = firstWorkflowDto.getLanes().get(0);
        assertEquals(2, firstStageDto.getLanes().size());
        LaneDto substageDto = firstStageDto.getLanes().get(0);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage1Id, substageDto.getLaneId());
        substageDto = firstStageDto.getLanes().get(1);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage2Id, substageDto.getLaneId());
        Map<String, List<CardDto>> committedCards = boardContentViewModel.getCommittedCards();

        List<CardDto> cardDtosInFirstSubStage = committedCards.get(substage1Id);
        assertEquals(2, cardDtosInFirstSubStage.size());
        assertEquals(secondCardId, cardDtosInFirstSubStage.get(0).getCardId());
        assertEquals(fourthCardId, cardDtosInFirstSubStage.get(1).getCardId());

        List<CardDto> cardDtosInSecondSubStage = committedCards.get(substage2Id);
        assertEquals(2, cardDtosInSecondSubStage.size());
        assertEquals(firstCardId, cardDtosInSecondSubStage.get(0).getCardId());
        assertEquals(thirdCardId, cardDtosInSecondSubStage.get(1).getCardId());

        BoardMemberDto userDto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(userId)).findFirst().get();
        BoardMemberDto assignee1Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee1Id)).findFirst().get();
        BoardMemberDto assignee2Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee2Id)).findFirst().get();
        assertEquals(email, userDto.getEmail());
        assertEquals(nickname, userDto.getNickname());
        assertEquals(assignee1Email, assignee1Dto.getEmail());
        assertEquals(assignee1Nickname, assignee1Dto.getNickname());
        assertEquals(assignee2Email, assignee2Dto.getEmail());
        assertEquals(assignee2Nickname, assignee2Dto.getNickname());
    }
```

Output:
```json lines
{"boardState":{"boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"teamId":"2b3dc6fa-68bc-431e-bfde-3ccdb1787de0","name":"Task Board","boardMembers":[{"boardRole":"Admin","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"userId":"62545803-9334-4739-9100-a0e1a669f015"},{"boardRole":"Admin","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"userId":"assignee1Id"},{"boardRole":"Member","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"userId":"assignee2Id"}]},"workflowStates":[{"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"name":"firstWorkflow","rootStages":[{"id":{"id":"38b0b736-34bb-41a9-b092-698a002175ed"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"parentId":{"id":"-1"},"name":"firstStage","wipLimit":{"value":-1},"order":0,"type":"Standard","children":[{"id":{"id":"0bb647ba-9ac6-4c7a-bc2b-06511fb9162a"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"parentId":{"id":"38b0b736-34bb-41a9-b092-698a002175ed"},"name":"substage1","wipLimit":{"value":-1},"order":0,"type":"Standard","children":[],"layout":"Vertical"},{"id":{"id":"665e114f-8c76-4082-943e-35df1a725858"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"parentId":{"id":"38b0b736-34bb-41a9-b092-698a002175ed"},"name":"substage2","wipLimit":{"value":-1},"order":1,"type":"Standard","children":[],"layout":"Vertical"}],"layout":"Vertical"}],"version":0}],"committedCardStates":{"0bb647ba-9ac6-4c7a-bc2b-06511fb9162a":[{"cardId":{"id":"08391c18-399b-481f-b996-da1cb9b5ae70"},"userId":"62545803-9334-4739-9100-a0e1a669f015","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"laneId":{"id":"0bb647ba-9ac6-4c7a-bc2b-06511fb9162a"},"description":"secondCard","estimate":null,"note":null,"deadline":null,"version":0},{"cardId":{"id":"ae679bce-2a28-4a43-8955-03d931b8e16d"},"userId":"62545803-9334-4739-9100-a0e1a669f015","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"laneId":{"id":"0bb647ba-9ac6-4c7a-bc2b-06511fb9162a"},"description":"fourthCard","estimate":null,"note":null,"deadline":null,"version":0}],"665e114f-8c76-4082-943e-35df1a725858":[{"cardId":{"id":"a99f6cbb-cf6d-45e1-91b7-08ece49236dd"},"userId":"62545803-9334-4739-9100-a0e1a669f015","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"laneId":{"id":"665e114f-8c76-4082-943e-35df1a725858"},"description":"firstCard","estimate":null,"note":null,"deadline":null,"version":1},{"cardId":{"id":"6465dee1-fe10-4202-b9b2-c3d6f7063210"},"userId":"62545803-9334-4739-9100-a0e1a669f015","boardId":{"id":"342b6f4e-73fd-4ef6-9b31-a57b8788fe0f"},"workflowId":{"id":"ba2812ac-1098-4536-9782-4c97c75a889c"},"laneId":{"id":"665e114f-8c76-4082-943e-35df1a725858"},"description":"thirdCard","estimate":null,"note":null,"deadline":null,"version":1}]},"boardVersion":0,"idempotentData":{"id":null,"handlerId":"f971cee5-2d4c-46fd-ae78-e74c05fa43ba","eventId":"a2136965-3363-4070-b7c9-a0c497d3d7c3","handledOn":"2021-04-04T16:00:00.000+00:00"}}
```

#### Example 2: idempotent projector

```java
    @Test
    public void get_board_content_is_idempotent_when_a_board_created_and_a_workflow_created_are_sent_twice() {

        DuplicatedEventsPublisher publisher = new DuplicatedEventsPublisher(domainEventBus);
        var myExecutor = new SimpleAsyncTaskExecutor();
        myExecutor.execute(publisher);
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(2)).when(isA(WorkflowEvents.WorkflowCreated.class)));

        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);
        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(1, boardContentViewModel.getWorkflows().size());

    }
```


### Exercise 9: Use Esdb Projection with JavaScript (Projection)
See board\src\main\resources\esdb

#### Example 1: GetBoardContent-by-BoardId.js

#### Step 1: Create a continuous project in EventStoreDB (Emit enabled) 
```javascript
fromStreams(["$ce-Card", "$ce-Tag", "$ce-Workflow", "$ce-Board"])
.when({
    $init: function(){
        return {
        }
    },
    $any: function(s,e) {
        linkTo("GetBoardContentUseCase-by-Board-" + e.body["boardId"]["id"], e);
    },
});
```

#### Step 2: Run GetBoardContentFromEsdbProjectionUseCaseTest

```java
  @BeforeEach
    public void setUp() {
        super.setUp();

        // Use InMemoryRepositoryPeer
        boardContentReadModelRepositoryPeer = new InMemoryBoardContentReadModelRepositoryPeer();
        idempotentRepositoryPeer = new InMemoryIdempotentRepositoryPeer();
        userRepository = new InMemoryUserRepository();
        boardContentStateRepository = new BoardContentStateRepositoryImpl(boardContentReadModelRepositoryPeer, idempotentRepositoryPeer);
        boardContentReadModelRepository = new BoardContentReadModelRepositoryImpl(boardContentReadModelRepositoryPeer, userRepository);

        notifyBoardContent = new NotifyBoardContentService(UUID.randomUUID().toString(), boardContentStateRepository);

        /*
            Do not need NotifyBoardContentAdapter to project read model
            notifyBoardContentAdapter = new NotifyBoardContentAdapter(new NotifyBoardContentService(UUID.randomUUID().toString(), boardContentStateRepository));
            executor.execute(notifyBoardContentAdapter);
            domainEventBus.register(notifyBoardContentAdapter);
         */
    }

    @Test
    public void get_board_content_with_a_valid_board_id() {
        createThreeUsersInBoardBoundedContext();
        createBoardUseCase(teamId, boardId, boardName, userId);
        inviteThreeUsers();
        createOneWorkflowAndThreeStages();
        createFourCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(4)).when(isA(CardEvents.CardCreated.class)));
        moveTwoCards();
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(2)).when(isA(CardEvents.CardMoved.class)));

        // Project board content read model at runtime from projection in EventStoreDB
        var events = eventStore.getEventFromStream("GetBoardContentUseCase-by-Board-" + boardId.id(), 0);
        events.forEach(x -> notifyBoardContent.project(DomainEventMapper.toDomain(x)));

        DateProvider.setDate(DateProvider.parse("2021-04-07 00:00:00"));
        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);

        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(boardId.id(), boardContentViewModel.getBoardId());
        assertEquals(boardName, boardContentViewModel.getBoardName());

        assertEquals(1, boardContentViewModel.getWorkflows().size());
        assertEquals(3 , boardContentViewModel.getBoardMembers().size());
        WorkflowDto firstWorkflowDto = boardContentViewModel.getWorkflows().get(0);
        assertEquals(firstWorkflowId, firstWorkflowDto.getWorkflowId());
        assertEquals(1, firstWorkflowDto.getLanes().size());
        LaneDto firstStageDto = firstWorkflowDto.getLanes().get(0);
        assertEquals(2, firstStageDto.getLanes().size());
        LaneDto substageDto = firstStageDto.getLanes().get(0);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage1Id, substageDto.getLaneId());
        substageDto = firstStageDto.getLanes().get(1);
        assertEquals(firstStageId, firstStageDto.getLaneId());
        assertEquals(substage2Id, substageDto.getLaneId());
        Map<String, List<CardDto>> committedCards = boardContentViewModel.getCommittedCards();

        List<CardDto> cardDtosInFirstSubStage = committedCards.get(substage1Id);
        assertEquals(2, cardDtosInFirstSubStage.size());
        assertEquals(secondCardId, cardDtosInFirstSubStage.get(0).getCardId());
        assertEquals(fourthCardId, cardDtosInFirstSubStage.get(1).getCardId());

        List<CardDto> cardDtosInSecondSubStage = committedCards.get(substage2Id);
        assertEquals(2, cardDtosInSecondSubStage.size());
        assertEquals(firstCardId, cardDtosInSecondSubStage.get(0).getCardId());
        assertEquals(thirdCardId, cardDtosInSecondSubStage.get(1).getCardId());

        BoardMemberDto userDto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(userId)).findFirst().get();
        BoardMemberDto assignee1Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee1Id)).findFirst().get();
        BoardMemberDto assignee2Dto = boardContentViewModel.getBoardMembers().stream().filter(x-> x.getUserId().equals(assignee2Id)).findFirst().get();
        assertEquals(email, userDto.getEmail());
        assertEquals(nickname, userDto.getNickname());
        assertEquals(assignee1Email, assignee1Dto.getEmail());
        assertEquals(assignee1Nickname, assignee1Dto.getNickname());
        assertEquals(assignee2Email, assignee2Dto.getEmail());
        assertEquals(assignee2Nickname, assignee2Dto.getNickname());
    }
```

