package ntut.csie.sslab.ezkanban.kanban.board.usecase.notifyboardcontent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntut.csie.sslab.ddd.adapter.repository.IdempotentRepositoryPeer;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentReadModelRepositoryPeer;
import ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent.BoardContentStateRepositoryImpl;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardRole;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentReadModelRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentStateRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.boardcontent.NotifyBoardContent;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.service.NotifyBoardContentService;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.JpaApplicationTestContext;
import ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config.UseCaseInjection;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Rollback(false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes= JpaApplicationTestContext.class)
@TestPropertySource(locations = "classpath:board-test.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureAfter({UseCaseInjection.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractBoardContentProjectorTest {
    String boardName;
    BoardId boardId;
    String teamId;
    String userId;
    String email;
    String nickname;
    String workflowName;
    WorkflowId workflowId;
    LaneId rootStageId;
    String stageName;
    LaneId laneId;
    CardId cardId;

    BoardContentReadModelRepository boardContentReadModelRepository;
    BoardContentStateRepository boardContentStateRepository;
    UserRepository userRepository;
    NotifyBoardContent projector;
    @Autowired
    BoardContentReadModelRepositoryPeer peer;
    @Autowired
    IdempotentRepositoryPeer idempotentRepositoryPeer;


    @BeforeEach
    public void setUp() {
        boardName = "board name";
        boardId = BoardId.create();
        teamId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();
        email = "teddy@teddysoft.tw";
        nickname = "teddy";
        rootStageId = LaneId.create();
        stageName = "stage 1";
        laneId = LaneId.create();
        workflowName = "workflow";
        workflowId = WorkflowId.create();
        cardId = CardId.create();

        boardContentStateRepository = new BoardContentStateRepositoryImpl(peer, idempotentRepositoryPeer);
        userRepository = new InMemoryUserRepository();
        boardContentReadModelRepository = new BoardContentReadModelRepositoryImpl(peer, userRepository);
        projector = new NotifyBoardContentService(UUID.randomUUID().toString(), boardContentStateRepository);
    }

    @AfterEach
    public void tearDown() {
        DateProvider.resetDate();
    }

    void projectAddBoardMemberEvent(String userId) {
        projector.project(new BoardEvents.BoardMemberAdded(userId, boardId, BoardRole.Member, UUID.randomUUID(), DateProvider.now()));
    }

    void projectCreateBoardEvents(String teamId, BoardId boardId, String boardName, String userId) {
        projector.project(new BoardEvents.BoardCreated(teamId, boardId, boardName, UUID.randomUUID(), DateProvider.now()));
        projector.project(new BoardEvents.BoardMemberAdded(userId, boardId, BoardRole.Admin, UUID.randomUUID(), DateProvider.now()));
    }

    void projectCardEvent(CardEvents event) {
        projector.project(event);
    }

    void createUser(String userId, String email, String nickname) {
        UserDto userDto = new UserDto(userId, email, nickname);
        userRepository.save(userDto);
    }

    void assertJsonString(String expected, String actual) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            assertEquals(mapper.readTree(expected), mapper.readTree(actual));
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }
    }

    static class InMemoryUserRepository implements UserRepository {

        List<UserDto> users = new ArrayList<>();

        @Override
        public List<UserDto> getUsers(List<String> userIds) {
            return users;
        }

        @Override
        public Optional<UserDto> findById(String userId) {
            return users.stream().filter(x -> x.getUserId().equals(userId)).findAny();
        }

        @Override
        public void save(UserDto data) {
            users.add(data);
        }

        @Override
        public void delete(UserDto data) {

        }
    }
}
