package ntut.csie.sslab.ezkanban.kanban.tag.usecase;

import ntut.csie.sslab.ddd.adapter.eventbroker.EsdbPersistentListener;
import ntut.csie.sslab.ddd.adapter.repository.EsdbStoreAdapter;
import ntut.csie.sslab.ddd.adapter.repository.EzOutboxStoreAdapter;
import ntut.csie.sslab.ddd.framework.ezes.PgMessageDbClient;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.MessageDataMapper;
import ntut.csie.sslab.ddd.usecase.RepositorySaveException;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.tag.adapter.out.repository.springboot.TagOrmClient;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagInput;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagUseCase;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.SnapshottedTagEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagOutboxRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.service.CreateTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class CreateTagUseCaseTest2 extends AbstractSpringBootJpaTest {
    @Autowired
    @Qualifier("TagPostgresOutboxStoreClient")
    private ntut.csie.sslab.ddd.framework.EzOutboxStore tagEzOutboxStore;

    private final static String ESDB_URL = "esdb://127.0.0.1:2113?tls=false";
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp(){
        super.setUp();
        MessageDataMapper.setMapper(TagEvents.mapper());
        DomainEventMapper.setMapper(TagEvents.mapper());

        // EventStoreDb
        tagRepository = new TagEventSourcingRepository(new EsdbStoreAdapter(ESDB_URL));
        esdbListener = new EsdbPersistentListener(ESDB_URL, domainEventTypeMapper, domainEventBus);

    }

    @Autowired
    private TagOrmClient tagOrmStoreClient;
    @Autowired
    private PgMessageDbClient pgMessageDbClient;

    @Test
    public void create_a_tag_use_case(){
        tagRepository = new TagOutboxRepository(new EzOutboxStoreAdapter(
                new ntut.csie.sslab.ddd.framework.EzOutboxStore(tagOrmStoreClient, pgMessageDbClient)));
        CreateTagUseCase createTagUseCase = new CreateTagService(tagRepository);
        CreateTagInput input = new CreateTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setBoardId("board id");
        input.setName("bug");
        input.setColor("Red");

        var output = createTagUseCase.execute(input);

        assertTrue(tagRepository.findById(output.getId()).isPresent());
        Tag tag = tagRepository.findById(output.getId()).get();
        assertEquals(input.getTagId(), tag.getId());
        assertEquals(input.getBoardId(), tag.getBoardId().id());
        assertEquals(input.getName(), tag.getName());
        assertEquals(input.getColor(), tag.getColor());
    }


    @Test
    public void find_all_tags_in_a_board(){

        BoardId boardId1 = BoardId.create();
        String tag1Id = createTagInBoard(boardId1);
        String tag2Id = createTagInBoard(boardId1);
        String tag3Id = createTagInBoard(boardId1);

        BoardId boardId2 = BoardId.create();
        String tag4Id = createTagInBoard(boardId2);

        BoardId boardId3 = BoardId.create();
        String tag5Id = createTagInBoard(boardId3);
        String tag6Id = createTagInBoard(boardId3);

        assertEquals(3, tagRepository.getTagsByBoardId(boardId1).size());
        assertEquals(1, tagRepository.getTagsByBoardId(boardId2).size());
        assertEquals(2, tagRepository.getTagsByBoardId(boardId3).size());
    }


    @Test
    public void optimistic_locking_failure(){
//        tagRepository = new TagEventSourcingRepository(new EsdbStore(ESDB_URL));
        tagRepository = new TagOutboxRepository(new EzOutboxStoreAdapter(
                new ntut.csie.sslab.ddd.framework.EzOutboxStore(tagOrmStoreClient, pgMessageDbClient)));
        String tag1Id = createTagInBoard(BoardId.create());
        Tag tagV1 = tagRepository.findById(tag1Id).get();
        Tag tagV2 = tagRepository.findById(tag1Id).get();
        tagV1.rename("story");
        tagRepository.save(tagV1);

        try{
            tagRepository.save(tagV2);
            fail("Infeasible path");
        }
        catch (RepositorySaveException e){
            assertEquals("Optimistic locking failure", e.getMessage());
        }
    }

    @Test
    public void test_Esdb_repository_snapshot() {
        EsdbStoreAdapter esdbStoreAdapter = new EsdbStoreAdapter(ESDB_URL);
        TagRepository tagRepository = new SnapshottedTagEventSourcingRepository(
                (TagEventSourcingRepository) this.tagRepository, esdbStoreAdapter);

        Tag tag = new Tag(BoardId.valueOf("board-001"),
                "008",
                "my tag",
                "red");
        tag.rename("name 1");
        tag.rename("name 2");
        tag.rename("name 3");
        tag.changeColor("yellow");
        tag.changeColor("white");
        tag.changeColor("black");

        tagRepository.save(tag);
    }

    @Test
    public void read_from_snapshot() {
        EsdbStoreAdapter esdbStoreAdapter = new EsdbStoreAdapter(ESDB_URL);
        TagRepository tagRepository = new SnapshottedTagEventSourcingRepository(
                (TagEventSourcingRepository) this.tagRepository, esdbStoreAdapter);

        Tag tag = tagRepository.findById("008").get();
    }

    private String createTagInBoard(BoardId boardId){
        CreateTagUseCase createTagUseCase = new CreateTagService(tagRepository);
        CreateTagInput input = new CreateTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setBoardId(boardId.id());
        input.setName("bug");
        input.setColor("White");

        return createTagUseCase.execute(input).getId();
    }

}
