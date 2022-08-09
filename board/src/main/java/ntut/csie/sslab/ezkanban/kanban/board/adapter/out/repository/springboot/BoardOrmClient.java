package ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot;

import ntut.csie.sslab.ddd.framework.OrmClient;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BoardOrmClient extends OrmClient<BoardData, String> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM board_session",
            nativeQuery = true)
    void clearBoardSession();
}
