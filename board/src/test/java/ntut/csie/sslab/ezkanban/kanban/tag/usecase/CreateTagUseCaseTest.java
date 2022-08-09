package ntut.csie.sslab.ezkanban.kanban.tag.usecase;

import ntut.csie.sslab.ddd.adapter.eventbroker.GoogleEventBusAdapter;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AllEventsListener;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagInput;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagUseCase;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagRepository;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.service.CreateTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;


public class CreateTagUseCaseTest {
    private TagRepository tagRepository;
    private SimpleAsyncTaskExecutor executor;
    private DomainEventBus eventBus;

    private AllEventsListener allEventsListener;

    @BeforeEach
    public void setUp(){
        eventBus = new GoogleEventBusAdapter();
        tagRepository = new InMemoryTagRepository(eventBus);

        allEventsListener = Mockito.mock(AllEventsListener.class);
        eventBus.register(allEventsListener);

        executor = new SimpleAsyncTaskExecutor();
        executor.execute((GoogleEventBusAdapter) eventBus);
    }

    @Test
    public void create_a_tag_use_case(){
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
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(TagEvents.TagCreated.class)));
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


    private String createTagInBoard(BoardId boardId){
        CreateTagUseCase createTagUseCase = new CreateTagService(tagRepository);
        CreateTagInput input = new CreateTagInput();
        input.setTagId(UUID.randomUUID().toString());
        input.setBoardId(boardId.id());
        input.setName("bug");

        return createTagUseCase.execute(input).getId();
    }
}
