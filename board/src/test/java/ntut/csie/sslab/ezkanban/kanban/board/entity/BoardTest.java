package ntut.csie.sslab.ezkanban.kanban.board.entity;

import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BoardTest {
    private String userId = "userId";

    private Board createBoard(){
        return new Board("teamId", BoardId.create(), "Scrum Board");
    }

    @Test
    public void create_a_valid_board() {
        BoardId boardId = BoardId.create();
        String teamId = "teamId";
        String boardName = "Scrum Board";

        Board board = new Board(teamId, boardId, boardName);

        assertThat(board.getTeamId()).isEqualTo(teamId);
        assertThat(board.getBoardId()).isEqualTo(boardId);
        assertThat(board.getName()).isEqualTo(boardName);
        assertThat(board.getCommittedWorkflows().size()).isEqualTo(0);
        assertThat(board.getMembers().size()).isEqualTo(0);
        assertThat(board.isDeleted()).isFalse();
    }

    @Test
    public void commit_a_workflow() {
        Board board = createBoard();
        WorkflowId workflowId = WorkflowId.create();

        board.commitWorkflow(workflowId);

        assertThat(board.getCommittedWorkflows().size()).isEqualTo(1);
        assertThat(board.getCommittedWorkflows().get(0).boardId()).isEqualTo(board.getBoardId());
        Assertions.assertThat(board.getCommittedWorkflows().get(0).workflowId()).isEqualTo(workflowId);
    }

    @ParameterizedTest
    @EnumSource(BoardRole.class)
    public void add_a_board_member_increases_the_number_of_board_member_by_one(BoardRole role) {

        Board board = createBoard();

        board.joinAs(role, userId);

        assertThat(board.getMembers().size()).isEqualTo(1);
        assertThat(board.getMembers().get(0).getBoardId()).isEqualTo(board.getBoardId());
        assertThat(board.getMembers().get(0).getBoardRole()).isEqualTo(role);
        assertThat(board.getMembers().get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    public void add_the_same_board_member_twice_does_nothing() {
        Board board = createBoard();
        board.joinAs(BoardRole.Member, userId);
        assertThat(board.getMembers().size()).isEqualTo(1);

        board.joinAs(BoardRole.Member, userId);

        assertThat(board.getMembers().size()).isEqualTo(1);
    }


    @Test
    public void remove_an_valid_board_member_decreases_the_number_of_board_member_by_one() {
        Board board = createBoard();
        board.joinAs(BoardRole.Admin, userId);
        assertThat(board.getMembers().size()).isEqualTo(1);

        board.removeMember(userId);

        assertThat(board.getMembers().size()).isEqualTo(0);
    }

    @Test
    public void remove_an_invalid_board_member_does_nothing() {
        Board board = createBoard();
        board.joinAs(BoardRole.Admin, userId);
        assertThat(board.getMembers().size()).isEqualTo(1);

        board.removeMember(UUID.randomUUID().toString());

//        assertEquals(1, board.getMembers().size());
        assertThat(board.getMembers().size()).isEqualTo(1);
    }


    @Test
    public void change_a_board_name_to_a_new_name() {
        Board board = createBoard();

        board.rename("newBoardName");

//        assertEquals("newBoardName", board.getName());
        assertThat(board.getName()).isEqualTo("newBoardName");
    }




}
