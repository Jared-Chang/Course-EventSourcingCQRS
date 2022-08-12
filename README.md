
# Copyright 2022 by Teddysoft. All rights reserved

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
or start all required service by docker compose

```shell
docker compose up -d
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
or start all required service by docker compose

```shell
docker compose up -d
```

### 4c. Postgres client pgadmin4
for MacOS
```shell
docker run -p 5050:80 -e "PGADMIN_DEFAULT_EMAIL=teddy.chen.tw@gmail.com" -e "PGADMIN_DEFAULT_PASSWORD=root" -d dpage/pgadmin4
```
or start all required service by docker compose

```shell
docker compose up -d
```

for Windows
```shell
docker run -p 5050:80 -e PGADMIN_DEFAULT_EMAIL=teddy.chen.tw@gmail.com -e PGADMIN_DEFAULT_PASSWORD=root -d dpage/pgadmin4
```
#### pgadmin4 client
url: http://127.0.0.1:5050/login


# Event Sourcing Exercises

### Create a branch for this course
```script
git branch 2022-08-10
git checkout 2022-08-10

```

### Exercise 1a: CreateMyTagUseCase

#### Iteration 1, step 1: Write first failed test method
```java
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class CreateMyTagUseCaseTest {
    @Test
    public void create_mytag_usecase() {

        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagUseCase(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CreateMyTagOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
    }
}
```

#### Iteration 1, sep 2: Replace CreateMyTagOutput with CqrsOutput
```java
public class CreateMyTagUseCaseTest {
    
    @Test
    public void create_mytag_usecase() {

        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagUseCase(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
    }
}
```

#### Iteration 1, step 3: Implement production code, CreateMyTagUseCase and CreateMyTagInput
```java

public class CreateMyTagUseCase {
    public CqrsOutput execute(CreateMyTagInput input) {
        return CqrsOutput.create();
    }
}

public class CreateMyTagInput {
    private String boardId;
    private String tagId;
    private String name;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
#### Iteration 1, step 4: Run the test case and it fails

#### Iteration 1, step 5: Implement CreateMyTagUseCase to pass the test

```java
public class CreateMyTagUseCase {

    public CqrsOutput execute(CreateMyTagInput input) {
        MyTag myTag = new MyTag(input.getBoardId(), input.getTagId(), input.getName());
        return CqrsOutput.create().setId(myTag.getTagId());
    }
}
```

***

#### Iteration 2, step 1: Add MyTagRepository to the test method
```java
public class CreateMyTagUseCaseTest {

    @Test
    public void create_mytag_usecase(){

        MyTagRepository repository = new MyTagRepository();
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagUseCase(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
    }
}
```

#### Iteration 2, step 2: Implement MyTagRepository
```java
public class MyTagRepository {
    private final List<MyTag> store = new ArrayList<>();

    public Optional<MyTag> findById(String tagId) {
        return store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
    }

    public void save(MyTag myTag) {
        store.add(myTag);
    }
}
```

#### Iteration 2, step 3: Implement CreateMyTagUseCase
```java
public class CreateMyTagUseCase {
    private final MyTagRepository repository;
    public CreateMyTagUseCase(MyTagRepository repository) {
        this.repository = repository;
    }

    public CqrsOutput execute(CreateMyTagInput input) {
        MyTag myTag = new MyTag(input.getBoardId(), input.getTagId(), input.getName());

        repository.save(myTag);
        return CqrsOutput.create().setId(myTag.getTagId());
    }
}
```
#### Iteration 2, step 4: Run the test method; it passes

***

#### Iteration 3, Refactoring CreateMyTagUseCase and MyTagRepository

#### Iteration 3a, step 1: Refactoring, extract CreateMyTagUseCase interface
```java
public interface CreateMyTagUseCase {
    CqrsOutput execute(CreateMyTagInput input);
}

public class CreateMyTagService implements CreateMyTagUseCase {
    private final MyTagRepository repository;
    public CreateMyTagService(MyTagRepository repository) {
        this.repository = repository;
    }

    @Override
    public CqrsOutput execute(CreateMyTagInput input) {
        MyTag myTag = new MyTag(input.getBoardId(), input.getTagId(), input.getName());

        repository.save(myTag);
        return CqrsOutput.create().setId(myTag.getTagId());
    }
}
```

#### Iteration 3a, step 2: Refactoring, revise test method 
```java
public class CreateMyTagUseCaseTest {
    @Test
    public void create_mytag_usecase() {
        MyTagRepository repository = new MyTagRepository();
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
    }
}
```

#### Iteration 3a, step 3: Refactoring, crete port.in.create and service packages 
* Move CreateMyTagInput and CreateMyTagUseCase to port.in.create package
* Move CreateMyTagService to service package

#### Iteration 3a, step 4: Run the test; it passes

***

#### Iteration 3b, step 1: Refactoring, extract MyTagRepository interface
```java
public interface MyTagRepository {
    Optional<MyTag> findById(String tagId);
    void save(MyTag myTag);
}

public class InMemoryMyTagRepository implements MyTagRepository {
    private final List<MyTag> store = new ArrayList<>();

    @Override
    public Optional<MyTag> findById(String tagId) {
        return store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
    }

    @Override
    public void save(MyTag myTag) {
        store.add(myTag);
    }
}
```

#### Iteration 3b, step 2: Refactoring, revise test method
```java
public class CreateMyTagUseCaseTest {
    @Test
    public void create_mytag_usecase(){
        MyTagRepository repository = new InMemoryMyTagRepository();
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
    }
}
```

#### Iteration 3b, step 3: Refactoring, crete mytag.usecase.port.out package
* Move MyTagRepository to mytag.usecase.port.out package

#### Iteration 3b, step 4: Refactoring, crete mytag.adapter.out.repository package
* Move MyTagRepository to mytag.adapter.out.repository package

#### Iteration 3b, step 5: Run the test; it passes

***

### Iteration 4, Generate MyTagCreated Domain Event 
#### Iteration 4, step 1: Write a new MyTagTest unit test; it cannot be compiled
```java
public class MyTagTest {
    @Test
    public void create_mytag_generates_a_mytag_created_event(){
        MyTag myTag = new MyTag("board id", "tad id", "issue");
        
        assertEquals(1, myTag.getDomainEvents().size());
    }
}
```

#### Iteration 4, step 2: Let MyTag extends AggregateRoot
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    private String boardId;
    private String tagId;
    private String name;
    
    public MyTag(String boardId, String tagId, String name) {
        super(tagId);
        this.boardId = boardId;
        this.tagId = tagId;
        this.name = name;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    @Override
    public void markAsDeleted(String userId) {
        
    }

    @Override
    protected void when(DomainEvent domainEvent) {

    }

    @Override
    public String getCategory() {
        return null;
    }
}
```

#### Iteration 4, step 3: Run the test; it fails

#### Iteration 4, step 5: Create MyTagCreated domain event
```java
public record MyTagCreated (
        String boardId,
        String tagId,
        String name,
        UUID id,
        Date occurredOn
) implements DomainEvent {
    
    @Override
    public String aggregateId() {
        return tagId;
    }
}
```

#### Iteration 4, step 6: Generate a new MyTagCreated in MyTag constructor
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    public MyTag(String boardId, String tagId, String name) {
        super(tagId);
        this.boardId = boardId;
        this.tagId = tagId;
        this.name = name;

        addDomainEvent(new MyTagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
    }
}
```
#### Iteration 4, step 7: Run the test; it passes

#### Iteration 4, step 8: Revise the test to verify MyTagCreated type; it passes
```java
public class MyTagTest {
    @Test
    public void create_mytag_generates_a_mytag_created_event(){
        MyTag myTag = new MyTag("board id", "tad id", "issue");

        assertEquals(1, myTag.getDomainEvents().size());
        assertEquals(MyTagCreated.class, myTag.getDomainEvents().get(0).getClass());
    }
}
```

### Iteration 5, Assert MyTagCreated in CreateMyTagUseCaseTest
#### Iteration 5, step 1: Add DomainEventBus, GoogleEventBusAdapter, FakeEventListener, SimpleAsyncTaskExecutor
```java
public class CreateMyTagUseCaseTest {
    @Test
    public void create_mytag_usecase(){
        DomainEventBus domainEventBus = new GoogleEventBusAdapter();
        FakeEventListener fakeEventListener = new FakeEventListener();
        domainEventBus.register(fakeEventListener);
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.execute((GoogleEventBusAdapter) domainEventBus);

        MyTagRepository repository = new InMemoryMyTagRepository(domainEventBus);
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.counter));
    }

    public class FakeEventListener{
        public int counter = 0;
        @Subscribe
        public void whenMyTagCreated(MyTagCreated event){
            System.out.println("whenMyTagCreated, event =  " + event);
            counter++;
        }
    }
}
```

#### Iteration 5, step 2: Revise InMemoryMyTagRepository to post domain events after save        
```java
public class InMemoryMyTagRepository implements MyTagRepository {
    private final List<MyTag> store = new ArrayList<>();
    private final DomainEventBus domainEventBus;
    public InMemoryMyTagRepository(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    @Override
    public Optional<MyTag> findById(String tagId) {
        return store.stream().filter(x -> x.getTagId().equals(tagId)).findAny();
    }

    @Override
    public void save(MyTag myTag) {
        store.add(myTag);
        domainEventBus.postAll(myTag);
    }
}
```

#### Iteration 5, step 3: Run the test; it passes

***

### Iteration 6, Revise MyTag to apply Event Sourcing coding style
#### Iteration 6, step 1: Revise MyTag, only show modified part of the code 
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    public MyTag(String boardId, String tagId, String name) {
        super();
        apply(new MyTagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    protected void when(DomainEvent domainEvent) {
        switch (domainEvent){
            case MyTagCreated event -> {
                this.id = event.aggregateId();
                this.boardId = event.boardId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.isDeleted = false;
            }
            default -> {}
        }
    }
}
```

#### Iteration 6, step 2: Run the tests; they pass

#### Iteration 6, step 3: Add reconstruct_mytag_from_mytag_created_event test method
```java
public class MyTagTest {
    @Test
    public void reconstruct_mytag_from_mytag_created_event(){
         MyTagCreated myTagCreated = new MyTagCreated(
                 "board id",
                 "tad id",
                 "issue",
                 UUID.randomUUID(),
                 DateProvider.now());

         MyTag myTag = new MyTag(Arrays.asList(myTagCreated));

        assertEquals(myTagCreated.aggregateId(), myTag.getId());
        assertEquals(myTagCreated.aggregateId(), myTag.getTagId());
        assertEquals(myTagCreated.boardId(), myTag.getBoardId());
        assertEquals(myTagCreated.tagId(), myTag.getTagId());
        assertEquals(myTagCreated.name(), myTag.getName());
    }
}
```

#### Iteration 6, step 4: Write a new constructor to accept a list of domain event
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    
    public MyTag(List<MyTagCreated> domainEvents) {
        domainEvents.forEach(x -> apply(x));
        clearDomainEvents();
    }
}
```

***

### Exercise 1b: DeleteMyTagUseCase

#### Iteration 1, step 1: Write DeleteMyTagUseCaseTest by copying from CreateMyTagUseCaseTest
```java
public class DeleteMyTagUseCaseTest {
    DomainEventBus domainEventBus;
    SimpleAsyncTaskExecutor executor;
    FakeEventListener fakeEventListener;
    MyTagRepository repository;

    @BeforeEach
    public void setUp(){
        fakeEventListener = new FakeEventListener();
        domainEventBus = new GoogleEventBusAdapter();
        domainEventBus.register(fakeEventListener);

        executor = new SimpleAsyncTaskExecutor();
        executor.execute((GoogleEventBusAdapter) domainEventBus);

        repository = new InMemoryMyTagRepository(domainEventBus);
    }

    @Test
    public void delete_mytag_usecase(){
        String tadId = create_mytag_usecase(UUID.randomUUID().toString());
        DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
        DeleteMyTagInput input = new DeleteMyTagInput();
        input.setTagId(tadId);

        CqrsOutput output = deleteMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isEmpty());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.counter));
    }

    public class FakeEventListener{
        public int counter = 0;

        @Subscribe
        public void whenMyTagTagDeleted(MyTagDeleted event){
            System.out.println("whenMyTagTagDeleted, event = " + event);
            counter++;
        }
    }

    private String create_mytag_usecase(String boardId){
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId(boardId);
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        return createMyTagUseCase.execute(input).getId();
    }
}

```

#### Iteration 1, step 2: Implement production code, CreateMyTagUseCase and DeleteMyTagInput
```java

public interface DeleteMyTagUseCase {
    CqrsOutput execute(DeleteMyTagInput input);
}

public class DeleteMyTagInput {
    private String tagId;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
```


#### Iteration 1, step 3: Implement production code, DeleteMyTagService
```java
public class DeleteMyTagService implements DeleteMyTagUseCase {

    private final MyTagRepository repository;
    public DeleteMyTagService(MyTagRepository repository) {
        this.repository = repository;
    }

    @Override
    public CqrsOutput execute(DeleteMyTagInput input) {
        var mytag = repository.findById(input.getTagId()).get();

        mytag.markAsDeleted("");
        repository.delete(mytag);

        return CqrsOutput.create().setId(mytag.getId());
    }
}
```

#### Iteration 1, step 4: Implement production code, InMemoryMyTagRepository, only show new code
```java
public class InMemoryMyTagRepository implements MyTagRepository {
    @Override
    public void delete(MyTag mytag) {
        if (store.stream().filter( x-> x.getTagId().equals(mytag.getTagId())).findAny().isEmpty()){
            return;
        }
        store.removeIf(x -> x.getTagId().equals(mytag.getTagId()));
        domainEventBus.postAll(mytag);
    }
}
```

#### Iteration 1, step 5: Implement production code, MyTag, only show new code
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    @Override
    public void markAsDeleted(String userId) {
        apply(new MyTagDeleted(boardId, tagId, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    protected void when(DomainEvent domainEvent) {
        switch (domainEvent) {
            case MyTagCreated event -> {
                this.id = event.aggregateId();
                this.boardId = event.boardId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.isDeleted = false;
            }
            case MyTagDeleted event -> isDeleted = true;
            default -> {
            }
        }
    }
}
```

#### Iteration 1, step 6: Implement production code, MyTagDeleted
```java
public record MyTagDeleted(
        String boardId,
        String tagId,
        UUID id,
        Date occurredOn
) implements DomainEvent {

    @Override
    public String aggregateId() {
        return tagId;
    }
}
```

#### Iteration 2, step 1: Revise CreateMyTagUseCase and CreateMyTagInput
```java
public interface CreateMyTagUseCase extends Command<CreateMyTagInput, CqrsOutput> { }

public class CreateMyTagInput implements Input { }
```

#### Iteration 2, step 2: Revise DeleteMyTagUseCase and DeleteMyTagInput
```java
public interface DeleteMyTagUseCase extends Command<DeleteMyTagInput, CqrsOutput> {}

public class DeleteMyTagInput implements Input {}
```

#### Iteration 3, step 1: Refactor domain events by copying TagEvents and delete MyTagCreated and MyTagDeleted
```java
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
}
```
#### Iteration 3, step 2: Revise MyTag
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {
    public MyTag(String boardId, String tagId, String name) {
        super();
        apply(new MyTagEvents.TagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
    }

    public MyTag(List<MyTagEvents> domainEvents) {
        domainEvents.forEach( x->  apply(x));
        clearDomainEvents();
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
                this.boardId = event.boardId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.isDeleted = false;
            }
            case MyTagEvents.TagDeleted event -> isDeleted = true;
            default -> {}
        }
    }
}
```


#### Iteration 3, step 3: Revise MyTagTest
```java
public class MyTagTest {
    @Test
    public void create_mytag_generates_a_mytag_created_event(){
        MyTag myTag = new MyTag("board id", "tad id", "issue");

        assertEquals(1, myTag.getDomainEvents().size());
        assertEquals(MyTagEvents.TagCreated.class, myTag.getDomainEvents().get(0).getClass());
    }

    @Test
    public void reconstruct_mytag_from_mytag_created_event(){
         MyTagEvents.TagCreated myTagCreated = new MyTagEvents.TagCreated(
                 "board id",
                 "tad id",
                 "issue",
                 UUID.randomUUID(),
                 DateProvider.now());

         MyTag myTag = new MyTag(Arrays.asList(myTagCreated));

        assertEquals(myTagCreated.aggregateId(), myTag.getId());
        assertEquals(myTagCreated.aggregateId(), myTag.getTagId());
        assertEquals(myTagCreated.boardId(), myTag.getBoardId());
        assertEquals(myTagCreated.tagId(), myTag.getTagId());
        assertEquals(myTagCreated.name(), myTag.getName());
    }
}
```

#### Iteration 3, step 4: Revise CreateMyTagUseCaseTest
```java
public class CreateMyTagUseCaseTest {
    public class FakeEventListener{
        public int counter = 0;
        @Subscribe
        public void whenMyTagCreated(MyTagEvents.TagCreated event){
            counter++;
        }
    }
}
```

#### Iteration 3, step 5: Revise DeleteMyTagUseCaseTest
```java
public class DeleteMyTagUseCaseTest {
    public class FakeEventListener{
        public int counter = 0;

        @Subscribe
        public void whenMyTagCreated(MyTagEvents.TagDeleted event){
            counter++;
        }
    }
}
```

***


### Exercise 2a: Use EventStoreDB 

### Revise CreateMyTagUseCaseTest to use EventSotoreDB
#### Iteration 1, step 1: Write MyTagEventSourcingRepository by copying BoardEventSourcingRepository 
```java
public class MyTagEventSourcingRepository implements MyTagRepository {
    private final GenericEventSourcingRepository<MyTag> eventSourcingRepository;

    public MyTagEventSourcingRepository(EventStore eventStore) {
        eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, MyTag.class, MyTag.CATEGORY);
    }

    @Override
    public Optional<MyTag> findById(String tagId)  {
        return eventSourcingRepository.findById(tagId);
    }

    @Override
    public void save(MyTag myTag) {
        eventSourcingRepository.save(myTag);
    }

    @Override
    public void delete(MyTag myTag) {
        eventSourcingRepository.delete(myTag);
    }
}


public class MyTag extends AggregateRoot<String , DomainEvent> {
    public final static String CATEGORY = "MyTag";

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
```

#### Iteration 1, step 2: Use MyTagEventSourcingRepository in CreateMyTagUseCaseTest
```java
public class CreateMyTagUseCaseTest {
    DomainEventBus domainEventBus;
    FakeEventListener fakeEventListener;
    SimpleAsyncTaskExecutor executor;
    MyTagRepository repository;
    String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";
    EsdbListener esdbListener;

    @BeforeEach
    public void setUp(){
        DomainEventMapper.setMapper(MyTagEvents.mapper());

        executor = new SimpleAsyncTaskExecutor();
        domainEventBus = new GoogleEventBusAdapter();

        fakeEventListener = new FakeEventListener();
        domainEventBus.register(fakeEventListener);
        executor.execute((GoogleEventBusAdapter) domainEventBus);

//        repository = new InMemoryMyTagRepository(domainEventBus);
        EventStore eventStore = new EsdbStoreAdapter(ESDB_URL);
        repository = new MyTagEventSourcingRepository(eventStore);
        esdbListener = new EsdbPersistentListener(ESDB_URL, DomainEventMapper.getMapper(), domainEventBus);
        executor.execute(esdbListener);

    }

    @AfterEach
    public void teardown(){
        if (null != esdbListener){
            esdbListener.shutdown();
        }
    }

    @Test
    public void create_mytag_usecase(){
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId("board id");
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        CqrsOutput output = createMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isPresent());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.counter));
    }

    public class FakeEventListener{
        public int counter = 0;

        @Subscribe
        public void whenMyTagCreated(MyTagEvents.TagCreated event){
            System.out.println("whenMyTagCreated, event =  " + event);
            counter++;
        }
    }
}
```
#### Iteration 1, step 3: 解釋
* MyTagEventSourcingRepository
* GenericEventSourcingRepository
* EventStore 
* EsdbListener
* EsdbPersistentListener
* DomainEventMapper

***


### Exercise 2b: Use EzOutboxStore

### Revise DeleteMyTagUseCaseTest to use EzOutboxStore
#### Iteration 1, step 1: Add MyTagData by copying from TagData
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


    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
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

}
```
#### Iteration 1, step 2: Create MyTagOrmClient by copying from TagOrmClient
```java
public interface MyTagOrmClient extends OrmClient<MyTagData, String> {}
```

#### Iteration 1, step 3: Create MyTagOutboxRepository by copying from BoardOutboxRepository and create MyTagMapper
```java
public class MyTagOutboxRepository implements MyTagRepository {
    private final GenericOutboxRepository<MyTag, MyTagData, String> outboxRepository;

    public MyTagOutboxRepository(OutboxStore<MyTagData, String> store) {
        outboxRepository = new GenericOutboxRepository<>(store, new MyTagMapper());
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
```

```java
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


 
#### Iteration 1, step 4: add bean in RepositoryInjection file
```java
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

    private MyTagOrmClient myTagOrmStoreClient;

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
        this.myTagOrmStoreClient = myTagOrmStoreClient;
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

    @Bean(name="MyTagPostgresOutboxStoreClient")
    public EzOutboxStore myTagPostgresOutboxStoreClient() {
        return new EzOutboxStore(myTagOrmStoreClient, pgMessageDbClient);
    }
}
```

#### Iteration 1, step 5: Revise DeleteMyTagUseCaseTest
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
    DomainEventBus domainEventBus;
    SimpleAsyncTaskExecutor executor;
    FakeEventListener fakeEventListener;
    MyTagRepository repository;

    @Value("${jdbc.test.url}")
    String JDBC_TEST_URL;

    @Autowired
    MyTagOrmClient myTagOrmStoreClient;

    @Autowired
    PgMessageDbClient pgMessageDbClient;

    PostgresDomainEventListener postgresDomainEventListener;

    @BeforeEach
    public void setUp(){
        DomainEventMapper.setMapper(MyTagEvents.mapper());
        fakeEventListener = new FakeEventListener();
        domainEventBus = new GoogleEventBusAdapter();
        domainEventBus.register(fakeEventListener);

        executor = new SimpleAsyncTaskExecutor();
        executor.execute((GoogleEventBusAdapter) domainEventBus);

//            repository = new InMemoryMyTagRepository(domainEventBus);
        repository = new MyTagOutboxRepository(new EzOutboxStoreAdapter(
                new EzOutboxStore(myTagOrmStoreClient, pgMessageDbClient)));
        try {
            postgresDomainEventListener = new PostgresDomainEventListener(
                    JDBC_TEST_URL,
                    "postgres",
                    "root", 20,
                    DomainEventMapper.getMapper(),
                    domainEventBus);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        executor.execute(postgresDomainEventListener);
    }

    @Test
    public void delete_mytag_usecase(){
        String tadId = create_mytag_usecase(UUID.randomUUID().toString());
        DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
        DeleteMyTagInput input = new DeleteMyTagInput();
        input.setTagId(tadId);

        CqrsOutput output = deleteMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isEmpty());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.counter));
    }

    public class FakeEventListener{
        public int counter = 0;

        @Subscribe
        public void whenMyTagTagDeleted(MyTagEvents.TagDeleted event){
            System.out.println("whenMyTagTagDeleted, event = " + event);
            counter++;
        }
    }

    private String create_mytag_usecase(String boardId){
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId(boardId);
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        return createMyTagUseCase.execute(input).getId();
    }
}
```

#### Iteration 1, step 6: 解釋

* @Value("${jdbc.test.url}") JDBC_TEST_URL
* MyTagOrmClient
* PgMessageDbClient
* PostgresDomainEventListener

### Exercise 3: Event Type Mapping

#### Iteration 1, step 1: see MyTagEvent, GenericEventSourcingRepository, DomainEventMapper, and DomainEventTypeMapper
```java
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
    ///////////////////////////////////////////////////////////////

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
}
```

```java
public class GenericEventSourcingRepository<T extends AggregateRoot> {

    @Override
    public Optional<T> findById(String aggregateId) {
        requireNotNull("AggregateId", aggregateId);
        Optional<AggregateRootData> aggregateRootData =
                eventStore.load(AggregateRoot.getStreamName(category, aggregateId));
        if (aggregateRootData.isEmpty()) {
            return Optional.empty();
        }

        // See this statement
        List<DomainEvent> domainEvents = DomainEventMapper.
                toDomain(aggregateRootData.get().getDomainEventDatas());
        try {
            T aggregate = (T) clazz.getConstructor(List.class).newInstance(domainEvents);
            aggregate.setVersion(aggregateRootData.get().getVersion());
            if (aggregate.isDeleted())
                return Optional.empty();
            else
                return Optional.of(aggregate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

```java
public class DomainEventMapper {
    public static final String RAW_TYPE = "rawType";
    private static DomainEventTypeMapper mapper = DomainEvent.mapper();

    public static final DomainEventTypeMapper getMapper() {
        return mapper;
    }

    public static void setMapper(DomainEventTypeMapper newMapper) {
        newMapper.getMap().forEach( (key, value) -> {
            mapper.put(key, value);
        });
    }

    public static DomainEventData toData(DomainEvent event) {
        requireNotNull("DomainEvent", event);

        return EventDataBuilderJava8.json(
                        mapper.toMappingType(event.getClass()),
                        event)
                .eventId(event.id())
                .buildNewDomainEventData();
    }

    public static List<DomainEventData> toData(List<DomainEvent> events) {
        requireNotNull("DomainEvent", events);

        return events.stream().map(DomainEventMapper::toData).collect(Collectors.toList());
    }

    public static <T extends DomainEvent> T toDomain(DomainEventData data) {
        requireNotNull("DomainEventData", data);
        requireNotNull("Please call setMapper to config public class DomainEventMapper first", mapper);

        T domainEvent = null;
        try {
            domainEvent = (T) Json.readAs(data.eventData(), mapper.toClass(data.eventType()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return domainEvent;
    }

    public static <T extends DomainEvent> List<T> toDomain(List<DomainEventData> datas) {
        requireNotNull("DomainEventData list", datas);

        List<T> result = new ArrayList<>();
        datas.forEach( x -> result.add(toDomain(x)));
        return result;
    }

    private static String getEventRawTypeMetadata(DomainEvent event) {
        DomainEventMetadata metadata = new DomainEventMetadata();
        metadata.append(RAW_TYPE, event.getClass().getName());

        return metadata.asJsonString();
    }
}
```

```java
public interface DomainEventTypeMapper {
    void put(String key, Class value);
    String toMappingType(Class cls);
    String toMappingType(DomainEvent event);
    boolean containsMappingType(String mappingType);
    Class<? extends DomainEvent> toClass(String mappingType);
    Map<String, Class> getMap();

    class DomainEventTypeMapperImpl implements DomainEventTypeMapper {

        private final BiMap<String, Class> biMapper;

        public DomainEventTypeMapperImpl() {
            biMapper = new BiMap<>();
        }

        @Override
        public void put(String key, Class value){
            biMapper.put(key, value);
        }

        @Override
        public String toMappingType(Class cls){
            if (null != biMapper.getKey(cls)) {
                return biMapper.getKey(cls);
            }
            throw new RuntimeException("Unsupported event for getting mapping: " + cls);
        }

        @Override
        public String toMappingType(DomainEvent event){
            if (null != biMapper.getKey(event.getClass())) {
                return biMapper.getKey(event.getClass());
            }
            throw new RuntimeException("Unsupported event for getting mapping: " + event);
        }

        @Override
        public boolean containsMappingType(String mappingType){
            return biMapper.containsKey(mappingType);
        }

        @Override
        public Class<? extends DomainEvent> toClass(String mappingType){
            if (biMapper.containsKey(mappingType)){
                return biMapper.get(mappingType);
            }
            throw new RuntimeException("Unsupported event mapping type: " + mappingType);
        }

        @Override
        public Map<String, Class> getMap() {
            return biMapper;
        }
    }
}
```

### Exercise 4: Find all tags in a board

#### Iteration 1, Add get_mytags_by_board_id() test method in CreateMyTagUseCaseTest; show new code only
```java
public class CreateMyTagUseCaseTest {

    @Test
    public void get_mytags_by_board_id(){
        String boardId1 = UUID.randomUUID().toString();
        String tag1Id = create_mytag_usecase(boardId1);
        String tag2Id = create_mytag_usecase(boardId1);
        String tag3Id = create_mytag_usecase(boardId1);

        String boardId2 = UUID.randomUUID().toString();
        String tag4Id = create_mytag_usecase(boardId2);

        String boardId3 = UUID.randomUUID().toString();
        String tag5Id = create_mytag_usecase(boardId3);
        String tag6Id = create_mytag_usecase(boardId3);

        assertEquals(3, repository.getMyTagsByBoardId(boardId1).size());
        assertEquals(1, repository.getMyTagsByBoardId(boardId2).size());
        assertEquals(2, repository.getMyTagsByBoardId(boardId3).size());
    }


    private String create_mytag_usecase(String boardId){
        CreateMyTagUseCase createMyTagUseCase = new CreateMyTagService(repository);
        CreateMyTagInput input = new CreateMyTagInput();
        input.setBoardId(boardId);
        input.setTagId(UUID.randomUUID().toString());
        input.setName("issue");

        return createMyTagUseCase.execute(input).getId();
    }
}
```

#### Iteration 2, Add  List<MyTag> getMyTagsByBoardId(String boardId1) in MyTagRepository
```java
public interface MyTagRepository {
    Optional<MyTag> findById(String tagId);
    void save(MyTag myTag);
    void delete(MyTag mytag);
    List<MyTag> getMyTagsByBoardId(String boardId);
}
```

#### Iteration 3, step 1: Implement InMemoryMyTagRepository; show new code only
```java
public class InMemoryMyTagRepository implements MyTagRepository {
    @Override
    public List<MyTag> getMyTagsByBoardId(String boardId1) {
        return store.stream().filter(x -> x.getBoardId().equals(boardId1)).toList();
    }
}
```

#### Iteration 3, step 2: Run the test


#### Iteration 4, step 1: Implement MyTagEventSourcingRepository; show new code only
```java
public class MyTagEventSourcingRepository implements MyTagRepository {
    private final GenericEventSourcingRepository<MyTag> eventSourcingRepository;
    private final EventStore eventStore;

    public MyTagEventSourcingRepository(EventStore eventStore) {
        eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, MyTag.class, MyTag.CATEGORY);
        this.eventStore = eventStore;
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
```
#### Iteration 4, step 2: Run the test


#### Iteration 5, step 1: Add get_mytags_by_board_id() test method in DeleteMyTagUseCaseTest; show new code only
```java
public class DeleteMyTagUseCaseTest {
    @Test
    public void get_mytags_by_board_id(){
        String boardId1 = UUID.randomUUID().toString();
        String tag1Id = create_mytag_usecase(boardId1);
        String tag2Id = create_mytag_usecase(boardId1);
        String tag3Id = create_mytag_usecase(boardId1);

        String boardId2 = UUID.randomUUID().toString();
        String tag4Id = create_mytag_usecase(boardId2);

        String boardId3 = UUID.randomUUID().toString();
        String tag5Id = create_mytag_usecase(boardId3);
        String tag6Id = create_mytag_usecase(boardId3);

        assertEquals(3, repository.getMyTagsByBoardId(boardId1).size());
        assertEquals(1, repository.getMyTagsByBoardId(boardId2).size());
        assertEquals(2, repository.getMyTagsByBoardId(boardId3).size());
    }
}
```

#### Iteration 5, step 2: Implement MyTagOutboxRepository; show new code only
```java
public class MyTagOutboxRepository implements MyTagRepository {
    private final GenericOutboxRepository<MyTag, MyTagData, String> outboxRepository;
    private final OutboxStore<MyTagData, String> store;
    
    public MyTagOutboxRepository(OutboxStore<MyTagData, String> store) {
        outboxRepository = new GenericOutboxRepository<>(store, new MyTagMapper());
        this.store = store;
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

#### Iteration 1: Add optimistic_locking_failure() test method in CreateMyTagUseCaseTest; show new code only

```java
    public class CreateMyTagUseCaseTest {

    @Test
    public void optimistic_locking_failure(){
        String myTag1Id = create_mytag_usecase(UUID.randomUUID().toString());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.tagCreated));
        MyTag myTagtV1 = repository.findById(myTag1Id).get();
        MyTag myTagtV2 = repository.findById(myTag1Id).get();
        myTagtV1.rename("story");
        repository.save(myTagtV1);
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.tagRenamed));

        try{
            repository.save(myTagtV2);
            fail("Infeasible path");
        }
        catch (RepositorySaveException e){
            assertEquals("Optimistic locking failure", e.getMessage());
        }
    }

    public class FakeEventListener{
        public int counter = 0;
        public int tagCreated = 0;
        public int tagRenamed = 0;

        @Subscribe
        public void whenMyTagCreated(MyTagEvents.TagCreated event){
            System.out.println("whenMyTagCreated, event =  " + event);
            counter++;
            tagCreated++;
        }

        @Subscribe
        public void whenMyTagRenamed(MyTagEvents.TagRenamed event){
            System.out.println("whenMyTagRenamed, event =  " + event);
            counter++;
            tagRenamed++;
        }
    }
}
```

#### Iteration 2, step 1: Implement rename(String newName) in MyTag; show new code only
```java
public class MyTag extends AggregateRoot<String , DomainEvent> {

    public void rename(String newName) {
        if (name.equals(newName)){
            return;
        }

        apply(new MyTagEvents.TagRenamed(boardId, tagId, newName, UUID.randomUUID(), DateProvider.now()));
    }

    @Override
    protected void when(DomainEvent domainEvent) {
        switch (domainEvent){
            case MyTagEvents.TagCreated event -> {
                this.id = event.aggregateId();
                this.boardId = event.boardId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.isDeleted = false;
            }
            case MyTagEvents.TagDeleted event -> isDeleted = true;
            case MyTagEvents.TagRenamed event -> this.name = event.name();
            default -> {}
        }
    }
}
```

#### Iteration 2, step 2: Run the test; it passes

#### Iteration 3, step 1: Switch to use InMemoryMyTagRepository; run optimistic_locking_failure test; it fails

#### Iteration 3, step 2: Implement optimistic locking in InMemoryMyTagRepository repository
```java
public class InMemoryMyTagRepository implements MyTagRepository {
    private final List<MyTag> store = new ArrayList<>();
    private final DomainEventBus domainEventBus;
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
#### Iteration 3, step 1: Run optimistic_locking_failure test; it passes; show new code only
```java
public class DeleteMyTagUseCaseTest {

    @Test
    public void optimistic_locking_failure(){
        String myTag1Id = create_mytag_usecase(UUID.randomUUID().toString());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.tagCreated));
        MyTag myTagtV1 = repository.findById(myTag1Id).get();
        MyTag myTagtV2 = repository.findById(myTag1Id).get();
        myTagtV1.rename("story");
        repository.save(myTagtV1);
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.tagRenamed));

        try{
            repository.save(myTagtV2);
            fail("Infeasible path");
        }
        catch (RepositorySaveException e){
            assertEquals("Optimistic locking failure", e.getMessage());
        }
    }

    public class FakeEventListener{
        public int counter = 0;
        public int tagCreated = 0;
        public int tagRenamed = 0;

        @Subscribe
        public void whenMyTagCreated(MyTagEvents.TagCreated event){
            System.out.println("whenMyTagCreated, event =  " + event);
            counter++;
            tagCreated++;
        }

        @Subscribe
        public void whenMyTagRenamed(MyTagEvents.TagRenamed event){
            System.out.println("whenMyTagRenamed, event =  " + event);
            counter++;
            tagRenamed++;
        }
    }
}
```
#### Iteration 3, step 2: See the long version in MyTagData and the @Version annotation
```java
@Entity
@Table(name="mytag")
public class MyTagData implements OutboxData {
    
    @Transient
    private String steamName;
    @Transient
    private List<DomainEventData> domainEventDatas;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "board_id", nullable = false)
    private String boardId;
    @Column(name = "tag_name")
    private String name;
    
    @Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private long version;
}
```
***

## Exercise 6: Snapshot
#### Iteration 1:  Explain Memento design pattern and the AggregateSnapshot<T> interface
```java
public interface AggregateSnapshot<T> {
    T getSnapshot();
    void setSnapshot(T snapshot);
}
```

#### Iteration 2: MyTag implement AggregateSnapshot
```java
public class MyTag extends AggregateRoot<String, DomainEvent> implements AggregateSnapshot<MyTag.MyTagSnapshot> {

    public final static String CATEGORY = "MyTag";
    private String boardId;
    private String tagId;
    private String name;

    //region: Snapshot
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
    //endregion

    private MyTag(){}

    public MyTag(String boardId, String tagId, String name) {
        super();
        apply(new MyTagEvents.TagCreated(boardId, tagId, name, UUID.randomUUID(), DateProvider.now()));
    }

    public MyTag(List<MyTagEvents> domainEvents) {
        super();
        domainEvents.forEach( x->  apply(x));
        clearDomainEvents();
    }

    public String getBoardId() {
        return boardId;
    }

    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        if (name.equals(newName)){
            return;
        }

        apply(new MyTagEvents.TagRenamed(boardId, tagId, newName, UUID.randomUUID(), DateProvider.now()));
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
                this.boardId = event.boardId();
                this.tagId = event.tagId();
                this.name = event.name();
                this.isDeleted = false;
            }
            case MyTagEvents.TagDeleted event -> isDeleted = true;
            case MyTagEvents.TagRenamed event -> this.name = event.name();
            default -> {}
        }
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

}
```

#### Iteration 3: Write SnapshottedMyTagEventSourcingRepository
```java
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
```
#### Iteration 4: Write read_from_snapshot and read_from_stream test methods in CreateMyTagUseCaseTest; show new code only        
```java
   public class CreateMyTagUseCaseTest {
    DomainEventBus domainEventBus;
    FakeEventListener fakeEventListener;
    SimpleAsyncTaskExecutor executor;
    MyTagRepository repository;
    String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";
    EsdbListener esdbListener;
    EventStore eventStore;

    @BeforeEach
    public void setUp(){
        DomainEventMapper.setMapper(MyTagEvents.mapper());

        executor = new SimpleAsyncTaskExecutor();
        domainEventBus = new GoogleEventBusAdapter();

        fakeEventListener = new FakeEventListener();
        domainEventBus.register(fakeEventListener);
        executor.execute((GoogleEventBusAdapter) domainEventBus);

//        repository = new InMemoryMyTagRepository(domainEventBus);
        eventStore = new EsdbStoreAdapter(ESDB_URL);
        repository = new MyTagEventSourcingRepository(eventStore);
        esdbListener = new EsdbPersistentListener(ESDB_URL, DomainEventMapper.getMapper(), domainEventBus);
        executor.execute(esdbListener);

    }

    @Test
    public void read_from_snapshot() {
        MyTagRepository myTagRepository = new SnapshottedMyTagEventSourcingRepository(
                (MyTagEventSourcingRepository) this.repository, eventStore);

        String tagId = "1002";
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());

        int loop = 1000;
        createMyTagAndGenerateNTagRenamedEventsIfItDoesNotExist(myTagRepository, tagId, loop);

        sw.start();
        var mySnapshotTag = myTagRepository.findById(tagId).get();
        sw.stop();
        long snapshotTotalTimeMillis  = sw.getTotalTimeMillis();
        System.out.println("Read form snapshot for " + loop + " events: " + snapshotTotalTimeMillis + " ms");
    }

    @Test
    public void read_from_stream() {
        MyTagRepository myTagRepository = new SnapshottedMyTagEventSourcingRepository(
                (MyTagEventSourcingRepository) this.repository, eventStore);

        String tagId = "1002";
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());

        int loop = 1000;
        createMyTagAndGenerateNTagRenamedEventsIfItDoesNotExist(myTagRepository, tagId, loop);

        sw.start();
        var myDirectTag = this.repository.findById(tagId).get();
        sw.stop();
        long directStreamTotalTimeMillis  = sw.getTotalTimeMillis();
        System.out.println("Read form stream for " + loop + " events: " + directStreamTotalTimeMillis + " ms");
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
```


## Exercise 7: $all stream (master stream)

#### Iteration 1: Explain why need the $all stream.

#### Iteration 2: See EsdbPersistentListener
```java
public class EsdbPersistentListener implements EsdbListener {
    private EventStoreDBClientSettings settings;
    private EventStoreDBClient eventStoreDBClient;
    private EventStoreDBPersistentSubscriptionsClient client;
    private final String INCLUDE_EZKANBAN_EVENT_REGEX = "(\\w+Events\\$\\w+)";
    private SubscriptionFilter filter;
    private final static String GROUP_NAME = "EZKANBAN_MONO_MAIN";
    private final DomainEventTypeMapper domainEventTypeMapper;
    private final DomainEventBus domainEventBus;
    private PersistentSubscription subscription;
    private final RecentlyReadEvents recentlyReadEvents;
    private final int CAPACITY = 32;
    private final boolean ignoreUnknownEventType;
    public static final boolean IGNORE_UNKNOWN_EVENT_TYPE = true;

    public EsdbPersistentListener(String connectionString,
                                  DomainEventTypeMapper domainEventTypeMapper,
                                  DomainEventBus domainEventBus) {
        this(connectionString, domainEventTypeMapper, domainEventBus, false);
    }

    public EsdbPersistentListener(String connectionString,
                                  DomainEventTypeMapper domainEventTypeMapper,
                                  DomainEventBus domainEventBus,
                                  boolean ignoreUnknownEventType) {
        super();

        requireNotNull("ConnectionString", connectionString);
        requireNotNull("DomainEventTypeMapper", domainEventTypeMapper);
        requireNotNull("DomainEventBus", domainEventBus);

        this.domainEventTypeMapper = domainEventTypeMapper;
        this.domainEventBus = domainEventBus;
        this.ignoreUnknownEventType = ignoreUnknownEventType;
        recentlyReadEvents = new RecentlyReadEvents(CAPACITY);
        connect(connectionString);

        filter = SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression(INCLUDE_EZKANBAN_EVENT_REGEX)
                .build();

        try {
            client.createToAll(GROUP_NAME,
                    PersistentSubscriptionToAllSettings.builder()
                            .filter(filter)
                            .fromEnd()
                            .build()).get();
            System.out.println("group created");
        } catch (ExecutionException e) {
            if (e.getMessage().contains("ALREADY_EXISTS: Subscription group EZKANBAN_MONO_MAIN on stream $all exists.")) {
                // Ignore
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(String connectionString) {
        settings = EventStoreDBConnectionString.parseOrThrow(connectionString);
        eventStoreDBClient = EventStoreDBClient.create(settings);
        client = EventStoreDBPersistentSubscriptionsClient.create(settings);
    }

    public void deletePersistentSubscription() {
        try {
            client.deleteToAll(GROUP_NAME).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Esdb persistent listener starts");

        final CompletableFuture<Integer> result = new CompletableFuture<>();

        SubscribePersistentSubscriptionOptions connectOptions = SubscribePersistentSubscriptionOptions.get()
                .setBufferSize(512);
        recentlyReadEvents.clear();

        try {
            subscription = client.subscribeToAll(GROUP_NAME, connectOptions, new PersistentSubscriptionListener() {
                @Override
                public void onEvent(PersistentSubscription subscription, ResolvedEvent resolvedEvent) {
                    RecordedEvent event = resolvedEvent.getEvent();
                    System.out.println("Persistent onEvent, EventType =====>" + event.getEventType());
                    final Optional<DomainEvent> domainEvent = toDomain(event.getEventType(), event.getEventData());

                    if (domainEvent.isPresent()) {
                        // TODO: Need to guarantee domain event posted successfully before ack.
                        recentlyReadEvents.add(domainEvent.get());
                        domainEventBus.post(domainEvent.get());
                    }
                    subscription.ack(resolvedEvent);
                }

                @Override
                public void onError(PersistentSubscription subscription, Throwable throwable) {
                    System.out.println("onError");
                    result.completeExceptionally(throwable);
                }

                @Override
                public void onCancelled(PersistentSubscription subscription) {
                    System.out.println("onCancelled");
                }
            }).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class RecentlyReadEvents {
        private final int capacity;
        private Queue<DomainEvent> recentlyReadEvents;

        public RecentlyReadEvents(int capacity) {
            this.recentlyReadEvents = new LinkedList<>();
            this.capacity = capacity;
        }

        public int size() {
            return recentlyReadEvents.size();
        }

        public DomainEvent take() {
            return recentlyReadEvents.poll();
        }

        public void add(DomainEvent domainEvent) {
            if (recentlyReadEvents.size() == capacity) {
                recentlyReadEvents.poll();
            }
            recentlyReadEvents.add(domainEvent);
        }

        public void clear() {
            recentlyReadEvents.clear();
        }

        public DomainEvent[] getRecentlyReadEvents() {
            var x = recentlyReadEvents.toArray(new DomainEvent[recentlyReadEvents.size()]);
            return recentlyReadEvents.toArray(new DomainEvent[recentlyReadEvents.size()]);
        }
    }

    public void updatePersistentSubscription() {

        PersistentSubscriptionToAllSettings updatedSettings = PersistentSubscriptionToAllSettings.builder()
                .filter(filter)
//                .startFrom(0, 0)
                .fromEnd()
                .build();

        UpdatePersistentSubscriptionToAllOptions options = UpdatePersistentSubscriptionToAllOptions.get()
                .settings(updatedSettings);

        try {
            client.updateToAll(GROUP_NAME, options).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (null != subscription) {
            subscription.stop();
        }
    }

    @Override
    public void shutdown() {
        if (null != subscription) {
            subscription.stop();
        }
    }

    private Optional<DomainEvent> toDomain(String eventType, byte[] eventData) {
        try {
            Class<?> cls = domainEventTypeMapper.toClass(eventType);
            Object domainEvent = Json.readAs(
                    eventData, cls);
            return Optional.of((DomainEvent) domainEvent);
        } catch (Exception e) {
            if (ignoreUnknownEventType) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }
    }

    public DomainEvent[] getRecentlyReadEvents() {
        return recentlyReadEvents.getRecentlyReadEvents();
    }
}
```

#### Iteration 3: See PersistentConsumer
```java
public class PersistentConsumer implements Runnable {

    private final String name;

    private final String streamName;

    // MILLISECONDS
    private int pollingInterval = 500;

    private PgMessageDbClient pgMessageDbClient;
    public PersistentConsumer(String streamName, String consumerName, PgMessageDbClient pgMessageDbClient, int pollingInterval)  {
        this.streamName = streamName;
        this.name = consumerName;
        this.pgMessageDbClient = pgMessageDbClient;
        this.pollingInterval = pollingInterval;
    }

    public void ack(Checkpoint checkpoint){
        pgMessageDbClient.ack(streamName, checkpoint);
    }

    @Override
    public void run() {
        Checkpoint checkpoint;
        var message = pgMessageDbClient.getLastStreamMessage(streamName);
        if (message.isPresent()) {
            checkpoint = Json.readValue(message.get().getEventBody(), Checkpoint.class);
        } else {
            checkpoint = Checkpoint.valueOf(0);
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                var messages = pgMessageDbClient.findAllStream(checkpoint.position());
                if (messages.size() > 0) {
                    for (var each : messages) {
                        // handle event here
                        System.out.println("Read event ==========> " + each.getEventBody().toString());
                    }

                    checkpoint = Checkpoint.valueOf(messages.size());
                    ack(checkpoint);
                }
                TimeUnit.MILLISECONDS.sleep(pollingInterval);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void close(){
    }

    public void shutdown(){
        close();
        Thread.currentThread().interrupt();
    }
}
```

#### Iteration 4, step 1: in Postgres client: 
```sql
delete from message_store.messages;
```

#### Iteration 4, step 2: run start_consumer in EzesPersistentConsumerTest
```java
public class EzesPersistentConsumerTest extends AbstractBoardContentProjectorTest {

    @Autowired
    PgMessageDbClient pgMessageDbClient;

    @Test
    public void start_consumer() throws SQLException {
        PersistentConsumer persistentConsumer = pgMessageDbClient.subscribeToAll("ezKanban-100", 5000);
        persistentConsumer.run();
    }
}
```

#### Iteration 4, step 3: debug delete_mytag_usecase in DeleteMyTagUseCaseTest; show new code only
```java
public class DeleteMyTagUseCaseTest {

    @Test
    public void delete_mytag_usecase(){
        String tadId = create_mytag_usecase(UUID.randomUUID().toString());
        DeleteMyTagUseCase deleteMyTagUseCase = new DeleteMyTagService(repository);
        DeleteMyTagInput input = new DeleteMyTagInput();
        input.setTagId(tadId);

        CqrsOutput output = deleteMyTagUseCase.execute(input);

        assertNotNull(output.getId());
        assertTrue(repository.findById(output.getId()).isEmpty());
        await().untilAsserted(()-> assertEquals(1, fakeEventListener.counter));
    }
}
```

#### Iteration 4, step 4: see the console; read 3 events
```
Read event ==========> {"position": 0}
2022-08-11 19:23:48.699 DEBUG 22086 --- [           main] tor$SharedEntityManagerInvocationHandler : Creating new EntityManager for shared EntityManager invocation
Hibernate: select max(global_position) FROM messages
2022-08-11 19:23:48.706 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository._ack]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
2022-08-11 19:23:48.706 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1143287451<open>)] for JPA transaction
2022-08-11 19:23:48.707 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == false
2022-08-11 19:23:48.708 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : begin
2022-08-11 19:23:48.708 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@17fbbf0a]
Hibernate: UPDATE messages
SET data = CAST(? AS JSONB)
WHERE stream_name = ?

Read event ==========> {"id": "6b30a729-bdcd-4197-93d4-61be35fc5281", "tagId": "60394c0f-4287-4bfb-ab47-486d961afd04", "boardId": "7b63330a-47fb-42b5-83fa-26a5e91e3267", "occurredOn": "2022-08-11T11:24:03.841+00:00"}
2022-08-11 19:24:08.751 DEBUG 22086 --- [           main] tor$SharedEntityManagerInvocationHandler : Creating new EntityManager for shared EntityManager invocation
Hibernate: select max(global_position) FROM messages
2022-08-11 19:24:08.753 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository._ack]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
2022-08-11 19:24:08.753 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1680256267<open>)] for JPA transaction
2022-08-11 19:24:08.753 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == false
2022-08-11 19:24:08.753 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : begin
2022-08-11 19:24:08.753 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@a9f7cf8]
Hibernate: UPDATE messages
SET data = CAST(? AS JSONB)
WHERE stream_name = ?

Read event ==========> {"id": "6b30a729-bdcd-4197-93d4-61be35fc5281", "tagId": "60394c0f-4287-4bfb-ab47-486d961afd04", "boardId": "7b63330a-47fb-42b5-83fa-26a5e91e3267", "occurredOn": "2022-08-11T11:24:03.841+00:00"}
2022-08-11 19:24:13.767 DEBUG 22086 --- [           main] tor$SharedEntityManagerInvocationHandler : Creating new EntityManager for shared EntityManager invocation
Hibernate: select max(global_position) FROM messages
2022-08-11 19:24:13.768 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository._ack]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
2022-08-11 19:24:13.769 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1999483352<open>)] for JPA transaction
2022-08-11 19:24:13.769 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == false
2022-08-11 19:24:13.769 DEBUG 22086 --- [           main] o.h.e.t.internal.TransactionImpl         : begin
2022-08-11 19:24:13.769 DEBUG 22086 --- [           main] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@7955b4d4]
Hibernate: UPDATE messages
SET data = CAST(? AS JSONB)
WHERE stream_name = ?
```

#### Iteration 4, step 5: see the Postgres client, message table
![](https://lh3.googleusercontent.com/pw/AL9nZEUYoYWnAP0xAoM2yJccgta2KyUZCEKKoENCGbdVe2ljA5b34E-9VckqC2Sno3uaU4rDdeJ4fzdkxB3S_tOuSsQt2vuq0_0xi2Ma5XoVOJ0uex37yeiqDu5ISoAUicUre0jhx2KXC8RvPda2AP5c68JfgA=w1842-h310-no?authuser=0)
 
***

# CQRS

### Exercise 8a: Use NotifyBoardContent & PostgreSQL

NotifyBoardContent projects BoardContentViewModel in board_content table of the Postgres database

#### Iteration 1: In Postgres client, execute:
```sql
delete from message_store.board_content;
```

#### Iteration 2: In GetBoardContentUseCaseTest, run get_board_content_with_a_valid_board_id test method

#### Iteration 3: In Postgres client, see data in the board_content table as following:
![](https://lh3.googleusercontent.com/pw/AL9nZEW3BcFZx262ix9091c_ONFNTv1Dcw3yd796gMWTDd7Z4F0xrU-bEBJIJwzvkRls1kSaFteAurDnlwejxRMF295pWZCrWm0L--xTMklbbVc3P8QPOS9grlhw6R4xP3BcRtOOJSw4-ZdKDofFbF9euA-VxQ=w1002-h155-no?authuser=0)

The projection is a json (the view_model field in the board_content table) as following:
```yaml
{"boardState":{"boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"teamId":"fa37b940-6dc6-4d3a-925f-a64c275bcc25","name":"Task Board","boardMembers":[{"boardRole":"Admin","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"userId":"0f35784f-ede1-474d-9eb1-639596d6305b"},{"boardRole":"Admin","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"userId":"assignee1Id"},{"boardRole":"Member","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"userId":"assignee2Id"}]},"workflowStates":[{"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"name":"firstWorkflow","rootStages":[{"id":{"id":"6b583cc6-615d-4420-bb62-eabce36e8111"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"parentId":{"id":"-1"},"name":"firstStage","wipLimit":{"value":-1},"order":0,"type":"Standard","children":[{"id":{"id":"a09881ac-6588-4908-8c72-d82786e924ec"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"parentId":{"id":"6b583cc6-615d-4420-bb62-eabce36e8111"},"name":"substage1","wipLimit":{"value":-1},"order":0,"type":"Standard","children":[],"layout":"Vertical"},{"id":{"id":"c8659f02-d6ec-438d-9ca6-37647c484677"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"parentId":{"id":"6b583cc6-615d-4420-bb62-eabce36e8111"},"name":"substage2","wipLimit":{"value":-1},"order":1,"type":"Standard","children":[],"layout":"Vertical"}],"layout":"Vertical"}],"version":0}],"committedCardStates":{"a09881ac-6588-4908-8c72-d82786e924ec":[{"cardId":{"id":"3a9cb639-6ab1-44ef-b0fe-1ff70094a123"},"userId":"0f35784f-ede1-474d-9eb1-639596d6305b","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"laneId":{"id":"a09881ac-6588-4908-8c72-d82786e924ec"},"description":"secondCard","estimate":null,"note":null,"deadline":null,"version":0},{"cardId":{"id":"8df0e342-1dde-41fb-9a41-9263d8a3e2ba"},"userId":"0f35784f-ede1-474d-9eb1-639596d6305b","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"laneId":{"id":"a09881ac-6588-4908-8c72-d82786e924ec"},"description":"fourthCard","estimate":null,"note":null,"deadline":null,"version":0}],"c8659f02-d6ec-438d-9ca6-37647c484677":[{"cardId":{"id":"d0436837-a5ed-41ff-9534-dcabcdb7e266"},"userId":"0f35784f-ede1-474d-9eb1-639596d6305b","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"laneId":{"id":"c8659f02-d6ec-438d-9ca6-37647c484677"},"description":"firstCard","estimate":null,"note":null,"deadline":null,"version":1},{"cardId":{"id":"a9444e36-d25a-4f15-8d92-a3a4c0507952"},"userId":"0f35784f-ede1-474d-9eb1-639596d6305b","boardId":{"id":"37730757-363b-4499-b562-142fd4aba41c"},"workflowId":{"id":"7c92d1f6-df41-4f7f-96da-2085cb399bf6"},"laneId":{"id":"c8659f02-d6ec-438d-9ca6-37647c484677"},"description":"thirdCard","estimate":null,"note":null,"deadline":null,"version":1}]},"boardVersion":0,"idempotentData":{"id":null,"handlerId":"82bd8f9e-52c8-45d2-aced-d8de97da3bc3","eventId":"594eaf00-00b1-414d-91e0-450629dd438e","handledOn":"2021-04-04T16:00:00.000+00:00"}}
```
Use Json viewer on the internet to se the above json

### Exercise 8b: Idempotent projector

#### Iteration 1: In GetBoardContentUseCaseTest, run get_board_content_is_idempotent_when_a_board_created_and_a_workflow_created_are_sent_twice test method 
```java
@Test
public class GetBoardContentUseCaseTest extends AbstractSpringBootJpaTest {
    
    public void get_board_content_is_idempotent_when_a_board_created_and_a_workflow_created_are_sent_twice() {

        DuplicatedEventsPublisher publisher = new DuplicatedEventsPublisher(domainEventBus);
        var myExecutor = new SimpleAsyncTaskExecutor();
        myExecutor.execute(publisher);
        await().untilAsserted(() -> Mockito.verify(allEventsListener,
                Mockito.times(2)).when(isA(WorkflowEvents.WorkflowCreated.class)));

        GetBoardContentUseCase getBoardContentUseCase = new GetBoardContentUseCaseImpl(boardContentReadModelRepository);
        GetBoardContentInput input = new GetBoardContentInput();
        input.setBoardId(boardId.id());

        var output = getBoardContentUseCase.execute(input);
        BoardContentViewModel boardContentViewModel = output.getViewModel();

        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        assertEquals(1, boardContentViewModel.getWorkflows().size());

    }
}
```
#### Iteration 1: Explain NotifyBoardContentService code
```java
public class NotifyBoardContentService implements NotifyBoardContent {
    private final BoardContentStateRepository boardContentStateRepository;

    private final String id;

    public NotifyBoardContentService(String id, BoardContentStateRepository boardContentStateRepository) {
        this.id = id;
        this.boardContentStateRepository = boardContentStateRepository;
    }

    @Override
    public void project(DomainEvent domainEvent) {
        BoardContentState boardContentState = null;

        if (boardContentStateRepository.isEventHandled(id, domainEvent.id().toString())){
            return;
        }

        switch (domainEvent) {
            case BoardEvents.BoardCreated event -> {
                boardContentState = BoardContentState.create();
                boardContentState.boardState().isDeleted(false);
                boardContentState.boardState().boardId(event.boardId());
                boardContentState.boardState().teamId(event.teamId());
                boardContentState.boardState().name(event.boardName());
            }
            case BoardEvents.BoardMemberAdded event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                BoardMember boardMember = BoardMemberBuilder.newInstance()
                        .memberType(event.boardRole())
                        .boardId(event.boardId())
                        .userId(event.userId())
                        .build();
                boardContentState.boardState().boardMembers().add(boardMember);
            }
            case WorkflowEvents.WorkflowCreated event -> {
                WorkflowState workflowState = WorkflowState.create();
                workflowState.isDeleted(false);
                workflowState.workflowId(event.workflowId());
                workflowState.boardId(event.boardId());
                workflowState.name(event.workflowName());
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                boardContentState.workflowStates().add(workflowState);
            }
            case WorkflowEvents.WorkflowDeleted event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                boardContentState.workflowStates().removeIf(x -> x.workflowId().equals(event.workflowId()));
            }
            case WorkflowEvents.StageCreated event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                WorkflowState workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(event.workflowId())).findAny().get();
                if (event.parentId().isNull()){
                    Lane stage = LaneBuilder.newInstance()
                            .workflowId(event.workflowId())
                            .parentId(NullLane.ID)
                            .laneId(event.stageId())
                            .name(event.name())
                            .wipLimit(event.wipLimit())
                            .type(event.type())
                            .stage()
                            .order(event.order())
                            .build();
                    insertRootStage(workflowState, stage, stage.getOrder());
                }
                else {
                    Lane parentLane = getLaneById(workflowState, event.parentId()).get();
                    parentLane.createStage(event.stageId(), event.name(), event.wipLimit(), event.type());
                }
            }
            case WorkflowEvents.SwimLaneCreated event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                WorkflowState workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(event.workflowId())).findAny().get();
                Lane lane = getLaneById(workflowState, event.parentId()).get();
                lane.createSwimLane(event.swimLaneId(), event.name(), event.wipLimit(), event.type());
            }
            case WorkflowEvents.WorkflowMoved event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                getWorkflowStateAndIncVersion(boardContentState, event.workflowId());
                List<WorkflowState> workflowStates = boardContentState.workflowStates();
                WorkflowState targetWorkflowState = workflowStates.stream().filter(x->x.workflowId().equals(event.workflowId())).findAny().get();

                workflowStates.remove(targetWorkflowState);
                int order = event.order();
                if(event.order() > workflowStates.size()){
                    order = workflowStates.size();
                }

                workflowStates.add(order, targetWorkflowState);
            }
            case CardEvents.CardCreated event -> {
                CardState cardState = CardState.create();
                cardState.isDeleted(false);
                cardState.cardId(event.cardId());
                cardState.boardId(event.boardId());
                cardState.workflowId(event.workflowId());
                cardState.laneId(event.laneId());
                cardState.userId(event.userId());
                cardState.description(event.description());

                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                if (boardContentState.committedCardStates().containsKey(event.laneId())) {
                    boardContentState.committedCardStates().get(event.laneId()).add(cardState);
                } else {
                    List cards = new LinkedList();
                    cards.add(cardState);
                    boardContentState.committedCardStates().put(event.laneId(), cards);
                }
            }
            case CardEvents.CardDeleted event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                CardState cardState = boardContentState.committedCardStates().get(event.laneId()).stream().filter(x -> x.cardId().equals(event.cardId())).findFirst().get();
                boardContentState.committedCardStates().get(event.laneId()).remove(cardState);
                removeEmptyLaneFromCommittedCardStates(event.laneId(), boardContentState.committedCardStates());
            }
            case CardEvents.CardMoved event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                CardState cardState = getCardStateAndIncVersion(boardContentState, event.cardId());
                cardState.laneId(event.newLaneId());

                Map<LaneId, List<CardState>> committedCardStates = boardContentState.committedCardStates();
                CardState targetCardState = committedCardStates.get(event.oldLaneId()).stream().filter(x -> x.cardId().equals(event.cardId())).findFirst().get();

                committedCardStates.get(event.oldLaneId()).remove(targetCardState);
                if (!committedCardStates.containsKey(event.newLaneId())) {
                    committedCardStates.put(event.newLaneId(), new LinkedList<>());
                }
                int order = event.order() > committedCardStates.get(event.newLaneId()).size() ? committedCardStates.get(event.newLaneId()).size() : event.order();
                removeEmptyLaneFromCommittedCardStates(event.oldLaneId(), committedCardStates);
                committedCardStates.get(event.newLaneId()).add(order, targetCardState);
            }
            default -> {
                return;
            }
        }

        requireNotNull("BoardContentState", boardContentState);

        setIdempotentData(boardContentState, domainEvent);
        boardContentStateRepository.save(boardContentState);
    }

    private void insertRootStage(WorkflowState data, Lane lane, int order) {
        data.rootStages().add(order, lane);
        lane.setParentId(NullLane.nullLane.getId());
        reorderRootStage(data);
    }

    private void reorderRootStage(WorkflowState data) {
        for(int i = 0; i < data.rootStages().size() ; i++){
            data.rootStages().get(i).setOrder(i);
        }
    }

    public Optional<Lane> getLaneById(WorkflowState data, LaneId laneId) {
        requireNotNull("Lane id", laneId);

        Optional<Lane> targetLane;
        for (var stage: data.rootStages()) {
            targetLane = stage.getLaneById(laneId);
            if(targetLane.isPresent()) {
                return targetLane;
            }
        }
        return Optional.empty();
    }

    private void removeEmptyLaneFromCommittedCardStates(LaneId laneId, Map<LaneId, List<CardState>> committedCardStates) {
        if (committedCardStates.get(laneId).isEmpty()) {
            committedCardStates.remove(laneId);
        }
    }

    private static int indexOf(List<WorkflowDto> workflowDtos, WorkflowId workflowId) {
        Optional<WorkflowDto> workflowDto = workflowDtos.stream().filter(x -> x.getWorkflowId().equals(workflowId.id())).findFirst();
        if (workflowDto.isPresent())
            return workflowDtos.indexOf(workflowDto.get());
        throw new RuntimeException("Workflow not found, workflowId: " + workflowId.id());
    }


    private WorkflowState getWorkflowStateAndIncVersion(BoardContentState boardContentState, WorkflowId workflowId) {
        var workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(workflowId)).findAny().get();
        workflowState.incVersion();
        return workflowState;
    }

    private CardState getCardStateAndIncVersion(BoardContentState state, CardId cardId) {
        Optional<CardState> cardState;
        for (List<CardState> cardStateList : state.committedCardStates().values()) {
            cardState = cardStateList.stream().filter(x -> x.cardId().equals(cardId)).findAny();
            if (cardState.isPresent()) {
                cardState.get().incVersion();
                return cardState.get();
            }
        }
        throw new RuntimeException("getCardStateAndIncVersion failed, card not found: " + cardId);
    }

    private void setIdempotentData(BoardContentState boardContentState, DomainEvent event){
        boardContentState.idempotentData().setHandlerId(this.id);
        boardContentState.idempotentData().setEventId(event.id().toString());
        boardContentState.idempotentData().setHandledOn(DateProvider.now());
    }
}
```

***

### Exercise 9: Use Esdb Projection with JavaScript 

#### Iteration 1: Run get_board_content_with_a_valid_board_id() in GetBoardContentFromEsdbProjectionUseCaseTest; it should fail

#### Iteration 2: Create a continuous project in EventStoreDB (Emit enabled) 
```javascript
// GetBoardContent-by-BoardId
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

![](https://lh3.googleusercontent.com/pw/AL9nZEWT8rRmSHg72MFWN_C8GXW3JtE_3_I54EDmxn1G4dwAKB5dje2leGvzvocf9jdzaGgQEbXT7ic76atUrgBesoJ9IyRhH1htv7d8qa1TcBEMnwOdaV08JJyZTmFdQkuIzQW1YG33Jk9IwKCPVNLMXi9nvA=w2076-h1830-no?authuser=0)

#### Iteration 3: Run get_board_content_with_a_valid_board_id() in GetBoardContentFromEsdbProjectionUseCaseTest again; it should pass

***
