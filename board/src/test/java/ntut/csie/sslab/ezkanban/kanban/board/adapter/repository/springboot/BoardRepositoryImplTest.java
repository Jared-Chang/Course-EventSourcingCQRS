package ntut.csie.sslab.ezkanban.kanban.board.adapter.repository.springboot;

import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardRepositoryImplTest extends AbstractSpringBootJpaTest {


    @Test
    public void when_get_board_by_id_with_existing_id_then_the_result_is_present(){

        String teamId = "team id";
        BoardId boardId = BoardId.create();
        String boardName = "board name";


        Board board = new Board(teamId, boardId, boardName);
        boardRepository.save(board);
        Assertions.assertTrue(boardRepository.findById(boardId).isPresent());
    }


    @Test
    public void when_get_board_by_id_with_non_existing_id_then_the_result_is_not_present(){
        Assertions.assertFalse(boardRepository.findById(BoardId.create()).isPresent());
    }


}
