package ntut.csie.sslab.ezkanban.kanban.board.usecase.create;

import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardInput;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;


public class CreateBoardUseCaseTest extends AbstractSpringBootJpaTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void create_a_board_with_three_members() {
        List<String> members = Arrays.asList(userId, "userId1", "userId2");
        CreateBoardUseCase createBoardUseCase = newCreateBoardUseCase();
        CreateBoardInput input = new CreateBoardInput();
        input.setTeamId(teamId);
        input.setName(boardName);
        input.setUserId(userId);
        input.setBoardId(boardId.id());

        var output = createBoardUseCase.execute(input);

        assertNotNull(output.getId());
        assertEquals(ExitCode.SUCCESS, output.getExitCode());
        Board board = boardRepository.findById(BoardId.valueOf(output.getId())).get();
        assertEquals(output.getId(), board.getId().id());
        assertEquals(input.getName(), board.getName());
        assertEquals(input.getTeamId(), board.getTeamId());
        await().untilAsserted(()-> Mockito.verify(allEventsListener, Mockito.times(1)).when(isA(BoardEvents.BoardCreated.class)));
    }

}
