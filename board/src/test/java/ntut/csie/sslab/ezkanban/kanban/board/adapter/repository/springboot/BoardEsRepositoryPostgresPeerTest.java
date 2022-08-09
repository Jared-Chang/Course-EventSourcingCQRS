//package ntut.csie.sslab.ezkanban.kanban.board.adapter.repository.springboot;
//
//import ntut.csie.sslab.ddd.adapter.gateway.EsAggregateStorePostgresPeer;
//import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
//import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
//import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
//import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class BoardEsRepositoryPostgresPeerTest extends AbstractSpringBootJpaTest {
//
//    //@Autowired
//    EsAggregateStorePostgresPeer esAggregateStorePostgresPeer;
//
//    @BeforeEach
//    public void setUp() {
//
//    }
//
//    @Test
//    public void when_get_board_by_id_with_existing_id_then_the_result_is_present(){
//
//        String teamId = "team id";
//        BoardId boardId = BoardId.create();
//        String boardName = "board name";
//
//
//        Board board = new Board(teamId, boardId, boardName);
//        esAggregateStorePostgresPeer.saveDomainEvents(board, BoardEvents.mapper());
//
//        Board loadedBoard = (Board) esAggregateStorePostgresPeer.load(Board.class, boardId.id()).get();
//
//        assertEquals("", loadedBoard.getName());
//    }
//
//
//    @Test
//    public void when_get_board_by_id_with_non_existing_id_then_the_result_is_not_present(){
//        Assertions.assertFalse(boardRepository.findById(BoardId.create()).isPresent());
//    }
//
//
//}
